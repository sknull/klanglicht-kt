package de.visualdigits.klanglicht.hardware.harmony.model.protocol.ping

import de.visualdigits.klanglicht.hardware.harmony.protocol.OAStanza

/*
 * Reply
 */
class PingReply : OAStanza(MessagePing.MIME_TYPE) {
    override fun getChildElementPairs(): MutableMap<String, Any?>  = mutableMapOf()
}
