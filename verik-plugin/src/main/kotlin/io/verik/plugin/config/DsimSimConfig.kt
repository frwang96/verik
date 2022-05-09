/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

/**
 * Configuration for a Metrics dsim simulation.
 */
data class DsimSimConfig(
    val name: String,
    val simTop: String,
    val plusArgs: Map<String, String>
)
