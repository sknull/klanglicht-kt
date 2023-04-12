package de.visualdigits.kotlin.klanglicht.dmx

import de.visualdigits.kotlin.klanglicht.model.color.ColorParameter
import de.visualdigits.kotlin.klanglicht.model.color.NamedParameter
import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.parameter.ParameterSet
import de.visualdigits.kotlin.klanglicht.model.parameter.Scene
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.ceil
import kotlin.math.min

internal class DMXInterfaceTest {

    private val prefs = Preferences.instance(
        klanglichtDir = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
        preferencesFileName = System.getenv("preferencesFileName")?:"preferences.json"
    )

    @Test
    fun testInterfaceFromPreparedFile() {
        prefs.setSceneFile(File(ClassLoader.getSystemResource("parameterset/rgbw_red.json").toURI()))
        prefs.write()
    }

    @Test
    fun testInterfaceFromModel1() {
        val scene = Scene(
            name = "test",
            parameterSet = setOf(
                ParameterSet(
                    baseChannel = 15,
                    parameters = mutableMapOf(
                        "MasterDimmer" to 255,
                        "Stroboscope" to 0,
                        "Red" to 255,
                        "Green" to 0,
                        "Blue" to 0
                    )
                )
            )
        )
        prefs.setScene(scene)
        prefs.write()
    }

    @Test
    fun testBlackout() {
        val scene = Scene(
            name = "test",
            parameterSet = setOf(
                ParameterSet(
                    baseChannel = 15,
                    parameters = mutableMapOf(
                        "MasterDimmer" to 0,
                        "Stroboscope" to 0,
                        "Red" to 0,
                        "Green" to 0,
                        "Blue" to 0
                    )
                )
            )
        )
        prefs.setScene(scene)
        prefs.write()
    }

    @Test
    fun testInterfaceFromModel2() {
        val color1 = RGBColor(
            red = 255,
            green = 0,
            blue = 0
        )
        val color2 = RGBColor(
            red = 0,
            green = 255,
            blue = 0
        )
        val fadeDuration = 2000 // millis

        val dmxFrameTime = prefs.dmxFrameTime!! // millis
        val steps = ceil(fadeDuration.toDouble() / dmxFrameTime.toDouble()).toInt()
        val start = System.currentTimeMillis()
        val step = 1.0 / steps
        for (f in 0..steps + 1) {
            val factor = step * f
            val color = color1.mix<ColorParameter>(color2, min(1.0, factor))
            val scene = Scene(
                name = "test",
                parameterSet = setOf(
                    ParameterSet(
                        baseChannel = 15,
                        parameterObjects = setOf(
                            NamedParameter("MasterDimmer", 255),
                            NamedParameter("Stroboscope", 0),
                            color
                        )
                    )
                )
            )
            prefs.setScene(scene)
            prefs.write()
        }
        val stop = System.currentTimeMillis()

        println("### durations: ${stop - start} millis")
    }

}
