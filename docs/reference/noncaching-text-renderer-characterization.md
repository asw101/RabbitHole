# NonCachingTextRenderer Characterization

This reference is the build contract for the `NonCachingTextRenderer`
characterization lane: headless-safe inner class behavior, buffer constant
identity, glyph cache lifecycle, text data accessors, character iterator
compliance, and GL-boundary skip rules.

## Contents

- [Scope](#scope)
- [Artifact inventory](#artifact-inventory)
- [Inner class contracts](#inner-class-contracts)
- [API reference](#api-reference)
- [Validation commands](#validation-commands)
- [Compatibility rules](#compatibility-rules)
- [Examples](#examples)

## Scope

This lane characterizes observable `NonCachingTextRenderer` behavior that is
testable without an OpenGL context, display server, or native JOGL bindings.
It documents the headless-safe inner classes, static constants, and pure-logic
helper methods that form the foundation of Alice's OpenGL text rendering
pipeline.

It covers:

| Area | Contract |
| --- | --- |
| Buffer constants | Static constant values and derived buffer sizes are self-consistent: `kSize`, `kQuadsPerBuffer`, `kVertsPerQuad`, `kCoordsPerVertVerts`, `kCoordsPerVertTex`, and the computed totals `kTotalBufferSizeVerts`, `kTotalBufferSizeCoordsVerts`, `kTotalBufferSizeCoordsTex`, `kTotalBufferSizeBytesVerts`, `kTotalBufferSizeBytesTex`, `kSizeInBytes_OneVertices_VertexData`, `kSizeInBytes_OneVertices_TexData`. |
| CharSequenceIterator | The private static inner class implements `CharacterIterator` correctly: `first()`, `last()`, `current()`, `next()`, `previous()`, `setIndex()`, `getBeginIndex()`, `getEndIndex()`, `getIndex()`, `clone()`, and empty-sequence edge cases. |
| TextData | The package-private static inner class preserves constructor arguments through accessor methods: `string()`, `origin()`, `origRect()`, `origOriginX()`, `origOriginY()`, and the `used`/`markUsed()`/`clearUsed()` lifecycle. |
| DefaultRenderDelegate | The public static inner class returns `true` from `intensityOnly()` and delegates `getBounds(GlyphVector, FontRenderContext)` to `GlyphVector.getVisualBounds()`. |
| CharacterCache | The private static inner class caches `Character` values for codepoints 0–127 and returns fresh `Character.valueOf()` for codepoints > 127. |
| Glyph | The non-static inner class stores unicode ID, glyph code, advance, and string fields through its constructors, and `clear()` nulls `glyphRectForTextureMapping`. |
| GlyphProducer | The non-static inner class initializes `unicodes2Glyphs` (length 512, all `undefined`) and `glyphCache` (length = font glyph count), and `register()` / `clearAllCacheEntries()` round-trip correctly. |

This lane does not cover:

- OpenGL rendering (`beginRendering`, `endRendering`, `draw3D`, `draw3D_ROBUST`)
- Texture allocation (`Manager.allocateBackingStore`)
- VBO pipeline (`Pipelined_QuadRenderer`)
- Font metrics that require a live `FontRenderContext` from a GL surface
- Mipmap generation or backing store compaction
- Full `getBounds(CharSequence)` with cached string locations

## Artifact inventory

| Artifact | Purpose |
| --- | --- |
| `core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java` | Production class (1842 lines). Contains all inner classes under test. |
| `core/glrender/src/test/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRendererCharacterizationTest.java` | Characterization test suite (~466 lines, 38 test methods). First test file in `core/glrender`. |

## Inner class contracts

### Constants (lines 57–79)

The buffer size constants form a derivation chain:

```text
kQuadsPerBuffer = 100
kVertsPerQuad = 4
kCoordsPerVertVerts = 3
kCoordsPerVertTex = 2

kTotalBufferSizeVerts = kQuadsPerBuffer × kVertsPerQuad = 400
kTotalBufferSizeCoordsVerts = kTotalBufferSizeVerts × kCoordsPerVertVerts = 1200
kTotalBufferSizeCoordsTex = kTotalBufferSizeVerts × kCoordsPerVertTex = 800
kTotalBufferSizeBytesVerts = kTotalBufferSizeCoordsVerts × 4 = 4800
kTotalBufferSizeBytesTex = kTotalBufferSizeCoordsTex × 4 = 3200
kSizeInBytes_OneVertices_VertexData = kCoordsPerVertVerts × 4 = 12
kSizeInBytes_OneVertices_TexData = kCoordsPerVertTex × 4 = 8
kSize = 256
DISABLE_GLYPH_CACHE = true
```

The characterization tests verify each derivation step. If any constant
changes, the tests will fail, alerting reviewers to recalculate downstream
buffer allocations.

### CharSequenceIterator (lines 803–891)

A private static inner class implementing `java.text.CharacterIterator` for
`CharSequence` inputs. Key behavioral contracts:

| Method | Behavior |
| --- | --- |
| `first()` on empty | Returns `CharacterIterator.DONE`, index stays at 0. |
| `first()` on non-empty | Returns first char, sets index to 0. |
| `last()` on empty | Returns `CharacterIterator.DONE`, index stays at 0. |
| `last()` on non-empty | Returns last char, sets index to `length - 1`. |
| `current()` at end | Returns `CharacterIterator.DONE` when index ≥ length. |
| `next()` past end | Increments index, returns `DONE`. |
| `previous()` at start | Clamps index to 0, returns first char. |
| `setIndex(n)` | Sets index to `n`, returns char at that position. |
| `getBeginIndex()` | Always returns 0. |
| `getEndIndex()` | Returns sequence length. |
| `clone()` | Returns a new `CharSequenceIterator` with the same sequence and current index. |

The tests access this class via reflection because it is `private static`.

### TextData (lines 893–960)

A package-private static inner class storing text rendering metadata:

| Field / Method | Contract |
| --- | --- |
| `string()` | Returns the `str` argument from the constructor, or `null` for single-glyph entries. |
| `origin()` | Returns the `Point` passed at construction. |
| `origRect()` | Returns the `Rectangle2D` passed at construction. |
| `origOriginX()` | Returns `(int) -origRect.getMinX()`. |
| `origOriginY()` | Returns `(int) -origRect.getMinY()`. |
| `used()` / `markUsed()` / `clearUsed()` | Boolean lifecycle: starts `false`, `markUsed()` sets `true`, `clearUsed()` resets to `false`. |
| `unicodeID` | Stores the unicode ID from the constructor (public field). |

### DefaultRenderDelegate (lines 1145–1180)

A public static inner class implementing `TextRenderer.RenderDelegate`:

| Method | Contract |
| --- | --- |
| `intensityOnly()` | Always returns `true`. |
| `getBounds(CharSequence, Font, FRC)` | Creates a `GlyphVector` via `font.createGlyphVector(frc, CharSequenceIterator)`, then delegates to `getBounds(GlyphVector, FRC)`. |
| `getBounds(String, Font, FRC)` | Creates a `GlyphVector` via `font.createGlyphVector(frc, str)`, then delegates to `getBounds(GlyphVector, FRC)`. |
| `getBounds(GlyphVector, FRC)` | Returns `gv.getVisualBounds()`. |
| `drawGlyphVector(g, gv, x, y)` | Delegates to `g.drawGlyphVector(gv, x, y)`. |
| `draw(g, str, x, y)` | Delegates to `g.drawString(str, x, y)`. |

The bounds methods are tested with non-empty strings to verify non-null,
positive-dimension results. Exact pixel values are not asserted because font
metrics vary across platforms and JDK versions.

### CharacterCache (lines 1557–1575)

A private static inner class caching `Character` objects for ASCII codepoints:

| Behavior | Contract |
| --- | --- |
| `cache` array | Length 128 (indices 0–127). Each entry is `Character.valueOf((char) i)`. |
| `valueOf(c)` where `c ≤ 127` | Returns the cached `Character` object (same identity across calls). |
| `valueOf(c)` where `c > 127` | Returns `Character.valueOf(c)` (a fresh object). |
| Boundary: `valueOf((char) 127)` | Returns cached object. |
| Boundary: `valueOf((char) 128)` | Returns non-cached object. |

Tests access this class via reflection and verify identity semantics with
`assertSame()` for cached values.

### Glyph (lines 1201–1392)

A non-static inner class representing a unicode glyph or character substring:

| Constructor | Fields set |
| --- | --- |
| `Glyph(unicodeID, glyphCode, advance, glyphVector, producer)` | Individual unicode glyph with all rendering fields. |
| `Glyph(str, needAdvance)` | String fallback glyph for complex text sequences. |

| Method | Contract |
| --- | --- |
| `getUnicodeID()` | Returns the `unicodeID` field. |
| `getGlyphCode()` | Returns the `glyphCode` field. |
| `getAdvance()` | Returns the `advance` field. |
| `clear()` | Sets `glyphRectForTextureMapping` to `null`. |

**GL dependency:** Constructing a `Glyph` requires an enclosing
`NonCachingTextRenderer` instance, which calls `new RectanglePacker(...)` in
its constructor. In headless CI, the `RectanglePacker` constructor may trigger
`Manager.allocateBackingStore()` which requires a GL context. Tests guard
construction with `Assume.assumeTrue` and skip gracefully when GL is
unavailable.

### GlyphProducer (lines 1394–1555)

A non-static inner class managing the glyph cache:

| Method | Contract |
| --- | --- |
| Constructor | Allocates `unicodes2Glyphs[512]` filled with `undefined` (-2), and `glyphCache[fontLengthInGlyphs]`. |
| `clearAllCacheEntries()` | Iterates 0–511, setting each `unicodes2Glyphs` entry to `undefined`. |
| `register(glyph)` | Sets `unicodes2Glyphs[glyph.unicodeID] = glyph.glyphCode` and `glyphCache[glyph.glyphCode] = glyph`. |
| `clearCacheEntry(unicodeID)` | Clears the glyph at the given unicode ID and resets the mapping to `undefined`. |
| `getGlyphs(CharSequence)` | Requires `getFontRenderContext()` → **GL-dependent, not tested headlessly**. |
| `getGlyphPixelWidth(char)` | Returns `glyph.getAdvance()` for cached glyphs. For uncached glyphs, falls through to `fontRenderContext` which is never initialized → **throws `InternalError` (FIXME in source)**. |

**GL dependency:** Same as `Glyph` — requires an enclosing
`NonCachingTextRenderer`. Tests guard with `Assume.assumeTrue`.

## API reference

The test suite uses reflection to access private and package-private inner
classes. The reflection targets are:

| Reflection target | Access pattern |
| --- | --- |
| `NonCachingTextRenderer$CharSequenceIterator` | `Class.forName(...)`, constructor via `getDeclaredConstructor(CharSequence.class)` with `setAccessible(true)`. |
| `NonCachingTextRenderer$TextData` | Direct constructor access (package-private). |
| `NonCachingTextRenderer$CharacterCache` | `Class.forName(...)`, `valueOf` method via `getDeclaredMethod("valueOf", char.class)` with `setAccessible(true)`, `cache` field via `getDeclaredField("cache")` with `setAccessible(true)`. |
| `Glyph` fields | `getDeclaredField("glyphRectForTextureMapping")` with `setAccessible(true)` to verify `clear()`. |

If any inner class is renamed, the tests fail with a descriptive message
naming the expected class rather than a silent skip.

## Validation commands

### Run the full characterization suite

```bash
git submodule update --init tweedle-lang
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=NonCachingTextRendererCharacterizationTest \
  test
```

Expected outcome: 38 test methods. 31 pass, 7 skipped (GL-dependent
`Glyph`/`GlyphProducer` tests guarded by `Assume.assumeTrue`). 0 failures, 0
errors.

### Run alongside all core/glrender tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am -DfailIfNoTests=false test
```

The characterization suite is the first test file in `core/glrender`. It runs
alongside any future tests without interference.

## Compatibility rules

1. **Do not assert exact font metrics.** Font rendering varies across platforms,
   JDK versions, and installed fonts. Assert `> 0`, `non-null`, or structural
   properties only.

2. **Do not remove `Assume.assumeTrue` guards.** GL-dependent tests must skip
   cleanly in headless CI. Converting them to `@Ignore` loses the ability to
   run them in a full GL environment.

3. **Do not change constant values without recalculating downstream buffers.**
   The constant derivation chain is load-bearing: VBO allocation, texture
   coordinate scaling, and quad vertex positioning all depend on these exact
   values.

4. **Reflection targets are fragile by design.** If an inner class is renamed
   during refactoring, the corresponding test fails immediately with a clear
   `Class.forName` or `getDeclaredMethod` error. This is the intended safety
   net — fix the test target names as part of the rename.

5. **`CharSequenceIterator` boundary behavior is load-bearing.** The `previous()`
   clamping to index 0 and the `DONE` sentinel for empty sequences are
   used by `GlyphProducer.getGlyphs()` during text layout. Changes to these
   behaviors require updating both the tests and all callers.

6. **`DISABLE_GLYPH_CACHE = true` is the current production value.** This means
   `GlyphProducer.getGlyphs()` always takes the "punt to robust renderer"
   path. The glyph cache data structures (`unicodes2Glyphs`, `glyphCache`) are
   allocated but effectively unused in production. Tests characterize both the
   allocation and the cache operations to protect against regressions if the
   flag is later set to `false`.

## Examples

### Verify a constant change is safe

Before changing `kQuadsPerBuffer` from 100 to 200:

1. Run the characterization suite (see [Validation commands](#validation-commands)).
2. The constant tests fail, showing the old expected values.
3. Recalculate all derived constants.
4. Update the test expectations to match.
5. Verify that VBO allocation in `Pipelined_QuadRenderer` uses the same
   constants and that buffer sizes are consistent.

### Add a new headless-safe inner class test

1. Identify the inner class and its access level.
2. If `private static`, use `Class.forName` + `setAccessible(true)`.
3. If non-static (requires enclosing instance), guard with
   `Assume.assumeTrue` in case the constructor triggers GL.
4. Assert observable behavior (return values, field state), not implementation
   details.
5. Run the validation command and verify the new test appears in the results.

### Understand why Glyph tests are skipped

The `Glyph` and `GlyphProducer` inner classes are non-static. Creating one
requires a `NonCachingTextRenderer` instance. The `NonCachingTextRenderer`
constructor calls `new RectanglePacker(new Manager(), kSize, kSize)`, which
may trigger `Manager.allocateBackingStore()`. On a headless CI server without
native JOGL libraries, this fails. The tests guard against this with:

```java
Assume.assumeTrue("Requires GL context for NonCachingTextRenderer construction",
    renderer != null);
```

When the assumption fails, JUnit marks the test as skipped (not failed). In a
full desktop or Xvfb environment with JOGL native libraries, these tests run
and pass.
