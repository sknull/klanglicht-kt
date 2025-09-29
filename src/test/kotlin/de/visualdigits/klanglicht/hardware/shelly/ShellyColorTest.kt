package de.visualdigits.klanglicht.hardware.shelly

import de.visualdigits.klanglicht.hardware.shelly.model.ShellyColor
import de.visualdigits.klanglicht.configuration.model.Stage
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

@Disabled("for local testing only")
class ShellyColorTest {

    val stage = Stage.readValue(File(File(ClassLoader.getSystemResource(".klanglicht").toURI()), "preferences_livingroom_dummy.json"))

    @Test
    fun testTiming() {
        val shellyDevice = stage.devices?.shellyMap?.get("Starwars")!!
        val red = ShellyColor("Starwars", shellyDevice.ipAddress, RGBColor(255, 0, 0), 1.0, true)
        ShellyColor("Starwars", shellyDevice.ipAddress, RGBColor(0, 255, 0), 1.0, true)
        val t = System.currentTimeMillis()
        red.write(transitionDuration = 3000)
        val d = System.currentTimeMillis() - t
        println(d)
    }

    @Test
     fun testFade() {
        val shellyDevice = stage.devices?.shellyMap?.get("Starwars")
        if (shellyDevice != null) {
            val color1 = ShellyColor("foo", shellyDevice.ipAddress, RGBColor(255, 0, 0), 1.0, true)
            val color2 = ShellyColor("bar", shellyDevice.ipAddress, RGBColor(0, 255, 0), 1.0, true)
//            color2.write()

            (0 until 5).forEach { i ->
                color1.fade(color2, 2000L)
                Thread.sleep(2000)
                color2.fade(color1, 2000L)
                Thread.sleep(2000)
            }
        }
     }
}
