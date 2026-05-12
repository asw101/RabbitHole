# Tutorial: Trace the ResourceEncoder Extraction

A guided walkthrough showing how resource-encoding requests flow through
`TweedleEncoder` and its new `ResourceEncoder` delegate. Use this to understand
the bridge, forwarding, and widened-method patterns before extending resource
encoding behavior.

## Prerequisites

- Familiarity with the visitor pattern used by `SourceCodeGenerator`
- Access to the `core/ast` source in `org.alice.serialization.tweedle`
- Recommended: read the [StatementEncoder extraction tutorial](./trace-statement-encoder-extraction.md)
  and [ExpressionEncoder extraction tutorial](./trace-expression-encoder-extraction.md)
  for the step 1 and step 2 patterns

## Overview

When a resource type is encoded to Tweedle source, the logic passes through
two classes:

```text
TweedleEncoder (@Override methods — visitor dispatch + public stubs for AST callbacks)
  └── ResourceEncoder (extracted resource-encoding logic)
      ├── processResourceType(String)             — full resource class generation
      ├── processDynamicResource(String, ...)      — variant resource class generation
      ├── getUserJointIdentifier(String)           — joint name prefixing
      ├── appendResourceConstructor(String, String) — superclass-specific constructor
      ├── appendResourceInstances(Class)           — enum constant instances
      ├── appendResourceInstance(String, String)   — single static instance
      ├── appendResourceFields(String, Class)      — reflected field enumeration
      ├── appendAddedJoints(String, Collection)    — ADDED_JOINTS, ALL_JOINTS, getJointIds()
      ├── appendStaticField (×2 overloads)         — annotated static field declaration
      ├── appendNewJointId(String, String)          — new JointId(name, parent)
      ├── appendNewJointArrayId(String, String)     — new JointArrayId(root, pattern)
      ├── getFieldReference(String, String)         — TypeName.fieldName
      ├── appendNewPose(InstantiableTweedleNode[]) — new JointedModelPose(pairs: ...)
      └── appendNewJointTransformation(String, AffineMatrix4x4) — JointIdTransformationPair
```

`TweedleEncoder` keeps the `@Override` stubs (required by `SourceCodeGenerator`)
and the public AST-callback stubs (called by nodes via `encodeDefinition(this)`).
Both types of stubs delegate the extracted logic to `ResourceEncoder`.

## Trace 1: A resource type class (e.g. AlienResource)

Follow the encoding of a `JointedModelResource` such as `AlienResource` through
the full class generation path.

### Step 1: Visitor dispatch

When `processResourceType("org.lgna.story.resources.biped.AlienResource")` is
called by the visitor pattern on `TweedleEncoder`:

```java
// TweedleEncoder.java
@Override
public void processResourceType(String jointedModelResource) {
  resourceEncoder.processResourceType(jointedModelResource);
}
```

The body is a one-line delegation. The `@Override` annotation stays on
`TweedleEncoder` because `SourceCodeGenerator` declares `processResourceType`
as a visitor method.

### Step 2: ResourceEncoder loads the resource class

```java
// ResourceEncoder.java
void processResourceType(String jointedModelResource) {
  try {
    Class<?> resourceClass = Class.forName(jointedModelResource);
    final String superclass = resourceClass.getInterfaces().length == 1
        ? resourceClass.getInterfaces()[0].getSimpleName()
        : "JointedModelInterface";
    encoder.forwardGetCodeStringBuilder()
        .append("class ").append(resourceClass.getSimpleName())
        .append(" extends ").append(superclass);
    encoder.openBlock();
    appendResourceConstructor(superclass, resourceClass.getSimpleName());
    appendResourceFields(superclass, resourceClass);
    appendResourceInstances(resourceClass);
    encoder.appendClassFooter(jointedModelResource);
  } catch (ClassNotFoundException cnfe) {
    throw new RuntimeException("Unable to find class " + jointedModelResource
        + " which should have been the caller type. ...", cnfe);
  }
}
```

**Key insight:** `Class.forName` is the only reflective call in the encoder.
The extraction consolidates all resource reflection in `ResourceEncoder` for
focused security review. The class name originates from the Alice project AST,
not from user-supplied freeform text.

