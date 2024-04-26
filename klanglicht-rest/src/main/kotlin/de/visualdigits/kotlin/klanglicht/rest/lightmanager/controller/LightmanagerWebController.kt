package de.visualdigits.kotlin.klanglicht.rest.lightmanager.controller

import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
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
    private val prefs: ApplicationPreferences,
    private val lightmanagerService: LightmanagerService,
    private val scenes: LMHtmlScenes,
    private val zones: LMHtmlZones
) {

    @GetMapping("/scenes", produces = ["application/xhtml+xml"])
    fun scenes(model: Model): String {
        model.addAttribute("theme", prefs.preferences?.theme)
        model.addAttribute("title", "Scenes")
        model.addAttribute("content", scenes.renderScenes(lightmanagerService.scenes()))
        return "pagetemplate"
    }

    @GetMapping("/zones", produces = ["application/xhtml+xml"])
    fun zones(model: Model): String {
        model.addAttribute("theme", prefs.preferences?.theme)
        model.addAttribute("title", "Zones")
        model.addAttribute("content", zones.renderZones())
        return "pagetemplate"
    }
}
