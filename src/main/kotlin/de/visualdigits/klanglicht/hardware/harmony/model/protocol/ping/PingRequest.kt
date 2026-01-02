package de.visualdigits.klanglicht.hardware.harmony.model.protocol.ping

import de.visualdigits.klanglicht.hardware.harmony.protocol.IrCommand

/*
 * Request
 */
class PingRequest() : IrCommand(MessagePing.MIME_TYPE) {
    override fun getChildElementPairs(): MutableMap<String, Any?> {
        return mutableMapOf()
    }
}
