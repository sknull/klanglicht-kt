package de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity

import de.visualdigits.klanglicht.hardware.harmony.protocol.IrCommand

/*
 * Request
 */
class StartActivityRequest(private val activityId: Int) : IrCommand(MessageStartActivity.MIME_TYPE) {
    override fun getChildElementPairs(): MutableMap<String, Any?> {
        return mutableMapOf(
            "activityId" to activityId,
            "timestamp" to generateTimestamp(),
        )
    }
}
