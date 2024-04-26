package de.visualdigits.kotlin.klanglicht.rest.shelly.controller

import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import de.visualdigits.kotlin.klanglicht.rest.shelly.model.html.ShellyStatus
import de.visualdigits.kotlin.klanglicht.rest.shelly.service.ShellyService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/v1/shelly/web")
class ShellyWebController(
    private val prefs: ApplicationPreferences,
    private val shellyStatus: ShellyStatus
) {

    @GetMapping("powers", produces = ["application/xhtml+xml"])
    fun currentPowers(model: Model, request: HttpServletRequest?): String {
        model.addAttribute("theme", prefs.preferences?.theme)
        model.addAttribute("title", "Current Power Values")
        model.addAttribute("content", shellyStatus.renderShellyStatus())
        return "pagetemplate"
    }
}
