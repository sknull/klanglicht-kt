package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import de.visualdigits.kotlin.twinkly.model.color.BlendMode
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import org.junit.jupiter.api.Test
import java.io.File

class LMScenesTest {

    @Test
    fun testReadScenes() {
        val scenes = LMScenes.readValue(File(ClassLoader.getSystemResource(".klanglicht/resources/scenes.json").toURI()))
        println(scenes)
    }

    @Test
    fun testGradient() {
        val red = RGBColor("ff0000")
        val green = RGBColor("00ff00")

        val step = 1.0 / 6.0
        (0 ..  6).forEach { f ->
            val faded = red.fade(green, f * step, BlendMode.AVERAGE)
            println(faded.hex())
        }
    }
}
