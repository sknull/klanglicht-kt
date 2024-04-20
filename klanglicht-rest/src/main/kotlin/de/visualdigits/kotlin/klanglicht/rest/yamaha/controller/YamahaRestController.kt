package de.visualdigits.kotlin.klanglicht.rest.yamaha.controller

import de.visualdigits.kotlin.klanglicht.rest.yamaha.service.YamahaService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/yamaha/xml")
class YamahaRestController(
    private val yamahaService: YamahaService
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @PutMapping("/surroundProgram")
    fun surroundProgram(@RequestParam("program") program: String) {
        log.info("Setting surround sound program to '$program'")
        yamahaService.controlSurroundProgram(program)
    }
}
