package de.visualdigits.klanglicht.hardware.harmony.protocol

import de.visualdigits.klanglicht.hardware.harmony.model.protocol.action.HoldActionReplyParser
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.action.MessageHoldAction
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity.GetCurrentActivityReplyParser
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity.MessageGetCurrentActivity
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity.MessageStartActivity
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity.StartActivityReplyParser
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.auth.AuthReplyParser
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.auth.MessageAuth
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.config.GetConfigReplyParser
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.config.MessageGetConfig
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.ping.MessagePing
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.ping.PingReplyParser
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.IqData
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.provider.IqProvider
import org.jivesoftware.smack.xml.XmlPullParser
import org.jxmpp.JxmppContext

class OAReplyProvider : IqProvider<IQ?>() {

    companion object {

        private val replyParsers = mutableMapOf(
            MessageAuth.MIME_TYPE to AuthReplyParser(),
            MessageGetConfig.MIME_TYPE to GetConfigReplyParser(),
            MessageHoldAction.MIME_TYPE to HoldActionReplyParser(),
            MessageGetCurrentActivity.MIME_TYPE to GetCurrentActivityReplyParser(),
            MessageStartActivity.MIME_TYPE to StartActivityReplyParser(),
            MessageStartActivity.MIME_TYPE2 to StartActivityReplyParser(),
            MessagePing.MIME_TYPE to PingReplyParser(),
        )
    }

    override fun parse(
        parser: XmlPullParser,
        initialDepth: Int,
        iqData: IqData?,
        xmlEnvironment: XmlEnvironment?,
        jxmppContext: JxmppContext?
    ): IQ? {
        val elementName = parser.name
        val attrs: MutableMap<String?, String?> = HashMap()
        (0..<parser.attributeCount).forEach { i ->
            val prefix = parser.getAttributePrefix(i)
            if (prefix != null) {
                attrs[prefix + ":" + parser.getAttributeName(i)] = parser.getAttributeValue(i)
            } else {
                attrs[parser.getAttributeName(i)] = parser.getAttributeValue(i)
            }
        }
        val statusCode = attrs["errorcode"]
        val errorString = attrs["errorstring"]

        val mimeType = parser.getAttributeValue(null, "mime")
        val replyParser: OAReplyParser = replyParsers[mimeType]!!
        if (!replyParser.validResponseCode(statusCode)) {
            throw RuntimeException(
                String.format("Got error response [%s]: %s", statusCode, attrs.get("errorstring"))
            )
        }

        val contents = StringBuilder()
        label@while (true) {
            when (parser.next()) {
                XmlPullParser.Event.END_ELEMENT -> {
                    if (parser.name == elementName) {
                        break@label
                    }
                    contents.append(parser.text)
                }

                else -> contents.append(parser.text)
            }
        }
        return replyParser.parseReplyContents(statusCode, errorString, contents.toString())
    }
}
