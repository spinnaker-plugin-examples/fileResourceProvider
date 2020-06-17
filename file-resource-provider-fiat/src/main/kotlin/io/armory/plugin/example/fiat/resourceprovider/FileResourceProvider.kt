package io.armory.plugin.example.fiat.resourceprovider

import com.netflix.spinnaker.fiat.model.Authorization
import com.netflix.spinnaker.fiat.model.resources.*
import com.netflix.spinnaker.fiat.providers.BaseResourceProvider
import java.io.File as JavaFile
import org.pf4j.Extension
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermission

@Extension
class FileResourceProvider(private val config: Config) : BaseResourceProvider<File>() {
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
    return mutableSetOf(File(config.path))
  }
}

class File(private val path: String): BaseAccessControlled<File>(), Viewable {

  private var permissions: Permissions = Permissions.EMPTY

  override fun getName(): String {
    return JavaFile(path).name
  }

  override fun getResourceType(): ResourceType {
    return ResourceType("file")
  }

  override fun getPermissions(): Permissions {
    val posix = Files.getFileAttributeView(Paths.get(path), PosixFileAttributeView::class.java).readAttributes()
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
    return builder.build()
  }

  override fun getView(userRoles: MutableSet<Role>, isAdmin: Boolean): Viewable.BaseView {
    return View(this, userRoles, isAdmin)
  }

  inner class View(file: File, userRoles: MutableSet<Role>, isAdmin: Boolean) : Viewable.BaseView(), Authorizable {
    private val name: String = file.name
    private val authorizations: MutableSet<Authorization> = if (isAdmin) {
      Authorization.ALL
    } else {
      file.permissions.getAuthorizations(userRoles)
    }

    override fun getName() = name
    override fun getAuthorizations() = authorizations
  }
}

