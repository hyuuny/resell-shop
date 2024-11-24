package com.hyuuny.resellshop.orders.presentation

import com.hyuuny.resellshop.bids.dataaccess.BidRepository
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.event.BidEventListener
import com.hyuuny.resellshop.bids.domain.event.BidStatusChangedEvent
import com.hyuuny.resellshop.core.common.exception.ErrorType
import com.hyuuny.resellshop.orders.dataaccess.OrderHistoryRepository
import com.hyuuny.resellshop.orders.dataaccess.OrderRepository
import com.hyuuny.resellshop.orders.domain.OrderStatus
import com.hyuuny.resellshop.orders.service.CreateOrderCommand
import com.hyuuny.resellshop.orders.service.OrderService
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
    private val service: OrderService,
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

    @Test
    fun `주문을 조회할 수 있다`() {
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

        Given {
            contentType(ContentType.JSON)
            log().all()
        } When {
            get("/api/v1/orders/${order.id}")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("id", equalTo(order.id.toInt()))
            body("status", equalTo(OrderStatus.CREATED.name))
            body("orderNumber", equalTo(order.orderNumber))
            body("sellerId", equalTo(order.sellerId.toInt()))
            body("buyerId", equalTo(order.buyerId.toInt()))
            body("commission", equalTo(order.commission.toInt()))
            body("deliveryFee", equalTo(order.deliveryFee.toInt()))
            body("productPrice", equalTo(order.productPrice.toInt()))
            body("totalPrice", equalTo(order.totalPrice.toInt()))
            body("createdAt", notNullValue())
            log().all()
        }
    }

    @Test
    fun `존재하지 않는 주문은 조회할 수 없다`() {
        val invalidId = 999999

        Given {
            contentType(ContentType.JSON)
            pathParams("id", invalidId)
            log().all()
        } When {
            get("/api/v1/orders/{id}")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
            body("code", equalTo(ErrorType.ORDER_NOT_FOUND.name))
            body("message", equalTo("주문을 찾을 수 없습니다. id: $invalidId"))
            log().all()
        }
    }

}