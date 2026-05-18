# core/glrender coverage push: test suite reference

The `core/glrender` module contains 36 JUnit 4 test files totaling 11,021 lines and 1,041 test methods (8 skipped). Every test exercises pure-math, property-synchronization, structure-verification, or contract-checking paths inside the OpenGL rendering layer — no live GL context is required. All 1,041 tests pass.

## Running the tests

```bash
# Run only the glrender module tests (skip checkstyle — requires network)
mvn test -pl core/glrender -Dcheckstyle.skip=true

# Run with JaCoCo coverage (requires verify phase)
mvn verify -pl core/glrender -Dcheckstyle.skip=true

# Run a single test class
mvn test -pl core/glrender -Dcheckstyle.skip=true -Dtest=GlrBoxIntersectionTest

# Run all intersection tests
mvn test -pl core/glrender -Dcheckstyle.skip=true -Dtest="Glr*IntersectionTest"
```

## File inventory

### Package: `render.gl` (5 files, 83 tests)

| File | Lines | Tests | Production class | Strategy |
|---|---:|---:|---|---|
| `AnimatorStateTest` | 233 | 24 | `AnimatorState` | State machine behavior, reflection |
| `GlDrawableUtilsComparisonTest` | 148 | 12 | `GlDrawableUtils` | Comparison math |
| `GlDrawableUtilsExternalServiceTest` | 233 | 22 | `GlDrawableUtils` | External service boundaries |
| `GlrRenderFactoryExternalServiceTest` | 201 | 18 | `GlrRenderFactory` | External service boundaries |
| `RendererNativeLibraryLoaderStructureTest` | 64 | 7 | `RendererNativeLibraryLoader` | Structure/contract |

### Package: `render.gl.imp` (18 files, 595 tests)

| File | Lines | Tests | Production class | Strategy |
|---|---:|---:|---|---|
| `ChangeHandlerExternalServiceTest` | 243 | 14 | `ChangeHandler` | External service boundaries |
| `ContextScaledCountTest` | 146 | 14 | `ContextScaledCount` | Scale math |
| `CurveRendererConstantsTest` | 72 | 8 | `CurveRenderer` | Constants verification |
| `GlResourceCacheExtractionContractTest` | 775 | 55 | `GlResourceCache` | Extraction contract |
| `GlResourceCacheStructureTest` | 109 | 11 | `GlResourceCache` | Structure/contract |
| `Graphics2DAffineTransformMathTest` | 276 | 16 | `Graphics2D` | Affine transform math |
| `Graphics2DExtractionContractTest` | 1,123 | 106 | `Graphics2D` | Extraction contract |
| `Graphics2DMatrixPropertyTest` | 205 | 15 | `Graphics2D` | Matrix property sync |
| `Graphics2DStructureTest` | 304 | 32 | `Graphics2D` | Structure/contract |
| `PickContextConstantsTest` | 243 | 28 | `PickContext` | Constants verification |
| `PickParametersDataTest` | 318 | 40 | `PickParameters` | Data/behavior |
| `PixelsLifecycleTest` | 240 | 20 | `Pixels` | Lifecycle management |
| `ReferencedObjectTest` | 162 | 12 | `ReferencedObject` | Reference lifecycle |
| `RenderContextExtendedTest` | 252 | 22 | `RenderContext` | Extended behavior paths |
| `RenderContextOpacityStackTest` | 290 | 29 | `RenderContext` | Opacity stack push/pop |
| `RenderContextTest` | 574 | 59 | `RenderContext` | Core behavior/contract |
| `RenderTargetGlEventHandlerExtractionContractTest` | 619 | 41 | `RenderTargetGlEventHandler` | Extraction contract |
| `SelectionBufferInfoZBufferTest` | 181 | 18 | `SelectionBufferInfo` | Z-buffer decoding |

### Package: `render.gl.imp.adapters` (9 files, 139 tests)

