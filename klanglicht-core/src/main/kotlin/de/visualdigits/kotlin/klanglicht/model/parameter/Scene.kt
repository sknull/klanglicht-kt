package de.visualdigits.kotlin.klanglicht.model.parameter

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File

class Scene(
    val name: String,
    val parameterSet: Set<ParameterSet>
) {
    companion object {
        private val mapper = jacksonMapperBuilder().build()

        fun load(sceneFile: File): Scene {
            return mapper.readValue(sceneFile, Scene::class.java)
        }
    }
}
