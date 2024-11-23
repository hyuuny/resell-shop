package com.hyuuny.resellshop.aspect

import com.hyuuny.resellshop.orders.dataaccess.OrderHistoryRepository
import com.hyuuny.resellshop.orders.domain.OrderHistory
import com.hyuuny.resellshop.orders.service.OrderResponse
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class OrderHistoryAspect(
    private val repository: OrderHistoryRepository,
) {

    @AfterReturning(
        pointcut = "execution(* com.hyuuny.resellshop.orders.service.OrderService.create(..))",
        returning = "order"
    )
    fun saveOrderHistory(order: OrderResponse) {
        repository.save(
            OrderHistory.of(
                orderId = order.id,
                status = order.status,
                sellerId = order.sellerId,
                buyerId = order.buyerId,
                createdAt = order.createdAt,
            )
        )
    }
}
