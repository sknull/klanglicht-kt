package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.page

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMSceneGroup
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import org.springframework.stereotype.Service

@Service
class LMHtmlScenes(
    private val prefs: ApplicationPreferences
) {

    fun renderScenes(scenes: LMScenes): String {
        val sb = StringBuilder()
        sb.append("<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
            .append(scenes.name)
            .append("</div>\n")
        sb.append("<div class=\"center-category\">\n")
        sb.append("<span class=\"label\">").append("C U R R E N T   S C E N E").append("</span>\n")
        sb.append("<div class=\"center-group\">\n")
        prefs.currentScene?.fadeables()?.forEach { fadeable ->
            val color = fadeable.getRgbColor()?.web() ?: "#000000"
            val html = renderPanel(color)
            sb.append(html)
        }
        sb.append("</div><!-- sub-group -->\n\n")
        sb.append("</div><!-- current scene -->\n\n")

        sb.append("<div class=\"category\">\n")
        sb.append("<span class=\"label\">").append("S C E N E S").append("</span>\n")
        scenes.scenes.values.forEach { sceneGroup ->
            val html = renderScenesGroup(
                sceneGroup
            )
            sb.append(html)
        }
        sb.append("</div><!-- scenes -->\n\n")
        return sb.toString()
    }

    private fun renderPanel(bgColor: String): String {
        val sb = StringBuilder()
        sb.append("      <div class=\"circle\" style=\"background-color:").append(bgColor)
            .append("\" >\n")
//        if (!value.isNullOrEmpty()) {
//            sb.append("<span class=\"label\">").append(value).append("</span>\n")
//        }
        sb.append("      </div> <!-- circle -->\n")
        return sb.toString()
    }

    private fun renderScenesGroup(
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
        sb.append("<span class=\"label\">").append(sceneGroup.name).append("</span>\n")
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
            val sceneColor = scene.color.joinToString(",")
            val label = if (scene.name.lowercase().startsWith(sceneGroup.name.lowercase())) {
                scene.name.substring(sceneGroup.name.length).trim { it <= ' ' }
            } else scene.name
            sb.append(renderButton(scene.name, label, sceneColor))
        }
        sb.append("    </div><!-- sub-group -->\n")
        sb.append("  </div><!-- group -->\n")
        if (sceneGroup.hasColorWheel) {
            val html: String = renderColorWheel(sceneGroup.name, sceneGroup.colorWheelOddEven)
            sb.append(html)
        }
        return sb.toString()
    }

    private fun renderButton(name: String, label: String, sceneColor: String): String {
        val url = prefs.preferences?.ownUrl
        val sb = StringBuilder()
        sb.append("      <div class=\"button\"")
        if (sceneColor.contains(",")) {
            sb.append(" style=\"background: -moz-linear-gradient(left, ")
                .append(sceneColor)
                .append("); background: -webkit-linear-gradient(left, ")
                .append(sceneColor)
                .append("); background: linear-gradient(to right, ")
                .append(sceneColor)
                .append(");\"")
        } else {
            sb.append(" style=\"background-color: ")
                .append(sceneColor)
                .append(";\"")
        }
        sb.append("><input type=\"submit\" value=\"")
            .append(label)
            .append("\" onclick=\"request('")
            .append(url)
            .append("/v1/scenes/json/control?name=")
            .append(name)
            .append("');\"/></div>\n")
        return sb.toString()
    }

    private fun renderColorWheel(id: String, oddEven: Boolean): String {
        val wheelId = id.replace(" ", "")
        val sb = StringBuilder()
        if (oddEven) {
            sb.append("\t<div class=\"colorwheel-wrapper-oddeven\">\n")
            sb.append(renderColorWheelPanel(id, wheelId, true))
            sb.append(renderColorWheelPanel(id, wheelId, false))

            sb.append(renderScript(wheelId, true))
            sb.append("\t\t</div><!-- colorwheel-wrapper-oddeven -->\n")
        } else {
            sb.append("\t<div class=\"colorwheel-wrapper\">\n")
            sb.append(renderColorWheelPanel(id, wheelId, null))

            sb.append(renderScript(wheelId, false))
            sb.append("\t\t</div><!-- colorwheel-wrapper -->\n")
        }
        return sb.toString()
    }

    private fun renderColorWheelPanel(id: String, wheelId: String, odd: Boolean?): String {
        val sb = StringBuilder()
        sb.append("\t\t<div class=\"colorwheel-panel\">\n")
        if (odd != null) {
            sb.append("\t\t<div class=\"colorwheel-title\"><span class=\"label\">COLORPICKER - $id - ${if (odd) "Odd" else "Even"}</span></div>\n")
            sb.append("\t\t\t<div class=\"color-wheel\" id=\"colorwheel-${wheelId}${if (odd) "Odd" else "Even"}\"></div>\n")
        } else {
            sb.append("\t\t<div class=\"colorwheel-title\"><span class=\"label\">COLORPICKER - $id</span></div>\n")
            sb.append("\t\t\t<div class=\"color-wheel\" id=\"colorwheel-${wheelId}\"></div>\n")
        }
        sb.append("\t\t</div><!-- colorwheel-panel -->\n")
        return sb.toString()
    }

    private fun renderScript(wheelId: String, oddEven: Boolean): String {
        val sb = StringBuilder()
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
        return sb.toString()
    }
}
