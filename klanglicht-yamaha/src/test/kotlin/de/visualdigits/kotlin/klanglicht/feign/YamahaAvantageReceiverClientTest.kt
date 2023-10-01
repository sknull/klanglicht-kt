package de.visualdigits.kotlin.klanglicht.feign

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class YamahaAvantageReceiverClientTest {

    private val URL = "http://192.168.178.46"

    @Test
    fun testSet() {
        val client = YamahaAvantageReceiverClient(URL)
        val responseCode = client.setSurroundProgram("Sci-Fi")
        assertEquals(0, responseCode.responseCode)
    }

    @Test
    fun testInfo() {
        val client = YamahaAvantageReceiverClient(URL)
        val deviceInfo = client.deviceInfo()
        println(deviceInfo)
    }

    @Test
    fun testFeatures() {
        val client = YamahaAvantageReceiverClient(URL)
        val features = client.features()
        println(features)
    }

    @Test
    fun testSoundProgramList() {
        val client = YamahaAvantageReceiverClient(URL)
        val soundProgramList = client.soundProgramList()
        println(soundProgramList)
    }
}
