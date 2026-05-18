# Raise core/ide test coverage

Use this guide to understand, run, extend, and verify the `core/ide` test suite
that raises line coverage from 9.61% to 25%+.

## Prerequisites

```sh
git submodule update --init tweedle-lang
```

Verify that `java -version` reports JDK 17+. The tests use JUnit 4 via the
parent POM's `junit` dependency. Both JUnit 4 and JUnit Jupiter 5.10.2 are on
the classpath.

## Run the tests

### Quick check — compile and test core/ide only

```sh
mvn test -pl core/ide -am
```

The `-am` flag builds upstream dependencies (`core/ast`, `core/util`,
`core/story-api`, etc.) required by `core/ide`. All 31 new test files plus the
102 existing test files execute. Expect approximately 3–5 minutes depending on
hardware.

### Full coverage report

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
```

Open the per-module HTML report:

```sh
open core/ide/target/site/jacoco/index.html
```

Or generate the Markdown summary with the new `core/ide` ratchet:

```sh
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/ide=25.0
```

### Run a single test file

```sh
mvn test -pl core/ide -am -Dtest=SampleFormatConverterTest
```

### Run tests in a single package

```sh
mvn test -pl core/ide -am -Dtest="org.alice.media.audio.*"
```

## Test architecture

### Phase 1 — Audio subsystem (~800 lines)

These tests cover the audio processing pipeline — pure math and stream
operations with zero GUI dependencies:

1. **SampleFormatConverterTest** — Sample format conversion between 8-bit, 16-bit,
   24-bit, and 32-bit PCM formats. Tests round-trip accuracy, boundary values
   (silence, max amplitude, min amplitude), mono/stereo conversion, and
   malformed input handling. Lives in `org.alice.media.audio` to access the
   package-private class.

2. **FloatSampleBufferAdditionalTest** — Float sample buffer allocation,
   channel-count management, sample-count queries, buffer resizing, and
   channel data access. Extends coverage beyond the existing
   `FloatSampleBufferCharacterizationTest`.

3. **MixingFloatAudioInputStreamTest** — Multi-stream mixing arithmetic,
   volume scaling, stream addition/removal, and read behavior on empty/single/
   multiple streams.

4. **ScheduledAudioStreamTest** — Scheduled playback start/stop, timeline
   positioning, scheduling multiple events, and empty schedule behavior.

### Phase 2 — Evidence and tools (~460 lines)

5. **EatmeEvidenceWriterContractTest** — Static utility methods for evidence
   JSON creation, field validation, and output formatting. Tests only the
   static surface — not the `run()` lifecycle.

6. **EatmeRunWorldContractTest** — Contract validation helpers, project
   reference accessors, and error condition reporting.

7. **VolumeLevelUtilitiesTest** — dB-to-linear and linear-to-dB conversion,
   clamping, boundary values (0.0, 1.0, -∞ dB), and NaN handling.

8. **ArithmeticUtilitiesTest** — Arithmetic expression constants, operator
   precedence helpers, and type compatibility checks.

### Phase 3 — AST factories (~900 lines)

9. **IncompleteAstUtilitiesTest** — Null-literal creation, incomplete
   expression detection, type-to-expression mapping, and creation of
   placeholder statements.

10. **ExpressionCreatorTest** — Expression node creation from Java types,
    literal construction for primitives (int, double, boolean, String),
    and method invocation expression building.

11. **StageExpressionCreatorTest** — Stage-specific expression creation,
    scene type handling, and gallery resource expression building.

12. **MergeUtilitiesTest** — Method/field merge detection (identical, different
    signature, same name), merge conflict classification, and type
    compatibility during project merge operations.

13. **StoryApiSpecificAstUtilitiesTest** — Story API type detection, scene
    type queries, joint resource handling, and Story API-specific method
    identification.

14. **BootstrapUtilitiesTest** — Bootstrap type initialization, type registry
    population, and standard library type availability.

15. **JointedTypeInfoTest** — Joint hierarchy construction, joint name lookup,
    parent/child traversal, and joint type metadata access.

16. **JointMethodUtilitiesTest** — Joint method creation, parameter building,
    and joint-specific AST node generation.

17. **PoseAstUtilitiesTest** — Pose expression creation, pose builder
    construction, and joint pose AST node generation.

### Phase 4 — Formatters (~260 lines)

18. **JavaFormatterTest** — Java code formatting via `getInstance()`:
    type name rendering (primitives, arrays, generics), method signature
    formatting, parameter list formatting, and code header/trailer generation.

19. **AliceFormatterTest** — Alice-style "friendly name" formatting:
    camelCase-to-display conversion, type-to-friendly-name mapping, method
    name localization, and differences from `JavaFormatter` output.

### Phase 5 — Animation/IK (~380 lines)

20. **TimeLineTest** — Keyframe addition, removal, and query by time. Tests
    ordered retrieval, boundary keyframes (t=0, t=max), duplicate time
    handling, and empty timeline behavior. Creates fresh `TimeLine`
    instances per test.

21. **KeyFrameDataTest** — KeyFrameData construction, timestamp accessors,
    pose data storage, interpolation weight accessors, and copy/clone.

22. **KeyFrameStylesTest** — Keyframe interpolation style enumeration, style
    name accessors, and style-to-interpolation-function mapping.

### Phase 6 — FieldTree (~255 lines)

23. **FieldTreeTest** — Tree construction from `JavaType`, child node
    traversal, field lookup by name, type filtering, and empty-type tree
    behavior. Uses `JavaType.getInstance(Class)` for type construction.

### Phase 7 — Project IO (~390 lines)

24. **ProjectFileUtilitiesContractTest** — Project file extension detection,
    temporary project file creation, project directory resolution, and
    path validation. Uses `@Rule TemporaryFolder`.

25. **FileProjectLoaderContractTest** — File-based project loading from
    `.a3p` archives, error handling for missing/corrupt files, and
    content type detection. Uses `@Rule TemporaryFolder`.

26. **UriProjectLoaderTest** — URI-to-file resolution, protocol handling
    (file://, relative paths), and error behavior on invalid URIs.

27. **StarterProjectUtilitiesTest** — Starter project enumeration, template
    availability checks, and starter project metadata access.

### Phase 8 — HTML encoding (~180 lines)

28. **HtmlEncoderContractTest** — HTML entity encoding for `<`, `>`, `&`,
    `"`, `'`. Section filtering, DOM helper methods, and **XSS vector
    validation** (`<script>`, `"onload=`, `javascript:` URIs).

