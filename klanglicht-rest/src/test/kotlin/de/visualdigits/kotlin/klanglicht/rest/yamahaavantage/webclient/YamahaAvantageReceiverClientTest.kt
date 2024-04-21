package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.webclient

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

    @Test
    fun testSet() {
//        val responseCode = client.setSurroundProgram("Sci-Fi")
        val responseCode = client.setSurroundProgram("Standard")
        assertEquals(0, responseCode.responseCode)
    }

    @Test
    fun testInfo() {
        val deviceInfo = client.deviceInfo()
        println(deviceInfo)
    }

    @Test
    fun testFeatures() {
        val features = client.features()
        println(features)
    }

    @Test
    fun testSoundProgramList() {
        val soundProgramList = client.soundProgramList()
        println(soundProgramList.soundProgramList.sorted().joinToString("\n"))
    }
}

