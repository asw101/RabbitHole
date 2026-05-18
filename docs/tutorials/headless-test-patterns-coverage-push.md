# Headless test patterns for the Issue #775 coverage push

This tutorial walks through the key test patterns used across the 84 test
files added by Issue #775. Each pattern solves a specific headless testing
challenge in the Alice codebase.

## Who is this for?

Contributors adding tests to `core/croquet`, `core/util`, or
`core/story-api` who need to test classes that have AWT, Swing, or
Application dependencies without a display server.

## Pattern 1: Swing listener removal for croquet states

### The problem

Every croquet `State` subclass registers a Swing listener during
construction. When `setValueTransactionlessly()` fires, these listeners
call `Application.getActiveInstance()` — which is `null` in tests.

### The solution

Remove listeners in `@Before`, before any value mutation:

```java
@Before
public void setUp() {
  state = new TestStringState(TEST_GROUP, "initial");
  removeDocumentListeners(state);
}

private static void removeDocumentListeners(StringState s) {
  Document doc = s.getSwingModel().getDocument();
  for (DocumentListener l : ((AbstractDocument) doc).getDocumentListeners()) {
    doc.removeDocumentListener(l);
  }
}
```

### Why it works

The listeners are the only bridge between the state model and the
Application singleton. Once removed, the state behaves as a pure value
holder.

### Variants

| State type | Model | Listener | Removal API |
| --- | --- | --- | --- |
| `StringState` | `PlainDocument` | `DocumentListener` | `doc.removeDocumentListener()` |
| `BooleanState` | `ToggleButtonModel` | `ItemListener` | `buttonModel.removeItemListener()` |
| `BoundedIntegerState` | `SpinnerNumberModel` | `ChangeListener` | `spinnerModel.removeChangeListener()` |
| `BoundedDoubleState` | `SpinnerNumberModel` | `ChangeListener` | `spinnerModel.removeChangeListener()` |
| `SingleSelectListState` | `ListSelectionModel` | `ListSelectionListener` | `listModel.removeListSelectionListener()` |

## Pattern 2: Concrete subclass stubs for abstract classes

### The problem

Most croquet and story-api classes are abstract. You cannot instantiate
them directly.

### The solution

Define a minimal inner class that implements only the abstract methods:

```java
static class TestStringState extends StringState {
  TestStringState(Group group, String initialValue) {
    super(group, UUID.randomUUID(), initialValue);
  }

  @Override
  protected Class<?> getClassUsedForLocalization() {
    return TestStringState.class;
  }

  @Override
  protected String getSubKeyForLocalization() {
    return "test";
  }
}
```

### Key rules

1. **Use `UUID.randomUUID()`** in constructors to avoid Group registration
   collisions between tests.
2. **Override only abstract methods** — don't add behavior.
3. **Make the class `static`** to avoid holding a reference to the test
   instance.

## Pattern 3: AWT data-only testing

### The problem

AWT classes like `Rectangle`, `Dimension`, `Font`, and `Area` sound
display-dependent, but many are pure data containers.

### The solution

Test utility methods that operate on AWT data types without creating
display peers:

```java
@Test
public void grow_symmetricPad_expandsBothAxes() {
  Rectangle r = new Rectangle(10, 10, 20, 20);
  Rectangle result = RectangleUtilities.grow(r, 5);
  assertEquals(new Rectangle(5, 5, 30, 30), result);
}
```

### What's safe headlessly

| Safe | Unsafe |
| --- | --- |
| `Rectangle`, `Point`, `Dimension` | `JFrame`, `JPanel`, `JButton` |
| `Font` (construction, metrics query) | `Graphics.drawString()` on screen |
| `Area`, `GeneralPath`, `AffineTransform` | `Component.paint()` |
| `BufferedImage` (TYPE_INT_ARGB) | `Toolkit.getDefaultToolkit()` |
| `MouseEvent` with `null` source | `SwingUtilities.invokeLater()` with UI |

## Pattern 4: Synthetic event construction

### The problem

Interact conditions, triggers, and input state handlers consume AWT events
(`MouseEvent`, `KeyEvent`, `ActionEvent`). Creating real events requires
a Component source.

### The solution

Pass `null` or a synthetic `Object` as the event source:

```java
// Trigger with null source — stores event data only
ActionEvent ae = new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, "test");
ActionEventTrigger trigger = new ActionEventTrigger(ae);
assertSame(ae, trigger.getEvent());

// InputState with correct API
InputState state = new InputState();
state.setMouseState(MouseEvent.BUTTON1, true);
state.setIsDragEvent(true);
assertTrue(state.getIsDragEvent());
```

### Why `new Canvas()` works headlessly

`java.awt.Canvas` can be constructed in headless mode — it does not create
a native peer until `addNotify()` is called. Alternatively, use
`new java.awt.Component() {}` as a lightweight source.

## Pattern 5: IK solver pure-math testing

### The problem

The IK solver (`org.lgna.ik.core.solver`) operates on bone chains that
normally come from `JointImp` objects (which require a scenegraph).

### The solution

Test the math layer directly — `Bone.Axis` axis vector operations,
constraint data classes, and weight vectors:

