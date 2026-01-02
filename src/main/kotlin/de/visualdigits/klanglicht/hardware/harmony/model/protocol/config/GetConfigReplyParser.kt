package de.visualdigits.klanglicht.hardware.harmony.model.protocol.config

import de.visualdigits.klanglicht.hardware.harmony.protocol.OAReplyParser
import org.jivesoftware.smack.packet.IQ

/*
 * Parser
 */
class GetConfigReplyParser : OAReplyParser() {
    override fun parseReplyContents(statusCode: String?, errorString: String?, contents: String?): IQ {
        return GetConfigReply(contents)
    }
}
