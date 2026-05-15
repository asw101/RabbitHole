# GlResourceCache — GL Resource Lifecycle Delegate

> Extracted from `RenderContext.java` (issue #655) to reduce the class from
> 603 lines to ~492 by moving GL resource lifecycle management into a
> focused, package-private delegate.

## Design

`GlResourceCache` owns four mutable collections tracking OpenGL resources
for a single `RenderContext`:

| Field                       | Type                                    | Purpose                          |
| --------------------------- | --------------------------------------- | -------------------------------- |
| `displayListMap`            | `Map<GlrGeometry<?>, Integer>`          | Geometry → GL display-list IDs   |
| `textureBindingMap`         | `Map<GlrTexture<?>, ForgettableBinding>`| Texture → GL texture bindings    |
| `toBeForgottenDisplayLists` | `CopyOnWriteArrayList<Integer>`         | Deferred-deletion queue          |
| `toBeForgottenTextures`     | `CopyOnWriteArrayList<ForgettableBinding>` | Deferred-deletion queue       |

Plus the **static** `unusedTexturesListeners` list and `clearUnusedTextures` broadcast.

**Key constraints:**
- **Package-private** — only `RenderContext` instantiates it.
- **`UnusedTexturesListener` stays in `RenderContext`** — public nested interface; moving it would break external references.
- **Stateless w.r.t. GL** — never stores a `GL2` reference; receives the owning `RenderContext` as a parameter.
- **Identical synchronization** — same lock objects, same ordering, no new locks.

**Note:** `textureBindingMap` is never populated (the only `put()` is commented out). This is pre-existing; the extraction moves it faithfully.

## Bug Fix

The original `removeUnusedTexturesListener` called `.add(listener)` instead
of `.remove(listener)`. Corrected in extraction. Safe because no caller
currently invokes it (grep-confirmed zero call sites).

## Concurrency Model

Identical to original — no lock objects change, no ordering changes.

| Operation                    | Lock held                                     |
| ---------------------------- | --------------------------------------------- |
| `getDisplayListID`           | `synchronized(displayListMap)`                |
| `generateDisplayListID`      | `synchronized(displayListMap)` (map.put only) |
| `forgetGeometryAdapter`      | `synchronized(displayListMap)`                |
| `forgetTextureAdapter`       | `synchronized(textureBindingMap)`             |
| `actuallyForgetDisplayLists` | `synchronized(toBeForgottenDisplayLists)`     |
| `actuallyForgetTextures`     | `synchronized(toBeForgottenTextures)`         |
| Listener add/remove          | None (CopyOnWriteArrayList)                   |

**Reentrant lock:** `forgetAllGeometryAdapters` holds `displayListMap` lock
and calls `forgetGeometryAdapter` which re-acquires it. Java `synchronized`
is reentrant — do not refactor into a lock-free helper.

## File Layout

```
core/glrender/src/main/java/edu/cmu/cs/dennisc/render/gl/imp/
├── Context.java              (unchanged)
├── GlResourceCache.java      (NEW — 196 lines, package-private)
├── RenderContext.java         (MODIFIED — 492 lines, down from 603)
├── PickContext.java           (unchanged)
└── RenderTargetImp.java       (unchanged)
```

Callers (`GlrGeometry`, `GlrTexture`, `RenderTargetImp`, `GlrRenderTarget`)
use `RenderContext`'s unchanged public API — forwarding wrappers are invisible.