### Step 3: Bridge methods and direct @Override access

The `ResourceEncoder` uses one bridge method for `SourceCodeGenerator`'s
`getCodeStringBuilder()` (inherited protected, not overridden):

```java
// TweedleEncoder.java (package-private bridge)
StringBuilder forwardGetCodeStringBuilder() {
  return getCodeStringBuilder();
}
```

For `openBlock()` and `appendClassFooter(String)`, no bridge is needed — both
are `@Override protected` on `TweedleEncoder`. Because the override is declared
in the same package as `ResourceEncoder`, Java allows direct access:

```java
// ResourceEncoder calls these directly on the TweedleEncoder reference:
encoder.openBlock();           // @Override protected — accessible from same package
encoder.appendClassFooter(...); // @Override protected — accessible from same package
```

This distinction matters: `getCodeStringBuilder()` and `bracketize(Runnable)` are
**not** overridden on `TweedleEncoder`, so they require bridge methods.
`openBlock()`, `appendClassFooter()`, `appendAssignmentOperator()`,
`appendSingleCodeLine()`, and `getListSeparator()` **are** overridden and are
directly accessible.

### Step 4: Resource constructor with superclass-specific parameters

```java
// ResourceEncoder.java
private void appendResourceConstructor(String superclass, String resourceName) {
  encoder.appendIndent();
  encoder.forwardAppendString(resourceName);
  encoder.forwardAppendString("(TextString name)");
  encoder.forwardBracketize(() -> {
    encoder.appendIndent();
    encoder.forwardAppendString("super(name: name");
    if ("FlyerResource".equals(superclass)) {
      encoder.forwardAppendString(",\n"
          + "          spreadWingsPose: " + resourceName + ".SPREAD_WINGS_POSE,\n"
          + "          foldWingsPose: " + resourceName + ".FOLD_WINGS_POSE,\n"
          + "          tailArray: " + resourceName + ".TAIL_ARRAY,\n"
          + "          neckArray: " + resourceName + ".NECK_ARRAY");
    }
    // ... QuadrupedResource and SlithererResource patterns ...
    encoder.forwardAppendString(");\n");
  });
}
```

The superclass-specific constructor bodies (FlyerResource with wing/tail/neck
arrays, QuadrupedResource and SlithererResource with tail arrays) are preserved
identically from the original `TweedleEncoder` implementation.

**Key insight:** `appendIndent` is a `private` method on `TweedleEncoder`
widened to package-private in step 3 — called directly as `encoder.appendIndent()`.
`forwardAppendString` and `forwardBracketize` are bridge methods:
`forwardAppendString` from step 1 and `forwardBracketize` new in step 3
(because `bracketize(Runnable)` is inherited protected from `SourceCodeGenerator`
and not overridden on `TweedleEncoder`).

### Step 5: Resource field reflection

```java
// ResourceEncoder.java
private void appendResourceFields(String superclass, Class<?> resourceClass) {
  Field[] fields = resourceClass.getDeclaredFields();
  List<String> newJoints = new ArrayList<>();
  for (Field field : fields) {
    try {
      Object value = field.get(resourceClass);
      if (value instanceof InstantiableTweedleNode node) {
        if (field.getType().getSimpleName().equals("JointId")) {
          newJoints.add(field.getName());
        }
        appendStaticField(field, () -> node.encodeDefinition(encoder));
      } else {
        // array handling with appendList...
      }
    } catch (IllegalAccessException e) {
      Logger.info("Export will skip inaccessible field " + ...);
    }
  }
  appendAddedJoints(superclass, newJoints);
}
```

**Key insight:** The `node.encodeDefinition(encoder)` call passes the
`TweedleEncoder` reference (stored as `this.encoder`), not `this`
(`ResourceEncoder`). This is critical because `encodeDefinition` calls back
to public methods on `TweedleEncoder` (`appendNewJointId`,
`appendNewJointArrayId`, etc.), which then delegate to `ResourceEncoder`. The
delegation chain is: AST node → `TweedleEncoder.appendNewJointId` →
`ResourceEncoder.appendNewJointId`.

## Trace 2: A dynamic resource variant

Follow the encoding of a user-created dynamic resource variant, such as a custom
biped with added joints.

### Step 1: TweedleEncoder delegates

