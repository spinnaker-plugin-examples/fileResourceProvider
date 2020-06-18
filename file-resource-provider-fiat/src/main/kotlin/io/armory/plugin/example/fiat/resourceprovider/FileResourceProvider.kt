package io.armory.plugin.example.fiat.resourceprovider

import com.netflix.spinnaker.fiat.model.Authorization
import com.netflix.spinnaker.fiat.model.resources.*
import com.netflix.spinnaker.fiat.providers.BaseResourceProvider
import java.io.File as JavaFile
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermission

class FileResourceProvider : BaseResourceProvider<File>() {
  private val config = FileResourceConfigurationProperties(path = "/Users/danielpeach/dev/spinnaker/fiat/gradle.properties")

  private val logger = LoggerFactory.getLogger(FileResourceProvider::class.java)

  init {
    JavaFile(config.path).also {
      if (!it.exists()) {
        throw IllegalArgumentException("File ${config.path} does not exist")
      }
      if (!it.isFile) {
        throw IllegalArgumentException("${config.path} is not a file")
      }
    }
  }

  override fun loadAll(): MutableSet<File> {
    logger.info("Loading file {}...", config.path)
    return mutableSetOf(File())
  }
}


