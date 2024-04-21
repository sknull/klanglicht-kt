package de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

class Qobuz(
    @JsonProperty("login_type") val loginType: String = ""
)
