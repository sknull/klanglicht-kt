package de.visualdigits.klanglicht.hardware.harmony.model.config

import com.fasterxml.jackson.annotation.JsonProperty

class Device {
    @JvmField
    var id: Int = 0

    @JvmField
    var label: String? = null

    var type: String? = null

    @JsonProperty("Transport")
    var transport: Int = 0

    var suggestedDisplay: String? = null

    var deviceTypeDisplayName: String? = null

    @JsonProperty("Capabilities")
    var capabilities: MutableList<Int> = mutableListOf()

    @JsonProperty("DongleRFID")
    var dongleRFID: Int = 0

    var controlGroup: MutableList<ControlGroup> = mutableListOf()

    @JsonProperty("ControlPort")
    var controlPort: String? = null

    @JsonProperty("IsKeyboardAssociated")
    var isKeyboardAssociated: Boolean = false

    var model: String? = null

    var deviceProfileUri: String? = null

    var manufacturer: String? = null

    var icon: String? = null

    @JsonProperty("isManualPower")
    var isManualPower: Boolean = false
}
