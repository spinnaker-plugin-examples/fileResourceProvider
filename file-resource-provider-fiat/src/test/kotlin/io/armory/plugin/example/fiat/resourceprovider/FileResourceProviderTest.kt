package io.armory.plugin.example.fiat.resourceprovider

import dev.minutest.junit.JUnit5Minutests
import dev.minutest.rootContext
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class FileResourceProviderTest : JUnit5Minutests {

  fun tests() = rootContext {

    test("need one test") {
      expectThat(1 + 1).isEqualTo(2)
    }
  }

  inner class Fixture {
    val subject = FileResourceProvider(FileConfig(javaClass.getResource("").path))
  }
}

