package com.example.mall.service.impl;

import com.example.mall.dao.OrderItemMapper;
import com.example.mall.dao.OrderMapper;
import com.example.mall.dao.ProductMapper;
import com.example.mall.dao.ShippingMapper;
import com.example.mall.enums.OrderStatusEnum;
import com.example.mall.enums.PaymentType;
import com.example.mall.enums.ProductEnum;
import com.example.mall.enums.ResponseEnum;
import com.example.mall.pojo.*;
import com.example.mall.service.ICartService;
import com.example.mall.service.IOrderService;
import com.example.mall.vo.OrderItemVo;
import com.example.mall.vo.OrderVo;
import com.example.mall.vo.ResponseVo;
import com.example.mall.vo.ShippingVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author ：Ming
 * @date ：Created in 2020/8/4 0004 22:02
 */
@Service
public class IOrderServiceImpl implements IOrderService {
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private ICartService iCartService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Override
    @Transactional
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        //收获地址校验 Exist
        Shipping shipping=shippingMapper.selectByUidAndShippingId(uid,shippingId);
        if(shipping==null){
            return  ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }
        //获取购物车，校验商品库存
        List<Cart> cartList=iCartService.listForCart(uid).stream()
                .filter(Cart::getProductSelected)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cartList)){
            return  ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }

        Set<Integer> set= cartList.stream().map(Cart::getProductId).collect(Collectors.toSet());
        List<Product> productList=productMapper.selectByPrimaryKeySet(set);
        Map<Integer,Product> map=  productList.stream().collect(Collectors.toMap(Product::getId, product -> product));
        List<OrderItem> orderItemList=new ArrayList<>();
        Long orderNo=generate();
        for (Cart cart :cartList){
            Product product=map.get(cart.getProductId());
            if (product==null){
                return  ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIXT,"商品不存在="+cart.getProductId());
            }
            if (!ProductEnum.ON_SALE.getCode().equals(product.getStatus()))
                return  ResponseVo.error(ResponseEnum.PRODUCT_OFF_OR_DELETE,"商品不是在售状态"+product.getName());
            if ( product.getStock() < cart.getQuantity()) {
                return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR, product.getName() + "库存不充足");
            }
            OrderItem orderItem=BuildOrderItem(uid,orderNo,product,cart.getQuantity());
            orderItemList.add(orderItem);

            //减库存
            product.setStock(product.getStock()-cart.getQuantity());
            int row=productMapper.updateByPrimaryKeySelective(product);
            if (row <=0 ){
                return  ResponseVo.error(ResponseEnum.ERROR);
            }
        }
        Order order=buildOrder(orderNo,uid,shippingId,orderItemList);

        //入库 事务
        int row=orderMapper.insertSelective(order);
        if(row<=0){
            return  ResponseVo.error(ResponseEnum.ERROR);
        }
        int rowForOrderItem =orderItemMapper.batchInsert(orderItemList);
        if (rowForOrderItem<=0){
            return  ResponseVo.error(ResponseEnum.ERROR);
        }
        //更新购物车
        //redis事务不能回滚
        for (Cart cart :cartList){
            iCartService.delete(uid,cart.getProductId());
        }
        OrderVo orderVo= buildOrderVo(order,orderItemList,shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orders=orderMapper.selectByUid(uid);

        Set<Long> OrderNoSet= orders.stream().map(Order::getOrderNo).collect(Collectors.toSet());
        List<OrderItem> orderItems=orderItemMapper.selectByOrderNoSet(OrderNoSet);
        Map<Long,List<OrderItem>> orderItemMap=orderItems.stream().collect(Collectors.groupingBy(OrderItem::getOrderNo));
        Set<Integer> shippingId= orders.stream().map(Order::getShippingId).collect(Collectors.toSet());
        List<Shipping> shippingList=shippingMapper.selectByIdSet(shippingId);
        Map<Integer,Shipping> shippingMap=shippingList.stream().collect(Collectors.toMap(Shipping::getId,shipping -> shipping));

        List<OrderVo> orderVoList=new ArrayList<>();
        for (Order order :orders){
            orderVoList.add(buildOrderVo(order
                    ,orderItemMap.get(order.getOrderNo())
                    ,shippingMap.get(order.getShippingId())));
        }


        PageInfo pageInfo=new PageInfo<>(orders);
        pageInfo.setList(orderVoList);
        return  ResponseVo.success(pageInfo);

    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order==null||order.getUserId()!=uid){
            return  ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        Set<Long> orderNoSet=new HashSet<Long>();
        orderNoSet.add(order.getOrderNo());
        List<OrderItem> orderItemList=orderItemMapper.selectByOrderNoSet(orderNoSet);
        Shipping shipping=shippingMapper.selectByPrimaryKey(order.getShippingId());
        OrderVo orderVo=buildOrderVo(order,orderItemList,shipping);
        return  ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order =orderMapper.selectByOrderNo(orderNo);
        if (order==null||order.getUserId()!=uid){
            return  ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())){
            return  ResponseVo.error(ResponseEnum.ORDER_STATUS_ERROR);
        }
        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCloseTime(new Date());
        int row=orderMapper.updateByPrimaryKeySelective(order);
        if (row<=0){
            return  ResponseVo.error(ResponseEnum.ERROR);
        }
        return  ResponseVo.success();
    }

    @Override
    public void paid(Long orderNo) {
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            throw new RuntimeException(ResponseEnum.ORDER_NOT_EXIST.getMsg()+"订单ID"+orderNo);
        }
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())){
            throw  new RuntimeException(ResponseEnum.ORDER_STATUS_ERROR.getMsg());
        }
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date());
        int row=orderMapper.updateByPrimaryKeySelective(order);
        if (row<=0){
            throw  new RuntimeException("订单更新失败");
        }
    }

    /**
     * 封装订单Vo
     * @param order
     * @param orderItemList
     * @param shipping
     * @return
     */
    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo=new OrderVo();
        BeanUtils.copyProperties(order,orderVo);
        List<OrderItemVo> orderItemVos= orderItemList.stream().map(e->{
            OrderItemVo orderItemVo=new OrderItemVo();
            BeanUtils.copyProperties(e,orderItemVo);
            return  orderItemVo;
        }).collect(Collectors.toList());
        orderVo.setOrderItemVoList(orderItemVos);
        if (shipping!=null) {
            orderVo.setShippingId(shipping.getId());
            ShippingVo shippingVo = new ShippingVo();
            BeanUtils.copyProperties(shipping, shippingVo);
            orderVo.setShippingVo(shippingVo);
        }
        return orderVo;
    }

    /**
     * 封装为订单
     * @param orderNo
     * @param uid
     * @param shippingId
     * @param orderItems
     * @return
     */
    private Order buildOrder(Long orderNo, Integer uid,Integer shippingId,List<OrderItem> orderItems) {
        Order order=new Order();
        order.setOrderNo(orderNo);
        order.setUserId(uid);
        order.setShippingId(shippingId);
        //计算总加
        BigDecimal bigDecimal=orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        order.setPayment(bigDecimal);
        order.setPaymentType(PaymentType.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        return  order;
    }

    /**
     * 生成订单NO
     * @return
     */
    private Long generate() {
        return  System.currentTimeMillis()+new Random().nextInt(999);
    }

    /**
     * 生成订单内容（商品）
     * @param uid
     * @param orderNo
     * @param product
     * @param quantity
     * @return
     */
    private OrderItem BuildOrderItem(Integer uid,Long orderNo,Product product,Integer quantity) {
        OrderItem orderItem=new OrderItem();
        orderItem.setUserId(uid);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }
}
