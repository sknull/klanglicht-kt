package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.model

import com.fasterxml.jackson.annotation.JsonProperty


class ResponseCode(
    @JsonProperty("response_code") val responseCode: Int = 0
)

