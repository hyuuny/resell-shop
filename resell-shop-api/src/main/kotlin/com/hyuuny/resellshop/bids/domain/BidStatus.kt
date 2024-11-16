package com.hyuuny.resellshop.bids.domain

enum class BidStatus(private val title: String) {
    WAITING("대기 중"),
    IN_PROGRESS("입찰 중"),
    COMPLETED("입찰 성공"),
    CANCELLED("취소");

    companion object {
        val ongoingStatuses = setOf(WAITING, IN_PROGRESS)
    }
}
