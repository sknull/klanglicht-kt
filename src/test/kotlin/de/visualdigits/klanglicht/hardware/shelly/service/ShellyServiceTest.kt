package de.visualdigits.klanglicht.hardware.shelly.service

import de.visualdigits.klanglicht.hardware.shelly.model.ShellyDevice
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ShellyServiceTest @Autowired constructor(

    private val shellyService: ShellyService
) {

    @Test
    fun testStatus() {
        val status = shellyService.status()
        print(status.toList().joinToString("\n") { s -> "${s.first.name} [${s.first.ipAddress}] ${s.second.mode}" })
    }

    @Test
    fun testSingleStatus() {
        val isOn = shellyService.isOn("192.168.178.48")
        print(isOn)
    }
}
