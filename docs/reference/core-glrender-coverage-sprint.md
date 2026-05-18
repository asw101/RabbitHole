# core/glrender coverage sprint — 4.6% → 30%

Issue #762 targets raising `core/glrender` line coverage from 4.6% to 30%+ by
adding test files for selection buffer z-math, geometry intersection,
camera projection matrices, mesh pre-processing, and curve renderer constants.
All new tests are JUnit 4, headless-safe, and require no OpenGL context — they
exercise only the pure-math and state-management paths that are decoupled from
`GL2` API calls.

This sprint builds on 21 existing test files that already cover
`RenderContext` opacity stacks, `Context` scaled-count logic,
`PickParameters` data accessors, `Pixels` lifecycle, `AdapterFactory`
registration, `Graphics2D` matrix properties, and several extraction-contract
tests. The incremental work targets geometry intersection (box, cylinder),
camera projection math (symmetric perspective, orthographic), selection buffer
z-buffer conversion, weighted mesh reset, and curve renderer constants — all
pure-logic code paths with zero GL dependencies.

## Test inventory

### Tier 1 — Selection buffer z-math (~90 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `SelectionBufferInfoTest` | `e.c.c.d.render.gl.imp` | `SelectionBufferInfo` z-buffer int→float conversion, `updatePointInSource(Matrix4x4)`, accessors | 90 |

The `SelectionBufferInfo` class parses an `IntBuffer` of OpenGL selection
records and provides z-buffer depth conversion from unsigned 32-bit integers to
`[0.0, 1.0]` floats. Tests construct `IntBuffer` records with known z-front
and z-back values (including boundary cases: `0`, `Integer.MAX_VALUE`,
`0xFFFFFFFF`) and verify the float conversion math. The
`updatePointInSource(Matrix4x4)` method is tested with identity and scaled
matrices to verify point-in-source coordinate computation.

Test doubles: None — `SelectionBufferInfo` is constructed directly from
an `IntBuffer` with `nameCount=0` (skips visual adapter lookup), so no GL
context or adapter registry is needed.

#### Example: z-buffer conversion test

```java
@Test
public void getZFront_maxUnsignedInt_returnsOne() {
    IntBuffer buffer = IntBuffer.wrap(new int[]{0, 0xFFFFFFFF, 0xFFFFFFFF});
    SelectionBufferInfo info = new SelectionBufferInfo(pickContext, buffer, 0);
    assertEquals(1.0f, info.getZFront(), 1e-6f);
}
```

### Tier 2 — Geometry intersection math (~230 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `GlrBoxTest` | `e.c.c.d.render.gl.imp.adapters` | `GlrBox.getIntersectionInSource()` for 6 axis-aligned faces, default extents, static normal/texCoord maps | 130 |
| `GlrCylinderTest` | `e.c.c.d.render.gl.imp.adapters` | `GlrCylinder.getIntersectionInSource()` for 3 origin alignments (TOP, CENTER, BOTTOM), cap handling | 100 |

Box intersection tests fire rays along each of the ±X, ±Y, ±Z axes and verify
the returned intersection point, surface normal, and (u,v) texture coordinate.
Cylinder tests cover the three `OriginAlignment` values and both capped and
uncapped configurations. Both test classes use reflection to set the private
geometry fields (`xMin/xMax/yMin/yMax/zMin/zMax` for box;
`length/bottomRadius/topRadius/originAlignment/bottomToTopAxis` for cylinder)
and inject an identity `AffineMatrix4x4` as the `absoluteTransformation`.

#### Example: box intersection test

```java
@Test
public void getIntersectionInSource_rayAlongNegativeZ_hitsPositiveZFace() {
    setBoxExtents(-0.5, 0.5, -0.5, 0.5, -0.5, 0.5);
    Ray ray = new Ray(new Point3(0, 0, 5), new Vector3(0, 0, -1));
    Point3 intersection = box.getIntersectionInSource(ray, identity, 5);
    assertNotNull(intersection);
    assertEquals(0.5, intersection.z(), 1e-9);
}
```

### Tier 3 — Camera projection math (~210 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `GlrSymmetricPerspectiveCameraTest` | `e.c.c.d.render.gl.imp.adapters` | `getActualProjectionMatrix`, `getRayAtViewportPixel`, `performLetterboxing`, near/far/aspect/angle properties | 130 |
| `GlrOrthographicCameraTest` | `e.c.c.d.render.gl.imp.adapters` | `getActualProjectionMatrix`, `getRayAtViewportPixel`, `performLetterboxing`, `getActualPicturePlane` | 80 |

