package de.visualdigits.klanglicht.configuration

import de.visualdigits.klanglicht.configuration.model.Stage
import de.visualdigits.klanglicht.hardware.hybrid.model.HybridScene
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionHybrid
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionTwinkly
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMScene
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMSceneGroup
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMSceneType
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMScenes
import de.visualdigits.kotlin.twinkly.model.device.xmusic.moods.Moods
import de.visualdigits.kotlin.twinkly.model.parameter.Fadeable
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
@ConfigurationProperties(prefix = "application")
@ConfigurationPropertiesScan
class ApplicationPreferences() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Value("\${klanglicht.baseUrl}")
    lateinit var baseUrl: String

    @Value("\${server.port}")
    var port: Int = 0

    @Value("\${klanglicht.theme}")
    lateinit var theme: String

    var stage: Stage? = null

    val klanglichtDirectory: File = File(System.getProperty("user.home"), ".klanglicht")

    var currentSceneName = "All Black"
    var currentScene: HybridScene? = null
    val colorStore: MutableMap<String, String> = mutableMapOf()

    @PostConstruct
    fun initialize() {
        log.info("")
        log.info("#### setUp - start")
        log.info("##")
        log.info("## klanglichtDirectory: " + klanglichtDirectory.canonicalPath)

        stage = Stage.readValue(Paths.get(klanglichtDirectory.canonicalPath, "resources", "stage.json").toFile())
        stage?.devices?.dmx?.initialize(klanglichtDirectory)

        currentScene = stage?.initialHybridScene()
        currentScene?.write(true, 1000)

        log.info("## baseUrl            : $baseUrl")
        log.info("## port               : $port")
        log.info("## theme              : $theme")
        log.info("## stage              : ${stage?.devices?.stage?.joinToString(", ") { d -> "${d.id} [${d.type}]" }}" )
        log.info("## shelly             : ${stage?.devices?.shelly?.joinToString(", ") { s -> "${s.name} [${s.model}] ${s.ipAddress}" }}" )
        log.info("## twinkly            : ${stage?.devices?.twinkly?.joinToString(", ") { t -> "${t.name} [${t.deviceOrigin}]" }}" )
        log.info("## colorWheels        : ${stage?.devices?.colorWheels?.joinToString(", ") { c -> "${c.id} [${c.devices.joinToString(",")}]" }}" )
        log.info("##")
        log.info("#### setUp - end")
        log.info("")
    }

    fun loadScenes(): LMScenes {
        val scenes = LMScenes.readValue(Paths.get(klanglichtDirectory.canonicalPath, "resources", "scenes.json").toFile())
        if (stage?.devices?.discoveredDevices?.isNotEmpty() == true) {
            val moodScenes = mutableListOf<LMScene>()
            moodScenes.add(
                LMScene(
                    name = "On",
                    color = listOf("#ffffff"),
                    type = LMSceneType.standard,
                    initialize = false,
                    actions = listOf(LMActionTwinkly("on"))
                )
            )
            moodScenes.add(
                LMScene(
                    name = "Off",
                    color = listOf("#000000"),
                    type = LMSceneType.standard,
                    initialize = false,
                    actions = listOf(LMActionTwinkly("off"))
                )
            )
            moodScenes.add(
                LMScene(
                    name = "Music On",
                    color = listOf("#ffffff"),
                    type = LMSceneType.standard,
                    initialize = false,
                    actions = listOf(LMActionTwinkly("musicOn"))
                )
            )
            moodScenes.add(
                LMScene(
                    name = "Music Off",
                    color = listOf("#000000"),
                    type = LMSceneType.standard,
                    initialize = false,
                    actions = listOf(LMActionTwinkly("musicOff"))
                )
            )
            moodScenes.addAll(
                Moods.entries.flatMap { mood ->
                    mood.effects.values.map { effect ->
                        LMScene(
                            name = "${mood.icon} ${mood.label} ${effect.label}",
                            color = listOf(mood.color),
                            type = LMSceneType.standard,
                            initialize = false,
                            actions = listOf(
                                LMActionTwinkly(
                                    command = "moodsEffect",
                                    moodsIndex = mood.index,
                                    effectIndex = effect.index
                                )
                            )
                        )
                    }
                }
            )
            scenes.groups.add(
                LMSceneGroup(
                    name = "TwinklyMusic",
                    displayName = "Twinkly Music",
                    hasColorWheel = false,
                    colorWheelOddEven = false,
                    selectable = false,
                    scenes = moodScenes
                )
            )
            createReferences(scenes)
            scenes.refreshSceneMap()
        }

        return scenes
    }

    private fun createReferences(scenes: LMScenes) {
        scenes.groups.forEach { sceneGroup ->
            sceneGroup.scenes.forEach { scene ->
                scene.group = sceneGroup
                scene.actions.forEach { action ->
                    action.scene = scene
                }
            }
        }
    }

    fun writeScenes(scenes: LMScenes) {
        val scenesJsonFile = Paths.get(klanglichtDirectory.canonicalPath, "resources", "scenes.json").toFile()
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        val backupScenesJsonFile = Paths.get(klanglichtDirectory.canonicalPath, "resources", "${timestamp}_scenes.json").toFile()
        if (scenesJsonFile.exists() && !scenesJsonFile.renameTo(backupScenesJsonFile))  error("Could not rename scene file '${scenesJsonFile.canonicalPath}' to '${backupScenesJsonFile.canonicalPath}'")

        val newScenes = LMScenes(
            name = scenes.name,
            groups = scenes.scenesGroupMap.values.map { sg -> LMSceneGroup(
                name = sg.name,
                displayName = sg.displayName,
                hasColorWheel = sg.hasColorWheel,
                colorWheelOddEven = sg.colorWheelOddEven,
                selectable = sg.selectable,
                scenes = sg.scenes.map { s ->
                    var actionHybrid = s.actions
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
                                repeatable = s.repeatable,
                                condition = s.condition,
                                actions = listOf(action),
                                initialize = false
                            )
                        }
                        LMSceneType.sequence -> {
                            LMScene(
                                name = s.name,
                                type = s.type,
                                color = s.color,
                                steps = s.steps,
                                repeatable = s.repeatable,
                                condition = s.condition,
                                actions = s.actions,
                                initialize = false
                            )
                        }
                        else -> {
                            val color = if (actionHybrid?.originalHexColors != null) {
                                actionHybrid = LMActionHybrid(
                                    ids = actionHybrid.ids,
                                    hexColors = actionHybrid.originalHexColors ?: error("No original hex colors"),
                                    factor = actionHybrid.factor,
                                    gains = actionHybrid.gains
                                )
                                listOf()
                            } else if (s.color != (actionHybrid?.hexColors ?: listOf<String>())) {
                                s.color
                            } else listOf()
                            LMScene(
                                name = s.name,
                                type = s.type,
                                color = color,
                                steps = s.steps,
                                repeatable = s.repeatable,
                                condition = s.condition,
                                actions = actionHybrid?.let { a -> listOf(a) } ?: listOf(),
                                initialize = false
                            )
                        }
                    }
                }.toMutableList()
            ) }.toMutableList())

        newScenes.writeValue(scenesJsonFile)
    }

    fun getAbsoluteResource(relativeResourePath: String): File {
        return Paths.get(klanglichtDirectory.canonicalPath, "resources", relativeResourePath).toFile()
    }

    fun getFadeable(id: String): Fadeable<*>? {
        return currentScene?.getFadeable(id)
    }

    fun updateScene(nextScene: HybridScene) {
        currentScene?.update(nextScene)
    }

    fun putColor(id: String, hexColor: String?) {
        hexColor?.let { colorStore[id] = it }?:also { colorStore.remove(id) }
    }

    fun getColor(id: String): String? {
        return colorStore[id]
    }
}
