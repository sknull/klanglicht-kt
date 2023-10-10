package de.visualdigits.kotlin.klanglicht.rest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class KlangLichtMain

fun main(args: Array<String>) {
    runApplication<KlangLichtMain>(*args)
}
