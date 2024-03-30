package de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model

import com.fasterxml.jackson.annotation.JsonProperty


class ResponseCode(
    @JsonProperty("response_code") val responseCode: Int = 0
)

