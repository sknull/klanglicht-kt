package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File
import java.util.Locale

@JsonIgnoreProperties("scenesMap")
class LMScenes(
    val name: String? = null,
    val scenes: LinkedHashMap<String, LMSceneGroup> = LinkedHashMap()
) {

    val scenesMap: LinkedHashMap<String, LMScene> = LinkedHashMap()

    companion object {
        private val mapper = jacksonMapperBuilder()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build()

        fun unmarshall(file: File): LMScenes {
            val lmScenes = mapper.readValue(file, LMScenes::class.java)
            lmScenes.scenes.values.forEach { g -> g.scenes.forEach { s -> lmScenes.scenesMap[s.name!!] = s }  }
            return lmScenes
        }
    }

    override fun toString(): String {
        return "$name\n" + scenes.toMap().map { e -> "  ${e.key}\n    ${e.value.scenes.joinToString("\n    ")}" }.joinToString("\n")
    }
}
