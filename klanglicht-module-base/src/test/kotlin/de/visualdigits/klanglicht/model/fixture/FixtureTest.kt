package de.visualdigits.klanglicht.model.fixture

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.klanglicht.model.dmx.fixture.Fixture
import org.junit.jupiter.api.Test
import java.io.File

class FixtureTest {

    @Test
    fun testReadModel() {
        File(ClassLoader.getSystemResource(".klanglicht/fixtures").toURI())
            .listFiles()
            ?.forEach { file ->
                println("Reading $file ...")
                val fixture = jacksonMapperBuilder().build().readValue(file, Fixture::class.java)
                println(fixture)
            }
    }
}
