# JavaCodeGenerator Delegate Extraction

This reference describes the extraction of import management, comment
formatting, and concurrency emission from the 643-line `JavaCodeGenerator` into
three package-private delegate classes: `JavaImportCollector`,
`JavaCommentFormatter`, and `JavaConcurrencyEmitter`.

The extraction is a pure internal refactor. The public API surface —
`JavaCodeGenerator` and its `Builder` — is unchanged. All existing generated
Java source output, import blocks, localized comments, and concurrent statement
emission are preserved identically. `NetbeansJavaCodeGenerator` continues to
compile and override without modification.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Class responsibilities](#class-responsibilities)
  - [JavaCodeGenerator (coordinator)](#javacodegenerator-coordinator)
  - [JavaImportCollector](#javaimportcollector)
  - [JavaCommentFormatter](#javacommentformatter)
  - [JavaConcurrencyEmitter](#javaconcurrencyemitter)
- [Public API](#public-api)
- [Package-private collaboration](#package-private-collaboration)
- [Bridge methods](#bridge-methods)
- [NetbeansJavaCodeGenerator compatibility](#netbeansjavacodegenerator-compatibility)
- [Security boundary](#security-boundary)
- [Error handling contract](#error-handling-contract)
- [Configuration](#configuration)
- [Validation](#validation)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

The original `JavaCodeGenerator.java` contained 643 lines mixing four distinct
concerns: Java import tracking and rendering (~60 lines), localized block
comment formatting and member prefix/postfix emission (~70 lines), concurrent
statement emission for `doTogether` and `eachInTogether` (~75 lines), and the
core code generation visitor overrides.

RabbitHole issue #649 decomposes the generator into focused delegates, mirroring
the [Encoder delegate decomposition](./encoder-delegate-decomposition.md)
pattern applied to `TweedleEncoder`. Each delegate is small enough to understand
in isolation and extend independently. The result brings `JavaCodeGenerator`
under 500 lines (~475).

## Architecture

```text
SourceCodeGenerator (abstract base — unchanged)
└── JavaCodeGenerator (coordinator, ≤ 475 lines)
    ├── JavaImportCollector (package-private, ~65 lines)
    │   └── On-demand package, explicit type, and static method import tracking;
    │       import block rendering with prefix/postfix support
    ├── JavaCommentFormatter (package-private, ~70 lines)
    │   └── Block comment formatting, localized comment resolution,
    │       member prefix/postfix emission
    └── JavaConcurrencyEmitter (package-private, ~75 lines)
        └── processDoTogether, processEachInTogether emission
            with lambda/anonymous-class dual paths
```

All four classes live in `org.lgna.project.ast`. The delegates are
package-private with no public constructors. They are instantiated only by
`JavaCodeGenerator` and receive a back-reference to it (or specific parameters)
for shared services.

## Class responsibilities

### JavaCodeGenerator (coordinator)

| Responsibility | Methods |
| --- | --- |
| Construction | `JavaCodeGenerator(Builder)` — creates all three delegates |
| Builder | `Builder` inner class — unchanged public API |
| Class processing | `processClass(CodeOrganizer, NamedUserType)` — calls `importCollector.buildImports(prefix, postfix)` |
| Import prefix/postfix | `getImportsPrefix()`, `getImportsPostfix()` — protected, overridable by subclasses |
| Type name tracking | `processTypeName(AbstractType)` — delegates `importCollector.trackType(...)` |
| Static method tracking | `appendTargetAndMethodName(Expression, AbstractMethod)` — delegates `importCollector.trackStaticMethod(...)` |
| Method, field, constructor, getter, setter, lambda processing | `processMethod`, `processField`, `processConstructor`, `processGetter`, `processSetter`, `processLambda` — delegates `commentFormatter.appendMemberPrefix/Postfix` |
| Section prefix/postfix | `appendSectionPrefix`, `appendSectionPostfix` — delegates `commentFormatter.getLocalizedMultiLineComment` |
| Localized comments | `getLocalizedComment(AbstractType, String, Locale)` — `@Override`, delegates to `commentFormatter` |
| Localized multi-line comments | `getLocalizedMultiLineComment(AbstractType, String)` — delegates to `commentFormatter` |
| Multi-line comment processing | `processMultiLineComment(String)` — unchanged |
| Concurrency | `processDoTogether(DoTogether)` — delegates to `concurrencyEmitter` |
| Concurrency | `processEachInTogether(AbstractEachInTogether)` — delegates to `concurrencyEmitter` |
| Statement disabled markers | `pushStatementDisabled()`, `popStatementDisabled()` — unchanged |
| Formatting primitives | `appendConcatenationOperator()`, `appendAssignmentOperator()`, `appendForEachToken()`, `appendInEachToken()` — unchanged |
| Count loop | `processCountLoop(CountLoop)` — unchanged |
| Do in order | `processDoInOrder(DoInOrder)` — unchanged (16 lines, not extracted) |
| Resource expressions | `processResourceExpression(ResourceExpression)` — unchanged |
| Local declarations | `processLocalDeclaration(LocalDeclarationStatement)` — unchanged |
| Arguments | `processArgument(AbstractParameter, AbstractArgument)`, `processKeyedArgument(JavaKeyedArgument)` — unchanged |
| Class header | `appendClassHeader(NamedUserType)` — unchanged |
| Method header | `appendMethodHeader(AbstractMethod)` — unchanged |
| Super constructor | `processSuperConstructor(SuperConstructorInvocationStatement)` — unchanged |
| Access level | `getAccessLevel(AbstractMethod)` — unchanged |

The coordinator owns `@Override` methods because `SourceCodeGenerator` requires
they reside on the subclass. Method bodies for extracted concerns are one-line
delegations.

### JavaImportCollector

| Responsibility | Methods / Fields |
| --- | --- |
| On-demand package tracking | `trackPackageOnDemand(JavaPackage)` — adds to `packagesToImportOnDemand` set |
| Explicit type tracking | `trackType(JavaType, List<JavaPackage>)` — adds to `typesToImport` or `packagesToImportOnDemand` based on enclosing type and marked packages |
| Static method tracking | `trackStaticMethod(JavaMethod)` — adds to `methodsToImportStatic` set |
| Import block rendering | `buildImports(String prefix, String postfix)` — returns `StringBuilder` with complete import block |
| State | `packagesToImportOnDemand: Set<JavaPackage>`, `typesToImport: Set<JavaType>`, `methodsToImportStatic: Set<JavaMethod>` |

`JavaImportCollector` is stateful — import sets accumulate across the full code
generation pass and are consumed once by `buildImports`. The three
`Set<...>` fields that were on `JavaCodeGenerator` (lines 633–635 of the
original) move to this class. The two immutable `List<...>` fields for
configured imports (`packagesMarkedForOnDemandImport`,
`staticMethodsMarkedForImport`) remain on `JavaCodeGenerator` and are passed
as parameters to `trackType` and `trackStaticMethod`.

### JavaCommentFormatter

| Responsibility | Methods |
| --- | --- |
| Block comment formatting | `formatBlockComment(String)` — wraps multi-line text in `/* ... */` block comment syntax |
| Localized comment resolution | `getLocalizedComment(AbstractType, String, Locale)` — resolves from `ResourceBundle` using class hierarchy key walk, substitutes `<classname>` and `<objectname>` placeholders |
| Multi-line localized comment | `getLocalizedMultiLineComment(AbstractType, String)` — resolves and formats via `formatBlockComment` |
| Member prefix emission | `appendMemberPrefix(AbstractMember, StringBuilder)` — resolves and appends leading comment block |
| Member postfix emission | `appendMemberPostfix(AbstractMember, StringBuilder)` — resolves and appends trailing comment block |

`JavaCommentFormatter` is constructed with the `commentsLocalizationBundleName`
string (may be `null`). It is stateless — every method call is independent.

`formatBlockComment` uses `SourceCodeGenerator.splitIntoLines(String)` which is
a static package-private method, accessible because both classes are in
`org.lgna.project.ast`.

### JavaConcurrencyEmitter

| Responsibility | Methods |
| --- | --- |
| Do together | `processDoTogether(DoTogether, JavaCodeGenerator)` — emits `ThreadUtilities.doTogether(...)` with lambda or anonymous `Runnable` wrappers |
| Each in together | `processEachInTogether(AbstractEachInTogether, JavaCodeGenerator)` — emits `ThreadUtilities.eachInTogether(...)` with lambda or anonymous `EachInTogetherRunnable` wrappers |

`JavaConcurrencyEmitter` is stateless. It calls back to `JavaCodeGenerator` for
`appendTargetAndMethodName`, `appendString`, `appendStatement`,
`processExpression`, `processTypeName`, `appendSpace`, and `isLambdaSupported`.
These are all accessible because they are `public` or package-private on
`SourceCodeGenerator`/`JavaCodeGenerator`.

The emitter resolves `JavaType.getInstance(ThreadUtilities.class)` and
`JavaType.getInstance(EachInTogetherRunnable.class)` the same way the original
inline code did.

## Public API

The public API is the set of classes and methods that callers outside
`org.lgna.project.ast` use. After this extraction, the public API is unchanged:

| Class | Visibility | Status |
| --- | --- | --- |
| `JavaCodeGenerator` | `public` | Unchanged — same constructor, same methods |
| `JavaCodeGenerator.Builder` | `public static` | Unchanged — same fluent builder methods |
| `SourceCodeGenerator` | `public abstract` | Unchanged — not modified |
| `JavaImportCollector` | package-private | New — not accessible outside package |
| `JavaCommentFormatter` | package-private | New — not accessible outside package |
| `JavaConcurrencyEmitter` | package-private | New — not accessible outside package |

## Package-private collaboration

The three delegates access `JavaCodeGenerator` and `SourceCodeGenerator` methods
through a combination of:

1. **Direct back-reference** — `JavaConcurrencyEmitter` holds a
   `JavaCodeGenerator` reference and calls public/package-private methods on it.
2. **Parameter passing** — `JavaCommentFormatter` receives the
   `StringBuilder` (via `getCodeStringBuilder()`) and the
   `commentsLocalizationBundleName` at construction.
3. **Return values** — `JavaImportCollector.buildImports()` returns a
   `StringBuilder` consumed by `JavaCodeGenerator.processClass`.

All three delegates are in the same package (`org.lgna.project.ast`) and can
access package-private methods like `splitIntoLines`, `getCodeStringBuilder`,
`isLambdaSupported`, and `appendTargetAndMethodName`.

## Bridge methods

`JavaCodeGenerator` retains thin bridge methods for comment formatting and
concurrency. Import tracking is delegated directly from the `processTypeName`
override (see [Trace 1](../tutorials/trace-java-code-generator-extraction.md#trace-1-import-collection-during-type-name-processing)):

```java
// Comment formatting — called from processMethod, processField, etc.
private void appendMemberPrefix(AbstractMember member) {
  commentFormatter.appendMemberPrefix(member, getCodeStringBuilder());
}

private void appendMemberPostfix(AbstractMember member) {
  commentFormatter.appendMemberPostfix(member, getCodeStringBuilder());
}

// Localized multi-line comment — called from appendSectionPrefix/Postfix
protected String getLocalizedMultiLineComment(AbstractType<?, ?, ?> type,
    String sectionName) {
  return commentFormatter.getLocalizedMultiLineComment(type, sectionName);
}

// Concurrency — called from @Override methods
@Override
public void processDoTogether(DoTogether doTogether) {
  concurrencyEmitter.processDoTogether(doTogether, this);
}

@Override
public void processEachInTogether(AbstractEachInTogether eachInTogether) {
  concurrencyEmitter.processEachInTogether(eachInTogether, this);
}
```

`getLocalizedMultiLineComment` remains `protected` because
`NetbeansJavaCodeGenerator.appendSectionPrefix` calls it directly.

## NetbeansJavaCodeGenerator compatibility

`NetbeansJavaCodeGenerator` (in `netbeans/src/main/java/.../`) extends
`JavaCodeGenerator` and overrides:

| Method | Compatibility |
| --- | --- |
| `getImportsPrefix()` | ✅ Unchanged — still `protected` on `JavaCodeGenerator`, return value passed to `importCollector.buildImports(prefix, postfix)` |
| `getImportsPostfix()` | ✅ Unchanged — same pattern as prefix |
| `appendSectionPrefix(...)` | ✅ Unchanged — still `protected` on `JavaCodeGenerator`, calls `getLocalizedMultiLineComment` which is still `protected` |
| `appendSectionPostfix(...)` | ✅ Unchanged — same pattern as prefix |
| `getAccessLevel(AbstractMethod)` | ✅ Unchanged — not extracted |

No modifications to `NetbeansJavaCodeGenerator` are needed.

## Security boundary

All three new classes are package-private — no `public` modifier on the class
declaration. This prevents access from outside `org.lgna.project.ast`:

```java
// JavaImportCollector.java
class JavaImportCollector { ... }

// JavaCommentFormatter.java
class JavaCommentFormatter { ... }

// JavaConcurrencyEmitter.java
class JavaConcurrencyEmitter { ... }
```

No new reflective access, file I/O, network I/O, or process spawning is
introduced. The delegates operate purely on in-memory AST data structures.

## Error handling contract

Error handling is unchanged from the original `JavaCodeGenerator`:

| Scenario | Behavior |
| --- | --- |
| `commentsLocalizationBundleName` is `null` | `getLocalizedComment` returns `null` — no comment emitted |
| Resource bundle key miss | `RuntimeException` caught silently, loop continues to supertype key |
| `<classname>` / `<objectname>` placeholders | Substituted via `String.replaceAll` — same regex behavior |
| `type == null` in `processTypeName` | Emits `"MISSING_TYPE"` — unchanged |
| Missing resource bundle for `DoInOrder` label | `MissingResourceException` caught, prints to `System.out` — unchanged |

## Configuration

No new configuration is introduced. The existing `JavaCodeGenerator.Builder`
fluent API remains the sole configuration surface:

```java
JavaCodeGenerator generator = new JavaCodeGenerator.Builder()
    .isLambdaSupported(true)
    .isPublicStaticFinalFieldGetterDesired(false)
    .setResourcesTypeWrapper(wrapper)
    .addCommentsLocalizationBundleName("org.alice.ide.comments")
    .addImportOnDemandPackage(org.lgna.story.SScene.class.getPackage())
    .addImportStaticMethod(myMethod)
    .addCodeOrganizerDefinition("methods", methodsOrgDef)
    .addDefaultCodeOrganizerDefinition(defaultOrgDef)
    .build();
```

The `Builder` internally constructs all three delegates during
`JavaCodeGenerator`'s constructor. Callers do not interact with delegates.

## Validation

Verify the extraction with these commands:

```bash
# Build core/ast module (catches compilation errors in same-package delegates)
mvn -pl core/ast compile -q

# Build netbeans module (catches NetbeansJavaCodeGenerator compilation)
mvn -pl netbeans compile -q

# Full project build
mvn compile -q

# Line count check
wc -l core/ast/src/main/java/org/lgna/project/ast/JavaCodeGenerator.java
# Expected: ≤ 500 lines

# Verify delegate visibility (no 'public class')
grep '^class ' core/ast/src/main/java/org/lgna/project/ast/JavaImportCollector.java
grep '^class ' core/ast/src/main/java/org/lgna/project/ast/JavaCommentFormatter.java
grep '^class ' core/ast/src/main/java/org/lgna/project/ast/JavaConcurrencyEmitter.java
```

## Acceptance criteria

| # | Criterion | Verification |
| --- | --- | --- |
| 1 | `JavaCodeGenerator.java` is under 500 lines | `wc -l` ≤ 500 |
| 2 | Three new files exist | `ls` lists `JavaImportCollector.java`, `JavaCommentFormatter.java`, `JavaConcurrencyEmitter.java` in `core/ast/src/main/java/org/lgna/project/ast/` |
| 3 | All three new classes are package-private | `grep '^class '` shows no `public` modifier |
| 4 | `core/ast` module compiles | `mvn -pl core/ast compile` succeeds |
| 5 | `netbeans` module compiles | `mvn -pl netbeans compile` succeeds |
| 6 | Full project compiles | `mvn compile` succeeds |
| 7 | No public API changes | `JavaCodeGenerator` and `Builder` have identical public/protected method signatures |
| 8 | `NetbeansJavaCodeGenerator` unchanged | `git diff netbeans/` shows no changes |
| 9 | `getLocalizedMultiLineComment` remains protected | `grep 'protected.*getLocalizedMultiLineComment' JavaCodeGenerator.java` matches |
| 10 | `getImportsPrefix` / `getImportsPostfix` remain protected | `grep 'protected.*getImportsP' JavaCodeGenerator.java` matches both |

## Claim boundaries

This extraction **does** cover:

- Import tracking sets and import block rendering → `JavaImportCollector`
- Block comment formatting, localized comment resolution, member prefix/postfix
  → `JavaCommentFormatter`
- `processDoTogether` and `processEachInTogether` → `JavaConcurrencyEmitter`
- Bringing `JavaCodeGenerator.java` under 500 lines

This extraction **does not** cover:

- Modifying `SourceCodeGenerator` in any way
- Modifying `NetbeansJavaCodeGenerator`
- Modifying `StoryApiSpecificAstUtilities` or any other external caller
- Changing the generated Java source output
- Adding tests (characterization tests are a separate issue)
- Extracting `processDoInOrder` (16 lines, not worth the delegation overhead)
- Extracting `processCountLoop`, resource expression, or other small methods
