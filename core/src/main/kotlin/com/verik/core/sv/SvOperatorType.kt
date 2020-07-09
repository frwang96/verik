package com.verik.core.sv

// Copyright (c) 2020 Francis Wang

enum class SvOperatorType {

    // operator
    DELAY,
    ADD,
    SUB,
    MUL,
    AND,
    OR,

    // assignment
    BASSIGN;

    fun precedence(): Int {
        return when (this) {
            DELAY -> 0
            MUL -> 3
            ADD, SUB -> 4
            AND -> 11
            OR -> 12
            BASSIGN -> 15
        }
    }
}