package com.hyuuny.resellshop.categories.infrastructure

import com.hyuuny.resellshop.categories.dataaccess.CategoryRepository
import com.hyuuny.resellshop.categories.domain.Category
import com.hyuuny.resellshop.core.common.exception.CategoryNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class CategoryReaderImpl(
    private val repository: CategoryRepository,
) : CategoryReader {

    override fun findById(id: Long): Category {
        return repository.findByIdOrNull(id)
            ?: throw CategoryNotFoundException("카테고리를 찾을 수 없습니다. id: $id")
    }

    override fun findAll(): List<Category> {
        return repository.findAll()
    }

}