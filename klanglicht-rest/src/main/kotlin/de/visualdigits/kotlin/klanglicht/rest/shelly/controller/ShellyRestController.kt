package de.visualdigits.kotlin.klanglicht.rest.shelly.controller

import de.visualdigits.kotlin.klanglicht.rest.shelly.handler.ShellyHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Arrays
import java.util.stream.Collectors

/**
 * REST controller for shelly devices.
 */
@RestController
@RequestMapping("/v1/shelly")
class ShellyRestController {
    @Autowired
    var shellyHandler: ShellyHandler? = null

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
        shellyHandler!!.control(sceneId, index)
    }

    @GetMapping(value = ["/hexColor"])
    fun hexColor(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "hexColors") hexColors: String,
        @RequestParam(value = "gains", required = false, defaultValue = "") gains: String,
        @RequestParam(value = "transition", required = false, defaultValue = "2000") transition: Int,
        @RequestParam(value = "turnOn", required = false, defaultValue = "true") turnOn: Boolean,
        @RequestParam(value = "store", required = false, defaultValue = "true") store: Boolean
    ) {
        val lIds = Arrays.stream(ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            .filter { e: String -> !e.isEmpty() }
            .collect(Collectors.toList())
        val lHexColors = Arrays.stream(hexColors.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()).filter { e: String -> !e.isEmpty() }.collect(Collectors.toList())
        val lGains = Arrays.stream(gains.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            .filter { e: String -> !e.isEmpty() }
            .collect(Collectors.toList())
        shellyHandler!!.hexColors(lIds, lHexColors, lGains, transition, turnOn, store)
    }

    @GetMapping(value = ["/color"])
    fun color(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "red", required = false, defaultValue = "0") red: Int,
        @RequestParam(value = "green", required = false, defaultValue = "0") green: Int,
        @RequestParam(value = "blue", required = false, defaultValue = "0") blue: Int,
        @RequestParam(value = "gains", required = false, defaultValue = "") gains: String,
        @RequestParam(value = "transition", required = false, defaultValue = "2000") transition: Int,
        @RequestParam(value = "turnOn", required = false, defaultValue = "true") turnOn: Boolean,
        @RequestParam(value = "store", required = false, defaultValue = "true") store: Boolean
    ) {
        shellyHandler!!.color(ids, red, green, blue, gains, transition, turnOn, store)
    }

    @GetMapping(value = ["/restore"])
    fun restoreColors(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "transition", required = false, defaultValue = "2000") transition: Int
    ) {
        shellyHandler!!.restoreColors(ids, transition)
    }

    @GetMapping(value = ["/power"])
    fun power(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "turnOn", required = false, defaultValue = "true") turnOn: Boolean,
        @RequestParam(value = "transition", required = false, defaultValue = "2000") transition: Int
    ) {
        shellyHandler!!.power(ids, turnOn, transition)
    }

    @GetMapping(value = ["/gain"])
    fun gain(
        @RequestParam(value = "ids", required = false, defaultValue = "") ids: String,
        @RequestParam(value = "gain", required = false, defaultValue = "2000") gain: Int,
        @RequestParam(value = "transition", required = false, defaultValue = "2000") transition: Int
    ) {
        shellyHandler!!.gain(ids, gain, transition)
    }
}
