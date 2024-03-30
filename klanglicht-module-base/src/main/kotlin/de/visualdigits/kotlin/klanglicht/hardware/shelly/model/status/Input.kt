package de.visualdigits.kotlin.klanglicht.hardware.shelly.model.status

import com.fasterxml.jackson.annotation.JsonProperty


class Input(
    val input: Int? = null,
    val event: String? = null,
    @JsonProperty("event_cnt") val eventCount: Int? = null
)
