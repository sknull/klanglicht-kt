package de.visualdigits.klanglicht.hardware.dmx.model

import de.visualdigits.klanglicht.hardware.dmx.model.parameter.DmxScene
import de.visualdigits.klanglicht.hardware.dmx.model.parameter.IntParameter
import de.visualdigits.klanglicht.hardware.dmx.model.parameter.ParameterSet
import de.visualdigits.klanglicht.configuration.model.Stage
import de.visualdigits.kotlin.twinkly.model.color.RGBWColor
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

@Disabled("for local testing only")
class SimpleTest {

    val stage = Stage.readValue(File(File(ClassLoader.getSystemResource(".klanglicht").toURI()), "preferences_minimal.json"))

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
            stage.devices?.dmx!!
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
            stage.devices?.dmx!!
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
            stage.devices?.dmx!!
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
            stage.devices?.dmx!!
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
            stage.devices?.dmx!!
        )

        dmxScene0.fade(dmxScene1, 1000L)
        dmxScene1.fade(dmxScene2, 2000L)
        Thread.sleep(2000)
        dmxScene2.fade(dmxScene1, 2000L)
        dmxScene1.fade(dmxScene0, 1000L)
    }
}
