package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonIgnore
import de.visualdigits.klanglicht.hardware.lightmanager.model.json.RequestType


class LMDefaultRequest(
    val name: String? = null,
    val type: RequestType? = null,
    val deviceId: Long = 0,
    val actorId: Int? = null,
    val actorCommand: Int? = null,
    val sequence: Int? = null,
    val level: Int? = null,
    val smk: IntArray? = null,
    val uri: String? = null,
    val data: Array<String> = arrayOf()
) : LMRequest {

    @JsonIgnore
    fun requestTemplate(): String {
        val params = mutableListOf(
                "typ", type?.name,
                "did", deviceId.toString(),
                "aid", actorId.toString(),
                "acmd", actorCommand.toString(),
                "lvl", "\${level}",
                "seq", sequence.toString()
        )
        if (hasSmk()) {
            params.addAll(listOf("smk", smk?.firstOrNull()?.toString(), smk?.get(1)?.toString()))
        }
        return "cmd=\"${params.joinToString(",")}\""
    }

    fun hasSmk(): Boolean {
        return smk?.isNotEmpty() == true
    }
}
