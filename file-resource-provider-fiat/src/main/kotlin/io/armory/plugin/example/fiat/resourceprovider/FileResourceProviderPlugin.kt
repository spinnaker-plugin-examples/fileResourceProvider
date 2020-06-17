package io.armory.plugin.example.fiat.resourceprovider

import com.netflix.spinnaker.kork.plugins.api.spring.PrivilegedSpringPlugin
import org.slf4j.LoggerFactory
import org.pf4j.PluginWrapper
import org.springframework.beans.factory.support.BeanDefinitionRegistry

class FileResourceProviderPlugin(wrapper: PluginWrapper) : PrivilegedSpringPlugin(wrapper) {

  override fun registerBeanDefinitions(registry: BeanDefinitionRegistry) {
    registerBean(primaryBeanDefinitionFor(FileResourceProvider::class.java), registry)
  }

  private val logger = LoggerFactory.getLogger(FileResourceProvider::class.java)

  override fun start() {
    logger.info("FileResourceProviderPlugin.start()")
  }

  override fun stop() {
    logger.info("FileResourceProviderPlugin.stop()")
  }
}
