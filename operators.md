# Operators

## Native

```
+a                SNum(n)         -> SNum(n)
                  UNum(n)         -> UNum(n)

-a                SNum(n)         -> SNum(n)
                  UNum(n)         -> UNum(n)

!a                Bits(n)         -> Bool
                  SNum(n)         -> Bool
                  UNum(n)         -> Bool

a + b             Int SNum(n)     -> SNum(n)
                  Int UNum(n)     -> UNum(n)
                  Str(n) SNum(m)  -> SNum(max(n, m)
                  Str(n) UNum(m)  -> UNum(max(n, m)
                  SNum(n) Int     -> SNum(n)
                  SNum(n) Str(m)  -> SNum(max(n, m))
                  SNum(n) SNum(m) -> SNum(max(n, m))
                  SNum(n) UNum(m) -> UNum(max(n, m))
                  UNum(n) Int     -> UNum(n)
                  UNum(n) Str(m)  -> UNum(max(n, m))
                  UNum(n) SNum(m) -> UNum(max(n, m))
                  UNum(n) UNum(m) -> UNum(max(n, m))

a - b             Int SNum(n)     -> SNum(n)
                  Int UNum(n)     -> UNum(n)
                  Str(n) SNum(m)  -> SNum(max(n, m)
                  Str(n) UNum(m)  -> UNum(max(n, m)
                  SNum(n) Int     -> SNum(n)
                  SNum(n) Str(m)  -> SNum(max(n, m))
                  SNum(n) SNum(m) -> SNum(max(n, m))
                  SNum(n) UNum(m) -> UNum(max(n, m))
                  UNum(n) Int     -> UNum(n)
                  UNum(n) Str(m)  -> UNum(max(n, m))
                  UNum(n) SNum(m) -> UNum(max(n, m))
                  UNum(n) UNum(m) -> UNum(max(n, m))

a * b             Int SNum(n)     -> SNum(n)
                  Int UNum(n)     -> UNum(n)
                  Str(n) SNum(m)  -> SNum(max(n, m)
                  Str(n) UNum(m)  -> UNum(max(n, m)
                  SNum(n) Int     -> SNum(n)
                  SNum(n) Str(m)  -> SNum(max(n, m))
                  SNum(n) SNum(m) -> SNum(max(n, m))
                  SNum(n) UNum(m) -> UNum(max(n, m))
                  UNum(n) Int     -> UNum(n)
                  UNum(n) Str(m)  -> UNum(max(n, m))
                  UNum(n) SNum(m) -> UNum(max(n, m))
                  UNum(n) UNum(m) -> UNum(max(n, m))

a > b             Int SNum(n)     -> Bool
                  Int UNum(n)     -> Bool
                  Str(n) SNum(n)  -> Bool
                  Str(n) UNum(n)  -> Bool
                  SNum(n) Int     -> Bool
                  SNum(n) Str(n)  -> Bool
                  SNum(n) SNum(n) -> Bool
                  UNum(n) Int     -> Bool
                  UNum(n) Str(n)  -> Bool
                  UNum(n) UNum(n) -> Bool

a >= b            Int SNum(n)     -> Bool
                  Int UNum(n)     -> Bool
                  Str(n) SNum(n)  -> Bool
                  Str(n) UNum(n)  -> Bool
                  SNum(n) Int     -> Bool
                  SNum(n) Str(n)  -> Bool
                  SNum(n) SNum(n) -> Bool
                  UNum(n) Int     -> Bool
                  UNum(n) Str(n)  -> Bool
                  UNum(n) UNum(n) -> Bool

a < b             Int SNum(n)     -> Bool
                  Int UNum(n)     -> Bool
                  Str(n) SNum(n)  -> Bool
                  Str(n) UNum(n)  -> Bool
                  SNum(n) Int     -> Bool
                  SNum(n) Str(n)  -> Bool
                  SNum(n) SNum(n) -> Bool
                  UNum(n) Int     -> Bool
                  UNum(n) Str(n)  -> Bool
                  UNum(n) UNum(n) -> Bool

a <= b            Int SNum(n)     -> Bool
                  Int UNum(n)     -> Bool
                  Str(n) SNum(n)  -> Bool
                  Str(n) UNum(n)  -> Bool
                  SNum(n) Int     -> Bool
                  SNum(n) Str(n)  -> Bool
                  SNum(n) SNum(n) -> Bool
                  UNum(n) Int     -> Bool
                  UNum(n) Str(n)  -> Bool
                  UNum(n) UNum(n) -> Bool

a[p]              Bits(n)         -> Bits(1)
                  SNum(n)         -> SNum(1)
                  UNum(n)         -> UNum(1)

a[p, q]           Bits(n)         -> Bits(p - q)
                  SNum(n)         -> SNum(p - q)
                  UNum(n)         -> UNum(p - q)
```

