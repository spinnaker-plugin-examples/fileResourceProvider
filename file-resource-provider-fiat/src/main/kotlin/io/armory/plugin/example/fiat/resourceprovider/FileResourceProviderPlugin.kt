package io.armory.plugin.example.fiat.resourceprovider

import com.netflix.spinnaker.kork.plugins.api.spring.PrivilegedSpringPlugin
import org.slf4j.LoggerFactory
import org.pf4j.PluginWrapper
import org.springframework.beans.factory.support.BeanDefinitionRegistry

class FileResourceProviderPlugin(wrapper: PluginWrapper) : PrivilegedSpringPlugin(wrapper) {
  private val logger = LoggerFactory.getLogger(FileResourceProvider::class.java)

  override fun registerBeanDefinitions(registry: BeanDefinitionRegistry) {
    listOf(
        beanDefinitionFor(FileResourceProvider::class.java),
        beanDefinitionFor(File::class.java),
        beanDefinitionFor(FileResourceConfigurationProperties::class.java)
    ).forEach {
      registerBean(it, registry)
    }
  }

  override fun start() {
    logger.info("FileResourceProviderPlugin.start()")
  }

  override fun stop() {
    logger.info("FileResourceProviderPlugin.stop()")
  }
}
