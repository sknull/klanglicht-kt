package de.visualdigits.kotlin.klanglicht.rest.scenes.service

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActionHybrid
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActionLmAir
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActionLmYamahaAvantage
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActionPause
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActionShelly
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.rest.hybrid.service.HybridStageService
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.service.LightmanagerService
import de.visualdigits.kotlin.klanglicht.rest.shelly.service.ShellyService
import de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.service.YamahaAvantageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ScenesService(
    var shellyService: ShellyService,
    var lightmanagerService: LightmanagerService,
    val hybridStageService: HybridStageService,
    val yamahaAvantageService: YamahaAvantageService,
    val configHolder: ConfigHolder
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun executeScene(name: String) {
        val lmScene = configHolder.scenes().scenesMap.get(name)
        lmScene
            ?.let { s ->
                log.info("Executing scene '$name'...")
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
                log.info("No scene with name '$name'")
        }
    }

    fun sceneNames(): Set<String> = configHolder.scenes().scenesMap.keys

    fun hybrid(ids: String, hexColors: String, gains: String) {
        hybridStageService.hexColor(
            ids = ids,
            hexColors = hexColors,
            gains = gains
        )
    }

    fun shelly(ids: String, turnOn: Boolean, transitionDuration: Long) {
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
