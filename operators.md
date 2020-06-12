# Operators

## Native

### `a && b`, `a || b`
```
Bool Bool -> Bool
```

### `+a`, `-a`
```
SNum(n) -> SNum(n)
UNum(n) -> UNum(n)
```

### `!a`
```
Bool    -> Bool
Bits(n) -> Bool
SNum(n) -> Bool
UNum(n) -> Bool
```

### `a + b`, `a - b`, `a * b`
```
Value SNum(n)    -> SNum(n)
Value UNum(n)    -> UNum(n)
Value(n) SNum(m) -> SNum(max(n, m))
Value(n) UNum(m) -> UNum(max(n, m))
SNum(n) Value    -> SNum(n)
SNum(n) Value(m) -> SNum(max(n, m))
SNum(n) SNum(m)  -> SNum(max(n, m))
SNum(n) UNum(m)  -> UNum(max(n, m))
UNum(n) Value    -> UNum(n)
UNum(n) Value(m) -> UNum(max(n, m))
UNum(n) SNum(m)  -> UNum(max(n, m))
UNum(n) UNum(m)  -> UNum(max(n, m))
```

### `a > b`, `a >= b`, `a < b`, `a <= b`
```
SNum(n) SNum(n)  -> Bool
UNum(n) UNum(n)  -> Bool
```

### `a[p]`
```
Bits(n) -> Bool
SNum(n) -> Bool
UNum(n) -> Bool
```

### `a[p, q]`
```
Bits(n) -> Bits(p - q)
SNum(n) -> SNum(p - q)
UNum(n) -> UNum(p - q)
```

## Infix

### `a add b`, `a sub b`
```
Value SNum(n)    -> SNum(n + 1)
Value UNum(n)    -> UNum(n + 1)
Value(n) SNum(m) -> SNum(max(n, m) + 1)
Value(n) UNum(m) -> UNum(max(n, m) + 1)
SNum(n) Value    -> SNum(n + 1)
SNum(n) Value(m) -> SNum(max(n, m) + 1)
SNum(n) SNum(m)  -> SNum(max(n, m) + 1)
SNum(n) UNum(m)  -> UNum(max(n, m) + 1)
UNum(n) Value    -> UNum(n + 1)
UNum(n) Value(m) -> UNum(max(n, m) + 1)
UNum(n) SNum(m)  -> UNum(max(n, m) + 1)
UNum(n) UNum(m)  -> UNum(max(n, m) + 1)
```

### `a mul b`
```
Value SNum(n)    -> SNum(2 * n)
Value UNum(n)    -> UNum(2 * n)
Value(n) SNum(m) -> SNum(n + m)
Value(n) UNum(m) -> UNum(n + m)
SNum(n) Value    -> SNum(2 * n)
SNum(n) Value(m) -> SNum(n + m)
SNum(n) SNum(m)  -> SNum(n + m)
SNum(n) UNum(m)  -> UNum(n + m)
UNum(n) Value    -> UNum(2 * n)
UNum(n) Value(m) -> UNum(n + m)
UNum(n) SNum(m)  -> UNum(n + m)
UNum(n) UNum(m)  -> UNum(n + m)
```

### `a sl b`, `a sr b`, `a rotl b`, `a rotr b`
```
Bits(n) Int      -> Bits(n)
Bits(n) Value    -> Bits(n)
Bits(n) Value(m) -> Bits(n)
Bits(n) UNum(m)  -> Bits(n)
SNum(n) Int      -> SNum(n)
SNum(n) Value    -> SNum(n)
SNum(n) Value(m) -> SNum(n)
SNum(n) UNum(m)  -> SNum(n)
UNum(n) Int      -> UNum(n)
UNum(n) Value    -> UNum(n)
UNum(n) Value(m) -> UNum(n)
UNum(n) UNum(m)  -> UNum(n)
```

### `a slExt b`
```
Bits(n) Int -> Bits(n + b)
SNum(n) Int -> SNum(n + b)
UNum(n) Int -> UNum(n + b)
```

### `a srTru b`
```
Bits(n) Int -> Bits(n - b)
SNum(n) Int -> SNum(n - b)
UNum(n) Int -> UNum(n - b)
```

