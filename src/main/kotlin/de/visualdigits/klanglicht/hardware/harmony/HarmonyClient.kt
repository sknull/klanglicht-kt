package de.visualdigits.klanglicht.hardware.harmony

import de.visualdigits.klanglicht.hardware.harmony.model.config.Activity
import de.visualdigits.klanglicht.hardware.harmony.model.config.HarmonyConfig
import de.visualdigits.klanglicht.hardware.harmony.model.config.Status
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.EventType
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity.GetCurrentActivityReply
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity.GetCurrentActivityRequest
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.auth.AuthReply
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.auth.AuthRequest
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.config.GetConfigReply
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.config.GetConfigRequest
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.ping.PingReply
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.ping.PingRequest
import de.visualdigits.klanglicht.hardware.harmony.protocol.EventStanza
import de.visualdigits.klanglicht.hardware.harmony.protocol.HarmonyBindIQProvider
import de.visualdigits.klanglicht.hardware.harmony.protocol.HarmonyHubListener
import de.visualdigits.klanglicht.hardware.harmony.protocol.HarmonyXMPPTCPConnection
import de.visualdigits.klanglicht.hardware.harmony.protocol.OAReplyFilter
import de.visualdigits.klanglicht.hardware.harmony.protocol.OAStanza
import de.visualdigits.klanglicht.hardware.harmony.protocol.activity.ActivityChangeListener
import de.visualdigits.klanglicht.hardware.harmony.protocol.activity.ActivityStatusListener
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.SmackException.NoResponseException
import org.jivesoftware.smack.StanzaCollector
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.XMPPConnection.FromMode
import org.jivesoftware.smack.filter.StanzaFilter
import org.jivesoftware.smack.packet.Bind
import org.jivesoftware.smack.packet.ExtensionElement
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.packet.XmlElement
import org.jivesoftware.smack.provider.ProviderManager
import org.jivesoftware.smack.sasl.SASLMechanism
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.parts.Resourcepart
import org.jxmpp.stringprep.XmppStringprepException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class HarmonyClient {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    private var smackConfigured: Boolean = false
    private var connection: HarmonyXMPPTCPConnection? = null

    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private var heartbeat: ScheduledFuture<*>? = null

    /*
     * To prevent timeouts when different threads send a message and expect a response, create a lock that only allows a
     * single thread at a time to perform a send/receive action.
     */
    private val messageLock = ReentrantLock()

    private var config: HarmonyConfig? = null

    private var currentActivity: Activity? = null

    private val activityChangeListeners: MutableSet<ActivityChangeListener> = HashSet<ActivityChangeListener>()
    private val activityStatusListeners: MutableSet<ActivityStatusListener> = HashSet<ActivityStatusListener>()

    companion object {

        const val DEFAULT_REPLY_TIMEOUT: Long = 30000
        const val START_ACTIVITY_REPLY_TIMEOUT: Long = 30000

        const val DEFAULT_PORT: Int = 5222
        const val DEFAULT_XMPP_USER: String = "guest@connect.logitech.com/gatorade."
        const val DEFAULT_XMPP_PASSWORD: String = "gatorade."

        private var instance: HarmonyClient? = null

        fun getInstance(): HarmonyClient{
            if (instance == null) {
                instance = HarmonyClient()
            }
            return instance?:error("No instance")
        }
    }

    fun connect(host: String) {
        configureSmack()
        val connectionConfig = createConnectionConfig(host, DEFAULT_PORT)
        val authConnection = HarmonyXMPPTCPConnection(connectionConfig)
        authConnection.connect()
        authConnection.login(DEFAULT_XMPP_USER, DEFAULT_XMPP_PASSWORD, Resourcepart.from("auth"))
        authConnection.fromMode = FromMode.USER

        val sessionRequest = AuthRequest()
        val oaResponse: AuthReply = sendOAStanza<AuthReply>(authConnection, sessionRequest)

        authConnection.disconnect()

        connection = HarmonyXMPPTCPConnection(connectionConfig)
        connection?.connect()
        connection?.login(oaResponse.username, oaResponse.identity, Resourcepart.from("main"))
        connection?.fromMode = FromMode.USER
        connection?.addConnectionListener(object : ConnectionListener {

            override fun connected(connection: XMPPConnection?) {
            }

            override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
            }

            override fun connectionClosed() {
            }

            override fun connectionClosedOnError(e: java.lang.Exception?) {
            }
        })

        heartbeat = scheduler.scheduleAtFixedRate({
            try {
                if (connection!!.isConnected) {
                    sendPing()
                }
            } catch (e: java.lang.Exception) {
                log.warn("Send heartbeat failed", e)
            }
        }, 30, 30, TimeUnit.SECONDS)

        monitorActivityChanges()
        getCurrentActivity()
    }

    fun disconnect() {
        if (connection != null) {
            connection?.disconnect()
        }
        if (heartbeat != null) {
            heartbeat?.cancel(false)
        }
    }

    private fun configureSmack() {
        if (!smackConfigured) {
            ProviderManager.addIQProvider(Bind.ELEMENT, Bind.NAMESPACE, HarmonyBindIQProvider())
            smackConfigured = true
        }
    }

    private fun createConnectionConfig(host: String, port: Int): XMPPTCPConnectionConfiguration {
        try {
            return XMPPTCPConnectionConfiguration.builder()
                .setHost(host)
                .setPort(port)
                .setXmppDomain(host)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .addEnabledSaslMechanism(SASLMechanism.PLAIN).build()
        } catch (e: XmppStringprepException) {
            throw RuntimeException(e)
        }
    }

    private inline fun <reified R : OAStanza> sendOAStanza(
        authConnection: XMPPTCPConnection?,
        stanza: OAStanza,
        replyTimeout: Long = DEFAULT_REPLY_TIMEOUT
    ): R {
        checkNotNull(authConnection) { "Nop connection" }
        
        val collector: StanzaCollector = authConnection.createStanzaCollector(OAReplyFilter(stanza, authConnection))
        messageLock.lock()
        try {
            authConnection.sendStanza(stanza)
            return getNextStanzaSkipContinues(collector, replyTimeout, authConnection) as R
        } catch (e: Exception) {
            throw java.lang.RuntimeException("Failed communicating with Harmony Hub", e)
        } finally {
            messageLock.unlock()
            collector.cancel()
        }
    }

    private fun getNextStanzaSkipContinues(
        collector: StanzaCollector,
        replyTimeout: Long,
        authConnection: XMPPTCPConnection
    ): Stanza {
        while (true) {
            val reply: Stanza =
                collector.nextResult(replyTimeout) ?: throw NoResponseException.newWith(authConnection, collector.stanzaFilter)
            if (reply is OAStanza && reply.isContinuePacket) {
                continue
            }
            return reply
        }
    }

    fun sendPing() {
        sendOAStanza<PingReply>(connection, PingRequest())
    }

    fun getConfig(): HarmonyConfig {
        if (config == null) {
            config = HarmonyConfig
                .parse(sendOAStanza<GetConfigReply>(connection, GetConfigRequest()).contents)
        }
        return config?:error("No config")
    }

    fun getCurrentActivity(): Activity? {
        val reply: GetCurrentActivityReply = sendOAStanza<GetCurrentActivityReply>(
            connection, GetCurrentActivityRequest()
        )
        val config: HarmonyConfig = getConfig()
        return updateCurrentActivity(config.getActivityById(reply.result))
    }

    fun addListener(listener: HarmonyHubListener) {
        listener.addTo(this)
    }

    fun removeListener(listener: HarmonyHubListener) {
        if (listener is ActivityStatusListener) {
            activityStatusListeners.remove(listener)
        }
        listener.removeFrom(this)
    }

    @Synchronized
    fun addListener(listener: ActivityChangeListener) {
        log.debug("listener[{}] added", listener)
        activityChangeListeners.add(listener)
        if (currentActivity != null) {
            log.debug("listener[{}] notified: {}", listener, currentActivity)
            listener.activityStarted(currentActivity)
        }
    }

    @Synchronized
    fun addListener(listener: ActivityStatusListener) {
        log.debug("status listener[{}] added", listener)
        activityStatusListeners.add(listener)
        if (currentActivity != null) {
            val status: Status? = currentActivity?.status
            if (status !== Status.UNKNOWN) {
                log.debug("status listener[{}] notified: {}", listener, currentActivity)
                listener.activityStatusChanged(currentActivity, status)
            }
        }
    }

    @Synchronized
    private fun updateCurrentActivity(activity: Activity?): Activity? {
        if (currentActivity !== activity) {
            currentActivity = activity
            for (listener in activityChangeListeners) {
                log.debug("listener[{}] notified: {}", listener, currentActivity)
                listener.activityStarted(currentActivity)
            }
        }
        return currentActivity
    }

    @Synchronized
    private fun updateActivityStatus(activity: Activity?, status: Status?): Status? {
        var newStatus = false
        if (status === Status.HUB_IS_OFF) {
            // HUB_IS_OFF is a special status received on PowerOff activity only,
            // but it affects the status of all activities
            for (act in getConfig().activities) {
                if (act.status !== status) {
                    newStatus = true
                    act.status = status
                }
            }
        } else if (status != Status.UNKNOWN && status != activity?.status) {
            newStatus = true
            activity?.status = status
        }
        // inform listeners only if status was changed - avoid duplicate notifications
        if (newStatus) {
            for (listener in activityStatusListeners) {
                log.debug("status listener[{}] notified: {} - {}", listener, activity, status)
                listener.activityStatusChanged(activity, status)
            }
        }
        return activity?.status
    }

    private fun monitorActivityChanges() {
        connection!!.addSyncStanzaListener(object : StanzaListener {
            override fun processStanza(stanza: Stanza?) {
                val event: EventStanza? = EventStanza.create(stanza)
                if (event == null) {
                    log.debug("Error processing message stanza.")
                    return
                }
                log.debug(
                    "Received event: type={}, id={}, status={}, error={}",
                    event.eventType, event.activityId, event.getActivityStatus(), event.errorCode
                )

                val id = event.activityId?.toInt()?:-1
                if ((event.errorCode?.toInt()?:-1) == 200 && id != null) {
                    when (event.eventType) {
                        EventType.START_ACTIVITY_FINISHED -> updateCurrentActivity(getConfig().getActivityById(id))
                        EventType.STATE_DIGEST -> updateActivityStatus(getConfig().getActivityById(id), event.getActivityStatus())
                        else -> {}
                    }
                }
            }
        }, object : StanzaFilter {
            override fun accept(stanza: Stanza): Boolean {
                stanza.getExtensionElement("event", "connect.logitech.com") ?: return false
                return true
            }
        })
    }
}