package cn.tedu.order.tcc;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

import java.math.BigDecimal;
/*
 第一阶段的方法
 通过注解指定第二阶段的两个方法名

 BusinessActionContext 上下文对象，用来在两个阶段之间传递数据
 @BusinessActionContextParameter 注解的参数数据会被存入 BusinessActionContext
  */
@LocalTCC
public interface OrderTccAction {

    @TwoPhaseBusinessAction(name = "orderTccAction")
    boolean prepareCreateOrder(
            BusinessActionContext businessActionContext,
            @BusinessActionContextParameter(paramName = "orderId") Long orderId,
            @BusinessActionContextParameter(paramName = "userId") Long userId,
            @BusinessActionContextParameter(paramName = "productId") Long productId,
            @BusinessActionContextParameter(paramName = "count") Integer count,
            @BusinessActionContextParameter(paramName = "money") BigDecimal money);
    boolean commit(BusinessActionContext businessActionContext);

    boolean rollback(BusinessActionContext businessActionContext);
}
