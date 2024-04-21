package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.webclient

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.jsonMapper
import de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.service.YamahaAvantageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@Disabled("only for local testing")
@ExtendWith(SpringExtension::class)
@SpringBootTest
class YamahaAvantageReceiverClientTest @Autowired constructor(
    private val client: YamahaAvantageService
) {

    private val mapper = jacksonMapperBuilder().enable(SerializationFeature.INDENT_OUTPUT).build()

    @Test
    fun testSet() {
//        val responseCode = client.setSurroundProgram("Sci-Fi")
        val responseCode = client.setSurroundProgram("Standard")
        assertEquals(0, responseCode.responseCode)
    }

    @Test
    fun testInfo() {
        val deviceInfo = client.deviceInfo()
        println(mapper.writeValueAsString(deviceInfo))
    }

    @Test
    fun testFeatures() {
        val features = client.features()
        println(mapper.writeValueAsString(features))
    }

    @Test
    fun testSoundProgramList() {
        val soundProgramList = client.soundProgramList()
        println(soundProgramList?.soundProgramList?.sorted()?.joinToString("\n"))
    }
}

