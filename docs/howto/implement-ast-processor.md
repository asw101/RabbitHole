# How to Implement a New AstProcessor

This guide explains how to create a new `AstProcessor` implementation for
custom AST traversal. Since issue #672, most `AstProcessor` methods have
`default {}` bodies, so new implementations only need to override methods
relevant to their purpose.

## Prerequisites

- Familiarity with the Alice 3 AST node hierarchy (`org.lgna.project.ast`)
- A module that depends on `core/ast`

## Quick start

### 1. Implement the required methods

`AstProcessor` has four required methods — one returning `CodeOrganizer` and
three structural void methods:

```java
import org.lgna.project.code.CodeOrganizer;
import org.lgna.project.ast.AstProcessor;

public class MyProcessor implements AstProcessor {

  @Override
  public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
    return new CodeOrganizer(CodeOrganizer.defaultCodeOrganizer);
  }

  @Override
  public void processClass(CodeOrganizer codeOrganizer, NamedUserType userType) {
    // Iterate codeOrganizer.getOrderedSections() and call process() on nodes
  }

  @Override
  public void processField(UserField field) {
    // Your field-processing logic
  }

  @Override
  public void processMethod(UserMethod method) {
    // Your method-processing logic
  }
}
```

This compiles immediately. All other void visitor methods (47 total, including
2 pre-existing defaults) are no-ops.

### 2. Override additional methods as needed

Override additional `process*` methods for your specific traversal needs.
All other AST nodes (statements, expressions, primitives, comments) will be
silently skipped via the inherited `default {}` bodies.

For example, to also process constructors:

```java
@Override
public void processConstructor(NamedUserConstructor constructor) {
  // Your constructor-processing logic
}
```

### 3. Dispatch through ProcessableNode

```java
ProcessableNode node = ...; // e.g., a NamedUserType
MyProcessor processor = new MyProcessor();
node.process(processor);
```

The node calls the appropriate `process*` method on your processor. Container
nodes (types, methods, blocks) recursively call `process` on their children.

## Method categories

| Category | Methods | Typical override reason |
|----------|---------|----------------------|
| **Structure** | `processClass`, `processConstructor`, `processMethod`, `processField` | Class/member enumeration |
| **Getters/Setters** | `processGetter`, `processIndexedGetter`, `processSetter`, `processIndexedSetter` | Property access analysis |
| **Statements** | `processBlock`, `processExpressionStatement`, `processReturnStatement`, `processLocalDeclaration`, `processConditional`, `processCountLoop`, `processForEach`, `processWhileLoop`, `processDoInOrder`, `processDoTogether`, `processEachInTogether` | Control flow analysis |
| **Expressions** | `processExpression`, `processMethodCall`, `processFieldAccess`, `processAssignmentExpression`, `processConcatenation`, `processInfixExpression`, `processInstantiation`, `processArrayInstantiation`, `processArrayAccess`, `processArrayLength` | Expression evaluation |
| **Literals** | `processNull`, `processBoolean`, `processInt`, `processFloat`, `processDouble`, `processEscapedStringLiteral`, `processTypeName`, `processTypeLiteral` | Literal collection |
| **References** | `processThisReference`, `processSuperReference`, `processVariableIdentifier` | Scope analysis |
| **Resources** | `processResourceExpression`, `processResourceType`, `processDynamicResource` | Resource enumeration |
| **Other** | `processLambda`, `processKeyedArgument`, `processMultiLineComment`, `processSuperConstructor`, `processThisConstructor`, `processConstructorBlock`, `processLogicalComplement` | Specialized analysis |

## Existing implementations

| Class | Module | Purpose | Methods overridden |
|-------|--------|---------|-------------------|
| `SourceCodeGenerator` | `core/ast` | Java source generation | All |
| `JavaCodeGenerator` | `core/ast` | Java code generation | All |
| `TweedleEncoder` | `core/ast` | Tweedle language encoding | All |
| `HtmlEncoder` | `core/ide` | HTML/SVG export | 3 (class, field, method) + getNewCodeOrganizerForTypeName |

## Tips

- **Start minimal.** The four required methods (`getNewCodeOrganizerForTypeName`,
  `processClass`, `processField`, `processMethod`) are the minimum. Override
  additional methods as needed — the defaults are safe no-ops.
- **Check `isPublicStaticFinalFieldGetterDesired()`.** This optional method
  defaults to `true`. Override it to `false` if your processor should skip
  generated field getters.
- **Use `CodeOrganizer`** to control section ordering. The organizer determines
  which sections appear and in what order when processing a class.

## See also

- [Encoder delegate decomposition](../reference/encoder-delegate-decomposition.md)
  — TweedleEncoder refactoring pattern
- [HtmlEncoder SVG delegate decomposition](../reference/html-encoder-svg-delegate-decomposition.md)
  — This refactoring's reference documentation
