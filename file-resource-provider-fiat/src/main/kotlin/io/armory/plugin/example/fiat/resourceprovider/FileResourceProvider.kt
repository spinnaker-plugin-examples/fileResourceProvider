package io.armory.plugin.example.fiat.resourceprovider

import com.netflix.spinnaker.fiat.model.Authorization
import com.netflix.spinnaker.fiat.model.resources.Permissions
import com.netflix.spinnaker.fiat.providers.BaseResourceProvider
import java.io.File as JavaFile
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermission

class FileResourceProvider(private val config: FileResourceConfigurationProperties) : BaseResourceProvider<File>() {
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

    val posix = Files.getFileAttributeView(Paths.get(config.path), PosixFileAttributeView::class.java).readAttributes()
    val group = posix.group().name
    val builder = Permissions.Builder()

    posix.permissions().forEach { permission ->
      when (permission) {
        PosixFilePermission.GROUP_EXECUTE -> builder[Authorization.EXECUTE] = mutableListOf(group)
        PosixFilePermission.GROUP_WRITE -> builder[Authorization.WRITE] = mutableListOf(group)
        PosixFilePermission.GROUP_READ -> builder[Authorization.READ] = mutableListOf(group)
        else -> Unit
      }
    }

    return mutableSetOf(File().apply {
      name = JavaFile(config.path).name
      permissions = builder.build()
    })
  }
}


