# core/croquet coverage push — 30.4% → 50%

Issue #775 raises `core/croquet` line coverage from 30.4% to 50%+ by adding
27 test files across triggers, cascade runtime internals, history steps,
preferences, codec/icon/data/meta helpers, state/model logic, and composite
infrastructure. All new tests are JUnit 4, headless-safe, and run without an
`Application` context or visible Swing display.

This push builds on the earlier work that brought coverage from 8.54% to
~30.4%. The incremental work targets the trigger class hierarchy (21 classes
with zero test coverage), the cascade runtime tree (`RtNode`/`RtItem`/`RtBlank`),
history steps, and the preferences subsystem — all testable headlessly with
the established listener-removal pattern.

## Coverage model

```
Baseline covered:  ~3,700 lines  (30.4% of ~12,170 total)
New direct:       +1,600 lines
New transitive:   +1,200 lines   (constructor chains, Group machinery, codec paths)
────────────────────────────────
Projected covered: ~6,500 lines  (~53.4%)
```

## Test inventory

### Tier 1 — Trigger class hierarchy (6 files, ~500 lines)

The `org.lgna.croquet.triggers` package contains 21 trigger classes — all
with zero test coverage. Most extend `Trigger` and wrap an AWT/Swing event
plus source metadata. The tests construct each trigger with `null` source
and synthetic events, validating accessor methods, equality semantics, and
factory methods where applicable.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `TriggerTest` | `Trigger` (abstract base), `NullTrigger` | 100 |
| `EventObjectTriggerTest` | `EventObjectTrigger`, `ActionEventTrigger`, `ChangeEventTrigger`, `ItemEventTrigger`, `TreeSelectionEventTrigger`, `PropertyChangeEventTrigger` | 120 |
| `InputEventTriggerTest` | `InputEventTrigger`, `KeyEventTrigger`, `MouseEventTrigger`, `AbstractMouseEventTrigger` | 80 |
| `ComponentEventTriggerTest` | `ComponentEventTrigger`, `WindowEventTrigger`, `AppleApplicationEventTrigger` | 60 |
| `DocumentEventTriggerTest` | `DocumentEventTrigger`, `IterationTrigger` | 40 |
| `MiscTriggerTest` | `DropTrigger`, `DragTrigger`, `PopupMenuEventTrigger`, `CascadeAutomaticDeterminationTrigger` | 100 |

**Test strategy:** All trigger classes store event data accessible via
getters. The constructor takes an AWT event and optional source component.
Tests use `null` source and synthetic events (e.g., `new ActionEvent(new Object(), 0, "test")`).
The `Trigger` abstract class is tested via `NullTrigger.getInstance()`.

**Headless safety:** Trigger classes do not create Swing components or touch
`Application.getActiveInstance()`. They are pure data holders.

### Tier 2 — Cascade runtime internals (6 files, ~400 lines)

The `org.lgna.croquet.imp.cascade` package implements the runtime tree for
cascade (menu) operations. These classes manage fill-in items, blank nodes,
and separator nodes — all testable without a running cascade UI.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `RtNodeTest` | `RtNode` (abstract base, tree structure) | 80 |
| `RtItemTest` | `RtItem`, `CascadeFillIn` (minimal concrete subclass) | 80 |
| `RtBlankTest` | `RtBlank` (blank node lifecycle) | 60 |
| `RtRootTest` | `RtRoot` (root node construction, child management) | 60 |
| `RtSeparatorTest` | `RtSeparator` (separator node) | 40 |
| `BlankNodeTest` | `BlankNode` (fill-in selection state) | 80 |

**Test strategy:** Create anonymous `CascadeFillIn` subclasses with minimal
implementations. `RtNode` tree operations (`setParent()`, `getParent()`,
`getNextSibling()`, `updateParentsAndNextSiblings()`) are pure data operations.

### Tier 3 — History steps (5 files, ~350 lines)

The `org.lgna.croquet.history` package records user interaction steps. Each
step type holds data about an operation (drag, menu selection, etc.) and
can be tested independently of the UI that created it.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `DragStepTest` | `DragStep`, `AbstractStep` | 80 |
| `MenuSelectionTest` | `MenuSelection`, `MenuItemPrepStep` | 60 |
| `PrepStepTest` | `PrepStep` (preparation step lifecycle) | 60 |
| `UserActivityDeepTest` | `UserActivity` (step accumulation, completion) | 80 |
| `MenuItemSelectStepTest` | `MenuItemSelectStep` (selection recording) | 70 |

**Test strategy:** Steps are constructed with `UserActivity` stubs and mock
`CompletionModel` instances. Each step records its model and completion
state without touching Swing or Application.

### Tier 4 — Preferences (3 files, ~200 lines)

The `org.lgna.croquet.preferences` package wraps `java.util.prefs.Preferences`
with type-safe state wrappers.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `PreferenceManagerTest` | `PreferenceManager` (registry, lookup) | 80 |
| `PreferenceStringStateTest` | `PreferenceStringState` (persistence round-trip) | 60 |
| `PreferencesManagerTest` | `PreferencesManager` (bulk operations) | 60 |

**Test strategy:** Tests use `Preferences.userRoot().node("test-" + UUID)`
to avoid collisions with production preferences. Cleanup removes the test
node in `@After`.

