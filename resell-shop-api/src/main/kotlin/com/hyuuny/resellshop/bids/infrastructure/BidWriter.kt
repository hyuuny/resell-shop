package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.domain.Bid

interface BidWriter {

    fun write(newBid: NewBid): Bid

    fun changePrice(changePriceBid: ChangePriceBid)

}