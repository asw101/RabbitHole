# Tutorial: Trace the ArgumentEncoder Extraction

A guided walkthrough showing how argument-encoding requests flow through
`TweedleEncoder` and its new `ArgumentEncoder` delegate. Use this to
understand the parameter-label resolution chain and argument-wrapping pattern
before extending argument encoding behavior.

## Prerequisites

- Familiarity with the visitor pattern used by `SourceCodeGenerator`
- Access to the `core/ast` source in `org.alice.serialization.tweedle`
- Recommended: read the [ExpressionEncoder extraction tutorial](./trace-expression-encoder-extraction.md)
  for the companion extraction in the same issue

## Overview

When a method invocation's arguments are encoded to Tweedle source, the
formatting passes through two classes:

```text
TweedleEncoder (@Override methods — visitor dispatch)
  └── ArgumentEncoder (extracted argument logic)
      ├── appendArgument(JavaKeyedArgument)
      ├── processKeyedArgument(JavaKeyedArgument)
      ├── appendOneArgument(MethodInvocation)    (private helper)
      ├── processArgument(AbstractParameter, AbstractArgument)
      ├── appendWrappedArg(ProcessableNode, String, Map)  (private helper)
      ├── getParameterLabel(AbstractParameter)    (private helper)
      └── parameterIndex(JavaMethodParameter)     (private helper)
```

`TweedleEncoder` keeps the `@Override` stubs (required by `SourceCodeGenerator`)
and delegates the extracted logic to `ArgumentEncoder`. One new forwarding
method (`forwardIdentifierName`) provides access to the `protected`
`identifierName` method.

## Trace 1: A labeled argument (normal path)

Follow a method call like `this.biped.turn(direction: LEFT, amount: 0.5)` where
the arguments have resolvable parameter labels.

### Step 1: Visitor dispatch

When `processMethodInvocation(MethodInvocation)` runs on `TweedleEncoder`,
it calls `appendEachArgument(invocation)` which iterates over required
arguments and calls `processArgument(parameter, argument)` for each one.

### Step 2: TweedleEncoder @Override

```java
// TweedleEncoder.java
@Override
public void processArgument(AbstractParameter parameter, AbstractArgument argument) {
  argumentEncoder.processArgument(parameter, argument);
}
```

The body is a one-line delegation. The `@Override` annotation stays on
`TweedleEncoder` because `SourceCodeGenerator` declares `processArgument`.

### Step 3: ArgumentEncoder resolves the label

```java
// ArgumentEncoder.java
void processArgument(AbstractParameter parameter, AbstractArgument argument) {
  final String parameterLabel = getParameterLabel(parameter);
  encoder.forwardAppendString(parameterLabel);
  encoder.forwardAppendString(": ");
  final Code parameterCode = parameter.getCode();
  Map<String, String> wrappedParams = parameterCode == null
      ? null
      : TweedleEncoderData.methodsWithWrappedArgs.get(parameterCode.getName());
  appendWrappedArg(argument, parameterLabel, wrappedParams);
}
```

First, it calls `getParameterLabel` to resolve the argument name. Then it
appends `label: ` followed by the argument value (possibly wrapped).

### Step 4: Parameter label resolution chain

