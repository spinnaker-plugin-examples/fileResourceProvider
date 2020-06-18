package io.armory.plugin.example.fiat.resourceprovider

import com.netflix.spinnaker.fiat.model.Authorization
import com.netflix.spinnaker.fiat.model.resources.*

class File: BaseAccessControlled<File>(), Viewable {

  private var name: String = ""
  private var permissions: Permissions = Permissions.EMPTY

  // BaseAccessControlled expects a lot of these getters and setters,
  // which need to be explicitly overridden rather than relying on
  // the Kotlin-generated getters and setters.
  override fun getResourceType() = ResourceType("file")

  override fun getPermissions() = permissions

  override fun setPermissions(permissions: Permissions): File {
    this.permissions = permissions
    return this
  }

  override fun getName() = name

  fun setName(name: String): File {
    this.name = name
    return this
  }

  override fun getView(userRoles: MutableSet<Role>, isAdmin: Boolean) = View(this, userRoles, isAdmin)

  // View must implement Authorizable. Otherwise, it will not be threaded through into UserPermission.View.
  // Eventually this should be a compile-time error (or somehow baked into a future plugin extension interface).
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
