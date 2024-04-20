package de.visualdigits.kotlin.klanglicht.rest.hybrid.controller

import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMHtmlScenes
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/v1/hybrid/web")
class HybridStageWebController(
    var configHolder: ConfigHolder,
) {

    @GetMapping("/scenes", produces = ["application/xhtml+xml"])
    fun scenes(model: Model): String {
        model.addAttribute("theme", configHolder.preferences?.theme)
        model.addAttribute("title", "Scenes")
        val scenes = LMHtmlScenes(configHolder.scenes())
        model.addAttribute("content", scenes.toHtml(configHolder))
        return "pagetemplate"
    }
}
