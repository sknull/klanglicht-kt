package de.visualdigits.kotlin.klanglicht.hardware.shelly

import de.visualdigits.kotlin.klanglicht.hardware.shelly.model.ShellyColor
import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

@Disabled("for local testing only")
class ShellyColorTest {

    val preferences = Preferences.load(
        klanglichtDirectory = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
        preferencesFileName = "preferences_livingroom_dummy.json"
    )

    @Test
    fun testTiming() {
        val shellyDevice = preferences.getShellyDevice("Starwars")!!
        val red = ShellyColor("Starwars", shellyDevice.ipAddress, RGBColor(255, 0, 0), 1.0, true)
        val green = ShellyColor("Starwars", shellyDevice.ipAddress, RGBColor(0, 255, 0), 1.0, true)
        val t = System.currentTimeMillis()
        red.write(preferences, transitionDuration = 3000)
        val d = System.currentTimeMillis() - t
        println(d)
    }

    @Test
     fun testFade() {
        val shellyDevice = preferences.getShellyDevice("Starwars")
        if (shellyDevice != null) {
            val color1 = ShellyColor("foo", shellyDevice.ipAddress, RGBColor(255, 0, 0), 1.0, true)
            val color2 = ShellyColor("bar", shellyDevice.ipAddress, RGBColor(0, 255, 0), 1.0, true)
//            color2.write()

            for (i in 0 until 5) {
                color1.fade(color2, 2000, preferences)
                Thread.sleep(2000)
                color2.fade(color1, 2000, preferences)
                Thread.sleep(2000)
            }
        }
     }
}
