package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html

import com.fasterxml.jackson.annotation.JsonIgnore
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.widgets.ColorWheel
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.util.Locale
import java.util.TreeMap
import java.util.function.Consumer

class LMScenes(
    val name: String? = null
) : HtmlRenderable {
    val COLOR_WHEEL_GROUPS: List<String> = mutableListOf("Dmx", "Deko", "Rgbw", "Bar", "Starwars")

    @JsonIgnore
    val scenes: MultiValueMap<String, LMScene> = LinkedMultiValueMap()

    fun add(scene: LMScene) {
        var group: String? = "common"
        val attributes = LMNamedAttributes(scene.name, "group", "color")
        if (attributes.matched()) {
            val name = attributes.name
            if (name?.isNotEmpty() == true) {
                scene.name = name
            }
            val g = attributes["group"]
            if (g.isNotEmpty()) {
                group = g
            }
            scene.color = attributes["color"]
        }
        if ("hidden" != group) {
            group?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                ?.let { scenes.add(it, scene) }
        }
    }

    override fun toHtml(configHolder: ConfigHolder): String {
        val sb = StringBuilder()
        sb.append("<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
            .append(name)
            .append("</div>\n")
        sb.append("<div class=\"center-category\">\n")
        renderLabel(sb, "C U R R E N T   S C E N E")
        sb.append("<div class=\"center-group\">\n")
        configHolder.currentScene?.fadeables()?.forEach { fadeable ->
            val color = fadeable.getRgbColor()?.web()?:"#000000"
            renderPanel(sb, "circle", color, "")
        }
        sb.append("</div><!-- sub-group -->\n\n")
        sb.append("</div><!-- current scene -->\n\n")

        sb.append("<div class=\"category\">\n")
        renderLabel(sb, "S C E N E S")
        val scenesMap: Map<String, List<LMScene>> = TreeMap<String, List<LMScene>>(scenes.toMap())
        scenesMap.forEach { (group: String, groupScenes: List<LMScene>) ->
            renderScenesGroup(
                sb,
                configHolder,
                group,
                groupScenes
            )
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

    private fun renderPanel(sb: StringBuilder, clazz: String, bgColor: String, value: String?) {
        sb.append("      <div class=\"").append(clazz).append("\" style=\"background-color:").append(bgColor)
            .append("\" >\n")
        if (value != null && !value.isEmpty()) {
            renderLabel(sb, value)
        }
        sb.append("      </div> <!-- ").append(clazz).append(" -->\n")
    }

    private fun renderScenesGroup(
        sb: StringBuilder,
        configHolder: ConfigHolder,
        group: String,
        groupScenes: List<LMScene>
    ) {
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
        groupScenes.forEach(Consumer { scene: LMScene -> sb.append(scene.toHtml(configHolder, group)) })
        sb.append("    </div><!-- sub-group -->\n")
        sb.append("  </div><!-- group -->\n")
        if (hasColorWheel) {
            val html: String = ColorWheel(group).toHtml(configHolder)
            sb.append(html)
        }
    }
}