```java
// TweedleEncoder.java
@Override
public void processDynamicResource(String dynamicResourceClass,
    String variant, InstantiableTweedleNode[] addedJoints) {
  resourceEncoder.processDynamicResource(dynamicResourceClass, variant, addedJoints);
}
```

### Step 2: ResourceEncoder generates the variant class

```java
// ResourceEncoder.java
void processDynamicResource(String dynamicResourceClass, String variant,
    InstantiableTweedleNode[] addedJoints) {
  try {
    final String variantName = variant + "Resource";
    Class<?> parentClass = Class.forName(dynamicResourceClass);
    // ... class header ...
    appendResourceConstructor(superclass, variantName);
    List<String> jointNames = new ArrayList<>();
    for (InstantiableTweedleNode joint : addedJoints) {
      final String jointIdentifier = getUserJointIdentifier(joint.toString());
      appendStaticField(null, "JointId", jointIdentifier,
          () -> joint.encodeDefinition(encoder));
      jointNames.add(jointIdentifier);
    }
    appendAddedJoints(superclass, jointNames);
    appendResourceInstance(variantName, "DEFAULT");
    encoder.appendClassFooter(dynamicResourceClass);
  } catch (ClassNotFoundException cnfe) {
    throw new RuntimeException("Unable to find class " + dynamicResourceClass
        + " ...", cnfe);
  }
}
```

**Key insight:** `getUserJointIdentifier` is now a method on `ResourceEncoder`
itself — it calls `TweedleEncoder.USER_PREFIX` (widened to package-private) to
prefix non-root joints with `"u_"`. The `getUserJointIdentifier` method on
`TweedleEncoder` delegates to `ResourceEncoder`:

```java
// TweedleEncoder.java
public String getUserJointIdentifier(String jointIdentifier) {
  return resourceEncoder.getUserJointIdentifier(jointIdentifier);
}
```

## Trace 3: An AST node callback (appendNewJointId)

Follow a `JointId` AST node encoding its definition through the encoder.

### Step 1: AST node calls encodeDefinition

When `appendResourceFields` processes a `JointId` field, it calls
`node.encodeDefinition(encoder)` where `encoder` is the `TweedleEncoder`
reference.

### Step 2: AST node calls public method on TweedleEncoder

The AST node's `encodeDefinition` implementation calls:

```java
encoder.appendNewJointId("LEFT_HAND", "BipedResource.LEFT_WRIST");
```

### Step 3: TweedleEncoder delegates to ResourceEncoder

```java
// TweedleEncoder.java
public void appendNewJointId(String joint, String parentReference) {
  resourceEncoder.appendNewJointId(joint, parentReference);
}
```

### Step 4: ResourceEncoder produces the Tweedle output

```java
// ResourceEncoder.java
void appendNewJointId(String joint, String parentReference) {
  encoder.appendInstantiation("JointId", () -> {
    encoder.appendArg("name", () -> encoder.quoteString(joint));
    encoder.appendAnotherArg("parent", parentReference);
  });
}
```

**Key insight:** `appendInstantiation`, `appendArg`, `appendAnotherArg`, and
`quoteString` are called directly on the `TweedleEncoder` reference without
`forward` prefixes. This is because these methods were widened from `private`
to package-private — they are methods defined directly on `TweedleEncoder`
(not inherited from `SourceCodeGenerator`), so Java accessibility rules allow
package-private access.

This is different from `forwardAppendString` and `forwardBracketize`, which are
bridges for `protected` methods *inherited* from `SourceCodeGenerator` and not
overridden on `TweedleEncoder`.

### Three categories of method access

| Category | Example | Why needed |
| --- | --- | --- |
| Bridge (`forward*`) | `forwardGetCodeStringBuilder`, `forwardBracketize`, `forwardAppendString` | `protected` methods inherited from `SourceCodeGenerator` and **not overridden** on `TweedleEncoder` — Java prevents same-package non-subclass access to the inherited declaration |
| Widened methods | `appendInstantiation`, `appendArg`, `appendIndent`, `tweedleTypeName` | Methods defined on `TweedleEncoder` itself — widened from `private` to package-private |
| Already accessible (`@Override protected`) | `openBlock`, `appendClassFooter`, `appendAssignmentOperator`, `appendSingleCodeLine`, `getListSeparator`, `appendStatementCompletion` | `@Override protected` methods declared on `TweedleEncoder` — accessible from same package |

