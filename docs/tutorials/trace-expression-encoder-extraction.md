# Tutorial: Trace the ExpressionEncoder Extraction

A guided walkthrough showing how expression-encoding requests flow through
`TweedleEncoder` and its new `ExpressionEncoder` delegate. Use this to
understand the bridge and forwarding patterns before extending expression
encoding behavior.

## Prerequisites

- Familiarity with the visitor pattern used by `SourceCodeGenerator`
- Access to the `core/ast` source in `org.alice.serialization.tweedle`
- Recommended: read the [StatementEncoder extraction tutorial](./trace-statement-encoder-extraction.md)
  for the step 1 mirror pattern

## Overview

When an expression involving a target and member is encoded to Tweedle source,
the formatting passes through two classes:

```text
TweedleEncoder (@Override methods — visitor dispatch)
  └── ExpressionEncoder (extracted expression logic)
      ├── processInstantiation(InstanceCreation)       (added in #730)
      ├── getDeclaringJavaClassName(InstanceCreation)   (private, added in #730)
      ├── appendTargetAndMember(Expression, String, AbstractType)
      ├── targetIsMath(Expression)          (private helper)
      ├── tweedleModuleForMath(String, AbstractType) (private helper)
      └── processResourceExpression(ResourceExpression)
```

`TweedleEncoder` keeps the `@Override` stubs (required by `SourceCodeGenerator`)
and delegates the extracted logic to `ExpressionEncoder`. Forwarding methods
provide access to inherited methods that the delegate cannot (or should not)
call directly — two are `protected` and one is `public` but wrapped for
uniformity.

## Trace 1: A normal method invocation target

Follow a method call like `this.biped.turn(LEFT, 0.5)` where `biped` is
a field target (not a Math target).

### Step 1: Visitor dispatch

When `processMethodInvocation(MethodInvocation)` runs on `TweedleEncoder`,
it eventually calls `appendTargetAndMember(target, memberName, returnType)`.

### Step 2: TweedleEncoder @Override

```java
// TweedleEncoder.java
@Override
protected void appendTargetAndMember(Expression target, String member,
    AbstractType<?, ?, ?> returnType) {
  expressionEncoder.appendTargetAndMember(target, member, returnType);
}
```

The body is a one-line delegation. The `@Override` annotation stays on
`TweedleEncoder` because `SourceCodeGenerator` declares `appendTargetAndMember`
as a `protected` method, and Java requires the override on the subclass.

### Step 3: ExpressionEncoder logic

```java
// ExpressionEncoder.java
void appendTargetAndMember(Expression target, String member,
    AbstractType<?, ?, ?> returnType) {
  if (targetIsMath(target)) {
    encoder.forwardAppendString(tweedleModuleForMath(member, returnType));
  } else {
    encoder.forwardProcessExpression(target);
  }
  encoder.forwardAppendAccessSeparator();

  String tweedleName = TweedleEncoderData.membersToRename.get(member);
  encoder.forwardAppendString(tweedleName == null ? member : tweedleName);
}
```

Since `biped` is not a Math target, `targetIsMath` returns `false`, so the
delegate calls `forwardProcessExpression(target)` to encode the target
expression. Then it appends the access separator (`.`). Finally, it looks up
the member name in the `membersToRename` map — if no rename exists, the
original name is used.

### Step 4: Forwarding methods

```java
// TweedleEncoder.java (package-private forwarding)
void forwardProcessExpression(Expression expression) {
  processExpression(expression);
}

void forwardAppendAccessSeparator() {
  appendAccessSeparator();
}
```

These exist because `appendAccessSeparator` is a `protected` method inherited
from `SourceCodeGenerator` (in package `org.lgna.project.ast`). Java
accessibility rules prevent `ExpressionEncoder` (same package as
`TweedleEncoder`, but not a subclass of `SourceCodeGenerator`) from calling
`protected` methods directly. Note: `processExpression` is actually `public` on
`SourceCodeGenerator`, so `forwardProcessExpression` is technically unnecessary
— it is included for uniformity with the other forwarding methods.

**Key difference from StatementEncoder:** The StatementEncoder bridges call
`super.method()` to invoke the parent implementation that the override would
otherwise replace. The ExpressionEncoder forwarding methods simply call the
inherited method — there is no `super` vs `this` distinction because the
extracted methods fully implement the behavior rather than wrapping a parent
implementation.

## Trace 2: A Math.sin() invocation

Follow a call like `Math.sin(angle)` where the target is a `TypeExpression`
wrapping `java.lang.Math`.

### Step 1: appendTargetAndMember delegates

Same as Trace 1: `TweedleEncoder.appendTargetAndMember` delegates to
`ExpressionEncoder.appendTargetAndMember`.

### Step 2: targetIsMath returns true

