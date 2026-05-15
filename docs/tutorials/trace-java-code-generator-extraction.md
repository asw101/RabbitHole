# Tutorial: Trace the JavaCodeGenerator Delegate Extraction

A guided walkthrough showing how import collection, comment formatting, and
concurrency emission flow through `JavaCodeGenerator` and its three new
delegates. Use this to understand the delegation patterns before extending Java
code generation behavior.

## Prerequisites

- Familiarity with the visitor pattern used by `SourceCodeGenerator`
- Access to the `core/ast` source in `org.lgna.project.ast`
- Recommended: read the
  [encoder delegate decomposition tutorial](./trace-encoder-delegate-decomposition.md)
  for the analogous Tweedle pattern

## Overview

When a `NamedUserType` is processed into Java source, formatting passes through
four classes:

```text
JavaCodeGenerator (@Override methods — visitor dispatch)
├── JavaImportCollector (import tracking + block rendering)
│   ├── trackType(JavaType, List<JavaPackage>)
│   ├── trackStaticMethod(JavaMethod)
│   └── buildImports(String prefix, String postfix)
├── JavaCommentFormatter (localized comment resolution + block formatting)
│   ├── formatBlockComment(String)
│   ├── getLocalizedComment(AbstractType, String, Locale)
│   ├── getLocalizedMultiLineComment(AbstractType, String)
│   ├── appendMemberPrefix(AbstractMember, StringBuilder)
│   └── appendMemberPostfix(AbstractMember, StringBuilder)
└── JavaConcurrencyEmitter (concurrent statement emission)
    ├── processDoTogether(DoTogether, JavaCodeGenerator)
    └── processEachInTogether(AbstractEachInTogether, JavaCodeGenerator)
```

`JavaCodeGenerator` keeps all `@Override` stubs (required by
`SourceCodeGenerator`) and delegates extracted logic to the appropriate helper.

## Trace 1: Import collection during type name processing

Follow a `processTypeName(javaType)` call for `org.lgna.story.SBiped`.

### Step 1: Visitor dispatch

When `SourceCodeGenerator` encounters a type reference in any method signature,
field declaration, or expression, it calls `processTypeName(type)`. The
`@Override` lives on `JavaCodeGenerator`.

### Step 2: JavaCodeGenerator override

```java
// JavaCodeGenerator.java
@Override
public void processTypeName(AbstractType<?, ?, ?> type) {
  if (type instanceof JavaType javaType) {
    if (!javaType.isPrimitive()) {
      importCollector.trackType(javaType, packagesMarkedForOnDemandImport);
    }
  }
  appendString(type == null ? "MISSING_TYPE" : type.getName());
}
```

The `@Override` still emits the type name to the code buffer, but the import
tracking side-effect delegates to `importCollector`.

### Step 3: JavaImportCollector logic

```java
// JavaImportCollector.java
void trackType(JavaType javaType, List<JavaPackage> markedPackages) {
  JavaPackage javaPackage = javaType.getPackage();
  if (javaPackage != null) {
    JavaType enclosingType = javaType.getEnclosingType();
    if (enclosingType != null || !markedPackages.contains(javaPackage)) {
      typesToImport.add(javaType);
    } else {
      packagesToImportOnDemand.add(javaPackage);
    }
  }
}
```

The logic is unchanged from the original inline code. The three tracking sets
(`packagesToImportOnDemand`, `typesToImport`, `methodsToImportStatic`) now live
on `JavaImportCollector` rather than `JavaCodeGenerator`.

### Step 4: Import block rendering

After all types are processed, `processClass` calls:

```java
// JavaCodeGenerator.java
@Override
public void processClass(CodeOrganizer codeOrganizer, NamedUserType userType) {
  super.processClass(codeOrganizer, userType);
  getCodeStringBuilder().insert(0,
      importCollector.buildImports(getImportsPrefix(), getImportsPostfix()));
}
```

`getImportsPrefix()` and `getImportsPostfix()` remain `protected` on
`JavaCodeGenerator` so that `NetbeansJavaCodeGenerator` can override them to add
editor-fold markers.

## Trace 2: Localized comment on a method

Follow `processMethod(userMethod)` for a method with a localized comment
configured via the resource bundle.

### Step 1: JavaCodeGenerator override

```java
// JavaCodeGenerator.java
@Override
public void processMethod(UserMethod method) {
  commentFormatter.appendMemberPrefix(method, getCodeStringBuilder());
  super.processMethod(method);
  commentFormatter.appendMemberPostfix(method, getCodeStringBuilder());
}
```

### Step 2: JavaCommentFormatter prefix

```java
// JavaCommentFormatter.java
void appendMemberPrefix(AbstractMember member, StringBuilder sb) {
  String comment = getLocalizedMultiLineComment(
      member.getDeclaringType(), member.getName());
  if (comment != null) {
    sb.append("\n").append(comment).append("\n");
  }
}
```

