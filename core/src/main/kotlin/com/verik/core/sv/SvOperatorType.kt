package com.verik.core.sv

// Copyright (c) 2020 Francis Wang

enum class SvOperatorType {

    // operator
    DELAY,
    NOT,
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
            NOT -> 2
            MUL -> 6
            ADD, SUB -> 7
            AND -> 15
            OR -> 16
            BASSIGN -> 18
        }
    }
}