package de.visualdigits.klanglicht.hardware.harmony.model.protocol.auth

import com.google.common.io.BaseEncoding
import de.visualdigits.klanglicht.hardware.harmony.protocol.OAStanza
import java.util.UUID

/*
 * Request
 */
class AuthRequest() : OAStanza(MessageAuth.MIME_TYPE) {

    init {
        setType(Type.get)
    }

    override fun getChildElementPairs(): MutableMap<String, Any?> {
        return mutableMapOf("method" to "pair")
    }

    private fun generateUniqueId(): String? {
        return BaseEncoding.base64().encode(UUID.randomUUID().toString().toByteArray())
    }

    private val deviceIdentifier: String
        get() = "iOS6.0.1#iPhone"
}
