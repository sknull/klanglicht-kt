package de.visualdigits.kotlin.klanglicht.rest.lightmanager.controller

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarkers
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMParams
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
@RequestMapping("/v1/lightmanager/json", produces = [MediaType.APPLICATION_JSON_VALUE])
class LightmanagerRestController(
    private val lightmanagerService: LightmanagerService
) {

    @GetMapping("params")
    fun params(): LMParams? = lightmanagerService.params()

    @GetMapping("zones")
    fun zones(): LMZones? = lightmanagerService.zones()

    @GetMapping("knownActors")
    fun knownActors(): Map<Int, String>? = lightmanagerService.knownActors()

    @GetMapping("markers")
    fun markers(): LMMarkers? = lightmanagerService.markers()
}
