package de.visualdigits.klanglicht.lightmanager.model.html.page.zones

import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActor
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMDefaultRequest
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMRequest
import de.visualdigits.klanglicht.lightmanager.model.html.page.components.LMHtml
import de.visualdigits.klanglicht.lightmanager.model.html.page.components.LMHtmlButton
import de.visualdigits.klanglicht.lightmanager.service.LightmanagerService

class LMHtmlZoneActor(
    val actor: LMActor
) : LMHtml {

    override fun html(indent: Int): String {
        val sindent = "  ".repeat(indent)
        val sb = StringBuilder()
        sb.append("$sindent<div class=\"panel\">\n")
        sb.append("$sindent  <span class=\"label\">").append(actor.name).append("</span>\n")
        var markerIsOn = false
        var colorOff = ""
        var colorOn = ""
        val marker = actor.markers["unified"]
        if (actor.markers.containsKey("unified")) {
            markerIsOn = marker?.state == true
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
                    LMHtmlButton(
                        label = rq.name ?: "",
                        href = "request('${rq.uri}', '${rq.type?.name}')",
                        color = if (markerIsOn) colorOn else colorOff
                    ).html(indent + 1)
                )
            }
        }
        sb.append("$sindent</div><!-- actor -->\n")

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
