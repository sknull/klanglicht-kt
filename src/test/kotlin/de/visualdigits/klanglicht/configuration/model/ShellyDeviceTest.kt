package de.visualdigits.klanglicht.configuration.model

import de.visualdigits.klanglicht.hardware.shelly.model.status.Light
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import de.visualdigits.kotlin.util.get
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URL

@Disabled("for local testing only")
class ShellyDeviceTest {

    val stage = Stage.readValue(File(File(ClassLoader.getSystemResource(".klanglicht").toURI()), "preferences_livingroom_dummy.json"))

    @Test
    fun testSetColor() {
        val ipAddress = stage.devices?.shellyMap?.get("Starwars")?.ipAddress!!
        val color = RGBColor(0,0,255)
        URL("http://$ipAddress/color/0?turn=on&red=${color.red}&green=${color.green}&blue=${color.blue}&white=0&gain=100&transition=1&")
            .get<Light>(clazz = Light::class.java)
    }
}
