package de.visualdigits.klanglicht.shelly.service

import de.visualdigits.klanglicht.shelly.service.ShellyService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class ShellyServiceTest @Autowired constructor(
    private val shellyService: ShellyService
) {

    @Test
    fun testStatus() {
        val status = shellyService.status()
        print(status)
    }
}
