package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScene
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import java.util.TreeMap

class LMHtmlScenes(
    val scene: LMScenes
) : HtmlRenderable {

    companion object {
        val COLOR_WHEEL_GROUPS: List<String> = mutableListOf("Dmx", "Deko", "Rgbw", "Bar", "Starwars")
    }

    override fun toHtml(configHolder: ConfigHolder): String {
        val sb = StringBuilder()
        sb.append("<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
            .append(scene.name)
            .append("</div>\n")
        sb.append("<div class=\"center-category\">\n")
        renderLabel(sb, "C U R R E N T   S C E N E")
        sb.append("<div class=\"center-group\">\n")
        configHolder.currentScene?.fadeables()?.forEach { fadeable ->
            val color = fadeable.getRgbColor()?.web()?:"#000000"
            val html = renderPanel("circle", color, "")
            sb.append(html)
        }
        sb.append("</div><!-- sub-group -->\n\n")
        sb.append("</div><!-- current scene -->\n\n")

        sb.append("<div class=\"category\">\n")
        renderLabel(sb, "S C E N E S")
        val scenesMap: Map<String, List<LMScene>> = TreeMap<String, List<LMScene>>(scene.scenes.toMap())
        scenesMap.forEach { (group: String, groupScenes: List<LMScene>) ->
            val html = renderScenesGroup(
                configHolder,
                group,
                groupScenes
            )
            sb.append(html)
        }
        sb.append("  <div class=\"group\">\n")
        renderLabel(sb, "All")
        sb.append(ColorWheel("All").toHtml(configHolder))
        sb.append("  </div><!-- group -->\n")

        sb.append("  <div class=\"group\">\n")
        renderLabel(sb, "All Odd Even")
        sb.append(ColorWheel("All").toHtml(configHolder, true))
        sb.append("  </div><!-- group -->\n")

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
        configHolder: ConfigHolder,
        group: String,
        groupScenes: List<LMScene>
    ): String {
        val sb = StringBuilder()
        val hasColorWheel = COLOR_WHEEL_GROUPS.contains(group)
        sb.append("  <div class=\"group")
        if (hasColorWheel) {
            sb.append(" has-colorwheel")
        }
        sb.append("\">\n")
        renderLabel(sb, group)
        sb.append("    <div class=\"sub-group")
        if (hasColorWheel) {
            sb.append(" has-colorwheel")
        }
        sb.append("\">\n")
        groupScenes.forEach { scene ->
            sb.append(LMHtmlScene(scene).toHtml(configHolder, group))
        }
        sb.append("    </div><!-- sub-group -->\n")
        sb.append("  </div><!-- group -->\n")
        if (hasColorWheel) {
            val html: String = ColorWheel(group).toHtml(configHolder)
            sb.append(html)
        }
        return sb.toString()
    }
}
