package de.visualdigits.kotlin.klanglicht.dmx

import de.visualdigits.kotlin.klanglicht.model.color.RGBWColor
import de.visualdigits.kotlin.klanglicht.model.parameter.IntParameter
import de.visualdigits.kotlin.klanglicht.model.parameter.ParameterSet
import de.visualdigits.kotlin.klanglicht.model.parameter.DmxScene
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

@Disabled("for local testing only")
class SimpleTest {

    val preferences = Preferences.load(
        klanglichtDirectory = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
        preferencesFileName = "preferences_minimal.json"
    )

    @Test
    fun testRgbw() {
        val dmxScene = DmxScene(
            name = "JUnit Test",
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = 15,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 255),
                        RGBWColor(20, 20, 0, 0)
                    )
                ),
                ParameterSet(
                    baseChannel = 29,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 255),
                        RGBWColor(0, 20, 20, 0)
                    )
                )
            )
        )

        dmxScene.write(preferences)
        dmxScene.write(preferences)
    }

    @Test
    fun testPowerOff() {
        val dmxScene0 = DmxScene(
            name = "JUnit Test",
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = 15,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 0),
                        RGBWColor(0, 0, 0, 0)
                    )
                ),
                ParameterSet(
                    baseChannel = 29,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 0),
                        RGBWColor(0, 0, 0, 0)
                    )
                ),
            )
        )
        dmxScene0.write(preferences)
    }

    @Test
    fun testFade() {
        val dmxScene0 = DmxScene(
            name = "JUnit Test",
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = 15,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 0),
                        RGBWColor(0, 0, 0, 0)
                    )
                ),
                ParameterSet(
                    baseChannel = 29,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 0),
                        RGBWColor(0, 0, 0, 0)
                    )
                ),
            )
        )

        val dmxScene1 = DmxScene(
            name = "JUnit Test",
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = 29,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 255),
                        RGBWColor(0, 255, 0, 0)
                    )
                ),
                ParameterSet(
                    baseChannel = 15,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 255),
                        RGBWColor(255, 0, 0, 0)
                    )
                ),
            )
        )
        dmxScene1.write(preferences)

        val dmxScene2 = DmxScene(
            name = "JUnit Test",
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = 15,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 255),
                        RGBWColor(0, 255, 0, 0)
                    )
                ),
                ParameterSet(
                    baseChannel = 29,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 255),
                        RGBWColor(255, 0, 0, 0)
                    )
                ),
            )
        )

        dmxScene0.fade(dmxScene1, 1000, preferences)
        dmxScene1.fade(dmxScene2, 2000, preferences)
        Thread.sleep(2000)
        dmxScene2.fade(dmxScene1, 2000, preferences)
        dmxScene1.fade(dmxScene0, 1000, preferences)
    }
}
