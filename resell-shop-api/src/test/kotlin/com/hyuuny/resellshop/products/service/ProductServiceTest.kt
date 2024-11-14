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
}
