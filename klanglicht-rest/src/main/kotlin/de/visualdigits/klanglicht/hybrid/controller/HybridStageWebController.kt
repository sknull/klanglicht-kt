package de.visualdigits.klanglicht.hybrid.controller

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.lightmanager.model.html.page.LMHtmlScenes
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/v1/hybrid/web")
class HybridStageWebController(
    private val prefs: ApplicationPreferences,
    private val scenes: LMHtmlScenes,
) {

    @GetMapping("/scenes", produces = ["application/xhtml+xml"])
    fun scenes(model: Model): String {
        model.addAttribute("theme", prefs.preferences?.theme)
        model.addAttribute("title", "Scenes")
        model.addAttribute("content", scenes.renderScenes(prefs.scenes()))
        return "pagetemplate"
    }
}
