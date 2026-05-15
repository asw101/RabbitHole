# GlResourceCache — GL Resource Lifecycle Delegate

> Extracted from `RenderContext.java` (issue #655) to reduce the class from
> 603 lines to under 500 lines by moving GL resource lifecycle management
> into a focused, package-private delegate.

## Overview

`GlResourceCache` owns the four mutable collections that track OpenGL
resources for a single `RenderContext`:

| Field                       | Type                                                    | Purpose                                         |
| --------------------------- | ------------------------------------------------------- | ----------------------------------------------- |
| `displayListMap`            | `Map<GlrGeometry<?>, Integer>`                          | Maps geometry adapters → GL display-list IDs    |
| `textureBindingMap`         | `Map<GlrTexture<?>, ForgettableBinding>`                | Maps texture adapters → GL texture bindings     |
| `toBeForgottenDisplayLists` | `List<Integer>` (CopyOnWriteArrayList)                  | Deferred-deletion queue for display lists       |
| `toBeForgottenTextures`     | `List<ForgettableBinding>` (CopyOnWriteArrayList)       | Deferred-deletion queue for texture bindings    |

It also owns the **static** unused-textures listener list and the
`clearUnusedTextures` broadcast.

### Design Constraints

- **Package-private** — only `RenderContext` (same package) instantiates it.
- **`UnusedTexturesListener` stays in `RenderContext`** — the nested
  interface is public on a public class; moving it to package-private
  `GlResourceCache` would break any external references. GlResourceCache
  references `RenderContext.UnusedTexturesListener`.
- **Stateless w.r.t. GL** — never stores a `GL2` reference. Every method
  that needs GL receives the owning `RenderContext` as a parameter, and
  reads `renderContext.gl` directly.
- **Identical synchronization** — every `synchronized` block locks on the
  same map instance that the original code locked on. No lock upgrades,
  no new lock objects.

### Observation: `textureBindingMap` is never populated

The only `textureBindingMap.put()` call in the codebase is **commented
out** (original line 506). The map is always empty. All `get()` calls
return `null`, and `forgetAllTextureAdapters` iterates an empty map.
Actual texture lifecycle is managed inside `TextureBinding` (which has
its own per-RenderContext map). This is a **pre-existing condition** — the
extraction faithfully moves the dead code without attempting to fix it.

The commented-out `put()` method (original lines 504–507, using the
old `TextureAdapter` class name) moves into `GlResourceCache` as-is,
still commented out. It follows the `textureBindingMap` it references.

## API Reference

### Static Members (Listener Management)

The `UnusedTexturesListener` interface **stays in `RenderContext`** (public
nested interface on a public class). GlResourceCache owns only the static
list and the add/remove methods:

```java
// Thread-safe CopyOnWriteArrayList; safe to call from any thread.
// Uses RenderContext.UnusedTexturesListener (interface stays in RenderContext).
static void addUnusedTexturesListener(RenderContext.UnusedTexturesListener listener);
static void removeUnusedTexturesListener(RenderContext.UnusedTexturesListener listener);
```

> **Bug fix (line 92):** The original `removeUnusedTexturesListener` called
> `unusedTexturesListeners.add(listener)` instead of `.remove(listener)`.
> This is corrected in the extraction.

### Instance — Display List Methods

```java
// Look up a cached display list for a geometry adapter.
// Returns null if no display list has been generated yet.
// Caller holds: synchronized(displayListMap) internally.
Integer getDisplayListID(GlrGeometry<? extends Geometry> geometryAdapter);

// Generate a new GL display list and register it in the cache.
// Also calls geometryAdapter.addRenderContext(renderContext).
// @param renderContext  the owning RenderContext (provides gl + identity)
Integer generateDisplayListID(
    GlrGeometry<? extends Geometry> geometryAdapter,
    RenderContext renderContext);

// Schedule a geometry adapter's display list for deferred deletion.
// If removeFromMap is true, removes the adapter from the map immediately.
// Also calls geometryAdapter.removeRenderContext(renderContext).
void forgetGeometryAdapter(
    GlrGeometry<? extends Geometry> geometryAdapter,
    boolean removeFromMap,
    RenderContext renderContext);

// Convenience overload: forgetGeometryAdapter(adapter, true, renderContext)
void forgetGeometryAdapter(
    GlrGeometry<? extends Geometry> geometryAdapter,
    RenderContext renderContext);

// Process the deferred-deletion queue: glDeleteLists for each queued ID.
void actuallyForgetDisplayListsIfNecessary(RenderContext renderContext);
```

### Instance — Texture Binding Methods

```java
// Schedule a texture adapter's binding for deferred deletion.
void forgetTextureAdapter(
    GlrTexture<? extends Texture> textureAdapter,
    boolean removeFromMap,
    RenderContext renderContext);

// Convenience overload: forgetTextureAdapter(adapter, true, renderContext)
void forgetTextureAdapter(
    GlrTexture<? extends Texture> textureAdapter,
    RenderContext renderContext);

// Process the deferred-deletion queue: ForgettableBinding.forget(renderContext).
void actuallyForgetTexturesIfNecessary(RenderContext renderContext);
```

### Instance — Bulk Operations

```java
// Forget all geometry adapters and all texture adapters.
// Called during render-target teardown.
void forgetAllCachedItems(RenderContext renderContext);

// Broadcast unusedTexturesCleared to all registered listeners.
// Note: instance→static change. Original was public instance method on
// RenderContext; GlResourceCache makes it static because the listeners
// list is static and no instance state is needed. The RenderContext
// forwarding wrapper converts: clearUnusedTextures() { GlResourceCache.clearUnusedTextures(this.gl); }
static void clearUnusedTextures(GL gl);
```

### Internal Methods (also moved, no forwarding wrappers needed)

```java
// Forget all geometry adapters. Called by forgetAllCachedItems.
private void forgetAllGeometryAdapters(RenderContext renderContext);

// Forget all texture adapters. Called by forgetAllCachedItems.
private void forgetAllTextureAdapters(RenderContext renderContext);

// Forget a single texture binding. Called by forgetTextureAdapter and forgetAllTextureAdapters.
private void forgetTextureBindingID(
    GlrTexture<? extends Texture> textureAdapter,
    ForgettableBinding value,
    boolean removeFromMap,
    RenderContext renderContext);
```

## Usage

### Construction (inside RenderContext)

```java
public class RenderContext extends Context {
    // Single delegate instance, created at construction time
    private final GlResourceCache resourceCache = new GlResourceCache();
    ...
}
```

### Forwarding Pattern (inside RenderContext)

Every public method on `RenderContext` that was moved to `GlResourceCache`
becomes a one-line forwarding wrapper:

```java
// Before (original — simple delegation, no param change):
public Integer getDisplayListID(GlrGeometry<? extends Geometry> geometryAdapter) {
    synchronized (this.displayListMap) {
        return this.displayListMap.get(geometryAdapter);
    }
}

// After (forwarding wrapper):
public Integer getDisplayListID(GlrGeometry<? extends Geometry> geometryAdapter) {
    return resourceCache.getDisplayListID(geometryAdapter);
}
```

Methods that used `this` (the RenderContext) internally gain an explicit
`renderContext` parameter on `GlResourceCache`, with `this` passed from
the forwarding wrapper:

```java
// Before (original — used `this` as RenderContext identity):
public Integer generateDisplayListID(GlrGeometry<? extends Geometry> geometryAdapter) {
    Integer id = gl.glGenLists(1);
    synchronized (this.displayListMap) {
        this.displayListMap.put(geometryAdapter, id);
    }
    geometryAdapter.addRenderContext(this);  // `this` is the RenderContext
    return id;
}

// After (forwarding wrapper — passes `this` explicitly):
public Integer generateDisplayListID(GlrGeometry<? extends Geometry> geometryAdapter) {
    return resourceCache.generateDisplayListID(geometryAdapter, this);
}
```

Callers outside the package see **no API change**. The method signatures,
return types, and exception behavior are identical.

### Callers — No Changes Required

**Direct callers** (these call `RenderContext` methods that become forwarding wrappers):

| Caller class                   | Methods called                                                        | Change needed |
| ------------------------------ | --------------------------------------------------------------------- | ------------- |
| `RenderTargetImp`              | `forgetAllCachedItems`, `clearUnusedTextures`, `actuallyForget*`      | None          |
| `GlrGeometry`                  | `getDisplayListID`, `generateDisplayListID`, `forgetGeometryAdapter`  | None          |
| `GlrTexture`                   | `forgetTextureAdapter`                                                | None          |

**Indirect callers** (call through `RenderTargetImp.imp`, never touch `RenderContext` directly):

| Caller class                   | Delegates through                   | Change needed |
| ------------------------------ | ----------------------------------- | ------------- |
| `GlrRenderTarget`              | `this.imp.forgetAllCachedItems/clearUnusedTextures` | None |
| `GlrOnscreenRenderTarget`      | inherited from `GlrRenderTarget`    | None          |

All direct callers go through `RenderContext`'s public API, which is
preserved unchanged as forwarding wrappers. `RenderTargetImp.clearUnusedTextures()`
has a null guard on `renderContext.gl` before delegating — this is
unaffected by the extraction.

## Concurrency Model

The concurrency model is **identical** to the original. No lock objects
change, no ordering changes, no new locks are introduced.

| Operation                    | Lock held                          | Notes                              |
| ---------------------------- | ---------------------------------- | ---------------------------------- |
| `getDisplayListID`           | `synchronized(displayListMap)`     | Read-only lookup                   |
| `generateDisplayListID`      | `synchronized(displayListMap)`     | Lock covers only `map.put()`; `gl.glGenLists(1)` and `addRenderContext` run OUTSIDE the lock |
| `forgetGeometryAdapter`      | `synchronized(displayListMap)`     | Write + queue add + `removeRenderContext` callback (all inside lock) |
| `forgetTextureAdapter`       | `synchronized(textureBindingMap)`  | Write + queue add + `removeRenderContext` via `forgetTextureBindingID` |
| `actuallyForgetDisplayLists` | `synchronized(toBeForgottenDisplayLists)` | Drain queue, call `glDeleteLists` |
| `actuallyForgetTextures`     | `synchronized(toBeForgottenTextures)`     | Drain queue, call `forget()` |
| Listener add/remove          | None (CopyOnWriteArrayList)        | Thread-safe by construction        |

**Reentrant lock pattern:** `forgetAllGeometryAdapters` holds
`synchronized(displayListMap)` and calls `forgetGeometryAdapter`, which
also synchronizes on `displayListMap`. Java `synchronized` is reentrant, so
this is correct. The extraction must preserve this exact nesting — do not
refactor into a lock-free internal helper.

**CopyOnWriteArrayList size-check-before-lock:** The `actuallyForget*`
methods read `size()` outside the synchronized block as a fast-path guard.
This is safe because CopyOnWriteArrayList's `size()` is atomic. The
extraction preserves this pattern exactly.

## Bug Fix: `removeUnusedTexturesListener`

The original code at line 92 of `RenderContext.java` contained:

```java
public static void removeUnusedTexturesListener(UnusedTexturesListener listener) {
    unusedTexturesListeners.add(listener);  // BUG: should be .remove()
}
```

This caused listeners to never be unregistered (and to be double-registered
instead). The extraction corrects this to:

```java
static void removeUnusedTexturesListener(UnusedTexturesListener listener) {
    unusedTexturesListeners.remove(listener);
}
```

This fix is safe because no caller currently calls `removeUnusedTexturesListener`
(grep confirms zero external call sites), so there is no behavioral change
in practice. The correction prevents future misuse.

## Configuration

No configuration is required. `GlResourceCache` is an internal
implementation detail with no external knobs, properties, or feature
flags.

## File Layout After Extraction

```
core/glrender/src/main/java/edu/cmu/cs/dennisc/render/gl/imp/
├── Context.java              (unchanged — base class)
├── GlResourceCache.java      (NEW — ~141 lines, package-private)
├── RenderContext.java         (MODIFIED — ~492 lines, down from 603)
├── PickContext.java           (unchanged)
└── RenderTargetImp.java       (unchanged — calls through RenderContext)
```

## Line-Count Budget

| Component          | Lines added | Lines removed | Net    |
| ------------------ | ----------- | ------------- | ------ |
| `GlResourceCache`  | ~141        | 0             | +141   |
| `RenderContext`     | ~30 (fwd)   | ~141 (moved)  | −111   |
| **Total delta**    |             |               | +30    |
| **RenderContext**   |             |               | **~498** (target: <500 ✓) |

> Line-count is approximate. Exact count depends on blank-line cleanup
> and import consolidation. May range from ~492 to ~505; verify after
> implementation.

## Examples

### Example 1: Geometry Rendering (GlrGeometry.render)

```java
// Inside GlrGeometry — unchanged caller code:
Integer id = rc.getDisplayListID(this);
if (id == null) {
    id = rc.generateDisplayListID(this);
    // ... build display list ...
}
rc.gl.glCallList(id);
```

The call chain is now:
1. `rc.getDisplayListID(this)` → `rc.resourceCache.getDisplayListID(this)`
2. `rc.generateDisplayListID(this)` → `rc.resourceCache.generateDisplayListID(this, rc)`

### Example 2: Texture Teardown (GlrTexture.forgetIfNecessary)

```java
// Inside GlrTexture — unchanged caller code:
rc.forgetTextureAdapter(this, true);
```

The call chain is now:
1. `rc.forgetTextureAdapter(this, true)` → `rc.resourceCache.forgetTextureAdapter(this, true, rc)`

### Example 3: Frame Cleanup (RenderTargetImp.repaint)

```java
// Inside RenderTargetImp — unchanged caller code:
this.renderContext.actuallyForgetTexturesIfNecessary();
this.renderContext.actuallyForgetDisplayListsIfNecessary();
```

The call chain is now:
1. `renderContext.actuallyForgetTexturesIfNecessary()` →
   `renderContext.resourceCache.actuallyForgetTexturesIfNecessary(renderContext)`
