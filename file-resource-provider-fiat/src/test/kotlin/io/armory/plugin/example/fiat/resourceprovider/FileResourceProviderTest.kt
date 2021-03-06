package io.armory.plugin.example.fiat.resourceprovider

import com.netflix.spinnaker.fiat.model.Authorization
import com.netflix.spinnaker.fiat.model.UserPermission
import com.netflix.spinnaker.fiat.model.resources.*
import dev.minutest.junit.JUnit5Minutests
import dev.minutest.rootContext
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermissions

class FileResourceProviderTest : JUnit5Minutests {

  fun tests() = rootContext<Fixture> {

    fixture {
      Fixture()
    }

    test("resource provider loads file permissions") {
      expectThat(subject.all.first().permissions) {
        get { get(Authorization.READ) }.isEqualTo(mutableListOf(group))
        get { get(Authorization.EXECUTE) }.isEqualTo(mutableListOf(group))
      }
    }

    test("file resources can be mapped through the UserPermission class") {
      val up = UserPermission().apply {
        addResources(subject.all as Collection<Resource>)
        roles = setOf(role)
      }

      expectThat(up.view)
          .isA<UserPermission.View>()
          .get { extensionResources[ResourceType("file")] }
            .isA<Set<Authorizable>>()
            .and {
              get { first().name }.isEqualTo("test.txt")
              get { first().authorizations }.contains(Authorization.READ, Authorization.EXECUTE)
            }
    }
  }

  inner class Fixture {
    val testPath = javaClass.classLoader.getResource("test.txt")?.path ?: throw IllegalStateException("whoops!")
    val group = Files.getFileAttributeView(Paths.get(testPath), PosixFileAttributeView::class.java).readAttributes().group().name
    val role = Role(group)

    val subject = FileResourceProvider(FileResourceConfigurationProperties().apply { path = testPath })

    init {
      Files.setPosixFilePermissions(Paths.get(testPath), PosixFilePermissions.fromString("---r-x---"))
    }
  }
}

