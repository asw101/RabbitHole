# ExpressionEncoder Extraction

This reference describes the extraction of expression-encoding methods from the
`TweedleEncoder` into a new package-private `ExpressionEncoder` class. Initially
created as step 2 of RabbitHole issue #506 (target-and-member resolution, Math
module routing, resource expression encoding), `ExpressionEncoder` was expanded
in issue #730 to include instantiation dispatch (`processInstantiation` and
`getDeclaringJavaClassName`).

The extraction is a pure internal refactor. The public API surface —
`TweedleEncoderDecoder` — is unchanged. All existing encode behavior, error
messages, and Tweedle output are preserved identically.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Class responsibilities](#class-responsibilities)
  - [ExpressionEncoder](#expressionencoder)
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
(step 1), `TweedleEncoder` still mixes expression-encoding logic
(target-and-member resolution, Math module routing, resource expression
encoding) with visitor coordination, formatting, and resource structure
generation. Extracting expression methods into `ExpressionEncoder` continues the
incremental decomposition toward the full
[Encoder delegate decomposition](./encoder-delegate-decomposition.md).

This extraction follows the same delegate pattern established by
`StatementEncoder` on the encode side and
[`ExpressionDecoder`](./decoder-delegate-decomposition.md) on the decode side.

## Architecture

```text
TweedleEncoderDecoder (public facade — unchanged)
└── TweedleEncoder (coordinator, extends SourceCodeGenerator)
    ├── StatementEncoder (package-private, ~46 lines — step 1)
    │   └── Statement completion, disabled markers, statement-end formatting
    └── ExpressionEncoder (package-private, ~97 lines — step 2 + #730)
        └── Instantiation dispatch, target+member resolution, Math routing, resource expressions
```

Both delegate classes live in `org.alice.serialization.tweedle`.
`ExpressionEncoder` is package-private with no public constructor. It is
instantiated only by `TweedleEncoder` and receives a back-reference to it for
shared services.

## Class responsibilities

### ExpressionEncoder

| Responsibility | Method |
| --- | --- |
| Instantiation dispatch | `processInstantiation(InstanceCreation)` — dispatches PersonResource, Double, DynamicResource, or falls back to `super` via bridge (added in #730) |
| Class name extraction | `getDeclaringJavaClassName(InstanceCreation)` — private helper; resolves Java constructor class name from `JavaConstructor` reflection proxy (added in #730) |
| Target and member resolution | `appendTargetAndMember(Expression, String, AbstractType)` — resolves Math targets, renames members, appends access separator |
| Math target detection | `targetIsMath(Expression)` — returns `true` for `TypeExpression` wrapping `java.lang.Math` |
| Math module routing | `tweedleModuleForMath(String, AbstractType)` — maps to `$WholeNumber`, `$Angle`, or `$DecimalNumber` |
| Resource expression encoding | `processResourceExpression(ResourceExpression)` — encodes resource name as escaped string |

`ExpressionEncoder` stores a `TweedleEncoder` reference passed at construction
and uses it for all bridge and forwarding calls. This matches the
`StatementEncoder(TweedleEncoder)` field-storage pattern from step 1. Methods
do not take the encoder as an additional parameter.

The constructor signature:

```java
ExpressionEncoder(TweedleEncoder encoder) {
  this.encoder = encoder;
}
```

### TweedleEncoder changes

| Change | Detail |
| --- | --- |
| New field | `private final ExpressionEncoder expressionEncoder` |
| Constructor wiring | `this.expressionEncoder = new ExpressionEncoder(this)` alongside existing `statementEncoder` |
| `angleMembers` visibility | Changed from `private static final` to `static final` (package-private) so `ExpressionEncoder.tweedleModuleForMath` can read it |
| `membersToRename` visibility | Changed from `private static final` to `static final` (package-private) so `ExpressionEncoder.appendTargetAndMember` can read it |
| `processInstantiation(InstanceCreation)` | Body delegates to `expressionEncoder.processInstantiation(creation)` (added in #730) |
| `appendTargetAndMember(Expression, String, AbstractType)` | Body delegates to `expressionEncoder.appendTargetAndMember(target, member, returnType)` |
| `processResourceExpression(ResourceExpression)` | Body delegates to `expressionEncoder.processResourceExpression(resourceExpression)` |
| `getDeclaringJavaClassName` removed | Private method moved entirely to `ExpressionEncoder` (added in #730) |
| Bridge methods added | `superProcessInstantiation(InstanceCreation)` (new in #730 — calls `super.processInstantiation`), `forwardProcessExpression(Expression)`, `forwardAppendAccessSeparator()`, `forwardAppendEscapedString(String)` — package-private forwarding methods |

The `@Override` annotations remain on `TweedleEncoder` because the visitor
pattern in `SourceCodeGenerator` requires the override stubs on the subclass.
Each stub delegates to `ExpressionEncoder` for the extracted logic.

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
both `StatementEncoder` and `ExpressionEncoder`. Callers never see the delegate
classes.

## Package-private collaboration

`ExpressionEncoder` accesses `TweedleEncoder` methods via package-private
forwarding methods. The following methods on `TweedleEncoder` are used by
`ExpressionEncoder`:

| Method | Purpose |
| --- | --- |
| `forwardAppendString(String)` | Append raw string to output buffer (already exists from step 1) |
| `superProcessInstantiation(InstanceCreation)` | Call `super.processInstantiation(creation)` — the `super` bridge for instantiation fallback (new in #730) |
| `forwardProcessExpression(Expression)` | Call `processExpression(target)` to encode the target expression (consistency bridge; `processExpression` is `public` but wrapped for uniformity) |
| `forwardAppendAccessSeparator()` | Call `appendAccessSeparator()` to write the `.` separator |
| `forwardAppendEscapedString(String)` | Call `appendEscapedString(value)` to write a quoted, escaped string |
| `membersToRename` | Package-private static map for member name translation (on `TweedleEncoderData`) |
| `angleMembers` | Package-private static set for Math angle function detection (on `TweedleEncoderData`) |

No interfaces or inheritance are introduced. All collaboration uses direct
method calls within the same package, matching the
[StatementEncoder pattern](./statement-encoder-extraction.md) and the
[Decoder delegate pattern](./decoder-delegate-decomposition.md).

## Bridge methods on TweedleEncoder

Because `TweedleEncoder` extends `SourceCodeGenerator` (in a different package),
`ExpressionEncoder` cannot call inherited `protected` methods directly — Java
accessibility rules prevent a same-package class from calling `protected`
methods inherited from a class in a different package. The forwarding methods
(`forwardAppendAccessSeparator`, `forwardAppendEscapedString`) are required for
this reason. The `forwardProcessExpression` method wraps `processExpression`,
which is actually `public` on `SourceCodeGenerator`, so it is technically
callable without a bridge — it is included for uniformity.

Issue #730 added `superProcessInstantiation(InstanceCreation)` as a `super`
bridge, following the same pattern as `StatementEncoder`'s
`superAppendStatementCompletion`. This is needed because `processInstantiation`
has a fallback path that calls `super.processInstantiation(creation)` for
unrecognized constructor types. The bridge pattern used:

```java
// TweedleEncoder.java — @Override stays here for polymorphic dispatch
@Override
public void processInstantiation(InstanceCreation creation) {
  expressionEncoder.processInstantiation(creation);
}

// Package-private super bridge: delegate calls this for fallback
void superProcessInstantiation(InstanceCreation creation) {
  super.processInstantiation(creation);
}

@Override
protected void appendTargetAndMember(Expression target, String member,
    AbstractType<?, ?, ?> returnType) {
  expressionEncoder.appendTargetAndMember(target, member, returnType);
}

// Package-private bridges: delegate calls these to reach inherited methods
void forwardProcessExpression(Expression expression) {
  processExpression(expression);
}

void forwardAppendAccessSeparator() {
  appendAccessSeparator();
}

void forwardAppendEscapedString(String value) {
  appendEscapedString(value);
}
```

```java
// TweedleEncoder.java — @Override stays here
@Override
public void processResourceExpression(ResourceExpression resourceExpression) {
  expressionEncoder.processResourceExpression(resourceExpression);
}
```

## Visibility changes

No additional visibility changes are required for this extraction. The
`angleMembers` and `membersToRename` collections already live on
`TweedleEncoderData`, which is package-private. `ExpressionEncoder` accesses
them directly as `TweedleEncoderData.angleMembers` and
`TweedleEncoderData.membersToRename`.

This follows the same pattern as `NODE_ENABLE` and `NODE_DISABLE` which were
widened to package-private in step 1 for `StatementEncoder`.

## Security boundary

No new I/O, network, reflection, or thread operations are introduced.
`ExpressionEncoder` only formats string output via the existing
`SourceCodeGenerator` buffer. The `membersToRename` and `angleMembers`
collections are constant data, not user-controlled input.

The `ExpressionEncoder` class is package-private and `final`-by-reference
(stored in a `private final` field on `TweedleEncoder`). All method calls are
compile-time verified — no reflection is used for delegation.

## Error handling contract

No error handling changes. The extracted methods do not throw checked exceptions
and contain no try/catch blocks. The `targetIsMath` method performs a safe
`instanceof` check and null-safe `getName()` comparison. The
`processResourceExpression` method reads the resource name from a non-null
`ResourceExpression.resource` property.

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

## Acceptance criteria

| Criterion | Verification |
| --- | --- |
| `ExpressionEncoder.java` exists | File present in `core/ast/src/main/java/org/alice/serialization/tweedle/` |
| `ExpressionEncoder` is package-private | No `public` keyword on class declaration |
| Constructor takes `TweedleEncoder` | `ExpressionEncoder(TweedleEncoder encoder)` |
| `processInstantiation` extracted | Method present on `ExpressionEncoder` with `(InstanceCreation)` signature (#730) |
| `getDeclaringJavaClassName` extracted | Private method on `ExpressionEncoder`, not on `TweedleEncoder` (#730) |
| `appendTargetAndMember` extracted | Method present on `ExpressionEncoder` with `(Expression, String, AbstractType)` signature |
| `targetIsMath` extracted | Private method on `ExpressionEncoder`, not on `TweedleEncoder` |
| `tweedleModuleForMath` extracted | Private method on `ExpressionEncoder`, not on `TweedleEncoder` |
| `processResourceExpression` extracted | Method present on `ExpressionEncoder` |
| `angleMembers` and `membersToRename` on `TweedleEncoderData` | Accessed as `TweedleEncoderData.angleMembers` and `TweedleEncoderData.membersToRename` |
| `superProcessInstantiation` bridge on TweedleEncoder | Package-private bridge calling `super.processInstantiation` (#730) |
| 3 forwarding methods on TweedleEncoder | `forwardProcessExpression`, `forwardAppendAccessSeparator`, `forwardAppendEscapedString` |
| `TweedleEncoder` delegates `@Override` bodies | `processInstantiation`, `appendTargetAndMember`, `processResourceExpression` delegate to `expressionEncoder` |
| `TweedleEncoderDecoder.java` unchanged | `git diff` shows no changes |
| `ExpressionEncoderExtractionTest` passes | All ExpressionEncoder characterization tests — zero failures |
| `ArgumentEncoderExtractionTest` passes | All ArgumentEncoder characterization tests — zero failures |
| `StatementEncoderExtractionTest` passes | Step 1 contract unbroken — zero failures |
| `TweedleEncoderTest` passes | Zero failures |
| `TweedleEncoderRenameContractTest` passes | Zero failures |
| `TweedleEncoderDecoderTest` passes | Zero failures |
| `SourceCodeGeneratorTest` passes | Zero failures |
| `core/story-api-migration` tests pass | Zero failures |
| Tweedle output is byte-identical | Encoder tests assert exact string output |

## Claim boundaries

This extraction proves:

- The `processInstantiation`, `getDeclaringJavaClassName`,
  `appendTargetAndMember`, `targetIsMath`, `tweedleModuleForMath`, and
  `processResourceExpression` methods can be extracted to a delegate without
  changing observable behavior.
- The `processInstantiation` fallback path correctly calls
  `super.processInstantiation(creation)` through the `superProcessInstantiation`
  bridge when no special-case dispatch applies.
- The PersonResource evaluation via `ReleaseVirtualMachine` produces identical
  output through the delegate indirection.
- The Math module routing logic (`$WholeNumber`, `$Angle`, `$DecimalNumber`)
  works correctly through the delegate indirection.
- The member rename lookup via `membersToRename` produces identical output
  when accessed from the delegate.
- All existing encoder test assertions pass identically.
- The step 1 `StatementEncoder` extraction remains unaffected.

This extraction does **not** prove:

| Non-claim | Reason |
| --- | --- |
| Full encoder decomposition complete | `EncoderMappings` and `ResourceStructureEncoder` are future steps per the [Encoder Delegate Decomposition](./encoder-delegate-decomposition.md). |
| New encode capabilities | No new Tweedle constructs are supported. |
| Performance improvement | Extraction is structural, not algorithmic. |
| Thread safety | `TweedleEncoder` was not thread-safe before; extraction does not change this. |
| Public API expansion | No new public methods or classes are introduced. |
| Argument encoding | Argument methods are extracted into [ArgumentEncoder](./argument-encoder-extraction.md) as a separate companion delegate in the same issue (#730). |

Adjacent claims are owned by their own documents:

| Claim | Document |
| --- | --- |
| Full encoder delegate decomposition | [Encoder Delegate Decomposition](./encoder-delegate-decomposition.md) |
| Statement encoder extraction (step 1) | [StatementEncoder Extraction](./statement-encoder-extraction.md) |
| Argument encoder extraction (#730) | [ArgumentEncoder Extraction](./argument-encoder-extraction.md) |
| Formatting encoder extraction (step 3) | [FormattingEncoder Extraction](./formatting-encoder-extraction.md) |
| Decoder delegate decomposition | [Decoder Delegate Decomposition](./decoder-delegate-decomposition.md) |
| TweedleEncoder rename | [TweedleEncoder Rename](./tweedle-encoder-rename.md) |
