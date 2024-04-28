package de.visualdigits.klanglicht.scenes.service

import de.visualdigits.klanglicht.hardware.lightmanager.model.lm.LMActionHybrid
import de.visualdigits.klanglicht.hardware.lightmanager.model.lm.LMActionLmAir
import de.visualdigits.klanglicht.hardware.lightmanager.model.lm.LMActionLmYamahaAvantage
import de.visualdigits.klanglicht.hardware.lightmanager.model.lm.LMActionPause
import de.visualdigits.klanglicht.hardware.lightmanager.model.lm.LMActionShelly
import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hybrid.service.HybridStageService
import de.visualdigits.klanglicht.lightmanager.service.LightmanagerService
import de.visualdigits.klanglicht.shelly.service.ShellyService
import de.visualdigits.klanglicht.yamahaavantage.service.YamahaAvantageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ScenesService(
    private val prefs: ApplicationPreferences,
    private val shellyService: ShellyService,
    private val lightmanagerService: LightmanagerService,
    private val hybridStageService: HybridStageService,
    private val yamahaAvantageService: YamahaAvantageService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private var previousSceneName: String = ""

    fun executeScene(sceneName: String) {
        if (sceneName != previousSceneName) {
            previousSceneName = sceneName
            val lmScene = prefs.scenes().scenesMap[sceneName]
            lmScene
                ?.let { s ->
                    log.info("Executing scene '$sceneName'...")
                    s.actions.forEach { action ->
                        log.info("  Executing action '$action'...")
                        when (action) {
                            is LMActionLmAir ->
                                lightmanagerService.controlIndex(index = action.sceneIndex)

                            is LMActionShelly ->
                                shellyService.power(ids = action.ids, turnOn = action.turnOn)

                            is LMActionHybrid ->
                                hybridStageService.hexColor(ids = action.ids, hexColors = action.hexColors, gains = action.gains)

                            is LMActionLmYamahaAvantage -> {
                                when (action.command) {
                                    "surroundProgram" -> yamahaAvantageService.setSurroundProgram(program = action.program)
                                }
                            }

                            is LMActionPause -> action.duration?.let { Thread.sleep(it) }
                        }
                    }
                }?:also {
                log.info("No scene with name '$sceneName'")
            }
        } else {
            log.info("Scene '$sceneName' already set - skipping")
        }
    }

    fun sceneNames(): Set<String> = prefs.scenes().scenesMap.keys

    fun hybrid(ids: List<String>, hexColors: List<String>, gains: List<Double>) {
        hybridStageService.hexColor(
            ids = ids,
            hexColors = hexColors,
            gains = gains
        )
    }

    fun shelly(ids: List<String>, turnOn: Boolean, transitionDuration: Long) {
        shellyService.power(ids = ids, turnOn = turnOn, transitionDuration = transitionDuration)
    }

    fun lmair(sceneIndex: Int) {
        lightmanagerService.controlIndex(index = sceneIndex)
    }

    fun yamahaAvantage(command: String, program: String) {
        when (command) {
            "surroundProgram" -> yamahaAvantageService.setSurroundProgram(program = program)
        }
    }
}
