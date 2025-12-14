package de.visualdigits.klanglicht.scenes.controller

import de.visualdigits.klanglicht.scenes.service.ScenesService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/scenes/json", produces = ["application/json"])
class ScenesRestController(
    private val scenesService: ScenesService
) {

    @GetMapping("sceneNames")
    fun hybrid(): Set<String> = scenesService.sceneNames()

    @PostMapping("control")
    fun controlPost(
        @RequestParam(value = "group") group: String,
        @RequestParam(value = "scene") scene: String
    ) = scenesService.executeScene(group, scene)

    @PutMapping("control")
    fun controlPut(
        @RequestParam(value = "group") group: String,
        @RequestParam(value = "scene") scene: String
    ) = scenesService.executeScene(group, scene)

    @GetMapping("control")
    fun controlGet(
        @RequestParam(value = "group") group: String,
        @RequestParam(value = "scene") scene: String
    ) = scenesService.executeScene(group, scene)

    @GetMapping("/save", produces = ["application/xhtml+xml"])
    fun save(
        @RequestParam(value = "group") group: String,
        @RequestParam(value = "scene") scene: String,
        request: HttpServletRequest
    ): ResponseEntity<Unit> {
        scenesService.saveCustomScene(scene)
        return ResponseEntity.status(302).location(URI.create("/v1/hybrid/web/scenes")).build()
    }

    @DeleteMapping("delete")
    fun delete(
        @RequestParam(value = "group") group: String,
        @RequestParam(value = "scene") scene: String
    ) = scenesService.deleteCustomScene(scene)

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
        @RequestParam(value = "program") program: String,
        @RequestParam(value = "enable") enable: Boolean
    ) {
        scenesService.yamahaAvantage(command, program, enable)
    }

    @GetMapping("twinkly")
    fun twinkly(
        @RequestParam(value = "command") command: String,
        @RequestParam(value = "moodsIndex") moodsIndex: Int,
        @RequestParam(value = "effectIndex") effectIndex: Int
    ) {
        scenesService.twinkly(command, moodsIndex, effectIndex)
    }
}
