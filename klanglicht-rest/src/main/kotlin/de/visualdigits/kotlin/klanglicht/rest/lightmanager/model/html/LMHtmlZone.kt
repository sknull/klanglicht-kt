package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZone
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder

class LMHtmlZone(
    val zone: LMZone
): HtmlRenderable {

    override fun toHtml(prefs: ApplicationPreferences, configHolder: ConfigHolder?): String {
        val sb = StringBuilder()
        sb.append("  <div class=\"group\">\n")
        renderLabel(sb, zone.name)
        sb.append("    <div class=\"sub-group\">\n")
        zone.actors.forEach { actor ->
            sb.append(LMHtmlActor(actor).toHtml(prefs, configHolder))
        }
        sb.append("    </div><!-- sub-group -->\n")
            .append("  </div><!-- group -->\n")
        return sb.toString()
    }
}
