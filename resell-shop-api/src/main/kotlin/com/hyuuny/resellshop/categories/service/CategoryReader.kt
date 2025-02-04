package com.hyuuny.resellshop.categories.service

import com.hyuuny.resellshop.categories.dataaccess.CategoryRepository
import com.hyuuny.resellshop.categories.domain.Category
import com.hyuuny.resellshop.core.common.exception.CategoryNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class CategoryReader(
    private val repository: CategoryRepository,
) {

    fun read(id: Long): Category = repository.findByIdOrNull(id)
        ?: throw CategoryNotFoundException("카테고리를 찾을 수 없습니다. id: $id")

    fun readAll(): List<Category> = repository.findAll()
}