| File | Lines | Tests | Production class | Strategy |
|---|---:|---:|---|---|
| `AdapterFactoryRegistrationTest` | 234 | 22 | `AdapterFactory` | Factory registration |
| `GlrBoxIntersectionTest` | 161 | 12 | `GlrBox` | Ray–box intersection math |
| `GlrCameraAdapterTest` | 203 | 16 | `GlrCameraAdapter` hierarchy | Adapter behavior, hierarchy |
| `GlrCylinderIntersectionTest` | 156 | 15 | `GlrCylinder` | Ray–cylinder intersection math |
| `GlrMeshBufferSelectionTest` | 190 | 13 | `GlrMesh` | Buffer selection |
| `GlrObjectExternalServiceTest` | 100 | 14 | `GlrObject` | External service boundaries |
| `GlrSkeletonVisualExtractionContractTest` | 100 | 10 | `GlrSkeletonVisual` | Extraction contract |
| `GlrTextureMapCoordinateTest` | 283 | 30 | `GlrTexture` | Coordinate math, ref counting, dirty flags |
| `WeightedMeshControlPreProcessTest` | 139 | 7 | `WeightedMeshControl` | Pre-processing logic |

### Package: `render.joglrenderer` (2 files, 216 tests)

| File | Lines | Tests | Production class | Strategy |
|---|---:|---:|---|---|
| `InnerClassExtractionContractTest` | 1,475 | 167 | JOGL renderer inner classes | Extraction contract |
| `NonCachingTextRendererCharacterizationTest` | 479 | 49 | `NonCachingTextRenderer` | Characterization |

### Package: `system.graphics` (2 files, 63 tests)

| File | Lines | Tests | Production class | Strategy |
|---|---:|---:|---|---|
| `ConformanceTestResultsExternalServiceTest` | 306 | 40 | `ConformanceTestResults` | External service boundaries |
| `ConformanceTestResultsMathTest` | 184 | 23 | `ConformanceTestResults` | Math/data validation |

## Conventions

All 36 test files follow consistent patterns:

| Convention | Detail |
|---|---|
| **JUnit version** | JUnit 4 (`org.junit.Test`, `org.junit.Before`). No JUnit 5 imports. |
| **Naming** | `{Class}{Aspect}Test.java` — e.g. `GlrBoxIntersectionTest`, `RenderContextOpacityStackTest`, `Graphics2DStructureTest`. |
| **Adapter construction** | `new GlrFoo()` + `initialize(new Foo())`, or `AdapterFactory.getAdapterFor(sgObject)`. Both patterns exist; each test uses whichever is simpler. |
| **Reflection** | `java.lang.reflect.Field` with `setAccessible(true)` for private fields. Used in ~30 of 36 files. |
| **Assertions** | `assertEquals`, `assertTrue`, `assertFalse`, `assertNotNull`, `assertNull` from `org.junit.Assert`. Floating-point comparisons use epsilon `1e-6`. |
| **No mocking framework** | No Mockito, no PowerMock. Concrete scene-graph objects, reflection, and inner stub classes only. |
| **No GL dependency** | Every test targets code paths that do not dereference a `gl` field. Methods that call into JOGL are never invoked. |
| **Static state cleanup** | `@Before` resets shared static fields via reflection to prevent cross-test contamination. |

## Test strategies

### Intersection math

`GlrBoxIntersectionTest` and `GlrCylinderIntersectionTest` test ray–surface intersection formulas. They create adapters directly, set private geometry fields via reflection, and assert intersection points against analytically computed values. Rays that miss return `Point3.NaN`.

### Extraction contracts

The largest category. `Graphics2DExtractionContractTest` (106 tests), `GlResourceCacheExtractionContractTest` (55 tests), `RenderTargetGlEventHandlerExtractionContractTest` (41 tests), and `InnerClassExtractionContractTest` (167 tests) verify that refactoring extractions preserved the original class structure — method signatures, field types, access modifiers, and inheritance relationships are asserted via reflection.

### Property synchronization

Tests like `RenderContextOpacityStackTest`, `Graphics2DMatrixPropertyTest`, and `ContextScaledCountTest` trigger `propertyChanged` on adapters and verify that internal cached fields update correctly.

### External service boundaries

