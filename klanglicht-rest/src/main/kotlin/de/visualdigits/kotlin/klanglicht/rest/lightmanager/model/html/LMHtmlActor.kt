package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html

import com.fasterxml.jackson.annotation.JsonIgnore
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.json.RequestType
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActor
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMDefaultRequest
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarker
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMRequest
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.service.LightmanagerService

class LMHtmlActor(
    val actor: LMActor
) : HtmlRenderable {

    override fun toHtml(prefs: ApplicationPreferences): String {
        val sb = StringBuilder()
        sb.append("      <div class=\"panel\">\n")
        renderLabel(sb, actor.name)
        renderRequests(sb)
        // fixme - yet not working reliable enough
//        renderSlider(sb, prefs, configHolder);
        sb.append("      </div><!-- actor -->\n")
        return sb.toString()
    }

    private fun renderSlider(sb: StringBuilder, prefs: ApplicationPreferences) {
        if (actor.isDimmer == true) {
            val actorId = actor.id
            val drq = getRequestBySmkState(1)
            var requestTemplate: String? = ""
            if (drq != null) {
                requestTemplate = drq.requestTemplate()
            }
            sb.append("<div class=\"slidercontainer\">\n")
                .append("  <input type=\"range\" min=\"1\" max=\"16\" value=\"8\" class=\"slider\" id=\"slider-")
                .append(actorId)
                .append("\"")
                .append("/>\n")
                .append("  <script>document.getElementById(\"slider-")
                .append(actorId)
                .append("\").oninput = function() { request('")
                .append(prefs.urlLightmanager)
                .append("/control', 'POST', '")
                .append(requestTemplate)
                .append("'.replace('\${level}', this.value)); }</script>\n")
                .append("</div>\n")
        }
    }

    private fun renderRequests(sb: StringBuilder) {
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
        }
        else if (actor.markers.isNotEmpty()) {
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
                    renderRequest(
                        sb,
                        "half-button",
                        markerIsOn,
                        colorOff,
                        colorOn,
                        request,
                        false,
                        isSeparate,
                        hasSeparateMarkers
                    )
                }
                sb.append("        </div><!-- double-button -->\n")
            }
            else {
                val rq0 = lmRequests.first() as LMDefaultRequest
                val smkState0 = determineSmkState(rq0)
                val rq: LMRequest = if (markerIsOn == true && !smkState0 || markerIsOn != true && smkState0) {
                    rq0
                }
                else if (lmRequests.size > 1) {
                    lmRequests.get(1)
                }
                else {
                    rq0
                }
                renderRequest(sb, "button", markerIsOn, colorOff, colorOn, rq, true, false, false)
            }
        }
    }

    private fun renderRequest(
        sb: StringBuilder,
        styleClass: String,
        markerIsOn: Boolean?,
        colorOff: String?,
        colorOn: String?,
        rq: LMRequest?,
        unified: Boolean?,
        isSeparate: Boolean?,
        hasSeparateMarkers: Boolean?
    ) {
        if (rq is LMDefaultRequest) {
            val drq = rq
            val isOn = determineSmkState(drq)
            sb.append("        <div class=\"")
                .append(styleClass)
                .append("\"")
            if (isSeparate == true) {
                if (isOn) {
                    sb.append(" style=\"background-color: ").append(colorOn).append(";\"")
                }
                else {
                    sb.append(" style=\"background-color: ").append(colorOff).append(";\"")
                }
            }
            else if (hasSeparateMarkers == true) {
                sb.append(" style=\"background-color: ").append(colorOn).append(";\"")
            }
            else if (markerIsOn == true && (isOn || unified == true)) {
                sb.append(" style=\"background-color: ").append(colorOn).append(";\"")
            }
            else {
                if (colorOff?.contains(",") == true) {
                    sb.append(" style=\"background: -moz-linear-gradient(left, ")
                        .append(colorOff)
                        .append("); background: -webkit-linear-gradient(left, ")
                        .append(colorOff)
                        .append("); background: linear-gradient(to right, ")
                        .append(colorOff)
                        .append(");\"")
                }
                else {
                    sb.append(" style=\"background-color: ")
                        .append(colorOff)
                        .append(";\"")
                }
            }
            sb.append("><input")
            if (hasSeparateMarkers == true) {
                if (markerIsOn == true) {
                    sb.append(" class='on'")
                }
                else {
                    sb.append(" class='off'")
                }
            }
            else if (isSeparate == true) {
                if (isOn) {
                    sb.append(" class='on'")
                }
                else {
                    sb.append(" class='off'")
                }
            }
            sb.append(" type=\"submit\" value=\"")
                .append(drq.name)
                .append("\" onclick=\"request('")
                .append(drq.uri)
                .append("'")
            val type = drq.type
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
    }

    fun addMarker(marker: LMMarker?) {
        var markerState = marker?.markerState!!
        if (markerState.isEmpty()) {
            markerState = "unified"
        }
        actor.markers[markerState] = marker
    }

    private fun determineSmkState(drq: LMDefaultRequest): Boolean {
        return drq.hasSmk() && drq.smk?.get(1) == 1
    }

    @JsonIgnore
    fun getRequestByName(key: String): LMRequest? {
        return actor.requests[key]
    }

    @JsonIgnore
    fun getRequestBySmkState(state: Int): LMDefaultRequest? {
        return actor.requestsBySmkState[state]
    }
}
