package de.visualdigits.klanglicht.hardware.harmony.model.protocol

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class HoldStatus(description: String) {
    PRESS("press"), RELEASE("release");

    @get:JsonValue
    val description: String?

    init {
        this.description = description
        storeInValueMap(this)
    }

    private fun storeInValueMap(holdStatus: HoldStatus?) {
        if (valueMap == null) valueMap = HashMap<String?, HoldStatus?>()
        valueMap!!.put(description, this)
    }

    override fun toString(): String {
        return description!!
    }

    companion object {
        private var valueMap: MutableMap<String?, HoldStatus?>? = null

        @JsonCreator
        fun forValue(description: String?): HoldStatus {
            val result: HoldStatus? = valueMap!!.get(description)
            if (result != null) return result
            return valueOf(description!!)
        }
    }
}
