package com.hyuuny.resellshop.core.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ResellShopResponse<T>(
    val message: String? = null,
    val code: String? = null,
    @JsonUnwrapped val data: T? = null
) {

    companion object {
        fun error(message: String?): ResellShopResponse<Unit> = ResellShopResponse(message = message)

        fun error(message: String?, code: String?): ResellShopResponse<Unit> =
            ResellShopResponse(message = message, code = code)

        fun <T> success(data: T?): ResellShopResponse<T> = ResellShopResponse(data = data)
    }
}
