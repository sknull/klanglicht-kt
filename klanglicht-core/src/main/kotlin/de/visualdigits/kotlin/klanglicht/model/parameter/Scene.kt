package de.visualdigits.kotlin.klanglicht.model.parameter

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.model.color.RGBAColor
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import java.io.File
import java.lang.IllegalArgumentException
import kotlin.math.ceil

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
                name = "Fram $factor",
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
        val steps = ceil(fadeDuration.toDouble() / dmxFrameTime.toDouble()).toInt()
        val step = 1.0 / steps
        for (f in 0..steps) {
            preferences.setScene(fade(other, step * f))
            Thread.sleep(dmxFrameTime)
        }
    }
}
