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
import com.hyuuny.resellshop.products.dataaccess.ProductRepository
import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.domain.ProductImage
import com.hyuuny.resellshop.products.domain.ProductSize
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

    @CsvSource("SELL", "BUY")
    @ParameterizedTest
    fun `같은 상품 사이즈의 중복 입찰을 등록할 수 없다`(type: BidType) {
        val request = CreateBidRequest(
            type = type,
            userId = 2L,
            productId = 1L,
            productSizeId = 1L,
            price = 43000,
        )
        service.create(request.toCommand())

        Given {
            contentType(ContentType.JSON)
            body(request)
            log().all()
        } When {
            post("/api/v1/bids")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
            body("code", equalTo(ErrorType.ALREADY_EXIST_BID.name))
            body("message", equalTo("이미 해당 상품에 대한 입찰이 존재합니다."))
            log().all()
        }
    }

    @Test
    fun `입찰 가격은 0보다 커야 한다`() {
        val request = CreateBidRequest(
            type = BidType.SELL,
            userId = 1L,
            productId = 1L,
            productSizeId = 1L,
            price = 0,
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
            log().all()
        } When {
            post("/api/v1/bids")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
            body("code", equalTo(ErrorType.INVALID_BID_PRICE.name))
            body("message", equalTo("입찰 가격은 0보다 커야 합니다."))
            log().all()
        }
    }

    @Test
    fun `상품의 사이즈별로 등록된 가장 낮은 입찰가 목록이 조회된다`() {
        val product = Product.of(
            categoryId = 1L,
            nameEn = "Stussy x Our Legacy Work Shop 8 Ball Pigment Dyed Yin Yang T-Shirt Black",
            brand = Brand.STUSSY,
            nameKo = "스투시 x 아워레가시 워크샵 8볼 피그먼트 다이드 음양 티셔츠 블랙",
            releasePrice = 82000,
            modelNumber = "3903959",
            releaseDate = LocalDate.of(2024, 9, 27),
            option = "BLACK",
        )
        product.addImages(
            listOf(
                ProductImage.of(product, "https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-1.jpg"),
                ProductImage.of(product, "https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-2.jpg"),
            )
        )
        product.addSizes(
            listOf(
                ProductSize.of(product, "S"),
                ProductSize.of(product, "M"),
                ProductSize.of(product, "L"),
                ProductSize.of(product, "XL"),
            )
        )
        val savedProduct = productRepository.save(product)
        val productSizes = savedProduct.sizes

        val bidCommands = listOf(
            CreateBidCommand(
                type = BidType.SELL,
                userId = 1L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[0].id!!,
                price = 70000
            ),
            CreateBidCommand(
                type = BidType.SELL,
                userId = 2L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[0].id!!,
                price = 55000,
            ),
            CreateBidCommand(
                type = BidType.BUY,
                userId = 3L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[0].id!!,
                price = 43000,
            ),
            CreateBidCommand(
                type = BidType.BUY,
                userId = 5L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[0].id!!,
                price = 30000,
            ),
            CreateBidCommand(
                type = BidType.SELL,
                userId = 3L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[1].id!!,
                price = 60000,
            ),
            CreateBidCommand(
                type = BidType.SELL,
                userId = 4L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[1].id!!,
                price = 65000,
            ),
            CreateBidCommand(
                type = BidType.SELL,
                userId = 5L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[2].id!!,
                price = 90000,
            ),
            CreateBidCommand(
                type = BidType.SELL,
                userId = 6L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[2].id!!,
                price = 75000,
            ),
            CreateBidCommand(
                type = BidType.BUY,
                userId = 1L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[2].id!!,
                price = 72000,
            ),
        )
        bidCommands.forEach { service.create(it) }

        Given {
            contentType(ContentType.JSON)
            pathParams("productId", savedProduct.id)
            log().all()
        } When {
            get("/api/v1/bids/products/{productId}")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("productId", equalTo(savedProduct.id?.toInt()))
            body("bidPriceDetails[0].productSizeId", equalTo(productSizes[0].id?.toInt()))
            body("bidPriceDetails[0].type", equalTo("SELL"))
            body("bidPriceDetails[0].minPrice", equalTo(bidCommands[1].price.toInt()))
            body("bidPriceDetails[1].productSizeId", equalTo(productSizes[0].id?.toInt()))
            body("bidPriceDetails[1].type", equalTo("BUY"))
            body("bidPriceDetails[1].minPrice", equalTo(bidCommands[3].price.toInt()))
            body("bidPriceDetails[2].productSizeId", equalTo(productSizes[1].id?.toInt()))
            body("bidPriceDetails[2].type", equalTo("SELL"))
            body("bidPriceDetails[2].minPrice", equalTo(bidCommands[4].price.toInt()))
            body("bidPriceDetails[3].productSizeId", equalTo(productSizes[1].id?.toInt()))
            body("bidPriceDetails[3].type", equalTo("BUY"))
            body("bidPriceDetails[3].minPrice", equalTo(0))
            body("bidPriceDetails[4].productSizeId", equalTo(productSizes[2].id?.toInt()))
            body("bidPriceDetails[4].type", equalTo("SELL"))
            body("bidPriceDetails[4].minPrice", equalTo(bidCommands[7].price.toInt()))
            body("bidPriceDetails[5].productSizeId", equalTo(productSizes[2].id?.toInt()))
            body("bidPriceDetails[5].type", equalTo("BUY"))
            body("bidPriceDetails[5].minPrice", equalTo(bidCommands[8].price.toInt()))
            body("bidPriceDetails[6].productSizeId", equalTo(productSizes[3].id?.toInt()))
            body("bidPriceDetails[6].type", equalTo("SELL"))
            body("bidPriceDetails[6].minPrice", equalTo(0))
            body("bidPriceDetails[7].productSizeId", equalTo(productSizes[3].id?.toInt()))
            body("bidPriceDetails[7].type", equalTo("BUY"))
            body("bidPriceDetails[7].minPrice", equalTo(0))
            log().all()
        }
    }

    @Test
    fun `입찰 금액을 변경할 수 있다`() {
        val command = CreateBidCommand(
            type = BidType.SELL,
            userId = 1L,
            productId = 1L,
            productSizeId = 1L,
            price = 55000,
        )
        val savedBid = service.create(command)
        val changePriceCommand = ChangePriceCommand(price = 60000)

        Given {
            contentType(ContentType.JSON)
            body(changePriceCommand)
            pathParams("id", savedBid.id)
            log().all()
        } When {
            patch("/api/v1/bids/{id}/change-price")
        } Then {
            statusCode(HttpStatus.SC_OK)
            log().all()
        }
    }

}
