package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.page

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMSceneGroup
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class LMHtmlScenes(
    private val prefs: ApplicationPreferences
) {

    @Value("\${server.port}")
    private var port: Int = 0

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
        val baseUrl = prefs.preferences?.baseUrl?.let{ "$it:$port" }?:"http://localhost:$port"
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
            .append(baseUrl)
            .append("/v1/scenes/json/control?name=")
            .append(name)
            .append("');\"/></div>\n")
        return sb.toString()
    }

    private fun renderColorWheel(id: String, oddEven: Boolean): String {
        val baseUrl = prefs.preferences?.baseUrl?.let{ "$it:$port" }?:"http://localhost:$port"
        val wheelId = id.replace(" ", "")
        val sb = StringBuilder()
        if (oddEven) {
            sb.append("\t<div class=\"colorwheel-wrapper-oddeven\">\n")
            sb.append(renderColorWheelPanel(id, wheelId, true))
            sb.append(renderColorWheelPanel(id, wheelId, false))

            sb.append(renderScriptOddEven(wheelId, baseUrl))
            sb.append("\t\t</div><!-- colorwheel-wrapper-oddeven -->\n")
        } else {
            sb.append("\t<div class=\"colorwheel-wrapper\">\n")
            sb.append(renderColorWheelPanel(id, wheelId, null))

            sb.append(renderScriptStandalone(wheelId, baseUrl))
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

    private fun renderScriptOddEven(wheelId: String, baseUrl: String): String {
        val keys = prefs.preferences?.stage?.map { it.id }?:listOf()
        var colors = "colorOdd,colorEven,".repeat(keys.size / 2)
        if (keys.size % 2 > 0) colors += "colorOdd" else colors = colors.substring(0, keys.size - 1)
        colors = colors.split(",").joinToString(" + \",\" + ")
        val hexColorOdd = prefs.getColor("${wheelId}Odd")?:"000000"
        val hexColorEven = prefs.getColor("${wheelId}Even")?:"000000"

        return """
            <script type="application/javascript">
              var colorWheel${wheelId}Odd = new iro.ColorPicker("#colorwheel-${wheelId}Odd", {
                wheelLightness: false,
                color: "$hexColorOdd"
              });
              
              var colorWheel${wheelId}Even = new iro.ColorPicker("#colorwheel-${wheelId}Even", {
                wheelLightness: false,
                color: "$hexColorEven"
              });
              
              colorWheel${wheelId}Odd.on('color:change', function(color, changes){
                var colorOdd = colorWheel${wheelId}Odd.color.hexString.substring(1);
                var colorEven = colorWheel${wheelId}Even.color.hexString.substring(1);
                fetch("$baseUrl/v1/hybrid/json/hexColor?hexColors=" + $colors + "&transition=0&", {method: 'GET'}).catch(err => console.error(err));
              });
              
              colorWheel${wheelId}Even.on('color:change', function(color, changes){
                var colorOdd = colorWheel${wheelId}Odd.color.hexString.substring(1);
                var colorEven = colorWheel${wheelId}Even.color.hexString.substring(1);
                fetch("$baseUrl/v1/hybrid/json/hexColor?hexColors=" + $colors + "&transition=0&", {method: 'GET'}).catch(err => console.error(err));
              });
            </script>
        """.trimIndent()
    }

    private fun renderScriptStandalone(wheelId: String, baseUrl: String): String {
        val hexColor = prefs.getFadeable(wheelId)
            ?.getRgbColor()?.web()
            ?:prefs.getColor(wheelId)
            ?:"000000"
        val colorWheelDevices = prefs.preferences?.getColorWheel(wheelId)?.devices?.joinToString(",")

        return """
            <script type="application/javascript">
              var colorWheel$wheelId = new iro.ColorPicker("#colorwheel-$wheelId", {
                wheelLightness: false,
                color: "$hexColor"
              });
              
              colorWheel$wheelId.on('color:change', function(color, changes){
                var colorOdd = colorWheel$wheelId.color.hexString.substring(1);
                var colorEven = "000000";
                fetch("$baseUrl/v1/hybrid/json/hexColor?ids=$colorWheelDevices&hexColors=" + colorOdd + "&transition=0&storeName=$wheelId&", {method: 'GET'}).catch(err => console.error(err));
              });
            </script>
        """.trimIndent()
    }
}