Camera tests construct real scenegraph camera instances
(`SymmetricPerspectiveCamera`, `OrthographicCamera`), set property values
(field-of-view angle, near/far planes, picture plane dimensions), create the
corresponding GL adapter, call `adapter.initialize(camera)`, and trigger
`propertyChanged` to copy properties to local fields. Projection matrix results
are verified against hand-computed reference values.

#### Example: perspective projection test

```java
@Test
public void getActualProjectionMatrix_90degFov_squareViewport_hasUnitFocalLength() {
    camera.verticalViewingAngle.setValue(new AngleInRadians(Math.PI / 2));
    triggerPropertyChanged("verticalViewingAngle");
    Matrix4x4 proj = adapter.getActualProjectionMatrix(
        new Rectangle(0, 0, 800, 800));
    // f = 1/tan(π/4) = 1.0; e22 holds the focal length
    assertEquals(1.0, proj.e22(), 1e-6);
}
```

#### Example: orthographic projection test

```java
@Test
public void getActualProjectionMatrix_unitPicturePlane_correctScale() {
    camera.picturePlane.setValue(
        new ClippedZPlane(1.0, 1.0));
    triggerPropertyChanged("picturePlane");
    Matrix4x4 proj = adapter.getActualProjectionMatrix(
        new Rectangle(0, 0, 400, 400));
    // 2 / (right - left) with unit plane = 1.0; e11 holds horizontal scale
    assertEquals(1.0, proj.e11(), 1e-6);
}
```

### Tier 4 — Curve renderer and weighted mesh (~55 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `CurveRendererTest` | `e.c.c.d.render.gl.imp` | `CurveRenderer` static constants (`STACK_COUNT`, `SLICE_COUNT`, `SIDE_COUNT`, `RING_COUNT`, `TAU`), constructor | 15 |
| `WeightedMeshControlTest` | `e.c.c.d.render.gl.imp.adapters` | `WeightedMeshControl.preProcess()` reset of joint matrices and weights arrays | 40 |

The curve renderer test validates known constants via reflection, ensuring
rendering fidelity is preserved across refactors. The weighted mesh control
test uses reflection to set up `weightedJointMatrices[]` and `weights[]`
arrays with non-default values, calls `preProcess()`, and verifies all
matrices are reset to `AffineMatrix4x4.NaN` and weights to `0f`.

#### Example: weighted mesh preProcess test

```java
@Test
public void preProcess_resetsWeightsToZero() throws Exception {
    float[] weights = new float[]{1.0f, 0.5f, 0.25f};
    setField(control, "weights", weights);
    control.preProcess();
    for (float w : weights) {
        assertEquals(0f, w, 0f);
    }
}
```

## Test style and conventions

All tests use JUnit 4 (`org.junit.Test`, `org.junit.Assert`) matching the
existing `core/glrender` test style.

Key conventions:

- **Reflection for private fields** — geometry and camera tests use
  `Field.setAccessible(true)` to inject test values into private fields.
  This matches the pattern used by 28+ existing tests in the codebase.
- **No GL context** — every test method exercises code paths that do not
  call `gl.glXxx()` methods. Methods that mix math with GL calls are excluded.
- **No Mockito** — not available in the project dependency tree. All test
  doubles are hand-rolled inner classes or direct object construction.
- **Epsilon comparisons** — all floating-point assertions use
  `assertEquals(expected, actual, 1e-6)` or tighter tolerances as appropriate.
- **`@After` cleanup** — `AdapterFactory.forgetAllElements()` is called in
  `@After` for any test that registers adapters, preventing cross-test state
  pollution.
- **Descriptive method names** — `methodUnderTest_condition_expectedResult`.
- **No file I/O** — all test fixtures are built in-memory.
- **Package-private access** — test classes are placed in the same package as
  the source classes to access package-private constructors and methods.

## Excluded source code

| Exclusion | Reason |
| --- | --- |
| `RenderContext.setLightColor/setFogColor/setColor/setMaterial/setClearColor` | Calls `gl.glXxx()` immediately — NPEs without live GL context |
| `RenderContext.setViewportAndAddToClearRect` | Requires `GL2.glViewport()` |
| `Context.initialize()` | Calls `disableNormalize()` which needs GL |
| `GlrBox/GlrCylinder.renderGeometry()` | Full GL vertex/normal/texcoord submission |
| `GlrAbstractCamera.performSetup()` | Calls `glu.gluPerspective()` / `gl.glOrtho()` |
| `CurveRenderer.renderTorus/renderDisc/renderCylinder/renderCone/renderSphere` | All GL immediate-mode rendering |
| `WeightedMeshControl.process/postProcess/transformBuffers` | Needs `WeightedMesh` buffer access and `Joint`/`WeightInfo` chains |
| `PickContext.renderPick*` | Full GL pick pipeline |
| `Pixels.prepareTexture/forwardBindTexture` | GL texture upload |

