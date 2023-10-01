package de.visualdigits.kotlin.klanglicht.model.stage

import de.visualdigits.kotlin.klanglicht.model.fixture.Fixture


data class StageFixture(
    val baseChannel: Int = 0,
    val manufacturer: String = "",
    val mode: String = "",
    val model: String = ""
) {
    var fixture: Fixture? = null
}
