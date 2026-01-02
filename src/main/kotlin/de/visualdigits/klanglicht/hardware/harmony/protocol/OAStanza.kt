package de.visualdigits.klanglicht.hardware.harmony.protocol

import com.google.common.base.Joiner
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.SimpleIQ

abstract class OAStanza(
    val mimeType: String? = null
) : IQ(object : SimpleIQ("oa", "connect.logitech.com") {
}) {
    var statusCode: String? = null
    var errorString: String? = null

    override fun getIQChildElementBuilder(xml: IQChildElementXmlStringBuilder): IQChildElementXmlStringBuilder {
        if (statusCode != null) {
            xml.attribute("errorcode", statusCode)
        }
        if (errorString != null) {
            xml.attribute("errorstring", errorString)
        }

        xml.attribute("mime", this.mimeType)
        xml.rightAngleBracket()
        xml.append(joinChildElementPairs(getChildElementPairs()))
        return xml
    }

    private fun joinChildElementPairs(pairs: MutableMap<String, Any?>): String {
        val parts: MutableList<String?> = ArrayList<String?>()
        for (pair in pairs.entries) {
            parts.add(pair.key + "=" + pair.value)
        }
        return Joiner.on(":").join(parts)
    }

    protected abstract fun getChildElementPairs(): MutableMap<String, Any?>

    val isContinuePacket: Boolean
        get() = "100" == statusCode

    protected fun generateTimestamp(): Long {
        return System.currentTimeMillis() - CREATION_TIME
    }

    companion object {
        private val CREATION_TIME = System.currentTimeMillis()
    }
}