## Configuration

### Test execution

Run only `core/glrender` tests:

```sh
mvn test -pl core/glrender
```

Run a single test class:

```sh
mvn test -pl core/glrender -Dtest=GlrBoxTest
```

Run with coverage and ratchet:

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/glrender=25.0
```

The `core/glrender=25.0` ratchet is the conservative floor; measured coverage
exceeds 30%. See [Expand coverage ratchets](../howto/expand-coverage-ratchets.md)
for the ratchet-raising workflow.

### Environment requirements

- **Java**: 17+ (project baseline)
- **Maven**: 3.9+ (project baseline)
- **OpenGL**: Not required — all tests are headless-safe
- **Display**: Not required — no AWT or Swing usage in test paths

### Submodule prerequisite

Before running tests, ensure the Tweedle grammar submodule is initialized
(required for downstream compilation):

```sh
git submodule update --init tweedle-lang
```

## Coverage arithmetic

| Metric | Value |
| --- | --- |
| Total coverable lines (JaCoCo) | ~5,820 |
| Previously covered lines | ~268 (4.6%) |
| New lines exercised (estimated) | ~1,523+ |
| Total covered lines (estimated) | ~1,791+ |
| New coverage (estimated) | ~30.8% |

Tier 3 (Camera projection) and Tier 2 (Geometry intersection) provide the
highest ROI: projection matrix computation and ray–geometry intersection are
large, branch-heavy methods whose multiple code paths (6 box faces × 2 tests
each, 3 cylinder alignments × cap variants, perspective vs orthographic ×
letterboxing variants) exercise hundreds of lines from relatively few test
methods. Tier 1 (Selection buffer) adds reliable coverage from simple
integer-to-float conversion math.

## Test file inventory

| # | Test file | Location | Primary target | Est. lines |
| --- | --- | --- | --- | ---: |
| 1 | `SelectionBufferInfoTest.java` | `…/gl/imp/` | z-buffer int→float, `updatePointInSource` | 90 |
| 2 | `GlrBoxTest.java` | `…/gl/imp/adapters/` | 6-face ray intersection, normals, texcoords | 130 |
| 3 | `GlrCylinderTest.java` | `…/gl/imp/adapters/` | 3-axis intersection, cap handling | 100 |
| 4 | `GlrSymmetricPerspectiveCameraTest.java` | `…/gl/imp/adapters/` | Projection matrix, getRay, letterboxing | 130 |
| 5 | `GlrOrthographicCameraTest.java` | `…/gl/imp/adapters/` | Projection matrix, getRay, picture plane | 80 |
| 6 | `CurveRendererTest.java` | `…/gl/imp/` | Constants validation, constructor | 15 |
| 7 | `WeightedMeshControlTest.java` | `…/gl/imp/adapters/` | `preProcess()` array reset | 40 |

Combined with indirect coverage from branch paths, field initializers, static
blocks, and constructor chains, plus the 21 existing test files, the module
reaches 30%+ line coverage.

## Relationship to existing tests

These 7 new tests complement the 21 existing test files:

| Existing test | Coverage area | New test relationship |
| --- | --- | --- |
| `RenderContextOpacityStackTest` | Opacity push/pop/multiply | Independent — no overlap |
| `RenderContextExtendedTest` | Brightness, ambient, fog, light ID | Independent — no overlap |
| `ContextScaledCountTest` | Scaled-count increment/decrement | Independent — no overlap |
| `PickParametersDataTest` | Pick result accessors, flipped-Y | `SelectionBufferInfoTest` tests the z-buffer records that feed into pick results |
| `PickContextConstantsTest` | Pick context boolean flags | Independent — no overlap |
| `PixelsLifecycleTest` | Pixels construct/touch/release | Independent — no overlap |
| `AdapterFactoryRegistrationTest` | Register/forget/forgetAll | Camera tests may exercise adapter creation paths |
| `GlrTextureMapCoordinateTest` | Texture coordinate generation | `GlrBoxTest` validates per-face texcoord maps |
| `GlrMeshBufferSelectionTest` | Mesh buffer setup | `WeightedMeshControlTest` validates joint matrix reset |
| `GlrSkeletonVisualExtractionContractTest` | Skeleton visual structure | Independent — no overlap |
