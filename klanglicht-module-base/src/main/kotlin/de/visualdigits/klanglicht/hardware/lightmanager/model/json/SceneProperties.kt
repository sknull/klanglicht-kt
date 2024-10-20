package de.visualdigits.klanglicht.hardware.lightmanager.model.json

import com.fasterxml.jackson.annotation.JsonProperty


class SceneProperties(
    val jbcode: Int? = null,
    val senderid: Long? = null,
    val sendertype: Int? = null,
    val ntype: NType? = null,
    val index: Int? = null,
    @JsonProperty("bemerkung") val comment: String? = null,
    @JsonProperty("dauer") val duration: Long? = null,
    @JsonProperty("befehl") val command: Int? = null,
    val dimlevel: Int? = null,
    val sunset: Boolean? = null,
    val enabled: Boolean? = null,
    @JsonProperty("aktorindex") val actorIndex: Int? = null,
    val markerselect: String? = null,
    val markermask: String? = null,
    val markeror: Boolean? = null
)
