package de.visualdigits.klanglicht.hardware.harmony.model.protocol.action

import de.visualdigits.klanglicht.hardware.harmony.protocol.OAReplyParser
import org.jivesoftware.smack.packet.IQ

/*
 * Parser (unused)
 */
class HoldActionReplyParser : OAReplyParser() {
    override fun parseReplyContents(statusCode: String?, errorString: String?, contents: String?): IQ {
        return HoldActionReply()
    }
}
