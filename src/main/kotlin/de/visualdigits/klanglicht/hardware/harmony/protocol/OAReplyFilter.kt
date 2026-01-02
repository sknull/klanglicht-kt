package de.visualdigits.klanglicht.hardware.harmony.protocol

import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.filter.AndFilter
import org.jivesoftware.smack.filter.FromMatchesFilter
import org.jivesoftware.smack.filter.IQTypeFilter
import org.jivesoftware.smack.filter.OrFilter
import org.jivesoftware.smack.filter.StanzaFilter
import org.jivesoftware.smack.filter.StanzaIdFilter
import org.jivesoftware.smack.packet.Stanza
import org.jxmpp.jid.Jid
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/*
 * Copied from IQReplyFilter, but tweaked to support the Harmony's response pattern
 */
class OAReplyFilter(request: OAStanza, connection: XMPPConnection) : StanzaFilter {
    private val iqAndIdFilter: StanzaFilter
    private val fromFilter: OrFilter
    private val to: Jid?
    private val local: Jid?
    private val server: Jid?
    private val stanzaId: String?

    init {
        to = request.getTo()
        if (connection.getUser() == null) {
            // We have not yet been assigned a username, this can happen if the connection is
            // in an early stage, i.e. when performing the SASL auth.
            local = null
        } else {
            local = connection.getUser()
        }
        server = connection.xmppServiceDomain
        stanzaId = request.getStanzaId()

        val iqFilter: StanzaFilter = OrFilter(IQTypeFilter.ERROR, IQTypeFilter.GET)
        val idFilter: StanzaFilter = StanzaIdFilter(request.getStanzaId())
        iqAndIdFilter = AndFilter(iqFilter, idFilter)
        fromFilter = OrFilter()
        fromFilter.addFilter(FromMatchesFilter.createFull(to))
        if (to == null) {
            if (local != null) {
                fromFilter.addFilter(FromMatchesFilter.createBare(local))
            }
            fromFilter.addFilter(FromMatchesFilter.createFull(server))
        } else if (local != null && to.equals(local.asBareJid())) {
            fromFilter.addFilter(FromMatchesFilter.createFull(null))
        }
    }

    override fun accept(stanza: Stanza): Boolean {
        // First filter out everything that is not an IQ stanza and does not have the correct ID set.
        if (!iqAndIdFilter.accept(stanza)) {
            return false
        }

        // Second, check if the from attributes are correct and log potential IQ spoofing attempts
        if (fromFilter.accept(stanza)) {
            return true
        } else {
            logger.warn(
                String.format(
                    "Rejected potentially spoofed reply to IQ-stanza. Filter settings: "
                            + "stanzaId=%s, to=%s, local=%s, server=%s. Received stanza with from=%s",
                    stanzaId, to, local, server, stanza.getFrom()
                ), stanza
            )
            return false
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(OAReplyFilter::class.java)
    }
}
