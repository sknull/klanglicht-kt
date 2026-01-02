package de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity

import de.visualdigits.klanglicht.hardware.harmony.protocol.OAStanza

/*
 * Reply (unused)
 */
class StartActivityReply : OAStanza(MessageStartActivity.MIME_TYPE) {
    override fun getChildElementPairs(): MutableMap<String, Any?> = mutableMapOf()
}
