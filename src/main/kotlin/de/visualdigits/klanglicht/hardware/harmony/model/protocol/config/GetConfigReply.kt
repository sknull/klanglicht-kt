package de.visualdigits.klanglicht.hardware.harmony.model.protocol.config

import de.visualdigits.klanglicht.hardware.harmony.protocol.OAStanza

class GetConfigReply(
    val contents: String? = null
): OAStanza(MessageGetConfig.MIME_TYPE) {

    override fun getChildElementPairs(): MutableMap<String, Any?> = mutableMapOf()
}