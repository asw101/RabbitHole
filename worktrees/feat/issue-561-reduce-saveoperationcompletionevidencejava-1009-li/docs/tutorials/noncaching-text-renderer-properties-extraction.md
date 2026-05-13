# Tutorial: Trace the NonCachingTextRenderer Properties Extraction

This tutorial walks through the delegation pattern used to extract color state,
property accessors, dispose/cleanup, and query utilities from
`NonCachingTextRenderer` into `TextRendererProperties`.

For the full reference, see the [NonCachingTextRenderer Properties Extraction
reference](../reference/noncaching-text-renderer-properties-extraction.md). For
a validation checklist, see the
[how-to guide](../howto/validate-noncaching-text-renderer-properties-extraction.md).

## Prerequisites

- Familiarity with the [pipeline extraction
  reference](../reference/noncaching-text-renderer-pipeline-extraction.md)
- Repository checked out with `git submodule update --init tweedle-lang`

## 1. Understand the before state

Before Phase 4, `NonCachingTextRenderer.java` was 619 lines after pipeline
extraction. The file still held:

- **Color state** — 7 fields for caching the current text color (`haveCachedColor`,
  `cachedR`, `cachedG`, `cachedB`, `cachedA`, `cachedColor`, `needToResetColor`)
- **Property fields** — `smoothing` (GL_LINEAR filtering) and `useVertexArrays`
- **Property accessors** — `setSmoothing`/`getSmoothing`,
  `setUseVertexArrays`/`getMyUseVertexArrays` (~12 lines)
- **Color setters** — `setColor(Color)` and `setColor(float,float,float,float)`
  (~29 lines)
- **Bounds queries** — `getBounds(String)`, `getBounds(CharSequence)`,
  `getCharWidth(char)` (~21 lines)
- **Dispose/cleanup** — `dispose()` (~12 lines)

These methods and fields form the property/state management layer — logically
distinct from the rendering pipeline (Phase 3) and the glyph/quad machinery
(Phases 1–2).

## 2. Trace the delegate class

Open `TextRendererProperties.java`. Observe:

- **Package-private visibility** — `class TextRendererProperties` (no `public`).
- **Constructor** — Takes a `NonCachingTextRenderer renderer` parameter
  and stores it as a `final` field.
- **@SuppressWarnings("CheckStyle")** — Matches the parent class convention
  to ease JOGL source comparison.
- **9 fields** — Color state, smoothing, and useVertexArrays, all
  package-private.
- **10 method bodies** — Verbatim copies from `NonCachingTextRenderer`, with
  `this.` references replaced by `renderer.` references.

Example — `setColor(Color)` before and after:

```java
// BEFORE (in NonCachingTextRenderer)
public void setColor(final Color color) throws GLException {
    final boolean noNeedForFlush = (haveCachedColor && (cachedColor != null) &&
        color.equals(cachedColor));
    if (!noNeedForFlush) {
        flushGlyphPipeline();
    }
    getBackingStore().setColor(color);
    haveCachedColor = true;
    cachedColor = color;
}

// AFTER (in TextRendererProperties)
public void setColor(final Color color) throws GLException {
    final boolean noNeedForFlush = (haveCachedColor && (cachedColor != null) &&
        color.equals(cachedColor));
    if (!noNeedForFlush) {
        renderer.flushGlyphPipeline();
    }
    renderer.getBackingStore().setColor(color);
    haveCachedColor = true;
    cachedColor = color;
}
```

Key differences:
- `flushGlyphPipeline()` is qualified as `renderer.flushGlyphPipeline()`
- `getBackingStore()` is qualified as `renderer.getBackingStore()`
- Fields (`haveCachedColor`, `cachedColor`) are local — they live on
  `TextRendererProperties` itself
- The class is package-private so `public` methods are only accessible
  within the package

## 3. Trace the public API delegation

Open `NonCachingTextRenderer.java`. Find the thin delegators:

```java
public void setColor(final Color color) throws GLException {
    properties.setColor(color);
}

public void setColor(final float r, final float g, final float b, final float a)
    throws GLException {
    properties.setColor(r, g, b, a);
}

public Rectangle2D getBounds(final String str) {
    return properties.getBounds(str);
}

public void dispose() throws GLException {
    properties.dispose();
}
```

Each one-liner delegation replaces the previous full method body. The
`throws GLException` clauses are retained for API compatibility even though
the delegate methods themselves do not declare checked exceptions.

## 4. Trace the two-level field paths

The color state fields moved from `NonCachingTextRenderer` to
`TextRendererProperties`. Two existing classes — `Manager` and
`TextRendererPipeline` — access these fields directly. Their access paths
changed from one level to two levels:

### Manager.java — `endMovement()`

```java
// BEFORE
renderer.setSmoothing(textRenderer.smoothing);

// AFTER (in allocateBackingStore)
renderer.setSmoothing(textRenderer.properties.smoothing);
```

The color state fields are accessed in `endMovement()`, not
`canCompact()` or `preExpand()`:

