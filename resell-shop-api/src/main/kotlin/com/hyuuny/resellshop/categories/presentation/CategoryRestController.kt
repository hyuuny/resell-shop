package com.hyuuny.resellshop.categories.presentation

import com.hyuuny.resellshop.categories.service.CategoryResponse
import com.hyuuny.resellshop.categories.service.CategoryService
import com.hyuuny.resellshop.core.common.response.ResellShopResponse
import com.hyuuny.resellshop.core.common.response.ResellShopResponse.Companion.success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/categories")
@RestController
class CategoryRestController(
    private val service: CategoryService,
) {

    @GetMapping
    fun getCategories(): ResponseEntity<ResellShopResponse<List<CategoryResponse>>> {
        val categories = service.findAll()
        return ResponseEntity.ok(success(categories))
    }

    @GetMapping("/{id}/children")
    fun getChildCategories(@PathVariable id: Long): ResponseEntity<ResellShopResponse<List<CategoryResponse>>> {
        val categories = service.findAllByParentId(id)
        return ResponseEntity.ok(success(categories))
    }
}