```java
// ExpressionEncoder.java
private boolean targetIsMath(Expression target) {
  if (target instanceof TypeExpression expression) {
    AbstractType<?, ?, ?> innerType = expression.value.getValue();
    return innerType instanceof JavaType && "Math".equals(innerType.getName());
  }
  return false;
}
```

The target is a `TypeExpression` containing a `JavaType` named `"Math"`, so
`targetIsMath` returns `true`.

### Step 3: tweedleModuleForMath routes to $Angle

```java
// ExpressionEncoder.java
private String tweedleModuleForMath(String member, AbstractType<?, ?, ?> returnType) {
  if (returnType != null && "int".equals(returnType.getName())) {
    return "$WholeNumber";
  }
  if (TweedleEncoderData.angleMembers.contains(member)) {
    return "$Angle";
  }
  return "$DecimalNumber";
}
```

`sin` is in the `angleMembers` set (populated in `TweedleEncoderData`'s `static {}`
block), so the method returns `"$Angle"`.

**Key insight:** `angleMembers` lives on `TweedleEncoderData`, which is already
package-private. `ExpressionEncoder` reads it directly as
`TweedleEncoderData.angleMembers` — no bridge method is needed for static field
access within the same package.

### Step 4: Output produced

The delegate calls:
1. `encoder.forwardAppendString("$Angle")` — writes the Tweedle module name
2. `encoder.forwardAppendAccessSeparator()` — writes `.`
3. `encoder.forwardAppendString("sin")` — no rename exists for `sin`

Result: `$Angle.sin`

### Routing table

| Condition | Module |
| --- | --- |
| Return type is `int` | `$WholeNumber` |
| Member is in `angleMembers` (`sin`, `cos`, `tan`, `asin`, `acos`, `atan`, `atan2`, `PI`) | `$Angle` |
| Otherwise | `$DecimalNumber` |

## Trace 3: A resource expression

Follow a `ResourceExpression` through the encoder.

### Step 1: Visitor dispatch

When `processResourceExpression(ResourceExpression)` is called by the visitor
pattern on `TweedleEncoder`:

```java
// TweedleEncoder.java
@Override
public void processResourceExpression(ResourceExpression resourceExpression) {
  expressionEncoder.processResourceExpression(resourceExpression);
}
```

### Step 2: ExpressionEncoder encodes the resource name

```java
// ExpressionEncoder.java
void processResourceExpression(ResourceExpression resourceExpression) {
  encoder.forwardAppendEscapedString(resourceExpression.resource.getValue().getName());
}
```

The delegate retrieves the resource name and writes it as a quoted, escaped
string via the `forwardAppendEscapedString` bridge method.

### Step 3: Forwarding method

```java
// TweedleEncoder.java
void forwardAppendEscapedString(String value) {
  appendEscapedString(value);
}
```

`appendEscapedString` is a `protected` method on `SourceCodeGenerator` that
writes the value wrapped in quotes with proper escaping.

## Trace 4: Member rename (Math.round)

Follow `Math.rint(x)` — the Java `rint` method is renamed to `round` in
Tweedle.

### Step 1–2: Math target detected

Same as Trace 2. Since `rint` returns `double`, and `rint` is not in
`angleMembers`, `tweedleModuleForMath` returns `"$DecimalNumber"`.

### Step 3: Member rename lookup

```java
String tweedleName = TweedleEncoderData.membersToRename.get("rint");
// Returns "round" — populated in TweedleEncoderData static initializer
encoder.forwardAppendString("round");
```

Result: `$DecimalNumber.round`

**Key insight:** `membersToRename` lives on `TweedleEncoderData`, already package-private
so `ExpressionEncoder` can read it. The rename map is immutable after class
initialization and contains entries like `rint→round`, `ceil→ceiling`, etc.

## Trace 5: PersonResource instantiation (added in #730)

Follow a `new PersonResource(...)` creation through the encoder.

### Step 1: Visitor dispatch

When `processInstantiation(InstanceCreation)` is called by the visitor pattern:

```java
// TweedleEncoder.java
@Override
public void processInstantiation(InstanceCreation creation) {
  expressionEncoder.processInstantiation(creation);
}
```

### Step 2: ExpressionEncoder identifies PersonResource

```java
// ExpressionEncoder.java
void processInstantiation(InstanceCreation creation) {
  String className = getDeclaringJavaClassName(creation);
  if (className != null) {
    if (className.endsWith("PersonResource")) {
      ReleaseVirtualMachine vm = new ReleaseVirtualMachine();
      final Object summary = creation.evaluate(vm);
      if (summary != null) {
        encoder.appendInstantiation("PersonResource",
            () -> encoder.appendArg("name",
                () -> encoder.forwardAppendEscapedString("Person/" + summary)));
        return;
      }
    }
    // ... Double and DynamicResource cases ...
  }
  encoder.superProcessInstantiation(creation);
}
```

