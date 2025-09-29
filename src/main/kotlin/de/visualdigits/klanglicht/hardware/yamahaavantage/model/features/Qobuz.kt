package de.visualdigits.klanglicht.hardware.yamahaavantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

class Qobuz(
    @JsonProperty("login_type") val loginType: String = ""
)
