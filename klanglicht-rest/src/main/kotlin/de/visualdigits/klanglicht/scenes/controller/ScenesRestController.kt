package de.visualdigits.klanglicht.scenes.controller

import de.visualdigits.klanglicht.scenes.service.ScenesService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/scenes/json", produces = ["application/json"])
class ScenesRestController(
    private val scenesService: ScenesService
) {

    @GetMapping("sceneNames")
    fun hybrid(): Set<String> = scenesService.sceneNames()

    @PostMapping("control")
    fun controlPost(@RequestParam(value = "name") name: String) = scenesService.executeScene(name)

    @PutMapping("control")
    fun controlPut(@RequestParam(value = "name") name: String) = scenesService.executeScene(name)

    @GetMapping("control")
    fun controlGet(@RequestParam(value = "name") name: String) = scenesService.executeScene(name)

    @GetMapping("hybrid")
    fun hybrid(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "hexColors", required = false, defaultValue = "") hexColors: String,
        @RequestParam(value = "gains", required = false, defaultValue = "1.0") gains: String,
    ) {
        scenesService.hybrid(
            ids = ids.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            hexColors = hexColors.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            gains = gains.split(",").map { it.toDouble() })
    }

    @GetMapping("shelly")
    fun shelly(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "turnOn", required = false, defaultValue = "true") turnOn: Boolean,
        @RequestParam(value = "transition", required = false) transitionDuration: Long
    ) {
        scenesService.shelly(ids.split(",").map { it.trim() }, turnOn, transitionDuration)
    }

    @GetMapping("lmair")
    fun lmair(@RequestParam(value = "sceneIndex") sceneIndex: Int) = scenesService.lmair(sceneIndex)

    @GetMapping("yamahaAvantage")
    fun yamaha(
        @RequestParam(value = "command") command: String,
        @RequestParam(value = "program") program: String
    ) {
        scenesService.yamahaAvantage(command, program)
    }
}
