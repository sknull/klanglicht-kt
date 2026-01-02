package de.visualdigits.klanglicht.hardware.harmony.protocol

import org.jivesoftware.smack.packet.EmptyResultIQ
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smack.util.PacketParserUtils
import org.jivesoftware.smack.util.ParserUtils
import org.jivesoftware.smack.xml.XmlPullParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class HarmonyXMPPTCPConnection(config: XMPPTCPConnectionConfiguration?) : XMPPTCPConnection(config) {

    override fun parseAndProcessStanza(parser: XmlPullParser) {
//        ParserUtils.assertAtStartTag(parser)
        val parserDepth = parser.depth
        var stanza: Stanza? = null
        try {
            if (IQ.IQ_ELEMENT == parser.name && parser.getAttributeValue("", "type") == null) {
                // Acknowledgement IQs don't contain a type so an empty result is created here to prevent a parsing NPE
                stanza = EmptyResultIQ()
            } else {
                stanza = PacketParserUtils.parseStanza(parser, null, jxmppContext)
            }
        } catch (e: Exception) {
            val content = PacketParserUtils.parseContentDepth(parser, parserDepth)
            logger.warn("Smack message parsing exception. Content: '{}'", content, e)
        }
//        ParserUtils.assertAtEndTag(parser)
        if (stanza != null) {
            processStanza(stanza)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(HarmonyXMPPTCPConnection::class.java)
    }
}
