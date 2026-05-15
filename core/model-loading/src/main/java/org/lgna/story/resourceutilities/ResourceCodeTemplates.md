# ResourceCodeTemplates — Code Generation Template Extraction

## Overview

`ResourceCodeTemplates` is a package-private helper class extracted from
`ModelResourceJavaGenerator` as part of issue #647. It owns the six
StringBuilder-based template methods that render the body of generated
`*Resource.java` enum files. The extraction reduced `ModelResourceJavaGenerator`
from 607 lines to ~400 lines while keeping every public and package-private
method signature on the generator unchanged.

## Architecture

```
ModelResourceJavaGenerator            ResourceCodeTemplates
 ├─ buildJavaCodeBody()  ────────────►  appendPreambleAndEnumConstants()
 │    delegates to six                  appendJointDeclarations()  → List<String>
 │    static methods                    appendRootJointIds()
 │                                      appendPoseFields()
 │  ◄── callbacks ──────────────────    appendArrayFields()
 │  (12 generator methods/constants)    appendConstructorsAndMethods()
 │                                      (private) appendEnumConstants()
 ├─ getJavaClassName()
 ├─ createJavaFile()                  Sibling utilities (also called):
 ├─ getExistingJointIds()               ModelResourceJointTreeUtilities
 ├─ getAccessorMethodsForResourceClass()  ModelResourceArrayUtilities
 ├─ getJointAccessCodeForClass()        JavaCodeUtilities
 └─ … (15+ methods unchanged)
```

### Design Decisions

| Decision | Rationale |
|---|---|
| Package-private class, package-private static methods | No new public API surface; templates are internal implementation detail |
| Static methods receiving all data as parameters | No coupling back to generator instance state; easy to test in isolation |
| `javaClassName` passed as `String` parameter | Avoids dependency from templates back to `ModelResourceJavaGenerator.getJavaClassName()` |
| `appendJointDeclarations` returns `List<String>` (root joints) | Caller derives `addedRoots = !rootJoints.isEmpty()` — clean data flow, no mutable out-parameter |
| Templates call back into generator static methods (same package) | `needsAccessorMethodForFieldName`, `getAccessorMethodName`, `needsToDefineRootsMethod`, `getJointRootsField`, `getJointAccessMethodNameForArrayJoint`, `getPoseBuilderTypeForSuperClass` — all package-private statics that remain on the generator |
| Templates call sibling utility classes directly | `ModelResourceJointTreeUtilities.isRootJoint`, `ModelResourceArrayUtilities.getArrayIndexForJoint` — already used by the generator; no new coupling |
| Templates reference generator constants | `ROOT_IDS_FIELD_NAME`, `ROOT_IDS_METHOD_NAME` — accessed as `ModelResourceJavaGenerator.ROOT_IDS_FIELD_NAME` etc. |
| Dead code carried forward (`poseBuilderType`) | `getPoseBuilderTypeForSuperClass` result is unused at source line 450; kept for byte-identical output — cleanup is a separate task |
| Private constructor | Utility class; never instantiated |

## API Reference

All methods are **package-private** (`static`, no access modifier) and live in
`org.lgna.story.resourceutilities.ResourceCodeTemplates`.

### appendPreambleAndEnumConstants

```java
static void appendPreambleAndEnumConstants(
    StringBuilder sb,
    ModelResourceExporter exporter,
    String javaClassName)
```

Appends the copyright header, `package` declaration, `import` statements,
optional `@Deprecated` annotation, the `public enum … implements …` declaration,
and all enum constant entries (via the private `appendEnumConstants` helper).

**Parameters:**
- `sb` — target buffer (mutated in place)
- `exporter` — the model resource exporter providing class data and sub-resources
- `javaClassName` — the generated class name (e.g. `"AlienResource"`)

### appendJointDeclarations

```java
static List<String> appendJointDeclarations(
    StringBuilder sb,
    List<Tuple2<String, String>> trimmedSkeleton,
    Set<String> existingIds,
    Map<String, String> jointToArrayName,
    Set<String> suppressJointIds,
    Set<String> hideElementArrays,
    Set<String> exposeFirstArrays,
    String javaClassName)
```

Iterates the skeleton tree and appends `public static final JointId` field
declarations. Applies `@FieldTemplate` annotations for visibility control
(COMPLETELY_HIDDEN for suppressed/root joints, PRIME_TIME for visible joints).

