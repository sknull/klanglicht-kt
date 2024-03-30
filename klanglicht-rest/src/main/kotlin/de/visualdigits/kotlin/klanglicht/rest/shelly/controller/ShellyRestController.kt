package de.visualdigits.kotlin.klanglicht.rest.shelly.controller

import de.visualdigits.kotlin.klanglicht.rest.hybrid.handler.HybridStageHandler
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.service.LightmanagerService
import de.visualdigits.kotlin.klanglicht.rest.shelly.service.ShellyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for shelly devices.
 */
@RestController
@RequestMapping("/v1/shelly")
class ShellyRestController {

    @Autowired
    var shellyService: ShellyService? = null

    @Autowired
    var lightmanagerService: LightmanagerService? = null

    @Autowired
    val hybridStageHandler: HybridStageHandler? = null

    /**
     * Sets the given scene or index on the connected lightmanager air.
     *
     * @param sceneId
     * @param index
     */
    @GetMapping("/control")
    fun control(
        @RequestParam(value = "scene", required = false, defaultValue = "0") sceneId: Int,
        @RequestParam(value = "index", required = false, defaultValue = "0") index: Int
    ) {
        if (sceneId != 0) {
            lightmanagerService?.controlScene(sceneId)
        } else if (index != 0) {
            lightmanagerService?.controlIndex(index)
        }
    }

    @GetMapping("power")
    fun power(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "turnOn", required = false, defaultValue = "true") turnOn: Boolean,
        @RequestParam(value = "transition", required = false) transitionDuration: Long?
    ) {
        shellyService?.power(ids = ids, turnOn = turnOn, transitionDuration = transitionDuration)
    }

    @GetMapping("hexColor")
    fun hexColor(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "hexColors") hexColors: String,
        @RequestParam(value = "gains", required = false, defaultValue = "") gains: String,
        @RequestParam(value = "transition", required = false) transitionDuration: Long?,
        @RequestParam(value = "turnOn", required = false, defaultValue = "true") turnOn: Boolean,
        @RequestParam(value = "store", required = false, defaultValue = "true") store: Boolean,
        @RequestParam(value = "storeName", required = false) storeName: String?
    ) {
        hybridStageHandler?.hexColor(
            ids = ids,
            hexColors = hexColors,
            gains = gains,
            transition = transitionDuration,
            turnOn = turnOn,
            store = store,
            storeName = storeName
        )
    }

    @GetMapping("restore")
    fun restoreColors(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "transition", required = false) transitionDuration: Long?
    ) {
        hybridStageHandler?.restoreColors(ids = ids, transitionDuration = transitionDuration)
    }

    @GetMapping("gain")
    fun gain(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "gain", required = false, defaultValue = "") gain: Int,
        @RequestParam(value = "transition", required = false) transitionDuration: Long?
    ) {
        hybridStageHandler?.gain(ids = ids, gain = gain, transitionDuration = transitionDuration)
    }
}
