package io.armory.plugin.example.fiat.resourceprovider

import com.netflix.spinnaker.fiat.model.Authorization
import com.netflix.spinnaker.fiat.model.resources.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermission

class File: BaseAccessControlled<File>(), Viewable {

  private var path: String = "/Users/danielpeach/dev/spinnaker/fiat/gradle.properties"

  private var permissions: Permissions = Permissions.EMPTY

  override fun getName(): String {
    return java.io.File(path).name
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

  override fun setPermissions(permissions: Permissions): File {
    this.permissions = permissions
    return this
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
