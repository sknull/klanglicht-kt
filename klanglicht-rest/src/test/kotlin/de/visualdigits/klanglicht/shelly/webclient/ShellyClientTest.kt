package de.visualdigits.klanglicht.shelly.webclient

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("for local testing only")
class ShellyClientTest {

    @Test
    fun testPowerOn() {
        ShellyClient.setPower(
            ipAddress = "192.168.178.38",
            command = "relay/0",
            turnOn = true
        )
    }

    @Test
    fun testPowerOff() {
        ShellyClient.setPower(
            ipAddress = "192.168.178.38",
            command = "relay/0",
            turnOn = false
        )
    }
}
