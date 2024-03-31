package de.visualdigits.kotlin.klanglicht.rest.lightmanager.controller

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarkers
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMParams
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZones
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.service.LightmanagerService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * REST controller for the light manager air.
 */
@RestController
@RequestMapping("/v1/lightmanager/json")
class LightmanagerRestController(
    var lightmanagerService: LightmanagerService
) {

    @GetMapping("params", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun params(): LMParams? = lightmanagerService.params()

    @GetMapping("zones", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun zones(): LMZones? = lightmanagerService.zones()

    @GetMapping("knownActors", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun knownActors(): Map<Int, String>? = lightmanagerService.knownActors()

    @GetMapping("scenes", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun scenes(): LMScenes? = lightmanagerService.scenes()

    @GetMapping("markers", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun markers(): LMMarkers? = lightmanagerService.markers()
}