## Trace 4: Pose and transformation encoding

Follow a `JointedModelPose` encoding path.

### Step 1: TweedleEncoder delegates

```java
// TweedleEncoder.java
public void appendNewPose(InstantiableTweedleNode[] jointTransformations) {
  resourceEncoder.appendNewPose(jointTransformations);
}
```

### Step 2: ResourceEncoder generates the pose structure

```java
// ResourceEncoder.java
void appendNewPose(InstantiableTweedleNode[] jointTransformations) {
  encoder.appendInstantiation("JointedModelPose", () -> {
    encoder.forwardAppendString("pairs: new JointIdTransformationPair[]");
    encoder.appendList(jointTransformations,
        (v) -> v.encodeDefinition(encoder), ",\n");
  });
}
```

Each `encodeDefinition(encoder)` call invokes
`TweedleEncoder.appendNewJointTransformation`, which delegates to
`ResourceEncoder.appendNewJointTransformation`:

```java
// ResourceEncoder.java
void appendNewJointTransformation(String jointId, AffineMatrix4x4 transformation) {
  encoder.forwardAppendString("        ");
  encoder.appendInstantiation("JointIdTransformationPair", () -> {
    encoder.appendArg("joint", jointId);
    encoder.appendAnotherArg("orientation", () -> {
      final UnitQuaternion q = transformation.orientation().asUnitQuaternion();
      encoder.appendInstantiation("Orientation", () -> {
        encoder.appendArg("x", Double.toString(q.x()));
        encoder.appendAnotherArg("y", Double.toString(q.y()));
        encoder.appendAnotherArg("z", Double.toString(q.z()));
        encoder.appendAnotherArg("w", Double.toString(q.w()));
      });
    });
    encoder.appendAnotherArg("position", () -> {
      final Tuple3 pos = transformation.translation();
      encoder.appendInstantiation("Position", () -> {
        encoder.appendArg("x", Double.toString(pos.x()));
        encoder.appendAnotherArg("y", Double.toString(pos.y()));
        encoder.appendAnotherArg("z", Double.toString(pos.z()));
      });
    });
  });
}
```

**Key insight:** `UnitQuaternion` and `Tuple3` are now imported by
`ResourceEncoder` instead of `TweedleEncoder`. The removed imports in
`TweedleEncoder` (`Tuple3`, `UnitQuaternion`, `java.lang.reflect.Field`,
`IdentifiableTweedleNode`) all moved to `ResourceEncoder` where they are
actually used.

## Summary of the delegation pattern

| TweedleEncoder stub | Calls on ResourceEncoder |
| --- | --- |
| `processResourceType(String)` (@Override) | `resourceEncoder.processResourceType(jointedModelResource)` |
| `processDynamicResource(String, String, InstantiableTweedleNode[])` (@Override) | `resourceEncoder.processDynamicResource(...)` |
| `getUserJointIdentifier(String)` (public) | `resourceEncoder.getUserJointIdentifier(jointIdentifier)` |
| `appendNewJointId(String, String)` (public) | `resourceEncoder.appendNewJointId(joint, parentReference)` |
| `appendNewJointArrayId(String, String)` (public) | `resourceEncoder.appendNewJointArrayId(pattern, startingJoint)` |
| `getFieldReference(String, String)` (public) | `resourceEncoder.getFieldReference(type, field)` |
| `appendNewPose(InstantiableTweedleNode[])` (public) | `resourceEncoder.appendNewPose(jointTransformations)` |
| `appendNewJointTransformation(String, AffineMatrix4x4)` (public) | `resourceEncoder.appendNewJointTransformation(jointId, transformation)` |

