package de.visualdigits.klanglicht.shelly.model.html

import de.visualdigits.klanglicht.hardware.shelly.model.ShellyDevice
import de.visualdigits.klanglicht.hardware.shelly.model.status.Status
import de.visualdigits.klanglicht.shelly.service.ShellyService
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import org.springframework.stereotype.Service

@Service
class ShellyStatus(
    private val shellyService: ShellyService
) {

    fun renderShellyStatus(): String {
        val sb = StringBuilder()
        sb.append("<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
            .append("Current Power Values")
            .append("</div>\n")
        sb.append("<div class=\"category\">\n")

        sb.append("<span class=\"label\">").append("Shelly Dashboard").append("</span>\n")
        shellyService.status().forEach { (device: ShellyDevice, status: Status) ->
            sb.append("  <div class=\"floatgroup\">\n")
            sb.append("<span class=\"label\">").append(device.name).append("</span>\n")
            sb.append("    <div class=\"sub-group\">\n")
            val powerStatus = renderPowerStatus(status, sb)
            renderOnOffStatus(powerStatus, status, sb)
            sb.append("    </div> <!-- sub-group -->\n")
            sb.append("  </div> <!-- group -->\n") // group
        }
        sb.append("</div > <!-- category -->\n") // category

        return sb.toString()
    }

    private fun renderOnOffStatus(
        powerStatus: Pair<String, Boolean>,
        status: Status,
        sb: StringBuilder
    ) {
        val lightColors: MutableList<String> = ArrayList()
        var isOn = false
        var bgColor = powerStatus.first
        val isOnline = powerStatus.second
        if (isOnline) {
            val relays = status.relays
            if (relays != null) {
                isOn = relays.any { it.isOn == true }
                bgColor = if (isOn) "red" else "green"
            } else {
                val lights = status.lights
                if (lights != null) {
                    lights.forEach { light ->
                        lightColors.add(RGBColor(light.red!!, light.green!!, light.blue!!).web())
                        if (light.isOn == true) {
                            isOn = true
                        }
                    }
                    bgColor = if (isOn) "red" else "green"
                } else {
                    bgColor = "#ff00ff"
                }
            }
        }
        renderPanel(sb, "circle", bgColor, if (isOn) "on" else "off")
        lightColors.forEach { lightColor ->
            renderPanel(sb, "circle", lightColor, "")
        }
    }

    private fun renderPowerStatus(
        status: Status,
        sb: StringBuilder
    ): Pair<String, Boolean> {
        var power = listOf(0.0)
        var bgColor = "#aaaaaa"
        val isOnline = "offline" != status.mode
        if (isOnline) {
            power = status.meters?.map { it.power ?: 0.0 } ?: listOf(0.0)
            val totalPower = power.reduce { a, b -> a + b }
            bgColor = if (totalPower > 0.0) "red" else "green"
        }
        renderPanel(sb, "textpanel", bgColor, "Power&nbsp;$power")
        return Pair(bgColor, isOnline)
    }

    private fun renderPanel(sb: StringBuilder, clazz: String, bgColor: String, value: String?) {
        sb.append("      <div class=\"").append(clazz).append("\" style=\"background-color:").append(bgColor)
            .append("\" >\n")
        if (!value.isNullOrEmpty()) {
            sb.append("<span class=\"label\">").append(value).append("</span>\n")
        }
        sb.append("      </div> <!-- ").append(clazz).append(" -->\n")
    }
}
