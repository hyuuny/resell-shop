package com.hyuuny.resellshop.orders.presentation

import com.hyuuny.resellshop.bids.dataaccess.BidRepository
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.event.BidEventListener
import com.hyuuny.resellshop.bids.domain.event.BidStatusChangedEvent
import com.hyuuny.resellshop.orders.dataaccess.OrderHistoryRepository
import com.hyuuny.resellshop.orders.dataaccess.OrderRepository
import com.hyuuny.resellshop.orders.domain.OrderStatus
import com.hyuuny.resellshop.products.TestEnvironment
import com.hyuuny.resellshop.utils.generateOrderNumber
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.time.LocalDateTime

@TestEnvironment
class OrderRestControllerTest(
    @LocalServerPort private val port: Int,
    private val repository: OrderRepository,
    private val orderHistoryRepository: OrderHistoryRepository,
    private val bidRepository: BidRepository,
) {

    @MockBean
    lateinit var bidEventListener: BidEventListener

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
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
        val request = CreateOrderRequest(
            orderNumber = generateOrderNumber(LocalDateTime.now()),
            sellerId = 1L,
            buyerId = 2L,
            bidId = 1L,
            commission = commission,
            deliveryFee = deliveryFee,
            productPrice = productPrice,
            totalPrice = totalPrice,
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
            log().all()
        } When {
            post("/api/v1/orders")
        } Then {
            statusCode(HttpStatus.SC_CREATED)
            body("id", notNullValue())
            body("status", equalTo(OrderStatus.CREATED.name))
            body("orderNumber", equalTo(request.orderNumber))
            body("sellerId", equalTo(request.sellerId.toInt()))
            body("buyerId", equalTo(request.buyerId.toInt()))
            body("commission", equalTo(commission.toInt()))
            body("deliveryFee", equalTo(deliveryFee.toInt()))
            body("productPrice", equalTo(productPrice.toInt()))
            body("totalPrice", equalTo(totalPrice.toInt()))
            body("createdAt", notNullValue())
            log().all()
        }
        verify(bidEventListener, times(1))
            .changeStatusEvent(BidStatusChangedEvent(request.bidId, BidStatus.COMPLETED))
    }

}