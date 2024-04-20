package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html

import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ColorWheel(
    val id: String? = null
) : HtmlRenderable {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun toHtml(configHolder: ConfigHolder?): String {
        return toHtml(configHolder, false)
    }

    fun toHtml(configHolder: ConfigHolder?, oddEven: Boolean): String {
        val wheelId = id!!.replace(" ", "")

        val sb = StringBuilder()

        if (oddEven) {
            sb.append("\t<div class=\"colorwheel-wrapper-oddeven\">\n")
            renderColorWheelPanel(sb, wheelId, true)
            renderColorWheelPanel(sb, wheelId, false)

            renderScript(sb, wheelId, true, configHolder)
            sb.append("\t\t</div><!-- colorwheel-wrapper-oddeven -->\n")
        } else {
            sb.append("\t<div class=\"colorwheel-wrapper\">\n")
            renderColorWheelPanel(sb, wheelId, null)

            renderScript(sb, wheelId, false, configHolder)
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

    private fun renderScript(sb: StringBuilder, wheelId: String, oddEven: Boolean, configHolder: ConfigHolder?) {
        if (oddEven) {
            sb.append("\t\t<script type=\"application/javascript\">\n")

            val hexColorOdd = configHolder?.getColor("${wheelId}Odd")?:"000000"
            sb.append("\t\t\tvar colorWheel${wheelId}Odd = new iro.ColorPicker(\"#colorwheel-${wheelId}Odd\", {\n")
            sb.append("\t\t\t\twheelLightness: false,\n")
            sb.append("\t\t\t\tcolor: \"${hexColorOdd}\"\n")
            sb.append("\t\t\t});\n\n")

            val hexColorEven = configHolder?.getColor("${wheelId}Even")?:"000000"
            sb.append("\t\t\tvar colorWheel${wheelId}Even = new iro.ColorPicker(\"#colorwheel-${wheelId}Even\", {\n")
            sb.append("\t\t\t\twheelLightness: false,\n")
            sb.append("\t\t\t\tcolor: \"${hexColorEven}\"\n")
            sb.append("\t\t\t});\n\n")

            sb.append("\t\t\tcolorWheel${wheelId}Odd.on('color:change', function(color, changes){\n")
            sb.append("\t\t\t\tvar colorOdd = colorWheel${wheelId}Odd.color.hexString.substring(1);\n")
            sb.append("\t\t\t\tvar colorEven = colorWheel${wheelId}Even.color.hexString.substring(1);\n")
            sb.append("\t\t\t\tsetColors${wheelId}OddEven(colorOdd, colorEven);\n")
            sb.append("\t\t\t});\n\n")

            sb.append("\t\t\tcolorWheel${wheelId}Even.on('color:change', function(color, changes){\n")
            sb.append("\t\t\t\tvar colorOdd = colorWheel${wheelId}Odd.color.hexString.substring(1);\n")
            sb.append("\t\t\t\tvar colorEven = colorWheel${wheelId}Even.color.hexString.substring(1);\n")
            sb.append("\t\t\t\tsetColors${wheelId}OddEven(colorOdd, colorEven);\n")
            sb.append("\t\t\t});\n\n")

            sb.append("\t\t</script>\n")
        } else {
            val hexColor = configHolder?.getFadeable(wheelId)
                ?.getRgbColor()?.web()
                ?:configHolder?.getColor(wheelId)
                ?:"000000"
            sb.append("\t\t<script type=\"application/javascript\">\n")

            sb.append("\t\t\tvar colorWheel${wheelId} = new iro.ColorPicker(\"#colorwheel-${wheelId}\", {\n")
            sb.append("\t\t\t\twheelLightness: false,\n")
            sb.append("\t\t\t\tcolor: \"${hexColor}\"\n")
            sb.append("\t\t\t});\n\n")

            sb.append("\t\t\tcolorWheel${wheelId}.on('color:change', function(color, changes){\n")
            sb.append("\t\t\t\tvar colorOdd = colorWheel${wheelId}.color.hexString.substring(1);\n")
            sb.append("\t\t\t\tvar colorEven = \"000000\";\n")
            sb.append("\t\t\t\tsetColors${wheelId}(colorOdd, colorEven);\n")
            sb.append("\t\t\t});\n")

            sb.append("\t\t</script>\n")
        }
    }
}
