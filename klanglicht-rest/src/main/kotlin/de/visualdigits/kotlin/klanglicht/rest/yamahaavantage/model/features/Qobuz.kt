package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

data class Qobuz(
    @JsonProperty("login_type") val loginType: String = ""
)
