package de.visualdigits.klanglicht.hardware.harmony.model.protocol.action

import de.visualdigits.klanglicht.hardware.harmony.protocol.OAStanza

class HoldActionReply(
    val contents: String? = null
): OAStanza(MessageHoldAction.MIME_TYPE) {

    override fun getChildElementPairs(): MutableMap<String, Any?> = mutableMapOf()
}