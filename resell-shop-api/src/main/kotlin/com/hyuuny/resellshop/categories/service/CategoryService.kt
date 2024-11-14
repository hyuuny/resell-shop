package com.hyuuny.resellshop.categories.service

import com.hyuuny.resellshop.categories.infrastructure.CategoryRepository
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

}
