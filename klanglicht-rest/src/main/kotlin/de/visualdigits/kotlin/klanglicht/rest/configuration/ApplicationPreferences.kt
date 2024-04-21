package de.visualdigits.kotlin.klanglicht.rest.configuration

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.model.dmx.parameter.Fadeable
import de.visualdigits.kotlin.klanglicht.model.hybrid.HybridScene
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
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

@Configuration
@ConfigurationProperties(prefix = "application")
@ConfigurationPropertiesScan
class ApplicationPreferences {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    var preferences: Preferences? = null

    @Value("\${application.klanglichtDirectory}")
    var klanglichtDirectory: File = File("/")

    @Value("\${application.services.lmair.url}")
    var urlLightmanager: String = ""

    var currentScene: HybridScene? = null
    val colorStore: MutableMap<String, String> = mutableMapOf()

    @PostConstruct
    fun initialize() {
        preferences?.initialize(klanglichtDirectory)

        log.info("#### setUp - start")
        log.info("##")
        log.info("## klanglichtDirectory: " + klanglichtDirectory.absolutePath)
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

    fun scenes(): LMScenes {
        return LMScenes.unmarshall(Paths.get(klanglichtDirectory.canonicalPath, "preferences", "scenes.json").toFile())
    }
}
