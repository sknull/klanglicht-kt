package de.visualdigits.klanglicht.hardware.harmony.model.config

import com.fasterxml.jackson.annotation.JsonProperty

class Activity {
    @JvmField
    var label: String? = null
    var suggestedDisplay: String? = null
    @JvmField
    var id: Int? = null
    var activityTypeDisplayName: String? = null
    var controlGroup: MutableList<ControlGroup> = mutableListOf()
    var activityOrder: Int? = null

    @JsonProperty("isTuningDefault")
    var isTuningDefault: Boolean = false
    var fixit: MutableMap<String?, Fixit> = mutableMapOf()
    var type: String? = null
    var icon: String? = null
    var baseImageUri: String? = null
    var status: Status? = Status.UNKNOWN

    override fun toString(): String {
        return String.format("Activity[%d]:%s", this.id, this.label)
    }
}
