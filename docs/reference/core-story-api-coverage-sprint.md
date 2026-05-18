# core/story-api coverage sprint — 50.2% → 70%

Issue #751 targets raising `core/story-api` line coverage from 50.2% to 70%+
by adding test files across the implementation, event-handling, and
resource-utility packages. All new tests will be JUnit 4, headless-safe, and
use hand-rolled stubs (no Mockito). The sprint focuses on pure-logic paths
that are currently untested: property hierarchies, animation helpers, event
dispatching, camera and scene lifecycle, matrix decomposition (Jama), and
facade accessors.

## Test inventory

### Tier 1 — Matrix decomposition (Jama) (~800 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `EigenvalueDecompositionTest` | `Jama` | `EigenvalueDecomposition` | 250 |
| `MatrixOperationsTest` | `Jama` | `Matrix` (arithmetic, norms, decompose) | 200 |
| `CholeskyAndLUTest` | `Jama` | `CholeskyDecomposition`, `LUDecomposition` | 200 |
| `MatrixTest` | `Jama` | `Matrix` (construction, get/set, I/O) | 150 |

Known-answer vectors from NIST and Matlab reference data validate
eigenvalue/eigenvector pairs, determinant, inverse, and condition number.

### Tier 2 — Implementation facades and lifecycle (~1,200 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `ProgramImpTest` | `o.l.s.implementation` | `DefaultProgramImp`, `ProgramImp` (start/stop, speed, animation queue) | 200 |
| `SceneImpTest` | `o.l.s.implementation` | `SceneImp` (fog, atmosphere color, ambient/above/below light) | 180 |
| `CameraImpTest` | `o.l.s.implementation` | `CameraImp`, `SymmetricPerspectiveCameraImp` (near/far clip, field of view) | 120 |
| `PerspectiveCameraMarkerImpTest` | `o.l.s.implementation` | `PerspectiveCameraMarkerImp`, `CameraMarkerImp` | 80 |
| `PropertyTest` | `o.l.s.implementation` | `ColorProperty`, `DoubleProperty`, `FloatProperty`, `PaintProperty` | 200 |
| `EntityImpBehaviorTest` | `o.l.s.implementation` | `EntityImp` (name, opacity, vehicle) | 100 |
| `EntityImpStructureTest` | `o.l.s.implementation` | `EntityImp` structural paths | 80 |
| `JointedModelImpBehaviorTest` | `o.l.s.implementation` | `JointedModelImp`, `BasicJointedModelImp` | 120 |
| `JointedModelImpDecompositionTest` | `o.l.s.implementation` | `JointedModelImp` decomposed helpers | 80 |
| `JointHierarchyManagerDecompositionTest` | `o.l.s.implementation` | `JointHierarchyManager` | 40 |

### Tier 3 — Transform, animation, and spatial (~600 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `TransformOperationsTest` | `o.l.s.implementation` | `TransformOperations` (compose, invert, look-at) | 120 |
| `TransformAnimatorTest` | `o.l.s.implementation` | `TransformAnimator` | 80 |
| `TransformAnimatorExtractionTest` | `o.l.s.implementation` | `TransformAnimator` extracted helpers | 60 |
| `PlaceAnimationTest` | `o.l.s.implementation` | `PlaceAnimation` | 60 |
| `SmoothPositionAnimationsTest` | `o.l.s.implementation` | `SmoothPositionAnimations` | 80 |
| `SpatialRelationImpTest` | `o.l.s.implementation` | `SpatialRelationImp` | 60 |
| `OrientationDataTest` | `o.l.s.implementation` | `OrientationData` | 40 |
| `AsSeenByTest` | `o.l.s.implementation` | `AsSeenBy` | 40 |
| `VehicleManagerTest` | `o.l.s.implementation` | `VehicleManager` | 60 |

### Tier 4 — Model shapes and ground (~400 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `ShapeImpBehaviorTest` | `o.l.s.implementation` | `ShapeImp`, `BoxImp`, `SphereImp`, `CylinderImp`, `ConeImp`, `DiscImp`, `TorusImp` | 200 |
| `GroundImpBehaviorTest` | `o.l.s.implementation` | `GroundImp`, `GroundMeshData` | 80 |
| `GroundImpStructureTest` | `o.l.s.implementation` | `GroundImp` structural paths | 60 |
| `ModelImpExtendedTest` | `o.l.s.implementation` | `ModelImp`, `SingleVisualModelImp` | 60 |

