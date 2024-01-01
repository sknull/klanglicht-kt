package de.visualdigits.kotlin.klanglicht.dmx

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.parameter.IntParameter
import de.visualdigits.kotlin.klanglicht.model.parameter.ParameterSet
import de.visualdigits.kotlin.klanglicht.model.parameter.DmxScene
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.ceil

@Disabled("for local testing only")
class DmxInterfaceTest {

    private val preferences = Preferences.load(
        klanglichtDirectory = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
        preferencesFileName = System.getenv("preferencesFileName")?:"preferences_livingroom_dummy.json"
    )

    @Test
    fun testInterfaceFromModel1() {
        val dmxScene = DmxScene(
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
        runBlocking { dmxScene.write(preferences) }
        preferences.writeDmxData()
    }

    @Test
    fun testBlackout() {
        val dmxScene = DmxScene(
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
        runBlocking { dmxScene.write(preferences) }
        preferences.writeDmxData()
    }

//    @Test
//    fun testMovinghead() {
//
//        val dmxRepeater = DmxRepeater.instance(prefs.dmxInterface, prefs.dmx.frameTime)
//        dmxRepeater.start()
//
//        testReset()
//
//        Thread.sleep(2000)
//
//        fade(stage)
//
//        dmxRepeater.join()
//    }
//
//    @Test
//    fun testReset() {
//        val scene = Scene(
//            name = "test",
//            parameterSet = listOf(
//                ParameterSet(
//                    baseChannel = 35,
//                    parameters = mutableListOf(
//                        IntParameter("Speed", 0),
//                        IntParameter("Pan", 0),
//                        IntParameter("PanFine", 0),
//                        IntParameter("Tilt", 0),
//                        IntParameter("TiltFine", 0),
//                        RGBWColor()
//                    )
//                )
//            )
//        )
//        prefs.setScene(scene)
//    }
//
//    @Test
//    fun testRotation() {
//        val stage = Stage.load(prefs.klanglichtDir, "home-lab-movinghead")
//        val stageFixture = stage.fixtures.first()
//        val fixture = stageFixture.fixture!!
//
//        println(fixture.panoParameterSet(0.0).parameters.map { it.parameterMap().toString() })
//        println(fixture.panoParameterSet(90.0).parameters.map { it.parameterMap().toString() })
//        println(fixture.panoParameterSet(180.0).parameters.map { it.parameterMap().toString() })
//        println(fixture.panoParameterSet(360.0).parameters.map { it.parameterMap().toString() })
//        println(fixture.panoParameterSet(540.0).parameters.map { it.parameterMap().toString() })
//
//        println(fixture.tiltParameterSet(0.0).parameters.map { it.parameterMap().toString() })
//        println(fixture.tiltParameterSet(90.0).parameters.map { it.parameterMap().toString() })
//        println(fixture.tiltParameterSet(180.0).parameters.map { it.parameterMap().toString() })
//    }
//
//    private fun fade(
//        stage: Stage,
//    ) {
//        val stageFixture = stage.fixtures.first()
//        val baseChannel = stageFixture.baseChannel
//        val fixture = stageFixture.fixture!!
//
//        val fadeDuration = 3000
//
//        val parameterSet1 = ParameterSet(
//            baseChannel = baseChannel,
//            parameters = mutableListOf(
//                RGBWColor(128, 0, 0, 0),
//                IntParameter("Speed", 0),
//                RotationParameter(fixture, 0.0, 0.0)
//            )
//        )
//
//        val parameterSet2 = ParameterSet(
//            baseChannel = baseChannel,
//            parameters = mutableListOf(
//                RGBWColor(0, 128, 0, 0),
//                IntParameter("Speed", 0),
//                RotationParameter(fixture, 80.0, 60.0)
//            )
//        )
//
//        fade(fadeDuration, parameterSet1, parameterSet2)
//    }

    private fun fade(
        fadeDuration: Int,
        parameterSet1: ParameterSet,
        parameterSet2: ParameterSet
    ) {
        val dmxFrameTime = preferences.getDmxFrameTime()
        val steps = ceil(fadeDuration.toDouble() / dmxFrameTime.toDouble()).toInt()
        val step = 1.0 / steps
        for (f in 0..steps) {
            val factor = step * f
            val frame = parameterSet1.fade(parameterSet2, factor)
            val dmxScene = DmxScene(
                name = "frame_$f",
                parameterSet = listOf(
                    frame
                )
            )
            runBlocking { dmxScene.write(preferences) }
            Thread.sleep(dmxFrameTime)
        }
    }
}
