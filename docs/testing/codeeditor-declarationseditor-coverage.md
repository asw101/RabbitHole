# Code Editor & Declarations Editor Test Coverage

Coverage tests for `org.alice.ide.codeeditor` and `org.alice.ide.declarationseditor`
packages — editor tab creation, code panel structure, and declaration editing logic.

**Module:** `core/ide`
**Framework:** JUnit 4
**Lines:** 2000+ across 12 test files
**Headless:** All tests run without a display (CI-safe)

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Test File Index](#test-file-index)
4. [Package: codeeditor](#package-codeeditor)
   - [StatementListBorderTest](#statementlistbordertest)
   - [CodeEditorGUITest](#codeeditorguitest)
5. [Package: declarationseditor](#package-declarationseditor)
   - [DeclarationTabStateTest](#declarationtabstatetest)
   - [DeclarationsEditorCompositeTest](#declarationseditorcompositetest)
   - [SeparatorClassesTest](#separatorclassestest)
   - [DeclarationHistoryNavigationTest](#declarationhistorynavigationtest)
   - [DeclarationMenuFillInTest](#declarationmenufillintest)
6. [Package: declarationseditor/type](#package-declarationseditortype)
   - [TypeCompositeHierarchyTest](#typecompositehierarchytest)
   - [TypeStateReflectionTest](#typestatereflectiontest)
   - [TypeMenuModelTest](#typemenumodeltest)
7. [Package: declarationseditor/type/data](#package-declarationseditortypedata)
   - [FieldDataTest](#fielddatatest)
   - [ManagedFieldDataTest](#managedfielddatatest)
8. [Test Patterns](#test-patterns)
9. [Configuration](#configuration)
10. [Troubleshooting](#troubleshooting)

---

## Overview

These 12 test files provide characterization coverage for the two core IDE editor
subsystems in Alice 3:

- **`org.alice.ide.codeeditor`** — The code panel that displays method/constructor
  bodies, parameter panes, expression drop-downs, and statement list borders. Most
  classes extend Swing/Croquet GUI components and are tested via reflection.

- **`org.alice.ide.declarationseditor`** — The tabbed declaration editor that manages
  type/code composites, history navigation (back/forward), separator labels, and
  menu models. Includes the `type/` and `type/data/` sub-packages for composite
  states, filtered member data, and management-level field filtering.

### What Is Covered

| Area | Classes Tested | Strategy |
|------|---------------|----------|
| Code editor GUI panes | 10 classes (CommentPane, MethodHeaderPane, etc.) | Reflection |
| Statement list border | StatementListBorder | Direct instantiation |
| Declaration tab state | DeclarationTabState | Reflection (requires IDE singleton) |
| Editor composite | DeclarationsEditorComposite, CodeComposite | Reflection (requires IDE singleton) |
| Separator labels | 6 separator singletons | Reflection |
| History/navigation | DeclarationCompositeHistory, 5 operations/cascades | Reflection |
| Menus/fill-ins | DeclarationMenu, TypeMenu, DeclarationCompositeFillIn, HighlightFieldOperation | Reflection |
| Type composites | 8 *Composite + 6 *ToolPaletteCoreComposite | Reflection |
| Type states | 11 *State classes including abstract hierarchy | Reflection |
| Type menu models | MethodMenuModel, FieldMenuModel, ConstructorMenuModel, MemberMenuModel | Reflection |
| Field data filtering | FieldData, ManagedFieldData, UnmanagedFieldData | Direct + isAcceptableItem |
| Managed field data | AbstractManagedFieldData, ManagedCameraMarkerFieldData, ManagedObjectMarkerFieldData | Direct + isAcceptableItem |

### What Is NOT Covered

- **View rendering** — Swing `paintComponent`/`paintBorder` calls require a live
  `Graphics` context. Border painting in `StatementListBorder` is tested structurally
  (constructor, `isBorderOpaque`, `getMinimum`) but not pixel-level.
- **IDE singleton interactions** — Classes that call `IDE.getActiveInstance()` internally
  (e.g., `DeclarationCompositeHistory.start()`, `TypeComposite.getInstance()`) are
  tested via reflection only. The singleton call-sites are verified to exist but not
  exercised.
- **Swing event dispatch** — No `SwingUtilities.invokeAndWait` is used in these 12 files.
  The existing `ProcedureTabSelectionTest` already covers that pattern.

---

## Architecture

### Test Strategy Decision Tree

```
Is the class a Swing/Croquet GUI component?
├── YES → Does it require Application/IDE singleton to construct?
│   ├── YES → Reflection only (verify hierarchy, methods, fields)
│   └── NO  → Direct instantiation if possible, else reflection
└── NO  → Does it use AST nodes (UserMethod, UserField, etc.)?
    ├── YES → Construct real AST objects, test behavior directly
    └── NO  → Direct instantiation + behavioral assertions
```

### Reflection Testing Pattern

All reflection tests follow a consistent structure:

```java
@Test
public void className_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.codeeditor.CommentPane");
    assertNotNull(cls);
}

@Test
public void className_extendsExpectedSuperclass() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.codeeditor.CommentPane");
    Class<?> parent = Class.forName("org.alice.ide.codeeditor.AbstractStatementPane");
    assertTrue(parent.isAssignableFrom(cls));
}

@Test
public void className_hasExpectedMethod() throws Exception {
    Class<?> cls = Class.forName("org.alice.ide.codeeditor.MethodHeaderPane");
    assertNotNull(cls.getDeclaredMethod("createNameLabel"));
}
```

### AST Node Construction Pattern

Tests that exercise `isAcceptableItem` or composite behavior construct real AST nodes:

```java
// Create a type with fields at different management levels
NamedUserType type = AstUtilities.createType("TestScene", JavaType.getInstance(SScene.class));
UserField managed = new UserField("camera", JavaType.getInstance(SCamera.class));
managed.managementLevel.setValue(ManagementLevel.MANAGED);
UserField unmanaged = new UserField("myVar", JavaType.INTEGER_OBJECT_TYPE);
unmanaged.managementLevel.setValue(ManagementLevel.NONE);
type.fields.add(managed);
type.fields.add(unmanaged);
```

### Static Cache Isolation

Each test method uses unique names (e.g., `"TestType_" + testMethodName`) when
creating `NamedUserType` instances via `AstUtilities.createType()`. This avoids
collisions in internal type registries without requiring cleanup.

---

## Test File Index

| # | File | Lines | Strategy | Classes Covered |
|---|------|-------|----------|-----------------|
| 1 | `codeeditor/StatementListBorderTest.java` | ~150 | Direct | StatementListBorder |
| 2 | `codeeditor/CodeEditorGUITest.java` | ~250 | Reflection | 10 GUI classes |
| 3 | `declarationseditor/DeclarationTabStateTest.java` | ~120 | Reflection+static | DeclarationTabState |
| 4 | `declarationseditor/DeclarationsEditorCompositeTest.java` | ~150 | Reflection | DeclarationsEditorComposite, CodeComposite |
| 5 | `declarationseditor/SeparatorClassesTest.java` | ~160 | Reflection | 6 separator singletons |
| 6 | `declarationseditor/DeclarationHistoryNavigationTest.java` | ~200 | Reflection | History, Back/Forward ops & cascades |
| 7 | `declarationseditor/DeclarationMenuFillInTest.java` | ~180 | Reflection | DeclarationMenu, TypeMenu, FillIn, Highlight |
| 8 | `declarationseditor/type/TypeCompositeHierarchyTest.java` | ~280 | Reflection | 14 composite classes |
| 9 | `declarationseditor/type/TypeStateReflectionTest.java` | ~220 | Reflection | 11 state classes |
| 10 | `declarationseditor/type/TypeMenuModelTest.java` | ~150 | Reflection | 4 menu model classes |
| 11 | `declarationseditor/type/data/FieldDataTest.java` | ~180 | Direct+AST | FieldData, ManagedFieldData, UnmanagedFieldData |
| 12 | `declarationseditor/type/data/ManagedFieldDataTest.java` | ~180 | Direct+AST | AbstractManagedFieldData, marker field data |

**Total: ~2280 lines**

---

## Package: codeeditor

### StatementListBorderTest

**File:** `core/ide/src/test/java/org/alice/ide/codeeditor/StatementListBorderTest.java`

Tests `StatementListBorder`, which renders the "drop statement here" placeholder
in empty code blocks. This class implements `javax.swing.border.Border` and can
be instantiated directly without an Application context.

#### Test Methods

| Method | What It Verifies |
|--------|-----------------|
| `constructor_mutable_nullAlternate` | Constructs with `isMutable=true`, null alternate list, verifies no exception |
| `constructor_immutable_nullAlternate` | Constructs with `isMutable=false`, null alternate list |
| `constructor_withNormalInsets` | Passes custom `Insets` and verifies `getMinimum()` |
| `getMinimum_returnsConstructorValue` | `minimum` parameter is returned by `getMinimum()` |
| `getMinimum_zero` | `minimum=0` returns 0 |
| `getMinimum_positive` | `minimum=3` returns 3 |
| `isBorderOpaque_returnsFalse` | Border is never opaque (transparent overlay) |
| `isDrawingDesired_defaultTrue` | Drawing is desired by default |
| `setDrawingDesired_false` | `setDrawingDesired(false)` suppresses drawing |
| `setDrawingDesired_true_afterFalse` | Can re-enable drawing |
| `implementsBorderInterface` | `StatementListBorder` implements `javax.swing.border.Border` |
| `hasGetBorderInsetsMethod` | `getBorderInsets(Component)` exists |
| `hasPaintBorderMethod` | `paintBorder(Component, Graphics, int, int, int, int)` exists |

#### Example

```java
@Test
public void getMinimum_returnsConstructorValue() {
    Insets insets = new Insets(2, 4, 6, 8);
    StatementListBorder border = new StatementListBorder(true, null, insets, 5);
    assertEquals(5, border.getMinimum());
}
```

---

### CodeEditorGUITest

**File:** `core/ide/src/test/java/org/alice/ide/codeeditor/CodeEditorGUITest.java`

Reflection-only tests for 10 GUI classes in the `codeeditor` package. These classes
extend Swing/Croquet pane types and require an Application or factory context to
instantiate, so we verify class structure, hierarchy, and method signatures only.

#### Classes Tested

| Class | Access | Superclass Verified |
|-------|--------|-------------------|
| `CommentPane` | public | `AbstractStatementPane` |
| `MethodHeaderPane` | public | `AbstractCodeHeaderPane` |
| `ConstructorHeaderPane` | public | `AbstractCodeHeaderPane` |
| `ParametersPane` | public | `AbstractListPropertyPane` |
| `TypedParameterPane` | public | `TypedDeclarationPane` → `LineAxisPanel` (Croquet) |
| `AbstractCodeHeaderPane` | package-private | `Panel` (Croquet) |
| `ExpressionPropertyDropDownPane` | public | `DropDown` (Croquet) |
| `ArgumentListPropertyPane` | public | `AbstractArgumentListPropertyPane` |
| `InstanceLine` | package-private | loaded via `Class.forName` |
| `CodeEditor` | public | verified via existing `CodeEditorTest` |

#### Test Methods (per class pattern)

Each class has 3-5 tests following this template:

| Method Pattern | What It Verifies |
|---------------|-----------------|
| `{className}_classExists` | `Class.forName` succeeds |
| `{className}_extendsExpectedParent` | Superclass is the expected Swing/Croquet type |
| `{className}_hasExpectedConstructor` | Constructor with expected parameter types exists |
| `{className}_hasKeyMethods` | Domain-specific methods (e.g., `createNameLabel`, `getParameters`) |
| `{className}_modifiers` | Public/package-private/abstract as expected |

#### Package-Private Class Testing

`AbstractCodeHeaderPane` and `InstanceLine` are package-private (no `public` modifier).
They are loaded via `Class.forName("org.alice.ide.codeeditor.AbstractCodeHeaderPane")`
and verified by checking `Modifier.isPublic(cls.getModifiers())` returns `false`.

```java
@Test
public void abstractCodeHeaderPane_isPackagePrivate() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.codeeditor.AbstractCodeHeaderPane");
    assertFalse("Should be package-private", Modifier.isPublic(cls.getModifiers()));
}
```

---

## Package: declarationseditor

### DeclarationTabStateTest

**File:** `core/ide/src/test/java/org/alice/ide/declarationseditor/DeclarationTabStateTest.java`

Tests `DeclarationTabState`, which extends `MutableDataTabState<DeclarationComposite>`
and manages the tabbed list of open declarations in the editor. Because the
constructor references `IDE.DOCUMENT_UI_GROUP`, this class is tested primarily
via reflection. Static icon accessor methods are testable directly.

#### Test Methods

| Method | What It Verifies |
|--------|-----------------|
| `tabState_classExists` | `Class.forName` succeeds |
| `tabState_extendsMutableDataTabState` | Class hierarchy check |
| `tabState_hasDefaultConstructor` | No-arg constructor exists |
| `tabState_hasGetData` | `getData()` method exists |
| `tabState_staticIcons_notNull` | `getProcedureIcon()`, `getFunctionIcon()`, `getFieldIcon()`, `getConstructorIcon()` are non-null |
| `tabState_hasGetItemSelectionOperationForCode` | Method exists via reflection |
| `tabState_hasRemoveAllOrphans` | `removeAllOrphans()` method exists via reflection |

#### Example

```java
@Test
public void tabState_staticIcons_notNull() {
    assertNotNull(DeclarationTabState.getProcedureIcon());
    assertNotNull(DeclarationTabState.getFunctionIcon());
    assertNotNull(DeclarationTabState.getFieldIcon());
    assertNotNull(DeclarationTabState.getConstructorIcon());
}
```

---

### DeclarationsEditorCompositeTest

**File:** `core/ide/src/test/java/org/alice/ide/declarationseditor/DeclarationsEditorCompositeTest.java`

Tests `DeclarationsEditorComposite`, the top-level composite that owns the
`DeclarationTabState`, `DeclarationMenu`, and `BackwardForwardComposite`.
Because the constructor initializes `DeclarationTabState` which calls
`IDE.DOCUMENT_UI_GROUP`, this class is tested via reflection.

#### Test Methods

| Method | What It Verifies |
|--------|-----------------|
| `composite_classExists` | `Class.forName` succeeds |
| `composite_extendsSimpleComposite` | Class hierarchy: `extends SimpleComposite` |
| `composite_hasDefaultConstructor` | No-arg constructor exists |
| `composite_hasGetTabState` | `getTabState()` method exists |
| `composite_hasGetDeclarationMenu` | `getDeclarationMenu()` method exists |
| `composite_hasGetControlsComposite` | `getControlsComposite()` method exists |
| `composite_hasCreateView` | `createView()` override exists |
| `composite_tabStateFieldType` | Private `tabState` field is of type `DeclarationTabState` |
| `composite_declarationMenuFieldType` | Private `declarationMenu` field is of type `DeclarationMenu` |

#### Example

```java
@Test
public void composite_hasGetTabState() throws Exception {
    Class<?> cls = Class.forName(
        "org.alice.ide.declarationseditor.DeclarationsEditorComposite");
    assertNotNull(cls.getMethod("getTabState"));
}
```

---

### SeparatorClassesTest

**File:** `core/ide/src/test/java/org/alice/ide/declarationseditor/SeparatorClassesTest.java`

Reflection tests for the 6 `LabelMenuSeparatorModel` subclasses used as visual
dividers in the declaration editor's type menu.

#### Classes Tested

| Class | Singleton Pattern |
|-------|------------------|
| `ProceduresSeparator` | `SingletonHolder.instance` + `getInstance()` |
| `FunctionsSeparator` | same |
| `FieldsSeparator` | same |
| `ManagedFieldsSeparator` | same |
| `UnmanagedFieldsSeparator` | same |
| `ClassesSeparator` | same |

#### Test Methods (per class)

| Method Pattern | What It Verifies |
|---------------|-----------------|
| `{name}_classExists` | Class loads via `Class.forName` |
| `{name}_extendsLabelMenuSeparatorModel` | Superclass is `LabelMenuSeparatorModel` |
| `{name}_hasSingletonHolder` | Inner class `SingletonHolder` exists |
| `{name}_hasGetInstance` | Static `getInstance()` method exists |
| `{name}_hasPrivateConstructor` | Constructor is private (singleton enforcement) |
| `{name}_getInstanceReturnsSameReference` | Two calls to `getInstance()` return same object |

#### Example

```java
@Test
public void proceduresSeparator_getInstanceReturnsSameReference() throws Exception {
    Class<?> cls = Class.forName("org.alice.ide.declarationseditor.ProceduresSeparator");
    java.lang.reflect.Method getInstance = cls.getDeclaredMethod("getInstance");
    Object first = getInstance.invoke(null);
    Object second = getInstance.invoke(null);
    assertSame(first, second);
}
```

---

### DeclarationHistoryNavigationTest

**File:** `core/ide/src/test/java/org/alice/ide/declarationseditor/DeclarationHistoryNavigationTest.java`

Reflection tests for the history and navigation subsystem. These classes depend on
`IDE.getActiveInstance()` internally, so only structural verification is performed.

#### Classes Tested

| Class | Type | Notes |
|-------|------|-------|
| `DeclarationCompositeHistory` | Concrete singleton | Manages back/forward history stack |
| `BackwardOperation` | Concrete | Extends `ActionOperation`, navigates backward |
| `ForwardOperation` | Concrete | Extends `ActionOperation`, navigates forward |
| `BackwardCascade` | Concrete | Extends `HistoryCascade` (`CascadeWithInternalBlank`), shows backward choices |
| `ForwardCascade` | Concrete | Extends `HistoryCascade` (`CascadeWithInternalBlank`), shows forward choices |
| `HistoryCascade` | Abstract | Base class for backward/forward cascades, extends `CascadeWithInternalBlank` |

#### Test Methods

| Method | What It Verifies |
|--------|-----------------|
| `history_classExists` | `DeclarationCompositeHistory` loads |
| `history_hasSingleton` | `getInstance()` and `SingletonHolder` exist |
| `history_hasHistoryField` | Private `history` field (List type) exists |
| `history_hasIndexField` | Private `index` field (int type) exists |
| `history_hasAppendMethod` | `appendIfAppropriate` method exists |
| `history_hasResetMethod` | `resetStack` method exists |
| `backwardOperation_classExists` | Class loads |
| `backwardOperation_extendsActionOperation` | Extends Croquet `ActionOperation` |
| `forwardOperation_classExists` | Class loads |
| `forwardOperation_extendsActionOperation` | Extends Croquet `ActionOperation` |
| `backwardCascade_classExists` | Class loads |
| `backwardCascade_extendsHistoryCascade` | Extends `HistoryCascade` |
| `forwardCascade_classExists` | Class loads |
| `forwardCascade_extendsHistoryCascade` | Extends `HistoryCascade` |
| `historyCascade_isAbstract` | `HistoryCascade` has `abstract` modifier |
| `historyCascade_hasPopulateMethod` | Abstract `getList(DeclarationCompositeHistory)` method exists |

---

### DeclarationMenuFillInTest

**File:** `core/ide/src/test/java/org/alice/ide/declarationseditor/DeclarationMenuFillInTest.java`

Reflection tests for menu models, fill-in items, and the field highlight operation.

#### Classes Tested

| Class | Type | Notes |
|-------|------|-------|
| `DeclarationMenu` | Concrete | Extends Croquet menu model |
| `TypeMenu` | Concrete | Type-specific menu, extends `MenuModel` |
| `DeclarationCompositeFillIn` | Concrete | Fill-in item extending `ImmutableCascadeFillIn` |
| `HighlightFieldOperation` | Concrete | Operation to highlight a field in the editor |

#### Test Methods

| Method | What It Verifies |
|--------|-----------------|
| `declarationMenu_classExists` | Class loads |
| `declarationMenu_extendsCroquetMenu` | Superclass is a Croquet menu type |
| `declarationMenu_hasDefaultConstructor` | No-arg constructor exists |
| `typeMenu_classExists` | Class loads |
| `typeMenu_extendsMenuModel` | Superclass is `MenuModel` |
| `typeMenu_hasExpectedConstructor` | Constructor takes `NamedUserType` or similar |
| `fillIn_classExists` | `DeclarationCompositeFillIn` loads |
| `fillIn_isConcrete` | Does NOT have `abstract` modifier |
| `fillIn_extendsImmutableCascadeFillIn` | Extends Croquet `ImmutableCascadeFillIn` |
| `fillIn_hasGetTransientValueMethod` | Core fill-in method exists |
| `highlightField_classExists` | `HighlightFieldOperation` loads |
| `highlightField_extendsOperation` | Extends Croquet `Operation` |
| `highlightField_hasConstructor` | Constructor exists with expected parameter types |

---

## Package: declarationseditor/type

### TypeCompositeHierarchyTest

**File:** `core/ide/src/test/java/org/alice/ide/declarationseditor/type/TypeCompositeHierarchyTest.java`

Reflection tests for the 14 composite classes in the `type` sub-package. These
composites represent the different panels in the type editor (procedures, functions,
fields, constructors, members) and their tool palette wrappers.

#### Composite Classes Tested

| Class | Expected Superclass |
|-------|-------------------|
| `ProceduresComposite` | Composite hierarchy member |
| `FunctionsComposite` | Composite hierarchy member |
| `FieldsComposite` | Composite hierarchy member |
| `ConstructorsComposite` | Composite hierarchy member |
| `MethodsComposite` | Composite hierarchy member |
| `MembersComposite` | Composite hierarchy member |
| `ManagedFieldsComposite` | Composite hierarchy member |
| `UnmanagedFieldsComposite` | Composite hierarchy member |
| `ProceduresToolPaletteCoreComposite` | ToolPalette hierarchy member |
| `FunctionsToolPaletteCoreComposite` | ToolPalette hierarchy member |
| `FieldsToolPaletteCoreComposite` | ToolPalette hierarchy member |
| `ConstructorsToolPaletteCoreComposite` | ToolPalette hierarchy member |
| `MembersToolPaletteCoreComposite` | ToolPalette hierarchy member |
| `MethodsToolPaletteCoreComposite` | ToolPalette hierarchy member |

#### Test Methods (per class pattern)

| Method Pattern | What It Verifies |
|---------------|-----------------|
| `{name}_classExists` | `Class.forName` succeeds |
| `{name}_extendsExpectedParent` | Class hierarchy membership |
| `{name}_isPublic` | Public access modifier |
| `{name}_hasExpectedConstructor` | Constructor signature matches expected parameters |

#### ToolPaletteCoreComposite Extras

The 6 `*ToolPaletteCoreComposite` classes additionally verify:

| Method | What It Verifies |
|--------|-----------------|
| `{name}_hasGetOuterCompositeMethod` | Method to retrieve the owning composite exists |
| `{name}_hasCreateViewMethod` | `createView()` method exists (from Croquet contract) |

---

### TypeStateReflectionTest

**File:** `core/ide/src/test/java/org/alice/ide/declarationseditor/type/TypeStateReflectionTest.java`

Reflection tests for the 11 state classes that manage selection state for different
member categories in the type editor.

#### State Classes Tested

| Class | Abstract? | Notes |
|-------|-----------|-------|
| `FilteredMemberState` | Abstract | Base class for filtered member selection |
| `AbstractManagedFieldState` | Abstract | Base for managed field selection |
| `MethodState` | Concrete | Selected method in methods panel |
| `ProcedureState` | Concrete | Selected procedure |
| `FunctionState` | Concrete | Selected function |
| `FieldState` | Concrete | Selected field |
| `ConstructorState` | Concrete | Selected constructor |
| `ManagedFieldState` | Concrete | Selected managed field |
| `UnmanagedFieldState` | Concrete | Selected unmanaged field |
| `ManagedCameraMarkerFieldState` | Concrete | Camera marker field selection |
| `ManagedObjectMarkerFieldState` | Concrete | Object marker field selection |

#### Test Methods (per class pattern)

| Method Pattern | What It Verifies |
|---------------|-----------------|
| `{name}_classExists` | `Class.forName` succeeds |
| `{name}_extendsExpectedParent` | State hierarchy membership |
| `{name}_isAbstract` / `_isConcrete` | Modifier verification |
| `{name}_hasExpectedConstructor` | Constructor parameter types |

#### Abstract Hierarchy Verification

```java
@Test
public void abstractManagedFieldState_isAbstract() throws ClassNotFoundException {
    Class<?> cls = Class.forName(
        "org.alice.ide.declarationseditor.type.AbstractManagedFieldState");
    assertTrue(Modifier.isAbstract(cls.getModifiers()));
}

@Test
public void managedCameraMarkerFieldState_extendsAbstractManagedFieldState()
    throws ClassNotFoundException {
    Class<?> cls = Class.forName(
        "org.alice.ide.declarationseditor.type.ManagedCameraMarkerFieldState");
    Class<?> parent = Class.forName(
        "org.alice.ide.declarationseditor.type.AbstractManagedFieldState");
    assertTrue(parent.isAssignableFrom(cls));
}
```

---

### TypeMenuModelTest

**File:** `core/ide/src/test/java/org/alice/ide/declarationseditor/type/TypeMenuModelTest.java`

Reflection tests for the 4 menu model classes used in the type editor's
context menus.

#### Classes Tested

| Class | Purpose |
|-------|---------|
| `MethodMenuModel` | Context menu for methods |
| `FieldMenuModel` | Context menu for fields |
| `ConstructorMenuModel` | Context menu for constructors |
| `MemberMenuModel` | Abstract base for all member menus |

#### Test Methods

| Method | What It Verifies |
|--------|-----------------|
| `memberMenuModel_classExists` | Base class loads |
| `memberMenuModel_isAbstractOrBase` | Modifier/hierarchy check |
| `methodMenuModel_classExists` | Concrete class loads |
| `methodMenuModel_extendsMemberMenuModel` | Hierarchy check |
| `methodMenuModel_hasExpectedConstructor` | Constructor signature |
| `fieldMenuModel_classExists` | Concrete class loads |
| `fieldMenuModel_extendsMemberMenuModel` | Hierarchy check |
| `constructorMenuModel_classExists` | Concrete class loads |
| `constructorMenuModel_extendsMemberMenuModel` | Hierarchy check |
| `allMenuModels_arePublic` | All 4 classes are public |

---

## Package: declarationseditor/type/data

### FieldDataTest

**File:** `core/ide/src/test/java/org/alice/ide/declarationseditor/type/data/FieldDataTest.java`

Direct instantiation tests for the field data filtering hierarchy. These classes
filter `UserField` members based on `ManagementLevel` and are testable without
an Application context.

#### Class Hierarchy

```
FilteredMemberData<UserField>  (abstract)
  └── FieldData                (abstract)
        ├── ManagedFieldData   (via AbstractManagedFieldData)
        └── UnmanagedFieldData (concrete — isAcceptableItem: level != MANAGED)
```

#### Test Methods

| Method | What It Verifies |
|--------|-----------------|
| `unmanagedFieldData_constructor` | Constructs with a `NamedUserType` |
| `unmanagedFieldData_getType_returnsConstructorType` | `getType()` round-trips |
| `unmanagedFieldData_acceptsUnmanagedField` | `isAcceptableItem` returns true for `NONE` level |
| `unmanagedFieldData_rejectsManagedField` | `isAcceptableItem` returns false for `MANAGED` level |
| `unmanagedFieldData_acceptsFieldWithNullLevel` | Default management level behavior |
| `fieldData_isAbstract` | `FieldData` has abstract modifier |
| `fieldData_extendsFilteredMemberData` | Hierarchy check |
| `filteredMemberData_isAbstract` | `FilteredMemberData` has abstract modifier |
| `filteredMemberData_hasTypeField` | Private `type` field of type `NamedUserType` |
| `filteredMemberData_getType` | `getType()` method exists |

#### isAcceptableItem Testing Pattern

```java
@Test
public void unmanagedFieldData_acceptsUnmanagedField() {
    NamedUserType type = AstUtilities.createType("TestType_unmgd",
        JavaType.getInstance(SScene.class));
    UserField field = new UserField("myVar", JavaType.INTEGER_OBJECT_TYPE);
    field.managementLevel.setValue(ManagementLevel.NONE);
    type.fields.add(field);

    UnmanagedFieldData data = new UnmanagedFieldData(type);
    // isAcceptableItem is protected — call via reflection from same package
    assertTrue(invokeIsAcceptable(data, field));
}

@Test
public void unmanagedFieldData_rejectsManagedField() {
    NamedUserType type = AstUtilities.createType("TestType_mgd",
        JavaType.getInstance(SScene.class));
    UserField field = new UserField("camera", JavaType.getInstance(SCamera.class));
    field.managementLevel.setValue(ManagementLevel.MANAGED);
    type.fields.add(field);

    UnmanagedFieldData data = new UnmanagedFieldData(type);
    assertFalse(invokeIsAcceptable(data, field));
}
```

---

### ManagedFieldDataTest

**File:** `core/ide/src/test/java/org/alice/ide/declarationseditor/type/data/ManagedFieldDataTest.java`

Direct instantiation tests for the managed field data hierarchy, including the
camera marker and object marker specializations.

#### Class Hierarchy

```
FieldData                          (abstract)
  └── AbstractManagedFieldData     (abstract — isAcceptableItem: level == MANAGED)
        ├── ManagedFieldData       (concrete — no additional filtering)
        ├── ManagedCameraMarkerFieldData  (concrete — camera markers only)
        └── ManagedObjectMarkerFieldData  (concrete — object markers only)
```

#### Test Methods

| Method | What It Verifies |
|--------|-----------------|
| `abstractManagedFieldData_isAbstract` | Abstract modifier check |
| `abstractManagedFieldData_extendsFieldData` | Hierarchy check |
| `managedFieldData_constructor` | Constructs with a `NamedUserType` |
| `managedFieldData_acceptsManagedField` | `isAcceptableItem` returns true for `MANAGED` |
| `managedFieldData_rejectsUnmanagedField` | `isAcceptableItem` returns false for `NONE` |
| `managedFieldData_getType_roundTrips` | `getType()` returns constructor type |
| `cameraMarkerFieldData_classExists` | `ManagedCameraMarkerFieldData` loads |
| `cameraMarkerFieldData_extendsAbstractManaged` | Hierarchy check |
| `cameraMarkerFieldData_constructor` | Constructor takes `NamedUserType` |
| `cameraMarkerFieldData_acceptsManagedField` | Inherits `isAcceptableItem` from abstract parent |
| `objectMarkerFieldData_classExists` | `ManagedObjectMarkerFieldData` loads |
| `objectMarkerFieldData_extendsAbstractManaged` | Hierarchy check |
| `objectMarkerFieldData_constructor` | Constructor takes `NamedUserType` |
| `objectMarkerFieldData_acceptsManagedField` | Inherits `isAcceptableItem` from abstract parent |
| `objectMarkerFieldData_rejectsUnmanagedField` | `NONE` level is rejected |

---

## Test Patterns

### Pattern 1: Reflection Class Structure

Used for GUI-bound classes that cannot be instantiated headlessly.

```java
@Test
public void className_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.codeeditor.CommentPane");
    assertNotNull(cls);
}

@Test
public void className_isPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.codeeditor.CommentPane");
    assertTrue(Modifier.isPublic(cls.getModifiers()));
}
```

### Pattern 2: Singleton Verification

Used for separator classes and `DeclarationCompositeHistory`.

```java
@Test
public void singleton_hasSingletonHolder() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.declarationseditor.ProceduresSeparator");
    boolean found = false;
    for (Class<?> inner : cls.getDeclaredClasses()) {
        if (inner.getSimpleName().equals("SingletonHolder")) {
            found = true;
        }
    }
    assertTrue("Should have SingletonHolder inner class", found);
}
```

### Pattern 3: AST-Backed Behavior

Used for `isAcceptableItem` filtering tests with real `UserField` / `NamedUserType` AST nodes.

```java
@Test
public void behavior_withRealAstNodes() {
    NamedUserType type = AstUtilities.createType("TestType_unique",
        JavaType.getInstance(SScene.class));
    UserField field = new UserField("myVar", JavaType.INTEGER_OBJECT_TYPE);
    field.managementLevel.setValue(ManagementLevel.NONE);
    type.fields.add(field);

    UnmanagedFieldData data = new UnmanagedFieldData(type);
    assertTrue(invokeIsAcceptable(data, field));
}
```

### Pattern 4: Abstract Class Hierarchy

Used for `FilteredMemberState`, `AbstractManagedFieldData`, `HistoryCascade`.

```java
@Test
public void abstractClass_isAbstract() throws ClassNotFoundException {
    Class<?> cls = Class.forName(
        "org.alice.ide.declarationseditor.type.AbstractManagedFieldState");
    assertTrue("Should be abstract", Modifier.isAbstract(cls.getModifiers()));
}

@Test
public void concreteSubclass_extendsAbstractParent() throws ClassNotFoundException {
    Class<?> child = Class.forName(
        "org.alice.ide.declarationseditor.type.ManagedCameraMarkerFieldState");
    Class<?> parent = Class.forName(
        "org.alice.ide.declarationseditor.type.AbstractManagedFieldState");
    assertTrue(parent.isAssignableFrom(child));
}
```

---

## Configuration

### Build Command

```bash
cd core/ide
mvn test -pl . -Dtest="org.alice.ide.codeeditor.*Test,org.alice.ide.declarationseditor.*Test,org.alice.ide.declarationseditor.type.*Test,org.alice.ide.declarationseditor.type.data.*Test" \
  -DfailIfNoTests=false
```

### Run Individual Test

```bash
mvn test -pl core/ide -Dtest=org.alice.ide.codeeditor.StatementListBorderTest
mvn test -pl core/ide -Dtest=org.alice.ide.declarationseditor.type.data.FieldDataTest
```

### Run All 12 New Files

```bash
mvn test -pl core/ide \
  -Dtest="StatementListBorderTest,CodeEditorGUITest,DeclarationTabStateTest,DeclarationsEditorCompositeTest,SeparatorClassesTest,DeclarationHistoryNavigationTest,DeclarationMenuFillInTest,TypeCompositeHierarchyTest,TypeStateReflectionTest,TypeMenuModelTest,FieldDataTest,ManagedFieldDataTest"
```

### Memory Configuration

If running all core/ide tests together, set:

```bash
export MAVEN_OPTS="-Xmx4g"
```

Individual test files use minimal memory (~256MB heap).

### CI Environment

- **Headless:** All tests are CI-safe. No `java.awt.headless=true` flag required
  because no Swing components are instantiated — only `Class.forName` reflection
  and in-memory AST objects.
- **No display:** Tests do not call `SwingUtilities.invokeAndWait` or create
  any `JFrame`/`JPanel` instances.
- **No mocking:** No Mockito or other mocking framework is used. Tests rely on
  real AST objects and reflection, matching the existing 265+ test file pattern.

---

## Troubleshooting

### Common Issues

| Symptom | Cause | Fix |
|---------|-------|-----|
| `ClassNotFoundException` for codeeditor classes | Test classpath missing `core/ide` classes | Ensure `mvn compile` succeeded before `mvn test` |
| `NoSuchMethodException` in reflection tests | Method was renamed or removed in a refactor | Update the expected method name string in the test |
| `AssertionError: singleton returned different instances` | Static field was reset between test runs | Verify no test clears static singleton holders |
| `isAcceptableItem` returns unexpected result | `ManagementLevel` enum has changed | Check `ManagementLevel.MANAGED` and `ManagementLevel.NONE` still exist |

### Static Cache Considerations

`AstUtilities.createType()` and `NamedUserType` constructors may register types in
internal registries. Tests avoid pollution by using unique names per test method:

```java
// GOOD: unique name prevents cache collision
NamedUserType type = AstUtilities.createType("TestType_testSpecificName", ...);

// BAD: reusing names across test methods risks cache hits from prior tests
NamedUserType type = AstUtilities.createType("TestType", ...);
```

### Adding New Tests

When adding tests to these files:

1. **Follow the naming convention:** `{className}_{whatItVerifies}`
2. **Use unique AST node names:** Append the test method name to avoid cache collisions
3. **Prefer reflection for GUI classes:** If a class extends `JPanel`, `JComponent`,
   or any Croquet view type, test via `Class.forName` rather than instantiation
4. **No new dependencies:** Do not add Mockito, PowerMock, or any mocking framework.
   The entire test suite uses real objects and reflection exclusively.
5. **Keep tests independent:** No `@Before`/`@After` cleanup. Each test method is
   self-contained.
