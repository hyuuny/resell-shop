package com.hyuuny.resellshop.orders.presentation

import com.hyuuny.resellshop.bids.dataaccess.BidRepository
import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.bids.domain.event.BidEventListener
import com.hyuuny.resellshop.bids.domain.event.BidStatusChangedEvent
import com.hyuuny.resellshop.core.common.exception.ErrorType
import com.hyuuny.resellshop.orders.dataaccess.OrderHistoryRepository
import com.hyuuny.resellshop.orders.dataaccess.OrderRepository
import com.hyuuny.resellshop.orders.domain.Order
import com.hyuuny.resellshop.orders.domain.OrderStatus
import com.hyuuny.resellshop.orders.service.CreateOrderCommand
import com.hyuuny.resellshop.orders.service.OrderService
import com.hyuuny.resellshop.products.TestEnvironment
import com.hyuuny.resellshop.products.dataaccess.ProductRepository
import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.domain.ProductImage
import com.hyuuny.resellshop.products.domain.ProductSize
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.LocalDateTime

@TestEnvironment
class OrderRestControllerTest(
    @LocalServerPort private val port: Int,
    private val repository: OrderRepository,
    private val orderHistoryRepository: OrderHistoryRepository,
    private val bidRepository: BidRepository,
    private val productRepository: ProductRepository,
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
        productRepository.deleteAll()
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

    @CsvSource("CREATED", "INSPECTION_FAILED")
    @ParameterizedTest
    fun `주문 내역을 취소할 수 있다`(status: OrderStatus) {
        val order = Order.of(
            status = status,
            orderNumber = generateOrderNumber(LocalDateTime.now()),
            sellerId = 1L,
            buyerId = 2L,
            bidId = 1L,
            commission = 3200L,
            deliveryFee = 3000L,
            productPrice = 20000L,
            totalPrice = 3200L + 3000L + 20000L,
            createdAt = LocalDateTime.now(),
        )
        val savedOrder = repository.save(order)

        Given {
            contentType(ContentType.JSON)
            pathParams("id", savedOrder.id)
            log().all()
        } When {
            patch("/api/v1/orders/{id}/cancel")
        } Then {
            statusCode(HttpStatus.SC_OK)
            log().all()
        }
    }

    @CsvSource(
        "SELLER_PRODUCT_DELIVERING",
        "COMPLETED_IN_STOCK",
        "INSPECTION",
        "INSPECTION_PASSED",
        "CANCELLED",
        "DELIVERING_TO_BUYER",
        "DELIVERED"
    )
    @ParameterizedTest
    fun `주문이 정상적으로 진행중이라면 주문을 취소할 수 없다`(status: OrderStatus) {
        val order = Order.of(
            status = status,
            orderNumber = generateOrderNumber(LocalDateTime.now()),
            sellerId = 1L,
            buyerId = 2L,
            bidId = 1L,
            commission = 3200L,
            deliveryFee = 3000L,
            productPrice = 20000L,
            totalPrice = 3200L + 3000L + 20000L,
            createdAt = LocalDateTime.now(),
        )
        val savedOrder = repository.save(order)

        Given {
            contentType(ContentType.JSON)
            pathParams("id", savedOrder.id)
            log().all()
        } When {
            patch("/api/v1/orders/{id}/cancel")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
            body("code", equalTo(ErrorType.NOT_CANCELABLE_ORDER.name))
            body("message", equalTo("주문을 취소할 수 상태입니다."))
            log().all()
        }
    }

    @Test
    fun `주문 목록을 조회할 수 있다`() {
        for (i in 1L..13L) {
            val product = Product.of(
                categoryId = i,
                brand = Brand.NIKE,
                nameEn = "product$i",
                nameKo = "상품$i",
                releasePrice = 30000L,
                modelNumber = "390395${i}",
                releaseDate = LocalDate.now(),
                option = "BLUE",
            )
            val image = ProductImage.of(product, "image")
            val size = ProductSize.of(product, "M")
            product.addImage(image)
            product.addSize(size)
            val savedProduct = productRepository.save(product)

            val orderNumber = generateOrderNumber(LocalDateTime.now())
            val bid = Bid.of(
                type = BidType.BUY,
                orderNumber = orderNumber,
                userId = i,
                productId = savedProduct.id!!,
                productSizeId = savedProduct.sizes.first().id!!,
                price = 10000L,
                createdAt = LocalDateTime.now(),
            )
            bidRepository.save(bid)

            val order = Order.of(
                status = OrderStatus.CREATED,
                orderNumber = generateOrderNumber(LocalDateTime.now()),
                sellerId = 1L,
                buyerId = i,
                bidId = bid.id!!,
                commission = 3200L,
                deliveryFee = 3000L,
                productPrice = bid.price.amount,
                totalPrice = 3200L + 3000L + bid.price.amount,
                createdAt = LocalDateTime.now(),
            )
            repository.save(order)
        }
        val pageable: Pageable = PageRequest.of(0, 10)

        Given {
            contentType(ContentType.JSON)
            params("page", pageable.pageNumber)
            params("size", pageable.pageSize)
            param("sort", "id,DESC")
            log().all()
        } When {
            get("/api/v1/orders")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("page", equalTo(1))
            body("size", equalTo(10))
            body("last", equalTo(false))
            log().all()
        }
    }

}