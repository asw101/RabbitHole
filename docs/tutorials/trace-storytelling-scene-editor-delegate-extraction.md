# Tutorial: Trace the StorytellingSceneEditor Delegate Extraction

This tutorial walks through two representative call paths after the extraction
of `SceneEditorFieldManager` and `SceneEditorCameraHelper` from
`StorytellingSceneEditor`. You will trace a field-management operation and a
camera helper call to understand how delegation works.

## Prerequisites

- Familiarity with the [StorytellingSceneEditor Characterization](../reference/storytelling-scene-editor-characterization.md)
- The [delegate extraction reference](../reference/storytelling-scene-editor-delegate-extraction.md)
- A checkout with the extraction applied

## Path 1: Adding a field to the scene

When a user drags a model from the gallery onto the 3D scene, the IDE calls
`getDoStatementsForAddField` to generate the setup statements.

### Step 1: The override on StorytellingSceneEditor

Open `StorytellingSceneEditor.java` and find `getDoStatementsForAddField`:

```java
@Override
public Statement[] getDoStatementsForAddField(UserField field, AffineMatrix4x4 initialTransform) {
  return fieldManager.getDoStatementsForAddField(field, initialTransform);
}
```

The `@Override` stays on `StorytellingSceneEditor` because `AbstractSceneEditor`
declares this as an abstract method. The body is a single forwarding call.

### Step 2: The implementation on SceneEditorFieldManager

Open `SceneEditorFieldManager.java` and find `getDoStatementsForAddField`:

```java
Statement[] getDoStatementsForAddField(UserField field, AffineMatrix4x4 initialTransform) {
  if ((initialTransform == null) && field.getValueType().isAssignableTo(SModel.class)) {
    // ... compute default transform from bounding box ...
    initialTransform = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, location);
  }
  return SetUpMethodGenerator.getSetupStatementsForField(
      false, field, editor.getActiveSceneInstance(), null, initialTransform);
}
```

Key observations:
- The method accesses `editor.getActiveSceneInstance()` via the stored
  `AbstractSceneEditor` reference.
- The method calls `SetUpMethodGenerator.getSetupStatementsForField` — the
  same static utility as before.
- The logic is identical to the original code that lived inside SSE.

### Step 3: Verify with reflection

The characterization test `api_getDoStatementsForAddField` uses:

```java
assertPublicMethod("getDoStatementsForAddField",
    resolve("org.lgna.project.ast.UserField"),
    resolve("org.alice.math.immutable.AffineMatrix4x4"));
```

This passes because the forwarding stub on `StorytellingSceneEditor`
preserves the exact method name, parameter types, and `public` modifier.

## Path 2: Getting the marker for a field

When the IDE needs to find the `MarkerImp` associated with a scene field,
it calls `getMarkerForField`.

### Step 1: The forwarding stub on StorytellingSceneEditor

```java
public MarkerImp getMarkerForField(UserField field) {
  return SceneEditorCameraHelper.getMarkerForField(this.getInstanceInJavaVMForField(field));
}
```

Note: SSE resolves the field to its Java VM instance *before* calling the
helper. The camera helper receives the already-resolved object instance.

### Step 2: The static helper on SceneEditorCameraHelper

```java
static MarkerImp getMarkerForField(Object fieldInstance) {
  if (fieldInstance instanceof SMarker marker) {
    return marker.getImplementation();
  }
  return null;
}
```

This is a pure function: no state, no back-reference. It takes the object
instance and returns the `MarkerImp` if it is an `SMarker`, or `null` otherwise.

### Step 3: Why static works here

All six camera/marker methods are pure functions of their inputs:

| Method | Inputs | State needed |
| --- | --- | --- |
| `getTransformForNewCameraMarker` | `TransformableImp cameraImp` | None |
| `getTransformForNewObjectMarker` | `EntityImp selectedImp` | None |
| `getColorForNewObjectMarker` | (none) | Calls `MarkerUtilities` static |
| `getColorForNewCameraMarker` | (none) | Calls `MarkerUtilities` static |
| `getGoodPointOfViewInSceneForObject` | `AxisAlignedBox box` | None |
| `getMarkerForField` | `Object fieldInstance` | None |

None of these methods access `StorytellingSceneEditor` instance fields. The
SSE forwarding stubs resolve any instance-dependent data (like `this.getInstanceInJavaVMForField(field)` or `this.movableSceneCameraImp`)
and pass it as a parameter.

## Path 3: Shared vehicle call detection

The `asSetVehicleCall` method is shared between three callers:

1. `SceneEditorFieldManager.getRiders()` — iterates setup statements looking
   for `setVehicle` calls to identify which fields ride on a given vehicle.
2. `StorytellingSceneEditor.useSceneAsVehicleForDisconnectedModels()` — finds
   existing `setVehicle` statements to update when re-parenting disconnected
   models.
3. `StorytellingSceneEditor.getCurrentStateCodeForField()` — strips
   `setVehicle` calls from the generated state-code block before wrapping it
   in a `DoTogether`.

All three call `SceneEditorFieldManager.asSetVehicleCall(statement)` as a
static package-private method:

```java
// In SceneEditorFieldManager
static MethodInvocation asSetVehicleCall(Statement statement) {
  if (statement instanceof ExpressionStatement expressionStatement) {
    Expression expression = expressionStatement.expression.getValue();
    if (expression instanceof MethodInvocation mi) {
      AbstractMethod method = mi.method.getValue();
      if (method.getName().equalsIgnoreCase("setVehicle")) {
        return mi;
      }
    }
  }
  return null;
}

// In StorytellingSceneEditor — uses the static method
MethodInvocation setVehicleCall = SceneEditorFieldManager.asSetVehicleCall(statement);
```

The original SSE had two related private methods — `asSetVehicleCall` and
`isSetVehicleInvocation`. The extraction unified them into one: callers that
only need a boolean check use `asSetVehicleCall(s) != null`.

## What to look for in review

1. **Forwarding stubs are trivially correct.** Each SSE stub is a single
   `return` statement. No logic transformation occurs.
2. **No circular dependencies.** `SceneEditorFieldManager` depends on
   `AbstractSceneEditor` (the parent class), not `StorytellingSceneEditor`
   (the singleton). `SceneEditorCameraHelper` depends on nothing in the
   scene editor package.
3. **The `@Override` stays on SSE.** Java requires the override annotation on
   the class that declares the method in the type hierarchy. Moving it to the
   delegate would be a compile error.
4. **Parameter decomposition at the boundary.** Camera helpers receive
   primitive data (transforms, boxes, imps) rather than the full SSE instance.
   This is intentional — it makes the helpers testable and stateless.