```java
// ArgumentEncoder.java
private String getParameterLabel(AbstractParameter parameter) {
  // 1. Constructor relabeling
  if (parameter instanceof JavaConstructorParameter constructorParameter) {
    String className = constructorParameter.getCode().getDeclaringType().getName();
    Map<String, String> paramLabelMap =
        TweedleEncoderData.constructorsWithRelabeledParams.get(className);
    if (paramLabelMap != null) {
      final String newLabel = paramLabelMap.get(parameter.getName());
      if (newLabel != null) {
        return newLabel;
      }
    }
  }

  // 2. identifierName via bridge
  String label = encoder.forwardIdentifierName(parameter);
  if (null != label) {
    return TweedleEncoderData.methodParamsToRelabel.getOrDefault(label, label);
  }

  // 3. Missing parameter name table
  if (parameter instanceof JavaMethodParameter methodParameter) {
    final String methodName = parameter.getCode().getName();
    if (TweedleEncoderData.methodsMissingParameterNames.containsKey(methodName)) {
      String[] paramNames = TweedleEncoderData.methodsMissingParameterNames.get(methodName);
      int i = parameterIndex(methodParameter);
      return paramNames[i];
    }
  }

  // 4. Constructor fallback for Double
  if (parameter instanceof JavaConstructorParameter) {
    String javaType = parameter.getCode().getDeclaringType().getName();
    if ("Double".equals(javaType)) {
      return "wholeNumber";
    }
  }

  // 5. Type-name fallback with error
  final String paramType = parameter.getValueType().getName().toLowerCase();
  Dialogs.showError("Unlabeled parameter", ...);
  return paramType;
}
```

**Key insight:** Step 2 uses `encoder.forwardIdentifierName(parameter)` — the
bridge method on `TweedleEncoder` that calls the overridden `identifierName`.
This is necessary because `identifierName` is `protected` on
`SourceCodeGenerator` and overridden on `TweedleEncoder` to add the `u_` user
prefix. `ArgumentEncoder` cannot call it directly.

### Step 5: Argument wrapping

```java
// ArgumentEncoder.java
private void appendWrappedArg(ProcessableNode argument, String parameterLabel,
    Map<String, String> wrappedParams) {
  String argStart = wrappedParams == null ? null : wrappedParams.get(parameterLabel);
  if (argStart != null) {
    encoder.forwardAppendString(argStart);
  }
  argument.process(encoder);
  if (argStart != null) {
    encoder.forwardAppendString(")");
  }
}
```

If the parameter has a wrapping prefix (e.g., `new Duration(seconds: ` for
duration arguments), it is prepended. The argument value is encoded via
`argument.process(encoder)` — note this passes the `TweedleEncoder` reference,
not `this`, so the visitor pattern dispatches to the correct `@Override` methods
on `TweedleEncoder`.

## Trace 2: A keyed argument (keyword factory)

Follow a keyed argument like `setPaint(LEFT, BLUE)` where `LEFT` is detected
as a keyword factory type method result.

### Step 1: appendArgument delegates

```java
// TweedleEncoder.java
@Override
protected void appendArgument(JavaKeyedArgument arg) {
  argumentEncoder.appendArgument(arg);
}
```

### Step 2: ArgumentEncoder routes to processKeyedArgument

```java
// ArgumentEncoder.java
void appendArgument(JavaKeyedArgument arg) {
  processKeyedArgument(arg);
}
```

### Step 3: Keyed argument dispatch

```java
// ArgumentEncoder.java
void processKeyedArgument(JavaKeyedArgument arg) {
  Expression expressionValue = arg.expression.getValue();
  if (expressionValue instanceof MethodInvocation methodInvocation) {
    AbstractMethod method = methodInvocation.method.getValue();
    AbstractType<?, ?, ?> factoryType =
        AstTypeResolutionHelpers.getKeywordFactoryType(arg);
    if (factoryType != null) {
      final String label = method.getName();
      encoder.forwardAppendString(
          TweedleEncoderData.methodParamsToRelabel.getOrDefault(label, label));
      encoder.forwardAppendString(": ");
      appendOneArgument(methodInvocation);
      return;
    }
  }
  encoder.forwardProcessExpression(expressionValue);
}
```

If the argument expression is a `MethodInvocation` with a detectable keyword
factory type, the method name becomes the label and the single argument is
extracted. Otherwise, the expression is encoded directly via
`forwardProcessExpression`.

### Step 4: Single argument extraction

