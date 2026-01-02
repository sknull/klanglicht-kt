package de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity

import com.google.common.collect.ImmutableMap
import de.visualdigits.klanglicht.hardware.harmony.protocol.OAStanza

/*
 * Reply
 */
class GetCurrentActivityReply : OAStanza(MessageGetCurrentActivity.MIME_TYPE) {
    var result: Int = 0

    override fun getChildElementPairs(): MutableMap<String, Any?> {
        return ImmutableMap.builder<String?, Any?>() //
            .put("result", result)
            .build()
    }
}
