# Typed Grouped AVP — Requirements

## Problem

Grouped AVPs with a defined inner structure (e.g. `User-Identifier`, `Serving-Node`,
`SM-Delivery-Outcome`) are currently returned as raw `GroupedAVP` instances. Callers
must know the inner AVP codes, look them up manually, and cast — the same ceremony the
mixin pattern eliminates for Commands.

## Requirements

1. Grouped AVPs with a defined inner structure expose typed accessors via the same mixin
   pattern used by Commands and Requests.

2. `AVP.readFrom()` produces the registered typed subclass when one exists for the decoded
   AVP code + vendor ID. Callers receive the typed instance directly; no post-decode cast
   or wrapping is required.

3. The write path is symmetric: a typed subclass instance is accepted wherever a grouped
   AVP is set on a Command.

4. For grouped AVPs without a registered typed subclass, behaviour is unchanged — a plain
   `GroupedAVP` is produced as today.

5. `GroupedAVP` becomes a valid `AVPContainer` so mixin interfaces can be applied to it
   and its subclasses.
