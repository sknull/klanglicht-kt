package de.visualdigits.klanglicht.shelly.controller

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.shelly.model.html.ShellyStatus
import de.visualdigits.klanglicht.shelly.service.ShellyService
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
