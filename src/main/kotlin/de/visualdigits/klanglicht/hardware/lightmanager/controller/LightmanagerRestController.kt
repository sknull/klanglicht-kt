package de.visualdigits.klanglicht.hardware.lightmanager.controller

import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMMarkers
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMParams
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMZones
import de.visualdigits.klanglicht.hardware.lightmanager.service.LightmanagerService
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
