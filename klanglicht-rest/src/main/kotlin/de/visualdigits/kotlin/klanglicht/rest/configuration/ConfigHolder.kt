package de.visualdigits.kotlin.klanglicht.rest.configuration

import de.visualdigits.kotlin.klanglicht.hardware.dmx.parameter.Fadeable
import de.visualdigits.kotlin.klanglicht.hardware.hybrid.HybridScene
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScene
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.hardware.preferences.Preferences
import de.visualdigits.kotlin.util.SystemUtils
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Paths

@Component
class ConfigHolder {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    var preferences: Preferences? = null

    var scenes: LMScenes? = null

    val klanglichtDirectory: File = File(SystemUtils.getUserHome(), ".klanglicht")

    var currentScene: HybridScene? = null
    val colorStore: MutableMap<String, String> = mutableMapOf()

    @PostConstruct
    fun initialize() {
        // load preferences
        log.info("#### setUp - start")
        log.info("##")
        log.info("## klanglichtDirectory: " + klanglichtDirectory.absolutePath)
        preferences = Preferences.load(klanglichtDirectory)
        scenes = LMScenes.unmarshall(Paths.get(klanglichtDirectory.canonicalPath, "preferences", "scenes.json").toFile())
        currentScene = preferences?.initialHybridScene()
        currentScene?.write(preferences, true, 1000)
        log.info("#### setUp - end")
    }

    @PreDestroy
    fun tearDown() {
        log.info("#### tearDown - start")
        preferences?.tearDownDmx()
        log.info("#### tearDown - end")
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
        hexColor?.let { colorStore[id] = it }
    }

    fun getColor(id: String): String? {
        return colorStore[id]
    }
}