## Infix

```
a addFull b       Int SNum(n)     -> SNum(n + 1)
                  Int UNum(n)     -> UNum(n + 1)
                  Str(n) SNum(m)  -> SNum(max(n, m) + 1)
                  Str(n) UNum(m)  -> UNum(max(n, m) + 1)
                  SNum(n) Int     -> SNum(n + 1)
                  SNum(n) Str(m)  -> SNum(max(n, m) + 1)
                  SNum(n) SNum(m) -> SNum(max(n, m) + 1)
                  SNum(n) UNum(m) -> UNum(max(n, m) + 1)
                  UNum(n) Int     -> UNum(n + 1)
                  UNum(n) Str(m)  -> UNum(max(n, m) + 1)
                  UNum(n) SNum(m) -> UNum(max(n, m) + 1)
                  UNum(n) UNum(m) -> UNum(max(n, m) + 1)

a mulFull b       Int SNum(n)     -> SNum(2 * n)
                  Int UNum(n)     -> UNum(2 * n)
                  Str(n) SNum(m)  -> SNum(n + m)
                  Str(n) UNum(m)  -> UNum(n + m)
                  SNum(n) Int     -> SNum(2 * n)
                  SNum(n) Str(m)  -> SNum(n + m)
                  SNum(n) SNum(m) -> SNum(n + m)
                  SNum(n) UNum(m) -> UNum(n + m)
                  UNum(n) Int     -> UNum(2 * n)
                  UNum(n) Str(m)  -> UNum(n + m)
                  UNum(n) SNum(m) -> UNum(n + m)
                  UNum(n) UNum(m) -> UNum(n + m)

a sl b            Bits(n) Int     -> Bits(n)
                  Bits(n) Str(m)  -> Bits(n)
                  Bits(n) UNum(m) -> Bits(n)
                  SNum(n) Int     -> SNum(n)
                  SNum(n) Str(m)  -> Bits(n)
                  SNum(n) UNum(m) -> SNum(n)
                  UNum(n) Int     -> UNum(n)
                  UNum(n) Str(m)  -> Bits(n)
                  UNum(n) UNum(m) -> UNum(n)

a sr b            Bits(n) Int     -> Bits(n)
                  Bits(n) Str(m)  -> Bits(n)
                  Bits(n) UNum(m) -> Bits(n)
                  SNum(n) Int     -> SNum(n)
                  SNum(n) Str(m)  -> Bits(n)
                  SNum(n) UNum(m) -> SNum(n)
                  UNum(n) Int     -> UNum(n)
                  UNum(n) Str(m)  -> Bits(n)
                  UNum(n) UNum(m) -> UNum(n)

a rot b           Bits(n) Int     -> Bits(n)
                  Bits(n) Str(m)  -> Bits(n)
                  Bits(n) UNum(m) -> Bits(n)
                  SNum(n) Int     -> SNum(n)
                  SNum(n) Str(m)  -> Bits(n)
                  SNum(n) UNum(m) -> SNum(n)
                  UNum(n) Int     -> UNum(n)
                  UNum(n) Str(m)  -> Bits(n)
                  UNum(n) UNum(m) -> UNum(n)

a and b           Int Bits(n)     -> Bits(n)
                  Int SNum(n)     -> SNum(n)
                  Int UNum(n)     -> UNum(n)
                  Str(n) Bits(n)  -> Bits(n)
                  Str(n) SNum(n)  -> SNum(n)
                  Str(n) UNum(n)  -> UNum(n)
                  Bits(n) Bits(n) -> Bits(n)
                  SNum(n) SNum(n) -> SNum(n)
                  UNum(n) UNum(n) -> UNum(n)

a or b            Int Bits(n)     -> Bits(n)
                  Int SNum(n)     -> SNum(n)
                  Int UNum(n)     -> UNum(n)
                  Str(n) Bits(n)  -> Bits(n)
                  Str(n) SNum(n)  -> SNum(n)
                  Str(n) UNum(n)  -> UNum(n)
                  Bits(n) Bits(n) -> Bits(n)
                  SNum(n) SNum(n) -> SNum(n)
                  UNum(n) UNum(n) -> UNum(n)

a xor b           Int Bits(n)     -> Bits(n)
                  Int SNum(n)     -> SNum(n)
                  Int UNum(n)     -> UNum(n)
                  Str(n) Bits(n)  -> Bits(n)
                  Str(n) SNum(n)  -> SNum(n)
                  Str(n) UNum(n)  -> UNum(n)
                  Bits(n) Bits(n) -> Bits(n)
                  SNum(n) SNum(n) -> SNum(n)
                  UNum(n) UNum(n) -> UNum(n)

a cat b           Str(n) Bits(m)  -> Bits(n + m)
                  Str(n) SNum(m)  -> SNum(n + m)
                  Str(n) UNum(m)  -> UNum(n + m)
                  Bool Bits(n)    -> Bits(n + 1)
                  Bool SNum(n)    -> SNum(n + 1)
                  Bool UNum(n)    -> UNum(n + 1)
                  Bits(n) Bool    -> Bits(n + 1)
                  Bits(n) Str(m)  -> Bits(n + m)
                  Bits(n) Bits(m) -> Bits(n + m)
                  Bits(n) SNum(m) -> SNum(n + m)
                  Bits(n) UNum(m) -> UNum(n + m)
                  SNum(n) Bool    -> SNum(n + 1)
                  SNum(n) Str(m)  -> SNum(n + m)
                  SNum(n) Bits(m) -> SNum(n + m)
                  SNum(n) SNum(m) -> SNum(n + m)
                  SNum(n) UNum(m) -> UNum(n + m)
                  UNum(n) Bool    -> UNum(n + 1)
                  UNum(n) Str(m)  -> UNum(n + m)
                  UNum(n) Bits(m) -> UNum(n + m)
                  UNum(n) SNum(m) -> UNum(n + m)
                  UNum(n) UNum(m) -> UNum(n + m)
```

