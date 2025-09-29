package de.visualdigits.klanglicht.lightmanager.model.html.page.zones

import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMZone
import de.visualdigits.klanglicht.lightmanager.model.html.page.components.LMHtml

class LMHtmlZone(
    val zone: LMZone
) : LMHtml {

    override fun html(indent: Int): String {
        val sindent = "  ".repeat(indent)
        val sb = StringBuilder()
        sb.append("$sindent<div class=\"group\">\n")
        sb.append("$sindent  <span class=\"label\">${zone.name}</span>\n")
        sb.append("$sindent  <div class=\"sub-group\">\n")
        zone.actors.forEach { actor ->
            sb.append(LMHtmlZoneActor(actor).html(indent + 2))
        }
        sb.append("$sindent  </div><!-- sub-group -->\n")
        sb.append("$sindent</div><!-- group -->\n")

        return sb.toString()
    }
}
