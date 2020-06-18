package io.armory.plugin.example.fiat.resourceprovider

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("file")
class FileResourceConfigurationProperties {
  var path: String = "/must/configure"
}
