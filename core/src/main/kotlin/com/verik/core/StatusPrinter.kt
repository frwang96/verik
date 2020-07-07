package com.verik.core

import java.lang.Exception
import kotlin.system.exitProcess

// Copyright (c) 2020 Francis Wang

enum class Verbosity {
    REGULAR,
    HIGH
}

class StatusPrinter {

    companion object {

        var verbosity = Verbosity.REGULAR

        fun info(message: String) {
            if (verbosity == Verbosity.HIGH) {
                println("INFO: $message")
            }
        }

        fun warning(message: String) {
            print("\u001B[33m") // ANSI yellow
            print("WARNING: $message")
            print("\u001B[0m\n") // ANSI reset
        }

        fun error(message: String?, exception: Exception): Nothing {
            print("\u001B[31m") // ANSI red
            print("ERROR:")
            if (message != null) print(" $message")
            print("\u001B[0m\n") // ANSI reset
            println("${exception::class.simpleName} at")
            for (trace in exception.stackTrace) {
                println("\t$trace")
            }
            exitProcess(1)
        }

    }
}