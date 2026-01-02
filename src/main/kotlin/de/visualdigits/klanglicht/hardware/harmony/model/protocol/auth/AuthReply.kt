package de.visualdigits.klanglicht.hardware.harmony.model.protocol.auth

import com.google.common.collect.ImmutableMap
import de.visualdigits.klanglicht.hardware.harmony.protocol.OAStanza

/*
 * Reply
 */
class AuthReply() : OAStanza(MessageAuth.MIME_TYPE) {
    val serverIdentity: String? = null
    val hubId: String? = null
    val identity: String? = null
    val status: String? = null
    val protocolVersion: MutableMap<String?, String?>? = null
    val hubProfiles: MutableMap<String?, String?>? = null
    val productId: String? = null
    val friendlyName: String? = null

    override fun getChildElementPairs(): MutableMap<String, Any?> {
        return ImmutableMap.builder<String?, Any?>() //
            .put("serverIdentity", serverIdentity!!)
            .put("hubId", hubId!!)
            .put("identity", this.identity!!)
            .put("status", status!!)
            .put("protocolVersion", protocolVersion!!)
            .put("hubProfiles", hubProfiles!!)
            .put("productId", productId!!)
            .put("friendlyName", friendlyName!!)
            .build()
    }

    val username: String
        get() = String.format("%s@connect.logitech.com/gatorade", this.identity)
}
