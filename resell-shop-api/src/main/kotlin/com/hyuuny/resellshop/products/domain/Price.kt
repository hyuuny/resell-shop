package com.hyuuny.resellshop.products.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal

private const val ZERO_NUMBER = 0L

@Embeddable
data class Price(
    @Column(nullable = false) val amount: BigDecimal
) {
    init {
        require(amount >= BigDecimal.ZERO)
    }

    constructor(amount: Long) : this(BigDecimal.valueOf(amount))

    companion object {
        val ZERO = Price(ZERO_NUMBER)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Price

        return amount.toDouble() == other.amount.toDouble()
    }

    override fun hashCode(): Int {
        return amount.hashCode()
    }
}
