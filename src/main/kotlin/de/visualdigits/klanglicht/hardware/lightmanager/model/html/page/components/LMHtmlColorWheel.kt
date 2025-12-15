package de.visualdigits.klanglicht.hardware.lightmanager.model.html.page.components

import de.visualdigits.klanglicht.configuration.ApplicationPreferences

class LMHtmlColorWheel(
    val prefs: ApplicationPreferences,
    val name: String,
    val displayName: String,
    val oddEven: Boolean
) : LMHtml {

    override fun html(indent: Int): String {
        val sindent = "  ".repeat(indent)
        val wheelId = name.replace(" ", "")
        val sb = StringBuilder()
        if (oddEven) {
            sb.append("$sindent<div class=\"colorwheel-wrapper-oddeven\">\n")
            sb.append(renderColorWheelPanel(displayName, wheelId, true, indent + 1))
            sb.append(renderColorWheelPanel(displayName, wheelId, false, indent + 1))
            sb.append(renderScriptOddEven(wheelId, indent + 1))
            sb.append("$sindent</div><!-- colorwheel-wrapper-oddeven -->\n")
        } else {
            sb.append("$sindent<div class=\"colorwheel-wrapper\">\n")
            sb.append(renderColorWheelPanel(displayName, wheelId, null, indent + 1))
            sb.append(renderScriptStandalone(wheelId, indent + 1))
            sb.append("$sindent</div><!-- colorwheel-wrapper -->\n")
        }
        return sb.toString()
    }

    private fun renderColorWheelPanel(displayName: String, wheelId: String, odd: Boolean?, indent: Int): String {
        val sindent = "  ".repeat(indent)
        val sb = StringBuilder()
        sb.append("$sindent<div class=\"colorwheel-panel\">\n")
        if (odd != null) {
            sb.append("$sindent  <div class=\"colorwheel-title\"><span class=\"label\">COLORPICKER - $displayName - ${if (odd) "Odd" else "Even"}</span></div>\n")
            sb.append("$sindent  <div class=\"color-wheel\" id=\"colorwheel-${wheelId}${if (odd) "Odd" else "Even"}\"></div>\n")
        } else {
            sb.append("$sindent  <div class=\"colorwheel-title\"><span class=\"label\">COLORPICKER - $displayName</span></div>\n")
            sb.append("$sindent  <div class=\"color-wheel\" id=\"colorwheel-${wheelId}\"></div>\n")
        }
        sb.append("$sindent</div><!-- colorwheel-panel -->\n")
        return sb.toString()
    }

    private fun renderScriptOddEven(wheelId: String, indent: Int): String {
        val sindent = "  ".repeat(indent)
        val keys = prefs.stage?.devices?.stage?.map { it.id }?: listOf()
        var colors = "colorOdd,colorEven,".repeat(keys.size / 2)
        if (keys.size % 2 > 0) colors += "colorOdd" else colors = colors.substring(0, keys.size - 1)

        val currentColorOdd = prefs.getColor("${wheelId}Odd")?:"000000"
        val currentColorEven = prefs.getColor("${wheelId}Even")?:"000000"

        return "$sindent<script type=\"application/javascript\">\n" +
                "$sindent  var colorWheel${wheelId}Odd = new iro.ColorPicker(\"#colorwheel-${wheelId}Odd\", {\n" +
                "$sindent    wheelLightness: false,\n" +
                "$sindent    color: \"$currentColorOdd\"\n" +
                "$sindent  });\n" +
                "$sindent\n" +
                "$sindent  var colorWheel${wheelId}Even = new iro.ColorPicker(\"#colorwheel-${wheelId}Even\", {\n" +
                "$sindent    wheelLightness: false,\n" +
                "$sindent    color: \"$currentColorEven\"\n" +
                "$sindent  });\n" +
                "$sindent\n" +
                "$sindent  colorWheel${wheelId}Odd.on('color:change', function(color, changes){\n" +
                "$sindent    var colorOdd = colorWheel${wheelId}Odd.color.hexString.substring(1);\n" +
                "$sindent    var colorEven = colorWheel${wheelId}Even.color.hexString.substring(1);\n" +
                "$sindent    fetch(\"${prefs.baseUrl}:${prefs.port}/v1/hybrid/json/hexColor?wheelId=$wheelId&hexColors=\" + ${colors.replace(",", " + \",\" + ")} + \"&transition=0&storeName=${wheelId}Odd&\", {method: 'GET'}).catch(err => console.error(err));\n" +
                "$sindent  });\n" +
                "$sindent\n" +
                "$sindent  colorWheel${wheelId}Even.on('color:change', function(color, changes){\n" +
                "$sindent    var colorOdd = colorWheel${wheelId}Odd.color.hexString.substring(1);\n" +
                "$sindent    var colorEven = colorWheel${wheelId}Even.color.hexString.substring(1);\n" +
                "$sindent    fetch(\"${prefs.baseUrl}:${prefs.port}/v1/hybrid/json/hexColor?wheelId=$wheelId&hexColors=\" + ${colors.replace(",", " + \",\" + ")} + \"&transition=0&storeName=${wheelId}Even&\", {method: 'GET'}).catch(err => console.error(err));\n" +
                "$sindent  });\n" +
                "$sindent</script>\n"
    }

    private fun renderScriptStandalone(wheelId: String, indent: Int): String {
        val sindent = "  ".repeat(indent)
        val currentColor = prefs.getColor(wheelId)?:"000000"
        val colorWheelDevices = prefs.stage?.devices?.colorWheelMap?.get(wheelId)?.devices?.joinToString(",")

        return "$sindent<script type=\"application/javascript\">\n" +
            "$sindent  var colorWheel$wheelId = new iro.ColorPicker(\"#colorwheel-$wheelId\", {\n" +
            "$sindent    wheelLightness: false,\n" +
            "$sindent    color: \"$currentColor\"\n" +
            "$sindent  });\n" +
            "$sindent\n" +
            "$sindent  colorWheel$wheelId.on('color:change', function(color, changes){\n" +
            "$sindent    var colorOdd = colorWheel$wheelId.color.hexString.substring(1);\n" +
            "$sindent    var colorEven = \"000000\";\n" +
            "$sindent    fetch(\"${prefs.baseUrl}:${prefs.port}/v1/hybrid/json/hexColor?wheelId=$wheelId&ids=$colorWheelDevices&hexColors=\" + colorOdd + \"&transition=0&storeName=$wheelId&\", {method: 'GET'}).catch(err => console.error(err));\n" +
            "$sindent  });\n" +
            "$sindent</script>\n"
    }
}
