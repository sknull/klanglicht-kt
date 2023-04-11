package de.visualdigits.kotlin.klanglicht.dmx

import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import org.junit.jupiter.api.Test
import java.io.File

internal class DMXInterfaceTest {

    @Test
    fun testInterface() {
        Preferences.load(File(ClassLoader.getSystemResource(".klanglicht").toURI()))
        val data = Preferences.writeSceneData(File(ClassLoader.getSystemResource("parameterset/rgbw_red.json").toURI()))
        println(data)
    }
}
