package de.visualdigits.kotlin.klanglicht.model.preferences

import de.visualdigits.kotlin.klanglicht.hardware.shelly.model.status.Light
import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.util.get
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URL

@Disabled("for local testing only")
class ShellyDeviceTest {

    val preferences = Preferences.load(
        klanglichtDirectory = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
        preferencesFileName = "preferences_livingroom_dummy.json"
    )

    @Test
    fun testSetColor() {
        val ipAddress = preferences.getShellyDevice("Starwars")?.ipAddress!!
        val color = RGBColor(0,0,255)
        URL("http://$ipAddress/color/0?turn=on&red=${color.red}&green=${color.green}&blue=${color.blue}&white=0&gain=100&transition=1&")
            .get<Light>()
    }
}