**Returns:** `List<String>` — the names of root joints discovered (parent is
null or empty). The caller uses this to decide whether to emit the
`JOINT_ID_ROOTS` array.

**Parameters:**
- `sb` — target buffer
- `trimmedSkeleton` — code-ready joint tree from `makeCodeReadyTree`
- `existingIds` — joint IDs already declared on the super class
- `jointToArrayName` — reverse lookup: joint name → owning array name
- `suppressJointIds` — joints to mark COMPLETELY_HIDDEN
- `hideElementArrays` — arrays whose individual elements are hidden
- `exposeFirstArrays` — arrays where the first element gets PRIME_TIME visibility
- `javaClassName` — the generated class name

### appendRootJointIds

```java
static void appendRootJointIds(
    StringBuilder sb,
    List<String> rootJoints)
```

Appends the `JOINT_ID_ROOTS` static field declaration if root joints exist.
Only called when `rootJoints` is non-empty.

**Parameters:**
- `sb` — target buffer
- `rootJoints` — root joint names returned by `appendJointDeclarations`

### appendPoseFields

```java
static void appendPoseFields(
    StringBuilder sb,
    Map<String, Map<String, AffineMatrix4x4>> poseEntries,
    List<String> mandatoryPoseNames,
    ModelClassData classData,
    String javaClassName)
    throws java.util.zip.DataFormatException
```

Appends pose constant declarations (`public static final JointedModelPose …`)
with their `JointIdTransformationPair` arrays. Also appends accessor methods
when the super class interface requires them.

**Throws:** `DataFormatException` if a mandatory pose has no data.

**Parameters:**
- `sb` — target buffer
- `poseEntries` — pose name → (joint name → transform) map
- `mandatoryPoseNames` — poses required by the super class interface
- `classData` — the model class metadata
- `javaClassName` — the generated class name

### appendArrayFields

```java
static void appendArrayFields(
    StringBuilder sb,
    Map<String, List<String>> arrayEntries,
    List<String> mandatoryArrayNames,
    List<String> declaredArrays,
    List<Tuple2<String, String>> trimmedSkeleton,
    Set<String> hideElementArrays,
    ModelClassData classData,
    String javaClassName)
```

Appends joint array declarations — either `JointArrayId` (for hidden-element
arrays) or `JointId[]` (for standard arrays). Skips arrays already declared on
the super class. Adds accessor methods when the super class interface mandates
them.

**Parameters:**
- `sb` — target buffer
- `arrayEntries` — array name → list of joint names
- `mandatoryArrayNames` — arrays required by the super class interface
- `declaredArrays` — array names already on the super class
- `trimmedSkeleton` — the skeleton tree (for parent lookup of hidden arrays)
- `hideElementArrays` — arrays whose elements are hidden behind a `JointArrayId`
- `classData` — the model class metadata
- `javaClassName` — the generated class name

### appendConstructorsAndMethods

```java
static void appendConstructorsAndMethods(
    StringBuilder sb,
    boolean addedRoots,
    ModelClassData classData,
    String javaClassName)
```

Appends the `resourceType` field, private constructors, `getRootJointIds()`,
`getImplementationAndVisualFactory()`, and `createImplementation()` methods,
plus the closing brace. Uses `classData.superClass` for roots field lookup
via `ModelResourceJavaGenerator.getJointRootsField()` and
`ModelResourceJavaGenerator.needsToDefineRootsMethod()`.

**Parameters:**
- `sb` — target buffer
- `addedRoots` — whether root joint IDs were declared (controls `getRootJointIds` body)
- `classData` — the model class metadata (superClass, implementationClass, abstractionClass)
- `javaClassName` — the generated class name

## Cross-Class Dependencies

`ResourceCodeTemplates` calls back into `ModelResourceJavaGenerator` and
sibling utility classes in the same package (package-private access):

### Callbacks to `ModelResourceJavaGenerator`

| Template method | Generator methods / constants used |
|---|---|
| `appendPreambleAndEnumConstants` | `createResourceEnumName`, `isValidEnumName` (via private `appendEnumConstants`) |
| `appendJointDeclarations` | `getJointAccessMethodNameForArrayJoint` |
| `appendRootJointIds` | `ROOT_IDS_FIELD_NAME` constant |
| `appendPoseFields` | `needsAccessorMethodForFieldName`, `getAccessorMethodName`, `getPoseBuilderTypeForSuperClass`† |
| `appendArrayFields` | `needsAccessorMethodForFieldName`, `getAccessorMethodName` |
| `appendConstructorsAndMethods` | `needsToDefineRootsMethod`, `getJointRootsField`, `ROOT_IDS_FIELD_NAME`, `ROOT_IDS_METHOD_NAME` |

