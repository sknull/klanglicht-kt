package de.visualdigits.klanglicht.configuration

import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMScene
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMSceneType
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMScenes
import de.visualdigits.klanglicht.model.hybrid.HybridScene
import de.visualdigits.klanglicht.model.preferences.Stage
import de.visualdigits.kotlin.twinkly.model.parameter.Fadeable
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
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
class ApplicationPreferences {

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

    @PreDestroy
    fun tearDown() {
        log.info("#### tearDown - start")
        stage?.devices?.dmx?.tearDownDmx()
        log.info("#### tearDown - end")
    }

    fun loadScenes(): LMScenes = LMScenes.readValue(Paths.get(klanglichtDirectory.canonicalPath, "resources", "scenes.json").toFile())

    fun writeScenes(scenes: LMScenes) {
        val scenesJsonFile = Paths.get(klanglichtDirectory.canonicalPath, "resources", "scenes.json").toFile()
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        val backupScenesJsonFile = Paths.get(klanglichtDirectory.canonicalPath, "resources", "${timestamp}_scenes.json").toFile()
        if (scenesJsonFile.exists()) {
            scenesJsonFile.renameTo(backupScenesJsonFile)
        }

        val newScenes = LMScenes(name = scenes.name)
        newScenes.scenes.putAll(
            scenes.scenes.map { (name, group) ->
                group.scenes = group.scenes.map { scene ->
                    when (scene.type) {
                        LMSceneType.custom -> scene
                        LMSceneType.gradient -> {
                            val steps = scene.color.size
                            val newScene = LMScene(
                                name = scene.name,
                                color = listOf(scene.color.first(), scene.color.last()),
                                type = LMSceneType.gradient,
                                steps = steps,
                                initialize = false
                            )
                            newScene
                        }
                    }
                }.toMutableList()
                Pair(name, group)
            })

        newScenes.writeValue(scenesJsonFile)
    }

    fun getAbsoluteResource(relativeResourePath: String): File {
        return Paths.get(klanglichtDirectory.absolutePath, "resources", relativeResourePath).toFile()
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
