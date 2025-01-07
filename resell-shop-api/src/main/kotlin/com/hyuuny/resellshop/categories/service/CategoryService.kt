package com.hyuuny.resellshop.categories.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class CategoryService(
    private val reader: CategoryReader
) {

    fun findAll(): List<CategoryResponse> {
        return reader.readAll().map { CategoryResponse(it) }
    }

    fun findAllByParentId(id: Long): List<CategoryResponse> {
        return reader.read(id).children.map { CategoryResponse(it) }
    }
}
