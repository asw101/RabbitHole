# StoryApiConfigurationManager Decomposition

> **Module:** `core/ide`
> **Package:** `org.alice.stageide`
> **Issue:** #661 — Reduce StoryApiConfigurationManager.java (587 → ≤500 lines)

## Overview

`StoryApiConfigurationManager` is the central Alice 3 IDE configuration hub —
it registers icon factories, builds method/function composite lists, provides
type comparators, manages instance-factory menus for jointed types, augments
user types with generated joint-accessor methods, and handles pose builder
resolution.

At 587 lines, the class mixed three distinct responsibilities:

1. **Type comparison** — a 39-line inner `TypeComparator` enum that ranks
   Alice types for display ordering (primitives first, then scene entities,
   then colors/transforms, then joints last).
2. **Joint method augmentation** — 139 lines of logic that inspects user-type
   constructors, identifies jointed-model resource types, and generates
   `getJoint()` / `getJointArray()` / `strikePose()` methods on user types.
3. **IDE configuration** — icon registration, composite lists, menu models,
   field-access labels, expression creators, and export/pose utilities.

The decomposition extracts concerns #1 and #2 into focused helper classes
while leaving the IDE configuration core in `StoryApiConfigurationManager`.

## Architecture

All three classes live in `org.alice.stageide` (same package). The extracted
helpers are package-private top-level classes, not inner classes.

```
┌──────────────────────────────────────────────────────┐
│          StoryApiConfigurationManager                │  ~450 lines
│  (icon registration, composite lists,                │
│   menu models, field-access labels,                  │
│   expression creator, export/pose utils)             │
│                                                      │
│  getTypeComparator()                                 │
│    → returns StoryTypeComparator.SINGLETON            │
│                                                      │
│  augmentTypeIfNecessary(UserType<?>)                  │
│    → delegates to JointMethodAugmentor.augment()     │
└──────────────┬───────────────────┬───────────────────┘
               │ returns SINGLETON │ delegates augmentation
               ▼                   ▼
┌────────────────────┐  ┌─────────────────────────────┐
│ StoryTypeComparator│  │    JointMethodAugmentor     │
│                    │  │                             │
│ Enum singleton     │  │  Final class. Package-      │
│ Comparator for     │  │  private static entry       │
│ AbstractType sort  │  │  point: augment(UserType<?>)│
│ ordering. Ranks    │  │                             │
│ primitives, scene  │  │  Generates joint-access     │
│ types, colors,     │  │  methods on user types      │
│ transforms, joints.│  │  from resource fields       │
│                    │  │  and dynamic joints.        │
└────────────────────┘  └─────────────────────────────┘
```

## Class Reference

### StoryApiConfigurationManager

**Role:** Central IDE configuration hub. Owns icon registration, method/function
composite lists, instance-factory menu models, field-access label creation,
expression creator access, signature locking, export filtering, and pose builder
resolution. Delegates type comparison and joint augmentation to extracted helpers.

**Unchanged public API:** Every public and protected method retains its original
signature. Callers (including `SimsStoryApiConfigurationManager` in core-nonfree)
require no changes.

| Visibility | Member | Change |
|---|---|---|
| `public` | `getTypeComparator()` | Now returns `StoryTypeComparator.SINGLETON` (one-line body) |
| `public` | `augmentTypeIfNecessary(UserType<?>)` | Now delegates to `JointMethodAugmentor.augment(rv)` (one-line body) |
| `protected static` | `BIPED_RESOURCE_TYPE` | **Retained** — accessed by `SimsStoryApiConfigurationManager` |
| `public static` | `SET_ACTIVE_SCENE_METHOD` | Unchanged |
| `public static` | `getInstance()` | Unchanged |
| `public` | All other overrides | Unchanged |

**Removed members:**

| Former Member | Moved To |
|---|---|
| `TypeComparator` inner enum (`private static`) | `StoryTypeComparator` top-level enum (package-private — widens visibility) |
| `JOINTED_MODEL_TYPE` constant | `JointMethodAugmentor.JOINTED_MODEL_TYPE` |
| `getFieldMethodNameHint(AbstractField)` | `JointMethodAugmentor` private static method |
| `addMethodsToType(UserType<?>, DynamicResource)` | `JointMethodAugmentor` private static method (was instance; uses no instance state) |
| `addMethodsToType(UserType<?>, AbstractType<?,?,?>)` | `JointMethodAugmentor` private static method (was instance; uses no instance state) |
| `getArgumentField(AbstractConstructor)` | `JointMethodAugmentor` private static method |

### StoryTypeComparator

**Role:** Enum-singleton `Comparator<AbstractType<?, ?, ?>>` that defines the
display ordering for Alice types in IDE dialogs. Primitives sort first
(Boolean 1.1, Double 1.2, Integer 1.3, String 1.4), then SThing (10.1),
then colors/paint (20.x), then spatial types (30.x), then everything else
(50.0), then SJoint last (99.9). Ties break alphabetically by type name.

**Visibility:** Package-private top-level enum (no `public` keyword).

**File:** `core/ide/src/main/java/org/alice/stageide/StoryTypeComparator.java`

| Visibility | Member | Purpose |
|---|---|---|
| `(pkg)` | `SINGLETON` | The sole enum instance |
| `private static final` | `DEFAULT_VALUE` | 50.0 — fallback sort rank for unknown types |
| `private final` | `mapTypeToValue` | Maps known `AbstractType` instances to sort-rank doubles |
| `private` | `getValue(AbstractType<?,?,?>)` | Returns sort rank, defaulting to 50.0 |
| `@Override` | `compare(AbstractType<?,?,?>, AbstractType<?,?,?>)` | Compares by rank, then alphabetically |

