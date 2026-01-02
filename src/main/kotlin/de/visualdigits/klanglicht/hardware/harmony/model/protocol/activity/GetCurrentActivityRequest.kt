package de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity

import de.visualdigits.klanglicht.hardware.harmony.protocol.IrCommand

/*
 * Request
 */
class GetCurrentActivityRequest() : IrCommand(MessageGetCurrentActivity.MIME_TYPE) {
    override fun getChildElementPairs(): MutableMap<String, Any?> {
        return mutableMapOf()
    }
}
