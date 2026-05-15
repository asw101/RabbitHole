# HtmlEncoder SVG Delegate Decomposition

This reference describes the decomposition of the 580-line `HtmlEncoder` into a
~350-line coordinator plus a package-private `SvgEncoder` delegate, paired with
the addition of default method bodies to `AstProcessor`.

The decomposition is a pure internal refactor. The public API surface —
`HtmlProjectWriter` — is unchanged. All existing HTML export behavior, SVG
rendering, CSS classes, and DOM structure are preserved identically.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Changes by component](#changes-by-component)
  - [AstProcessor (interface)](#astprocessor-interface)
  - [HtmlEncoder (coordinator)](#htmlencoder-coordinator)
  - [SvgEncoder (delegate)](#svgencoder-delegate)
- [Public API](#public-api)
- [Package-private collaboration](#package-private-collaboration)
- [Security boundary](#security-boundary)
- [Configuration](#configuration)
- [Validation](#validation)
- [Acceptance criteria](#acceptance-criteria)
- [Risks and mitigations](#risks-and-mitigations)

## Motivation

The original `HtmlEncoder.java` contained 580 lines, of which roughly 196 were
empty method stubs required by the `AstProcessor` interface. These stubs existed
solely because `AstProcessor` declared all 48 void methods as abstract — even though
`HtmlEncoder` intentionally no-ops on 45 of them (statements, expressions,
primitives, and comments are rendered as SVG images, not as individual DOM
nodes).

Additionally, five SVG-related methods (`pushSvg`, `useCommonIdGenerator`,
`addToSvg`, `addTypeSvgInline`, `addExpressionToSvg`) managed isolated state
(`activeSVG`, `firstIDGenerator`) that was unrelated to the HTML DOM building
responsibility of the rest of the class.

RabbitHole issue #672 addresses both concerns:

1. **Default methods on AstProcessor** — 45 void methods receive `default {}`
   bodies, eliminating the need for empty stubs in any implementor.
2. **SVG delegate extraction** — SVG rendering moves to a focused `SvgEncoder`
   class that owns its own state.

This mirrors the pattern established in the
[TweedleEncoder delegate decomposition](./encoder-delegate-decomposition.md)
(issue #483).

## Architecture

```text
HtmlProjectWriter (public facade — unchanged)
└── HtmlEncoder (coordinator, ~350 lines)
    │   implements AstProcessor
    │   Owns: DOM document, element stack, CSS class generation
    │   Delegates SVG rendering to SvgEncoder
    │
    └── SvgEncoder (package-private, ~50 lines)
        └── SVG canvas management, Batik SVGGraphics2D lifecycle,
            ID generator sharing, component-to-SVG painting
```

```text
AstProcessor (interface, core/ast)
├── getNewCodeOrganizerForTypeName(String) — required (returns CodeOrganizer)
├── isPublicStaticFinalFieldGetterDesired() — default true
├── processClass(...)    — required (abstract)
├── processField(...)    — required (abstract)
├── processMethod(...)   — required (abstract)
├── processBlock(...)    — default {}
├── processExpression(...)— default {}
├── ... (45 of 48 void methods — default {}; 3 stay abstract)
```

## Changes by component

### AstProcessor (interface)

**File:** `core/ast/src/main/java/org/lgna/project/ast/AstProcessor.java`

45 of the 48 abstract void methods now have `default {}` bodies. Three
structural methods — `processClass`, `processField`, `processMethod` — remain
abstract alongside the non-void `getNewCodeOrganizerForTypeName`. This gives the
interface four required methods total (one returning `CodeOrganizer`, three void)
while making all visitor-leaf methods optional.

**Before (48 abstract void methods):**
```java
void processClass(CodeOrganizer codeOrganizer, NamedUserType userType); // stays abstract
void processField(UserField field);                                      // stays abstract
void processMethod(UserMethod method);                                   // stays abstract
void processBlock(BlockStatement blockStatement);   // becomes default {}
void processExpression(Expression expression);      // becomes default {}
void processNull();                                 // becomes default {}
// ... 42 more abstract void methods → default {}
```

**After (3 abstract + 45 default void methods):**
```java
void processClass(CodeOrganizer codeOrganizer, NamedUserType userType); // required
void processField(UserField field);                                      // required
void processMethod(UserMethod method);                                   // required
default void processBlock(BlockStatement blockStatement) { }
default void processExpression(Expression expression) { }
default void processNull() { }
// ... 42 more default void methods
```

**Impact on existing implementors:**

| Implementor | Module | Effect |
|-------------|--------|--------|
| `SourceCodeGenerator` | `core/ast` | None — already overrides all methods |
| `JavaCodeGenerator` | `core/ast` | None — already overrides all methods |
| `TweedleEncoder` | `core/ast` | None — already overrides all methods |
| `HtmlEncoder` | `core/ide` | 45 empty stubs removed |

**Impact on future implementors:**

New `AstProcessor` implementations must override four methods at compile time:
`getNewCodeOrganizerForTypeName`, `processClass`, `processField`, and
`processMethod`. All other visitor methods default to no-ops, which is
acceptable for a visitor interface where partial processing is the common case.
The three required void methods ensure that every implementor makes an explicit
decision about the core structural traversal.

### HtmlEncoder (coordinator)

**File:** `core/ide/src/main/java/org/alice/ide/croquet/models/html/HtmlEncoder.java`

Two categories of changes:

1. **Stub deletion (−195 lines):** 45 empty `@Override` methods (lines 385–579
   in the original) are deleted. These methods — `processGetter`,
   `processBlock`, `processExpression`, `processNull`, etc. — now inherit
   `default {}` bodies from `AstProcessor`.

2. **SVG delegation (−30 lines net):** Five SVG methods and two fields move to
   `SvgEncoder`. HtmlEncoder gains a single field:

   ```java
   private final SvgEncoder svgEncoder;
   ```

   Initialized in the constructor:

   ```java
   HtmlEncoder(Document doc) {
     document = doc;
     svgEncoder = new SvgEncoder(doc, this::parentNode);
   }
   ```

**Methods moved to SvgEncoder:**

| Original method | Original lines | Now calls |
|----------------|---------------|-----------|
| `pushSvg(Runnable)` | 136–150 | `svgEncoder.pushSvg(parentNode(), content)` |
| `useCommonIdGenerator()` | 152–160 | Internal to SvgEncoder |
| `addToSvg(SwingComponentView)` | 162–173 | `svgEncoder.addToSvg(view)` |
| `addTypeSvgInline(AbstractType)` | 175–177 | `svgEncoder.addTypeSvgInline(type, parentNode())` |
| `addExpressionToSvg(Expression)` | 179–181 | `svgEncoder.addExpressionToSvg(expression)` |

**Fields moved to SvgEncoder:**

| Field | Type |
|-------|------|
| `activeSVG` | `SVGGraphics2D` |
| `firstIDGenerator` | `SVGIDGenerator` |

**Retained in HtmlEncoder:**

All DOM builder methods (`pushDiv`, `pushSpan`, `addDiv`, `addSpan`,
`addElement`, `parentNode`, `parentElement`) remain in HtmlEncoder. They are
simple 3-line wrappers called throughout the class; extracting them would add
indirection without improving cohesion.

Three AST processing methods that contain real logic — `processClass`,
`processField`, `processMethod` — remain as explicit `@Override` methods.
These correspond to the three void methods that stay abstract in `AstProcessor`.

The six formerly-empty stubs with documentation comments (`processGetter`,
`processIndexedGetter`, `processSetter`, `processIndexedSetter`,
`processConstructor`, `processSuperConstructor`) are deleted along with the
other 39 pure-empty stubs (45 total). Their "Does not include X" intent is now
expressed by inheriting the `default {}` no-op from the interface.

### SvgEncoder (delegate)

**File:** `core/ide/src/main/java/org/alice/ide/croquet/models/html/SvgEncoder.java`

A package-private class (~50 lines) that manages the Batik `SVGGraphics2D`
lifecycle for rendering Alice AST elements as inline SVG.

```java
class SvgEncoder {
  private final Document document;
  private final Supplier<Node> parentNodeSupplier;
  private SVGGraphics2D activeSVG;
  private SVGIDGenerator firstIDGenerator;

  SvgEncoder(Document document, Supplier<Node> parentNodeSupplier) { ... }

  void pushSvg(Node parentNode, Runnable content) { ... }
  void addToSvg(SwingComponentView<?> view) { ... }
  void addTypeSvgInline(AbstractType<?,?,?> type, Node parentNode) { ... }
  void addExpressionToSvg(Expression expression) { ... }
}
```

**Responsibilities:**
- Creates `SVGGraphics2D` instances from the shared `Document`
- Shares a single `SVGIDGenerator` across all SVGs in a document to prevent ID
  collisions
- Paints Swing components onto the SVG canvas
- Appends the finished SVG root element to the caller-supplied parent node
- Enforces the invariant that SVGs cannot be nested (throws `RuntimeException`)

**Not responsible for:**
- DOM element creation (stays in HtmlEncoder)
- CSS class assignment (stays in HtmlEncoder)
- AST traversal logic (stays in HtmlEncoder)
- Element stack management (stays in HtmlEncoder)

## Public API

The public API is unchanged. `HtmlProjectWriter` is the only public entry point
for HTML export:

```java
HtmlProjectWriter writer = new HtmlProjectWriter();

// Write a single type
writer.writeType(outputStream, namedUserType);

// Write an entire project
writer.writeProject(outputStream, project);

// Write a single declaration
writer.writeDeclaration(outputStream, declaration);
```

`HtmlProjectWriter` creates an `HtmlEncoder` internally (line 143 of
`HtmlProjectWriter.java`). No external code instantiates `HtmlEncoder` or
`SvgEncoder` directly.

## Package-private collaboration

All three classes reside in `org.alice.ide.croquet.models.html`:

```text
HtmlProjectWriter.java  (public)  — creates HtmlEncoder
HtmlEncoder.java        (public)  — creates SvgEncoder, implements AstProcessor
SvgEncoder.java         (package) — SVG rendering delegate
```

`SvgEncoder` receives a `Supplier<Node>` for parent-node access rather than
holding a reference to `HtmlEncoder`, keeping the dependency one-directional.

## Security boundary

- `SvgEncoder` is package-private — cannot be instantiated outside the package.
- All DOM manipulation uses W3C DOM APIs (`Document.createElement`,
  `Element.setAttribute`, `Node.appendChild`). No string-based HTML
  concatenation exists anywhere in the pipeline.
- `processEscapedStringLiteral` in HtmlEncoder (inherited default no-op) does
  not participate in HTML generation. String content is set via
  `Element.setTextContent`, which performs XML escaping automatically.

## Configuration

No configuration changes. `HtmlEncoder` and `SvgEncoder` have no configurable
settings. The CSS styles embedded in `HtmlProjectWriter.cssStyle` are unchanged.

The `codeOrganizerDefinitionMap` static configuration (Scene → scene organizer,
Program → program organizer) and `sectionsToSkip` list are unchanged.

## Validation

### Build validation

```bash
# Compile both affected modules and all dependencies
mvn compile -pl core/ast,core/ide -am

# Run core/ide tests (integration coverage)
mvn test -pl core/ide
```

### Line count verification

```bash
wc -l core/ide/src/main/java/org/alice/ide/croquet/models/html/HtmlEncoder.java
# Expected: < 500 lines (target: ~354)

wc -l core/ide/src/main/java/org/alice/ide/croquet/models/html/SvgEncoder.java
# Expected: ~50 lines
```

### Interface contract verification

```bash
# Confirm AstProcessor has exactly 4 required (abstract) methods
# 1 non-void: getNewCodeOrganizerForTypeName
# 3 void: processClass, processField, processMethod
grep -c '^\s\+void\|^\s\+CodeOrganizer' \
  core/ast/src/main/java/org/lgna/project/ast/AstProcessor.java
# Expected: 4 (lines without 'default' keyword)
```

## Acceptance criteria

| # | Criterion | Verification |
|---|-----------|--------------|
| 1 | `HtmlEncoder.java` < 500 lines | `wc -l` check |
| 2 | `SvgEncoder.java` exists and is package-private | `grep 'class SvgEncoder'` has no `public` modifier |
| 3 | `AstProcessor` has 45 `default void` methods (+ 2 pre-existing) | `grep -c 'default void'` = 47 |
| 4 | Four methods remain required (no `default` keyword) | `getNewCodeOrganizerForTypeName`, `processClass`, `processField`, `processMethod` |
| 5 | `mvn compile -pl core/ast,core/ide -am` succeeds | Zero compilation errors |
| 6 | `mvn test -pl core/ide` passes | All tests green |
| 7 | `HtmlProjectWriter.java` unchanged | `git diff` shows no changes |
| 8 | HTML output identical | Manual or diff comparison of exported HTML |

## Risks and mitigations

| Risk | Likelihood | Mitigation |
|------|-----------|------------|
| Future `AstProcessor` implementors get silent no-ops | Medium | Four methods stay required as compile-time signals (`getNewCodeOrganizerForTypeName`, `processClass`, `processField`, `processMethod`). Javadoc on `AstProcessor` notes that leaf visitor methods are optional. |
| No dedicated `HtmlEncoder` unit tests | Known | Compilation verification + full `core/ide` test suite. HTML export is exercised by integration tests through `HtmlProjectWriter`. |
| `SvgEncoder` parent-node supplier returns stale node | Low | `parentNode()` reads from the live element stack; `Supplier<Node>` is called at render time, not captured early. |
| SVG ID collisions across documents | None | `firstIDGenerator` sharing is preserved identically in `SvgEncoder`. |
