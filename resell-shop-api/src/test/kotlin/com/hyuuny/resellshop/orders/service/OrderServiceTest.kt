package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.bids.dataaccess.BidRepository
import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.bids.domain.event.BidEventListener
import com.hyuuny.resellshop.bids.domain.event.BidStatusChangedEvent
import com.hyuuny.resellshop.core.common.exception.NotCancelableOrderException
import com.hyuuny.resellshop.core.common.exception.OrderNotFoundException
import com.hyuuny.resellshop.orders.dataaccess.OrderHistoryRepository
import com.hyuuny.resellshop.orders.dataaccess.OrderRepository
import com.hyuuny.resellshop.orders.domain.Order
import com.hyuuny.resellshop.orders.domain.OrderHistory
import com.hyuuny.resellshop.orders.domain.OrderStatus
import com.hyuuny.resellshop.products.TestEnvironment
import com.hyuuny.resellshop.products.dataaccess.ProductRepository
import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.domain.ProductImage
import com.hyuuny.resellshop.products.domain.ProductSize
import com.hyuuny.resellshop.utils.generateOrderNumber
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.time.LocalDateTime

@TestEnvironment
class OrderServiceTest(
    private val repository: OrderRepository,
    private val orderHistoryRepository: OrderHistoryRepository,
    private val service: OrderService,
    private val bidRepository: BidRepository,
    private val productRepository: ProductRepository,
) {

    @MockBean
    lateinit var bidEventListener: BidEventListener

    @AfterEach
    fun tearDown() {
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

        val savedOrder = service.findById(order.id)

        assertThat(savedOrder.id).isNotNull()
        assertThat(savedOrder.status).isEqualTo(OrderStatus.CREATED)
        assertThat(savedOrder.orderNumber).isEqualTo(command.orderNumber)
        assertThat(savedOrder.sellerId).isEqualTo(command.sellerId)
        assertThat(savedOrder.buyerId).isEqualTo(command.buyerId)
        assertThat(savedOrder.bidId).isEqualTo(command.bidId)
        assertThat(savedOrder.commission).isEqualTo(command.commission)
        assertThat(savedOrder.deliveryFee).isEqualTo(command.deliveryFee)
        assertThat(savedOrder.productPrice).isEqualTo(command.productPrice)
        assertThat(savedOrder.totalPrice).isEqualTo(command.totalPrice)
        assertThat(savedOrder.createdAt).isNotNull()
    }

    @Test
    fun `존재하지 않는 주문은 조회할 수 없다`() {
        val invalidId = 99999L

        val exception = assertThrows<OrderNotFoundException> {
            service.findById(invalidId)
        }
        assertThat(exception.message).isEqualTo("주문을 찾을 수 없습니다. id: $invalidId")
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
        orderHistoryRepository.save(
            OrderHistory.of(
                orderId = savedOrder.id!!,
                status = OrderStatus.CREATED,
                sellerId = savedOrder.sellerId,
                buyerId = savedOrder.buyerId,
                createdAt = savedOrder.createdAt,
            )
        )

        service.cancel(savedOrder.id!!)

        repository.findByIdOrNull(savedOrder.id!!)!!.let { assertThat(it.status).isEqualTo(OrderStatus.CANCELLED) }
        verify(bidEventListener, times(1))
            .changeStatusEvent(BidStatusChangedEvent(order.bidId, BidStatus.CANCELLED))
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

        val exception = assertThrows<NotCancelableOrderException> {
            service.cancel(savedOrder.id!!)
        }
        assertThat(exception.message).isEqualTo("주문을 취소할 수 상태입니다.")
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
        val searchCommand = OrderSearchCommand()
        val pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"))

        val response = service.search(searchCommand, pageable)

        assertThat(response.content).hasSize(10)
        assertThat(response.page).isEqualTo(1)
        assertThat(response.size).isEqualTo(10)
        assertThat(response.last).isEqualTo(false)
    }

}