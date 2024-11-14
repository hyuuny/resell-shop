package com.hyuuny.resellshop.categories.domain

import jakarta.persistence.*

@Entity
@Table(name = "categories")
class Category(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id") var parent: Category? = null,
    val name: String,
    val iconImageUrl: String,
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    val children: MutableList<Category> = mutableListOf()
) {
    companion object {
        fun of(
            parent: Category?,
            name: String,
            iconImageUrl: String,
        ): Category = Category(
            parent = parent,
            name = name,
            iconImageUrl = iconImageUrl,
        )
    }

    fun addChild(child: Category) {
        children.add(child)
        child.parent = this
    }
}
