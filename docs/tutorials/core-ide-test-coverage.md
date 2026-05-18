# Tutorial: Trace the core/ide test coverage effort

This tutorial walks through the test architecture for the `core/ide` module
coverage improvement from 9.61% to 25%+. Follow along to understand the class
selection strategy, test patterns, and coverage measurement workflow.

## What you will learn

- How the 31 test files were selected and prioritized
- How to test package-private classes, AST factories, and singleton formatters
- How to write headless-safe tests for audio and animation subsystems
- How to measure and validate JaCoCo line coverage
- How to extend coverage beyond 25%

## Prerequisites

- JDK 17+
- Maven 3.8+
- Tweedle grammar submodule initialized: `git submodule update --init tweedle-lang`
- Familiarity with JUnit 4 (`@Test`, `@Before`, `@Rule`)

## Step 1: Understand the coverage landscape

The `core/ide` module has 39,338 coverable lines. Before this effort, 3,780
lines were covered (9.61%) by 102 existing test files. To reach 25%, we need
9,835 lines covered — an additional 6,055 lines.

Building with a 10% buffer, the implementation targets ~6,660 additional lines
across 31 new test files organized in 10 functional phases.

### Coverage math

```
Total coverable lines:    39,338
Previously covered:        3,780  (9.61%)
Target covered:            9,835  (25.00%)
Lines needed:              6,055
Direct new lines:         ~4,195  (31 test files, phases 1-10)
Est. transitive lines:    ~1,860  (internal calls reached by tested methods)
Total new coverage:       ~6,055  (direct + transitive)
With 10% buffer target:   ~6,660
```

Transitive coverage comes from methods called internally by tested code —
JaCoCo counts all executed bytecode, not just directly asserted paths. Phase 12
(gap-fill iteration) addresses any shortfall.

## Step 2: Explore the audio subsystem tests

The audio subsystem is the highest-yield target — pure math with zero GUI
dependencies. Start with `SampleFormatConverterTest`:

```sh
# View the source class (package-private)
head -20 core/ide/src/main/java/org/alice/media/audio/SampleFormatConverter.java
```

> **Tip:** In an IDE, use Navigate → Class (`Ctrl+N` in IntelliJ) to jump
> directly to `SampleFormatConverter`.

Notice that `SampleFormatConverter` has no `public` keyword on the class
declaration — it is package-private. The test class must live in the same
package:

```
core/ide/src/test/java/org/alice/media/audio/SampleFormatConverterTest.java
```

This follows the existing pattern established by
`FloatSampleBufferCharacterizationTest` in the same package.

### Key pattern: Testing package-private classes

```java
// Same package as the class under test
package org.alice.media.audio;

import org.junit.Test;
import static org.junit.Assert.*;

public class SampleFormatConverterTest {

  @Test
  public void convert_8bitTo16bit_preservesAmplitude() {
    // SampleFormatConverter is accessible here because we're
    // in the same package
    byte[] input8bit = { 0, 64, 127, -128 };
    short[] expected16bit = { 0, 8192, 16256, -16384 };
    // ... conversion and assertion
  }
}
```

## Step 3: Explore the AST factory tests

AST factory methods are the second-highest yield. These tests use
`JavaType.getInstance(Class)` to construct type objects:

```java
import org.lgna.project.ast.JavaType;

@Test
public void createExpression_forIntegerType_returnsIntegerLiteral() {
  JavaType intType = JavaType.getInstance(int.class);
  // Use the type in factory method calls
  Expression result = ExpressionCreator.createExpression(intType);
  assertNotNull(result);
}
```

### Key pattern: AST construction

The `core/ast` module provides `JavaType.getInstance(Class)` as a lightweight
factory for wrapping Java classes in the Alice AST type system. This avoids the
heavyweight construction of `NamedUserType` or `UserMethod` when a simple type
reference suffices.

For methods that require a `NamedUserType`:

```java
NamedUserType userType = new NamedUserType();
userType.name.setValue("MyType");
UserMethod method = new UserMethod();
method.name.setValue("doSomething");
method.returnType.setValue(JavaType.getInstance(void.class));
userType.methods.add(method);
```

## Step 4: Explore the formatter tests

Formatters use the singleton pattern. Tests access them via `getInstance()`:

```java
@Before
public void setUp() {
  formatter = JavaFormatter.getInstance();
}

@Test
public void getTextForType_withPrimitiveInt_returnsInt() {
  assertEquals("int", formatter.getTextForType(
      JavaType.getInstance(int.class)));
}
```

### Key pattern: Comparing formatter outputs

The `JavaFormatter` and `AliceFormatter` produce different text for the same
types. Testing both validates the formatting abstraction:

```java
@Test
public void sameType_differentFormatters_produceDifferentOutput() {
  JavaType doubleType = JavaType.getInstance(Double.class);
  String javaText = JavaFormatter.getInstance().getTextForType(doubleType);
  String aliceText = AliceFormatter.getInstance().getTextForType(doubleType);
  // Java: "Double", Alice: "DecimalNumber" (or similar friendly name)
  assertNotEquals(javaText, aliceText);
}
```

## Step 5: Explore the animation/IK tests

The `TimeLine` class manages keyframes for IK pose animations. Tests create
a fresh instance per test method:

