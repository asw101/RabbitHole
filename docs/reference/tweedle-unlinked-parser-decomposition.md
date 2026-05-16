# TweedleUnlinkedParser Visitor Extraction

This reference describes the extraction of three inner types from
`TweedleUnlinkedParser.java` in the `org.alice.tweedle.unlinked` package. The
refactoring reduces the parser file from 558 lines to 221 lines by promoting
inner visitor classes to package-private top-level files.

The change is a pure structural extraction. No methods are added, removed, or
renamed. No public API changes. All `core/tweedle` tests pass unchanged.

## Contents

- [Motivation](#motivation)
- [What changed](#what-changed)
- [Architecture](#architecture)
- [Package layout](#package-layout)
- [Public API](#public-api)
- [Utility method visibility](#utility-method-visibility)
- [Configuration](#configuration)
- [Validation](#validation)
- [Examples](#examples)
- [Claim boundaries](#claim-boundaries)

## Motivation

`TweedleUnlinkedParser.java` contained 558 lines spread across one outer class
and seven inner types (six classes and one functional interface). Three of
those — `ExpressionVisitor`, `StatementVisitor`, and `BinaryConstructor` — made
up 338 of those lines and had no logical dependency on being nested inside the
parser.

This created three problems:

1. **File size** — at 558 lines the file exceeded the project's 500-line
   modernization target and appeared as a hotspot in static analysis, making it
   harder to distinguish genuine complexity from layout bloat.
2. **Scroll fatigue** — developers editing the parser's core methods had to
   navigate past 253 lines of expression-visitor logic and 79 lines of
   statement-visitor logic to reach the utility methods at the bottom.
3. **Tight coupling signal** — nesting visitors as inner classes implied they
   required access to the parser's private state, when in practice they only
   called a small set of utility methods that could be package-private.

RabbitHole issue #714 extracts the three inner types into their own files and
promotes the utility methods they depend on from `private` to package-private.

## What changed

| Metric | Before | After |
| --- | --- | --- |
| `TweedleUnlinkedParser.java` lines | 558 | 221 |
| `ExpressionVisitor` location | Inner class | Top-level file (259 lines) |
| `StatementVisitor` location | Inner class | Top-level file (92 lines) |
| `BinaryConstructor` location | Inner class | Top-level file (9 lines) |
| Total lines across all files | 558 | 581 |
| Public API surface | Unchanged | Unchanged |
| Test changes | 0 | 0 |

The 23-line increase is from package declarations, imports, and constructor
boilerplate in the three new files. No logic was added or removed.

### Files created

| File | Purpose | Lines |
| --- | --- | --- |
| `ExpressionVisitor.java` | ANTLR visitor that converts parse-tree expression nodes to `TweedleExpression` AST nodes | 259 |
| `StatementVisitor.java` | ANTLR visitor that converts parse-tree statement nodes to `TweedleStatement` AST nodes | 92 |
| `BinaryConstructor.java` | Functional interface for binary-expression factory methods | 9 |

### Files modified

| File | Change |
| --- | --- |
| `TweedleUnlinkedParser.java` | Remove 3 inner types; promote 7 utility methods from `private` to package-private |

### Files NOT modified

- `TypeVisitor` — remains an inner class of `TweedleUnlinkedParser` (it
  accesses `ClassBody` and `ClassBodyDeclarationVisitor`, both parser-private)
- `ClassBody` — remains a private inner class
- `ClassBodyDeclarationVisitor` — remains a private inner class
- `MemberDeclarationVisitor` — remains a private inner class
- All test files — no tests reference the inner types by qualified name

## Architecture

After extraction, the `org.alice.tweedle.unlinked` package contains four
cooperating classes that together transform a Tweedle source string into an
unlinked AST:

```text
TweedleUnlinkedParser (221 lines, public)
├── parseType(String)       → TweedleType           [public]
├── parseStatement(String)  → TweedleStatement       [package]
├── parseExpression(String) → TweedleExpression      [package]
│
├── Inner: TypeVisitor      → visits type declarations
├── Inner: ClassBody        → accumulates fields/methods/constructors
├── Inner: ClassBodyDeclarationVisitor → visits class body members
├── Inner: MemberDeclarationVisitor    → visits individual members
│
└── Utility methods (package-private, used by extracted visitors):
    ├── collectBlockStatements(List<BlockStatementContext>)
    ├── visitLabeledArguments(LabeledExpressionListContext)
    ├── visitUnlabeledArguments(UnlabeledExpressionListContext, ExpressionVisitor)
    ├── getTypeOrVoid(TypeTypeOrVoidContext)
    ├── getType(TypeTypeContext)
    ├── getTypeReference(String)
    └── getPrimitiveType(String)

ExpressionVisitor (259 lines, package-private)
├── Constructor: ExpressionVisitor(TweedleUnlinkedParser parser)
├── Constructor: ExpressionVisitor(TweedleUnlinkedParser parser, TweedleType expectedType)
├── Constructor: ExpressionVisitor(TweedleUnlinkedParser parser, TweedleType expectedType, boolean allowPrimitiveNull)
└── 7 visitor overrides and 8 helper methods for expression parse-tree nodes
    (calls parser.getType(), parser.visitUnlabeledArguments(), etc.)

StatementVisitor (92 lines, package-private)
├── Constructor: StatementVisitor(TweedleUnlinkedParser parser)
└── 3 visitor overrides handling 8+ statement types
    (calls parser.collectBlockStatements(), parser.getType(), etc.)

BinaryConstructor (9 lines, package-private)
└── @FunctionalInterface: newBinExp(TweedleExpression, TweedleExpression) → BinaryExpression
```

### Data flow

```text
Source string
    │
    ▼
TweedleUnlinkedParser.tweedleParserForSource(source)
    │  creates ANTLR lexer → token stream → TweedleParser
    ▼
TweedleParser parse tree
    │
    ├─▶ TypeVisitor.visit()        → TweedleType (class/enum)
    │     └─▶ MemberDeclarationVisitor
    │           ├─▶ ExpressionVisitor  (field initializers, optional params)
    │           └─▶ StatementVisitor   (method/constructor bodies)
    │
    ├─▶ StatementVisitor.visit()   → TweedleStatement
    │     └─▶ ExpressionVisitor    (expressions within statements)
    │
    └─▶ ExpressionVisitor.visit()  → TweedleExpression
          └─▶ BinaryConstructor    (binary operator factory)
```

### Parser reference wiring

Each extracted visitor holds a `final` reference to its owning
`TweedleUnlinkedParser` instance, passed as the first constructor argument:

```java
// In TweedleUnlinkedParser:
TweedleStatement parseStatement(String sourceForExpression) {
    return new StatementVisitor(this).visit(
        tweedleParserForSource(sourceForExpression).blockStatement());
}

// In StatementVisitor:
class StatementVisitor extends TweedleParserBaseVisitor<TweedleStatement> {
    private final TweedleUnlinkedParser parser;

    StatementVisitor(TweedleUnlinkedParser parser) {
        this.parser = parser;
    }
    // ...
}
```

When visitor code needs a utility method that lives on the parser, it calls
through the reference: `parser.getType(ctx)`, `parser.collectBlockStatements(ctx)`.

## Package layout

```
core/tweedle/src/main/java/org/alice/tweedle/unlinked/
├── BinaryConstructor.java          ←  9 lines, @FunctionalInterface
├── ExpressionVisitor.java          ← 259 lines, package-private class
├── StatementVisitor.java           ←  92 lines, package-private class
└── TweedleUnlinkedParser.java      ← 221 lines, public class
```

All four files are in the same package (`org.alice.tweedle.unlinked`). The three
extracted files use default (package-private) access — they are not `public` and
cannot be referenced from outside the package.

## Public API

**No public API changes.** The only public entry point remains:

```java
public class TweedleUnlinkedParser {
    public TweedleType parseType(String sourceForType);
}
```

The `parseStatement` and `parseExpression` methods remain package-private, as
they were before (used only by tests within the same package).

### Visibility changes

| Member | Before | After | Reason |
| --- | --- | --- | --- |
| `ExpressionVisitor` | `private` inner class | package-private top-level class | Extracted to own file |
| `StatementVisitor` | `private` inner class | package-private top-level class | Extracted to own file |
| `BinaryConstructor` | `private` inner interface | package-private top-level interface | Extracted to own file |
| `collectBlockStatements` | `private` | package-private | Called by `StatementVisitor` and `ExpressionVisitor` |
| `visitLabeledArguments` | `private` | package-private | Called by `ExpressionVisitor` |
| `visitUnlabeledArguments` | `private` | package-private | Called by `ExpressionVisitor` |
| `getTypeOrVoid` | `private` | package-private | Called by `MemberDeclarationVisitor` (inner); promoted for consistency with other type-resolution methods |
| `getType` | `private` | package-private | Called by all visitors |
| `getTypeReference` | `private` | package-private | Called by `ExpressionVisitor` |
| `getPrimitiveType` | `private` | package-private | Called by `ExpressionVisitor` directly and by `getType` |

No method was made `public`. No method was made `protected`. The widening is
strictly from `private` to package-private (default access).

## Utility method visibility

Seven methods on `TweedleUnlinkedParser` were promoted from `private` to
package-private. This section documents each method's contract so that
extracted visitors can call them correctly.

### `collectBlockStatements`

```java
List<TweedleStatement> collectBlockStatements(
    List<TweedleParser.BlockStatementContext> contexts)
```

Converts a list of ANTLR block-statement parse nodes into a list of
`TweedleStatement` AST nodes. Creates a fresh `StatementVisitor` internally.

### `visitLabeledArguments`

```java
Map<String, TweedleExpression> visitLabeledArguments(
    TweedleParser.LabeledExpressionListContext context)
```

Converts labeled arguments (e.g., `name: value`) into a name→expression map.
Returns `Collections.emptyMap()` if the context is null.

### `visitUnlabeledArguments`

```java
List<TweedleExpression> visitUnlabeledArguments(
    TweedleParser.UnlabeledExpressionListContext listContext,
    ExpressionVisitor expressionVisitor)
```

Converts unlabeled positional arguments into a list of expressions. Accepts an
`ExpressionVisitor` parameter so the caller controls type context. Returns an
empty list if the context is null.

### `getTypeOrVoid`

```java
TweedleType getTypeOrVoid(TweedleParser.TypeTypeOrVoidContext context)
```

Returns `TweedleVoidType.VOID` if the context is a void token, otherwise
delegates to `getType`.

### `getType`

```java
TweedleType getType(TweedleParser.TypeTypeContext context)
```

Resolves a type context to a `TweedleType`. Handles class types (via
`getTypeReference`), primitive types (via `getPrimitiveType`), and array types
(wraps in `TweedleArrayType` when brackets are present).

### `getTypeReference`

```java
TweedleTypeReference getTypeReference(String typeName)
```

Creates an unlinked type reference from a class name. Linking happens later
during type resolution.

### `getPrimitiveType`

```java
TweedlePrimitiveType getPrimitiveType(String typeName)
```

Looks up a primitive type by name from `TweedleTypes.PRIMITIVE_TYPES`. Returns
`null` if no match (defensive — the grammar should prevent this).

## Configuration

No configuration is required. The extraction is a compile-time structural change
with no runtime knobs, feature flags, or property files.

## Validation

### Automated

```bash
# Run all core/tweedle tests (includes parser round-trip tests)
mvn -pl core/tweedle test -q

# Verify line count target met
wc -l core/tweedle/src/main/java/org/alice/tweedle/unlinked/TweedleUnlinkedParser.java
# Expected: 221 (under 500-line target)
```

### Manual checks

1. **No public API leakage** — confirm that `ExpressionVisitor`,
   `StatementVisitor`, and `BinaryConstructor` have no `public` modifier on
   their class/interface declarations.
2. **Parser field is final** — confirm that both `ExpressionVisitor.parser` and
   `StatementVisitor.parser` are declared `final`.
3. **No import changes in other modules** — a `git diff` of the change should
   show modifications only within `core/tweedle/src/main/java/org/alice/tweedle/unlinked/`.

## Examples

### Parsing an expression (unchanged usage)

```java
TweedleUnlinkedParser parser = new TweedleUnlinkedParser();
TweedleType type = parser.parseType("class Foo extends Bar { }");
// Returns TweedleClass with name "Foo", superclass "Bar"
```

Callers see no difference. The internal creation of `ExpressionVisitor` and
`StatementVisitor` now passes `this` as the first constructor argument, but this
is invisible to external code.

### Internal wiring (for maintainers)

Before extraction, inner classes accessed parser methods directly:

```java
// OLD — inner class had implicit TweedleUnlinkedParser.this reference
private class ExpressionVisitor extends TweedleParserBaseVisitor<TweedleExpression> {
    @Override
    public TweedleExpression visitMethodCall(...) {
        List<TweedleExpression> args = visitUnlabeledArguments(ctx, this);
        //                             ^^^^^^^^^^^^^^^^^^^^^^^^
        //                             Implicit outer-class method call
    }
}
```

After extraction, the parser reference is explicit:

```java
// NEW — top-level class holds explicit parser reference
class ExpressionVisitor extends TweedleParserBaseVisitor<TweedleExpression> {
    private final TweedleUnlinkedParser parser;

    ExpressionVisitor(TweedleUnlinkedParser parser) {
        this.parser = parser;
    }

    @Override
    public TweedleExpression visitMethodCall(...) {
        List<TweedleExpression> args = parser.visitUnlabeledArguments(ctx, this);
        //                             ^^^^^^^
        //                             Explicit parser reference
    }
}
```

## Claim boundaries

This refactoring makes the following claims:

| Claim | Evidence |
| --- | --- |
| Zero public API changes | Only `parseType(String)` is public; signature unchanged |
| Zero test changes | All tests pass without modification |
| Under 500-line target | `TweedleUnlinkedParser.java` is 221 lines |
| No behavioral change | Extracted code is identical; only moved and re-wired |
| Package-private only | No `public` modifier on any extracted type |
| Final parser reference | Both visitor constructors assign to `final` field |

This refactoring does **not** claim:

- That the visitor implementations themselves are well-factored (they could
  benefit from further decomposition in the future)
- That the remaining inner classes (`TypeVisitor`, `ClassBody`,
  `ClassBodyDeclarationVisitor`, `MemberDeclarationVisitor`) should stay nested
  forever — they are candidates for future extraction if the parser grows
- That the 7 promoted utility methods have optimal signatures — they preserve
  the existing signatures exactly