† `getPoseBuilderTypeForSuperClass` result (`poseBuilderType`) is assigned but
never used in the current source (dead code at line 450). We carry it forward
for byte-identical output; removing it is a separate cleanup.

### Callbacks to sibling utility classes

| Template method | Utility class | Method |
|---|---|---|
| `appendJointDeclarations` | `ModelResourceJointTreeUtilities` | `isRootJoint` |
| `appendJointDeclarations` | `ModelResourceArrayUtilities` | `getArrayIndexForJoint` |

### Shared utilities

Uses `JavaCodeUtilities.LINE_RETURN` and `JavaCodeUtilities.getCopyrightComment()`
from the same package.

## Usage

`ResourceCodeTemplates` is never used directly by external code. It is called
exclusively from `ModelResourceJavaGenerator.buildJavaCodeBody()`:

```java
// In ModelResourceJavaGenerator.buildJavaCodeBody():
static String buildJavaCodeBody(ModelResourceExporter exporter)
    throws DataFormatException {

    StringBuilder sb = new StringBuilder();
    String javaClassName = getJavaClassName(exporter);

    ResourceCodeTemplates.appendPreambleAndEnumConstants(sb, exporter, javaClassName);

    // … prepare skeleton, lookup maps, etc. …

    List<String> rootJoints = ResourceCodeTemplates.appendJointDeclarations(
        sb, trimmedSkeleton, existingIds, jointToArrayName,
        suppressJointIds, hideElementArrays, exposeFirstArrays, javaClassName);
    boolean addedRoots = !rootJoints.isEmpty();

    if (addedRoots) {
        ResourceCodeTemplates.appendRootJointIds(sb, rootJoints);
    }

    ResourceCodeTemplates.appendPoseFields(
        sb, poseEntries, mandatoryPoseNames,
        exporter.getClassData(), javaClassName);

    ResourceCodeTemplates.appendArrayFields(
        sb, arrayEntries, mandatoryArrayNames, declaredArrays,
        trimmedSkeleton, hideElementArrays,
        exporter.getClassData(), javaClassName);

    ResourceCodeTemplates.appendConstructorsAndMethods(
        sb, addedRoots, exporter.getClassData(), javaClassName);

    return sb.toString();
}
```

## Testing

No new test class is needed for `ResourceCodeTemplates`. The existing
characterization tests provide full coverage:

| Test | What it validates |
|---|---|
| `ModelResourceJavaGeneratorTest.buildJavaCodeBodyProducesCompleteEnumCode` | Round-trip: generated code contains package, enum, constants, and methods |
| `ModelResourceJavaGeneratorTest.createJavaFileWritesCompilableCode` | End-to-end: file written to disk contains expected enum and constant |
| `ModelExportTest` extraction contract tests (reflection) | All 15+ methods remain on `ModelResourceJavaGenerator` with correct signatures |
| `ModelExportTest` behavioral characterization tests | `getExistingJointIds`, `getAccessorMethodsForResourceClass`, `getJointAccessCodeForClass` produce identical output |

Because `ResourceCodeTemplates` is package-private with no public API, it is
tested exclusively through `ModelResourceJavaGenerator` — any output divergence
is caught by the existing `buildJavaCodeBody` characterization test.

## Configuration

No configuration is required. `ResourceCodeTemplates` has no tuneable
parameters, system properties, or environment variables. It is a pure
computation class with no I/O.

## File Inventory

| File | Lines (est.) | Role |
|---|---|---|
| `ModelResourceJavaGenerator.java` | ~400 | Orchestrator: data prep, lookups, delegation |
| `ResourceCodeTemplates.java` | ~220 | Templates: StringBuilder rendering |

Both files live in:
```
core/model-loading/src/main/java/org/lgna/story/resourceutilities/
```

## Migration Notes

- **No API changes.** All callers of `ModelResourceJavaGenerator` are unaffected.
- **No new dependencies.** `ResourceCodeTemplates` uses only types already
  imported by the generator.
- **No reflection changes.** `ModelExportTest` reflection tests target
  `ModelResourceJavaGenerator.class` and continue to pass.
- **Byte-identical output.** The generated `*Resource.java` files are identical
  before and after this extraction.
