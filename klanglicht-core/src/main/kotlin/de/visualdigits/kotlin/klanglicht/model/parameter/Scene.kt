package de.visualdigits.kotlin.klanglicht.model.parameter

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import java.io.File

class Scene(
    val name: String,
    val parameterSet: List<ParameterSet>
) : Fadeable<Scene> {

    companion object {
        private val mapper = jacksonMapperBuilder().build()

        fun load(sceneFile: File): Scene {
            return mapper.readValue(sceneFile, Scene::class.java)
        }
    }

    /**
     * Writes the given scene into the internal dmx frame of the interface.
     */
    fun write(preferences: Preferences, write: Boolean = true) {
        parameterSet
            .sortedBy { it.baseChannel }
            .forEach { parameterSet ->
                val baseChannel = parameterSet.baseChannel
                val bytes = (preferences.fixtures[baseChannel]?.map { channel ->
                    (parameterSet.parameterMap[channel.name] ?: 0).toByte()
                } ?: listOf())
                    .toByteArray()
                preferences.dmxInterface.dmxFrame.set(baseChannel, bytes)
                if (write) preferences.dmxInterface.write()
            }
    }

    fun fade(
        other: Scene,
        fadeDuration: Long,
        preferences: Preferences
    ) {
        val dmxFrameTime = preferences.dmx.frameTime // millis
        val step = 1.0 / fadeDuration.toDouble() * dmxFrameTime.toDouble()
        var factor = 0.0

        while (factor < 1.0) {
            fade(other, factor).write(preferences)
            factor += step
            Thread.sleep(dmxFrameTime)
        }
        other.write(preferences)
    }

    override fun fade(
        other: Any,
        factor: Double
    ): Scene {
        return if (other is Scene) {
            Scene(
                name = "Frame $factor",
                parameterSet = parameterSet
                    .sortedBy { it.baseChannel }
                    .zip(other.parameterSet.sortedBy { it.baseChannel })
                    .map { (paramsFrom, paramsTo) ->
                        paramsFrom.fade(paramsTo, factor)
                    }
            )
        }
        else throw IllegalArgumentException("Cannot not fade another type")
    }
}
