package de.visualdigits.klanglicht.hardware.harmony.model.config

import com.fasterxml.jackson.annotation.JsonValue

enum class PowerState(
    @get:JsonValue val description: String
) {
    
    ON("On"),

    OFF("Off")
}
