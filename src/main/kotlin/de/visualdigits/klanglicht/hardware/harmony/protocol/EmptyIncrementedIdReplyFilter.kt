package de.visualdigits.klanglicht.hardware.harmony.protocol

import org.jivesoftware.smack.filter.AndFilter
import org.jivesoftware.smack.filter.StanzaFilter
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import java.util.regex.Pattern

class EmptyIncrementedIdReplyFilter(
    val request: OAStanza,
    val connection: XMPPTCPConnection
): StanzaFilter {
    private val filter: AndFilter? = null

    private val numericIdRE: Pattern = Pattern.compile("^(.*?)(\\d+)$")

    override fun accept(stanza: Stanza): Boolean {
        return filter!!.accept(stanza)
    }

    private fun incrementStanzaId(request: OAStanza): String {
        val stanzaId = request.getStanzaId()
        val matcher = numericIdRE.matcher(stanzaId)
        if (!matcher.matches()) {
            throw IllegalArgumentException(java.lang.String.format("Can't handle non-numeric stanza id %s", stanzaId))
        }
        val beginning = matcher.group(1)
        val stanzaNum = matcher.group(2).toLong()
        return java.lang.String.format("%s%d", beginning, (stanzaNum + 1))
    }
}