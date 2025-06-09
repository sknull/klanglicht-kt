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
    val scenes: LMScenes? = prefs.loadScenes()
) : LMHtml {

    override fun html(indent: Int): String {
        val sindent = "  ".repeat(indent)
        val sb = StringBuilder()
        sb.append("$sindent<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
        sb.append(scenes?.name)
        sb.append("</div>\n")
        sb.append("$sindent<div class=\"center-category\">\n")
        sb.append("$sindent  <span class=\"label\">").append("C U R R E N T   S C E N E").append("</span>\n")
        sb.append("$sindent  <div class=\"center-group\">\n")
        prefs.currentScene?.fadeables()?.forEach { fadeable ->
            sb.append(LMHtmlPanel(fadeable.toRgbColor().web() ?: "#000000").html(indent + 2))
        }

        val selectableGroupNames = scenes?.selectableGroupNames()?:listOf()
        var currentSceneName = prefs.currentSceneName
        val parts = currentSceneName.split(' ')
        val currentGroupName = parts.firstOrNull()?.let { gn -> selectableGroupNames.find { n -> n == gn } }
        if (currentGroupName != null) {
            currentSceneName = parts.drop(1).joinToString(" ")
        }

        sb.append("$sindent    <div class='inputform'>\n")
        sb.append("$sindent      <form action='${prefs.baseUrl}:${prefs.port}/v1/scenes/json/save' method='GET'>\n")
        sb.append("$sindent        <input type='text' id='name' name='name' value='$currentSceneName'/>\n")
        sb.append("$sindent        <input type='submit' value='Save'/>\n")
        sb.append("$sindent      </form>\n")
        sb.append("$sindent    </div> <!-- input form -->\n")

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
