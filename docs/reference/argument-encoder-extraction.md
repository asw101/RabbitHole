# ArgumentEncoder Extraction

This reference describes the extraction of argument-encoding methods from
`TweedleEncoder` into a new package-private `ArgumentEncoder` class. This is
part of RabbitHole issue #730, isolating argument labeling, keyed argument
dispatch, parameter label resolution, and argument wrapping into a focused
companion class.

The extraction is a pure internal refactor. The public API surface —
`TweedleEncoderDecoder` — is unchanged. All existing encode behavior, error
messages, and Tweedle output are preserved identically.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Class responsibilities](#class-responsibilities)
  - [ArgumentEncoder](#argumentencoder)
  - [TweedleEncoder changes](#tweedleencoder-changes)
- [Public API](#public-api)
- [Package-private collaboration](#package-private-collaboration)
- [Bridge methods on TweedleEncoder](#bridge-methods-on-tweedleencoder)
- [Visibility changes](#visibility-changes)
- [Security boundary](#security-boundary)
- [Error handling contract](#error-handling-contract)
- [Configuration](#configuration)
- [Validation](#validation)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

After the [StatementEncoder extraction](./statement-encoder-extraction.md)
(step 1), [ExpressionEncoder extraction](./expression-encoder-extraction.md)
(step 2), and [FormattingEncoder extraction](./formatting-encoder-extraction.md)
(step 3), `TweedleEncoder` still contains argument-encoding logic: keyed
argument dispatch, labeled argument formatting, parameter label resolution with
constructor relabeling and missing-name fallback, and argument wrapping. These
methods form a cohesive group — they all work together to emit labeled
`name: value` Tweedle argument syntax.

Extracting argument methods into `ArgumentEncoder` brings `TweedleEncoder`
from 499 lines to ~370 lines (under the 400-line target). This extraction is
paired with the [ExpressionEncoder expansion](./expression-encoder-extraction.md)
(adding `processInstantiation`) as issue #730.

## Architecture

```text
TweedleEncoderDecoder (public facade — unchanged)
└── TweedleEncoder (coordinator, ~370 lines after extraction)
    ├── StatementEncoder (package-private, ~46 lines — step 1)
    ├── ExpressionEncoder (package-private, ~97 lines — step 2 + #730)
    ├── FormattingEncoder (package-private, ~122 lines — step 3)
    ├── ResourceEncoder (package-private, ~240 lines)
    └── ArgumentEncoder (package-private, ~119 lines — #730)
        └── Keyed arguments, labeled arguments, parameter labels, wrapping
```

All delegate classes live in `org.alice.serialization.tweedle`.
`ArgumentEncoder` is package-private with no public constructor. It is
instantiated only by `TweedleEncoder` and receives a back-reference to it for
shared services.

## Class responsibilities

### ArgumentEncoder

| Responsibility | Method |
| --- | --- |
| Keyed argument override | `appendArgument(JavaKeyedArgument)` — entry point for `@Override appendArgument` |
| Keyed argument dispatch | `processKeyedArgument(JavaKeyedArgument)` — detects keyword factory type, extracts label from method name, dispatches to `appendOneArgument` or falls back to `processExpression` |
| Single argument extraction | `appendOneArgument(MethodInvocation)` — validates single required argument, delegates with optional wrapping |
| Labeled argument encoding | `processArgument(AbstractParameter, AbstractArgument)` — emits `label: value` syntax with constructor relabeling and method wrapping |
| Argument wrapping | `appendWrappedArg(ProcessableNode, String, Map)` — wraps argument value in optional prefix/suffix (e.g., `new Duration(seconds: ...)`) |
| Parameter label resolution | `getParameterLabel(AbstractParameter)` — resolves label through constructor relabeling, `identifierName`, relabel map, missing-name table, and type-fallback chain |
| Parameter index lookup | `parameterIndex(JavaMethodParameter)` — ordinal position of parameter in its method's parameter list |

`ArgumentEncoder` stores a `TweedleEncoder` reference passed at construction
and uses it for all bridge and forwarding calls. Methods do not take the
encoder as an additional parameter.

The constructor signature:

```java
ArgumentEncoder(TweedleEncoder encoder) {
  this.encoder = encoder;
}
```

### TweedleEncoder changes

| Change | Detail |
| --- | --- |
| New field | `private final ArgumentEncoder argumentEncoder` |
| Constructor wiring | `this.argumentEncoder = new ArgumentEncoder(this)` alongside existing delegates |
| `appendArgument(JavaKeyedArgument)` | Body delegates to `argumentEncoder.appendArgument(arg)` |
| `processKeyedArgument(JavaKeyedArgument)` | Body delegates to `argumentEncoder.processKeyedArgument(arg)` |
| `processArgument(AbstractParameter, AbstractArgument)` | Body delegates to `argumentEncoder.processArgument(parameter, argument)` |
| Private methods removed | `appendOneArgument`, `appendWrappedArg`, `getParameterLabel`, `parameterIndex` — moved entirely to `ArgumentEncoder` |
| Bridge method added | `forwardIdentifierName(AbstractDeclaration)` — package-private method that calls the `protected` `identifierName(variable)` on behalf of the delegate |

The `@Override` annotations remain on `TweedleEncoder` because the visitor
pattern in `SourceCodeGenerator` requires the override stubs on the subclass.
Each stub delegates to `ArgumentEncoder` for the extracted logic.

## Public API

The public API is exclusively `TweedleEncoderDecoder`. No API changes are made
by this extraction.

```java
public class TweedleEncoderDecoder implements EncoderDecoder<String> {
  public <N extends AbstractNode & ProcessableNode> String encode(N node);
  public <N extends AbstractNode & ProcessableNode> String encode(N node,
      Set<AbstractDeclaration> terminals);
  public <N extends ProcessableNode> String encodeProcessable(N node);
}
```

All encode entry points instantiate `TweedleEncoder`, which internally creates
all delegates including `ArgumentEncoder`. Callers never see the delegate
classes.

## Package-private collaboration

`ArgumentEncoder` accesses `TweedleEncoder` methods via package-private
forwarding methods. The following methods on `TweedleEncoder` are used by
`ArgumentEncoder`:

| Method | Purpose |
| --- | --- |
| `forwardAppendString(String)` | Append raw string to output buffer (already exists from step 1) |
| `forwardIdentifierName(AbstractDeclaration)` | Call `identifierName(variable)` to resolve identifier with user-prefix rules (new bridge) |
| `forwardAppendEscapedString(String)` | Call `appendEscapedString(value)` to write a quoted, escaped string (already exists) |
| `forwardProcessExpression(Expression)` | Call `processExpression(target)` to encode an expression when keyed argument dispatch falls through (already exists from step 2) |

`ArgumentEncoder` also accesses static data from `TweedleEncoderData`:

| Field | Purpose |
| --- | --- |
| `TweedleEncoderData.methodParamsToRelabel` | Map for parameter label translation (e.g., `multipleEventPolicy` → `overlappingEventPolicy`) |
| `TweedleEncoderData.constructorsWithRelabeledParams` | Map of constructor class → parameter rename map (e.g., `Size(width, height, depth)`) |
| `TweedleEncoderData.methodsMissingParameterNames` | Map of method name → parameter name array for methods with unnamed parameters |
| `TweedleEncoderData.methodsWithWrappedArgs` | Map of method name → parameter → wrapper prefix (e.g., duration wrapping) |
| `TweedleEncoderData.optionalParamsToWrap` | Map of optional parameter name → wrapper prefix |

`ArgumentEncoder` calls `argument.process(encoder)` for AST visitor dispatch,
passing the `TweedleEncoder` reference (not itself) so the visitor pattern
dispatches correctly through `SourceCodeGenerator`.

No interfaces or inheritance are introduced. All collaboration uses direct
method calls within the same package.

## Bridge methods on TweedleEncoder

Because `TweedleEncoder` extends `SourceCodeGenerator` (in a different package),
`ArgumentEncoder` cannot call inherited `protected` methods directly. The new
bridge method provides access to `identifierName`:

```java
// TweedleEncoder.java — @Override stays here for polymorphic dispatch
@Override
protected void appendArgument(JavaKeyedArgument arg) {
  argumentEncoder.appendArgument(arg);
}

@Override
public void processKeyedArgument(JavaKeyedArgument arg) {
  argumentEncoder.processKeyedArgument(arg);
}

@Override
public void processArgument(AbstractParameter parameter, AbstractArgument argument) {
  argumentEncoder.processArgument(parameter, argument);
}

// Package-private bridge: delegate calls this to reach inherited method
String forwardIdentifierName(AbstractDeclaration variable) {
  return identifierName(variable);
}
```

Unlike `StatementEncoder` which needs `super` call bridges, `ArgumentEncoder`
does not need `super` bridges. The extracted methods fully implement the
behavior — they do not wrap a parent implementation. The `forwardIdentifierName`
bridge wraps the overridden `identifierName` method (which applies user-prefix
logic) rather than `super.identifierName`.

**Key pattern:** `argument.process(encoder)` calls are made with the
`TweedleEncoder` reference so that the `SourceCodeGenerator` visitor pattern
dispatches to the correct `@Override` methods. If `argument.process(this)` were
used, the argument would try to invoke visitor methods on `ArgumentEncoder`
which does not implement the visitor interface.

## Visibility changes

No visibility changes are required for `ArgumentEncoder`. All static data
fields (`methodParamsToRelabel`, `constructorsWithRelabeledParams`, etc.) live
on `TweedleEncoderData`, which is already package-private. The
`forwardAppendString` and `forwardAppendEscapedString` bridge methods already
exist from prior extractions.

The only new bridge method is `forwardIdentifierName(AbstractDeclaration)`,
which is package-private on `TweedleEncoder`.

## Security boundary

No new I/O, network, reflection, or thread operations are introduced.
`ArgumentEncoder` only formats string output via the existing
`SourceCodeGenerator` buffer.

The `ReleaseVirtualMachine` used in `processInstantiation` for PersonResource
evaluation remains in `ExpressionEncoder`, not in `ArgumentEncoder`.

`ArgumentEncoder` reads configuration data from `TweedleEncoderData` static
maps, which are constant after class initialization and not user-controlled.

The `Dialogs.showError` call in `getParameterLabel` (for unlabeled parameters)
is preserved identically — it remains the existing error reporting path for
parameters without resolvable labels.

## Error handling contract

The extracted methods preserve all error handling:

| Method | Error handling |
| --- | --- |
| `appendOneArgument` | `Logger.errln` if argument count is not exactly 1 |
| `getParameterLabel` | `Dialogs.showError` + `Logger.errln` for unlabeled parameters; falls back to type name |
| `parameterIndex` | Returns `-1` if parameter not found (defensive, should not occur) |
| `processKeyedArgument` | Falls through to `processExpression` if no factory type detected |

No new exceptions are thrown. No existing error paths are removed.

## Configuration

No runtime configuration changes. The extraction uses the existing Maven
reactor, Tweedle grammar submodule, and JUnit configuration.

From a fresh checkout or worktree, initialize the Tweedle grammar submodule
before Maven validation:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
```

Set the Node memory preference when running Maven:

```bash
export NODE_OPTIONS=--max-old-space-size=32768
```

## Validation

Run the focused core AST encoder tests from the repository root:

```bash
NODE_OPTIONS=--max-old-space-size=32768 git submodule update --init tweedle-lang
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/ast -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=TweedleEncoderTest,TweedleEncoderRenameContractTest,TweedleEncoderDecoderTest,SourceCodeGeneratorTest,ExpressionEncoderExtractionTest,ArgumentEncoderExtractionTest,StatementEncoderExtractionTest \
  test
```

Run the story-api-migration round-trip tests:

```bash
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/story-api-migration -am \
  -DfailIfNoTests=false \
  test
```

All suites must pass with identical results before and after the extraction.
The combined `ExpressionEncoderExtractionTest` and `ArgumentEncoderExtractionTest`
contain characterization tests covering both `ExpressionEncoder` and
`ArgumentEncoder` delegation.

## Acceptance criteria

| Criterion | Verification |
| --- | --- |
| `ArgumentEncoder.java` exists | File present in `core/ast/src/main/java/org/alice/serialization/tweedle/` |
| `ArgumentEncoder` is package-private | No `public` keyword on class declaration |
| Constructor takes `TweedleEncoder` | `ArgumentEncoder(TweedleEncoder encoder)` |
| `appendArgument` extracted | Method present on `ArgumentEncoder` with `(JavaKeyedArgument)` signature |
| `processKeyedArgument` extracted | Method present on `ArgumentEncoder` |
| `processArgument` extracted | Method present on `ArgumentEncoder` with `(AbstractParameter, AbstractArgument)` signature |
| `appendOneArgument` extracted | Private method on `ArgumentEncoder`, not on `TweedleEncoder` |
| `appendWrappedArg` extracted | Private method on `ArgumentEncoder`, not on `TweedleEncoder` |
| `getParameterLabel` extracted | Private method on `ArgumentEncoder`, not on `TweedleEncoder` |
| `parameterIndex` extracted | Private method on `ArgumentEncoder`, not on `TweedleEncoder` |
| `forwardIdentifierName` bridge added | Package-private method on `TweedleEncoder` |
| `TweedleEncoder` delegates `@Override` bodies | `appendArgument`, `processKeyedArgument`, `processArgument` delegate to `argumentEncoder` |
| `TweedleEncoder` under 400 lines | `wc -l` confirms reduction from 499 |
| `TweedleEncoderDecoder.java` unchanged | `git diff` shows no changes |
| `ExpressionEncoderExtractionTest` passes | All ExpressionEncoder characterization tests — zero failures |
| `ArgumentEncoderExtractionTest` passes | All ArgumentEncoder characterization tests — zero failures |
| `StatementEncoderExtractionTest` passes | Prior extraction contract unbroken — zero failures |
| `TweedleEncoderTest` passes | Zero failures |
| `TweedleEncoderRenameContractTest` passes | Zero failures |
| `TweedleEncoderDecoderTest` passes | Zero failures |
| `SourceCodeGeneratorTest` passes | Zero failures |
| `core/story-api-migration` tests pass | Zero failures |
| Tweedle output is byte-identical | Encoder tests assert exact string output |

## Claim boundaries

This extraction proves:

- The `appendArgument`, `processKeyedArgument`, `processArgument`,
  `appendOneArgument`, `appendWrappedArg`, `getParameterLabel`, and
  `parameterIndex` methods can be extracted to a delegate without changing
  observable behavior.
- The keyed argument factory-type dispatch path produces identical output when
  running through `ArgumentEncoder` indirection.
- The parameter label resolution chain (constructor relabeling →
  `identifierName` → relabel map → missing-name table → type fallback) works
  correctly through the `forwardIdentifierName` bridge.
- The `argument.process(encoder)` pattern correctly routes AST visitor dispatch
  through `TweedleEncoder` rather than `ArgumentEncoder`.
- The `Dialogs.showError` error path for unlabeled parameters is preserved.
- All existing encoder test assertions pass identically.
- The combined `ExpressionEncoder` + `ArgumentEncoder` extraction reduces
  `TweedleEncoder` from 499 to under 400 lines.

This extraction does **not** prove:

| Non-claim | Reason |
| --- | --- |
| Full encoder decomposition complete | `EncoderMappings` and `ResourceStructureEncoder` remain as future steps per the [Encoder Delegate Decomposition](./encoder-delegate-decomposition.md). |
| New encode capabilities | No new Tweedle constructs are supported. |
| Performance improvement | Extraction is structural, not algorithmic. |
| Thread safety | `TweedleEncoder` was not thread-safe before; extraction does not change this. |
| Public API expansion | No new public methods or classes are introduced. |
| TweedleEncoderData extraction | Static data maps remain on `TweedleEncoderData`; they are not moved to `ArgumentEncoder`. |

Adjacent claims are owned by their own documents:

| Claim | Document |
| --- | --- |
| Full encoder delegate decomposition | [Encoder Delegate Decomposition](./encoder-delegate-decomposition.md) |
| Statement encoder extraction (step 1) | [StatementEncoder Extraction](./statement-encoder-extraction.md) |
| Expression encoder extraction (step 2 + #730) | [ExpressionEncoder Extraction](./expression-encoder-extraction.md) |
| Formatting encoder extraction (step 3) | [FormattingEncoder Extraction](./formatting-encoder-extraction.md) |
| Decoder delegate decomposition | [Decoder Delegate Decomposition](./decoder-delegate-decomposition.md) |
| TweedleEncoder rename | [TweedleEncoder Rename](./tweedle-encoder-rename.md) |
