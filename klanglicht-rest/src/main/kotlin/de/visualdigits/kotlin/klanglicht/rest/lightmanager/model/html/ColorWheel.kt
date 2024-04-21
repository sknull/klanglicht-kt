package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html

import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ColorWheel(
    val id: String? = null
) : HtmlRenderable {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun toHtml(prefs: ApplicationPreferences): String {
        return toHtml(prefs, false)
    }

    fun toHtml(prefs: ApplicationPreferences, oddEven: Boolean): String {
        val wheelId = id!!.replace(" ", "")

        val sb = StringBuilder()

        if (oddEven) {
            sb.append("\t<div class=\"colorwheel-wrapper-oddeven\">\n")
            renderColorWheelPanel(sb, wheelId, true)
            renderColorWheelPanel(sb, wheelId, false)

            renderScript(sb, wheelId, true, prefs)
            sb.append("\t\t</div><!-- colorwheel-wrapper-oddeven -->\n")
        } else {
            sb.append("\t<div class=\"colorwheel-wrapper\">\n")
            renderColorWheelPanel(sb, wheelId, null)

            renderScript(sb, wheelId, false, prefs)
            sb.append("\t\t</div><!-- colorwheel-wrapper -->\n")
        }


        return sb.toString()
    }

    private fun renderColorWheelPanel(sb: StringBuilder, wheelId: String, odd: Boolean?) {
        sb.append("\t\t<div class=\"colorwheel-panel\">\n")
        if (odd != null) {
            sb.append("\t\t<div class=\"colorwheel-title\"><span class=\"label\">COLORPICKER - $id - ${if (odd) "Odd" else "Even"}</span></div>\n")
            sb.append("\t\t\t<div class=\"color-wheel\" id=\"colorwheel-${wheelId}${if (odd) "Odd" else "Even"}\"></div>\n")
        } else {
            sb.append("\t\t<div class=\"colorwheel-title\"><span class=\"label\">COLORPICKER - $id</span></div>\n")
            sb.append("\t\t\t<div class=\"color-wheel\" id=\"colorwheel-${wheelId}\"></div>\n")
        }
        sb.append("\t\t</div><!-- colorwheel-panel -->\n")
    }

    private fun renderScript(sb: StringBuilder, wheelId: String, oddEven: Boolean, prefs: ApplicationPreferences) {
        val ownUrl = prefs.preferences?.ownUrl
        if (oddEven) {
            val keys = prefs.preferences?.stage?.map { it.id }?:listOf()
            var colors = "colorOdd,colorEven,".repeat(keys.size / 2)
            if (keys.size % 2 > 0) colors += "colorOdd" else colors = colors.substring(0, keys.size - 1)
            colors = colors.split(",").joinToString(" + \",\" + ")
            sb.append("\t\t<script type=\"application/javascript\">\n")

            val hexColorOdd = prefs.getColor("${wheelId}Odd")?:"000000"
            sb.append("\t\t\tvar colorWheel${wheelId}Odd = new iro.ColorPicker(\"#colorwheel-${wheelId}Odd\", {\n")
            sb.append("\t\t\t\twheelLightness: false,\n")
            sb.append("\t\t\t\tcolor: \"${hexColorOdd}\"\n")
            sb.append("\t\t\t});\n\n")

            val hexColorEven = prefs.getColor("${wheelId}Even")?:"000000"
            sb.append("\t\t\tvar colorWheel${wheelId}Even = new iro.ColorPicker(\"#colorwheel-${wheelId}Even\", {\n")
            sb.append("\t\t\t\twheelLightness: false,\n")
            sb.append("\t\t\t\tcolor: \"${hexColorEven}\"\n")
            sb.append("\t\t\t});\n\n")

            sb.append("\t\t\tcolorWheel${wheelId}Odd.on('color:change', function(color, changes){\n")
            sb.append("\t\t\t\tvar colorOdd = colorWheel${wheelId}Odd.color.hexString.substring(1);\n")
            sb.append("\t\t\t\tvar colorEven = colorWheel${wheelId}Even.color.hexString.substring(1);\n")
            sb.append("\t\t\t\tfetch(\"$ownUrl/v1/hybrid/json/hexColor?hexColors=\" + $colors + \"&transition=0&\", {method: 'GET'}).catch(err => console.error(err));\n")
            sb.append("\t\t\t});\n\n")

            sb.append("\t\t\tcolorWheel${wheelId}Even.on('color:change', function(color, changes){\n")
            sb.append("\t\t\t\tvar colorOdd = colorWheel${wheelId}Odd.color.hexString.substring(1);\n")
            sb.append("\t\t\t\tvar colorEven = colorWheel${wheelId}Even.color.hexString.substring(1);\n")
            sb.append("\t\t\t\tfetch(\"$ownUrl/v1/hybrid/json/hexColor?hexColors=\" + $colors + \"&transition=0&\", {method: 'GET'}).catch(err => console.error(err));\n")
            sb.append("\t\t\t});\n\n")

            sb.append("\t\t</script>\n")
        } else {
            val hexColor = prefs.getFadeable(wheelId)
                ?.getRgbColor()?.web()
                ?:prefs.getColor(wheelId)
                ?:"000000"
            sb.append("\t\t<script type=\"application/javascript\">\n")

            sb.append("\t\t\tvar colorWheel$wheelId = new iro.ColorPicker(\"#colorwheel-$wheelId\", {\n")
            sb.append("\t\t\t\twheelLightness: false,\n")
            sb.append("\t\t\t\tcolor: \"${hexColor}\"\n")
            sb.append("\t\t\t});\n\n")

            sb.append("\t\t\tcolorWheel$wheelId.on('color:change', function(color, changes){\n")
            sb.append("\t\t\t\tvar colorOdd = colorWheel$wheelId.color.hexString.substring(1);\n")
            sb.append("\t\t\t\tvar colorEven = \"000000\";\n")
            val colorWheelDevices = prefs.preferences?.getColorWheel(wheelId)?.devices?.joinToString(",")
            sb.append("\t\t\t\tfetch(\"$ownUrl/v1/hybrid/json/hexColor?ids=$colorWheelDevices&hexColors=\" + colorOdd + \"&transition=0&storeName=$wheelId&\", {method: 'GET'}).catch(err => console.error(err));\n")
            sb.append("\t\t\t});\n")

            sb.append("\t\t</script>\n")
        }
    }
}
