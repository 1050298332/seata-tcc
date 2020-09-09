package cn.tedu.order.tcc;

import cn.tedu.order.entity.Order;
import cn.tedu.order.mapper.OrderMapper;
import cn.tedu.order.tcc.OrderTccAction;
import cn.tedu.order.tcc.ResultHolder;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
@Slf4j
public class OrderTccActionImpl implements OrderTccAction {
    @Autowired
    OrderMapper orderMapper;
    @Override
    public boolean prepareCreateOrder(BusinessActionContext businessActionContext,Long orderId, Long userId, Long productId, Integer count, BigDecimal money) {
       //插入订单,状态设置成0-冻结状态
        orderMapper.create(new Order(orderId, userId, productId, count, money, 0));
        log.info("创建订单第一阶段,冻结订单成功！");
        //第一阶段成功,设置成功标记
        ResultHolder.setResult(OrderTccAction.class,businessActionContext.getXid(),"p");
        return true;
    }

    @Override
    public boolean commit(BusinessActionContext businessActionContext) {
        //判断第一阶段是否成功
        log.info("创建订单的第二阶段,开始执行提交操作");
        if(ResultHolder.getResult(OrderTccAction.class,businessActionContext.getXid()) == null){
            return true;
        }
        //修改订单窗台  从0设置成1-正常状态的订单
        //需要订单ID来修改订单
        Long orderId = Long.parseLong(businessActionContext.getActionContext("orderId").toString());
        orderMapper.updateStatus(orderId,1);
        log.info("创建订单的第二阶段,提交订单(解冻)成功！");
        return true;
    }

    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {
        //判断第一阶段是否成功
        log.info("创建订单的第二阶段,开始执行回滚操作");
        if(ResultHolder.getResult(OrderTccAction.class,businessActionContext.getXid()) == null){
            return true;
        }
        //回滚订单,根据订单ID删除订单
        Long orderId = Long.parseLong(businessActionContext.getActionContext("orderId").toString());
        orderMapper.deleteById(orderId);
        log.info("创建订单第二阶段,回滚订单(删除)成功");
        return true;
    }
}
