package de.visualdigits.klanglicht.yamahaavantage.controller

import de.visualdigits.klanglicht.yamahaavantage.service.YamahaAvantageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/yamaha/avantage/json")
class YamahaAvantageRestController(
    private val yamahaAvantageService: YamahaAvantageService
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @PutMapping("/surroundProgram")
    fun controlSurroundProgram(@RequestParam("program") program: String) {
        log.info("Setting surround sound program to '$program'")
        yamahaAvantageService.setSurroundProgram(program)
    }

    @PutMapping("/setPureDirect")
    fun controlSurroundProgram(@RequestParam("enable") enable: Boolean) {
        log.info("Setting pure direct to '$enable'")
        yamahaAvantageService.setPureDirect(enable)
    }

    @PutMapping("/requestSystemReboot")
    fun requestSystemReboot() {
        log.info("Requesting system reboot")
        yamahaAvantageService.requestSystemReboot()
    }
}
