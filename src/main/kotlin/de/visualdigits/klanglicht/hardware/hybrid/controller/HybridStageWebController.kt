package de.visualdigits.klanglicht.hardware.hybrid.controller

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.lightmanager.model.html.page.scenes.LMHtmlScenes
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

        prefs.stage?.devices?.refreshTwinklyDevices(1000)
        val scenes = prefs.loadScenes()
        model.addAttribute("content", LMHtmlScenes(prefs, scenes).html(4))

        return "pagetemplate"
    }
}
