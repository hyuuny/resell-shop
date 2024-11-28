package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.dataaccess.BidHistoryRepository
import com.hyuuny.resellshop.bids.dataaccess.BidRepository
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.core.common.exception.AlreadyExistBidException
import com.hyuuny.resellshop.core.common.exception.BidNotFoundException
import com.hyuuny.resellshop.core.common.exception.InvalidBidPriceException
import com.hyuuny.resellshop.products.TestEnvironment
import com.hyuuny.resellshop.products.dataaccess.ProductRepository
import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.domain.ProductImage
import com.hyuuny.resellshop.products.domain.ProductSize
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate

@TestEnvironment
class BidServiceTest(
    private val repository: BidRepository,
    private val bidHistoryRepository: BidHistoryRepository,
    private val productRepository: ProductRepository,
    private val service: BidService,
) {

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
        bidHistoryRepository.deleteAll()
        productRepository.deleteAll()
    }

    @Test
    fun `판매자는 상품 판매 입찰을 등록할 수 있고, 입찰 히스토리도 저장된다`() {
        val command = CreateBidCommand(
            type = BidType.SELL,
            userId = 1L,
            productId = 1L,
            productSizeId = 1L,
            price = 55000,
        )

        val savedBid = service.create(command)
        val savedBidHistory = bidHistoryRepository.findByBidId(savedBid.id)

        assertThat(savedBid.id).isNotNull()
        assertThat(savedBid.type).isEqualTo(command.type)
        assertThat(savedBid.status).isEqualTo(BidStatus.WAITING)
        assertThat(savedBid.orderNumber).isNotNull()
        assertThat(savedBid.userId).isEqualTo(command.userId)
        assertThat(savedBid.productId).isEqualTo(command.productId)
        assertThat(savedBid.productSizeId).isEqualTo(command.productSizeId)
        assertThat(savedBid.price).isEqualTo(command.price)
        assertThat(savedBid.createdAt).isNotNull()

        assertThat(savedBidHistory).isNotNull()
        assertThat(savedBidHistory!!.bidId).isEqualTo(savedBid.id)
        assertThat(savedBidHistory.type).isEqualTo(savedBid.type)
        assertThat(savedBidHistory.status).isEqualTo(savedBid.status)
        assertThat(savedBidHistory.userId).isEqualTo(savedBid.userId)
    }

    @Test
    fun `구매자는 상품 구매 입찰을 등록할 수 있고, 입찰 히스토리도 저장된다`() {
        val command = CreateBidCommand(
            type = BidType.BUY,
            userId = 2L,
            productId = 1L,
            productSizeId = 1L,
            price = 50000,
        )

        val savedBid = service.create(command)
        val savedBidHistory = bidHistoryRepository.findByBidId(savedBid.id)

        assertThat(savedBid.id).isNotNull()
        assertThat(savedBid.type).isEqualTo(command.type)
        assertThat(savedBid.status).isEqualTo(BidStatus.WAITING)
        assertThat(savedBid.orderNumber).isNotNull()
        assertThat(savedBid.userId).isEqualTo(command.userId)
        assertThat(savedBid.productId).isEqualTo(command.productId)
        assertThat(savedBid.productSizeId).isEqualTo(command.productSizeId)
        assertThat(savedBid.price).isEqualTo(command.price)
        assertThat(savedBid.createdAt).isNotNull()

        assertThat(savedBidHistory).isNotNull()
        assertThat(savedBidHistory!!.bidId).isEqualTo(savedBid.id)
        assertThat(savedBidHistory.type).isEqualTo(savedBid.type)
        assertThat(savedBidHistory.status).isEqualTo(savedBid.status)
        assertThat(savedBidHistory.userId).isEqualTo(savedBid.userId)
    }

    @CsvSource("SELL", "BUY")
    @ParameterizedTest
    fun `같은 상품 사이즈의 중복 입찰을 등록할 수 없다`(type: BidType) {
        val command = CreateBidCommand(
            type = type,
            userId = 2L,
            productId = 1L,
            productSizeId = 1L,
            price = 50000,
        )
        service.create(command)
        val exception = assertThrows<AlreadyExistBidException> {
            service.create(command)
        }
        assertThat(exception.message).isEqualTo("이미 해당 상품에 대한 입찰이 존재합니다.")
    }

    @Test
    fun `입찰 가격은 0보다 커야 한다`() {
        val command = CreateBidCommand(
            type = BidType.SELL,
            userId = 1L,
            productId = 1L,
            productSizeId = 1L,
            price = 0,
        )
        val exception = assertThrows<InvalidBidPriceException> {
            service.create(command)
        }
        assertThat(exception.message).isEqualTo("입찰 가격은 0보다 커야 합니다.")
    }

    @Test
    fun `상품의 사이즈별로 등록된 가장 낮은 입찰가 목록이 조회된다`() {
        val product = Product.of(
            categoryId = 1L,
            nameEn = "Stussy x Our Legacy Work Shop 8 Ball Pigment Dyed Yin Yang T-Shirt Black",
            brand = Brand.STUSSY,
            nameKo = "스투시 x 아워레가시 워크샵 8볼 피그먼트 다이드 음양 티셔츠 블랙",
            releasePrice = 82000,
            modelNumber = "3903959",
            releaseDate = LocalDate.of(2024, 9, 27),
            option = "BLACK",
        )
        product.addImages(
            listOf(
                ProductImage.of(product, "https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-1.jpg"),
                ProductImage.of(product, "https://my-bucket.s3.us-west-2.amazonaws.com/products/images/sample-2.jpg"),
            )
        )
        product.addSizes(
            listOf(
                ProductSize.of(product, "S"),
                ProductSize.of(product, "M"),
                ProductSize.of(product, "L"),
                ProductSize.of(product, "XL"),
            )
        )
        val savedProduct = productRepository.save(product)
        val productSizes = savedProduct.sizes

        val bidCommands = listOf(
            CreateBidCommand(
                type = BidType.SELL,
                userId = 1L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[0].id!!,
                price = 70000
            ),
            CreateBidCommand(
                type = BidType.SELL,
                userId = 2L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[0].id!!,
                price = 55000,
            ),
            CreateBidCommand(
                type = BidType.BUY,
                userId = 3L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[0].id!!,
                price = 43000,
            ),
            CreateBidCommand(
                type = BidType.BUY,
                userId = 5L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[0].id!!,
                price = 30000,
            ),
            CreateBidCommand(
                type = BidType.SELL,
                userId = 3L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[1].id!!,
                price = 60000,
            ),
            CreateBidCommand(
                type = BidType.SELL,
                userId = 4L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[1].id!!,
                price = 65000,
            ),
            CreateBidCommand(
                type = BidType.SELL,
                userId = 5L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[2].id!!,
                price = 90000,
            ),
            CreateBidCommand(
                type = BidType.SELL,
                userId = 6L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[2].id!!,
                price = 75000,
            ),
            CreateBidCommand(
                type = BidType.BUY,
                userId = 1L,
                productId = savedProduct.id!!,
                productSizeId = productSizes[2].id!!,
                price = 72000,
            ),
        )
        bidCommands.forEach { service.create(it) }

        val response = service.findAllMinPriceByProductId(product.id!!)
        assertThat(response.productId).isEqualTo(product.id)
        assertThat(response.bidPriceDetails.size).isEqualTo(8)

        assertThat(response.bidPriceDetails[0].productSizeId).isEqualTo(productSizes[0].id)
        assertThat(response.bidPriceDetails[0].type).isEqualTo(BidType.SELL)
        assertThat(response.bidPriceDetails[0].minPrice).isEqualTo(bidCommands[1].price)
        assertThat(response.bidPriceDetails[1].productSizeId).isEqualTo(productSizes[0].id)
        assertThat(response.bidPriceDetails[1].type).isEqualTo(BidType.BUY)
        assertThat(response.bidPriceDetails[1].minPrice!!).isEqualTo(bidCommands[3].price)

        assertThat(response.bidPriceDetails[2].productSizeId).isEqualTo(productSizes[1].id)
        assertThat(response.bidPriceDetails[2].type).isEqualTo(BidType.SELL)
        assertThat(response.bidPriceDetails[2].minPrice).isEqualTo(bidCommands[4].price)
        assertThat(response.bidPriceDetails[3].productSizeId).isEqualTo(productSizes[1].id)
        assertThat(response.bidPriceDetails[3].type).isEqualTo(BidType.BUY)
        assertThat(response.bidPriceDetails[3].minPrice).isEqualTo(0)

        assertThat(response.bidPriceDetails[4].productSizeId).isEqualTo(productSizes[2].id)
        assertThat(response.bidPriceDetails[4].type).isEqualTo(BidType.SELL)
        assertThat(response.bidPriceDetails[4].minPrice!!).isEqualTo(bidCommands[7].price)
        assertThat(response.bidPriceDetails[5].productSizeId).isEqualTo(productSizes[2].id)
        assertThat(response.bidPriceDetails[5].type).isEqualTo(BidType.BUY)
        assertThat(response.bidPriceDetails[5].minPrice).isEqualTo(bidCommands[8].price)

        assertThat(response.bidPriceDetails[6].productSizeId).isEqualTo(productSizes[3].id)
        assertThat(response.bidPriceDetails[6].type).isEqualTo(BidType.SELL)
        assertThat(response.bidPriceDetails[6].minPrice!!).isEqualTo(0)
        assertThat(response.bidPriceDetails[7].productSizeId).isEqualTo(productSizes[3].id)
        assertThat(response.bidPriceDetails[7].type).isEqualTo(BidType.BUY)
        assertThat(response.bidPriceDetails[7].minPrice).isEqualTo(0)
    }

    @Test
    fun `입찰 금액을 변경할 수 있다`() {
        val command = CreateBidCommand(
            type = BidType.SELL,
            userId = 1L,
            productId = 1L,
            productSizeId = 1L,
            price = 55000,
        )
        val savedBid = service.create(command)
        val changePriceCommand = ChangePriceCommand(price = 60000)

        service.changePrice(savedBid.id, changePriceCommand)

        repository.findByIdOrNull(savedBid.id)!!.let {
            assertThat(it).isNotNull()
            assertThat(it.price.amount).isEqualTo(changePriceCommand.price)
        }
    }

    @Test
    fun `존재하지 않는 입찰 내역의 금액을 변경할 수 없다`() {
        val invalidId = 9999L
        val changePriceCommand = ChangePriceCommand(price = 60000)
        val exception = assertThrows<BidNotFoundException> {
            service.changePrice(invalidId, changePriceCommand)
        }
        assertThat(exception.message).isEqualTo("입찰 내역을 찾을 수 없습니다. id: $invalidId")
    }

    @Test
    fun `등록한 입찰 내역을 삭제할 수 있다`() {
        val command = CreateBidCommand(
            type = BidType.SELL,
            userId = 1L,
            productId = 1L,
            productSizeId = 1L,
            price = 55000,
        )
        val savedBid = service.create(command)

        service.delete(savedBid.id)

        assertThat(repository.findByIdOrNull(savedBid.id)).isNull()
    }

    @Test
    fun `존재하지 않는 입찰 내역을 삭제할 수 없다`() {
        val invalidId = 9999L
        val exception = assertThrows<BidNotFoundException> {
            service.delete(invalidId)
        }
        assertThat(exception.message).isEqualTo("입찰 내역을 찾을 수 없습니다. id: $invalidId")
    }

}