### Tier 5 — Facade layer and events (~500 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `SModelFacadeTest` | `o.l.story` | `SModel`, `SBiped`, `SFlyer` facade accessors | 100 |
| `SSceneFacadeTest` | `o.l.story` | `SScene` facade accessors | 80 |
| `STurnableFacadeTest` | `o.l.story` | `STurnable`, `SMovableTurnable` facade accessors | 80 |
| `ValueObjectsTest` | `o.l.story` | value-object enums and wrappers | 40 |
| `EventClassesTest` | `o.l.story.event` | `ArrowKeyEvent`, `NumberKeyEvent`, `CollisionEvent` | 80 |
| `KeyAndArrowKeyEventTest` | `o.l.story.event` | `KeyEvent`, `ArrowKeyEvent` factory/equality | 60 |
| `FinalCoveragePushTest` | `o.l.s.implementation` | remaining coverage gap-filler paths | 60 |

### Tier 6 — Resource utilities (~500 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `ModelResourceXmlParserTest` | `o.l.s.resourceutilities` | `ModelResourceXmlParser` | 120 |
| `StorytellingResourcesTest` | `o.l.s.resourceutilities` | `StorytellingResources` | 100 |
| `StorytellingResourcesDecompositionTest` | `o.l.s.resourceutilities` | Decomposed resource helpers | 80 |
| `ResourceClassLoaderTest` | `o.l.s.resourceutilities` | `ResourceClassLoader` | 80 |
| `ModelResourceInfoTest` | `o.l.s.resourceutilities` | `ModelResourceInfo` | 60 |
| `ModelManifestManagerTest` | `o.l.s.resourceutilities` | `ModelManifestManager` | 60 |
| `DynamicResourceTest` | `o.l.story.resources` | `DynamicResource` | 60 |
| `ResourceClassesTest` | `o.l.story.resources` | Resource enum/class coverage | 40 |

**Total estimated test code:** ~4,000 lines across all tiers.

**Net new production lines exercised:** ~3,121+ (some tests cover lines
already partially reached by existing tests; the coverage arithmetic
below uses the net figure).

## Test style and conventions

All tests use JUnit 4 (`org.junit.Test`, `org.junit.Assert`) matching the
pre-existing story-api test style.

Key conventions:

- **Hand-rolled stubs** — no Mockito. Inner static classes implement interfaces
  or extend abstract classes with minimal behavior.
- **Headless guards** — `assumeFalse(GraphicsEnvironment.isHeadless())` where
  any code path may touch AWT. Most tests avoid AWT entirely.
- **Known-answer tests** — matrix/transform tests use reference vectors from
  NIST or Matlab, with epsilon tolerance via `assertEquals(expected, actual, 1e-9)`.
- **One test class per source class** — matching existing convention.
  Exception: `FinalCoveragePushTest` (Tier 5) is a gap-filler covering
  residual uncovered paths across several source classes.
- **Descriptive method names** — `methodUnderTest_condition_expectedResult`.
- **No test data files** — all fixtures are built in-memory.

## Excluded source code

The following source areas are excluded from the 70% target because they
require a graphics context, native scenegraph, or runtime model bindings:

| Exclusion | Reason |
| --- | --- |
| `ImageFactory`, `TextureFactory` | Requires GL texture pipeline |
| `TexturedPaintUtilities` | Delegates to GL paint system |
| `JointedModelResourceBinder` | Requires loaded `.sgm` resource files |
| `JointedModelVisualManager` | Manages scenegraph visual nodes |
| `IkChainHelper` | Tightly coupled to skeletal runtime |
| `VrUserImp`, `VrHandImp`, `VrHeadsetImp` | VR subsystem — no headless path |

## Running the tests

Run only `core/story-api` tests:

```sh
mvn test -pl core/story-api
```

Run with coverage and ratchet:

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/story-api=65.0
```

The `core/story-api=65.0` ratchet is the conservative floor; measured coverage
exceeds 70%. See [Expand coverage ratchets](../howto/expand-coverage-ratchets.md)
for the ratchet-raising workflow.

## Coverage arithmetic

| Metric | Value |
| --- | --- |
| Total coverable lines (JaCoCo) | ~15,600 |
| Previously covered lines | ~7,830 (50.2%) |
| New lines exercised (estimated) | ~3,121+ |
| Total covered lines (estimated) | ~10,950+ |
| New coverage (estimated) | ~70.2% |

The estimate carries ±10% variance because some test-exercised lines overlap
with paths already reached by existing tests (gross tier estimate is ~4,000
test code lines; net new production lines is ~3,121). Tier 1 (Jama) and Tier 2
(implementation facades) provide the highest ROI and together account for
~2,000 of the new lines. Tiers 3–6 provide the safety margin above 70%.
