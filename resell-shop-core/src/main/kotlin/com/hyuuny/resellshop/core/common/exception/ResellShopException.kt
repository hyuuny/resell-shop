package com.hyuuny.resellshop.core.common.exception

open class ResellShopException(
    val code: ErrorType,
    override val message: String? = null,
) : RuntimeException(message)

class ProductNotFoundException(
    message: String,
) : ResellShopException(code= ErrorType.PRODUCT_NOT_FOUND, message = message)

class CategoryNotFoundException(
    message: String,
) : ResellShopException(code = ErrorType.CATEGORY_NOT_FOUND, message = message)

class AlreadyExistBidException(
    message: String,
) : ResellShopException(code = ErrorType.ALREADY_EXIST_BID, message = message)

class InvalidBidPriceException(
    message: String,
) : ResellShopException(code = ErrorType.INVALID_BID_PRICE, message = message)