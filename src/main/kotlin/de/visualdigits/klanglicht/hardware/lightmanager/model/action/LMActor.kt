package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonIgnore

class LMActor(
    val id: Int? = null,
    val name: String? = null,
    val actorOff: List<String> = listOf(),
    val actorOn: List<String> = listOf(),
    val colorOff: String? = null,
    val colorOn: String? = null,
    val isDimmer: Boolean? = null,
) {

    val markers: MutableMap<String, LMMarker?> = mutableMapOf()
    val requests: MutableMap<String, LMRequest> = mutableMapOf()
    val requestsBySmkState: MutableMap<Int, LMDefaultRequest> = mutableMapOf()

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

    @JsonIgnore
    fun getRequestByName(key: String): LMRequest? {
        return requests[key]
    }

    @JsonIgnore
    fun getRequestBySmkState(state: Int): LMDefaultRequest? {
        return requestsBySmkState[state]
    }
}
