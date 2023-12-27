package de.visualdigits.kotlin.klanglicht.rest.lightmanager.widgets

import de.visualdigits.kotlin.klanglicht.rest.common.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.HtmlRenderable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ColorWheel(
    val id: String? = null
) : HtmlRenderable {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun toHtml(configHolder: ConfigHolder): String {
        val wheelId = id!!.replace(" ", "")
        val lastColorState = configHolder.getLastColor(id)
        val hexColor = lastColorState.hexColor?:"#000000"
        log.info("Got color '${RGBColor(hexColor).ansiColor()}' for id '$id'")
        return "    <div class=\"colorwheel-wrapper\">\n" +
                "\t\t<div class=\"colorwheel-title\"><span class=\"label\">COLORPICKER - " + id + "</span></div>\n" +
                "\t\t<div class=\"colorwheel-panel\">\n" +
                "\t\t\t<div class=\"color-wheel\" id=\"colorwheel-" + wheelId + "\"></div>\n" +
                "\t\t</div>\n" +
                "        <script type=\"application/javascript\">\n" +
                "            var colorWheel" + wheelId + " = new iro.ColorPicker(\"#colorwheel-" + wheelId + "\", {\n" +
                "                wheelLightness: false,\n" +
                "                color: \"" + hexColor + "\"\n" +
                "            });\n" +
                "\n" +
                "            colorWheel" + wheelId + ".on('color:change', function(color, changes){\n" +
                "                var colorOdd = colorWheel" + wheelId + ".color.hexString.substring(1);\n" +
                "                var colorEven = \"000000\";\n" +
                "                Colors" + wheelId + " = colorOdd, colorEven;\n" +
                "            });\n" +
                "        </script>\n" +
                "    </div>"
    }
}
