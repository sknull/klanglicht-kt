package de.visualdigits.klanglicht.hardware.yamahaavantage.model

import com.fasterxml.jackson.annotation.JsonProperty


class ResponseCode(
    @JsonProperty("response_code") val responseCode: Int = 0
)

