package de.visualdigits.klanglicht.hardware.harmony.protocol

import org.jivesoftware.smack.packet.Bind
import org.jivesoftware.smack.packet.IqData
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.provider.BindIQProvider
import org.jivesoftware.smack.xml.XmlPullParser
import org.jxmpp.JxmppContext
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

/*
 * Copied from BindIQProvider, but tweaked to support the Harmony's JID that does not have a localpart (user)
 */
class HarmonyBindIQProvider : BindIQProvider() {

    override fun parse(parser: XmlPullParser,
                       initialDepth: Int,
                       iqData: IqData?,
                       xmlEnvironment: XmlEnvironment?,
                       jxmppContext: JxmppContext?
    ): Bind {
        var name: String
        var bind: Bind? = null
        outerloop@ while (true) {
            val eventType = parser.next()
            when (eventType) {
                XmlPullParser.Event.START_ELEMENT -> {
                    name = parser.getName()
                    when (name) {
                        "resource" -> {
                            val resourceString = parser.nextText()
                            bind = Bind.newSet(Resourcepart.from(resourceString))
                        }

                        "jid" -> {
                            val fullJid = JidCreate.entityFullFrom("client@" + parser.nextText())
                            bind = Bind.newResult(fullJid)
                        }
                    }
                }

                XmlPullParser.Event.END_ELEMENT -> if (parser.getDepth() == initialDepth) {
                    break@outerloop
                }

                else -> {
                    // nothing to do
                }
            }
        }
        return bind?:error("Could not determine bind")
    }
}