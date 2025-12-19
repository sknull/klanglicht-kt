package de.visualdigits.klanglicht.hardware.twinkly.service

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.shelly.service.ShellyService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TwinklyServiceTest @Autowired constructor(
    private val prefs: ApplicationPreferences,
    private val twinklyService: TwinklyService,
    private val shellyService: ShellyService
) {

    @Test
    fun testMusic() {
        prefs.stage!!.devices!!.refreshTwinklyDevices(shellyService)
        val scenes = prefs.loadScenes()
        scenes.refreshSceneMap()
        twinklyService.musicOff()
    }
}