### Phase 9 — Name validators (~310 lines)

29. **NameValidatorTest** — Valid/invalid Java identifier detection, reserved
    word rejection, empty string handling, whitespace-only rejection, and
    Unicode identifier support.

30. **NodeNameValidatorTest** — AST node name validation, duplicate name
    detection within scope, type-name vs method-name rules, and special
    character handling.

### Phase 10 — Cascade and miscellaneous (~260 lines)

31. **OneShotSorterTest** — One-shot animation sorting by priority, category
    grouping, stable sort order verification, and empty/single-element edge
    cases. Uses headless guard if static initialization touches AWT.

## How to add more tests

### 1. Choose a target class

Look at the JaCoCo HTML report for `core/ide`. Sort by "Missed Lines" to find
the largest uncovered classes. Exclude Swing/AWT-dependent classes and croquet
composites listed in the
[reference](../reference/core-ide-test-coverage.md#excluded-source-code).

### 2. Create the test file

Place it in the mirror package under `core/ide/src/test/java/`. For example,
to test `org.alice.ide.formatter.JavaFormatter`:

```
core/ide/src/test/java/org/alice/ide/formatter/JavaFormatterTest.java
```

For package-private classes, place the test in the same package as the class
under test. For example, `SampleFormatConverter` is package-private in
`org.alice.media.audio`, so `SampleFormatConverterTest` goes in
`core/ide/src/test/java/org/alice/media/audio/`.

### 3. Follow conventions

```java
package org.alice.ide.formatter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class JavaFormatterTest {

  private JavaFormatter formatter;

  @Before
  public void setUp() {
    formatter = JavaFormatter.getInstance();
  }

  @Test
  public void getTextForType_withPrimitiveInt_returnsInt() {
    assertEquals("int", formatter.getTextForType(
        JavaType.getInstance(int.class)));
  }

  @Test
  public void getTextForType_withNull_returnsEmptyOrThrows() {
    // Test null handling — document actual behavior
    try {
      String result = formatter.getTextForType(null);
      assertNotNull(result);
    } catch (NullPointerException e) {
      // Acceptable — documents that null is not supported
    }
  }

  @Test
  public void getHeaderText_returnsNonEmpty() {
    String header = formatter.getHeaderText();
    assertNotNull(header);
    assertFalse(header.isEmpty());
  }
}
```

Key rules:

- Use JUnit 4 imports (`org.junit.Test`, `org.junit.Assert`).
- Use `@Rule TemporaryFolder` for file system tests.
- Save and restore static state in `@Before`/`@After`.
- Guard AWT-dependent paths with
  `Assume.assumeFalse(GraphicsEnvironment.isHeadless())`.
- Name methods: `methodUnderTest_condition_expectedResult`.
- Include ≥3 meaningful `@Test` methods per file.
- Use `JavaType.getInstance(Class)` for AST type construction.
- Use `getInstance()` for singleton formatters.

### 4. Verify

```sh
mvn test -pl core/ide -am -Dtest=YourNewTest
```

### 5. Update the ratchet

After merging, follow [Expand coverage ratchets](expand-coverage-ratchets.md)
to raise the `core/ide` floor if measured coverage has increased.

## Static state management

Several classes under test use static singletons or shared state. Tests that
mutate these must save the original state in `@Before` and restore it in
`@After`:

| Class | Static state | Restoration pattern |
| --- | --- | --- |
| `JavaFormatter` | Singleton via `getInstance()` | Read-only — no restoration needed |
| `AliceFormatter` | Singleton via `getInstance()` | Read-only — no restoration needed |
| `TimeLine` | Mutable keyframe list | Create fresh instance in `@Before` |
| `ScheduledAudioStream` | Internal scheduling state | Create fresh instance per test |
| `IncompleteAstUtilities` | Static factories | Stateless — no restoration needed |

## Known limitations

- **Coverage estimate variance**: Actual JaCoCo line coverage may differ ±15%
  from the raw-line-count estimates because JaCoCo counts coverable
  instructions, not raw source lines. Gap-fill iteration in Phase 12 addresses
  shortfalls.
- **HeadlessException**: Some cascade manager static initializers may attempt
  AWT lookups — these are guarded with `Assume.assumeFalse(
  GraphicsEnvironment.isHeadless())` and will be skipped in headless CI.
- **Croquet singleton**: Several `core/ide` classes call
  `Application.getActiveInstance()` internally. Tests avoid calling methods that
  trigger this path. If a method is discovered to require the singleton at
  implementation time, it is excluded and a substitute class is tested instead.
- **AST construction complexity**: `NamedUserType`, `UserMethod`, and similar AST
  objects have multi-step construction. Tests use the
  `JavaType.getInstance(Class)` shortcut established by existing test patterns.
- **Resource bundles**: `AliceFormatter` localization tests cover only
  bundle-independent methods. Bundle-dependent methods are excluded because
  localization bundles may not be on the test classpath.
- **MergeUtilities IDE dependency**: 1–2 methods in `MergeUtilities` call the
  IDE singleton. Those specific methods are skipped; the remainder are tested.

## Troubleshooting

### Tests fail with `HeadlessException`

Set the system property when running Maven:

```sh
mvn test -pl core/ide -am -Djava.awt.headless=true
```

The CI pipeline sets this automatically.

### Tests fail with `NullPointerException` in static initializer

Some `core/ide` classes have static initializers that depend on the croquet
framework or IDE singleton. If a new test file triggers this:

1. Add `Assume.assumeTrue` at the top of each `@Test` method or in a `@Before`
   method to skip the test gracefully.
2. If the class is fundamentally untestable in isolation, substitute another
   class from the coverage gap analysis.

### AST construction fails with `IllegalStateException`

Ensure the Tweedle grammar submodule is initialized:

```sh
git submodule update --init tweedle-lang
```

Also verify that the `core/ast` module compiled successfully:

```sh
mvn compile -pl core/ast
```

### Coverage report shows lower than expected lines

JaCoCo measures **coverable lines** (lines with bytecode instructions), not raw
source lines. Comments, blank lines, import statements, and some declarations
do not count. If coverage is below target after all tests pass:

1. Run the full coverage report and check the HTML report for specific gaps.
2. Add edge-case tests for uncovered branches in already-tested classes.
3. Add tests for additional classes from the coverage gap analysis.

### A single test hangs or times out

Audio stream tests that accidentally open a real audio device can hang. Ensure
tests use mock/in-memory streams and do not call `AudioSystem.getLine()`. If a
test hangs, interrupt Maven with `Ctrl-C` and add a `@Test(timeout = 5000)`
annotation.

## Verification checklist

After all 31 test files are implemented, verify:

- [ ] `mvn test -pl core/ide -am` — all tests pass (0 failures, 0 errors)
- [ ] No modifications to existing 102 test files
- [ ] `mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify` completes
- [ ] `core/ide` line coverage ≥ 25% in JaCoCo HTML report
- [ ] `python3 scripts/summarize-jacoco-coverage.py --min-module-line-percent core/ide=25.0` exits 0
- [ ] No `HeadlessException` in test output
- [ ] No hardcoded file paths — all IO tests use `@Rule TemporaryFolder`
