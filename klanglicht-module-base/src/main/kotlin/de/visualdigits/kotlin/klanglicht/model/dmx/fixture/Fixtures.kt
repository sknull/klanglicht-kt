package de.visualdigits.kotlin.klanglicht.model.dmx.fixture

import java.io.File
import java.nio.file.Paths

class Fixtures(
    val fixtures: List<Fixture>
) {
    companion object {
        private var fixtures: Fixtures? = null

        fun load(klanglichtDir: File): Fixtures {
            if (fixtures == null) {
                fixtures = Fixtures(Paths.get(klanglichtDir.canonicalPath, "fixtures").toFile()
                        .listFiles()
                        ?.map { file -> Fixture.load(klanglichtDir, file.name) }
                        ?:listOf()
                )
            }
            return fixtures!!
        }
    }

    fun getFixture(manufacturer: String, model: String): Fixture? {
        return fixtures.find { fixture -> fixture.manufacturer == manufacturer && fixture.model == model }
    }
}
