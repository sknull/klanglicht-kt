package de.visualdigits.kotlin.klanglicht.model.stage

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File
import java.nio.file.Paths


data class Stage(
    val name: String = "",
    val fixtures: List<StageFixture> = listOf()
) {
    companion object {
        fun load(klanglichtDir: File, stageName: String): Stage {
            return jacksonMapperBuilder().build().readValue(
                Paths.get(klanglichtDir.canonicalPath, "stages", "$stageName.json").toFile(),
                Stage::class.java
            )
        }
    }
}
