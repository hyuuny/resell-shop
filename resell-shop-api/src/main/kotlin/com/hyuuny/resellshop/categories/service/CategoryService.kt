package com.hyuuny.resellshop.categories.service

import com.hyuuny.resellshop.categories.infrastructure.CategoryReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class CategoryService(
    private val reader: CategoryReader
) {

    fun findAll(): List<CategoryResponse> {
        return reader.findAll().map { CategoryResponse(it) }
    }

    fun findAllByParentId(id: Long): List<CategoryResponse> {
        val category = reader.findById(id)
        return category.children.map { CategoryResponse(it) }
    }
}
