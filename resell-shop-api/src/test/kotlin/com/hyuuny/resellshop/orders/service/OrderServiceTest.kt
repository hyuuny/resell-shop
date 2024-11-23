package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.bids.dataaccess.BidRepository
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.event.BidEventListener
import com.hyuuny.resellshop.bids.domain.event.BidStatusChangedEvent
import com.hyuuny.resellshop.orders.dataaccess.OrderHistoryRepository
import com.hyuuny.resellshop.orders.dataaccess.OrderRepository
import com.hyuuny.resellshop.orders.domain.OrderStatus
import com.hyuuny.resellshop.products.TestEnvironment
import com.hyuuny.resellshop.utils.generateOrderNumber
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDateTime

@TestEnvironment
class OrderServiceTest(
    private val repository: OrderRepository,
    private val orderHistoryRepository: OrderHistoryRepository,
    private val service: OrderService,
    private val bidRepository: BidRepository,
) {

    @MockBean
    lateinit var bidEventListener: BidEventListener

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
        orderHistoryRepository.deleteAll()
        bidRepository.deleteAll()
    }

    @Test
    fun `주문과 주문 히스토리를 생성하면서, 연관된 입찰 상태를 변경한다`() {
        val commission = 3200L
        val deliveryFee = 3000L
        val productPrice = 20000L
        val totalPrice = commission + deliveryFee + productPrice
        val command = CreateOrderCommand(
            orderNumber = generateOrderNumber(LocalDateTime.now()),
            sellerId = 1L,
            buyerId = 2L,
            bidId = 1L,
            commission = commission,
            deliveryFee = deliveryFee,
            productPrice = productPrice,
            totalPrice = totalPrice,
        )

        val order = service.create(command)

        assertThat(order.id).isNotNull()
        assertThat(order.status).isEqualTo(OrderStatus.CREATED)
        assertThat(order.orderNumber).isEqualTo(command.orderNumber)
        assertThat(order.sellerId).isEqualTo(command.sellerId)
        assertThat(order.buyerId).isEqualTo(command.buyerId)
        assertThat(order.bidId).isEqualTo(command.bidId)
        assertThat(order.commission).isEqualTo(command.commission)
        assertThat(order.deliveryFee).isEqualTo(command.deliveryFee)
        assertThat(order.productPrice).isEqualTo(command.productPrice)
        assertThat(order.totalPrice).isEqualTo(command.totalPrice)
        assertThat(order.createdAt).isNotNull()

        orderHistoryRepository.findByOrderId(order.id)!!.let {
            assertThat(it.id).isNotNull()
            assertThat(it.orderId).isEqualTo(order.id)
            assertThat(it.status).isEqualTo(order.status)
            assertThat(it.sellerId).isEqualTo(order.sellerId)
            assertThat(it.buyerId).isEqualTo(order.buyerId)
            assertThat(it.createdAt).isEqualTo(order.createdAt)
        }
        verify(bidEventListener, times(1))
            .changeStatusEvent(BidStatusChangedEvent(order.bidId, BidStatus.COMPLETED))
    }
}