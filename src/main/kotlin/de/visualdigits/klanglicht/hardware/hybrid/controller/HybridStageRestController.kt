package de.visualdigits.klanglicht.hardware.hybrid.controller

import de.visualdigits.klanglicht.hardware.hybrid.service.HybridStageService
import de.visualdigits.klanglicht.hardware.lightmanager.model.client.ClientStage
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/hybrid/json")
class HybridStageRestController(
    private val hybridStageService: HybridStageService
) {

    @GetMapping("stage", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getClientScenes(): ClientStage = hybridStageService.getClientStage()

    @GetMapping("hexColor")
    fun hexColor(
        @RequestParam(value = "wheelId", required = false, defaultValue = "") wheelId: String,
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "hexColors") hexColors: String,
        @RequestParam(value = "gains", required = false, defaultValue = "1.0") gains: String,
        @RequestParam(value = "transition", required = false) transition: Long?,
        @RequestParam(value = "turnOn", required = false, defaultValue = "true") turnOn: Boolean,
        @RequestParam(value = "store", required = false, defaultValue = "true") store: Boolean,
        @RequestParam(value = "storeName", required = false) storeName: String?
    ) {
        hybridStageService.hexColor(
            wheelId = wheelId,
            ids = ids.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            hexColors = hexColors.split(",").map { it.trim() }.filter { it.isNotEmpty() },
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