The delegate evaluates the creation expression via `ReleaseVirtualMachine` to
get the person summary string, then emits
`new PersonResource(name: "Person/...")`.

### Step 3: Super bridge fallback

If none of the special cases match, the delegate calls
`encoder.superProcessInstantiation(creation)`:

```java
// TweedleEncoder.java (package-private super bridge)
void superProcessInstantiation(InstanceCreation creation) {
  super.processInstantiation(creation);
}
```

This reaches `SourceCodeGenerator.processInstantiation`, which encodes the
instantiation using the default visitor pattern. The bridge is required because
Java's `super.method()` can only appear in the declaring subclass.

**Key difference from forwarding methods:** `superProcessInstantiation` calls
`super.processInstantiation()` (the *parent* implementation), not
`this.processInstantiation()`. This is the same `super` bridge pattern used
by `StatementEncoder`'s `superAppendStatementCompletion`.

## Trace 6: Double boxing instantiation (added in #730)

Follow `new Double(42)` through the encoder.

### Step 1: processInstantiation delegates

Same as Trace 5 Step 1.

### Step 2: Double case detected

```java
if (className.equals("Double")) {
  final ArrayList<SimpleArgument> requiredArgs = creation.requiredArguments.getValue();
  if (requiredArgs.size() == 1) {
    encoder.forwardAppendString("$DecimalNumber.from");
    Expression arg = requiredArgs.getFirst().expression.getValue();
    encoder.forwardParenthesize(
        () -> encoder.appendArg("wholeNumber", () -> arg.process(encoder)));
    return;
  }
}
```

The output is `$DecimalNumber.from(wholeNumber: 42)` — wrapping the Java
`Double` constructor into Tweedle's `$DecimalNumber.from` factory method.

## Summary of the delegation pattern

| TweedleEncoder @Override | Calls on ExpressionEncoder |
| --- | --- |
| `processInstantiation(InstanceCreation)` | `expressionEncoder.processInstantiation(creation)` |
| `appendTargetAndMember(Expression, String, AbstractType)` | `expressionEncoder.appendTargetAndMember(target, member, returnType)` |
| `processResourceExpression(ResourceExpression)` | `expressionEncoder.processResourceExpression(resourceExpression)` |

| ExpressionEncoder method | Visibility | Calls back to |
| --- | --- | --- |
| `processInstantiation` | package-private | `superProcessInstantiation` (fallback), `forwardAppendString`, `forwardAppendEscapedString` |
| `getDeclaringJavaClassName` | private | (pure logic, no callbacks) |
| `appendTargetAndMember` | package-private | `forwardProcessExpression`, `forwardAppendAccessSeparator`, `forwardAppendString` |
| `targetIsMath` | private | (pure logic, no callbacks) |
| `tweedleModuleForMath` | private | reads `TweedleEncoderData.angleMembers` |
| `processResourceExpression` | package-private | `forwardAppendEscapedString` |

## Exercises

1. **Trace Math.round manually.** Starting from `appendTargetAndMember` with
   target `TypeExpression(Math)` and member `"rint"`, trace through
   `targetIsMath`, `tweedleModuleForMath`, and the member rename to confirm the
   output is `$DecimalNumber.round`.

2. **Compare bridge types.** `ExpressionEncoder` uses both `super` bridges
   (`superProcessInstantiation`) and `forward` bridges
   (`forwardProcessExpression`). When is each type needed? (Answer: `super`
   bridges invoke the parent class implementation that the `@Override` would
   otherwise shadow. `forward` bridges provide access to inherited utility
   methods that the delegate calls as-is.)

3. **Verify static map access.** Open `ExpressionEncoder.java` and confirm that
   `angleMembers` and `membersToRename` are accessed as
   `TweedleEncoderData.angleMembers` and `TweedleEncoderData.membersToRename`.

4. **Compare with decode side.** Open `ExpressionDecoder.java` and compare its
   constructor pattern with `ExpressionEncoder(TweedleEncoder)`. Note the
   symmetry: both delegates hold a back-reference to their coordinator.

5. **Trace DynamicResource instantiation.** Starting from
   `processInstantiation` with a `InstanceCreation` wrapping a `DynamicSphereResource`
   constructor with two arguments where the second is a `StringLiteral("DEFAULT")`,
   trace through the delegate to confirm the output is `SphereResource.DEFAULT`.

6. **Compare with ArgumentEncoder.** Open the
   [ArgumentEncoder tutorial](./trace-argument-encoder-extraction.md) and note
   that `ArgumentEncoder` uses only `forward` bridges (no `super` bridges),
   while `ExpressionEncoder` uses both. This is because extracted argument
   methods fully implement behavior, while `processInstantiation` has a
   `super` fallback path.