### Tier 5 — Codec, icon, data, and meta helpers (4 files, ~250 lines)

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `SimpleTabCompositeCodecTest` | `SimpleTabCompositeCodec` (encode/decode round-trip) | 60 |
| `IconFactoryTest` | `IconFactory`, `EmptyIconFactory`, `AsymmetricIconFactory` | 80 |
| `MutableListDataDeepTest` | `MutableListData` deep paths (bulk ops, listener interplay) | 60 |
| `StateTrackingMetaStateTest` | `StateTrackingMetaState` (meta-state tracking) | 50 |

**Test strategy:** Codecs are tested with `ByteArrayOutputStream`/
`ByteArrayInputStream` round-trips. `IconFactory` tests validate icon
dimensions and null-safety of `getIcon(Dimension)`.

### Tier 6 — State and model deep coverage (6 files, ~400 lines)

These tests deepen coverage of existing state and model classes beyond the
Phase 1 suite (which tested basic get/set/codec). The focus is on branch
coverage in edge cases: null values, boundary clamping, event suppression,
and disabled-state behavior.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `StringStateDeepTest` | `StringState` (null, empty, Unicode, document sync edge cases) | 80 |
| `ItemStateTest` | `ItemState` (item selection, codec, enabled toggle) | 60 |
| `AbstractElementDeepTest` | `AbstractElement` (ID, name, localization, group lookup) | 60 |
| `CascadeItemTest` | `CascadeItem`, `CascadeFillIn` (item selection pipeline) | 60 |
| `AbstractMenuModelTest` | `AbstractMenuModel` (menu item registration) | 60 |
| `ImporterTest` | `Importer` (file filter, selection state) | 80 |

**Test strategy:** Uses the established listener-removal pattern from
`core/croquet/TESTING.md`. Deep tests use descriptive method names like
`setValue_withNull_returnsNull()` to document exact edge-case behavior.

### Tier 7 — Composite and imp deep coverage (4 files, ~300 lines)

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `WizardDialogLogicTest` | `WizardDialogCoreComposite` page logic (navigation, validation) | 80 |
| `BooleanStateMenuModelTest` | `BooleanStateMenuModel` (menu-to-state binding) | 60 |
| `SingleSelectListStateMenuTest` | `SingleSelectListState` menu integration paths | 80 |
| `FrameIsShowingStateTest` | `IsShowingState` (frame visibility tracking) | 80 |

**Test strategy:** Composite tests use anonymous subclasses with no-op
implementations for abstract view methods. The focus is on the model/logic
layer — no JPanel or JFrame creation.

## Common test patterns

### Trigger construction

```java
@Test
public void actionEventTrigger_getEvent_returnsWrappedEvent() {
  ActionEvent ae = new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, "test");
  ActionEventTrigger trigger = new ActionEventTrigger(ae);
  assertSame(ae, trigger.getEvent());
}
```

### Cascade runtime tree

```java
@Test
public void rtNode_setParent_linksNodes() {
  RtRoot<String> root = new RtRoot<>(cascade);
  RtItem<String> item = new RtItem<>(fillIn, node);
  item.setParent(root);
  assertSame(root, item.getParent());
}
```

### History step data

```java
@Test
public void dragStep_getModel_returnsConstructorArg() {
  DragStep step = new DragStep(userActivity, dragModel);
  assertSame(dragModel, step.getModel());
}
```

### Preferences isolation

```java
private Preferences testNode;

@Before
public void setUp() {
  testNode = Preferences.userRoot().node("croquet-test-" + UUID.randomUUID());
}

@After
public void tearDown() throws Exception {
  testNode.removeNode();
}
```

## Exclusions

These classes are **not tested** in this sprint due to hard `Application`
dependencies:

| Class | Reason |
| --- | --- |
| `AbstractComposite` | `getView()` requires Application context |
| `AbstractWindow` | Creates `JFrame` peer |
| `Operation` subclasses | Fire through Application event dispatch |
| `ToolBarComposite` | Swing toolbar construction |
| `DragComponent` | AWT drag-and-drop machinery |

Coverage for these classes remains at baseline and will require an
Application test harness in a future sprint.

## Verification

```bash
# Run all core/croquet tests
mvn test -pl core/croquet -Djava.awt.headless=true

# Run only the new trigger tests
mvn test -pl core/croquet \
  -Dtest="org.lgna.croquet.triggers.*Test" \
  -Djava.awt.headless=true

# Generate coverage report
mvn verify -pl core/croquet -Djava.awt.headless=true
# Open core/croquet/target/site/jacoco/index.html

# Full aggregate verification
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --min-module-line-percent core/croquet=45.0
```

## Risks and mitigations

| Risk | Severity | Mitigation |
| --- | --- | --- |
| Cascade `RtItem` needs `CascadeItem` abstract class | HIGH | Use concrete `CascadeFillIn` or anonymous subclass with minimal stubs |
| `Application.getActiveInstance()` blocks composite tests | HIGH | Focus on static methods, data classes, and model-layer logic only |
| Abstract classes need anonymous subclass stubs | MEDIUM | Create minimal inner `TestXxx` classes following established pattern |
| Constructor side effects (listener registration) | MEDIUM | Use listener-removal pattern from `CroquetTestUtils` |
| Build time increase | LOW | Unit tests run fast (~5s per class) |
