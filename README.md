# Verik Guide

## Context

`@Virtual` : Component is expected at simulation time so sources are not transpiled

## Data Types

`val type = Type()` : Construct type

`val type = Type() array "8"` : Construct array of type

`type[0]` : Index element of type array

`type[4:0]` : Index range of type array

`type con <expr>` : Connect combinationally

`type set <expr>` : Set sequentially

`type.randomize()` : Randomize value

### Bool

### Bit

### Signed

### Unsigned

### Enumer

## Nets

### IO Type

### `@In`

### `@Out`

### `@InOut`

### Net Type

### `@Wire`

### `@Reg`

### Routines

### Task

### Function

## Components

### Circuit

### Module

### Interface

### Port

### Class

## Control Flow

### Initial

### Always

## Verik Commands

### Control Flow

### Logging