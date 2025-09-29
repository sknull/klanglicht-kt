package de.visualdigits.klanglicht.lightmanager.model.html.page.scenes

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMSceneGroup
import de.visualdigits.klanglicht.lightmanager.model.html.page.components.LMHtml
import de.visualdigits.klanglicht.lightmanager.model.html.page.components.LMHtmlColorWheel

class LMHtmlSceneGroup(
    val prefs: ApplicationPreferences,
    val sceneGroup: LMSceneGroup
): LMHtml {

    override fun html(indent: Int): String {
        val sindent = "  ".repeat(indent)
        val sb = StringBuilder()
        sb.append("$sindent<div class=\"group")
        if (sceneGroup.hasColorWheel) {
            if (sceneGroup.colorWheelOddEven) {
                sb.append(" has-colorwheel-odd-even")
            } else {
                sb.append(" has-colorwheel")
            }
        }
        sb.append("\">\n")
        sb.append("$sindent  <span class=\"label\">").append(sceneGroup.name).append("</span>\n")
        sb.append("$sindent  <div class=\"sub-group")
        if (sceneGroup.hasColorWheel) {
            if (sceneGroup.colorWheelOddEven) {
                sb.append(" has-colorwheel-odd-even")
            } else {
                sb.append(" has-colorwheel")
            }
        }
        sb.append("\">\n")
        sceneGroup.scenes.forEach { scene ->
            sb.append(LMHtmlScene(prefs, sceneGroup, scene).html(indent + 1))
        }
        sb.append("$sindent  </div><!-- sub-group -->\n")
        sb.append("$sindent</div><!-- group -->\n")
        if (sceneGroup.hasColorWheel) {
            sb.append(LMHtmlColorWheel(prefs, sceneGroup.name, sceneGroup.colorWheelOddEven).html(indent))
        }
        return sb.toString()
    }
}
