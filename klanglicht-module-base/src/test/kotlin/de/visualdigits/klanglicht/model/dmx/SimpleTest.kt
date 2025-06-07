package de.visualdigits.klanglicht.model.dmx

import de.visualdigits.klanglicht.model.dmx.parameter.DmxScene
import de.visualdigits.klanglicht.model.dmx.parameter.IntParameter
import de.visualdigits.klanglicht.model.dmx.parameter.ParameterSet
import de.visualdigits.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.twinkly.model.color.RGBWColor
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
            ),
            preferences?.dmx!!
        )

        dmxScene.write()
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
            ),
            preferences?.dmx!!
        )
        dmxScene0.write()
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
            ),
            preferences?.dmx!!
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
            ),
            preferences?.dmx!!
        )
        dmxScene1.write()

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
                    ),
                ),
            ),
            preferences?.dmx!!
        )

        dmxScene0.fade(dmxScene1, 1000L)
        dmxScene1.fade(dmxScene2, 2000L)
        Thread.sleep(2000)
        dmxScene2.fade(dmxScene1, 2000L)
        dmxScene1.fade(dmxScene0, 1000L)
    }
}
