package de.visualdigits.kotlin.klanglicht.rest

import de.visualdigits.kotlin.klanglicht.rest.common.configuration.ConfigHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class KlanglichtHandler {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    val configHolder: ConfigHolder? = null
}
