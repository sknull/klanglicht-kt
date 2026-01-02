package de.visualdigits.klanglicht.hardware.harmony.protocol

import com.fasterxml.jackson.databind.ObjectMapper

abstract class IrCommand(mimeType: String?) : OAStanza(mimeType) {
    fun generateAction(deviceId: Int, button: String?): String {
        return ObjectMapper().writeValueAsString(
            mapOf(
                "type" to "IRCommand",
                "deviceId" to deviceId.toString(),
                "command" to button,
            )
        ).replace(":", "::")
    }
}