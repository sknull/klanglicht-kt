package de.visualdigits.klanglicht.lightmanager.model.html.page.scenes

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMScenes
import de.visualdigits.klanglicht.lightmanager.model.html.page.components.LMHtml
import kotlin.math.max

/**
 * Scenes description used to render the HTML markup.
 */
class LMHtmlScenes(
    val prefs: ApplicationPreferences,
    val scenes: LMScenes? = prefs.scenes()
) : LMHtml {

    override fun html(indent: Int): String {
        val sindent = "  ".repeat(indent)
        val sb = StringBuilder()
        sb.append("$sindent<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
            .append(scenes?.name)
            .append("</div>\n")
        sb.append("$sindent<div class=\"center-category\">\n")
        sb.append("$sindent  <span class=\"label\">").append("C U R R E N T   S C E N E").append("</span>\n")
        sb.append("$sindent  <div class=\"center-group\">\n")
        prefs.currentScene?.fadeables()?.forEach { fadeable ->
            val color = fadeable.toRgbColor().web() ?: "#000000"
            val html = LMHtmlPanel(color).html(indent + 2)
            sb.append(html)
        }
        sb.append("$sindent  </div><!-- sub-group -->\n")
        sb.append("$sindent</div><!-- current scene -->\n")

        sb.append("$sindent<div class=\"category\">\n")
        sb.append("$sindent  <span class=\"label\">").append("S C E N E S").append("</span>\n")
        scenes?.scenes?.values?.forEach { sceneGroup ->
            val html = LMHtmlSceneGroup(prefs, sceneGroup).html(indent + 1)
            sb.append(html)
        }
        sb.append("$sindent</div><!-- scenes -->\n")
        sb.append("  ".repeat(max(0, indent - 1)))

        return sb.toString()
    }
}