## Function

```
inv(a)            Bits(n)         -> Bits(n)
                  SNum(n)         -> SNum(n)
                  UNum(n)         -> UNum(n)

redAnd(a)         Bits(n)         -> Bool
                  SNum(n)         -> Bool
                  UNum(n)         -> Bool

redOr(a)          Bits(n)         -> Bool
                  SNum(n)         -> Bool
                  UNum(n)         -> Bool

redNand(a)        Bits(n)         -> Bool
                  SNum(n)         -> Bool
                  UNum(n)         -> Bool

redNor(a)         Bits(n)         -> Bool
                  SNum(n)         -> Bool
                  UNum(n)         -> Bool

redXor(a)         Bits(n)         -> Bool
                  SNum(n)         -> Bool
                  UNum(n)         -> Bool

redXnor(a)        Bits(n)         -> Bool
                  SNum(n)         -> Bool
                  UNum(n)         -> Bool
```

## Cast

```
len(a)            Data(n)         -> Int

pack(a)           Data(n)         -> Bits(n)

unpack(A(), b)    Data(n) Bit(n)  -> Data(n)

Bool.of(a)        Bits(1)         -> Bool
                  SNum(1)         -> Bool
                  UNum(1)         -> Bool

Bits.of(a)        Int             -> Bits(n)
                  Str(n)          -> Bits(n)
                  SNum(n)         -> Bits(n)
                  UNum(n)         -> Bits(n)

SNum.of(a)        Int             -> SNum(n)
                  Str(n)          -> SNum(n)
                  Bits(n)         -> SNum(n)
                  UNum(n)         -> SNum(n)

UNum.of(a)        Int             -> UNum(n)
                  Str(n)          -> UNum(n)
                  Bits(n)         -> UNum(n)
                  SNum(n)         -> UNum(n)
```

