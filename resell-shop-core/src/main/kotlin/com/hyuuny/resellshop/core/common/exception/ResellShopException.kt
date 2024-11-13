package com.hyuuny.resellshop.core.common.exception

open class ResellShopException(
    val code: ErrorCode,
    override val message: String? = null,
) : RuntimeException(message)

class ProductNotFoundException(
    message: String,
) : ResellShopException(code= ErrorCode.PRODUCT_NOT_FOUND, message = message)
