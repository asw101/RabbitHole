# ResourceEncoder Extraction

This reference describes the extraction of resource-encoding methods from the
988-line `TweedleEncoder` into a new package-private `ResourceEncoder` class.
This is the third step of RabbitHole issue #506, isolating resource type class
generation, resource field reflection, resource constructors and instances, joint
IDs, poses, and transformations into a focused companion class.

The extraction is a pure internal refactor. The public API surface —
`TweedleEncoderDecoder` — is unchanged. All existing encode behavior, error
messages, and Tweedle output are preserved identically.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Class responsibilities](#class-responsibilities)
  - [ResourceEncoder](#resourceencoder)
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
(step 1) and [ExpressionEncoder extraction](./expression-encoder-extraction.md)
(step 2), `TweedleEncoder` still contains ~210 lines of resource-encoding logic:
resource type class generation via `Class.forName` reflection, resource
constructors with superclass-specific parameters, resource field enumeration,
resource instance generation, joint ID/array construction, pose and
transformation encoding. Extracting these methods into `ResourceEncoder`
continues the incremental decomposition toward the full
[Encoder delegate decomposition](./encoder-delegate-decomposition.md).

This extraction follows the same delegate pattern established by
`StatementEncoder` and `ExpressionEncoder` on the encode side, and mirrors the
`ResourceStructureEncoder` target described in the decomposition plan.

## Architecture

```text
TweedleEncoderDecoder (public facade — unchanged)
└── TweedleEncoder (coordinator, extends SourceCodeGenerator, ~820 lines → ~820 after)
    ├── StatementEncoder (package-private, ~46 lines — step 1)
    │   └── Statement completion, disabled markers, statement-end formatting
    ├── ExpressionEncoder (package-private, ~60 lines — step 2)
    │   └── Target+member resolution, Math routing, resource expressions
    └── ResourceEncoder (package-private, ~240 lines — step 3)
        └── Resource type/dynamic resource classes, fields via reflection,
            constructors, instances, joint IDs, poses, transformations
```

All delegate classes live in `org.alice.serialization.tweedle`.
`ResourceEncoder` is package-private with no public constructor. It is
instantiated only by `TweedleEncoder` and receives a back-reference to it for
shared services.

## Class responsibilities

### ResourceEncoder

| Responsibility | Method |
| --- | --- |
| Resource type class generation | `processResourceType(String)` — reflects on the resource class, generates class header, constructor, fields, instances, and footer |
| Dynamic resource class generation | `processDynamicResource(String, String, InstantiableTweedleNode[])` — generates a named variant resource class with added joints |
| User joint identifier | `getUserJointIdentifier(String)` — prefixes non-root joint names with `u_` |
| Resource constructor | `appendResourceConstructor(String, String)` — generates superclass-specific constructor with named parameters |
| Resource instances | `appendResourceInstances(Class)` — enumerates enum constants as static instances |
| Resource instance | `appendResourceInstance(String, String)` — generates a single `static ClassName NAME = new ClassName(name: "Type/NAME")` |
| Resource fields | `appendResourceFields(String, Class)` — reflects over declared fields, dispatches `JointId` and array fields |
| Added joints | `appendAddedJoints(String, Collection)` — generates `ADDED_JOINTS`, `ALL_JOINTS`, and `getJointIds()` |
| Static field (Field) | `appendStaticField(Field, Runnable)` — field-reflection overload extracting annotation, type, and name |
| Static field (params) | `appendStaticField(FieldTemplate, String, String, Runnable)` — generates `@annotation static Type name = value` |
| Joint ID construction | `appendNewJointId(String, String)` — `new JointId(name: "joint", parent: ref)` |
| Joint array ID construction | `appendNewJointArrayId(String, String)` — `new JointArrayId(root: ref, pattern: "pattern")` |
| Field reference | `getFieldReference(String, String)` — returns `TweedleTypeName.field` |
| Pose construction | `appendNewPose(InstantiableTweedleNode[])` — `new JointedModelPose(pairs: new JointIdTransformationPair[]{...})` |
| Joint transformation | `appendNewJointTransformation(String, AffineMatrix4x4)` — `new JointIdTransformationPair(joint: ..., orientation: ..., position: ...)` |

`ResourceEncoder` stores a `TweedleEncoder` reference passed at construction
and uses it for all bridge and forwarding calls. This matches the
`StatementEncoder(TweedleEncoder)` and `ExpressionEncoder(TweedleEncoder)`
field-storage pattern from steps 1 and 2. Methods do not take the encoder as an
additional parameter.

The constructor signature:

```java
ResourceEncoder(TweedleEncoder encoder) {
  this.encoder = encoder;
}
```

### TweedleEncoder changes

| Change | Detail |
| --- | --- |
| New field | `private final ResourceEncoder resourceEncoder` |
| Constructor wiring | `this.resourceEncoder = new ResourceEncoder(this)` alongside existing `statementEncoder` and `expressionEncoder` |
| `USER_PREFIX` visibility | Changed from `private static final` to `static final` (package-private) so `ResourceEncoder.getUserJointIdentifier` can read it |
| `processResourceType(String)` | Body delegates to `resourceEncoder.processResourceType(jointedModelResource)` |
| `processDynamicResource(String, String, InstantiableTweedleNode[])` | Body delegates to `resourceEncoder.processDynamicResource(...)` |
| `getUserJointIdentifier(String)` | Body delegates to `resourceEncoder.getUserJointIdentifier(jointIdentifier)` |
| `appendNewJointId(String, String)` | Body delegates to `resourceEncoder.appendNewJointId(joint, parentReference)` |
| `appendNewJointArrayId(String, String)` | Body delegates to `resourceEncoder.appendNewJointArrayId(pattern, startingJoint)` |
| `getFieldReference(String, String)` | Body delegates to `resourceEncoder.getFieldReference(type, field)` |
| `appendNewPose(InstantiableTweedleNode[])` | Body delegates to `resourceEncoder.appendNewPose(jointTransformations)` |
| `appendNewJointTransformation(String, AffineMatrix4x4)` | Body delegates to `resourceEncoder.appendNewJointTransformation(jointId, transformation)` |
| 6 private methods removed | `appendResourceConstructor`, `appendResourceInstances`, `appendResourceInstance`, `appendResourceFields`, `appendAddedJoints`, `appendStaticField` (both overloads) — moved entirely to `ResourceEncoder` |
| Bridge methods added | `forwardGetCodeStringBuilder()`, `forwardOpenBlock()`, `forwardAppendClassFooter(String)` — package-private forwarding methods that bridge inherited `protected` methods |
| 10 private methods widened | `appendInstantiation`, `appendArg` (×2), `appendAnotherArg` (×2), `quoteString`, `appendAssignmentOperator`, `appendSingleCodeLine`, `appendVisibilityTag`, `appendList`, `getListSeparator` — changed from `private` to package-private so `ResourceEncoder` can call them directly |
| 4 unused imports removed | `Tuple3`, `UnitQuaternion`, `java.lang.reflect.Field`, `IdentifiableTweedleNode` — no longer directly referenced in `TweedleEncoder` |

The `@Override` annotations remain on `TweedleEncoder` for `processResourceType`
and `processDynamicResource` because the visitor pattern in
`SourceCodeGenerator` requires the override stubs on the subclass. The public
`appendNewJointId`, `appendNewJointArrayId`, `getFieldReference`,
`appendNewPose`, and `appendNewJointTransformation` methods retain their
signatures because AST nodes call them via `encodeDefinition(this)`.

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
`StatementEncoder`, `ExpressionEncoder`, and `ResourceEncoder`. Callers never
see the delegate classes.

The public methods `appendNewJointId`, `appendNewJointArrayId`,
`getFieldReference`, `appendNewPose`, and `appendNewJointTransformation` remain
on `TweedleEncoder` as thin delegation stubs. AST nodes calling
`encodeDefinition(this)` continue to call these methods through the same
`TweedleEncoder` reference — no call sites change.

## Package-private collaboration

`ResourceEncoder` accesses `TweedleEncoder` methods via package-private
forwarding methods and widened package-private methods. The following methods on
`TweedleEncoder` are used by `ResourceEncoder`:

| Method | Purpose |
| --- | --- |
| `forwardAppendString(String)` | Append raw string to output buffer (exists from step 1) |
| `forwardAppendSpace()` | Append single space character (exists from step 1) |
| `forwardAppendNewLine()` | Append platform newline (exists from step 1) |
| `forwardAppendEscapedString(String)` | Write a quoted, escaped string (exists from step 2) |
| `forwardGetCodeStringBuilder()` | Access the `StringBuilder` for direct `append` calls (new bridge) |
| `forwardOpenBlock()` | Write `{` and increase indent (new bridge) |
| `forwardAppendClassFooter(String)` | Close the class block and write footer (new bridge) |
| `forwardAppendIndent()` | Write current indentation (exists from step 1) |
| `forwardBracketize(Runnable)` | Write `{`, run body, write `}` (exists from step 1) |
| `appendInstantiation(String, Runnable)` | Write `new Type(`, run args, write `)` (widened to package-private) |
| `appendArg(String, String)` | Write first labeled argument as string (widened to package-private) |
| `appendArg(String, Runnable)` | Write first labeled argument as Runnable (widened to package-private) |
| `appendAnotherArg(String, String)` | Write additional labeled argument as string (widened to package-private) |
| `appendAnotherArg(String, Runnable)` | Write additional labeled argument as Runnable (widened to package-private) |
| `quoteString(String)` | Write quoted string literal (widened to package-private) |
| `appendAssignmentOperator()` | Write ` <- ` (widened to package-private) |
| `appendSingleCodeLine(Runnable)` | Write indented single line (widened to package-private) |
| `appendVisibilityTag(FieldTemplate)` | Write `@annotation ` prefix from template (widened to package-private) |
| `appendList(T[], Consumer, String)` | Write comma-separated list (widened to package-private) |
| `getListSeparator()` | Get the list separator string (widened to package-private) |
| `appendStatementCompletion()` | Write statement end — delegates to `statementEncoder` (already package-private via override) |
| `tweedleTypeName(String)` | Translate Java type name to Tweedle name (already package-private via override) |
| `USER_PREFIX` | Package-private static constant `"u_"` (was `private`) |

No interfaces or inheritance are introduced. All collaboration uses direct
method calls within the same package, matching the
[StatementEncoder pattern](./statement-encoder-extraction.md),
[ExpressionEncoder pattern](./expression-encoder-extraction.md), and the
[Decoder delegate pattern](./decoder-delegate-decomposition.md).

## Bridge methods on TweedleEncoder

Because `TweedleEncoder` extends `SourceCodeGenerator` (in a different package),
`ResourceEncoder` cannot call inherited `protected` methods directly — Java
accessibility rules prevent a same-package class from calling `protected`
methods inherited from a class in a different package. Three new forwarding
methods are added:

```java
// TweedleEncoder.java — package-private bridges for ResourceEncoder
StringBuilder forwardGetCodeStringBuilder() {
  return getCodeStringBuilder();
}

void forwardOpenBlock() {
  openBlock();
}

void forwardAppendClassFooter(String resourceType) {
  appendClassFooter(resourceType);
}
```

These join the existing bridges from steps 1 and 2 (`forwardAppendString`,
`forwardAppendSpace`, `forwardAppendNewLine`, `forwardAppendIndent`,
`forwardBracketize`, `forwardAppendEscapedString`).

Unlike the `StatementEncoder` bridges which call `super.method()` to invoke the
parent implementation that the override would otherwise replace, the
`ResourceEncoder` forwarding methods simply call the inherited method — there is
no `super` vs `this` distinction because the extracted methods fully implement
the behavior rather than wrapping a parent implementation.

The `@Override` delegation pattern on `TweedleEncoder`:

```java
// TweedleEncoder.java — @Override stays here for polymorphic dispatch
@Override
public void processResourceType(String jointedModelResource) {
  resourceEncoder.processResourceType(jointedModelResource);
}

@Override
public void processDynamicResource(String dynamicResourceClass,
    String variant, InstantiableTweedleNode[] addedJoints) {
  resourceEncoder.processDynamicResource(dynamicResourceClass, variant, addedJoints);
}
```

The AST-callback public methods delegate directly:

```java
public void appendNewJointId(String joint, String parentReference) {
  resourceEncoder.appendNewJointId(joint, parentReference);
}

public void appendNewJointArrayId(String pattern, String startingJoint) {
  resourceEncoder.appendNewJointArrayId(pattern, startingJoint);
}

public String getFieldReference(String type, String field) {
  return resourceEncoder.getFieldReference(type, field);
}

public void appendNewPose(InstantiableTweedleNode[] jointTransformations) {
  resourceEncoder.appendNewPose(jointTransformations);
}

public void appendNewJointTransformation(String jointId,
    AffineMatrix4x4 transformation) {
  resourceEncoder.appendNewJointTransformation(jointId, transformation);
}
```

## Visibility changes

| Symbol | Before | After | Reason |
| --- | --- | --- | --- |
| `USER_PREFIX` | `private static final String` | `static final String` (package-private) | Read by `ResourceEncoder.getUserJointIdentifier` |
| `appendInstantiation(String, Runnable)` | `private` | package-private (no modifier) | Called by `ResourceEncoder.appendNewJointId`, `appendNewJointArrayId`, `appendNewPose`, `appendNewJointTransformation`, `appendResourceInstance` |
| `appendArg(String, String)` | `private` | package-private | Called by `ResourceEncoder` for labeled argument encoding |
| `appendArg(String, Runnable)` | `private` | package-private | Called by `ResourceEncoder` for labeled argument encoding |
| `appendAnotherArg(String, String)` | `private` | package-private | Called by `ResourceEncoder` for additional argument encoding |
| `appendAnotherArg(String, Runnable)` | `private` | package-private | Called by `ResourceEncoder` for additional argument encoding |
| `quoteString(String)` | `private` | package-private | Called by `ResourceEncoder.appendNewJointId`, `appendNewJointArrayId` |
| `appendAssignmentOperator()` | `private` | package-private | Called by `ResourceEncoder.appendResourceInstance`, `appendAddedJoints` |
| `appendSingleCodeLine(Runnable)` | `private` | package-private | Called by `ResourceEncoder.appendStaticField`, `appendAddedJoints` |
| `appendVisibilityTag(FieldTemplate)` | `private` | package-private | Called by `ResourceEncoder.appendStaticField` |
| `appendList(T[], Consumer, String)` | `private` | package-private | Called by `ResourceEncoder.appendResourceFields`, `appendAddedJoints`, `appendNewPose` |
| `getListSeparator()` | `private` | package-private | Called by `ResourceEncoder.appendResourceFields`, `appendAddedJoints` |

All widened members remain inaccessible outside the package. The `static final`
constant `USER_PREFIX` is `"u_"` — a string literal with no security
sensitivity. The method widenings expose formatting primitives only to
`ResourceEncoder` within the same package.

## Security boundary

`ResourceEncoder` consolidates all `Class.forName` reflection in the encoder.
The `processResourceType` and `processDynamicResource` methods use
`Class.forName(jointedModelResource)` to load resource classes by their
fully-qualified Java name. This pattern exists unchanged from the original
`TweedleEncoder` — extraction does not broaden or narrow the reflection scope.

The reflected class names originate from Alice project AST nodes
(`AbstractResource.getClassName()`), which are bounded to the Alice model
registry (e.g., `org.lgna.story.resources.biped.AlienResource`). No
user-supplied freeform strings reach `Class.forName` — the class names are
determined by the project's declared model resources.

`ClassNotFoundException` is caught and rethrown as `RuntimeException` with a
descriptive message, matching the original behavior. `IllegalAccessException` on
field reflection is logged and the field is skipped, matching the original
behavior.

No new I/O, network, or thread operations are introduced. `ResourceEncoder` is
package-private and stored in a `private final` field on `TweedleEncoder`. All
method calls are compile-time verified — no reflection is used for delegation.

## Error handling contract

Error handling is preserved identically from the original methods:

| Method | Exception | Handling |
| --- | --- | --- |
| `processResourceType` | `ClassNotFoundException` | Rethrown as `RuntimeException` with descriptive message |
| `processDynamicResource` | `ClassNotFoundException` | Rethrown as `RuntimeException` with descriptive message |
| `appendResourceFields` | `IllegalAccessException` | Logged via `Logger.info`, field skipped |

The `RuntimeException` wrapping preserves the original stack trace via the
`cause` parameter. The `Logger.info` message format is unchanged:
`"Export will skip inaccessible field ClassName.fieldName"`.

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
  -Dtest=TweedleEncoderTest,TweedleEncoderRenameContractTest,TweedleEncoderDecoderTest,SourceCodeGeneratorTest,DecoderDelegateDecompositionCharacterizationTest,SilverThreadTweedleDecoderRoundTripTest \
  -Dcheckstyle.skip \
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
| `ResourceEncoder.java` exists | File present in `core/ast/src/main/java/org/alice/serialization/tweedle/` |
| `ResourceEncoder` is package-private | No `public` keyword on class declaration |
| Constructor takes `TweedleEncoder` | `ResourceEncoder(TweedleEncoder encoder)` |
| 14 methods extracted | `processResourceType`, `processDynamicResource`, `getUserJointIdentifier`, `appendResourceConstructor`, `appendResourceInstances`, `appendResourceInstance`, `appendResourceFields`, `appendAddedJoints`, `appendStaticField` (×2), `appendNewJointId`, `appendNewJointArrayId`, `getFieldReference`, `appendNewPose`, `appendNewJointTransformation` |
| `USER_PREFIX` widened to package-private | No `private` modifier on the constant in `TweedleEncoder` |
| 10 private methods widened to package-private | `appendInstantiation`, `appendArg` (×2), `appendAnotherArg` (×2), `quoteString`, `appendAssignmentOperator`, `appendSingleCodeLine`, `appendVisibilityTag`, `appendList`, `getListSeparator` |
| 3 new bridge methods on TweedleEncoder | `forwardGetCodeStringBuilder`, `forwardOpenBlock`, `forwardAppendClassFooter` |
| `TweedleEncoder` delegates `@Override` bodies | `processResourceType`, `processDynamicResource` delegate to `resourceEncoder` |
| Public method stubs delegate | `appendNewJointId`, `appendNewJointArrayId`, `getFieldReference`, `appendNewPose`, `appendNewJointTransformation`, `getUserJointIdentifier` delegate to `resourceEncoder` |
| 6 private resource methods removed from TweedleEncoder | `appendResourceConstructor`, `appendResourceInstances`, `appendResourceInstance`, `appendResourceFields`, `appendAddedJoints`, `appendStaticField` (×2) — no longer present |
| 4 unused imports removed | `Tuple3`, `UnitQuaternion`, `java.lang.reflect.Field`, `IdentifiableTweedleNode` |
| TweedleEncoder under 830 lines | `wc -l` confirms ~820 lines (was 988) |
| `TweedleEncoderDecoder.java` unchanged | `git diff` shows no changes |
| `StatementEncoder.java` unchanged | `git diff` shows no changes |
| `ExpressionEncoder.java` unchanged | `git diff` shows no changes |
| `TweedleEncoderTest` passes | Zero failures |
| `TweedleEncoderRenameContractTest` passes | Zero failures |
| `TweedleEncoderDecoderTest` passes | Zero failures |
| `SourceCodeGeneratorTest` passes | Zero failures |
| `DecoderDelegateDecompositionCharacterizationTest` passes | Zero failures |
| `SilverThreadTweedleDecoderRoundTripTest` passes | Zero failures |
| `core/story-api-migration` tests pass | Zero failures |
| Tweedle output is byte-identical | Encoder tests assert exact string output |

## Claim boundaries

This extraction proves:

- The 14 resource-encoding methods can be extracted to a delegate without
  changing observable behavior.
- Resource type class generation via `Class.forName` reflection works correctly
  through the delegate indirection, producing identical Tweedle class structures.
- Dynamic resource variant generation with user-defined joints produces
  identical output when the logic resides on `ResourceEncoder`.
- Joint ID, joint array, pose, and transformation encoding — called by AST nodes
  via `encodeDefinition(this)` — produce identical Tweedle through the
  delegation stubs on `TweedleEncoder`.
- The `USER_PREFIX` constant for joint identifier prefixing is correctly
  accessed as a package-private static field from `ResourceEncoder`.
- Superclass-specific resource constructor parameters (FlyerResource,
  QuadrupedResource, SlithererResource) produce identical constructor bodies.
- Field reflection with `IllegalAccessException` handling is preserved
  identically.
- All existing encoder test assertions pass identically.
- The step 1 `StatementEncoder` and step 2 `ExpressionEncoder` extractions
  remain unaffected.

This extraction does **not** prove:

| Non-claim | Reason |
| --- | --- |
| Full encoder decomposition complete | `EncoderMappings` extraction is a future step; `StatementEncoder`, `ExpressionEncoder`, and `ResourceEncoder` are extracted but the static maps remain on `TweedleEncoder`. |
| New encode capabilities | No new Tweedle constructs are supported. |
| Performance improvement | Extraction is structural, not algorithmic. |
| Thread safety | `TweedleEncoder` was not thread-safe before; extraction does not change this. |
| Public API expansion | No new public methods or classes are introduced. |
| TweedleEncoder under 500 lines | The 500-line target requires the `EncoderMappings` extraction (step 4). This step reduces to ~820 lines. |
| New resource type support | Only existing Alice model resource classes are handled. |

Adjacent claims are owned by their own documents:

| Claim | Document |
| --- | --- |
| Full encoder delegate decomposition | [Encoder Delegate Decomposition](./encoder-delegate-decomposition.md) |
| Statement encoder extraction (step 1) | [StatementEncoder Extraction](./statement-encoder-extraction.md) |
| Expression encoder extraction (step 2) | [ExpressionEncoder Extraction](./expression-encoder-extraction.md) |
| Decoder delegate decomposition | [Decoder Delegate Decomposition](./decoder-delegate-decomposition.md) |
| TweedleEncoder rename | [TweedleEncoder Rename](./tweedle-encoder-rename.md) |
