package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File
import java.util.Locale

class LMScenes(
    val name: String? = null
) {

    val scenes: MutableMap<String, MutableList<LMScene>> = mutableMapOf()

    val scenesMap: MutableMap<String, LMScene> = mutableMapOf()

    companion object {
        private val mapper = jacksonMapperBuilder()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build()

        fun unmarshall(file: File): LMScenes {
            val lmScenes = mapper.readValue(file, LMScenes::class.java)
            lmScenes.scenes.values.forEach { l -> l.forEach { s -> lmScenes.scenesMap[s.name!!] = s }  }
            return lmScenes
        }
    }

    override fun toString(): String {
        return "$name\n" + scenes.toMap().map { e -> "  ${e.key}\n    ${e.value.joinToString("\n    ")}" }.joinToString("\n")
    }

    fun add(scene: LMScene) {
        scenesMap[scene.name!!] = scene
        var group: String? = "common"
        val attributes = LMNamedAttributes(scene.name, "group", "color")
        if (attributes.matched()) {
            val name = attributes.name
            if (name?.isNotEmpty() == true) {
                scene.name = name
            }
            val g = attributes["group"]
            if (g.isNotEmpty()) {
                group = g
            }
            scene.color = attributes["color"]
        }
        if ("hidden" != group) {
            group
                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                ?.let { name ->
                    var s = scenes[name]
                    if (s == null) {
                        s = mutableListOf()
                        scenes[name] = s
                    }
                    s.add(scene)
                }
        }
    }
}
