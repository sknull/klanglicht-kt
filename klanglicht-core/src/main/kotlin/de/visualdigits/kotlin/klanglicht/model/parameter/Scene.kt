package de.visualdigits.kotlin.klanglicht.model.parameter

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import java.io.File
import java.lang.IllegalArgumentException

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

    override fun fade(
        other: Any,
        factor: Double
    ): Scene {
        return if (other is Scene) {
            Scene(
                name = "Frame $factor",
                parameterSet = parameterSet
                    .zip(other.parameterSet)
                    .map { (paramsFrom, paramsTo) ->
                        paramsFrom.fade(paramsTo, factor)
                    }
            )
        } else throw IllegalArgumentException("Cannot not fade another type")
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
            preferences.setScene(fade(other, factor))
            factor += step
            Thread.sleep(dmxFrameTime)
        }
        preferences.setScene(other)
    }
}
