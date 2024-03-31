package de.visualdigits.kotlin.klanglicht.rest.scenes.controller

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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/scenes/json")
class ScenesRestController(
    var shellyService: ShellyService,
    var lightmanagerService: LightmanagerService,
    val hybridStageService: HybridStageService,
    val yamahaAvantageService: YamahaAvantageService,
    val configHolder: ConfigHolder
) {

    @PostMapping("control")
    fun control(
        @RequestParam(value = "scene") scene: Int,
    ) {
        val lmScene = configHolder.scenes?.scenesMap?.get(scene)
        lmScene
            ?.let { s ->
                s.actions.forEach { a ->
                    when (a) {
                        is LMActionLmAir ->
                            lightmanagerService.controlIndex(index = a.sceneIndex)
                        is LMActionShelly ->
                            shellyService.power(ids = a.ids, turnOn = a.turnOn)
                        is LMActionHybrid ->
                            hybridStageService.hexColor(ids = a.ids, hexColors = a.hexColors, gains = a.gains)
                        is LMActionLmYamahaAvantage -> {
                            when (a.command) {
                                "surroundProgram" -> yamahaAvantageService.setSurroundProgram(program = a.program)
                            }
                        }
                        is LMActionPause -> a.duration?.let { Thread.sleep(it) }
                    }
                }
            }
    }

    @GetMapping("hybrid")
    fun hybrid(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "hexColors", required = false, defaultValue = "") hexColors: String,
        @RequestParam(value = "gains", required = false, defaultValue = "") gains: String,
    ) {
        hybridStageService.hexColor(
            ids = ids,
            hexColors = hexColors,
            gains = gains
        )
    }

    @GetMapping("shelly")
    fun shelly(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "turnOn", required = false, defaultValue = "true") turnOn: Boolean,
        @RequestParam(value = "transition", required = false) transitionDuration: Long
    ) {
        shellyService.power(ids = ids, turnOn = turnOn, transitionDuration = transitionDuration)
    }

    @GetMapping("lmair")
    fun lmair(
        @RequestParam(value = "sceneIndex") sceneIndex: Int
    ) {
        lightmanagerService.controlIndex(index = sceneIndex)
    }

    @GetMapping("yamahaAvantage")
    fun yamaha(
        @RequestParam(value = "command") command: String,
        @RequestParam(value = "program") program: String
    ) {
        when (command) {
            "surroundProgram" -> yamahaAvantageService.setSurroundProgram(program = program)
        }
    }
}
