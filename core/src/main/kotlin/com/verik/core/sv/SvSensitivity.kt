package com.verik.core.sv

// Copyright (c) 2020 Francis Wang

enum class SvSensitivityType {
    POSEDGE,
    NEGEDGE
}

data class SvSensitivityEntry (
        val type: SvSensitivityType,
        val identifier: String
) {

    fun build(): String {
        return when (type) {
            SvSensitivityType.POSEDGE -> "posedge $identifier"
            SvSensitivityType.NEGEDGE -> "negedge $identifier"
        }
    }
}
