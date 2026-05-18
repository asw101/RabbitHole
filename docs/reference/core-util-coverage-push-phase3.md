# core/util coverage push phase 3 — 51.6% → 70%

Issue #775 raises `core/util` line coverage from 51.6% to 70%+ by adding
27 test files for AWT utilities, animation framework deepening, codec edge
cases, image/texture utilities, and miscellaneous utility classes. All new
tests are JUnit 4, headless-safe, and require no external dependencies beyond
the existing test classpath.

This push builds on Phase 1 (#736, 3.63% → ~43%) and Phase 2 (#751, ~43% →
~51.6%). Phase 3 targets the AWT utility classes (pure data manipulation —
no peer creation), animation framework deep branches, image/texture helpers,
and remaining utility gaps.

## Coverage model

```
Baseline covered:  ~4,200 lines  (51.6% of ~8,140 total)
New direct:       +1,500 lines
New transitive:   +1,200 lines   (shared utility paths, codec infrastructure)
────────────────────────────────
Projected covered: ~6,900 lines  (~84.8% — well above 70% target)
```

The conservative 70% target accounts for untestable GUI-dependent paths that
will never be reached headlessly.

## Test inventory

### Tier 1 — AWT utilities (7 files, ~500 lines)

The `edu.cmu.cs.dennisc.java.awt` package contains pure geometric and
typographic utilities that operate on AWT data types (`Rectangle`, `Dimension`,
`Font`, `Area`) without creating display peers. All methods are static and
stateless.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `RectangleUtilitiesTest` | `RectangleUtilities` (grow, inset, getPoint, setBounds) | 100 |
| `DimensionUtilitiesTest` | `DimensionUtilities` (constrain, scale, aspect ratio) | 60 |
| `FontUtilitiesTest` | `FontUtilities` (derive, scale, family lookup) | 80 |
| `BeveledShapeTest` | `BeveledShape` (beveled rectangle construction, contains) | 60 |
| `MultilineTextTest` | `MultilineText` (line splitting, measurement with null Graphics) | 60 |
| `MouseEventUtilitiesTest` | `MouseEventUtilities` (button detection, modifier extraction) | 60 |
| `AreaUtilitiesTest` | `AreaUtilities` (union, subtract, transform) | 80 |

**Headless safety:** These classes use `java.awt.geom.*` and `java.awt.Font`
data types, which are pure-data in headless mode. No `Graphics` context or
display connection is required. `MultilineText` tests pass `null` for the
`Graphics` parameter where supported, or use `BufferedImage.createGraphics()`
which works headlessly.

### Tier 2 — Animation framework deepening (7 files, ~550 lines)

Phase 2 established the animation framework tests. Phase 3 deepens branch
coverage with edge cases: zero-duration animations, cancellation mid-flight,
observer notification ordering, and thread lifecycle.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `AbstractAnimatorTest` | `AbstractAnimator` (add/remove animation, flush, speed factor) | 100 |
| `AbstractAnimationTest` | `AbstractAnimation` (lifecycle hooks, exception handling) | 80 |
| `WaitingAnimationTest` | `WaitingAnimation` (zero wait, cancel during wait) | 60 |
| `TraditionalStyleTest` | `TraditionalStyle` (all 7 ease curves, boundary values 0.0/1.0) | 80 |
| `DurationBasedAnimationTest` | `DurationBasedAnimation` (negative duration, progress callback) | 80 |
| `InterpolationAnimationTest` | `InterpolationAnimation` (start/end interpolation, overshoot) | 80 |
| `AnimationThreadTest` | `AnimationThread` (start, interrupt, clean shutdown) | 70 |

**Test strategy:** Tests use `TestAnimation extends DurationBasedAnimation`
inner classes that record invocation order. A `StubAnimator` provides a
controllable clock for deterministic timing without real thread sleeps.

### Tier 3 — Codec edge cases (3 files, ~250 lines)

These tests extend the existing `BinaryCodecRoundTripTest` with deeper
boundary and error paths.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `BinaryCodecEdgeCasesTest` | `OutputStreamBinaryEncoder`, `InputStreamBinaryDecoder` (NaN, Infinity, MAX_VALUE, empty arrays, null strings, large arrays) | 120 |
| `InputStreamBinaryDecoderTest` | `InputStreamBinaryDecoder` (EOF handling, type mismatch, truncated stream) | 80 |
| `OutputStreamBinaryEncoderTest` | `OutputStreamBinaryEncoder` (flush behavior, close semantics, nested encodable) | 50 |

