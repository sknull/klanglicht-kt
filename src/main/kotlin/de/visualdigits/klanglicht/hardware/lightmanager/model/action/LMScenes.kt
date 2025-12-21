package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File
import java.util.Locale

/**
 * Scenes description beans used within the application preferences.
 */
@JsonIgnoreProperties("scenesMap", "scenesGroupMap")
class LMScenes(
    val name: String? = null,
    val groups: MutableList<LMSceneGroup> = mutableListOf()
) {

    val scenesGroupMap: LinkedHashMap<String, LMSceneGroup> = LinkedHashMap()
    val scenesMap: LinkedHashMap<String, LinkedHashMap<String, LMScene>> = LinkedHashMap()

    companion object {
        private val mapper = jacksonMapperBuilder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, JsonInclude.Include.NON_EMPTY))
            .build()

        fun readValue(file: File): LMScenes {
            return mapper.readValue(file, LMScenes::class.java)
        }
    }

    init {
        refreshSceneMap()
    }

    fun refreshSceneMap() {
        groups.forEach { sceneGroup ->
            scenesGroupMap[sceneGroup.name] = sceneGroup
            val sceneGroupMap = scenesMap.computeIfAbsent(sceneGroup.name) { LinkedHashMap() }
            sceneGroup.scenes.forEach { scene ->
                sceneGroupMap[scene.name] = scene
            }
        }
    }

    override fun toString(): String {
        return "$name\n" + groups.joinToString("\n") { scene -> "  ${scene.name}\n    ${scene.scenes.joinToString("\n    ")}" }
    }

    fun writeValue(file: File) {
        mapper.writeValue(file, this)
    }

    fun selectableGroupNames(): List<String> = groups.filter { s -> s.selectable }.map { s -> s.name }

    fun add(lmScene: LMScene) {
        var sceneName = lmScene.name
        var groupName = "common"
        val attributes = LMNamedAttributes(lmScene.name, "group", "color")
        val color = if (attributes.matched) {
            if (attributes.name.isNotEmpty()) {
                sceneName = attributes.name
            }
            val g = attributes["group"]
            if (g.isNotEmpty()) {
                groupName = g
            }
            attributes["color"].split(",").map { it.trim() }
        } else listOf()
        val scene = LMScene(sceneName, color = color, condition = lmScene.condition, actions = lmScene.actions)
        scenesMap[groupName]?.set(scene.name, scene)
        if ("hidden" != groupName) {
            groupName
                .replaceFirstChar { fc -> if (fc.isLowerCase()) fc.titlecase(Locale.getDefault()) else fc.toString() }
                .let { name -> Pair(name, scenesGroupMap[name]?:LMSceneGroup(name)) }
                .also { (name, group) ->
                    scenesGroupMap[name] = group
                    group.scenes.add(scene)
                }
        }
    }
}
