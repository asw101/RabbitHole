# Tutorial: Trace the NonCachingTextRenderer Pipeline Extraction

This tutorial walks through the delegation pattern used to extract rendering
pipeline methods from `NonCachingTextRenderer` into `TextRendererPipeline`.

For the full reference, see the [NonCachingTextRenderer Pipeline Extraction
reference](../reference/noncaching-text-renderer-pipeline-extraction.md). For
a validation checklist, see the
[how-to guide](../howto/validate-noncaching-text-renderer-pipeline-extraction.md).

## Prerequisites

- Familiarity with the [inner class extraction
  reference](../reference/noncaching-text-renderer-inner-class-extraction.md)
- Repository checked out with `git submodule update --init tweedle-lang`

## 1. Understand the before state

Before Phase 3, `NonCachingTextRenderer.java` was 842 lines after inner class
extraction. The file still contained full method implementations for:

- `beginRendering(boolean, int, int, boolean)` — GL state setup (~52 lines)
- `endRendering(boolean)` — GL state teardown (~36 lines)
- `internal_draw3D(CharSequence, float, float, float, float)` — glyph loop
  (~5 lines)
- `flushGlyphPipeline()` — quad renderer flush (~4 lines)
- `draw3D_ROBUST(CharSequence, float, float, float, float)` — string
  rasterization and backing store draw (~80 lines)
- `debug(GL)` — debug frame creation (~27 lines)

These methods form the rendering pipeline — the code that bridges OpenGL
state management with glyph caching and texture rendering.

## 2. Trace the delegate class

Open `TextRendererPipeline.java`. Observe:

- **Package-private visibility** — `class TextRendererPipeline` (no `public`).
- **Constructor** — Takes a `NonCachingTextRenderer textRenderer` parameter
  and stores it as a `final` field.
- **@SuppressWarnings("CheckStyle")** — Matches the parent class convention
  to ease JOGL source comparison.
- **Method bodies** — Verbatim copies from `NonCachingTextRenderer`, with
  `this.` references replaced by `textRenderer.` references.

Example — `beginRendering` before and after:

```java
// BEFORE (in NonCachingTextRenderer)
private void beginRendering(boolean ortho, int width, int height,
                            boolean disableDepthTestForOrtho) {
    GL2 gl = GLContext.getCurrentGL().getGL2();
    if (DEBUG && !debugged) {
        debug(gl);
    }
    inBeginEndPair = true;
    // ...
}

// AFTER (in TextRendererPipeline)
void beginRendering(boolean ortho, int width, int height,
                    boolean disableDepthTestForOrtho) {
    GL2 gl = GLContext.getCurrentGL().getGL2();
    if (NonCachingTextRenderer.DEBUG && !textRenderer.debugged) {
        debug(gl);
    }
    textRenderer.inBeginEndPair = true;
    // ...
}
```

Key differences:
- Visibility changed from `private` to package-private
- `DEBUG` is qualified as `NonCachingTextRenderer.DEBUG` (static field)
- Instance fields use `textRenderer.` prefix
- `debug(gl)` calls the pipeline's own copy (also extracted)

## 3. Trace the public API delegation

Open `NonCachingTextRenderer.java`. Find the three public `beginRendering`
variants:

```java
public void beginRendering(int width, int height) {
    pipeline.beginRendering(true, width, height, true);
}

public void beginRendering(int width, int height,
                           boolean disableDepthTestForOrtho) {
    pipeline.beginRendering(true, width, height, disableDepthTestForOrtho);
}

public void begin3DRendering() {
    pipeline.beginRendering(false, 0, 0, false);
}
```

These one-liner delegations replace the previous pattern where each public
method called `private beginRendering(boolean, int, int, boolean)`. Now they
call `pipeline.beginRendering(...)` directly.

Similarly, `draw3D` delegates to `pipeline.internal_draw3D(...)`, and
`endRendering()`/`end3DRendering()` delegate to `pipeline.endRendering(...)`.

## 4. Understand the retained delegators

Two methods stay on `NonCachingTextRenderer` as thin package-private
delegators:

```java
void flushGlyphPipeline() {
    pipeline.flushGlyphPipeline();
}

void draw3D_ROBUST(CharSequence str, float x, float y, float z,
                   float scaleFactor) {
    pipeline.draw3D_ROBUST(str, x, y, z, scaleFactor);
}
```

Why are these retained?

- **`flushGlyphPipeline()`** — Called by `setColor(Color)`,
  `setColor(float,…)`, and `flush()` — methods that remain on
  `NonCachingTextRenderer` itself. Removing this delegator would require
  those methods to reference `TextRendererPipeline` directly.

- **`draw3D_ROBUST()`** — Called by `TextRendererGlyph.draw3D()` via
  `textRenderer.draw3D_ROBUST(...)` as a fallback when the glyph has no
  texture data. Removing this delegator would similarly leak the pipeline
  abstraction into the glyph class.

The delegators keep the dependency graph clean: `TextRendererGlyph` depends
only on `NonCachingTextRenderer` (not on `TextRendererPipeline`), and the
`setColor`/`flush` methods on `NonCachingTextRenderer` call a local method
rather than reaching into the pipeline delegate.

## 5. Verify the field widenings

Five fields were widened from `private` to package-private for pipeline
access:

| Field | Why the pipeline needs it |
| --- | --- |
| `haveMaxSize` | `beginRendering` queries GL max texture size once |
| `numRenderCycles` | `endRendering` increments the flush cycle counter |
| `dbgFrame` | `debug(GL)` creates and stores the debug Frame |
| `debugged` | `beginRendering` checks whether debug was already initialized |
| `CYCLES_PER_FLUSH` | `endRendering` uses the flush interval constant |

Note that `CYCLES_PER_FLUSH` is `static final` — widening it from
`private static final` to `static final` (package-private) has no runtime
cost. It simply makes the constant accessible within the package.

## 6. Run the verification

```bash
export NODE_OPTIONS=--max-old-space-size=32768

# Full suite — must show 159 tests, 0 failures
mvn -pl core/glrender -am -DfailIfNoTests=false -Dcheckstyle.skip test

# Line count check
wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java
# Expected: under 650

wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererPipeline.java
# Expected: ~253
```

## 7. Non-claims

This extraction does **not**:
- Change any rendering behavior or OpenGL call sequence
- Add, remove, or rename any public or protected API
- Affect classes outside `edu.cmu.cs.dennisc.render.joglrenderer`
- Introduce new public types visible to other packages
- Optimize, reorder, or deduplicate any rendering logic
- Extract `clearUnusedEntries()` — this method stays on
  `NonCachingTextRenderer` because `Manager` calls it directly

## Summary

| Aspect | Before | After |
| --- | --- | --- |
| `NonCachingTextRenderer.java` lines | 842 | ~626 |
| Pipeline methods in NCTR | 6 (full bodies) | 2 thin delegators |
| `TextRendererPipeline.java` | — | ~253 lines |
| Fields widened (Phase 3) | 0 | 5 |
| Total widened fields (all phases) | 22 | 27 |
| Public API surface | unchanged | unchanged |
| Test count | 159 | 159 |
