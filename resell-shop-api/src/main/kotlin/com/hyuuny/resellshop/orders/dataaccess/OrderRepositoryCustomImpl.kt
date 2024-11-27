package com.hyuuny.resellshop.orders.infrastructure

import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.orders.domain.Order
import com.hyuuny.resellshop.orders.domain.QOrder.order
import com.hyuuny.resellshop.orders.service.OrderSearchCommand
import com.hyuuny.resellshop.utils.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import kotlin.math.min

class OrderRepositoryCustomImpl : QuerydslRepositorySupport(Order::class.java), OrderRepositoryCustom {

    override fun findAllBySearchCommand(searchCommand: OrderSearchCommand, pageable: Pageable): SimplePage<Order> {
        val query = from(order)

        with(searchCommand) {
            orderNumber?.let { query.where(order.orderNumber.eq(it)) }
            status?.let { query.where(order.status.eq(it)) }
            sellerId?.let { query.where(order.sellerId.eq(it)) }
            buyerId?.let { query.where(order.buyerId.eq(it)) }
            fromDate?.let { query.where(order.createdAt.goe(it.atStartOfDay())) }
            toDate?.let { query.where(order.createdAt.loe(searchCommand.getMaxToDateTime())) }
        }
        query.orderBy(*QueryDslUtil.getSort(pageable, order))

        val size = pageable.pageSize
        val content = query.limit(size.toLong() + 1).offset(pageable.offset).fetch()
        val last = content.size <= size

        return SimplePage(content.slice(0 until min(content.size, size)), pageable.pageNumber + 1, size, last)
    }
}
