package com.hyuuny.resellshop.orders.domain

enum class OrderStatus(private val title: String) {
    CREATED("주문 생성"),
    SELLER_PRODUCT_DELIVERING("판매자 배송 중"),
    COMPLETED_IN_STOCK("입고 완료"),
    INSPECTION("검수 중"),
    INSPECTION_PASSED("검수 합격"),
    INSPECTION_FAILED("검수 실패"),
    CANCELLED("취소됨"),
    DELIVERING_TO_BUYER("배송 중"),
    DELIVERED("배송 완료");

    companion object {
        val cancelableStatus = setOf(CREATED, INSPECTION_FAILED)
    }
}
