package com.hyuuny.resellshop.product.service

import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.infrastructure.ProductRepository
import com.hyuuny.resellshop.products.service.CreateProductCommand
import com.hyuuny.resellshop.products.service.ProductImageCommand
import com.hyuuny.resellshop.products.service.ProductService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
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

}
