package de.visualdigits.kotlin.klanglicht.model.preferences

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

@Disabled("for local testing only")
class ShellyDeviceTest {

    val preferences = Preferences.load(
        klanglichtDirectory = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
        preferencesFileName = "preferences_livingroom_dummy.json"
    )

    @Test
    fun testSetColor() {
        preferences.getShellyDevice("Starwars")?.setColor(RGBColor(0,0,255))
    }
}
