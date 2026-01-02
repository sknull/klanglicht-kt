package de.visualdigits.klanglicht.hardware.harmony.model.config

import com.fasterxml.jackson.annotation.JsonProperty

class Fixit {
    var id: String? = null

    @JsonProperty("Power")
    var power: PowerState? = null

    @JsonProperty("Input")
    var input: String? = null

    @JsonProperty("isAlwaysOn")
    var isAlwaysOn: Boolean = false

    @JsonProperty("isRelativePower")
    var isRelativePower: Boolean = false
}
