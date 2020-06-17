package io.armory.plugin.example.fiat.resourceprovider

import com.netflix.spinnaker.kork.plugins.api.PluginConfiguration

@PluginConfiguration("armory.fileResourceProvider")
data class Config(val path: String)
