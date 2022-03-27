/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin

import org.gradle.api.GradleException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

object ConfigUtil {

    fun getVersion(): String {
        val properties = Properties()
        properties.load(this::class.java.classLoader.getResourceAsStream("verik-plugin.properties"))
        return properties.getProperty("version")
            ?: throw GradleException("Verik configuration failed: Could not determine version")
    }

    fun getToolchain(): String {
        val properties = Properties()
        properties.load(this::class.java.classLoader.getResourceAsStream("verik-plugin.properties"))
        return properties.getProperty("toolchain")
            ?: throw GradleException("Verik configuration failed: Could not determine toolchain")
    }

    fun getTimestamp(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"))
    }
}
