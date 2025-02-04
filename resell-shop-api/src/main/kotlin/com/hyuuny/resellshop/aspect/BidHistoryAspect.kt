package com.hyuuny.resellshop.aspect

import com.hyuuny.resellshop.bids.dataaccess.BidHistoryRepository
import com.hyuuny.resellshop.bids.domain.BidHistory
import com.hyuuny.resellshop.bids.service.BidResponse
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class BidHistoryAspect(
    private val repository: BidHistoryRepository,
) {

    @AfterReturning(
        pointcut = "execution(* com.hyuuny.resellshop.bids.service.BidService.create(..))",
        returning = "bid"
    )
    fun saveBidHistory(bid: BidResponse) {
        repository.save(
            BidHistory.of(
                bidId = bid.id,
                type = bid.type,
                status = bid.status,
                userId = bid.userId,
                createdAt = bid.createdAt,
            )
        )
    }
}
