# NonCachingTextRenderer Pipeline Extraction

This reference documents the extraction of 6 rendering pipeline methods from
`NonCachingTextRenderer` into a new package-private `TextRendererPipeline`
delegate class (issue #537). This is Phase 3 of the `NonCachingTextRenderer`
reduction, following inner class extraction (Phases 1–2, issues #514/#524).

## Contents

- [Motivation](#motivation)
- [Extracted methods](#extracted-methods)
- [File inventory](#file-inventory)
- [Delegate pattern](#delegate-pattern)
- [Visibility changes](#visibility-changes)
- [Retained delegators](#retained-delegators)
- [Validation commands](#validation-commands)
- [Compatibility rules](#compatibility-rules)
- [Examples](#examples)

## Motivation

After inner class extraction (Phases 1–2), `NonCachingTextRenderer.java` was
842 lines. The file still contained the full rendering pipeline: begin/end
rendering, glyph pipeline flush, 3D text draw, robust string rasterization,
and debug visualization — roughly 200 lines of implementation logic.

Phase 3 extracts these rendering pipeline methods into `TextRendererPipeline`,
a package-private delegate class. This brings `NonCachingTextRenderer` under
650 lines, containing only the public API surface, thin delegators, field
declarations, and utility methods.

## Extracted methods

| Method | Signature | Lines | Purpose |
| --- | --- | --- | --- |
| `beginRendering` | `(boolean ortho, int width, int height, boolean disableDepthTestForOrtho)` | ~52 | Sets GL state for text rendering: pushes client attribs, queries max texture size, resets cached color, initializes ortho or 3D backing store rendering. |
| `endRendering` | `(boolean ortho)` | ~36 | Flushes glyph pipeline, pops GL client attribs, zeros array buffer binding, ends ortho or 3D backing store rendering, triggers periodic `clearUnusedEntries`. |
| `internal_draw3D` | `(CharSequence str, float x, float y, float z, float scaleFactor)` | ~5 | Iterates glyphs from `GlyphProducer` and draws each with accumulated x-advance. |
| `flushGlyphPipeline` | `()` | ~4 | Delegates to `mPipelinedQuadRenderer.draw()` if the quad renderer is non-null. |
| `draw3D_ROBUST` | `(CharSequence str, float x, float y, float z, float scaleFactor)` | ~80 | Looks up or rasterizes a string on the backing store, then draws the texture rectangle at the requested 3D coordinate. |
| `debug` | `(GL gl)` | ~27 | Creates a debug visualization frame with a `GLCanvas` and `FPSAnimator` for inspecting the backing store. |

**Total extracted:** ~204 lines of method bodies.

## File inventory

| File | Role | Approx lines |
| --- | --- | --- |
| `NonCachingTextRenderer.java` | Public API surface, thin delegators, fields, constructor, utility methods. | ~626 |
| `TextRendererPipeline.java` | Package-private delegate. Owns 6 rendering pipeline method implementations. | ~253 |
| `TextRendererGlyph.java` | Extracted `Glyph` (Phase 1). | ~200 |
| `TextRendererGlyphProducer.java` | Extracted `GlyphProducer` (Phase 1). | ~170 |
| `TextRendererQuadRenderer.java` | Extracted `Pipelined_QuadRenderer` (Phase 1). | ~165 |
| `CharSequenceIterator.java` | Extracted `CharSequenceIterator` (Phase 2). | ~90 |
| `TextData.java` | Extracted `TextData` (Phase 2). | ~70 |
| `Manager.java` | Extracted `Manager` (Phase 2). | ~185 |
| `DefaultRenderDelegate.java` | Extracted `DefaultRenderDelegate` (Phase 2). | ~40 |
| `CharacterCache.java` | Extracted `CharacterCache` (Phase 2). | ~25 |
| `DebugListener.java` | Extracted `DebugListener` (Phase 2). | ~60 |

All source files reside in
`core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/`.

## Delegate pattern

`TextRendererPipeline` takes a `NonCachingTextRenderer` reference in its
constructor. All back-references to the enclosing renderer's state (fields,
backing store, glyph producer) go through this reference.

```java
// TextRendererPipeline constructor
TextRendererPipeline(NonCachingTextRenderer renderer) {
    this.renderer = renderer;
}
```

`NonCachingTextRenderer` creates the pipeline in its own constructor:

```java
// In NonCachingTextRenderer constructor
this.pipeline = new TextRendererPipeline(this);
```

### Call flow

Public methods delegate directly to the pipeline:

```
beginRendering(int, int)           → pipeline.beginRendering(true, w, h, true)
beginRendering(int, int, boolean)  → pipeline.beginRendering(true, w, h, d)
begin3DRendering()                 → pipeline.beginRendering(false, 0, 0, false)
draw3D(...)                        → pipeline.internal_draw3D(str, x, y, z, sf)
endRendering()                     → pipeline.endRendering(true)
end3DRendering()                   → pipeline.endRendering(false)
```

### Back-reference access

The pipeline accesses enclosing state through its `renderer` reference:

| Pipeline usage | Field/method accessed |
| --- | --- |
| `renderer.inBeginEndPair` | Render cycle flag |
| `renderer.isOrthoMode` | Ortho vs 3D mode |
| `renderer.beginRenderingWidth` | Cached render width |
| `renderer.beginRenderingHeight` | Cached render height |
| `renderer.beginRenderingDepthTestDisabled` | Depth test flag |
| `renderer.haveMaxSize` | Max texture size queried flag |
| `renderer.packer` | Rectangle packer |
| `renderer.needToResetColor` | Color reset flag |
| `renderer.haveCachedColor` | Color cached flag |
| `renderer.cachedR/G/B/A` | Cached RGBA components |
| `renderer.cachedColor` | Cached Color object |
| `renderer.mipmap` | Mipmap flag |
| `renderer.mGlyphProducer` | Glyph producer |
| `renderer.mPipelinedQuadRenderer` | Quad renderer |
| `renderer.stringLocations` | String→Rect cache |
| `renderer.renderDelegate` | Render delegate |
| `renderer.font` | Font reference |
| `renderer.getBackingStore()` | Backing TextureRenderer |
| `renderer.getGraphics2D()` | Cached Graphics2D |
| `renderer.getFontRenderContext()` | Font render context |
| `renderer.getMyUseVertexArrays()` | Vertex array flag |
| `renderer.is15Available(gl)` | GL 1.5 check |
| `renderer.clearUnusedEntries()` | Cache eviction |

## Visibility changes

### Fields widened private → package-private (Phase 3)

| Field | Type | Accessed by |
| --- | --- | --- |
| `haveMaxSize` | `boolean` | `TextRendererPipeline` |
| `numRenderCycles` | `int` | `TextRendererPipeline` |
| `dbgFrame` | `Frame` | `TextRendererPipeline` |
| `debugged` | `boolean` | `TextRendererPipeline` |
| `CYCLES_PER_FLUSH` | `int` (static final) | `TextRendererPipeline` |

These add to the 22 members already widened in Phases 1–2 (14 in Phase 1,
8 in Phase 2).

### Methods remaining package-private

`flushGlyphPipeline()` remains as a thin package-private delegator on
`NonCachingTextRenderer` because `setColor(Color)`, `setColor(float,…)`, and
`flush()` — methods that stay on `NonCachingTextRenderer` — call it directly.

`draw3D_ROBUST()` remains as a thin package-private delegator because
`TextRendererGlyph.draw3D()` calls `textRenderer.draw3D_ROBUST()` as a
fallback path.

## Retained delegators

Two thin delegator methods are retained on `NonCachingTextRenderer`:

- **`flushGlyphPipeline()`** — Called by `setColor(Color)`,
  `setColor(float,…)`, and `flush()`, which remain on `NonCachingTextRenderer`.
- **`draw3D_ROBUST()`** — Called by `TextRendererGlyph.draw3D()` via
  `textRenderer.draw3D_ROBUST(...)` as a fallback when glyph texture data is
  unavailable.

```java
// Thin delegator — called by setColor and flush methods
void flushGlyphPipeline() {
    pipeline.flushGlyphPipeline();
}

// Thin delegator — called by TextRendererGlyph.draw3D()
void draw3D_ROBUST(CharSequence str, float x, float y, float z,
                   float scaleFactor) {
    pipeline.draw3D_ROBUST(str, x, y, z, scaleFactor);
}
```

## Validation commands

### Full module test suite (159 tests)

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

Expected: 159 tests run, 0 failures, 0 errors, 7 skipped.

### Focused contract tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=InnerClassExtractionContractTest \
  -Dcheckstyle.skip test
```

### Line count verification

```bash
wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java
# Expected: under 650 lines

wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererPipeline.java
# Expected: ~253 lines
```

### File existence check

```bash
test -f core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererPipeline.java \
  && echo "TextRendererPipeline.java exists" \
  || echo "MISSING"
```

## Compatibility rules

1. **Public API unchanged.** `NonCachingTextRenderer` exposes the same public
   method signatures (`beginRendering`, `endRendering`, `begin3DRendering`,
   `end3DRendering`, `draw3D`, `setColor`, etc.). No public or protected
   members are added, removed, or renamed.

2. **No new public classes.** `TextRendererPipeline` is package-private. It is
   invisible to code outside `edu.cmu.cs.dennisc.render.joglrenderer`.

3. **Behavioral equivalence.** Every extracted method body is moved verbatim
   (with `this.` references replaced by `renderer.`). No logic changes,
   reorderings, or optimizations.

4. **Existing delegator contracts preserved.** `flushGlyphPipeline()` and
   `draw3D_ROBUST()` remain callable on `NonCachingTextRenderer` via thin
   delegators. `setColor` and `flush` methods continue to call
   `flushGlyphPipeline()`, and `TextRendererGlyph.draw3D()` continues to call
   `draw3D_ROBUST()`, without changes.

5. **Contract test thresholds.** The `InnerClassExtractionContractTest`
   line-count assertion is tightened from `< 850` to `< 650` to lock in the
   pipeline extraction gains. New tests verify `TextRendererPipeline` class
   existence, visibility, and constructor signature.

## Examples

### Verifying the delegate wiring

```java
// In a test or debug context — the pipeline field is package-private
NonCachingTextRenderer renderer = ...; // assume constructed
// The pipeline is created automatically in the constructor.
// Public API usage is unchanged:
renderer.beginRendering(800, 600);
renderer.draw3D("Hello", 0, 0, 0, 1.0f);
renderer.endRendering();
```

### Tracing the delegation path

To understand the call chain for `beginRendering(800, 600)`:

1. `NonCachingTextRenderer.beginRendering(int, int)` — public entry point
2. Calls `pipeline.beginRendering(true, 800, 600, true)`
3. `TextRendererPipeline.beginRendering(...)` — pushes GL state, queries max
   texture size, resets cached color, initializes ortho backing store rendering
4. Accesses `renderer.packer`, `renderer.getBackingStore()`, etc.
   through the back-reference

### Adding a new pipeline method

If future refactoring moves another method into the pipeline:

1. Move the method body to `TextRendererPipeline.java`
2. Replace `this.` field/method references with `renderer.` references
3. If the moved method was private, widen any accessed fields to package-private
4. If other extracted classes call the method on `textRenderer`, retain a thin
   delegator on `NonCachingTextRenderer`
5. Update `InnerClassExtractionContractTest` line-count threshold
6. Run: `mvn -pl core/glrender -am -DfailIfNoTests=false -Dcheckstyle.skip test`
