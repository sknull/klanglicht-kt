package de.visualdigits.kotlin.klanglicht.rest.yamaha.controller

import de.visualdigits.kotlin.klanglicht.hardware.yamaha.client.YamahaReceiverClient
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
@RequestMapping("/v1/yamaha/xml")
class YamahaReceiverRestController(
    val configHolder: ConfigHolder
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    var client: YamahaReceiverClient? = null

    @PostConstruct
    fun initialize() {
        if (client == null) {
            client = configHolder.preferences?.getService("receiver")?.url?.let { YamahaReceiverClient(it) }
        }
    }

    @PutMapping("/surroundProgram")
    fun surroundProgram(@RequestParam("program") program: String) {
        log.info("Setting surround sound program to '$program'")
        client?.controlSurroundProgram(program)
    }
}
