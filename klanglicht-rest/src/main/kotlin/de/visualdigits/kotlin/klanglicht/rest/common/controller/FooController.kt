package de.visualdigits.kotlin.klanglicht.rest.common.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FooController {

    @GetMapping("/hello")
    fun hello(): String {
        return "hello"
    }
}
