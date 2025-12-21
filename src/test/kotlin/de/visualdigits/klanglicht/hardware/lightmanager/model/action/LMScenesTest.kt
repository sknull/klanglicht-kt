package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import de.visualdigits.kotlin.twinkly.model.color.BlendMode
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import org.junit.jupiter.api.Test
import java.io.File

class LMScenesTest {

    @Test
    fun testReadScenes() {
        val scenes = LMScenes.readValue(File(ClassLoader.getSystemResource(".klanglicht/resources/scenes.json").toURI()))
        println(scenes)
    }

    @Test
    fun convert() {
        val scenes = LMScenes.readValue(File(ClassLoader.getSystemResource("scratch_32.json").toURI()))

        val newScenes = LMScenes(
            name = scenes.name,
            groups = scenes.scenesGroupMap.values.map { sg -> LMSceneGroup(
                name = sg.name,
                displayName = sg.displayName,
                hasColorWheel = sg.hasColorWheel,
                colorWheelOddEven = sg.colorWheelOddEven,
                selectable = sg.selectable,
                scenes = sg.scenes.map { s ->
                    var actionHybrid = s.actions
                        .filterIsInstance<LMActionHybrid>()
                        .firstOrNull()
                    when (s.type) {
                        LMSceneType.gradient -> {
                            val action = actionHybrid
                                ?.let { a -> LMActionHybrid(
                                    ids = a.ids,
                                    hexColors = listOf(a.originalHexColors?.first()?:a.hexColors.first(), a.originalHexColors?.last()?:a.hexColors.last()),
//                                    factor = s.factor,
                                    gains = a.gains
                                ) }?:error("Invalid gradient")
                            LMScene(
                                name = s.name,
                                type = s.type,
                                color = listOf(),
                                steps = s.steps,
                                repeatable = s.repeatable,
                                condition = s.condition,
                                actions = listOf(action),
                                initialize = false
                            )
                        }
                        LMSceneType.sequence -> {
                            LMScene(
                                name = s.name,
                                type = s.type,
                                color = s.color,
                                steps = s.steps,
                                repeatable = s.repeatable,
                                condition = s.condition,
                                actions = s.actions,
                                initialize = false
                            )
                        }
                        else -> {
                            val color = if (actionHybrid?.originalHexColors != null) {
                                actionHybrid = LMActionHybrid(
                                    ids = actionHybrid.ids,
                                    hexColors = actionHybrid.originalHexColors ?: error("No original hex colors"),
//                                    factor = s.factor,
                                )
                                listOf()
                            } else if (s.color != (actionHybrid?.hexColors ?: listOf<String>())) {
                                s.color
                            } else listOf()
                            LMScene(
                                name = s.name,
                                type = s.type,
                                color = color,
                                steps = s.steps,
                                repeatable = s.repeatable,
                                condition = s.condition,
                                actions = actionHybrid?.let { a -> listOf(a) } ?: listOf(),
                                initialize = false
                            )
                        }
                    }
                }.toMutableList()
            ) }.toMutableList())
        val targetFile = File("E:/Programmierung/IntelliJ/klanglicht-kt/src/test/resources/scenes.json")
        newScenes.writeValue(targetFile)
    }

    @Test
    fun testGradient() {
        val red = RGBColor("ff0000")
        val green = RGBColor("00ff00")

        val step = 1.0 / 6.0
        (0 ..  6).forEach { f ->
            val faded = red.fade(green, f * step, BlendMode.AVERAGE)
            println(faded.hex())
        }
    }
}
