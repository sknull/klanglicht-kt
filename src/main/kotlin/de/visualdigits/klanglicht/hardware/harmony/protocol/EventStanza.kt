package de.visualdigits.klanglicht.hardware.harmony.protocol

import com.fasterxml.jackson.databind.ObjectMapper
import de.visualdigits.klanglicht.hardware.harmony.model.config.Status
import de.visualdigits.klanglicht.hardware.harmony.model.protocol.EventType
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.StandardExtensionElement
import org.jivesoftware.smack.packet.Stanza
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

class EventStanza(
    var errorCode: String? = null,
    var activityId: String? = null,
    private var activityStatus: Int? = null,
    var eventType: EventType? = null
) {

    companion object {

        val logger: Logger = LoggerFactory.getLogger(EventStanza::class.java)

        fun create(stanza: Stanza?): EventStanza? {
            if (stanza !is Message) {
                return null
            }
            val message = stanza
            val element = message.getExtensionElement("event", "connect.logitech.com")
            if (element == null || element !is StandardExtensionElement) {
                return null
            }
            val stdElement = element
            val type = stdElement.getAttributeValue("type")
            val content = stdElement.getText()

            if (type != null && type == "connect.stateDigest?notify") {
                if (content != null) {
                    try {
                        logger.debug("stateDigest notify content: {}", content)
                        val event = ObjectMapper().readValue<EventStanza>(content, EventStanza::class.java)
                        // if error code is ommitted in the stanza, we assume it was successful
                        if (event.errorCode == null) {
                            event.errorCode = "200"
                        }
                        event.eventType = EventType.STATE_DIGEST
                        return event
                    } catch (e: IOException) {
                        logger.error("Exception parsing stateDigest: {}", e.message)
                    }
                }
            } else if (type == "harmony.engine?startActivityFinished") {
                if (content != null) {
                    try {
                        val event = ObjectMapper().convertValue<EventStanza>(
                            OAReplyParser.parseKeyValuePairs(null, null, content),
                            EventStanza::class.java
                        )
                        event.eventType = EventType.START_ACTIVITY_FINISHED
                        return event
                    } catch (e: IllegalArgumentException) {
                        logger.debug("Exception parsing startActivityFinished: {}", e.message)
                    }
                }
            }
            return null
        }
    }

    fun getActivityStatus(): Status {
        if (activityStatus != null) {
            when (activityStatus) {
                0 -> return Status.HUB_IS_OFF
                1 -> return Status.ACTIVITY_IS_STARTING
                2 -> return Status.ACTIVITY_IS_STARTED
                3 -> return Status.HUB_IS_TURNING_OFF
            }
        }
        return Status.UNKNOWN
    }
}