```java
private TimeLine timeLine;

@Before
public void setUp() {
  timeLine = new TimeLine();
}

@Test
public void addKeyFrame_atTimeZero_isRetrievable() {
  KeyFrameData kf = new KeyFrameData(0.0);
  timeLine.addKeyFrame(kf);
  assertEquals(1, timeLine.getKeyFrameCount());
}

@Test
public void getKeyFrames_afterMultipleAdds_returnsInTimeOrder() {
  timeLine.addKeyFrame(new KeyFrameData(2.0));
  timeLine.addKeyFrame(new KeyFrameData(0.5));
  timeLine.addKeyFrame(new KeyFrameData(1.0));
  List<KeyFrameData> frames = timeLine.getKeyFrames();
  assertTrue(frames.get(0).getTime() <= frames.get(1).getTime());
  assertTrue(frames.get(1).getTime() <= frames.get(2).getTime());
}
```

## Step 6: Explore the project IO tests

Project IO tests use `@Rule TemporaryFolder` exclusively — the convention
established by 28 existing `core/ide` test files:

```java
@Rule
public TemporaryFolder tempDir = new TemporaryFolder();

@Test
public void isProjectFile_withA3pExtension_returnsTrue() throws Exception {
  File projectFile = tempDir.newFile("test.a3p");
  assertTrue(ProjectFileUtilities.isProjectFile(projectFile));
}

@Test
public void isProjectFile_withTxtExtension_returnsFalse() throws Exception {
  File textFile = tempDir.newFile("readme.txt");
  assertFalse(ProjectFileUtilities.isProjectFile(textFile));
}
```

## Step 7: Explore the HTML encoder security tests

The `HtmlEncoderContractTest` includes XSS vector validation — a security
boundary test:

```java
@Test
public void encode_scriptTag_isEscaped() {
  String malicious = "<script>alert('xss')</script>";
  String encoded = HtmlEncoder.encode(malicious);
  assertFalse("Script tag must be escaped",
      encoded.contains("<script>"));
}

@Test
public void encode_onloadAttribute_isEscaped() {
  String malicious = "\"onload=\"alert('xss')\"";
  String encoded = HtmlEncoder.encode(malicious);
  assertFalse("Event handler must be escaped",
      encoded.contains("onload="));
}

@Test
public void encode_ampersand_isEntityEncoded() {
  assertEquals("a&amp;b", HtmlEncoder.encode("a&b"));
}
```

## Step 8: Explore the headless guard pattern

Some classes may touch AWT during static initialization. The headless guard
pattern prevents test failures in CI:

```java
import org.junit.Assume;
import java.awt.GraphicsEnvironment;

@Test
public void sort_withAnimations_producesStableOrder() {
  Assume.assumeFalse("Requires headed environment",
      GraphicsEnvironment.isHeadless());

  // Test body — only runs when display is available
  OneShotSorter sorter = new OneShotSorter();
  // ...
}
```

If all methods in a class require AWT, add the guard to `@Before`:

```java
@Before
public void setUp() {
  Assume.assumeFalse("Requires headed environment",
      GraphicsEnvironment.isHeadless());
}
```

Tests guarded this way show as "skipped" (not "failed") in Maven output.

## Step 9: Measure coverage

After all tests are implemented, run the full coverage measurement:

```sh
# Build and test with coverage
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify

# Generate the summary with the core/ide ratchet
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/ide=25.0
```

The `--min-module-line-percent core/ide=25.0` flag causes the script to fail
(exit code 1) if `core/ide` line coverage drops below 25%.

### Reading the HTML report

Open `core/ide/target/site/jacoco/index.html` in a browser. The report shows:

- **Element**: Package or class name
- **Missed Instructions / Cov.**: Bytecode instruction coverage
- **Missed Branches / Cov.**: Branch coverage
- **Missed Lines**: The metric we optimize — uncovered lines
- **Missed Methods**: Methods with zero coverage

Sort by "Missed Lines" descending to find the next targets for gap-fill.

## Step 10: Gap-fill iteration

If coverage is below 25% after all 31 test files pass, iterate:

1. **Check the JaCoCo report** for the largest remaining uncovered classes.
2. **Add edge-case tests** for already-tested classes — these hit uncovered
   branches and conditional lines cheaply.
3. **Add tests for additional classes** from the same functional areas.
4. **Re-run coverage** and check the delta.

Common gap-fill targets:

- Additional methods in `IncompleteAstUtilities` or `MergeUtilities`
- Error/exception paths in project IO classes
- Boundary conditions in audio buffer operations
- Additional operator types in `ArithmeticUtilities`

## Summary

| Phase | Functional area | Test files | Est. lines |
| ---: | --- | ---: | ---: |
| 1 | Audio subsystem | 4 | 800 |
| 2 | Evidence & tools | 4 | 460 |
| 3 | AST factories | 9 | 900 |
| 4 | Formatters | 2 | 260 |
| 5 | Animation/IK | 3 | 380 |
| 6 | FieldTree | 1 | 255 |
| 7 | Project IO | 4 | 390 |
| 8 | HTML encoding | 1 | 180 |
| 9 | Name validators | 2 | 310 |
| 10 | Cascade & misc | 1 | 260 |
| **Total** | | **31** | **~4,195** |

With transitive coverage and the existing 3,780 covered lines, the module
reaches 25%+ (9,835+ of 39,338 lines).

## Next steps

- Read the [reference](../reference/core-ide-test-coverage.md) for the complete
  test inventory, excluded source areas, and security considerations.
- Read the [how-to](../howto/raise-core-ide-test-coverage.md) for running,
  extending, and troubleshooting the test suite.
- Follow [Expand coverage ratchets](../howto/expand-coverage-ratchets.md) to
  raise the CI floor after merging.
