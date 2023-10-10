package de.visualdigits.kotlin.klanglicht.rest.controller

import de.visualdigits.kotlin.klanglicht.model.parameter.Scene
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/dmx/v1")
class KlangLichtController {

    private val log = LoggerFactory.getLogger(KlangLichtController::class.java)

    @GetMapping("hello")
    fun hello(): String {
        log.info("### hello world!")
        return "foo"
    }

    @GetMapping("setScene")
    fun setScene(scene: Scene) {

    }
}
