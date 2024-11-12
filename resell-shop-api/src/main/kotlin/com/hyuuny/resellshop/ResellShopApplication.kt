package com.hyuuny.resellshop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class ResellShopApplication

fun main(args: Array<String>) {
    runApplication<ResellShopApplication>(*args)
}