`GlDrawableUtilsExternalServiceTest`, `GlrRenderFactoryExternalServiceTest`, `ChangeHandlerExternalServiceTest`, `GlrObjectExternalServiceTest`, and `ConformanceTestResultsExternalServiceTest` verify behavior at system boundaries without requiring live external services (GL context, native libraries).

### Characterization

`NonCachingTextRendererCharacterizationTest` (49 tests) captures existing behavior of the text renderer to guard against regressions during future refactoring.

### Structure/contract

`GlResourceCacheStructureTest`, `Graphics2DStructureTest`, `RendererNativeLibraryLoaderStructureTest`, and `GlrSkeletonVisualExtractionContractTest` verify class structure (method presence, field types, inheritance) via reflection.

## Test patterns and examples

### Pattern 1: Adapter construction with reflection

Standard pattern for creating an adapter, wiring it to a scene-graph element, and accessing private fields:

```java
import edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrBox;
import edu.cmu.cs.dennisc.scenegraph.Box;
import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import static org.junit.Assert.*;

public class GlrBoxIntersectionTest {

    private GlrBox adapter;
    private Box sgBox;

    @Before
    public void setUp() throws Exception {
        sgBox = new Box();
        adapter = new GlrBox();
        adapter.initialize(sgBox);

        // Set private bounds via reflection
        Field xMinField = GlrBox.class.getDeclaredField("xMin");
        xMinField.setAccessible(true);
        xMinField.setDouble(adapter, -0.5);
        // ... similar for xMax, yMin, yMax, zMin, zMax
    }

    @Test
    public void hitAlongZAxis() {
        Ray ray = new Ray(new Point3(0, 0, -5), new Vector3(0, 0, 1));
        Point3 result = adapter.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
        assertFalse(result.isNaN());
        assertEquals(-0.5, result.z, 1e-6);
    }
}
```

### Pattern 2: Opacity stack testing (RenderContext)

```java
@Test
public void pushAndPopOpacity() throws Exception {
    RenderContext rc = new RenderContext();
    Field stackField = RenderContext.class.getDeclaredField("globalOpacityStack");
    stackField.setAccessible(true);

    // Push opacity, verify stack depth changes
    rc.pushGlobalOpacity(0.5f);
    // ... assertions on internal state via reflection
    rc.popGlobalOpacity();
}
```

### Pattern 3: Concrete test subclass for abstract classes

When the class under test is abstract (like `GlrTexture`), create a minimal concrete inner class:

```java
private static class TestableGlrTexture extends GlrTexture<SingleImageTexture> {
    @Override
    protected TextureData newTextureData(RenderContext rc) {
        throw new UnsupportedOperationException("not needed for ref-count tests");
    }
}
```

### Pattern 4: Extraction contract verification

Verify that extracted classes maintain the expected structure:

```java
@Test
public void delegateClassExists() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.Graphics2DDelegate");
    assertNotNull(cls);
    assertTrue(Modifier.isPublic(cls.getModifiers()));
}

@Test
public void methodSignaturePreserved() throws Exception {
    Method m = Graphics2D.class.getDeclaredMethod("setTransform", AffineTransform.class);
    assertNotNull(m);
}
```

## Configuration

No special configuration beyond the standard Maven build:

- **JUnit 4** — already a test dependency in `core/glrender/pom.xml`
- **Scene-graph classes** — from sibling modules (`core/scenegraph`, `core/util`)
- **Math classes** — `Point3`, `Vector3`, `Ray`, `Matrix4x4` from `core/util`
- **Reflection** — `java.lang.reflect.Field`, `java.lang.reflect.Method` (JDK standard library)

No additional dependencies, mocking frameworks, or build-plugin changes needed.

For environments with constrained memory:

```bash
export MAVEN_OPTS="-Xmx2g"
mvn test -pl core/glrender -Dcheckstyle.skip=true
```

## Summary by numbers

| Metric | Value |
|---|---:|
| Test files | 36 |
| Total lines | 11,021 |
| Test methods | 1,041 |
| Skipped tests | 8 |
| Packages covered | 5 |
| Production classes tested | ~25 |
| Uses reflection | 30 of 36 files |
| Uses mocks | 0 |
| Requires GL context | 0 |
