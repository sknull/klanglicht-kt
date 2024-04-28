package de.visualdigits.klanglicht.hardware.lightmanager.model.lm

import java.util.Objects

class LMZone(
    val id: Int? = null,
    val name: String? = null,
    val logo: String? = null,
    val tempChannel: Int? = null,
    val arrow: String? = null
) : Comparable<LMZone> {

    val actors: MutableList<LMActor> = ArrayList()

    fun addActor(actor: LMActor) {
        actors.add(actor)
    }

    override fun compareTo(other: LMZone): Int {
        return name!!.lowercase().compareTo(other.name?.lowercase()?:"")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val lmZone = other as LMZone
        return id == lmZone.id && name == lmZone.name
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name)
    }
}
