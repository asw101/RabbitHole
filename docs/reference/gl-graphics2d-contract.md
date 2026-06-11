---
title: GL Graphics2D Contract
description: Supported OpenGL-backed Graphics2D contract for Alice rendering.
doc_type: reference
owner: rabbithole-maintainers
---

# GL Graphics2D contract

> **Applies to:** OpenGL-backed rendering through `edu.cmu.cs.dennisc.render.Graphics2D`

`edu.cmu.cs.dennisc.render.gl.imp.Graphics2D` is Alice's package-private OpenGL-backed implementation of the public `edu.cmu.cs.dennisc.render.Graphics2D` abstraction. It exposes the Java2D-shaped operations Alice supports in the GL renderer. It is not a full Java2D implementation.

## Supported contract

| Area | Supported behavior |
| --- | --- |
| Lifecycle | `initialize(Dimension)`, `dispose()`, `isValid()`, and `getGL()` manage a render-pass-scoped GL surface. |
| State | Color paint, background, font, stroke, rendering hints, font render context, and affine transforms are supported. `getColor()` requires the current paint to be a `Color`. |
| Primitives | Lines, rectangles, ovals, round rectangles, polylines, polygons, and filled polygons render through `GlPrimitiveShapeRenderer`. |
| Shapes | `draw(Shape)`, `fill(Shape)`, and glyph-vector outlines render through `GlTessellationRenderer`. |
| Text | String, char, and byte drawing, text bounds, and font lifecycle methods render through `GlTextRenderer`. |
| Images | `drawImage(Image, int, int, ImageObserver)` and image lifecycle methods support `BufferedImage` inputs. Image-generator lifecycle methods and painting support `Texture` image generators. |

The implementation coordinates four package-local delegates. These delegate names describe the internal module boundary; callers should depend on the supported behavior, not these package-private classes:

- `GlPrimitiveShapeRenderer`
- `GlTessellationRenderer`
- `GlTextRenderer`
- `GlImageRenderer`

## Intentionally unsupported operation groups

Unsupported Java2D operation groups intentionally throw `RuntimeException`. The stable baseline message is `not implemented` unless a method has a documented compatibility message.

| Group | Unsupported methods |
| --- | --- |
| Graphics cloning and paint modes | `create()`, `setPaintMode()`, `setXORMode(Color)` |
| Clipping and copy | `getClipBounds()`, `clipRect(...)`, `setClip(...)`, `getClip()`, `copyArea(...)`, `clip(Shape)` |
| Arcs and 3D rectangles | `drawArc(...)`, `fillArc(...)`, `draw3DRect(...)`, `fill3DRect(...)` |
| Scaled, transformed, filtered, regional, or background images | `drawImage` overloads other than `drawImage(Image, int, int, ImageObserver)`, `drawRenderedImage(...)`, `drawRenderableImage(...)` |
| Unsupported image input types | non-`BufferedImage` `Image` inputs and non-`Texture` `ImageGenerator` inputs |
| Attributed text | `drawString(AttributedCharacterIterator, int, int)`, `drawString(AttributedCharacterIterator, float, float)` |
| Device, composite, and hit testing | `hit(...)`, `getDeviceConfiguration()`, `getComposite()`, `setComposite(Composite)` |
| Non-color paint rendering | `setPaint(Paint)` when the paint is not a `Color` |

The `drawString(AttributedCharacterIterator, float, float)` compatibility message remains `todo: use drawString( String, float, float ) for now`. `getColor()` with non-`Color` paint remains `use getPaint()`.

Most unsupported failures are constructed at the unsupported `Graphics2D` call site so existing diagnostic stack traces continue to start at the method a caller invoked. The legacy image-input failures remain in `GlImageRenderer`: non-`BufferedImage` `Image` inputs throw `RuntimeException` with message `todo`, and non-`Texture` `ImageGenerator` inputs throw `RuntimeException` with message `TODO`.

## Preserved Alice 3 baseline cross-map behavior

`disposeForgottenImageGenerators()` preserves the baseline cross-map behavior:

1. It releases pixels for forgotten image generators.
2. It does not clear the forgotten image-generator map.
3. It clears the forgotten font map.

This compatibility behavior is intentional. Changing it requires separate characterization tests and an explicit behavior-change decision.
