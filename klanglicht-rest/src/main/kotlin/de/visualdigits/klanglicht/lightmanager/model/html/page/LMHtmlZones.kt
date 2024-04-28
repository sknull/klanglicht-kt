package de.visualdigits.klanglicht.lightmanager.model.html.page

import de.visualdigits.klanglicht.hardware.lightmanager.model.lm.LMDefaultRequest
import de.visualdigits.klanglicht.hardware.lightmanager.model.lm.LMRequest
import de.visualdigits.klanglicht.hardware.lightmanager.model.lm.LMZone
import de.visualdigits.klanglicht.lightmanager.service.LightmanagerService
import org.springframework.stereotype.Service

@Service
class LMHtmlZones(
    private val lightmanagerService: LightmanagerService
) : LMHtml() {

    fun renderZones(): String {
        val sb = StringBuilder()
        val zones = lightmanagerService.zones()
        sb.append("<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
            .append(zones.name)
            .append("</div>\n")
        sb.append("<div class=\"category\">\n")
        sb.append("<span class=\"label\">").append("Z O N E S").append("</span>\n")
        zones.zones.forEach { zone ->
            sb.append(renderZone(zone))
        }
        sb.append("</div><!-- zones -->\n\n")
        return sb.toString()
    }

    private fun renderZone(zone: LMZone): String {
        val sb = StringBuilder()
        sb.append("  <div class=\"group\">\n")
        sb.append("<span class=\"label\">${zone.name}</span>\n")
        sb.append("    <div class=\"sub-group\">\n")
        zone.actors.forEach { actor ->
            sb.append("      <div class=\"panel\">\n")
            sb.append("<span class=\"label\">").append(actor.name).append("</span>\n")
            var markerIsOn = false
            var colorOff = ""
            var colorOn = ""
            val marker = actor.markers["unified"]
            if (actor.markers.containsKey("unified")) {
                markerIsOn = marker?.state?:false
                colorOff = marker?.colorOff?:""
                colorOn = marker?.colorOn?:""
            }
            if (colorOff.isEmpty()) {
                colorOff = colorOff.ifEmpty { LightmanagerService.COLOR_OFF }
            }
            if (colorOn.isEmpty()) {
                colorOn = colorOn.ifEmpty { LightmanagerService.COLOR_ON }
            }
            val lmRequests = actor.requests.values.toList()
            if (lmRequests.isNotEmpty() && lmRequests.first() is LMDefaultRequest) {
                val rq: LMRequest = determineRequest(lmRequests, markerIsOn)
                if (rq is LMDefaultRequest) {
                    sb.append(
                        renderButton(
                            value = rq.name ?: "",
                            url = "request('${rq.uri}', '${rq.type?.name}')",
                            color = if (markerIsOn) colorOn else colorOff
                        )
                    )
                }
            }
            sb.append("      </div><!-- actor -->\n")
        }
        sb.append("    </div><!-- sub-group -->\n")
            .append("  </div><!-- group -->\n")
        return sb.toString()
    }

    private fun determineRequest(lmRequests: List<LMRequest>, markerIsOn: Boolean): LMRequest {
        val rq0 = lmRequests.first() as LMDefaultRequest
        val smkState0 = rq0.hasSmk() && rq0.smk?.get(1) == 1
        val rq = if (markerIsOn && !smkState0 || !markerIsOn && smkState0) {
            rq0
        } else if (lmRequests.size > 1) {
            lmRequests[1]
        } else {
            rq0
        }
        return rq
    }

}