```java
// BEFORE (in endMovement)
if (textRenderer.haveCachedColor) {
    if (textRenderer.cachedColor == null) {
        ...setColor(textRenderer.cachedR, textRenderer.cachedG,
            textRenderer.cachedB, textRenderer.cachedA);
    } else {
        ...setColor(textRenderer.cachedColor);
    }
}

// AFTER (in endMovement)
final TextRendererProperties props = textRenderer.properties;
if (props.haveCachedColor) {
    if (props.cachedColor == null) {
        ...setColor(props.cachedR, props.cachedG, props.cachedB, props.cachedA);
    } else {
        ...setColor(props.cachedColor);
    }
}
```

### TextRendererPipeline.java — `beginRendering()`

```java
// BEFORE
if (renderer.needToResetColor && renderer.haveCachedColor) {
    if (renderer.cachedColor == null) {
        renderer.getBackingStore().setColor(renderer.cachedR, renderer.cachedG,
            renderer.cachedB, renderer.cachedA);
    } else {
        renderer.getBackingStore().setColor(renderer.cachedColor);
    }
    renderer.needToResetColor = false;
}

// AFTER
final TextRendererProperties props = renderer.properties;
if (props.needToResetColor && props.haveCachedColor) {
    if (props.cachedColor == null) {
        backingStore.setColor(props.cachedR, props.cachedG,
            props.cachedB, props.cachedA);
    } else {
        backingStore.setColor(props.cachedColor);
    }
    props.needToResetColor = false;
}
```

The local alias `props = renderer.properties` avoids repeating the
two-level path. Since `properties` is a `final` field, the JIT compiler
inlines the alias into a direct field load.

## 5. Understand the dispose delegation

The `dispose()` method is extracted because it resets fields that now live on
`TextRendererProperties`. It also accesses renderer-owned resources
(`mPipelinedQuadRenderer`, `packer`, `dbgFrame`) through the back-reference:

```java
// In TextRendererProperties
void dispose() {
    if (renderer.mPipelinedQuadRenderer != null) {
        renderer.mPipelinedQuadRenderer.dispose();
    }
    renderer.packer.dispose();
    renderer.packer = null;
    renderer.cachedBackingStore = null;
    renderer.cachedGraphics = null;
    renderer.cachedFontRenderContext = null;
    if (renderer.dbgFrame != null) {
        renderer.dbgFrame.dispose();
    }
}
```

The `dispose()` method nulls renderer fields that are no longer needed. These
fields (`cachedBackingStore`, `cachedGraphics`, `cachedFontRenderContext`) stay
on `NonCachingTextRenderer` because they are accessed by `getBackingStore()`
and `getGraphics2D()` — methods that remain on the renderer.

Note the asymmetry: `dispose()` lives on `TextRendererProperties` but nulls
fields on `NonCachingTextRenderer` via the back-reference. The fields
themselves (`packer`, `cachedBackingStore`, `cachedGraphics`,
`cachedFontRenderContext`, `dbgFrame`, `mPipelinedQuadRenderer`) are not
extracted — they stay on `NonCachingTextRenderer` because other retained
methods depend on them.

## 6. Understand the dead code removal

Phase 4 removes stale comments that were artifacts of the original JOGL fork:

- `//emzic: added boolean flag` (two instances) — Attribution comments from
  the original contributor. These are no longer useful context since the fields
  are now in a well-documented delegate class.
- Several orphaned comment blocks that described field groups that have moved
  to `TextRendererProperties`.

## 7. Run the verification

```bash
export NODE_OPTIONS=--max-old-space-size=32768

# Full suite — must pass
mvn -pl core/glrender -am -DfailIfNoTests=false -Dcheckstyle.skip test

# Line count check
wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java
# Expected: under 500

wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererProperties.java
# Expected: ~190
```

## 8. Non-claims

This extraction does **not**:
- Change any rendering behavior or OpenGL call sequence
- Add, remove, or rename any public or protected API
- Affect classes outside `edu.cmu.cs.dennisc.render.joglrenderer`
- Introduce new public types visible to other packages
- Optimize, reorder, or deduplicate any property logic
- Move `getBackingStore()`, `getGraphics2D()`, or `getFontRenderContext()` —
  these utility methods stay on `NonCachingTextRenderer` because they manage
  caching state that is not color/property related
- Move `getFont()` — this is a trivial field accessor that belongs with the
  field declaration

## Summary

| Aspect | Before (Phase 3) | After (Phase 4) |
| --- | --- | --- |
| `NonCachingTextRenderer.java` lines | 619 | <500 |
| Property methods in NCTR | 10 (full bodies) | 10 thin delegators |
| `TextRendererProperties.java` | — | ~190 lines |
| Color/property fields on NCTR | 9 | 0 (moved to properties) |
| Two-level field paths | 0 | 8 unique fields, 18 code locations (9 in Manager, 9 in Pipeline) |
| Dead comments removed | 0 | ~14 lines |
| Public API surface | unchanged | unchanged |