### Step 3: Comment resolution

```java
// JavaCommentFormatter.java
String getLocalizedMultiLineComment(AbstractType<?, ?, ?> type,
    String sectionName) {
  String comment = getLocalizedComment(type, sectionName, Locale.getDefault());
  if (comment != null) {
    comment = formatBlockComment(comment);
  }
  return comment;
}
```

`getLocalizedComment` walks up the type hierarchy looking for a matching key
in the `ResourceBundle`. `formatBlockComment` wraps the resolved text in
`/* ... */` syntax using `SourceCodeGenerator.splitIntoLines()`.

### Step 4: NetBeans compatibility

`NetbeansJavaCodeGenerator.appendSectionPrefix` calls
`getLocalizedMultiLineComment(declaringType, sectionName)`. This method is
`protected` on `JavaCodeGenerator` and delegates to `commentFormatter`:

```java
// JavaCodeGenerator.java
protected String getLocalizedMultiLineComment(AbstractType<?, ?, ?> type,
    String sectionName) {
  return commentFormatter.getLocalizedMultiLineComment(type, sectionName);
}
```

The NetBeans subclass sees the same `protected` method it always had.

## Trace 3: Do-together concurrency emission

Follow `processDoTogether(doTogether)` for a `DoTogether` block containing
two statements.

### Step 1: Visitor dispatch

`SourceCodeGenerator` calls `processDoTogether(doTogether)` on
`JavaCodeGenerator`.

### Step 2: JavaCodeGenerator override

```java
// JavaCodeGenerator.java
@Override
public void processDoTogether(DoTogether doTogether) {
  concurrencyEmitter.processDoTogether(doTogether, this);
}
```

One-line delegation. The `@Override` stays on `JavaCodeGenerator` because
`SourceCodeGenerator` requires it.

### Step 3: JavaConcurrencyEmitter logic

```java
// JavaConcurrencyEmitter.java
void processDoTogether(DoTogether doTogether, JavaCodeGenerator generator) {
  JavaType threadUtilitiesType = JavaType.getInstance(ThreadUtilities.class);
  JavaMethod doTogetherMethod = threadUtilitiesType
      .getDeclaredMethod("doTogether", Runnable[].class);
  TypeExpression target = new TypeExpression(threadUtilitiesType);
  generator.appendTargetAndMethodName(target, doTogetherMethod);
  generator.appendString("(");
  // ... lambda/anonymous wrapping for each statement ...
  generator.appendString(");");
}
```

The emitter calls back to `generator` for all output methods. This is the same
pattern used by `ExpressionCodeEmitter` and `StatementCodeEmitter` in
`SourceCodeGenerator`. The `isLambdaSupported()` check determines whether to
emit `()->{ ... }` or `new Runnable(){ public void run(){ ... }}`.

### Step 4: Nested DoInOrder handling

If a statement inside `DoTogether` is a `DoInOrder`, the emitter unwraps it
and emits each sub-statement. This preserves the original behavior where
`doTogether` flattens `doInOrder` blocks into individual `Runnable` bodies.

## Key design decisions

| Decision | Rationale |
| --- | --- |
| Import sets live on `JavaImportCollector`, not `JavaCodeGenerator` | Clean ownership — the collector manages its own state |
| `commentsLocalizationBundleName` stays on `JavaCodeGenerator` | Needed by the `Builder`; passed to `JavaCommentFormatter` at construction |
| `processDoInOrder` stays on `JavaCodeGenerator` | Only 16 lines — extraction overhead exceeds the benefit |
| `JavaConcurrencyEmitter` takes `JavaCodeGenerator` parameter, not constructor | Avoids circular reference complexity; methods are stateless |
| `buildImports` takes prefix/postfix as parameters | Allows `NetbeansJavaCodeGenerator` to inject editor-fold markers without knowing about the delegate |

## Before / after comparison

| Metric | Before | After |
| --- | --- | --- |
| `JavaCodeGenerator.java` lines | 643 | ~475 |
| Import tracking fields on `JavaCodeGenerator` | 3 `Set<...>` | 0 (moved to `JavaImportCollector`) |
| `getImports()` method on `JavaCodeGenerator` | 34 lines | 0 (moved to `JavaImportCollector.buildImports()`) |
| `formatBlockComment` + `getLocalizedComment` + helpers | ~70 lines | 0 (moved to `JavaCommentFormatter`) |
| `processDoTogether` + `processEachInTogether` | ~75 lines | 2 one-line delegations |
| New files | 0 | 3 (`JavaImportCollector`, `JavaCommentFormatter`, `JavaConcurrencyEmitter`) |
| Public API methods | Same | Same |
| NetBeans changes | — | None |
