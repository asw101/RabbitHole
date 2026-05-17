# Raise core/util test coverage

Use this guide to understand, run, extend, and verify the `core/util` test
suite that raises line coverage from 3.63% to 40%+.

## Prerequisites

```sh
git submodule update --init tweedle-lang
```

Verify that `java -version` reports JDK 17+. The tests use JUnit 4 via the
parent POM's `junit` dependency and JUnit Jupiter 5.10.2 for any new tests in
the module (both are on the classpath).

## Run the tests

### Quick check — compile and test core/util only

```sh
mvn test -pl core/util
```

This skips unrelated modules and runs in under 60 seconds. All 71 new test
files plus the 12 existing test files execute.

### Full coverage report

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
```

Open the per-module HTML report:

```sh
open core/util/target/site/jacoco/index.html
```

Or generate the Markdown summary:

```sh
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/util=35.0
```

## Test architecture

### Phase 1 — High-impact tests (~2,100 lines)

These tests cover the largest untested utility classes:

1. **BinaryCodecRoundTripTest** — Encode→decode round-trips for all primitive
   types (boolean, byte, short, int, long, float, double, char), strings,
   enums, UUIDs, byte arrays, and Serializable objects. Exercises
   `OutputStreamBinaryEncoder` and `InputStreamBinaryDecoder` through the
   `AbstractBinaryEncoder`/`AbstractBinaryDecoder` abstract base classes.

2. **BufferUtilitiesTest** — NIO buffer ↔ array conversions for all primitive
   buffer types (`FloatBuffer`, `DoubleBuffer`, `IntBuffer`, `ShortBuffer`,
   `ByteBuffer`), direct buffer creation, and buffer-to-buffer copying.

3. **ReflectionUtilitiesTest** — Modifier checks (`isFinal`, `isStatic`,
   `isAbstract`), array class creation, `newInstance` for no-arg constructors,
   field/method/constructor access helpers, `invoke`, and public-static-final
   field introspection.

4. **FileUtilitiesTest** — Extension and basename parsing, file-content
   validation, directory listing with filters, file copy, timestamp queries.
   Uses `@Rule TemporaryFolder` exclusively.

5. **ZipUtilitiesTest** — Zip/unzip round-trips for single files and
   directories, filtered extraction, and `DataSource.write()` integration.

6. **PrintUtilitiesTest** — Push/pop of the static `PrintStream` stack,
   `append` formatting for primitives, arrays, and buffers, `toString`
   delegation, `println` output capture via `ByteArrayOutputStream`.

### Phase 2 — Medium-impact tests (~990 lines)

7. **ClassUtilitiesTest** — `forName` with primitives and class-name
   replacements, `getInstance`, assignability checks, array dimension
   calculation, package/class name parsing.

8. **SystemUtilitiesTest** — Boolean property accessors, platform detection
   (`isWindows`, `isMac`, `isLinux`), bit count, Java version parsing, path
   parsing, generic array creation.

9. **LoggerTest** — Logger singleton access, level management, log methods at
   `FINE`/`INFO`/`WARNING`/`SEVERE`, stdout/stderr capture to verify
   `ConsoleFormatter` output format and `SegregatingConsoleHandler` routing.

10. **XMLUtilitiesTest** — `DocumentBuilder` creation, document write/read
    round-trips, child element queries, whitespace text-node removal.

11. **TextFileUtilitiesTest** — Read/write round-trips via `File`,
    `InputStream`, `Reader`, and `Path` overloads. Tests UTF-8 encoding and
    multiline content.

### Phase 3 — Margin tests (~450 lines)

12. **ArrayUtilitiesTest** — `reverse`, `concat`, `createArray` from
    collections, `set`, typed array creation, `toString` variants.

13. **ListsTest** — Factory methods for `LinkedList`, `ArrayList`,
    `CopyOnWriteArrayList`, and primitive-array-to-list conversions.

14. **ThrowableUtilitiesTest** — Stack trace to `String` and to `byte[]`
    conversions.

15. **LazyTest** — Lazy initialization, memoization verification, and `peek`
    before/after `get`.

16. **MapsTest** — `HashMap`, `WeakHashMap`, `ConcurrentHashMap`, and
    `InitializingIfAbsentMap` factories.

## How to add more tests

### 1. Choose a target class

Look at the JaCoCo HTML report for `core/util`. Sort by "Missed Lines" to find
the largest uncovered classes. Exclude Swing/AWT-dependent classes listed in
the [reference](../reference/core-util-test-coverage.md#excluded-source-code).

### 2. Create the test file

Place it in the mirror package under `core/util/src/test/java/`. For example,
to test `edu.cmu.cs.dennisc.java.io.FileUtilities`:

```
core/util/src/test/java/edu/cmu/cs/dennisc/java/io/FileUtilitiesTest.java
```

### 3. Follow conventions

```java
package edu.cmu.cs.dennisc.java.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class FileUtilitiesTest {

  @Rule
  public TemporaryFolder tempDir = new TemporaryFolder();

  @Test
  public void getExtension_withDottedFilename_returnsExtension() {
    assertEquals("txt", FileUtilities.getExtension("readme.txt"));
  }
}
```

Key rules:

- Use JUnit 4 imports (`org.junit.Test`, `org.junit.Assert`).
- Use `@Rule TemporaryFolder` for file system tests.
- Save and restore static state in `@Before`/`@After`.
- Guard AWT-dependent paths with
  `assumeFalse(GraphicsEnvironment.isHeadless())`.
- Name methods: `methodUnderTest_condition_expectedResult`.

### 4. Verify

```sh
mvn test -pl core/util -Dtest=YourNewTest
```

### 5. Update the ratchet

After merging, follow [Expand coverage ratchets](expand-coverage-ratchets.md)
to raise the `core/util` floor if measured coverage has increased.

## Static state management

Several utility classes use static singletons or stacks. Tests that mutate
these must save the original state in `@Before` and restore it in `@After`:

| Class | Static state | Restoration pattern |
| --- | --- | --- |
| `PrintUtilities` | `PrintStream` stack (push/pop) | Pop all pushed streams in `@After` |
| `Logger` | Singleton logger level and handlers | Save level in `@Before`, restore in `@After` |
| `SystemUtilities` | System properties | Save property values, restore with `System.setProperty` or `System.clearProperty` |

This prevents test pollution when Maven runs tests in the same JVM fork.

## Known limitations

- **Coverage estimate variance**: Actual JaCoCo line coverage may differ ±15%
  from the raw-line-count estimates because JaCoCo counts coverable
  instructions, not raw source lines.
- **HeadlessException**: `FileUtilities.getDefaultDirectory()` calls Swing's
  `FileSystemView` — it is skipped in all test environments.
- **Zip Slip vulnerability**: `ZipUtilities` line 277 does not validate entry
  paths during extraction. This is a pre-existing security issue noted but not
  fixed in this coverage effort.
- **Codec stream format**: `OutputStreamBinaryEncoder` writes an
  `ObjectOutputStream` header; round-trip tests must use matched
  encoder/decoder pairs.

## Troubleshooting

### Tests fail with `HeadlessException`

Set the system property when running Maven:

```sh
mvn test -pl core/util -Djava.awt.headless=true
```

The CI pipeline sets this automatically.

### Logger tests interfere with other tests

Logger tests mutate the singleton `java.util.logging.Logger` for the
`edu.cmu.cs.dennisc` hierarchy. If you see unexpected log output in other test
classes, ensure the `LoggerTest` `@After` method is restoring the original
level and handlers. Running with `-DforkCount=1 -DreuseForks=false` isolates
each test class in its own JVM.

### Coverage report shows 0% for a new test

Ensure the test class is in the correct package directory under
`core/util/src/test/java/` and that the class name ends with `Test`. The
Surefire plugin discovers tests by naming convention.
