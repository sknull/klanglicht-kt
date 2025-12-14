package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File
import java.util.Locale

/**
 * Scenes description beans used within the application preferences.
 */
@JsonIgnoreProperties("scenesMap")
class LMScenes(
    val name: String? = null,
    val scenes: List<LMSceneGroup> = listOf()
) {

    val scenesGroupMap: LinkedHashMap<String, LMSceneGroup> = LinkedHashMap()
    val scenesMap: LinkedHashMap<String, LinkedHashMap<String, LMScene>> = LinkedHashMap()

    companion object {
        private val mapper = jacksonMapperBuilder().enable(SerializationFeature.INDENT_OUTPUT).build()

        fun readValue(file: File): LMScenes {
            val lmScenes = mapper.readValue(file, LMScenes::class.java)
            lmScenes.refreshSceneMap()
            return lmScenes
        }
    }

    init {
        refreshSceneMap()
    }

    fun refreshSceneMap() {
        scenesMap.clear()
        scenes.forEach { sceneGroup ->
            scenesGroupMap[sceneGroup.name] = sceneGroup
            val sceneGroupMap = scenesMap.computeIfAbsent(sceneGroup.name) { LinkedHashMap() }
            sceneGroup.scenes.forEach { scene ->
                scene.groupName = sceneGroup.name
                sceneGroupMap[scene.name] = scene
            }
        }
    }

    override fun toString(): String {
        return "$name\n" + scenes.joinToString("\n") { scene -> "  ${scene.name}\n    ${scene.scenes.joinToString("\n    ")}" }
    }

    fun writeValue(file: File) {
        mapper.writeValue(file, this)
    }

    fun selectableGroupNames(): List<String> = scenes.filter { s -> s.selectable }.map { s -> s.name }

    fun add(lmScene: LMScene) {
        var sceneName = lmScene.name
        var groupName: String = "common"
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
        val scene = LMScene(sceneName, groupName, color, LMSceneType.custom, 0, false, lmScene.condition, lmScene.actions)
        scenesMap[groupName]?.set(scene.name, scene)
        if ("hidden" != groupName) {
            groupName
                ?.replaceFirstChar { fc -> if (fc.isLowerCase()) fc.titlecase(Locale.getDefault()) else fc.toString() }
                ?.let { name -> Pair(name, scenesGroupMap[name]?:LMSceneGroup(name)) }
                ?.also { (name, group) ->
                    scenesGroupMap[name] = group
                    group.scenes.add(scene)
                }
        }
    }
}
