package de.visualdigits.klanglicht.hybrid.controller

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.lightmanager.model.html.page.scenes.LMHtmlScenes
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/v1/hybrid/web")
class HybridStageWebController(
    private val prefs: ApplicationPreferences
) {

    @GetMapping("/scenes", produces = ["application/xhtml+xml"])
    fun scenes(model: Model): String {
        model.addAttribute("theme", prefs.theme)
        model.addAttribute("title", "Scenes")
        model.addAttribute("content", LMHtmlScenes(prefs).html(4))
        return "pagetemplate"
    }
}
