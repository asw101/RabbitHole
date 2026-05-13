# NonCachingTextRenderer Properties Extraction

This reference documents the extraction of color state, property accessors,
dispose/cleanup, and query utilities from `NonCachingTextRenderer` into a new
package-private `TextRendererProperties` delegate class (issue #543). This is
Phase 4 of the `NonCachingTextRenderer` reduction, following inner class
extraction (Phases 1–2, issues #514/#524) and pipeline extraction (Phase 3,
issue #537).

## Contents

- [Motivation](#motivation)
- [Extracted fields](#extracted-fields)
- [Extracted methods](#extracted-methods)
- [Dead code removed](#dead-code-removed)
- [File inventory](#file-inventory)
- [Delegate pattern](#delegate-pattern)
- [Caller path updates](#caller-path-updates)
- [Visibility changes](#visibility-changes)
- [Retained delegators](#retained-delegators)
- [Validation commands](#validation-commands)
- [Compatibility rules](#compatibility-rules)
- [Examples](#examples)

## Motivation

After pipeline extraction (Phase 3), `NonCachingTextRenderer.java` was 619
lines — just above the 500-line target. The file still held color caching
state (7 fields), smoothing/vertex-array property fields, and 10+ methods
for property access, bounds computation, dispose/cleanup, and character width
queries.

Phase 4 extracts these fields and methods into `TextRendererProperties`, a
package-private delegate class. This brings `NonCachingTextRenderer` under
500 lines, containing only the public API surface, thin delegators, field
declarations for rendering state, and constructor logic.

## Extracted fields

| Field | Type | Default | Purpose |
| --- | --- | --- | --- |
| `haveCachedColor` | `boolean` | `false` | Whether a color has been set |
| `cachedR` | `float` | `0.0f` | Cached red component |
| `cachedG` | `float` | `0.0f` | Cached green component |
| `cachedB` | `float` | `0.0f` | Cached blue component |
| `cachedA` | `float` | `0.0f` | Cached alpha component |
| `cachedColor` | `Color` | `null` | Cached Color object (mutually exclusive with RGBA floats) |
| `needToResetColor` | `boolean` | `false` | Flag to re-apply color after backing store change |
| `smoothing` | `boolean` | `true` | Whether GL_LINEAR filtering is enabled |
| `useVertexArrays` | `boolean` | `true` | Whether vertex arrays are used for rendering |

**Total extracted:** 9 fields.

## Extracted methods

| Method | Signature | Lines | Purpose |
| --- | --- | --- | --- |
| `setColor` (Color) | `(Color color)` | ~12 | Sets text color from a Color object, flushing the glyph pipeline if the color changed. |
| `setColor` (RGBA) | `(float r, float g, float b, float a)` | ~17 | Sets text color from RGBA components, flushing the glyph pipeline if the color changed. |
| `setSmoothing` | `(boolean smoothing)` | ~3 | Sets GL_LINEAR filtering on the backing store. |
| `getSmoothing` | `()` → `boolean` | ~3 | Returns whether smoothing is enabled. |
| `setUseVertexArrays` | `(boolean useVertexArrays)` | ~3 | Sets whether vertex arrays are used for rendering. |
| `getMyUseVertexArrays` | `()` → `boolean` | ~3 | Returns the vertex array usage flag. |
| `getBounds` (String) | `(String str)` → `Rectangle2D` | ~3 | Thin overload delegating to `getBounds(CharSequence)`. |
| `getBounds` (CharSequence) | `(CharSequence str)` → `Rectangle2D` | ~15 | Returns the bounding rectangle for a character sequence, using the string location cache or computing via the render delegate. |
| `getCharWidth` | `(char inChar)` → `float` | ~3 | Returns the pixel width of a character via the glyph producer. |
| `dispose` | `()` | ~12 | Disposes the quad renderer, rectangle packer, and debug frame; nulls cached references. |

**Total extracted:** ~74 lines of method bodies (10 methods).

## Dead code removed

| Item | Lines removed | Reason |
| --- | --- | --- |
| `//emzic: added boolean flag` comment (line 109) | 1 | Stale attribution comment from original JOGL fork. |
| `//emzic: added boolean flag` comment (line 112) | 1 | Stale attribution comment. |
| `// For resetting the color after disposal...` comment block (lines 92) | 1 | Orphaned comment — fields moved to `TextRendererProperties`. |
| `// For debugging only` comment (line 101) | 1 | Orphaned comment. |
| `// Debugging purposes only` comment (line 104) | 1 | Orphaned comment. |
| `// Whether GL_LINEAR filtering is enabled...` comment (line 116) | 1 | Orphaned comment — field moved. |
| Blank lines between removed field groups | ~8 | Cleanup after field extraction. |

**Total dead code removed:** ~14 lines.

## File inventory

| File | Role | Approx lines |
| --- | --- | --- |
| `NonCachingTextRenderer.java` | Public API surface, thin delegators, rendering state fields, constructor. | <500 |
| `TextRendererProperties.java` | Package-private delegate. Owns color state, property accessors, bounds, dispose. | ~190 |
| `TextRendererPipeline.java` | Package-private delegate. Owns 6 rendering pipeline methods (Phase 3). | ~253 |
| `Manager.java` | Extracted `Manager` (Phase 2). Updated field paths for color state. | ~205 |
| `TextRendererGlyph.java` | Extracted `Glyph` (Phase 1). | ~200 |
| `TextRendererGlyphProducer.java` | Extracted `GlyphProducer` (Phase 1). | ~170 |
| `TextRendererQuadRenderer.java` | Extracted `Pipelined_QuadRenderer` (Phase 1). | ~165 |
| `CharSequenceIterator.java` | Extracted `CharSequenceIterator` (Phase 2). | ~90 |
| `TextData.java` | Extracted `TextData` (Phase 2). | ~70 |
| `DefaultRenderDelegate.java` | Extracted `DefaultRenderDelegate` (Phase 2). | ~40 |
| `CharacterCache.java` | Extracted `CharacterCache` (Phase 2). | ~25 |
| `DebugListener.java` | Extracted `DebugListener` (Phase 2). | ~60 |

All source files reside in
`core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/`.

## Delegate pattern

`TextRendererProperties` takes a `NonCachingTextRenderer` reference in its
constructor. All back-references to the enclosing renderer's state go through
this reference — following the same pattern established by `TextRendererPipeline`
in Phase 3.

```java
// TextRendererProperties constructor
TextRendererProperties(NonCachingTextRenderer renderer) {
    this.renderer = renderer;
}
```

`NonCachingTextRenderer` creates the properties delegate in its constructor:

```java
// In NonCachingTextRenderer constructor
this.properties = new TextRendererProperties(this);
```

### Call flow — public API delegation

Public methods on `NonCachingTextRenderer` delegate to the properties object:

```
setColor(Color)            → properties.setColor(color)
setColor(float,float,…)    → properties.setColor(r, g, b, a)
setSmoothing(boolean)      → properties.setSmoothing(smoothing)
getSmoothing()             → properties.getSmoothing()
setUseVertexArrays(bool)   → properties.setUseVertexArrays(useVertexArrays)
getMyUseVertexArrays()     → properties.getMyUseVertexArrays()
getBounds(String)          → properties.getBounds(str)
getBounds(CharSequence)    → properties.getBounds(str)
getCharWidth(char)         → properties.getCharWidth(inChar)
dispose()                  → properties.dispose()
```

### Back-reference access

The properties delegate accesses enclosing state through its `renderer`
reference:

| Properties usage | Field/method accessed |
| --- | --- |
| `renderer.flushGlyphPipeline()` | Flush glyph pipeline before color change |
| `renderer.getBackingStore()` | Apply color/smoothing to backing TextureRenderer |
| `renderer.stringLocations` | Look up cached string bounds |
| `renderer.renderDelegate` | Compute bounds via render delegate |
| `renderer.font` | Font for bounds computation |
| `renderer.getFontRenderContext()` | Font render context for bounds |
| `renderer.normalize(rect)` | Normalize bounds rectangle |
| `renderer.mGlyphProducer` | Get glyph pixel width |
| `renderer.mPipelinedQuadRenderer` | Dispose quad renderer |
| `renderer.packer` | Dispose rectangle packer |
| `renderer.dbgFrame` | Dispose debug frame |

## Caller path updates

Two existing extracted classes access the color state and smoothing fields.
Their field access paths are updated from direct field access to
two-level access through the `properties` delegate:

### Manager.java (8 unique fields, 9 code locations)

In `endMovement()`, the code uses a local alias `props = textRenderer.properties`
to avoid repeating the two-level path. In `allocateBackingStore()`, the direct
path is used once.

| Before | After | Method |
| --- | --- | --- |
| `textRenderer.smoothing` | `textRenderer.properties.smoothing` | `allocateBackingStore()` |
| `textRenderer.haveCachedColor` | `props.haveCachedColor` | `endMovement()` |
| `textRenderer.cachedColor` | `props.cachedColor` | `endMovement()` (×2) |
| `textRenderer.cachedR` | `props.cachedR` | `endMovement()` |
| `textRenderer.cachedG` | `props.cachedG` | `endMovement()` |
| `textRenderer.cachedB` | `props.cachedB` | `endMovement()` |
| `textRenderer.cachedA` | `props.cachedA` | `endMovement()` |
| `textRenderer.needToResetColor` | `props.needToResetColor` | `endMovement()` |

### TextRendererPipeline.java (7 unique fields, 9 code locations)

In `beginRendering()`, the code uses a local alias `props = renderer.properties`
to avoid repeating the two-level path.

| Before | After | Method |
| --- | --- | --- |
| `renderer.needToResetColor` | `props.needToResetColor` | `beginRendering()` (×2: read + write) |
| `renderer.haveCachedColor` | `props.haveCachedColor` | `beginRendering()` |
| `renderer.cachedColor` | `props.cachedColor` | `beginRendering()` (×2) |
| `renderer.cachedR` | `props.cachedR` | `beginRendering()` |
| `renderer.cachedG` | `props.cachedG` | `beginRendering()` |
| `renderer.cachedB` | `props.cachedB` | `beginRendering()` |
| `renderer.cachedA` | `props.cachedA` | `beginRendering()` |

## Visibility changes

### New field on NonCachingTextRenderer

| Field | Type | Visibility | Accessed by |
| --- | --- | --- | --- |
| `properties` | `TextRendererProperties` | package-private, final | `Manager`, `TextRendererPipeline`, `InnerClassExtractionContractTest` |

### Fields widened private → package-private on NonCachingTextRenderer

No additional fields are widened in Phase 4. The design specification
mentions "widen 3 private fields," but upon source analysis all fields
accessed by `TextRendererProperties` via the back-reference are already
package-private from prior phases. The `properties` field is declared
package-private from creation. Fields that were previously
package-private for `Manager` and `TextRendererPipeline` access
(`haveCachedColor`, `cachedR`, etc.) are removed from `NonCachingTextRenderer`
and recreated on `TextRendererProperties` with package-private visibility.

Note: The one truly private field being extracted (`useVertexArrays`)
moves entirely to `TextRendererProperties` — it is not widened on
`NonCachingTextRenderer`.

### Fields on TextRendererProperties

All 9 extracted fields are package-private (not private) to allow direct
access from `Manager` and `TextRendererPipeline` through the two-level path
`textRenderer.properties.fieldName`.

## Retained delegators

All 10 extracted methods have thin one-line delegators on
`NonCachingTextRenderer`. These preserve the existing public API surface:

```java
public void setColor(Color color) {
    properties.setColor(color);
}

public void setColor(float r, float g, float b, float a) {
    properties.setColor(r, g, b, a);
}

public Rectangle2D getBounds(String str) {
    return properties.getBounds(str);
}

public Rectangle2D getBounds(CharSequence str) {
    return properties.getBounds(str);
}

public float getCharWidth(char inChar) {
    return properties.getCharWidth(inChar);
}

public void setSmoothing(boolean smoothing) {
    properties.setSmoothing(smoothing);
}

public boolean getSmoothing() {
    return properties.getSmoothing();
}

public void setUseVertexArrays(boolean useVertexArrays) {
    properties.setUseVertexArrays(useVertexArrays);
}

public final boolean getMyUseVertexArrays() {
    return properties.getMyUseVertexArrays();
}

public void dispose() {
    properties.dispose();
}
```

## Validation commands

### Full module test suite

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

Expected: all tests pass, 0 failures, 0 errors.

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
# Expected: under 500 lines

wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererProperties.java
# Expected: ~190 lines
```

### File existence check

```bash
test -f core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererProperties.java \
  && echo "TextRendererProperties.java exists" \
  || echo "MISSING"
```

## Compatibility rules

1. **Public API unchanged.** `NonCachingTextRenderer` exposes the same public
   method signatures (`setColor`, `getSmoothing`, `setSmoothing`, `getBounds`,
   `getCharWidth`, `getMyUseVertexArrays`, `setUseVertexArrays`, `dispose`,
   etc.). No public or protected members are added, removed, or renamed.

2. **No new public classes.** `TextRendererProperties` is package-private.
   It is invisible to code outside `edu.cmu.cs.dennisc.render.joglrenderer`.

3. **Behavioral equivalence.** Every extracted method body is moved verbatim
   (with `this.` references replaced by `renderer.` references). No logic
   changes, reorderings, or optimizations.

4. **Two-level field paths.** `Manager` and `TextRendererPipeline` access
   color state through a local alias (`props = textRenderer.properties`)
   and then `props.fieldName`, or directly as
   `textRenderer.properties.fieldName` for single accesses. This is a
   compile-time path change only — the JIT inlines these single-hop
   field accesses.

5. **Contract test threshold tightened.** The `InnerClassExtractionContractTest`
   line-count assertion in `nonCachingTextRenderer_lineCount_under500()` is
   tightened from `lineCount < 650` to `lineCount < 500` to lock in the
   properties extraction gains. (Note: the test method is already named
   `under500` but currently asserts `< 650`.) New tests verify
   `TextRendererProperties` class existence, visibility, constructor
   signature, and field ownership.

## Examples

### Verifying the delegate wiring

```java
// The properties field is package-private
NonCachingTextRenderer renderer = ...; // assume constructed
// The properties delegate is created automatically in the constructor.
// Public API usage is unchanged:
renderer.setColor(Color.RED);
renderer.beginRendering(800, 600);
renderer.draw3D("Hello", 0, 0, 0, 1.0f);
renderer.endRendering();
renderer.dispose();
```

### Tracing the color delegation path

To understand the call chain for `setColor(Color.RED)`:

1. `NonCachingTextRenderer.setColor(Color)` — public entry point
2. Calls `properties.setColor(Color.RED)`
3. `TextRendererProperties.setColor(Color)` — checks if color changed
4. If changed, calls `renderer.flushGlyphPipeline()` (back-reference)
5. Calls `renderer.getBackingStore().setColor(color)` (back-reference)
6. Updates `haveCachedColor`, `cachedColor` fields

### Tracing the Manager color path

When `Manager.endMovement()` restores colors after backing store compaction:

1. `final TextRendererProperties props = textRenderer.properties` — local alias
2. `props.haveCachedColor` — checks if color was cached
3. `props.cachedColor` — checks Color vs RGBA
4. `props.cachedR/G/B/A` — RGBA components

### Adding a new property to TextRendererProperties

If future refactoring moves another property field:

1. Move the field from `NonCachingTextRenderer` to `TextRendererProperties`
2. Move associated getter/setter method bodies to `TextRendererProperties`
3. Replace `this.` field/method references with `renderer.` references
4. Leave thin delegators on `NonCachingTextRenderer` for API compatibility
5. Update all callers: `textRenderer.field` → `textRenderer.properties.field`
6. Update `InnerClassExtractionContractTest` with new contract tests
7. Run: `mvn -pl core/glrender -am -DfailIfNoTests=false -Dcheckstyle.skip test`