| ResourceEncoder method | Visibility | Calls back to |
| --- | --- | --- |
| `processResourceType` | package-private | `forwardGetCodeStringBuilder`, `encoder.openBlock()`, `encoder.appendClassFooter()`, plus private helpers |
| `processDynamicResource` | package-private | `getUserJointIdentifier`, `appendStaticField`, `appendAddedJoints`, `appendResourceInstance`, `encoder.appendClassFooter()` |
| `getUserJointIdentifier` | package-private | reads `TweedleEncoder.USER_PREFIX` |
| `appendResourceConstructor` | private | `encoder.appendIndent()`, `forwardAppendString`, `forwardBracketize` |
| `appendResourceInstances` | private | `appendResourceInstance` |
| `appendResourceInstance` | private | `forwardAppendNewLine`, `encoder.appendIndent()`, `forwardAppendString`, `forwardAppendSpace`, `encoder.appendAssignmentOperator()`, `encoder.appendInstantiation`, `encoder.appendStatementCompletion()` |
| `appendResourceFields` | private | `appendStaticField`, `appendAddedJoints`, `forwardAppendString`, `encoder.appendList`, `encoder.getListSeparator()` |
| `appendAddedJoints` | private | `forwardAppendNewLine`, `encoder.appendSingleCodeLine()`, `forwardAppendString`, `encoder.appendAssignmentOperator()`, `encoder.appendList`, `encoder.getListSeparator()`, `encoder.appendIndent()`, `forwardBracketize` |
| `appendStaticField` (×2) | private | `encoder.appendSingleCodeLine()`, `encoder.appendVisibilityTag`, `forwardAppendString`, `forwardAppendSpace`, `encoder.appendAssignmentOperator()` |
| `appendNewJointId` | package-private | `encoder.appendInstantiation`, `encoder.appendArg`, `encoder.appendAnotherArg`, `encoder.quoteString` |
| `appendNewJointArrayId` | package-private | `encoder.appendInstantiation`, `encoder.appendArg`, `encoder.appendAnotherArg`, `encoder.quoteString` |
| `getFieldReference` | package-private | `encoder.tweedleTypeName` |
| `appendNewPose` | package-private | `encoder.appendInstantiation`, `forwardAppendString`, `encoder.appendList` |
| `appendNewJointTransformation` | package-private | `forwardAppendString`, `encoder.appendInstantiation`, `encoder.appendArg`, `encoder.appendAnotherArg` |

## Exercises

1. **Trace AlienResource encoding.** Starting from
   `processResourceType("org.lgna.story.resources.biped.AlienResource")`, trace
   through `Class.forName`, the class header, `appendResourceConstructor` (which
   superclass branch?), `appendResourceFields`, and `appendResourceInstances`.
   Verify the output would produce a valid Tweedle class declaration.

2. **Follow the encodeDefinition callback chain.** In `appendResourceFields`,
   find the `node.encodeDefinition(encoder)` call. Confirm that `encoder` is
   `this.encoder` (the `TweedleEncoder` reference, not `this`). Then trace
   the AST node calling `encoder.appendNewJointId(...)` back to
   `ResourceEncoder.appendNewJointId(...)`.

3. **Compare bridge vs widened method access.** Find a call to
   `encoder.forwardAppendString(...)` and a call to
   `encoder.appendInstantiation(...)` in `ResourceEncoder`. Explain why one
   uses a bridge method and the other calls the method directly. (Answer:
   `appendString` is `protected` on `SourceCodeGenerator` — inherited and not
   overridden on `TweedleEncoder`. `appendInstantiation` is declared directly on
   `TweedleEncoder` — widened from `private` to package-private. Also note
   that `openBlock()` is `@Override protected` on `TweedleEncoder` and thus
   accessible directly — no bridge needed despite being originally declared
   on `SourceCodeGenerator`.)

4. **Verify superclass-specific constructor branches.** Open
   `ResourceEncoder.appendResourceConstructor` and list the three superclass
   patterns: `FlyerResource` (4 extra params), `QuadrupedResource` (1 extra
   param), `SlithererResource` (1 extra param). What output does the default
   case produce?

5. **Compare with decode side.** Open `FieldDecoder.java` and compare its
   constructor pattern (`FieldDecoder(Decoder, ExpressionDecoder)`) with
   `ResourceEncoder(TweedleEncoder)`. Note the symmetry: both delegates hold
   a back-reference to their coordinator. The decode-side resource handling
   is in `FieldDecoder` while the encode-side resource handling is in
   `ResourceEncoder` — the decomposition is symmetric but the responsibility
   split differs.
