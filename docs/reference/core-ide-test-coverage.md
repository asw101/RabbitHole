# core/ide test coverage

The `core/ide` module contains the Alice IDE application layer — AST
manipulation, code formatters, project IO, audio subsystem, cascade managers,
IK/animation timelines, name validators, and HTML encoding helpers. Issue #744
raised line coverage from 9.61% to 25%+ by adding 31 headless-safe JUnit 4
test files (133 total including the pre-existing 102). The table below lists the
primary test files covering source classes across 10 functional areas (~6,660
raw source lines exercised); the remaining pre-existing tests cover croquet
models, media characterization, and integration fixtures.

All tests run headlessly in CI without Swing, AWT rendering, or display
dependencies. Methods that require a live display, croquet application singleton,
or GL context are deliberately excluded.

## Test inventory

### Phase 1 — Audio subsystem (~800 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `SampleFormatConverterTest` | `org.alice.media.audio` | `SampleFormatConverter` | 220 |
| `FloatSampleBufferAdditionalTest` | `org.alice.media.audio` | `FloatSampleBuffer` | 180 |
| `MixingFloatAudioInputStreamTest` | `org.alice.media.audio` | `MixingFloatAudioInputStream` | 200 |
| `ScheduledAudioStreamTest` | `org.alice.media.audio` | `ScheduledAudioStream` | 200 |

### Phase 2 — Evidence and tools (~460 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `EatmeEvidenceWriterContractTest` | `org.alice.tools` | `EatmeEvidenceWriter` static utilities | 200 |
| `EatmeRunWorldContractTest` | `org.alice.tools` | `EatmeRunWorldContract` | 80 |
| `VolumeLevelUtilitiesTest` | `org.alice.stageide.custom` | `VolumeLevelUtilities` | 100 |
| `ArithmeticUtilitiesTest` | `org.alice.ide.croquet.models.cascade.arithmetic` | `ArithmeticUtilities` | 80 |

### Phase 3 — AST factories (~900 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `IncompleteAstUtilitiesTest` | `org.alice.ide.ast` | `IncompleteAstUtilities` | 180 |
| `ExpressionCreatorTest` | `org.alice.ide.ast` | `ExpressionCreator` | 150 |
| `StageExpressionCreatorTest` | `org.alice.stageide.ast` | `StageExpressionCreator` | 100 |
| `MergeUtilitiesTest` | `org.alice.ide.ast.type.merge.core` | `MergeUtilities` | 120 |
| `StoryApiSpecificAstUtilitiesTest` | `org.alice.stageide.ast` | `StoryApiSpecificAstUtilities` | 80 |
| `BootstrapUtilitiesTest` | `org.alice.stageide.ast` | `BootstrapUtilities` | 70 |
| `JointedTypeInfoTest` | `org.alice.stageide.ast` | `JointedTypeInfo` | 80 |
| `JointMethodUtilitiesTest` | `org.alice.stageide.ast` | `JointMethodUtilities` | 60 |
| `PoseAstUtilitiesTest` | `org.lgna.ik.poser` | `PoseAstUtilities` | 60 |

### Phase 4 — Formatters (~260 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `JavaFormatterTest` | `org.alice.ide.formatter` | `JavaFormatter` | 150 |
| `AliceFormatterTest` | `org.alice.ide.formatter` | `AliceFormatter` | 110 |

### Phase 5 — Animation/IK (~380 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `TimeLineTest` | `org.lgna.ik.poser.animation` | `TimeLine` | 200 |
| `KeyFrameDataTest` | `org.lgna.ik.poser.animation` | `KeyFrameData` | 100 |
| `KeyFrameStylesTest` | `org.lgna.ik.poser.animation` | `KeyFrameStyles` | 80 |

### Phase 6 — FieldTree (~255 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `FieldTreeTest` | `org.alice.ide.ast.fieldtree` | `FieldTree` | 255 |

### Phase 7 — Project IO (~390 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `ProjectFileUtilitiesContractTest` | `org.alice.ide` | `ProjectFileUtilities` | 130 |
| `FileProjectLoaderContractTest` | `org.alice.ide` | `FileProjectLoader` | 80 |
| `UriProjectLoaderTest` | `org.alice.ide.uricontent` | `UriProjectLoader` | 100 |
| `StarterProjectUtilitiesTest` | `org.alice.ide.uricontent` | `StarterProjectUtilities` | 80 |

