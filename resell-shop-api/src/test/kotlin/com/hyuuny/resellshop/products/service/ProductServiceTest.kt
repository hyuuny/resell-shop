package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.core.common.exception.ProductNotFoundException
import com.hyuuny.resellshop.products.TestEnvironment
import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.infrastructure.ProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.LocalDate

@TestEnvironment
@DisplayName("상품 테스트")
class ProductServiceTest(
    @Autowired private val repository: ProductRepository,
    @Autowired private val service: ProductService,
) {

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun `상품이 정상 등록된다`() {
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

        assertThat(savedProduct.categoryId).isEqualTo(command.categoryId)
        assertThat(savedProduct.nameEn).isEqualTo(command.nameEn)
        assertThat(savedProduct.brand).isEqualTo(command.brand)
        assertThat(savedProduct.nameKo).isEqualTo(command.nameKo)
        assertThat(savedProduct.releasePrice).isEqualTo(command.releasePrice)
        assertThat(savedProduct.modelNumber).isEqualTo(command.modelNumber)
        assertThat(savedProduct.releaseDate).isEqualTo(command.releaseDate)
        assertThat(savedProduct.option).isEqualTo(command.option)
        assertThat(savedProduct.thumbnailUrl).isEqualTo(command.images.first().imageUrl)
        assertThat(savedProduct.images.size).isEqualTo(command.images.size)
        savedProduct.images.forEachIndexed { index, productImage ->
            assertThat(productImage.imageUrl).isEqualTo(command.images[index].imageUrl)
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

        val product = service.getProduct(savedProduct.id)

        assertThat(product.categoryId).isEqualTo(command.categoryId)
        assertThat(product.nameEn).isEqualTo(command.nameEn)
        assertThat(product.brand).isEqualTo(command.brand)
        assertThat(product.nameKo).isEqualTo(command.nameKo)
        assertThat(product.releasePrice!!.compareTo(command.releasePrice)).isEqualTo(0)
        assertThat(product.modelNumber).isEqualTo(command.modelNumber)
        assertThat(product.releaseDate).isEqualTo(command.releaseDate)
        assertThat(product.option).isEqualTo(command.option)
        assertThat(product.thumbnailUrl).isEqualTo(command.images.first().imageUrl)
        assertThat(product.images.size).isEqualTo(command.images.size)
        product.images.forEachIndexed { index, productImage ->
            assertThat(productImage.imageUrl).isEqualTo(command.images[index].imageUrl)
        }
    }

    @Test
    fun `존재하지 않는 상품은 조회할 수 없다`() {
        val invalidId = 9999L
        val exception = assertThrows<ProductNotFoundException> {
            service.getProduct(invalidId)
        }
        assertThat(exception.message).isEqualTo("상품을 찾을 수 없습니다. id: $invalidId")
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
        val savedProductOne = service.create(commandOne)
        val savedProductTwo = service.create(commandTwo)
        val savedProductThree = service.create(commandThree)
        val searchCommand = ProductSearchCommand()
        val pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"))

        val result = service.getAllBySearchCommand(searchCommand, pageable)

        assertThat(result).hasSize(3)
        assertThat(result[0].id).isEqualTo(savedProductThree.id)
        assertThat(result[0].categoryId).isEqualTo(savedProductThree.categoryId)
        assertThat(result[0].nameKo).isEqualTo(savedProductThree.nameKo)
        assertThat(result[0].thumbnailUrl).isEqualTo(savedProductThree.images.first().imageUrl)
        assertThat(result[1].id).isEqualTo(savedProductTwo.id)
        assertThat(result[1].categoryId).isEqualTo(savedProductTwo.categoryId)
        assertThat(result[1].nameKo).isEqualTo(savedProductTwo.nameKo)
        assertThat(result[1].thumbnailUrl).isEqualTo(savedProductTwo.images.first().imageUrl)
        assertThat(result[2].id).isEqualTo(savedProductOne.id)
        assertThat(result[2].categoryId).isEqualTo(savedProductOne.categoryId)
        assertThat(result[2].nameKo).isEqualTo(savedProductOne.nameKo)
        assertThat(result[2].thumbnailUrl).isEqualTo(savedProductOne.images.first().imageUrl)
    }
}
