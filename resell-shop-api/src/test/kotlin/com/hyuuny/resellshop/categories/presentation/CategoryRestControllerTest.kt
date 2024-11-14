package com.hyuuny.resellshop.categories.presentation

import com.hyuuny.resellshop.categories.domain.Category
import com.hyuuny.resellshop.categories.infrastructure.CategoryRepository
import com.hyuuny.resellshop.products.TestEnvironment
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

@TestEnvironment
class CategoryRestControllerTest(
    @LocalServerPort private val port: Int,
    private val repository: CategoryRepository,
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
    fun `카테고리 목록을 조회할 수 있다`() {
        val categoryOne =
            Category.of(null, "아우터", "https://my-bucket.s3.us-west-2.amazonaws.com/categories/icons/outer.png")
        val categoryTwo =
            Category.of(null, "상의", "https://my-bucket.s3.us-west-2.amazonaws.com/categories/icons/top.png")
        val categoryThree =
            Category.of(null, "신발", "https://my-bucket.s3.us-west-2.amazonaws.com/categories/icons/shoes.png")
        val categoryFour =
            Category.of(null, "하의", "https://my-bucket.s3.us-west-2.amazonaws.com/categories/icons/bottom.png")
        val categoryFive =
            Category.of(null, "가방", "https://my-bucket.s3.us-west-2.amazonaws.com/categories/icons/bags.png")
        val categorySix =
            Category.of(null, "지갑", "https://my-bucket.s3.us-west-2.amazonaws.com/categories/icons/purse.png")
        val categories = listOf(categoryOne, categoryTwo, categoryThree, categoryFour, categoryFive, categorySix)
        repository.saveAll(categories)

        Given {
            contentType(ContentType.JSON)
            log().all()
        } When {
            get("/api/v1/categories")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("data.size()", equalTo(categories.size))
            log().all()
        }
    }

    @Test
    fun `부모 카테고리와 하위 카테고리 목록을 조회할 수 있다`() {
        val parentCategory =
            Category.of(null, "아우터", "https://my-bucket.s3.us-west-2.amazonaws.com/categories/icons/outer.png")
        val savedParentCategory = repository.save(parentCategory)
        val childCategoryOne = Category.of(
            savedParentCategory,
            "패딩",
            "https://my-bucket.s3.us-west-2.amazonaws.com/categories/icons/padding.png"
        )
        val childCategoryTwo = Category.of(
            savedParentCategory,
            "코트",
            "https://my-bucket.s3.us-west-2.amazonaws.com/categories/icons/coat.png"
        )
        val childCategoryThree = Category.of(
            savedParentCategory,
            "자켓",
            "https://my-bucket.s3.us-west-2.amazonaws.com/categories/icons/jackets.png"
        )
        val childCategories = listOf(childCategoryOne, childCategoryTwo, childCategoryThree)
        repository.saveAll(childCategories)
        childCategories.forEach { savedParentCategory.addChild(it) }
        repository.save(parentCategory)

        Given {
            contentType(ContentType.JSON)
            log().all()
        } When {
            get("/api/v1/categories/${savedParentCategory.id}/children")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("data.size()", equalTo(childCategories.size))
            log().all()
        }
    }
}
