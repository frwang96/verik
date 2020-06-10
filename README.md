# Verik Guide

## Context

`@Virtual` : Component is expected at simulation time so sources are not transpiled

## Data Types

`val type = Type()` : Construct type

`val type = Type() array "8"` : Construct array of type

`type[0]` : Index element of type array

`type[4:0]` : Index range of type array

`type set <expr>` : Set value of type

### Native

`Bool` : Boolean value of either `true` or `false`

`Bits(n)` : Group of n bits

`SNum(n)` :   Signed number of n bits

`UNum(n)` : Unsigned number of n bits

Legal operators on native types is defined in [operators](operators.md)

### User Defined

#### Enums

#### Structs

## Nets

### IO Type

### `@In`

### `@Out`

## Routines

### Tasks

### Functions

## Components

### Circuit

### Module

### Interface

### Class

## Control Flow

### Initial

### Always

## Verik Commands

### Control Flow

### Logging