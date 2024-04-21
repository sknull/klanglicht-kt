package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZones
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences

class LMHtmlZones(
    val zones: LMZones
) : HtmlRenderable {

    override fun toHtml(prefs: ApplicationPreferences): String {
        val sb = StringBuilder()
        sb.append("<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
            .append(zones.name)
            .append("</div>\n")
        sb.append("<div class=\"category\">\n")
        renderLabel(sb, "Z O N E S")
        zones.zones.forEach { zone ->
            sb.append(LMHtmlZone(zone).toHtml(prefs))
        }
        sb.append("</div><!-- zones -->\n\n")
        return sb.toString()
    }
}