**Test strategy:** Round-trip tests write to `ByteArrayOutputStream`, then
read back from `ByteArrayInputStream`. Edge cases test IEEE 754 special
values and empty/null containers.

### Tier 4 — Image and texture utilities (4 files, ~300 lines)

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `ImageUtilitiesTest` | `ImageUtilities` (read, write, scale, crop, format detection) | 100 |
| `TgaUtilitiesTest` | `TgaUtilities` (TGA header parsing, pixel format, RLE decode) | 80 |
| `BufferedImageTextureTest` | `BufferedImageTexture` (construction, dimension query, mipmap) | 60 |
| `PngUtilitiesTest` | `PngUtilities` (PNG metadata, transparency detection) | 60 |

**Test strategy:** Tests create small in-memory `BufferedImage` instances
(1×1, 4×4, 16×16) with known pixel values. No file I/O — all operations
use byte arrays. TGA tests use hand-crafted byte arrays matching the TGA
header specification.

### Tier 5 — Miscellaneous utilities (6 files, ~400 lines)

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `GlyphVectorTest` | `GlyphVector` utilities (glyph bounds, outline extraction) | 60 |
| `ApplicationRootTest` | `ApplicationRoot` (root directory resolution, platform detection) | 80 |
| `ProcessWorkerTest` | `ProcessWorker` (spawn, stdout/stderr capture, timeout) | 80 |
| `UndoRedoManagerTest` | `UndoRedoManager` (push, undo, redo, clear, listener notification) | 80 |
| `AudioResourceTest` | `AudioResource` (construction, content type, byte array access) | 40 |
| `SystemUtilitiesDeepTest` | `SystemUtilities` (property lookup, platform detection edge cases) | 60 |

**Test strategy:** `ProcessWorkerTest` spawns `echo` or `cat` subprocesses.
`ApplicationRootTest` uses a temp directory as the application root.
`UndoRedoManager` tests are pure-logic with no UI dependency.

## Patterns

### AWT data-only testing

```java
@Test
public void grow_symmetricPad_expandsBothAxes() {
  Rectangle r = new Rectangle(10, 10, 20, 20);
  Rectangle result = RectangleUtilities.grow(r, 5);
  assertEquals(new Rectangle(5, 5, 30, 30), result);
}
```

### In-memory image creation

```java
private static BufferedImage createTestImage(int w, int h, int argb) {
  BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
  for (int y = 0; y < h; y++) {
    for (int x = 0; x < w; x++) {
      img.setRGB(x, y, argb);
    }
  }
  return img;
}
```

### TGA byte array construction

```java
private static byte[] createMinimalTga(int width, int height) {
  byte[] header = new byte[18];
  header[2] = 2; // uncompressed true-color
  header[12] = (byte) (width & 0xFF);
  header[13] = (byte) ((width >> 8) & 0xFF);
  header[14] = (byte) (height & 0xFF);
  header[15] = (byte) ((height >> 8) & 0xFF);
  header[16] = 32; // 32 bits per pixel
  byte[] pixels = new byte[width * height * 4];
  return concat(header, pixels);
}
```

## Exclusions

| Class/Package | Reason |
| --- | --- |
| `edu.cmu.cs.dennisc.render.*` | OpenGL rendering pipeline — requires GPU |
| `edu.cmu.cs.dennisc.java.awt.print.*` | Print service — requires printer drivers |
| `edu.cmu.cs.dennisc.javax.swing.*` | Swing component creation — requires display |
| `edu.cmu.cs.dennisc.video.*` | Video encoding — requires native libraries |

## Verification

```bash
# Run all core/util tests
mvn test -pl core/util

# Run only Phase 3 AWT utility tests
mvn test -pl core/util \
  -Dtest="edu.cmu.cs.dennisc.java.awt.*Test"

# Run only Phase 3 image tests
mvn test -pl core/util \
  -Dtest="edu.cmu.cs.dennisc.image.*Test,edu.cmu.cs.dennisc.texture.*Test"

# Generate coverage report
mvn verify -pl core/util
# Open core/util/target/site/jacoco/index.html

# Full aggregate verification
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --min-module-line-percent core/util=65.0
```
