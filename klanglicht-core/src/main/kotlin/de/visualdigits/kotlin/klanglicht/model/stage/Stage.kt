package de.visualdigits.kotlin.klanglicht.model.stage

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.model.fixture.Fixture
import java.io.File
import java.nio.file.Paths


@JsonIgnoreProperties("fixturesResolved")
data class Stage(
    val name: String = "",
    val fixtures: List<StageFixture> = listOf()
) {

    companion object {
        fun load(klanglichtDir: File, stageName: String): Stage {
            val stage = jacksonMapperBuilder().build().readValue(
                Paths.get(klanglichtDir.canonicalPath, "stages", "$stageName.json").toFile(),
                Stage::class.java
            )

            stage.fixtures.forEach { stageFixture ->
                val fixtureName = "${stageFixture.manufacturer}_${stageFixture.model}"
                stageFixture.fixture = Fixture.load(klanglichtDir, fixtureName)
                stageFixture.fixture?.channelsForCurrentMode =
                    stageFixture.fixture
                        ?.channelsForMode(stageFixture.mode)
                        ?.associate { Pair(it.name!!, it) }
                        ?:mapOf()
            }
            return stage
        }
    }
}
