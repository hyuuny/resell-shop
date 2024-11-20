package com.hyuuny.resellshop.products.dataaccess

import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.domain.QProduct.product
import com.hyuuny.resellshop.products.service.ProductSearchCommand
import com.hyuuny.resellshop.utils.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import kotlin.math.min

class ProductRepositoryImpl : QuerydslRepositorySupport(Product::class.java), ProductRepositoryCustom {

    override fun findAllBySearchCommand(
        searchCondition: ProductSearchCommand,
        pageable: Pageable
    ): SimplePage<Product> {
        var query = from(product)

        with(searchCondition) {
            categoryId?.let { query = query.where(product.categoryId.eq(it)) }
            brand?.let { query = query.where(product.brand.eq(it)) }
            nameKo?.let { query = query.where(product.nameKo.contains(it)) }
        }
        query.orderBy(*QueryDslUtil.getSort(pageable, product))

        val size = pageable.pageSize
        val content = query.limit(size.toLong() + 1).offset(pageable.offset).fetch()
        val last = content.size <= size

        return SimplePage(content.slice(0 until min(content.size, size)), pageable.pageNumber + 1, size, last)
    }

}