```java
// ArgumentEncoder.java
private void appendOneArgument(MethodInvocation argumentOwner) {
  if (!argumentOwner.getVariableArgumentsProperty().isEmpty()
      || !argumentOwner.getKeyedArgumentsProperty().isEmpty()
      || argumentOwner.getRequiredArgumentsProperty().size() != 1) {
    Logger.errln("Expected a single argument.", argumentOwner);
  }
  if (!argumentOwner.getRequiredArgumentsProperty().isEmpty()) {
    final String methodName = argumentOwner.method.getValue().getName();
    Map<String, String> wrappedParams =
        TweedleEncoderData.optionalParamsToWrap.containsKey(methodName)
            ? TweedleEncoderData.optionalParamsToWrap : null;
    appendWrappedArg(argumentOwner.getRequiredArgumentsProperty().get(0),
        methodName, wrappedParams);
  }
}
```

The method validates that the invocation has exactly one required argument (logs
an error if not), then extracts and wraps that single argument.

## Trace 3: An unlabeled parameter (error path)

When a parameter has no resolvable label through any of the five resolution
steps in `getParameterLabel`:

1. Not a constructor with relabeled params
2. `identifierName` returns `null`
3. Not in `methodsMissingParameterNames`
4. Not a `Double` constructor
5. Falls through to type-name fallback

```java
final String paramType = parameter.getValueType().getName().toLowerCase();
final String message = "Unable to read label from parameter on method: %s\n..."
    .formatted(parameter.getCode().toString(), paramType);
Dialogs.showError("Unlabeled parameter", message);
Logger.errln(message);
return paramType;
```

The type name is used as the label, and an error dialog is shown. This
preserves the exact error behavior from before the extraction.

## Summary of the delegation pattern

| TweedleEncoder @Override | Calls on ArgumentEncoder |
| --- | --- |
| `appendArgument(JavaKeyedArgument)` | `argumentEncoder.appendArgument(arg)` |
| `processKeyedArgument(JavaKeyedArgument)` | `argumentEncoder.processKeyedArgument(arg)` |
| `processArgument(AbstractParameter, AbstractArgument)` | `argumentEncoder.processArgument(parameter, argument)` |

| ArgumentEncoder method | Visibility | Calls back to |
| --- | --- | --- |
| `appendArgument` | package-private | `processKeyedArgument` |
| `processKeyedArgument` | package-private | `forwardAppendString`, `forwardProcessExpression`, `appendOneArgument` |
| `appendOneArgument` | private | `appendWrappedArg` |
| `processArgument` | package-private | `forwardAppendString`, `getParameterLabel`, `appendWrappedArg` |
| `appendWrappedArg` | private | `forwardAppendString`, `argument.process(encoder)` |
| `getParameterLabel` | private | `forwardIdentifierName`, `parameterIndex`, static data maps |
| `parameterIndex` | private | (pure logic, no callbacks) |

## Exercises

1. **Trace a Duration-wrapped argument.** Starting from `processArgument` with
   a method named `turn` and a parameter `duration`, trace through
   `appendWrappedArg` to confirm the output includes
   `duration: new Duration(seconds: ...)`.

2. **Compare visitor dispatch.** In `appendWrappedArg`,
   `argument.process(encoder)` passes the `TweedleEncoder` reference. What
   would happen if it passed `this` instead? (Answer: `ArgumentEncoder` does
   not implement the `AstProcessor` visitor interface, so `process(this)` would
   fail to compile.)

3. **Verify identifierName bridge.** Open `TweedleEncoder.java` and confirm
   that `forwardIdentifierName(AbstractDeclaration)` calls `identifierName`
   (the overridden version, not `super`). Then open `ArgumentEncoder.java` and
   confirm it is accessed as `encoder.forwardIdentifierName(parameter)`.

4. **Compare with StatementEncoder.** `StatementEncoder` needs `super` bridges
   (`superAppendStatementCompletion`). `ArgumentEncoder` does not — it only
   uses `forward` bridges. Why? (Answer: The extracted argument methods fully
   implement behavior; they do not wrap a parent implementation that needs
   `super` invocation.)
