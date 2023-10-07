package de.visualdigits.kotlin.klanglicht.dmx

import de.visualdigits.kotlin.klanglicht.model.color.RGBWColor
import de.visualdigits.kotlin.klanglicht.model.dmx.DmxRepeater
import de.visualdigits.kotlin.klanglicht.model.parameter.IntParameter
import de.visualdigits.kotlin.klanglicht.model.parameter.ParameterSet
import de.visualdigits.kotlin.klanglicht.model.parameter.Scene
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.ceil

@Disabled("for local testing only")
class SimpleTest {

    @Test
    fun testRgbw() {
        val preferences = Preferences.load(
            klanglichtDir = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
            preferencesFileName = "preferences_livingroom.json"
        )

        val baseChannel = 21

        val scene = Scene(
            name = "JUnit Test",
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = baseChannel,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 0),
                        RGBWColor(0, 0, 0, 0)
                    )
                )
            )
        )

        val dmxRepeater = DmxRepeater(preferences.dmxInterface, preferences.dmx.frameTime)
        dmxRepeater.start()

        preferences.setScene(scene)

        dmxRepeater.join()
    }

    @Test
    fun testFade() {
        val preferences = Preferences.load(
            klanglichtDir = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
            preferencesFileName = "preferences_livingroom.json"
        )

        val baseChannel = 21

        val scene1 = Scene(
            name = "JUnit Test",
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = baseChannel,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 255),
                        RGBWColor(255, 0, 0, 0)
                    )
                )
            )
        )

        val scene2 = Scene(
            name = "JUnit Test",
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = baseChannel,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 255),
                        RGBWColor(0, 255, 0, 0)
                    )
                )
            )
        )

        val dmxRepeater = DmxRepeater(preferences.dmxInterface, preferences.dmx.frameTime)
        dmxRepeater.start()

        val fadeDuration = 3000

        val dmxFrameTime = preferences.dmx.frameTime // millis
        val steps = ceil(fadeDuration.toDouble() / dmxFrameTime.toDouble()).toInt()
        val step = 1.0 / steps
        for (f in 0..steps) {
            preferences.setScene(scene1.fade(scene2, step * f))
            Thread.sleep(dmxFrameTime)
        }

        dmxRepeater.join()
    }
}
