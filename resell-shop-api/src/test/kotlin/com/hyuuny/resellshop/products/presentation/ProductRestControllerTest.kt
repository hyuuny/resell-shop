package com.hyuuny.resellshop.products.presentation

import com.hyuuny.resellshop.core.common.exception.ErrorType
import com.hyuuny.resellshop.products.TestEnvironment
import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.infrastructure.ProductRepository
import com.hyuuny.resellshop.products.service.CreateProductCommand
import com.hyuuny.resellshop.products.service.ProductImageCommand
import com.hyuuny.resellshop.products.service.ProductSearchCommand
import com.hyuuny.resellshop.products.service.ProductService
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
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

@TestEnvironment
class ProductRestControllerTest(
    @LocalServerPort private val port: Int,
    private val repository: ProductRepository,
    private val service: ProductService,
) {

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
        repository.deleteAll()
    }

    @Test
    fun `상품 목록을 조회할 수 있다`() {
        val commandOne = CreateProductCommand(
            categoryId = 1L,
            nameEn = "Stussy x Our Legacy Work Shop 8 Ball Pigment Dyed Yin Yang T-Shirt Black",
            brand = Brand.STUSSY,
            nameKo = "스투시 x 아워레가시 워크샵 8볼 피그먼트 다이드 음양 티셔츠 블랙",
            releasePrice = BigDecimal(82000),
            modelNumber = "3903959",
            releaseDate = LocalDate.of(2024, 9, 27),
            option = "BLACK",
            images = listOf(
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-1.jpg"),
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-2.jpg"),
            )
        )
        val commandTwo = CreateProductCommand(
            categoryId = 1L,
            nameEn = "Nike x Off White NRG Fleece Hoodie Black",
            brand = Brand.NIKE,
            nameKo = "나이키 x 오프화이트 NRG 플리스 후디 블랙",
            releasePrice = BigDecimal(139000),
            modelNumber = "DN1760-010",
            releaseDate = LocalDate.of(2022, 12, 21),
            option = "BLACK",
            images = listOf(
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-3.jpg"),
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-4.jpg"),
            )
        )
        val commandThree = CreateProductCommand(
            categoryId = 2L,
            nameEn = "Stussy Basic Zip Hoodie Black 2024",
            brand = Brand.STUSSY,
            nameKo = "스투시 베이직 후드 집업 블랙 2024",
            releasePrice = BigDecimal(199000),
            modelNumber = "197500/M",
            option = "BLACK",
            releaseDate = null,
            images = listOf(
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-5.jpg"),
            )
        )
        service.create(commandOne)
        service.create(commandTwo)
        service.create(commandThree)
        val searchCommand = ProductSearchCommand()
        val pageable: Pageable = PageRequest.of(0, 10)

        Given {
            contentType(ContentType.JSON)
            param("page", pageable.pageNumber)
            param("size", pageable.pageSize)
            param("sort", "id,DESC")
            param("categoryId", searchCommand.categoryId)
            param("brand", searchCommand.brand)
            param("nameKo", searchCommand.nameKo)
        } When {
            get("/api/v1/products")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("data.size()", equalTo(3))
            body("data[0].id", notNullValue())
            body("data[0].categoryId", equalTo(commandThree.categoryId.toInt()))
            body("data[0].nameKo", equalTo(commandThree.nameKo))
            body("data[0].thumbnailUrl", equalTo(commandThree.images.first().imageUrl))
            body("data[1].id", notNullValue())
            body("data[1].categoryId", equalTo(commandTwo.categoryId.toInt()))
            body("data[1].nameKo", equalTo(commandTwo.nameKo))
            body("data[1].thumbnailUrl", equalTo(commandTwo.images.first().imageUrl))
            body("data[2].id", notNullValue())
            body("data[2].categoryId", equalTo(commandOne.categoryId.toInt()))
            body("data[2].nameKo", equalTo(commandOne.nameKo))
            body("data[2].thumbnailUrl", equalTo(commandOne.images.first().imageUrl))
            log().all()
        }
    }

    @Test
    fun `상품을 검색할 수 있다`() {
        val commandOne = CreateProductCommand(
            categoryId = 1L,
            nameEn = "Stussy x Our Legacy Work Shop 8 Ball Pigment Dyed Yin Yang T-Shirt Black",
            brand = Brand.STUSSY,
            nameKo = "스투시 x 아워레가시 워크샵 8볼 피그먼트 다이드 음양 티셔츠 블랙",
            releasePrice = BigDecimal(82000),
            modelNumber = "3903959",
            releaseDate = LocalDate.of(2024, 9, 27),
            option = "BLACK",
            images = listOf(
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-1.jpg"),
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-2.jpg"),
            )
        )
        val commandTwo = CreateProductCommand(
            categoryId = 1L,
            nameEn = "Nike x Off White NRG Fleece Hoodie Black",
            brand = Brand.NIKE,
            nameKo = "나이키 x 오프화이트 NRG 플리스 후디 블랙",
            releasePrice = BigDecimal(139000),
            modelNumber = "DN1760-010",
            releaseDate = LocalDate.of(2022, 12, 21),
            option = "BLACK",
            images = listOf(
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-3.jpg"),
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-4.jpg"),
            )
        )
        val commandThree = CreateProductCommand(
            categoryId = 2L,
            nameEn = "Stussy Basic Zip Hoodie Black 2024",
            brand = Brand.STUSSY,
            nameKo = "스투시 베이직 후드 집업 블랙 2024",
            releasePrice = BigDecimal(199000),
            modelNumber = "197500/M",
            option = "BLACK",
            releaseDate = null,
            images = listOf(
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-5.jpg"),
            )
        )
        val savedProductOne = service.create(commandOne)
        service.create(commandTwo)
        val savedProductThree = service.create(commandThree)

        val searchCommand = ProductSearchCommand(nameKo = "스투시")
        val pageable: Pageable = PageRequest.of(0, 10)

        Given {
            contentType(ContentType.JSON)
            param("page", pageable.pageNumber)
            param("size", pageable.pageSize)
            param("sort", "id,DESC")
            param("categoryId", searchCommand.categoryId)
            param("brand", searchCommand.brand)
            param("nameKo", searchCommand.nameKo)
            log().all()
        } When {
            get("/api/v1/products")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("data.size()", equalTo(2))
            body("data[0].id", equalTo(savedProductThree.id.toInt()))
            body("data[0].categoryId", equalTo(savedProductThree.categoryId.toInt()))
            body("data[0].nameKo", equalTo(savedProductThree.nameKo))
            body("data[0].thumbnailUrl", equalTo(savedProductThree.images.first().imageUrl))
            body("data[1].id", equalTo(savedProductOne.id.toInt()))
            body("data[1].categoryId", equalTo(savedProductOne.categoryId.toInt()))
            body("data[1].nameKo", equalTo(savedProductOne.nameKo))
            body("data[1].thumbnailUrl", equalTo(savedProductOne.images.first().imageUrl))
            log().all()
        }
    }

    @Test
    fun `상품을 조회할 수 있다`() {
        val command = CreateProductCommand(
            categoryId = 1L,
            nameEn = "Stussy x Our Legacy Work Shop 8 Ball Pigment Dyed Yin Yang T-Shirt Black",
            brand = Brand.STUSSY,
            nameKo = "스투시 x 아워레가시 워크샵 8볼 피그먼트 다이드 음양 티셔츠 블랙",
            releasePrice = BigDecimal(82000),
            modelNumber = "3903959",
            releaseDate = LocalDate.of(2024, 9, 27),
            option = "BLACK",
            images = listOf(
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-1.jpg"),
                ProductImageCommand("https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-2.jpg"),
            )
        )
        val savedProduct = service.create(command)

        Given {
            contentType(ContentType.JSON)
            pathParam("id", savedProduct.id)
            log().all()
        } When {
            get("/api/v1/products/{id}")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("id", equalTo(savedProduct.id.toInt()))
            body("categoryId", equalTo(savedProduct.categoryId.toInt()))
            body("nameEn", equalTo(savedProduct.nameEn))
            body("nameKo", equalTo(savedProduct.nameKo))
            body("brand", equalTo(savedProduct.brand.name))
            body("releaseDate", equalTo(savedProduct.releaseDate.toString()))
            body("option", equalTo(savedProduct.option))
            body("thumbnailUrl", equalTo(savedProduct.images.first().imageUrl))
            log().all()
        }
    }

    @Test
    fun `존재하지 않는 상품은 조회할 수 없다`() {
        Given {
            contentType(ContentType.JSON)
            pathParam("id", 9999999)
            log().all()
        } When {
            get("/api/v1/products/{id}")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
            body("code", equalTo(ErrorType.PRODUCT_NOT_FOUND.name))
            body("message", equalTo("상품을 찾을 수 없습니다. id: 9999999"))
            log().all()
        }
    }

}
