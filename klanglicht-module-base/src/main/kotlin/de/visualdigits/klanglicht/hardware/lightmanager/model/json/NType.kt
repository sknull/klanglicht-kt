package de.visualdigits.klanglicht.hardware.lightmanager.model.json

import com.fasterxml.jackson.annotation.JsonValue

enum class NType(
    @JsonValue val ntype: Int
) {

    irlan(1),
    scene(9),
    marker(8),
    pause(10),
    child(13)
}
