package de.visualdigits.kotlin.klanglicht.hardware.shelly.status

import de.visualdigits.kotlin.klanglicht.hardware.shelly.model.status.Light
import org.junit.jupiter.api.Test
import java.io.File

class LightTest {

    @Test
    fun testModel() {
        val json = File(ClassLoader.getSystemResource("shelly/shelly-rgb.json").toURI()).readText()
        val light = Light.load(json)
    }
}
