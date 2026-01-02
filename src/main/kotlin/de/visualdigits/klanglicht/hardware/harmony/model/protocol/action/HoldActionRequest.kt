package de.visualdigits.klanglicht.hardware.harmony.model.protocol.action

import de.visualdigits.klanglicht.hardware.harmony.model.protocol.HoldStatus
import de.visualdigits.klanglicht.hardware.harmony.protocol.IrCommand

/*
 * Request
 */
class HoldActionRequest(private val deviceId: Int, private val button: String?, private val status: HoldStatus?) :
    IrCommand(MessageHoldAction.MIME_TYPE) {
    override fun getChildElementPairs(): MutableMap<String, Any?> {
        return mutableMapOf(
            "action" to generateAction(deviceId, button),
            "status" to status!!,
            "timestamp" to generateTimestamp()
        )
    }
}
