package de.visualdigits.klanglicht.hardware.lightmanager.model.html.page.zones

import de.visualdigits.klanglicht.hardware.lightmanager.model.html.page.components.LMHtml
import de.visualdigits.klanglicht.hardware.lightmanager.service.LightmanagerService
import kotlin.math.max

class LMHtmlZones(
    val lightmanagerService: LightmanagerService
) : LMHtml {

    override fun html(indent: Int): String {
        val sindent = "  ".repeat(indent)
        val sb = StringBuilder()
        val zones = lightmanagerService.zones()
        sb.append("$sindent<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
            .append(zones.name)
            .append("</div>\n")
        sb.append("$sindent  <div class=\"category\">\n")
        sb.append("$sindent  <span class=\"label\">").append("Z O N E S").append("</span>\n")
        zones.zones.forEach { zone ->
            sb.append(LMHtmlZone(zone).html(indent + 2))
        }
        sb.append("$sindent</div><!-- zones -->\n\n")
        sb.append("  ".repeat(max(0, indent - 1)))

        return sb.toString()
    }
}
