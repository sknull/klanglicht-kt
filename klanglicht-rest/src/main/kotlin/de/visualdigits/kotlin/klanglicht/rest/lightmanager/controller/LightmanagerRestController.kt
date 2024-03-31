package de.visualdigits.kotlin.klanglicht.rest.lightmanager.controller

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarkers
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMParams
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZones
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.service.LightmanagerService
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMHtmlZones
import org.springframework.beans.factory.annotation.Autowired
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
    var client: LightmanagerService
) {

    @GetMapping("params", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun params(): LMParams? = client?.params()

    @GetMapping("zones", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun zones(): LMZones? = client?.zones()

    @GetMapping("knownActors", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun knownActors(): Map<Int, String>? = client?.knownActors()

    @GetMapping("scenes", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun scenes(): LMScenes? = client?.scenes()

    @GetMapping("markers", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun markers(): LMMarkers? = client?.markers()
}
