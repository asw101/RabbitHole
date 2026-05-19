# core/story-api, core/model-loading, core/image-editor coverage push — phase 3

Issue #793 raises test coverage across three modules:

- **core/story-api** from ~62% to 70%+ (+2,235 lines)
- **core/model-loading** from ~65% to 70%+ (+177 lines)
- **core/image-editor** from ~64% to 70%+ (+194 lines)

All new tests are JUnit 4, headless-safe, and use hand-rolled stubs (no
Mockito). This push builds on Phase 1 (#751) and Phase 2 (#775) for
story-api, and adds first-time coverage tests for model-loading and
image-editor modules.

## Coverage model

```
                       Baseline        New lines    Projected
core/story-api         ~62%            +2,235       ~70%+
core/model-loading     ~65%            +177         ~70%+
core/image-editor      ~64%            +194         ~70%+
────────────────────────────────────────────────────────────
Total new test lines:  ~2,606
```

## Module 1: core/model-loading (3 files, ~177 lines)

### Test inventory

| Test file | Package | Source classes covered | Lines |
| --- | --- | --- | ---: |
| `ColladaTransformUtilitiesTest` | `o.l.s.resourceutilities` | `ColladaTransformUtilities` — `createFlippedRowMajorTransform`, `createFlippedPoint3DoubleArray`, `createFlippedPoint3FloatArray` | 60 |
| `GltfBufferUtilsTest` | `o.l.s.resourceutilities` | `GltfBufferUtils` — `normalize(FloatBuffer)`, `convertToFloatArray(DoubleBuffer)` | 60 |
| `JavaCodeUtilitiesTest` | `o.l.s.resourceutilities` | `JavaCodeUtilities` — `getCopyrightComment`, `getDirectoryStringForPackage` | 57 |

### Test strategy

**ColladaTransformUtilities** tests validate coordinate system conversion
between Maya (forward=+Z, right=−X, up=+Y) and Alice (forward=−Z,
right=+X, up=+Y). `createFlippedRowMajorTransform` is tested with
identity matrices, known 4×4 rotation matrices, and a translation-only
matrix — output is verified element-by-element against hand-computed
flipped results. `createFlippedPoint3DoubleArray` and
`createFlippedPoint3FloatArray` are tested with axis-aligned unit vectors
and the origin.

**GltfBufferUtils** tests exercise the glTF binary buffer utilities.
`normalize(FloatBuffer)` is tested with zero-length normals, NaN normals,
unit-length, and arbitrary normal vectors — verified against known L2-norm
results within 1e-6 tolerance. `convertToFloatArray(DoubleBuffer)` is
tested with packed `DoubleBuffer` data and null input.

**JavaCodeUtilities** tests verify code generation helpers.
`getCopyrightComment` is tested for non-null, non-empty return and presence
of the copyright year. `getDirectoryStringForPackage` is tested with
dotted package names (`org.lgna.story`) and verified to return the correct
OS-independent path separator form.

> **Note:** `GltfBufferUtils` is package-private. The test file is placed in
> the same package (`org.lgna.story.resourceutilities`) to access it without
> reflection.

### Example: ColladaTransformUtilities

```java
@Test
public void createFlippedRowMajorTransform_identity_flipsCorrectElements() {
    double[] identity = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
    double[] result = ColladaTransformUtilities.createFlippedRowMajorTransform(identity);
    // Identity stays identity after flip (negated elements are already 0)
    assertArrayEquals(identity, result, 1e-12);
}

@Test
public void createFlippedPoint3DoubleArray_unitX_negatesX() {
    double[] unitX = {1.0, 0.0, 0.0};
    double[] flipped = ColladaTransformUtilities.createFlippedPoint3DoubleArray(unitX);
    // Maya→Alice flips X and Z, keeps Y
    assertEquals(-1.0, flipped[0], 1e-12);
    assertEquals(0.0, flipped[1], 1e-12);
    assertEquals(0.0, flipped[2], 1e-12);
}
```

### Configuration

```bash
# Compile-verify
mvn test-compile -pl core/model-loading -am -q

# Run tests
mvn test -pl core/model-loading -am \
  -Dtest="ColladaTransformUtilitiesTest,GltfBufferUtilsTest,JavaCodeUtilitiesTest"
```

---

## Module 2: core/image-editor (3 files, ~194 lines)

### Test inventory

| Test file | Package | Source classes covered | Lines |
| --- | --- | --- | ---: |
| `SaveOperationTest` | `o.a.imageeditor.croquet` | `SaveOperation` — construction, `getOwner`, key interactions | 65 |
| `ImageEditorPaneCoverageTest` | `o.a.imageeditor.croquet` | `ImageEditorPane` — headless construction, basic state | 65 |
| `SaveOverPaneCoverageTest` | `o.a.imageeditor.croquet` | `SaveOverPane` — construction, `getComposite` | 64 |

### Test strategy

All three test files use a headless `ImageEditorFrame` approach to avoid
AWT/Swing display requirements. The pattern chains through the image
editor's composite hierarchy:

```
SaveOperation → ImageEditorFrame (headless) → SaveOverComposite → SaveOverPane
```

**SaveOperation** tests verify that the operation can be constructed with a
headless owner frame, that `getOwner()` returns the expected frame
reference, and that the operation's key binding is correctly configured.

**ImageEditorPaneCoverageTest** creates an `ImageEditorPane` via headless
construction and verifies initial state: null image, default crop bounds,
and correct composite association.

**SaveOverPaneCoverageTest** constructs a `SaveOverPane` through the
`SaveOverComposite` obtained from a headless `ImageEditorFrame`, then
verifies `getComposite()` returns the expected composite and the pane's
initial visibility state.

> **Headless safety:** All three tests run without a display. The
> `ImageEditorFrame` constructor accepts headless mode, and no Swing
> rendering methods are invoked.

### Example: SaveOperation

```java
@Test
public void construction_headlessFrame_succeeds() {
    ImageEditorFrame frame = new ImageEditorFrame();
    SaveOperation op = new SaveOperation(frame);
    assertNotNull(op);
    assertSame(frame, op.getOwner());
}
```

### Configuration

```bash
# Compile-verify
mvn test-compile -pl core/image-editor -am -q

# Run tests
mvn test -pl core/image-editor -am \
  -Dtest="SaveOperationTest,ImageEditorPaneCoverageTest,SaveOverPaneCoverageTest"
```

---

## Module 3: core/story-api (9 files, ~2,235 lines)

### Test inventory

#### Batch 1 — Markers, Axes, and Cameras (~750 lines)

| Test file | Source classes covered | Lines |
| --- | --- | ---: |
| `AxesAndSunImpTest` | `AxesImp` (ExtravagantAxes creation, scale properties, showing/hiding), `SunImp` (@Deprecated — construction, DirectionalLight composition, initial orientation) | 250 |
| `StandInAndTargetImpTest` | `StandInImp` (no-arg construction, vehicle management, release), `TargetImp` (STarget wrapping, getAbstraction) | 250 |
| `MarkerImpCoverageTest` | `ObjectMarkerImp` (setShowing, color paint, marker visual), `OrthographicCameraImp` (no-arg construction, getSgCamera, getAbstraction) | 250 |

#### Batch 2 — Shapes and Text (~780 lines)

| Test file | Source classes covered | Lines |
| --- | --- | ---: |
| `BillboardImpCoverageTest` | `BillboardImp` (front paint via inherited setPaint, backPaint property, getSgPaintAppearances) | 260 |
| `TextModelImpCoverageTest` | `TextModelImp` (append, insert, delete, replace, getValue, font) | 260 |
| `CylinderHierarchyImpTest` | `ConeImp`, `CylinderImp` via `AbstractCylinderImp` hierarchy (radius, length, resize per-axis); `DiscImp` via `ShapeImp` (outerRadius, setSize) | 260 |

#### Batch 3 — Structural and Hierarchy (~705 lines)

| Test file | Source classes covered | Lines |
| --- | --- | ---: |
| `JointedModelSubclassStructureTest` | Reflection tests for 12 implementation classes in 4 hierarchies: **JointedModelImp** (7): `BipedImp`, `FlyerImp`, `QuadrupedImp`, `SlithererImp`, `SwimmerImp`, `TransportImp`, `BasicJointedModelImp`; **CameraImp** (2): `SymmetricPerspectiveCameraImp`, `VrHeadsetImp`; **CameraMarkerImp** (1): `OrthographicCameraMarkerImp`; **TransformableImp** (2): `VrUserImp`, `VrHandImp` | 350 |
| `VisualScaleHierarchyTest` | `SingleVisualModelImp`, `VisualScaleModelImp`, `SimpleModelImp`, `PropertyOwnerImp` via concrete shapes | 200 |
| `ShapeResizeAndScaleTest` | `BoxImp`, `SphereImp`, `TorusImp` — per-axis setValueForResizer, setSize round-trip, geometry type, uniform scale via Resizer.UNIFORM | 155 |

### Test strategies

#### Direct instantiation (Batches 1 & 2)

Shape and marker implementations are testable through their facade
constructors (`new SBox()`, `new SSphere()`, `new SBillboard()`, etc.):

```java
SBox box = new SBox();
BoxImp imp = box.getImplementation();
imp.setValueForResizer(Resizer.X_AXIS, 2.0);
assertEquals(2.0, imp.getValueForResizer(Resizer.X_AXIS), 1e-9);
```

`AxesImp` is constructed via `new SAxes()` — the SAxes constructor
creates the AxesImp internally, which instantiates an `ExtravagantAxes`
scenegraph node. `SunImp` is constructed via `new SSun()` (both SSun and
SunImp are `@Deprecated`), which wraps a `DirectionalLight` and applies
an initial −0.25 revolution X-axis rotation. `TargetImp` is constructed
via `new STarget()`. `StandInImp` is the exception — it has a no-arg
constructor (`new StandInImp()`) and `getAbstraction()` returns null.
All are headless-safe: they create scenegraph nodes but never render them.

#### Facade accessor pattern

`TextModelImp` text operations are accessed through the `STextModel` facade:

```java
STextModel text = new STextModel();
TextModelImp imp = text.getImplementation();
imp.setValue("Hello");
assertEquals("Hello", imp.getValue());
imp.append(" World");
assertEquals("Hello World", imp.getValue());
imp.insert(5, ",");
assertEquals("Hello, World", imp.getValue());
```

#### Reflection-based structural tests (Batch 3)

JointedModel subclasses require `JointImplementationAndVisualDataFactory`
which cannot be instantiated without real model resources. Instead,
`JointedModelSubclassStructureTest` uses reflection to verify structural
properties across 12 implementation classes in 4 hierarchies:

**JointedModelImp subclasses** (7 classes) — verified for:
1. **Class hierarchy** — each extends `JointedModelImp<S, R>`
2. **Constructor signatures** — presence of `(SAbstraction, JointImplementationAndVisualDataFactory<R>)` constructor
3. **Method presence** — required overrides like `getResource()`

**CameraImp subclasses** (2) — `SymmetricPerspectiveCameraImp`, `VrHeadsetImp`:
1. **Class hierarchy** — extends `CameraImp<SymmetricPerspectiveCamera>`
2. **Constructor signatures** — `(SCamera)` or `(String, SVRHeadset, AbstractTransformableImp)` forms

**TransformableImp subclasses** (2) — `VrUserImp`, `VrHandImp`:
1. **Class hierarchy** — extends `TransformableImp`
2. **Constructor signatures** — `(String, SVRUser)` or `(String, SVRHand, AbstractTransformableImp)` forms

**CameraMarkerImp subclass** (1) — `OrthographicCameraMarkerImp`:
1. **Class hierarchy** — extends `CameraMarkerImp`

```java
@Test
public void bipedImp_extendsJointedModelImp() {
    assertTrue(JointedModelImp.class.isAssignableFrom(BipedImp.class));
}

@Test
public void bipedImp_hasRequiredConstructor() throws NoSuchMethodException {
    BipedImp.class.getDeclaredConstructor(
        SBiped.class,
        JointedModelImp.JointImplementationAndVisualDataFactory.class
    );
}

@Test
public void symmetricPerspectiveCameraImp_extendsCameraImp() {
    assertTrue(CameraImp.class.isAssignableFrom(SymmetricPerspectiveCameraImp.class));
}

@Test
public void allJointedModelSubclasses_declareGetResource() {
    for (Class<?> cls : JOINTED_MODEL_SUBCLASSES) {
        boolean found = Arrays.stream(cls.getDeclaredMethods())
            .anyMatch(m -> m.getName().equals("getResource"));
        assertTrue(cls.getSimpleName() + " missing getResource", found);
    }
}
```

#### Shape resize and scale tests

`ShapeResizeAndScaleTest` exercises the per-axis resize methods that were
not covered in Phase 1's `ShapeImpBehaviorTest`:

```java
@Test
public void boxImp_setValueForResizer_xAxis_updatesOnlyXDimension() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    double originalHeight = imp.getValueForResizer(Resizer.Y_AXIS);
    imp.setValueForResizer(Resizer.X_AXIS, 5.0);
    assertEquals(5.0, imp.getValueForResizer(Resizer.X_AXIS), 1e-9);
    assertEquals(originalHeight, imp.getValueForResizer(Resizer.Y_AXIS), 1e-9);
}

@Test
public void sphereImp_radiusProperty_setValue() {
    SSphere sphere = new SSphere();
    SphereImp imp = sphere.getImplementation();
    imp.radius.setValue(3.0);
    assertEquals(3.0, imp.radius.getValue(), 1e-9);
}

@Test
public void torusImp_innerRadius_doesNotAffectOuterRadius() {
    STorus torus = new STorus();
    TorusImp imp = torus.getImplementation();
    double outerBefore = imp.outerRadius.getValue();
    imp.innerRadius.setValue(0.5);
    assertEquals(0.5, imp.innerRadius.getValue(), 1e-9);
    assertEquals(outerBefore, imp.outerRadius.getValue(), 1e-9);
}
```

### Configuration

```bash
# Compile-verify
mvn test-compile -pl core/story-api -am -q

# Run all story-api tests from this push
mvn test -pl core/story-api -am \
  -Dtest="AxesAndSunImpTest,StandInAndTargetImpTest,MarkerImpCoverageTest,\
BillboardImpCoverageTest,TextModelImpCoverageTest,CylinderHierarchyImpTest,\
JointedModelSubclassStructureTest,VisualScaleHierarchyTest,ShapeResizeAndScaleTest"
```

---

## Running all tests

```bash
# Compile all three modules
mvn test-compile -pl core/story-api,core/model-loading,core/image-editor -am -q

# Run all 15 new test files
mvn test -pl core/story-api,core/model-loading,core/image-editor -am -q

# Run with coverage reporting
mvn verify -pl core/story-api,core/model-loading,core/image-editor -am \
  -Pcoverage -q
```

### Memory configuration

Large modules may require increased heap during compilation and test
execution:

```bash
export MAVEN_OPTS="-Xmx4g"
export NODE_OPTIONS="--max-old-space-size=32768"
```

### Environment requirements

- **Java**: 17+ (project baseline)
- **Maven**: 3.9+ (project baseline)
- **OpenGL**: Not required — all tests are headless-safe
- **Display**: Not required — no AWT or Swing rendering in test paths

### Submodule prerequisite

Before running tests, ensure the Tweedle grammar submodule is initialized
(required for downstream compilation):

```sh
git submodule update --init tweedle-lang
```

---

## Coverage arithmetic

```
Module              Total lines   Baseline covered   New lines   Projected covered   Projected %
core/story-api      ~15,800       ~9,800 (62%)       +2,235      ~12,035             ~76%
core/model-loading  ~1,180        ~767 (65%)          +177       ~944                ~80%
core/image-editor   ~1,290        ~826 (64%)          +194       ~1,020              ~79%
──────────────────────────────────────────────────────────────────────────────────────────────
Total new lines:    ~2,606
```

Note: Projected percentages exceed 70% to provide margin. Transitive
coverage from constructor chains, property initializers, and static
blocks contributes additional lines not counted in the direct estimates.

---

## Test file locations

```
core/model-loading/src/test/java/org/lgna/story/resourceutilities/
├── ColladaTransformUtilitiesTest.java
├── GltfBufferUtilsTest.java
└── JavaCodeUtilitiesTest.java

core/image-editor/src/test/java/org/alice/imageeditor/croquet/
├── SaveOperationTest.java
├── ImageEditorPaneCoverageTest.java
└── SaveOverPaneCoverageTest.java

core/story-api/src/test/java/org/lgna/story/implementation/
├── AxesAndSunImpTest.java
├── StandInAndTargetImpTest.java
├── MarkerImpCoverageTest.java
├── BillboardImpCoverageTest.java
├── TextModelImpCoverageTest.java
├── CylinderHierarchyImpTest.java
├── JointedModelSubclassStructureTest.java
├── VisualScaleHierarchyTest.java
└── ShapeResizeAndScaleTest.java
```

## Overlap with existing tests

| Existing test | New test | Distinction |
| --- | --- | --- |
| `ShapeImpBehaviorTest` | `ShapeResizeAndScaleTest` | Existing covers basic construction and opacity. New covers per-axis setValueForResizer, setSize round-trip, geometry type assertions. |
| `EntityImpBehaviorTest` | `StandInAndTargetImpTest` | Existing covers EntityImp contract (name, vehicle, registry). New covers StandInImp vehicle management/release and TargetImp wrapping. |
| `MoreImplBehaviorTest` | `BillboardImpCoverageTest`, `TextModelImpCoverageTest` | Existing covers basic construction and getValue. New covers text mutation ops (append, insert, delete, replace) and back-paint handling. |
| `EntityImpStructureTest` | `JointedModelSubclassStructureTest` | Existing covers EntityImp hierarchy. New covers 12 implementation classes across JointedModelImp, CameraImp, CameraMarkerImp, and TransformableImp hierarchies via reflection. |

## Excluded source code

| Class/Package | Reason |
| --- | --- |
| `JointedModelImp` instance methods (walk, pose) | Requires `JointImplementationAndVisualDataFactory` with real model resources |
| `org.lgna.story.implementation.eventhandling.*` | Event dispatch requires `ProgramImp` with render target |
| `org.lgna.story.implementation.overlay.*` | Overlay rendering requires GL context |
| `org.alice.interact.manipulator.Camera*` (full drag) | Requires `OnscreenRenderTarget` for pick ray |
| `SaveOperation.fire()` / actual save pipeline | Requires initialized Croquet application with document IO |
| `GltfBufferUtils.convertToFloatArray` edge cases | `DoubleBuffer` with non-triple-aligned remaining count causes `BufferUnderflowException` — not a testable code path |

## Risks and mitigations

| Risk | Mitigation |
| --- | --- |
| `GltfBufferUtils` is package-private | Test file placed in same package |
| `SSun` and `SunImp` are `@Deprecated` | Tests document and exercise deprecated API to maintain coverage during deprecation period |
| `AxesImp` constructor creates `ExtravagantAxes` (scenegraph) | Constructor is headless-safe; no GL context required |
| `ObjectMarkerImp.setShowing` may fail without visual init | `ObjectMarkerImp` initializes visuals in constructor |
| `SaveOverPane` requires composite chain | Chain through headless `ImageEditorFrame` |
| `JavaCodeUtilities.getCopyrightComment` caches in static | Test ordering is irrelevant — first call populates cache |
| JointedModel subclasses need model resource factories | Use reflection structural tests only |

## Headless safety

All 15 test files are headless-safe:

- **No AWT display** — no `Frame`, `Window`, or `Dialog` creation
- **No rendering** — scenegraph nodes are created but never painted
- **No `SwingUtilities.invokeAndWait`** — no EDT dependency
- **No OpenGL context** — no GL calls
- **No file I/O** — all assertions are in-memory
- **No network** — no HTTP/socket calls

Tests can run in CI environments with `java.awt.headless=true`.
