package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.json.RequestType
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActor
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMDefaultRequest
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMRequest
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZone
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.service.LightmanagerService
import org.springframework.stereotype.Service

@Service
class LMHtmlZones(
    private val prefs: ApplicationPreferences,
    private val lightmanagerService: LightmanagerService
) {

    fun renderZones(): String {
        val sb = StringBuilder()
        val zones = lightmanagerService.zones()
        sb.append("<div class=\"title\" onclick=\"toggleFullScreen();\" title=\"Toggle Fullscreen\">")
            .append(zones.name)
            .append("</div>\n")
        sb.append("<div class=\"category\">\n")
        sb.append("<span class=\"label\">").append("Z O N E S").append("</span>\n")
        zones.zones.forEach { zone ->
            sb.append(renderZone(zone, prefs))
        }
        sb.append("</div><!-- zones -->\n\n")
        return sb.toString()
    }

    private fun renderZone(zone: LMZone, prefs: ApplicationPreferences): String {
        val sb = StringBuilder()
        sb.append("  <div class=\"group\">\n")
        sb.append("<span class=\"label\">").append(zone.name).append("</span>\n")
        sb.append("    <div class=\"sub-group\">\n")
        zone.actors.forEach { actor ->
            sb.append(renderActor(actor, prefs))
        }
        sb.append("    </div><!-- sub-group -->\n")
            .append("  </div><!-- group -->\n")
        return sb.toString()
    }

    fun renderActor(actor: LMActor, prefs: ApplicationPreferences): String {
        val sb = StringBuilder()
        sb.append("      <div class=\"panel\">\n")
        sb.append("<span class=\"label\">").append(actor.name).append("</span>\n")
        sb.append(renderRequests(actor))
        // fixme - yet not working reliable enough
//        renderSlider(sb, prefs, configHolder);
        sb.append("      </div><!-- actor -->\n")
        return sb.toString()
    }

    private fun renderRequests(actor: LMActor): String {
        val sb = StringBuilder()
        var markerIsOn: Boolean? = false
        var hasSeparateMarkers: Boolean? = false
        var isSeparate: Boolean? = false
        var colorOff: String? = ""
        var colorOn: String? = ""
        if (actor.markers.containsKey("unified")) {
            val marker = actor.markers["unified"]
            markerIsOn = marker?.state
            colorOff = marker?.colorOff
            colorOn = marker?.colorOn
            isSeparate = marker?.separate
        } else if (actor.markers.isNotEmpty()) {
            hasSeparateMarkers = true
        }
        if (colorOff?.isEmpty() == true) {
            colorOff =
                colorOff.ifEmpty { LightmanagerService.COLOR_OFF }
        }
        if (colorOn?.isEmpty() == true) {
            colorOn = colorOn.ifEmpty { LightmanagerService.COLOR_ON }
        }
        val lmRequests = actor.requests.values.toList()
        if (lmRequests.isNotEmpty() && lmRequests.first() is LMDefaultRequest) {
            if (hasSeparateMarkers == true || isSeparate == true) {
                sb.append("        <div class=\"double-button\">\n")
                //                Collections.reverse(lmRequests);
                for (request in lmRequests) {
                    val marker = actor.markers[(request as LMDefaultRequest).name]
                    if (marker != null) {
                        colorOff = marker.colorOff
                        colorOn = marker.colorOn
                        markerIsOn = marker.state
                    }
                    sb.append(renderRequest(
                        styleClass = "half-button",
                        markerIsOn = markerIsOn,
                        colorOff = colorOff,
                        colorOn = colorOn,
                        rq = request,
                        unified = false,
                        isSeparate = isSeparate,
                        hasSeparateMarkers = hasSeparateMarkers
                    ))
                }
                sb.append("        </div><!-- double-button -->\n")
            } else {
                val rq0 = lmRequests.first() as LMDefaultRequest
                val smkState0 = determineSmkState(rq0)
                val rq: LMRequest = if (markerIsOn == true && !smkState0 || markerIsOn != true && smkState0) {
                    rq0
                } else if (lmRequests.size > 1) {
                    lmRequests[1]
                } else {
                    rq0
                }
                sb.append(renderRequest(
                    styleClass = "button",
                    markerIsOn = markerIsOn,
                    colorOff = colorOff,
                    colorOn = colorOn,
                    rq = rq,
                    unified = true,
                    isSeparate = false,
                    hasSeparateMarkers = false
                ))
            }
        }
        return sb.toString()
    }

    private fun renderRequest(
        styleClass: String,
        markerIsOn: Boolean?,
        colorOff: String?,
        colorOn: String?,
        rq: LMRequest?,
        unified: Boolean?,
        isSeparate: Boolean?,
        hasSeparateMarkers: Boolean?
    ): String {
        val sb = StringBuilder()
        if (rq is LMDefaultRequest) {
            val isOn = determineSmkState(rq)
            sb.append("        <div class=\"")
                .append(styleClass)
                .append("\"")
            if (isSeparate == true) {
                if (isOn) {
                    sb.append(" style=\"background-color: ").append(colorOn).append(";\"")
                } else {
                    sb.append(" style=\"background-color: ").append(colorOff).append(";\"")
                }
            } else if (hasSeparateMarkers == true) {
                sb.append(" style=\"background-color: ").append(colorOn).append(";\"")
            } else if (markerIsOn == true && (isOn || unified == true)) {
                sb.append(" style=\"background-color: ").append(colorOn).append(";\"")
            } else {
                if (colorOff?.contains(",") == true) {
                    sb.append(" style=\"background: -moz-linear-gradient(left, ")
                        .append(colorOff)
                        .append("); background: -webkit-linear-gradient(left, ")
                        .append(colorOff)
                        .append("); background: linear-gradient(to right, ")
                        .append(colorOff)
                        .append(");\"")
                } else {
                    sb.append(" style=\"background-color: ")
                        .append(colorOff)
                        .append(";\"")
                }
            }
            sb.append("><input")
            if (hasSeparateMarkers == true) {
                if (markerIsOn == true) {
                    sb.append(" class='on'")
                } else {
                    sb.append(" class='off'")
                }
            } else if (isSeparate == true) {
                if (isOn) {
                    sb.append(" class='on'")
                } else {
                    sb.append(" class='off'")
                }
            }
            sb.append(" type=\"submit\" value=\"")
                .append(rq.name)
                .append("\" onclick=\"request('")
                .append(rq.uri)
                .append("'")
            val type = rq.type
            if (type == RequestType.GET || type == RequestType.PUT || type == RequestType.POST) {
                sb.append(",'")
                    .append(type.name)
                    .append("'")
            }
            sb.append(");\"/></div>\n")
        }
        //                        else if (rq instanceof LMCamRequest) {
//                            LMCamRequest crq = (LMCamRequest) rq;
//                        }
        return sb.toString()
    }

    private fun determineSmkState(drq: LMDefaultRequest): Boolean {
        return drq.hasSmk() && drq.smk?.get(1) == 1
    }
}
