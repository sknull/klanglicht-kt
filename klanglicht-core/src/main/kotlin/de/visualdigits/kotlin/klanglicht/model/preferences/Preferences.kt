package de.visualdigits.kotlin.klanglicht.model.preferences

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.dmx.DMXInterface
import de.visualdigits.kotlin.klanglicht.dmx.DMXInterfaceType
import de.visualdigits.kotlin.klanglicht.dmx.DmxFrame
import de.visualdigits.kotlin.klanglicht.model.fixture.Channel
import de.visualdigits.kotlin.klanglicht.model.fixture.Fixtures
import de.visualdigits.kotlin.klanglicht.model.parameter.Scene
import de.visualdigits.kotlin.klanglicht.model.stage.Stage
import java.io.File
import java.nio.file.Paths


data class Preferences(
    val dividerPositions: List<Double> = listOf(),
    val dmxFrameTime: Int? = null,
    val dmxPort: String? = null,
    val dmxInterfaceType: DMXInterfaceType? = null,
    val fonts: List<Font> = listOf(),
    val lightmanagerUrl: String? = null,
    val pushEnabled: Boolean = false,
    val pushIn: String? = null,
    val pushOut: String? = null,
    val remoteUrl: String? = null,
    val stageParameters: StageParameters = StageParameters(),
    val stagePositions: Map<String, StagePosition> = mapOf(),
    val stageSetupName: String? = null,
    val uiLanguage: String? = null,
    val yamahaReceiverUrl: String? = null
) {
    companion object {
        private val mapper = jacksonMapperBuilder().build()

        private var preferences: Preferences? = null

        private var dmxInterface: DMXInterface? = null

        private val fixtures: MutableMap<Int, List<Channel>> = mutableMapOf()

        fun instance(): Preferences = preferences!!

        fun load(klanglichtDir: File): Preferences {
            if (preferences == null) {
                preferences = mapper.readValue(
                        Paths.get(klanglichtDir.canonicalPath, "preferences", "preferences.json").toFile(),
                        Preferences::class.java
                    )
                val fixtures = Fixtures.load(klanglichtDir)
                val stage = Stage.load(klanglichtDir, preferences?.stageSetupName!!)
                stage.fixtures.forEach { stageFixture ->
                    fixtures.getFixture(stageFixture.manufacturer, stageFixture.model)?.let {
                        f -> f.channelsForMode(stageFixture.mode)?.let {
                            c -> this.fixtures[stageFixture.baseChannel] = c
                        }
                    }
                }
                dmxInterface = DMXInterface.load(preferences?.dmxInterfaceType?:DMXInterfaceType.Dummy)
                dmxInterface?.open(preferences?.dmxPort!!)
            }
            return preferences!!
        }

        fun writeSceneData(sceneFile: File) {
            dmxInterface?.write(sceneData(sceneFile))
        }

        fun sceneData(sceneFile: File): DmxFrame {
            val dmxFrame = DmxFrame()
            Scene.load(sceneFile).parameterSet.forEach { parameterSet ->
                val bc = parameterSet.baseChannel
                val bytes = (fixtures[bc]?.map { channel ->
                        (parameterSet.parameters[channel.name]?:0).toByte()
                    }?:listOf())
                    .toByteArray()
                dmxFrame.set(bc, bytes)
            }
            return dmxFrame
        }
    }
}
