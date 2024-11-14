package com.hyuuny.resellshop.categories.service

import com.hyuuny.resellshop.categories.infrastructure.CategoryRepository
import com.hyuuny.resellshop.core.common.exception.CategoryNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class CategoryService(
    private val repository: CategoryRepository,
) {

    fun findAll(): List<CategoryResponse> {
        return repository.findAll().map { CategoryResponse(it) }
    }

    fun findAllByParentId(id: Long): List<CategoryResponse> {
        val category = repository.findByIdOrNull(id)
            ?: throw CategoryNotFoundException("카테고리를 찾을 수 없습니다. id: $id")
        return category.children.map { CategoryResponse(it) }
    }
}