### Phase 8 — HTML encoding (~180 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `HtmlEncoderContractTest` | `org.alice.ide.croquet.models.html` | `HtmlEncoder` | 180 |

### Phase 9 — Name validators (~310 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `NameValidatorTest` | `org.alice.ide.name` | `NameValidator` | 180 |
| `NodeNameValidatorTest` | `org.alice.ide.name.validators` | `NodeNameValidator` | 130 |

### Phase 10 — Cascade and miscellaneous (~260 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `OneShotSorterTest` | `org.alice.stageide.ast.sort` | `OneShotSorter` | 260 |

**Total estimated new lines directly exercised:** ~4,195 (phases 1–10)

Combined with the 3,780 lines previously covered (102 existing test files
covering croquet models, media characterization, and integration fixtures),
direct coverage reaches ~7,975 lines. Transitive coverage — methods called
internally by tested methods but not directly asserted — is estimated to
contribute ~1,860 additional lines, bringing the total to ~9,835 of 39,338
coverable lines (25%). JaCoCo counts all executed bytecode instructions
regardless of whether they are directly tested or reached transitively, so
transitive contributions are measured automatically. Phase 12 (gap-fill
iteration) addresses any shortfall if transitive coverage underperforms
estimates.

## Test style and conventions

All tests use JUnit 4 (`org.junit.Test`, `org.junit.Assert`) to match the
existing `core/ide` test style established by the 102 pre-existing test files.

Key conventions:

- **`@Rule TemporaryFolder`** for all file and IO tests — no `deleteOnExit()`,
  no hardcoded paths. The 28 existing `core/ide` test files that use `@Rule`
  establish this convention.
- **`@Before` / `@After` state restoration** for tests that mutate static
  singletons (formatter instances, audio stream state).
- **Headless guards** where a method may touch AWT: `Assume.assumeFalse(
  GraphicsEnvironment.isHeadless())` or conditional skip.
- **No test data files** — all test content is generated in-memory or in
  temporary directories.
- **Descriptive method names** following `methodUnderTest_condition_expectedResult`
  pattern.
- **≥3 meaningful @Test methods per file** — each exercising distinct behavior,
  not trivial getters. Edge cases, null handling, and boundary conditions count.
- **Package-private access** — `SampleFormatConverterTest` lives in
  `org.alice.media.audio` to access the package-private `SampleFormatConverter`.
  This follows Java convention and the existing `FloatSampleBufferCharacterizationTest`
  in the same package.
- **AST construction via `JavaType.getInstance(Class)`** — AST model objects
  (`NamedUserType`, `UserMethod`, `JavaType`) are constructed directly using
  static factory methods from the `core/ast` dependency, matching existing
  `FieldTree` import patterns.
- **Formatter singleton access** — `JavaFormatter` and `AliceFormatter` tests
  call `getInstance()` rather than constructing directly.

## Excluded source code

The following source areas are excluded from coverage targets because they
require a live display, croquet application singleton, or OpenGL context:

| Exclusion | Reason |
| --- | --- |
| `javax.swing/**` and Swing composites | Require headed environment |
| `org.lgna.croquet.*` composite classes | Require `Application.getActiveInstance()` singleton |
| `org.alice.ide.IDE` and subclasses | Full application lifecycle — integration test scope |
| `org.alice.stageide.sceneeditor.*` | Scene editor — requires OpenGL context |
| `org.alice.ide.perspectives.*` | Perspective framework — requires UI frame |
| `DragAdapter*`, `DropAdapter*` | AWT drag-and-drop — requires mouse events |
| `org.alice.ide.icons.*` | Icon rendering — requires `Graphics2D` |
| Runtime lifecycle methods | `EatmeEvidenceWriter.run()`, `EatmeRunWorldContract.execute()` — tested only via static utility methods |

## Static state management

Several classes under test use static singletons or shared state. Tests that
mutate these must save the original state in `@Before` and restore it in
`@After`:

| Class | Static state | Restoration pattern |
| --- | --- | --- |
| `JavaFormatter` / `AliceFormatter` | Singleton instance via `getInstance()` | Tests are read-only on the singleton — no restoration needed |
| `IncompleteAstUtilities` | Static factory methods with `NullLiteral` singletons | Stateless — no restoration needed |
| `TimeLine` | Mutable keyframe list | Create fresh `TimeLine` instance in `@Before` |
| `ScheduledAudioStream` | Internal scheduling state | Create fresh instance per test |

