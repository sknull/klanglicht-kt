package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMSceneGroup
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences

class LMHtmlScenes(
    val scene: LMScenes
) : HtmlRenderable {

    override fun toHtml(prefs: ApplicationPreferences): String {
        val sb = StringBuilder()
        sb.append("<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
            .append(scene.name)
            .append("</div>\n")
        sb.append("<div class=\"center-category\">\n")
        renderLabel(sb, "C U R R E N T   S C E N E")
        sb.append("<div class=\"center-group\">\n")
        prefs.currentScene?.fadeables()?.forEach { fadeable ->
            val color = fadeable.getRgbColor()?.web()?:"#000000"
            val html = renderPanel("circle", color, "")
            sb.append(html)
        }
        sb.append("</div><!-- sub-group -->\n\n")
        sb.append("</div><!-- current scene -->\n\n")

        sb.append("<div class=\"category\">\n")
        renderLabel(sb, "S C E N E S")
        scene.scenes.values.forEach { sceneGroup ->
            val html = renderScenesGroup(
                prefs,
                sceneGroup
            )
            sb.append(html)
        }
        sb.append("</div><!-- scenes -->\n\n")
        return sb.toString()
    }

    private fun renderPanel(clazz: String, bgColor: String, value: String?): String {
        val sb = StringBuilder()
        sb.append("      <div class=\"").append(clazz).append("\" style=\"background-color:").append(bgColor)
            .append("\" >\n")
        if (value != null && !value.isEmpty()) {
            renderLabel(sb, value)
        }
        sb.append("      </div> <!-- ").append(clazz).append(" -->\n")
        return sb.toString()
    }

    private fun renderScenesGroup(
        prefs: ApplicationPreferences,
        sceneGroup: LMSceneGroup
    ): String {
        val sb = StringBuilder()
        sb.append("  <div class=\"group")
        if (sceneGroup.hasColorWheel) {
            if (sceneGroup.colorWheelOddEven) {
                sb.append(" has-colorwheel-odd-even")
            } else {
                sb.append(" has-colorwheel")
            }
        }
        sb.append("\">\n")
        renderLabel(sb, sceneGroup.name)
        sb.append("    <div class=\"sub-group")
        if (sceneGroup.hasColorWheel) {
            if (sceneGroup.colorWheelOddEven) {
                sb.append(" has-colorwheel-odd-even")
            } else {
                sb.append(" has-colorwheel")
            }
        }
        sb.append("\">\n")
        sceneGroup.scenes.forEach { scene ->
            sb.append(LMHtmlScene(scene).toHtml(prefs, sceneGroup.name))
        }
        sb.append("    </div><!-- sub-group -->\n")
        sb.append("  </div><!-- group -->\n")
        if (sceneGroup.hasColorWheel) {
            val html: String = ColorWheel(sceneGroup.name).toHtml(prefs, sceneGroup.colorWheelOddEven)
            sb.append(html)
        }
        return sb.toString()
    }
}
