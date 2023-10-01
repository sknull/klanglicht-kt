package de.visualdigits.kotlin.klanglicht.dmx

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.color.RGBWColor
import de.visualdigits.kotlin.klanglicht.model.parameter.IntParameter
import de.visualdigits.kotlin.klanglicht.model.parameter.ParameterSet
import de.visualdigits.kotlin.klanglicht.model.parameter.RotationParameter
import de.visualdigits.kotlin.klanglicht.model.parameter.Scene
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.klanglicht.model.stage.Stage
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
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = 15,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 255),
                        IntParameter("Stroboscope", 0),
                        RGBColor(255, 0, 0)
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
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = 15,
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 0),
                        IntParameter("Stroboscope", 0),
                        RGBColor()
                    )
                )
            )
        )
        prefs.setScene(scene)
        prefs.write()
    }

    @Test
    fun testMovinghead() {
        val stage = Stage.load(prefs.klanglichtDir!!, "home-lab-movinghead")

        val repeater = Repeater(prefs)
        repeater.start()

        testReset()

        Thread.sleep(2000)

        fade(stage)

        repeater.join()
    }

    @Test
    fun testReset() {
        val scene = Scene(
            name = "test",
            parameterSet = listOf(
                ParameterSet(
                    baseChannel = 35,
                    parameters = mutableListOf(
                        IntParameter("Speed", 0),
                        IntParameter("Pan", 0),
                        IntParameter("PanFine", 0),
                        IntParameter("Tilt", 0),
                        IntParameter("TiltFine", 0),
                        RGBWColor()
                    )
                )
            )
        )
        prefs.setScene(scene)
    }

    @Test
    fun testRotation() {
        val stage = Stage.load(prefs.klanglichtDir!!, "home-lab-movinghead")
        val stageFixture = stage.fixtures.first()
        val fixture = stageFixture.fixture!!

        println(fixture.panoParameterSet(0.0).parameters.map { it.parameterMap().toString() })
        println(fixture.panoParameterSet(90.0).parameters.map { it.parameterMap().toString() })
        println(fixture.panoParameterSet(180.0).parameters.map { it.parameterMap().toString() })
        println(fixture.panoParameterSet(360.0).parameters.map { it.parameterMap().toString() })
        println(fixture.panoParameterSet(540.0).parameters.map { it.parameterMap().toString() })

        println(fixture.tiltParameterSet(0.0).parameters.map { it.parameterMap().toString() })
        println(fixture.tiltParameterSet(90.0).parameters.map { it.parameterMap().toString() })
        println(fixture.tiltParameterSet(180.0).parameters.map { it.parameterMap().toString() })
    }

    private fun fade(
        stage: Stage,
    ) {
        val stageFixture = stage.fixtures.first()
        val baseChannel = stageFixture.baseChannel
        val fixture = stageFixture.fixture!!

        val fadeDuration = 3000

        val parameterSet1 = ParameterSet(
            baseChannel = baseChannel,
            parameters = mutableListOf(
                RGBWColor(128, 0, 0, 0),
                IntParameter("Speed", 0),
                RotationParameter(fixture, 0.0, 0.0)
            )
        )

        val parameterSet2 = ParameterSet(
            baseChannel = baseChannel,
            parameters = mutableListOf(
                RGBWColor(0, 128, 0, 0),
                IntParameter("Speed", 0),
                RotationParameter(fixture, 80.0, 60.0)
            )
        )

        val dmxFrameTime = prefs.dmxFrameTime!! // millis
        val steps = ceil(fadeDuration.toDouble() / dmxFrameTime.toDouble()).toInt()
        val step = 1.0 / steps
        for (f in 0..steps) {
            val factor = step * f
            val frame = parameterSet1.fade(parameterSet2, factor)
//            println(frame.parameters.map { it.parameterMap().toString() })
            val scene = Scene(
                name = "frame_$f",
                parameterSet = listOf(
                    frame
                )
            )
            prefs.setScene(scene)
            Thread.sleep(dmxFrameTime)
//            prefs.write()
        }
    }

}
