package com.verik.core

import kotlin.system.exitProcess

// Copyright (c) 2020 Francis Wang

class StatusPrinter {

    companion object {

        fun info(message: String) {
            println("INFO: $message")
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