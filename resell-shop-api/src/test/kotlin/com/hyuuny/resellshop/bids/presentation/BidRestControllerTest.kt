package com.hyuuny.resellshop.bids.presentation

import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.bids.infrastructure.BidHistoryRepository
import com.hyuuny.resellshop.bids.infrastructure.BidRepository
import com.hyuuny.resellshop.bids.service.BidService
import com.hyuuny.resellshop.bids.service.ChangePriceCommand
import com.hyuuny.resellshop.bids.service.CreateBidCommand
import com.hyuuny.resellshop.core.common.exception.ErrorType
import com.hyuuny.resellshop.products.TestEnvironment
import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.domain.ProductImage
import com.hyuuny.resellshop.products.domain.ProductSize
import com.hyuuny.resellshop.products.infrastructure.ProductRepository
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
import org.mockito.Mockito.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.time.LocalDate

@TestEnvironment
class BidRestControllerTest(
    @LocalServerPort private val port: Int,
    private val repository: BidRepository,
    private val productRepository: ProductRepository,
    private val service: BidService,
) {

    @MockBean
    lateinit var bidHistoryRepository: BidHistoryRepository

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
        repository.deleteAll()
        productRepository.deleteAll()
    }

    @Test
    fun `판매자는 상품 판매 입찰을 등록할 수 있고, 입찰 히스토리도 저장된다`() {
        val request = CreateBidRequest(
            type = BidType.SELL,
            userId = 1L,
            productId = 1L,
            productSizeId = 1L,
            price = 55000,
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
            log().all()
        } When {
            post("/api/v1/bids")
        } Then {
            statusCode(HttpStatus.SC_CREATED)
            body("id", notNullValue())
            body("type", equalTo(request.type.name))
            body("status", equalTo(BidStatus.WAITING.name))
            body("orderNumber", notNullValue())
            body("userId", equalTo(request.userId.toInt()))
            body("productId", equalTo(request.productId.toInt()))
            body("productSizeId", equalTo(request.productSizeId.toInt()))
            body("price", equalTo(request.price.toInt()))
            body("createdAt", notNullValue())
            log().all()
        }
        verify(bidHistoryRepository, times(1)).save(any())
    }

    @Test
    fun `구매자는 상품 구매 입찰을 등록할 수 있고, 입찰 히스토리도 저장된다`() {
        val request = CreateBidRequest(
            type = BidType.BUY,
            userId = 2L,
            productId = 1L,
            productSizeId = 1L,
            price = 43000,
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
            log().all()
        } When {
            post("/api/v1/bids")
        } Then {
            statusCode(HttpStatus.SC_CREATED)
            body("id", notNullValue())
            body("type", equalTo(request.type.name))
            body("status", equalTo(BidStatus.WAITING.name))
            body("orderNumber", notNullValue())
            body("userId", equalTo(request.userId.toInt()))
            body("productId", equalTo(request.productId.toInt()))
            body("productSizeId", equalTo(request.productSizeId.toInt()))
            body("price", equalTo(request.price.toInt()))
            body("createdAt", notNullValue())
            log().all()
        }
        verify(bidHistoryRepository, times(1)).save(any())
    }

}