### `a and b`, `a or b`, `a xor b`,<br/>`a nand b`, `a nor b`, `a xnor b`
```
Value Bits(n)    -> Bits(n)
Value SNum(n)    -> SNum(n)
Value UNum(n)    -> UNum(n)
Value(n) Bits(n) -> Bits(n)
Value(n) SNum(n) -> SNum(n)
Value(n) UNum(n) -> UNum(n)
Bool Bool        -> Bool
Bits(n) Value    -> Bits(n)
Bits(n) Value(n) -> Bits(n)
Bits(n) Bits(n)  -> Bits(n)
SNum(n) Value    -> SNum(n)
SNum(n) Value(n) -> SNum(n)
SNum(n) SNum(n)  -> SNum(n)
UNum(n) Value    -> UNum(n)
UNum(n) Value(n) -> UNum(n)
UNum(n) UNum(n)  -> UNum(n)
```

### `a cat b`
```
Value(n) Bool    -> Bits(n + 1))
Value(n) Bits(m) -> Bits(n + m)
Value(n) SNum(m) -> SNum(n + m)
Value(n) UNum(m) -> UNum(n + m)
Bool Value(n)    -> Bits(n + 1)
Bool Bool        -> Bits(2)
Bool Bits(n)     -> Bits(n + 1)
Bool SNum(n)     -> SNum(n + 1)
Bool UNum(n)     -> UNum(n + 1)
Bits(n) Bool     -> Bits(n + 1)
Bits(n) Value(m) -> Bits(n + m)
Bits(n) Bits(m)  -> Bits(n + m)
Bits(n) SNum(m)  -> SNum(n + m)
Bits(n) UNum(m)  -> UNum(n + m)
SNum(n) Bool     -> SNum(n + 1)
SNum(n) Value(m) -> SNum(n + m)
SNum(n) Bits(m)  -> SNum(n + m)
SNum(n) SNum(m)  -> SNum(n + m)
SNum(n) UNum(m)  -> UNum(n + m)
UNum(n) Bool     -> UNum(n + 1)
UNum(n) Value(m) -> UNum(n + m)
UNum(n) Bits(m)  -> UNum(n + m)
UNum(n) SNum(m)  -> UNum(n + m)
UNum(n) UNum(m)  -> UNum(n + m)
```

## Function

### `inv(a)`
```
Bits(n) -> Bits(n)
SNum(n) -> SNum(n)
UNum(n) -> UNum(n)
```

### `redAnd(a)`, `redOr(a)`, `redXor(a)`,<br/>`redNand(a)`, `redNor(a)`, `redXnor(a)`
```
Bits(n) -> Bool
SNum(n) -> Bool
UNum(n) -> Bool
```

### `max(a, b)`, `min(a, b)`
```
Value SNum(n)    -> SNum(n)
Value UNum(n)    -> UNum(n)
Value(n) SNum(n) -> SNum(n)
Value(n) UNum(n) -> UNum(n)
SNum(n) SNum(n)  -> SNum(n)
UNum(n) UNum(n)  -> UNum(n)
```

### `signed(a)`
```
Bits(n) -> SNum(n)
UNum(n) -> SNum(n)
```

### `unsigned(a)`
```
Bits(n) -> UNum(n)
SNum(n) -> UNum(n)
```

## Other

### `Value(A(), b)`

```
Bits(n) Int    -> Bits(n)
Bits(n) String -> Bits(n)
SNum(n) Int    -> SNum(n)
SNum(n) String -> SNum(n)
UNum(n) Int    -> UNum(n)
UNum(n) String -> UNum(n)
```

### `len(a)`
```
Data(n) -> Int
```

### `ext(n, a)`, `tru(n, a)`
```
Int Bits(m) -> Bits(n)
Int SNum(m) -> SNum(n)
Int UNum(m) -> UNum(n)
```

### `pack(a)`

```
Bool    -> Bits(1)
Data(n) -> Bits(n)
```

### `unpack(A(), b)`

```
Bool Bits(1)    -> Bool
Bits(n) Bits(n) -> Bits(n)
SNum(n) Bits(n) -> SNum(n)
UNum(n) Bits(n) -> UNum(n)
Data(n) Bits(n) -> Data(n)
```

