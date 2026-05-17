# core/util test coverage

The `core/util` module contains low-level utility classes used across the entire
Alice codebase — binary codecs, buffer conversions, reflection helpers, file and
zip operations, logging, printing, and collection factories. Issue #736 raised
line coverage from 3.63% to 40%+ by adding 71 headless-safe JUnit test files
(83 total including pre-existing). The table below lists the 16 primary
high-impact test files covering 26 source classes (~5,360 raw source lines);
the remaining tests cover immutable math types, pattern utilities, property
helpers, lgna-common concurrency, and resource types.

All tests run headlessly in CI without Swing, AWT rendering, or display
dependencies. Methods that require a live display (e.g.,
`FileUtilities.getDefaultDirectory()`, `SystemUtilities.loadLibrary()`) are
deliberately excluded.

## Test inventory

| Test file | Package | Source classes covered | Estimated lines exercised |
| --- | --- | --- | ---: |
| `BinaryCodecRoundTripTest` | `edu.cmu.cs.dennisc.codec` | `AbstractBinaryEncoder`, `AbstractBinaryDecoder`, `OutputStreamBinaryEncoder`, `InputStreamBinaryDecoder` | 900 |
| `BufferUtilitiesTest` | `edu.cmu.cs.dennisc.java.util` | `BufferUtilities` | 220 |
| `ReflectionUtilitiesTest` | `edu.cmu.cs.dennisc.java.lang.reflect` | `ReflectionUtilities` | 300 |
| `FileUtilitiesTest` | `edu.cmu.cs.dennisc.java.io` | `FileUtilities` | 200 |
| `ZipUtilitiesTest` | `edu.cmu.cs.dennisc.java.util.zip` | `ZipUtilities`, `DataSource` | 250 |
| `PrintUtilitiesTest` | `edu.cmu.cs.dennisc.print` | `PrintUtilities` | 280 |
| `ClassUtilitiesTest` | `edu.cmu.cs.dennisc.java.lang` | `ClassUtilities` | 130 |
| `SystemUtilitiesTest` | `edu.cmu.cs.dennisc.java.lang` | `SystemUtilities`, `SystemProperty` | 200 |
| `LoggerTest` | `edu.cmu.cs.dennisc.java.util.logging` | `Logger`, `ConsoleFormatter`, `SegregatingConsoleHandler` | 250 |
| `TextFileUtilitiesTest` | `edu.cmu.cs.dennisc.java.io` | `TextFileUtilities` | 90 |
| `XMLUtilitiesTest` | `edu.cmu.cs.dennisc.xml` | `XMLUtilities` | 160 |
| `ArrayUtilitiesTest` | `edu.cmu.cs.dennisc.java.lang` | `ArrayUtilities` | 150 |
| `ListsTest` | `edu.cmu.cs.dennisc.java.util` | `Lists` | 150 |
| `ThrowableUtilitiesTest` | `edu.cmu.cs.dennisc.java.lang` | `ThrowableUtilities` | 50 |
| `LazyTest` | `edu.cmu.cs.dennisc.pattern` | `Lazy` | 50 |
| `MapsTest` | `edu.cmu.cs.dennisc.java.util` | `Maps`, `InitializingIfAbsentMap` | 50 |

**Total estimated new lines exercised:** ~3,430

Combined with the 363 lines previously covered (math, color, WindowStack,
FileDialogUtilities, IssueReportWorker), the module reaches ~3,793 of 10,001
coverable lines — approximately 38%. The safety-margin classes (Maps,
DataSource, ConsoleFormatter, SegregatingConsoleHandler, SystemProperty) push
the total above 40%.

## Test style and conventions

All tests use JUnit 4 (`org.junit.Test`, `org.junit.Assert`) to match the
existing `core/util` test style established by `WindowStackTest`,
`FileDialogUtilitiesTest`, and the math tests.

Key conventions:

- **`@Rule TemporaryFolder`** for all file and zip tests — no `deleteOnExit()`,
  no hardcoded paths.
- **`@Before` / `@After` state restoration** for tests that mutate static
  singletons (PrintUtilities print-stream stack, Logger levels, system
  properties).
- **Headless guards** where a method may touch AWT: `assumeFalse(
  GraphicsEnvironment.isHeadless())` or conditional skip.
- **No test data files** — all test content is generated in-memory or in
  temporary directories.
- **Descriptive method names** following `methodUnderTest_condition_expectedResult`
  pattern.

## Excluded source code

The following source areas are excluded from coverage targets because they
require a live display, native libraries, or external services:

| Exclusion | Reason |
| --- | --- |
| `javax/swing/**` | Swing component wrappers — require headed environment |
| `image/**`, `AsynchronousIcon*` | Image rendering — requires `Graphics2D` |
| `DragAdapter*` | AWT drag-and-drop — requires mouse events |
| `SpringUtilities*` | Swing layout — requires container hierarchy |
| `GlyphVector*` | Font rendering — requires `FontRenderContext` |
| `SystemUtilities.getDefaultDirectory()` | Uses `FileSystemView` (Swing) |
| `SystemUtilities.loadLibrary()` | Loads native `.so`/`.dll` at runtime |
| `FileUtilities.getDefaultDirectory()` | Delegates to `SystemUtilities` Swing method |

## Source file locations

Test sources live alongside existing tests:

```
core/util/src/test/java/
├── edu/cmu/cs/dennisc/
│   ├── codec/
│   │   └── BinaryCodecRoundTripTest.java
│   ├── java/
│   │   ├── io/
│   │   │   ├── FileUtilitiesTest.java
│   │   │   └── TextFileUtilitiesTest.java
│   │   ├── lang/
│   │   │   ├── ArrayUtilitiesTest.java
│   │   │   ├── ClassUtilitiesTest.java
│   │   │   ├── SystemUtilitiesTest.java
│   │   │   ├── ThrowableUtilitiesTest.java
│   │   │   └── reflect/
│   │   │       └── ReflectionUtilitiesTest.java
│   │   └── util/
│   │       ├── BufferUtilitiesTest.java
│   │       ├── ListsTest.java
│   │       ├── MapsTest.java
│   │       ├── logging/
│   │       │   └── LoggerTest.java
│   │       └── zip/
│   │           └── ZipUtilitiesTest.java
│   ├── pattern/
│   │   └── LazyTest.java
│   ├── print/
│   │   └── PrintUtilitiesTest.java
│   └── xml/
│       └── XMLUtilitiesTest.java
```

## Running the tests

Run only `core/util` tests:

```sh
mvn test -pl core/util
```

Run with coverage reporting:

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/util=35.0
```

The `core/util=35.0` ratchet protects against regression below the new
coverage floor. See [Expand coverage ratchets](../howto/expand-coverage-ratchets.md)
for the ratchet-raising workflow.

## Coverage arithmetic

| Metric | Value |
| --- | --- |
| Total coverable lines (JaCoCo) | 10,001 |
| Previously covered lines | 363 |
| Previous coverage | 3.63% |
| New lines exercised (estimated) | ~3,660 |
| Total covered lines (estimated) | ~4,023 |
| New coverage (estimated) | ~40.2% |

The estimate carries ±15% variance. The margin classes (Maps, Lists,
ThrowableUtilities, Lazy) provide a safety buffer that absorbs estimation error.
