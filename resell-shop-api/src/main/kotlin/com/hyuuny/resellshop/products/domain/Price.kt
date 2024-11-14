package com.hyuuny.resellshop.products.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal

private const val ZERO_NUMBER = 0L

@Embeddable
data class Price(
    @Column(nullable = false) val amount: Long
) {
    init {
        require(amount >= 0) { "금액은 음수가 아니어야 합니다." }
    }

    constructor(amount: BigDecimal) : this(amount.toLong())

    companion object {
        val ZERO = Price(ZERO_NUMBER)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Price

        return amount == other.amount // Long 타입에 맞게 비교
    }

    override fun hashCode(): Int {
        return amount.hashCode()
    }
}
