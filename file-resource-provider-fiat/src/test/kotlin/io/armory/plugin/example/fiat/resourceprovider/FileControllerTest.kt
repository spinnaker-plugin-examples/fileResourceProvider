package io.armory.plugin.example.fiat.resourceprovider

import com.netflix.spinnaker.fiat.model.Authorization
import com.netflix.spinnaker.fiat.model.UserPermission
import com.netflix.spinnaker.fiat.model.resources.Permissions
import com.netflix.spinnaker.fiat.model.resources.Role
import com.netflix.spinnaker.fiat.shared.FiatService
import com.netflix.spinnaker.orca.api.test.orcaFixture
import dev.minutest.junit.JUnit5Minutests
import dev.minutest.rootContext
import com.netflix.spinnaker.orca.api.test.OrcaFixture
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.*
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class FileControllerTest : JUnit5Minutests {

  fun tests() = rootContext<Fixture> {
    orcaFixture {
      Fixture()
    }

    context("file controller; user has READ and WRITE but not EXECUTE permissions") {
      test("allows access to user with file READ authorization") {
        val response = subject.get("/file/read/gradle.properties") {
          withAuthFor("daniel.peach@armory.io")
        }.andReturn().response

        expectThat(response) {
          get { status }.isEqualTo(200)
          get { contentAsString }.isEqualTo("Congrats! You can read gradle.properties!")
        }
      }

      test("allows access to user with file WRITE authorization") {
        val response = subject.get("/file/write/gradle.properties") {
          withAuthFor("daniel.peach@armory.io")
        }.andReturn().response

        expectThat(response) {
          get { status }.isEqualTo(200)
          get { contentAsString }.isEqualTo("Congrats! You can write gradle.properties!")
        }
      }

      test("denies access to user without file EXECUTE authorization") {
        val response = subject.get("/file/execute/gradle.properties") {
          withAuthFor("daniel.peach@armory.io")
        }.andReturn().response

        expectThat(response) {
          get { status }.isEqualTo(403)
        }
      }
    }
  }

  @AutoConfigureMockMvc
  @ContextConfiguration(classes = [FileControllerTestConfiguration::class])
  @TestPropertySource(properties = ["services.fiat.enabled=true"])
  inner class Fixture : OrcaFixture() {

    @Autowired
    lateinit var subject: MockMvc
  }

  private fun MockHttpServletRequestDsl.withAuthFor(user: String): MockHttpServletRequestDsl {
    this.headers {
      add("X-SPINNAKER-USER", user)
    }
    return this
  }

}

@TestConfiguration
class FileControllerTestConfiguration {
  @Bean
  fun fileController(): FileController {
    return FileController()
  }

  @Bean
  fun fiatService(): FiatService {
    return mockk("fiatService") {
      every { getUserPermission("daniel.peach@armory.io") } returns UserPermission().apply {
        id = "daniel.peach@armory.io"
        roles = mutableSetOf(Role("engineers"))
        addResource(File().apply {
          name = "gradle.properties"
          permissions = Permissions.Builder().apply {
            this[Authorization.READ] = mutableListOf("engineers")
            this[Authorization.WRITE] = mutableListOf("engineers")
          }.build()
        })
      }.view
    }
  }
}

@RestController
class FileController {

  @PreAuthorize("hasPermission(#name, 'FILE', 'READ')")
  @GetMapping("/file/read/{name}")
  fun readFile(@PathVariable name: String): String {
    return "Congrats! You can read $name!"
  }

  @PreAuthorize("hasPermission(#name, 'FILE', 'WRITE')")
  @GetMapping("/file/write/{name}")
  fun writeFile(@PathVariable name: String): String {
    return "Congrats! You can write $name!"
  }

  @PreAuthorize("hasPermission(#name, 'FILE', 'EXECUTE')")
  @GetMapping("/file/execute/{name}")
  fun executeFile(@PathVariable name: String): String {
    return "Congrats! You can execute $name!"
  }
}