**Sort rank table:**

| Type | Rank | Category |
|---|---|---|
| `Boolean` | 1.1 | Primitives |
| `Double` | 1.2 | Primitives |
| `Integer` | 1.3 | Primitives |
| `String` | 1.4 | Primitives |
| `SThing` | 10.1 | Scene entity |
| `Color` | 20.1 | Appearance |
| `Paint` | 20.2 | Appearance |
| `Position` | 30.1 | Spatial |
| `Orientation` | 30.2 | Spatial |
| `VantagePoint` | 30.3 | Spatial |
| `SJoint` | 99.9 | Joints (last) |
| *(everything else)* | 50.0 | Default |

### JointMethodAugmentor

**Role:** Generates joint-accessor and pose methods on user types whose
superclass is `SJointedModel`. Inspects the user type's first constructor to
determine the resource type, then creates `UserMethod` instances for each
`JointId`, `JointId[]`, `JointArrayId`, and `Pose` field on that resource.
Also handles `DynamicResource` models that use string-based joint lookup.

**Visibility:** Package-private final class (no `public` keyword).

**File:** `core/ide/src/main/java/org/alice/stageide/JointMethodAugmentor.java`

| Visibility | Member | Purpose |
|---|---|---|
| `(pkg) static` | `augment(UserType<?>)` | Entry point. Returns the type after augmentation. No-op for non-jointed types |
| `private static final` | `JOINTED_MODEL_TYPE` | `JavaType.getInstance(SJointedModel.class)` — cached type reference |
| `private static` | `getFieldMethodNameHint(AbstractField)` | Reads `@FieldTemplate.methodNameHint` annotation from resource fields |
| `private static` | `addMethodsToType(UserType<?>, DynamicResource)` | Generates string-based `getJoint()` methods for dynamic joints |
| `private static` | `addMethodsToType(UserType<?>, AbstractType<?,?,?>)` | Generates typed `getJoint()`, `getJointArray()`, `strikePose()` methods from resource fields |
| `private static` | `getArgumentField(AbstractConstructor)` | Extracts the `JavaField` from the first constructor argument for resource-type inference |

**Augmentation decision tree:**

```
augment(userType)
  │
  ├─ Is userType assignable to SJointedModel?
  │   ├─ NO → return userType unchanged
  │   └─ YES
  │       ├─ Get first declared constructor
  │       ├─ firstArgument ← instantiate first super-constructor argument (Object)
  │       ├─ constructorParameterType ← constructor's first parameter type
  │       ├─ inferredResourceType ← constructorParameterType
  │       │   └─ if null → fallback: getArgumentField(constructor).getValueType()
  │       │
  │       ├─ constructorParameterType != ancestorType.firstParameterType?
  │       │   ├─ YES and inferredResourceType != null
  │       │   │   └─ addMethodsToType(rv, inferredResourceType)
  │       │   ├─ YES and firstArgument instanceof DynamicResource
  │       │   │   └─ addMethodsToType(rv, dynamicResource)
  │       │   ├─ YES otherwise
  │       │   │   └─ Logger.severe("Failed to augment...")
  │       │   └─ NO (same parameter type as ancestor) → skip augmentation
  │       │
  │       └─ return userType (possibly augmented)
```

**Generated method patterns:**

For each visible static field on the resource type:

| Resource Field Type | Generated Method | Return Type | Body |
|---|---|---|---|
| `JointId` | `get<Name>()` | `SJoint` | `return this.getJoint(<field>)` |
| `JointId[]` | `get<Name>()` | `SJoint[]` | `return this.getJointArray(<field>)` |
| `JointArrayId` | `get<Name>()` | `SJoint[]` | `return this.getJointArray(<field>)` |
| `Pose` | `<name>()` | `void` | `this.strikePose(<field>)` |

For `DynamicResource` models, each `getModelSpecificJoints()` entry generates:
`get<Name>()` → `SJoint` via `this.getJoint("<jointName>")`.

All generated methods have `ManagementLevel.GENERATED` and are not user-editable.

## Subclass Compatibility

`SimsStoryApiConfigurationManager` in `core-nonfree` extends
`StoryApiConfigurationManager`. Verified compatibility:

- Does **not** override `augmentTypeIfNecessary()` — delegation is transparent.
- Does **not** override `getTypeComparator()` — enum extraction is transparent.
- **Does** access `BIPED_RESOURCE_TYPE` — field remains `protected static` in
  the parent class.

No changes to `core-nonfree` are required.

## Import Changes

**StoryApiConfigurationManager** — the following imports are removed because
they are only used by the extracted code:

- `org.lgna.project.annotations.FieldTemplate`
- `org.lgna.project.annotations.Visibility`
- `org.lgna.story.resources.DynamicResource`
- `org.lgna.story.resources.JointArrayId`
- `org.lgna.story.resources.JointId`
- `java.lang.reflect.Field`

The `org.lgna.story.*` wildcard import remains (used by retained code).

## Non-Claims

- **No behavioral change.** This is a pure structural refactoring. All generated
  methods produce identical AST output.
- **No test coverage change.** No new tests are added or removed. Existing tests
  validate the augmentation behavior through downstream integration.
- **No public API change.** All public/protected method signatures on
  `StoryApiConfigurationManager` are unchanged.
- **No performance impact.** The delegation adds one static method call overhead
  per `augmentTypeIfNecessary` invocation (negligible, called once per type load).

## Files Changed

| File | Action | Lines Before | Lines After |
|---|---|---|---|
| `StoryApiConfigurationManager.java` | Modified | 587 | 391 |
| `StoryTypeComparator.java` | Created | — | 98 |
| `JointMethodAugmentor.java` | Created | — | 202 |
