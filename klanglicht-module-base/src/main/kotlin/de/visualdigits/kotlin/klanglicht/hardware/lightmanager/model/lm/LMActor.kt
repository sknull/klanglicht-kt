package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

import com.fasterxml.jackson.annotation.JsonIgnore

class LMActor(
    var id: Int? = null,
    var name: String? = null,
    var markers: MutableMap<String, LMMarker?> = mutableMapOf(),
    var actorOff: String? = null,
    var actorOn: String? = null,
    var colorOff: String? = null,
    var colorOn: String? = null,
    var isDimmer: Boolean? = null,
    var requests: MutableMap<String, LMRequest> = mutableMapOf(),
    var requestsBySmkState: MutableMap<Int, LMDefaultRequest> = mutableMapOf()
) {

    fun addRequest(key: String, request: LMRequest) {
        requests[key] = request
        if (request is LMDefaultRequest) {
            val drq = request
            if (drq.hasSmk()) {
                drq.smk?.get(1)?.let { requestsBySmkState[it] = drq }
            }
        }
    }

    fun addMarker(marker: LMMarker?) {
        var markerState = marker?.markerState!!
        if (markerState.isEmpty()) {
            markerState = "unified"
        }
        markers[markerState] = marker
    }

    private fun determineSmkState(drq: LMDefaultRequest): Boolean {
        return drq.hasSmk() && drq.smk?.get(1) == 1
    }

    @JsonIgnore
    fun getRequestByName(key: String): LMRequest? {
        return requests[key]
    }

    @JsonIgnore
    fun getRequestBySmkState(state: Int): LMDefaultRequest? {
        return requestsBySmkState[state]
    }
}
