package de.visualdigits.klanglicht.hardware.harmony

import org.junit.jupiter.api.Test

class HarmonyClientTest {

    @Test
    fun testClient() {
        val client = HarmonyClient.getInstance()
        client.connect("192.168.178.27")
        val config = client.getConfig()
        println(config)
        client.disconnect()
    }
}