```java
@Test
public void axis_invertDirection_negatesVector() {
  Bone bone = createTestBone();
  Bone.Axis axis = new Bone.Axis(bone, 0);
  axis.setCurrentValue(new Vector3(1, 0, 0));
  axis.invertDirection();
  Vector3 result = axis.getCurrentValue();
  assertEquals(-1.0, result.x, 1e-10);
  assertEquals(0.0, result.y, 1e-10);
  assertEquals(0.0, result.z, 1e-10);
}
```

For `Solver` integration tests, construct synthetic `Bone` chains using
reflection to bypass the `JointImp` dependency:

```java
@Test
public void axis_updateLinearContributions_computesCrossProduct() {
  Bone bone = createTestBone();
  Bone.Axis axis = new Bone.Axis(bone, 0);
  axis.setCurrentValue(new Vector3(0, 0, 1));  // Z axis
  Vector3 jointToEE = new Vector3(1, 0, 0);    // X direction
  axis.updateLinearContributions(jointToEE);
  Vector3 contrib = axis.getLinearContribution();
  // Z cross X = -Y
  assertEquals(0.0, contrib.x, 1e-10);
  assertEquals(-1.0, contrib.y, 1e-10);
  assertEquals(0.0, contrib.z, 1e-10);
}
```

## Pattern 6: In-memory binary codec round-trips

### The problem

Binary codecs read/write to streams. Edge cases (NaN, empty arrays, null
strings) need systematic coverage.

### The solution

Write to `ByteArrayOutputStream`, read back from `ByteArrayInputStream`:

```java
@Test
public void roundTrip_nanDouble_preservesNaN() {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  try (OutputStreamBinaryEncoder enc = new OutputStreamBinaryEncoder(baos)) {
    enc.encode(Double.NaN);
  }
  try (InputStreamBinaryDecoder dec =
      new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()))) {
    assertTrue(Double.isNaN(dec.decodeDouble()));
  }
}

@Test
public void roundTrip_emptyIntArray_returnsEmptyArray() {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  try (OutputStreamBinaryEncoder enc = new OutputStreamBinaryEncoder(baos)) {
    enc.encode(new int[0]);
  }
  try (InputStreamBinaryDecoder dec =
      new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()))) {
    int[] result = dec.decodeIntArray();
    assertEquals(0, result.length);
  }
}
```

## Pattern 7: Preferences test isolation

### The problem

Preferences tests must not pollute the user's actual preferences store.

### The solution

Use a UUID-namespaced test node, cleaned up in `@After`:

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

@Test
public void preferenceStringState_putAndGet_roundTrips() {
  testNode.put("key", "value");
  assertEquals("value", testNode.get("key", null));
}
```

## Pattern 8: Temporary file system for resource tests

### The problem

`StorytellingResources`, `DynamicResource`, and `AliceResourceUtilities`
interact with the file system.

### The solution

Use JUnit 4's `TemporaryFolder` rule:

```java
@Rule
public TemporaryFolder tempFolder = new TemporaryFolder();

@Test
public void dynamicResource_saveAndLoad_roundTrips() throws Exception {
  File dir = tempFolder.newFolder("resources");
  DynamicResource resource = createTestResource();
  resource.saveTo(dir);
  DynamicResource loaded = DynamicResource.loadFrom(dir);
  assertEquals(resource.getName(), loaded.getName());
}
```

## Pattern 9: Animation test doubles

### The problem

Animation classes have abstract lifecycle methods (`prologue()`,
`update()`, `epilogue()`) and depend on a clock source.

### The solution

Create recording test doubles:

```java
static class TestAnimation extends DurationBasedAnimation {
  final List<String> events = new ArrayList<>();
  double lastPortion = -1;

  TestAnimation(double duration) {
    super(duration);
  }

  @Override protected void prologue() { events.add("prologue"); }
  @Override protected void setPortion(double portion) {
    lastPortion = portion;
    events.add("update:" + portion);
  }
  @Override protected void epilogue() { events.add("epilogue"); }
}

@Test
public void durationAnimation_fullLifecycle_callsInOrder() {
  TestAnimation anim = new TestAnimation(1.0);
  anim.update(0.0);  // prologue
  anim.update(0.5);  // update
  anim.update(1.0);  // epilogue
  assertEquals(Arrays.asList("prologue", "update:0.5", "epilogue"), anim.events);
}
```

## Summary of patterns by module

| Pattern | core/croquet | core/util | core/story-api |
| --- | :---: | :---: | :---: |
| Listener removal | ✓ | | |
| Concrete stubs | ✓ | | ✓ |
| AWT data-only | | ✓ | |
| Synthetic events | ✓ | | ✓ |
| Pure-math testing | | | ✓ |
| Binary codec round-trip | | ✓ | |
| Preferences isolation | ✓ | | |
| Temporary filesystem | | | ✓ |
| Animation test doubles | | ✓ | |

## Next steps

- For the full test inventory, see the reference documentation:
  - [core/croquet coverage push](../reference/core-croquet-coverage-push.md)
  - [core/util coverage push phase 3](../reference/core-util-coverage-push-phase3.md)
  - [core/story-api coverage push phase 2](../reference/core-story-api-coverage-push-phase2.md)
- For running the tests, see [How to run Issue #775 coverage push](../howto/run-issue-775-coverage-push.md).
- For the coverage ratchet policy, see [Coverage reporting](../reference/coverage-reporting.md).
