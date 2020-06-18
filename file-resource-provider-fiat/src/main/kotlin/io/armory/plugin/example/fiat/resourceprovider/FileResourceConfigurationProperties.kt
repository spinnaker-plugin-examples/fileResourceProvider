package io.armory.plugin.example.fiat.resourceprovider

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("file-resource-provider")
data class FileResourceConfigurationProperties(val path: String)
