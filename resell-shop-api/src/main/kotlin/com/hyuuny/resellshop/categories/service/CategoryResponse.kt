package com.hyuuny.resellshop.categories.service

import com.hyuuny.resellshop.categories.domain.Category

data class CategoryResponse(
    val id: Long,
    val parentId: Long?,
    val name: String,
    val iconImageUrl: String,
) {
    constructor(entity: Category) : this(
        id = entity.id!!,
        parentId = entity.parent?.id,
        name = entity.name,
        iconImageUrl = entity.iconImageUrl,
    )
}
