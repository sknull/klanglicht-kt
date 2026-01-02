package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import org.w3c.dom.ls.LSInput
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

        fun readValue(json: String): LMScenes {
            return mapper.readValue(json, LMScenes::class.java)
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

    fun writeValueAsString(): String {
        val newScenes = convertBeforeSaving()
        return mapper.writeValueAsString(newScenes)
    }

    fun writeValue(file: File) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        val backupScenesJsonFile =  File(file.canonicalFile.parentFile, "${timestamp}_${file.name}")
        if (file.exists() && !file.renameTo(backupScenesJsonFile))  error("Could not rename scene file '${file.canonicalPath}' to '${backupScenesJsonFile.canonicalPath}'")
        val newScenes = convertBeforeSaving()
        mapper.writeValue(file, newScenes)
    }

    private fun convertBeforeSaving(): LMScenes {
        return LMScenes(
            name = name,
            groups = scenesGroupMap.values.map { sg -> LMSceneGroup(
                name = sg.name,
                displayName = sg.displayName,
                hasColorWheel = sg.hasColorWheel,
                colorWheelOddEven = sg.colorWheelOddEven,
                selectable = sg.selectable,
                scenes = sg.scenes.map { s ->
                    val actionHybrid = s.actions
                        .filterIsInstance<LMActionHybrid>()
                        .firstOrNull()
                    when (s.type) {
                        LMSceneType.gradient -> {
                            val action = actionHybrid
                                ?.let { a -> LMActionHybrid(
                                    ids = a.ids,
                                    hexColors = listOf(a.originalHexColors?.first()?:a.hexColors.first(), a.originalHexColors?.last()?:a.hexColors.last()),
                                    factor = actionHybrid.factor,
                                    gains = a.gains
                                ) }?:error("Invalid gradient")
                            LMScene(
                                name = s.name,
                                type = s.type,
                                color = listOf(),
                                steps = s.steps,
                                repeatable = if (s.repeatable == true) null else false,
                                condition = s.condition,
                                actions = listOf(action),
                                initialize = false
                            )
                        }
                        LMSceneType.standard -> {
                            val color = if (actionHybrid?.originalHexColors?.isNotEmpty() == true) {
                                if (s.color == actionHybrid.originalHexColors) {
                                    null
                                } else if (actionHybrid.factor != 1.0) {
                                    actionHybrid.hexColors = actionHybrid.originalHexColors?:listOf()
                                    null
                                } else {
                                    actionHybrid.originalHexColors!!
                                }
                            } else{
                                s.color
                            }
                            LMScene(
                                name = s.name,
                                type = s.type,
                                color = color,
                                steps = s.steps,
                                repeatable = if (s.repeatable == true) null else false,
                                condition = s.condition,
                                actions = s.actions,
                                initialize = false
                            )
                        }
                    }
                }.toMutableList()
            ) }.toMutableList())
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
