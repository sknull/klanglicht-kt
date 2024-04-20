package de.visualdigits.kotlin.klanglicht.rest.hybrid.controller

import de.visualdigits.kotlin.klanglicht.rest.hybrid.service.HybridStageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/hybrid/json")
class HybridStageRestController(
    private val hybridStageService: HybridStageService
) {

    @GetMapping("hexColor")
    fun hexColor(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "hexColors") hexColors: String,
        @RequestParam(value = "gains", required = false, defaultValue = "") gains: String,
        @RequestParam(value = "transition", required = false) transition: Long?,
        @RequestParam(value = "turnOn", required = false, defaultValue = "true") turnOn: Boolean,
        @RequestParam(value = "store", required = false, defaultValue = "true") store: Boolean,
        @RequestParam(value = "storeName", required = false) storeName: String?
    ) {
        hybridStageService.hexColor(
            ids = ids.split(",").map { it.trim() },
            hexColors = hexColors.split(",").map { it.trim() },
            gains = gains.split(",").map { it.toDouble() },
            transition = transition,
            turnOn = turnOn,
            store = store,
            storeName = storeName
        )
    }

    @GetMapping("putColor")
    fun putColor(
        @RequestParam(value = "id") id: String,
        @RequestParam(value = "hexColor") hexColor: String
    ) {
        hybridStageService.putColor(
            id = id,
            hexColor = hexColor
        )
    }
}
