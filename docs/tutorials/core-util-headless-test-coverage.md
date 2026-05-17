# Tutorial: Write headless-safe tests for core/util

This tutorial walks through writing a headless-safe utility test from scratch,
using `BufferUtilities` as the example. By the end you will understand the
pattern used by all 16 test files in the `core/util` coverage expansion.

## Prerequisites

- JDK 17+ installed
- Maven available on `PATH`
- Tweedle grammar submodule initialized:
  ```sh
  git submodule update --init tweedle-lang
  ```

## Step 1: Identify the target class

Open the JaCoCo report or inspect the source directly:

```sh
wc -l core/util/src/main/java/edu/cmu/cs/dennisc/java/util/BufferUtilities.java
```

`BufferUtilities` is ~220 lines of pure logic — NIO buffer conversions with no
AWT, Swing, or native dependencies. This makes it an ideal headless test
target.

## Step 2: Read the source API

```sh
grep -n 'public static' \
  core/util/src/main/java/edu/cmu/cs/dennisc/java/util/BufferUtilities.java
```

You'll see methods like:

```
copyFloatBuffer(FloatBuffer) → FloatBuffer
createDirectFloatBuffer(float[]) → FloatBuffer
convertFloatBufferToArray(FloatBuffer) → float[]
convertDoubleBufferToArray(DoubleBuffer) → double[]
```

Each method is a pure function — takes input, returns output, no side effects.

## Step 3: Create the test file

Create the test in the mirror package:

```sh
mkdir -p core/util/src/test/java/edu/cmu/cs/dennisc/java/util/
```

Write the test class:

```java
package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.junit.Assert.*;

public class BufferUtilitiesTest {

  // -- FloatBuffer round-trips --

  @Test
  public void convertFloatBufferToArray_fromHeapBuffer_roundTrips() {
    float[] input = {1.0f, 2.5f, -3.14f, 0.0f};
    FloatBuffer buffer = FloatBuffer.wrap(input);
    float[] result = BufferUtilities.convertFloatBufferToArray(buffer);
    assertArrayEquals(input, result, 0.0f);
  }

  @Test
  public void createDirectFloatBuffer_preservesValues() {
    float[] input = {10.0f, 20.0f, 30.0f};
    FloatBuffer direct = BufferUtilities.createDirectFloatBuffer(input);
    assertTrue("Should be a direct buffer", direct.isDirect());
    assertEquals(input.length, direct.capacity());
    for (int i = 0; i < input.length; i++) {
      assertEquals(input[i], direct.get(i), 0.0f);
    }
  }

  @Test
  public void copyFloatBuffer_createsIndependentCopy() {
    float[] input = {1.0f, 2.0f, 3.0f};
    FloatBuffer original = FloatBuffer.wrap(input);
    FloatBuffer copy = BufferUtilities.copyFloatBuffer(original);
    assertNotSame(original, copy);
    assertEquals(original.capacity(), copy.capacity());
    // Mutating original data doesn't affect the copy
    input[0] = 999.0f;
    assertNotEquals(999.0f, copy.get(0), 0.0f);
  }

  // -- DoubleBuffer round-trips --

  @Test
  public void convertDoubleBufferToArray_fromHeapBuffer_roundTrips() {
    double[] input = {1.0, 2.5, -3.14, 0.0, Double.MAX_VALUE};
    DoubleBuffer buffer = DoubleBuffer.wrap(input);
    double[] result = BufferUtilities.convertDoubleBufferToArray(buffer);
    assertArrayEquals(input, result, 0.0);
  }

  // -- Empty buffers --

  @Test
  public void convertFloatBufferToArray_emptyBuffer_returnsEmptyArray() {
    FloatBuffer buffer = FloatBuffer.allocate(0);
    float[] result = BufferUtilities.convertFloatBufferToArray(buffer);
    assertEquals(0, result.length);
  }

  // -- IntBuffer --

  @Test
  public void convertIntBufferToArray_fromHeapBuffer_roundTrips() {
    int[] input = {1, -2, Integer.MAX_VALUE, 0};
    IntBuffer buffer = IntBuffer.wrap(input);
    int[] result = BufferUtilities.convertIntBufferToArray(buffer);
    assertArrayEquals(input, result);
  }
}
```

