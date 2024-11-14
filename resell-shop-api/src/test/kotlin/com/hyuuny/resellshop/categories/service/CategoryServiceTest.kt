package com.hyuuny.resellshop.categories.service

import com.hyuuny.resellshop.categories.domain.Category
import com.hyuuny.resellshop.categories.infrastructure.CategoryRepository
import com.hyuuny.resellshop.products.TestEnvironment
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

@TestEnvironment
class CategoryServiceTest(
    private val repository: CategoryRepository,
    private val service: CategoryService,
) {

    @AfterEach
    fun tearDown() {
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

        val foundCategories = service.findAll()

        assertThat(foundCategories.size).isEqualTo(6)
        foundCategories.forEachIndexed { index, category ->
            assertThat(categories[index].id).isEqualTo(category.id)
            assertThat(categories[index].name).isEqualTo(category.name)
            assertThat(categories[index].iconImageUrl).isEqualTo(category.iconImageUrl)
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

        val foundCategories = service.findAllByParentId(savedParentCategory.id!!)

        assertThat(foundCategories.size).isEqualTo(3)
        foundCategories.forEachIndexed { index, childCategory ->
            assertThat(childCategories[index].id).isEqualTo(childCategory.id)
            assertThat(childCategories[index].parent!!.id).isEqualTo(parentCategory.id)
            assertThat(childCategories[index].name).isEqualTo(childCategory.name)
            assertThat(childCategories[index].iconImageUrl).isEqualTo(childCategory.iconImageUrl)
        }
    }
}
