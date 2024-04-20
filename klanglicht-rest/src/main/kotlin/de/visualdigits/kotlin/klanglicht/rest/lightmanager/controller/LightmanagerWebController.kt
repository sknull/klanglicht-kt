package de.visualdigits.kotlin.klanglicht.rest.lightmanager.controller

import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMHtmlScenes
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMHtmlZones
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.service.LightmanagerService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/v1/lightmanager/web")
class LightmanagerWebController(
    var configHolder: ConfigHolder,
    var client: LightmanagerService
) {


    @GetMapping("/scenes", produces = ["application/xhtml+xml"])
    fun scenes(model: Model): String {
        model.addAttribute("theme", configHolder.preferences?.theme)
        model.addAttribute("title", "Scenes")
        val scenes = LMHtmlScenes(configHolder.scenes())
        model.addAttribute("content", scenes.toHtml(configHolder))
        return "pagetemplate"
    }

    @GetMapping("/zones", produces = ["application/xhtml+xml"])
    fun zones(model: Model): String {
        model.addAttribute("theme", configHolder.preferences?.theme)
        model.addAttribute("title", "Zones")
        val zones = client.zones()?.let { LMHtmlZones(it) }
        model.addAttribute("content", zones?.toHtml(configHolder))
        return "pagetemplate"
    }
}