## Step 4: Run the test

```sh
mvn test -pl core/util -Dtest=BufferUtilitiesTest
```

All tests should pass. The test creates no files, opens no windows, and has no
external dependencies.

## Step 5: Verify coverage

```sh
mvn -pl core/util -Pcoverage verify
```

Open `core/util/target/site/jacoco/index.html` and navigate to
`edu.cmu.cs.dennisc.java.util > BufferUtilities`. You should see the tested
methods highlighted green.

## Patterns used across all 16 test files

### Pattern: TemporaryFolder for file tests

```java
@Rule
public TemporaryFolder tempDir = new TemporaryFolder();

@Test
public void writeAndRead_roundTrips() throws Exception {
  File f = tempDir.newFile("test.txt");
  TextFileUtilities.write(f, "hello");
  assertEquals("hello", TextFileUtilities.read(f));
}
```

The `TemporaryFolder` rule creates a unique directory per test run and deletes
it automatically. Never use `File.createTempFile` with `deleteOnExit()`.

### Pattern: Static state save/restore

```java
private PrintStream originalOut;

@Before
public void saveState() {
  originalOut = System.out;
}

@After
public void restoreState() {
  System.setOut(originalOut);
}
```

`PrintUtilitiesTest` and `LoggerTest` use this pattern to prevent test
pollution when mutating global singletons.

### Pattern: Headless guard

```java
@Test
public void someGuiMethod_inHeadedMode_works() {
  assumeFalse("Skipped in headless CI",
      GraphicsEnvironment.isHeadless());
  // test GUI behavior
}
```

Most `core/util` tests don't need this because they test pure logic. It's used
only in `WindowStackTest` and `FileDialogUtilitiesTest`.

### Pattern: Encode→decode round-trip

The codec classes (`OutputStreamBinaryEncoder`, `InputStreamBinaryDecoder`) do
not implement `AutoCloseable`, so use explicit instantiation and `flush()`:

```java
@Test
public void encodeDecodeInt_roundTrips() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  BinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
  encoder.encode(42);
  encoder.flush();

  BinaryDecoder decoder = new InputStreamBinaryDecoder(
      new ByteArrayInputStream(baos.toByteArray()));
  assertEquals(42, decoder.decodeInt());
}
```

`BinaryCodecRoundTripTest` uses this pattern for every primitive type, strings,
enums, UUIDs, and byte arrays. The round-trip proves that the encoder and
decoder are symmetric.

### Pattern: Reflection on test-owned classes

```java
private static class TestTarget {
  public static final String CONSTANT = "value";
  private int field;
  public TestTarget() {}
  public int getField() { return field; }
}

@Test
public void getPublicStaticFinalFields_findsConstant() {
  // Reflect only on TestTarget, never on system classes
}
```

`ReflectionUtilitiesTest` defines small inner classes as reflection targets.
This avoids depending on the internal layout of JDK classes.

## What not to test

- **Swing/AWT rendering** — `javax.swing` wrappers, `Graphics2D` delegates,
  drag adapters, icon renderers. These require a display server.
- **Native library loading** — `SystemUtilities.loadLibrary()` loads
  platform-specific `.so`/`.dll` files.
- **External services** — `RestUtilities` (Jira), mail API code. These have
  network dependencies.
- **Pre-existing bugs** — `ZipUtilities` has a Zip Slip vulnerability at line
  277. Note it but don't fix it in a coverage PR.

## Next steps

- Read the [reference](../reference/core-util-test-coverage.md) for the
  complete test inventory and coverage arithmetic.
- Follow [Expand coverage ratchets](../howto/expand-coverage-ratchets.md) to
  add a `core/util` ratchet after merging.
- Check the JaCoCo HTML report for remaining uncovered pure-logic methods and
  add more tests.