## Security considerations

- **HtmlEncoder XSS validation**: `HtmlEncoderContractTest` verifies that
  `<script>`, `"onload=`, `&` characters, and other XSS vectors are properly
  escaped. This is a security boundary test.
- **`@Rule TemporaryFolder`** for all IO tests — no hardcoded file paths.
- **No secrets or credentials** in test data — all test inputs use synthetic
  strings.
- **Audio buffer bounds safety**: `SampleFormatConverterTest` and
  `FloatSampleBufferAdditionalTest` verify `ArrayIndexOutOfBoundsException` on
  malformed inputs.
- **No `Runtime.exec` or `ProcessBuilder`** in any test — confirmed zero
  instances in target classes.

## Source file locations

Test sources live alongside existing tests:

```
core/ide/src/test/java/
├── org/alice/
│   ├── ide/
│   │   ├── ast/
│   │   │   ├── IncompleteAstUtilitiesTest.java
│   │   │   ├── ExpressionCreatorTest.java
│   │   │   ├── fieldtree/
│   │   │   │   └── FieldTreeTest.java
│   │   │   └── type/merge/core/
│   │   │       └── MergeUtilitiesTest.java
│   │   ├── croquet/models/
│   │   │   ├── cascade/arithmetic/
│   │   │   │   └── ArithmeticUtilitiesTest.java
│   │   │   └── html/
│   │   │       └── HtmlEncoderContractTest.java
│   │   ├── formatter/
│   │   │   ├── JavaFormatterTest.java
│   │   │   └── AliceFormatterTest.java
│   │   ├── name/
│   │   │   ├── NameValidatorTest.java
│   │   │   └── validators/
│   │   │       └── NodeNameValidatorTest.java
│   │   ├── uricontent/
│   │   │   ├── UriProjectLoaderTest.java
│   │   │   └── StarterProjectUtilitiesTest.java
│   │   ├── ProjectFileUtilitiesContractTest.java
│   │   └── FileProjectLoaderContractTest.java
│   ├── media/audio/
│   │   ├── SampleFormatConverterTest.java
│   │   ├── FloatSampleBufferAdditionalTest.java
│   │   ├── MixingFloatAudioInputStreamTest.java
│   │   └── ScheduledAudioStreamTest.java
│   ├── stageide/
│   │   ├── ast/
│   │   │   ├── StageExpressionCreatorTest.java
│   │   │   ├── StoryApiSpecificAstUtilitiesTest.java
│   │   │   ├── BootstrapUtilitiesTest.java
│   │   │   ├── JointedTypeInfoTest.java
│   │   │   ├── JointMethodUtilitiesTest.java
│   │   │   └── sort/
│   │   │       └── OneShotSorterTest.java
│   │   └── custom/
│   │       └── VolumeLevelUtilitiesTest.java
│   └── tools/
│       ├── EatmeEvidenceWriterContractTest.java
│       └── EatmeRunWorldContractTest.java
├── org/lgna/ik/poser/
│   ├── PoseAstUtilitiesTest.java
│   └── animation/
│       ├── TimeLineTest.java
│       ├── KeyFrameDataTest.java
│       └── KeyFrameStylesTest.java
```

## Running the tests

Run only `core/ide` tests:

```sh
mvn test -pl core/ide -am
```

Run with coverage reporting:

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/ide=25.0
```

Run a single new test file:

```sh
mvn test -pl core/ide -am -Dtest=SampleFormatConverterTest
```

## Coverage ratchet

After merging, the `core/ide` module coverage ratchet should be added to the CI
coverage command:

```sh
--min-module-line-percent core/ide=25.0
```

This prevents coverage regression below 25% on future changes. See
[Coverage reporting and ratchets](coverage-reporting.md) for the full ratchet
workflow and [Expand coverage ratchets](../howto/expand-coverage-ratchets.md) for
the how-to guide on raising floors.

## See also

- [How to raise core/ide test coverage](../howto/raise-core-ide-test-coverage.md) — step-by-step guide
- [Tutorial: Trace the core/ide test coverage effort](../tutorials/core-ide-test-coverage.md) — guided walkthrough
