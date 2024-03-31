package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.controller

import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.client.YamahaAvantageReceiverClient
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/yamaha/avantage/json")
class YamahaAvantageReceiverRestController(
    val configHolder: ConfigHolder
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    var client: YamahaAvantageReceiverClient? = null

    @PostConstruct
    fun initialize() {
        if (client == null) {
            client = YamahaAvantageReceiverClient(
                configHolder!!.preferences?.getService("receiver")?.url!!
            )
        }
    }

    @PutMapping("/surroundProgram")
    fun controlSurroundProgram(@RequestParam("program") program: String) {
        log.info("Setting surround sound program to '$program'")
        client!!.setSurroundProgram(program)
    }
}
