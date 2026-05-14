# AliceResourceUtilities Decomposition

> **Issue:** [#580](https://github.com/rysweet/RabbitHole/issues/580) — Reduce AliceResourceUtilities.java from 916 lines  
> **Module:** `core/story-api`  
> **Package:** `org.lgna.story.implementation.alice`

## Overview

`AliceResourceUtilities` was a 916-line god class responsible for four distinct
concerns: enum naming conventions, texture/filename resolution, binary model I/O
with caching, and metadata/localization. It has been decomposed into three
focused helper classes while `AliceResourceUtilities` itself remains as a
slimmed facade (~440 lines) preserving full backward compatibility.

### New Classes

| Class | Responsibility | Approximate Size |
|---|---|---|
| `ResourceEnumResolver` | Enum ↔ camelCase conversion, resource name resolution | ~225 lines |
| `ResourceTextureManager` | Filename construction, URL resolution, texture base names | ~140 lines |
| `ModelResourceLoader` | Binary encode/decode, URL caching, skeleton copy operations | ~270 lines |

### What Stays in AliceResourceUtilities

- Public constants (`MODEL_RESOURCE_EXTENSION`, `TEXTURE_RESOURCE_EXTENSION`)
- Metadata loading (`getModelResourceInfo`, `getBoundingBox`, `getPlaceOnGround`)
- Localization (`getModelClassName`, `getTags`, `getGroupTags`, `getThemeTags`, `getLocalizedTag`)
- Resource stream accessors (`getAliceResourceAsStream`, `getAliceResource`)
- General-purpose accessors (`getName`, `trimName`, `getUrl`, `getKey`)
- 36 public delegate methods forwarding to the three new classes

## Architecture

```
┌──────────────────────────────────────────────────────┐
│               AliceResourceUtilities                 │
│  (facade: metadata, localization, 36 delegates)      │
│                                                      │
│  delegates to:                                       │
│  ┌───────────────┐ ┌─────────────────┐ ┌───────────┐│
│  │ResourceEnum   │ │ResourceTexture  │ │ModelResour││
│  │Resolver       │ │Manager          │ │ceLoader   ││
│  └───────┬───────┘ └────────┬────────┘ └─────┬─────┘│
│          │                  │                 │      │
│          └───── same package, package-private ┘      │
└──────────────────────────────────────────────────────┘

External callers (JointImplementationAndVisualDataFactory, JsonModelIo,
JointedModelAliceExporter, IDE classes) continue to call
AliceResourceUtilities — no import changes required.
```

### Cross-References Between Extracted Classes

Two bidirectional call paths exist between the new classes. Both are
safe because all classes live in the same package and use only static methods
(no initialization-order hazard):

1. **ResourceTextureManager ↔ ResourceEnumResolver** —
   `createTextureBaseName` calls `ResourceEnumResolver.makeEnumName` and
   `ResourceEnumResolver.enumToCamelCase`; `findAndStoreResourceNames` calls
   `ResourceTextureManager.checkVisualAndTextureName`.

2. **ModelResourceLoader ↔ ResourceTextureManager** —
   `getVisual`/`getTexturedAppearances` resolve URLs via
   `ResourceTextureManager.getTextureURL`/`getVisualURL`.

### Back-Calls to AliceResourceUtilities

The extracted classes call back to the facade for shared utility methods that
remain in `AliceResourceUtilities`. These are all same-package static calls:

| Extracted Class | Calls Back To | Reason |
|---|---|---|
| `ResourceTextureManager` | `AliceResourceUtilities.getAliceResource()` | URL resolution for thumbnails/visuals/textures |
| `ResourceTextureManager` | `AliceResourceUtilities.getUrl()` | Compound URL builder |
| `ResourceTextureManager` | `AliceResourceUtilities.getName()` | Class name for `getThumbnailURL(Class<?>)` |
| `ResourceEnumResolver` | `AliceResourceUtilities.getName()` | Class name lookup in `getModelNameFromClassAndResource` |
| `ModelResourceLoader` | (none — resolves via ResourceTextureManager) | — |

These create a bidirectional dependency between the facade and its helpers,
which is acceptable for same-package statics but should be understood by
implementers. The private helpers `getThumbnailURLInternalFromFilename` and
`getResourceSubDirWithSeparator` move to `ResourceTextureManager` (they are
texture-path concerns). The private `getVisualResourceFileName(ModelResource)`
one-arg overload also moves to `ResourceTextureManager` as a private helper
called by `getVisualURL`.

## API Reference

### ResourceEnumResolver

Handles all conversions between Java enum naming conventions (`UPPER_SNAKE`)
and Alice camelCase resource names, plus the core resource-name resolution
algorithm that maps enum constants to visual+texture name pairs.

```java
package org.lgna.story.implementation.alice;

class ResourceEnumResolver {

  // ── Enum ↔ CamelCase conversion ──────────────────────

  /** Convert UPPER_SNAKE enum name to CamelCase. */
  static String enumToCamelCase(String enumName, boolean startWithLowerCase);

  /** Convert UPPER_SNAKE enum name to CamelCase (starts uppercase). */
  static String enumToCamelCase(String enumName);

  /** Convert camelCase name to UPPER_SNAKE. */
  static String camelCaseToEnum(String name);

  /** True if name is already in UPPER_SNAKE format. */
  static boolean isEnumName(String name);

  /** Normalize a name to UPPER_SNAKE format. */
  static String makeEnumName(String name);

  /** Convert a tag string to localization key format (spaces → underscores). */
  static String makeLocalizationKey(String key);

  /** Join a subarray of name segments into UPPER_SNAKE. */
  static String arrayToEnum(String[] nameArray, int start, int end);

  /** Get the "default" texture enum name for a resource. Always returns "DEFAULT". */
  static String getDefaultTextureEnumName(String resourceName);

  // ── Resource name resolution ─────────────────────────

  /**
   * Resolve the visual model name for a resource+enum combination.
   *
   * Uses a cached lookup. On first call for a given identifier, runs the
   * resource name resolution algorithm (findAndStoreResourceNames) to
   * determine the visual/texture split encoded in the enum constant name.
   */
  static String getModelNameFromClassAndResource(ModelResource resource, String resourceName);

  /**
   * Resolve the texture name for a resource+enum combination.
   * Returns null when resourceName is null (class-level lookup).
   */
  static String getTextureNameFromClassAndResource(ModelResource resource, String resourceName);

  /** Get the visual resource name (model name) for a ModelResource enum value. */
  static String getVisualResourceName(ModelResource resource);

  /** Get the texture resource name for a ModelResource enum value. */
  static String getTextureResourceName(ModelResource resource);
}
```

**Cache:** `resourceIdentifierToResourceNamesMap` (a `HashMap<String, ResourceNames>`)
is owned by this class. It is the same unsynchronized cache that existed in
`AliceResourceUtilities` — behavior is preserved exactly.

**Inner class:** `ResourceNames` (package-private record-style class with
`visualName` and `textureName` fields) moves here.

### ResourceTextureManager

Constructs file paths and URLs for textures, visuals, and thumbnails. Owns
the filename construction logic and URL resolution.

```java
package org.lgna.story.implementation.alice;

class ResourceTextureManager {

  // ── Filename construction ────────────────────────────

  /** Build texture base name: "{model}_{TEXTURE}" or "{model}" or "{model}_cls". */
  static String createTextureBaseName(String modelName, String textureName);

  /** Thumbnail filename: "{base}.png". */
  static String getThumbnailResourceFileName(String modelName, String textureName);

  /** Texture resource filename: "{base}.a3t". */
  static String getTextureResourceFileName(String modelName, String textureName);

  /** Visual resource filename: "{model}.{ext}". */
  static String getVisualResourceFileNameFromModelName(String modelName, String extension);

  /** Visual resource filename: "{model}.a3r". */
  static String getVisualResourceFileNameFromModelName(String modelName);

  // ── Compound filename builders (resource → filename) ─

  /** Texture resource filename for a ModelResource + resourceName. */
  static String getTextureResourceFileName(ModelResource resource, String resourceName);

  /** Texture resource filename for a ModelResource (uses resource.toString()). */
  static String getTextureResourceFileName(ModelResource resource);

  /** Visual resource filename for a ModelResource + resourceName. */
  static String getVisualResourceFileName(ModelResource resource, String resourceName);

  /** Thumbnail filename for a ModelResource + resourceName. */
  static String getThumbnailResourceFileName(ModelResource resource, String resourceName);

  // ── URL resolution ───────────────────────────────────

  /** Resolve the texture URL (handles DynamicResource URI case). */
  static URL getTextureURL(ModelResource resource);

  /** Resolve the visual URL (handles DynamicResource URI case). */
  static URL getVisualURL(ModelResource resource);

  /** Thumbnail URL for a ModelResource instance + enum name. */
  static URL getThumbnailURL(ModelResource modelResource, String instanceName);

  /** Thumbnail URL for a ModelResource class (no enum). */
  static URL getThumbnailURL(Class<?> modelResource);

  // ── Internal helpers ─────────────────────────────────

  /** Check if a thumbnail exists for the given visual+texture name pair. */
  static boolean checkVisualAndTextureName(ModelResource resource, String visualName, String textureName);
}
```

**Note on `checkVisualAndTextureName`:** This method was originally placed in
the design spec under `ResourceEnumResolver`. It was moved to
`ResourceTextureManager` because its implementation calls
`getThumbnailResourceFileName` and `getThumbnailURLInternalFromFilename` — both
texture-file concerns. `ResourceEnumResolver.findAndStoreResourceNames` calls
across to `ResourceTextureManager.checkVisualAndTextureName`, creating one
direction of the bidirectional dependency.

### ModelResourceLoader

Binary codec operations (encode/decode for `.a3r` and `.a3t` files), cached
visual/texture loading, skeleton copy, and skeleton manipulation.

```java
package org.lgna.story.implementation.alice;

class ModelResourceLoader {

  // ── Binary decode ────────────────────────────────────

  /** Decode a SkeletonVisual from a URL (.a3r binary stream). */
  static SkeletonVisual decodeVisual(URL url);

  /** Decode TexturedAppearance[] from a URL (.a3t binary stream). */
  static TexturedAppearance[] decodeTexture(URL url);

  // ── Binary encode ────────────────────────────────────

  /** Encode a SkeletonVisual to an OutputStream. */
  static void encodeVisual(SkeletonVisual toSave, OutputStream os) throws IOException;

  /** Encode a SkeletonVisual to a File (creates parent dirs). */
  static void encodeVisual(SkeletonVisual toSave, File file) throws IOException;

  /** Encode TexturedAppearance[] to an OutputStream. */
  static void encodeTexture(TexturedAppearance[] toSave, OutputStream os) throws IOException;

  /** Encode TexturedAppearance[] to a File (creates parent dirs). */
  static void encodeTexture(TexturedAppearance[] toSave, File file) throws IOException;

  // ── Cached resource loading ──────────────────────────

  /**
   * Load and cache a SkeletonVisual for a ModelResource.
   * On first load, runs QA inspection and logs any problems.
   */
  static SkeletonVisual getVisual(ModelResource resource);

  /** Get a deep copy of the cached SkeletonVisual. */
  static SkeletonVisual getVisualCopy(ModelResource resource);

  /** Load and cache TexturedAppearance[] for a ModelResource. */
  static TexturedAppearance[] getTexturedAppearances(ModelResource resource);

  // ── Skeleton operations ──────────────────────────────

  /** Deep-copy a SkeletonVisual (geometry shared, skeleton/appearance cloned). */
  static SkeletonVisual createCopy(SkeletonVisual sgOriginal);

  /** Replace the visual elements of an existing skeleton with those from a new resource. */
  static SkeletonVisual createReplaceVisualElements(SkeletonVisual sgOriginal, ModelResource resource);

  /** Get the original local transformation of a joint from the cached visual. */
  static AffineMatrix4x4 getOriginalJointTransformation(ModelResource resource, JointId jointId);

  /** Get the original local orientation of a joint as a quaternion. */
  static UnitQuaternion getOriginalJointOrientation(ModelResource resource, JointId jointId);
}
```

**Caches:** Two `HashMap` caches move here:
- `urlToVisualMap` (`HashMap<URL, SkeletonVisual>`) — loaded `.a3r` visuals
- `urlToTextureMap` (`HashMap<URL, TexturedAppearance[]>`) — loaded `.a3t` textures

Both are unsynchronized, matching the original behavior exactly.

**`correctDimensions`:** The private helper that stretches textures to
power-of-two dimensions (Mac rendering fix for Baby Penguin model) moves here
as a private method called by `decodeTexture`.

## Backward Compatibility

### Delegate Pattern

`AliceResourceUtilities` retains 36 public `static` methods that delegate to
the extracted classes. This ensures **zero changes** to any caller:

```java
// Example delegates in AliceResourceUtilities
public static String enumToCamelCase(String enumName) {
  return ResourceEnumResolver.enumToCamelCase(enumName);
}

public static SkeletonVisual decodeVisual(URL url) {
  return ModelResourceLoader.decodeVisual(url);
}

public static URL getTextureURL(ModelResource resource) {
  return ResourceTextureManager.getTextureURL(resource);
}
```

The full delegate list (grouped by target class):

#### → ResourceEnumResolver (12 delegates)
| Method | Signature |
|---|---|
| `enumToCamelCase` | `(String, boolean)` |
| `enumToCamelCase` | `(String)` |
| `camelCaseToEnum` | `(String)` |
| `isEnumName` | `(String)` |
| `makeEnumName` | `(String)` |
| `makeLocalizationKey` | `(String)` |
| `arrayToEnum` | `(String[], int, int)` |
| `getDefaultTextureEnumName` | `(String)` |
| `getModelNameFromClassAndResource` | `(ModelResource, String)` |
| `getTextureNameFromClassAndResource` | `(ModelResource, String)` |
| `getVisualResourceName` | `(ModelResource)` |
| `getTextureResourceName` | `(ModelResource)` |

#### → ResourceTextureManager (11 delegates)
| Method | Signature |
|---|---|
| `getThumbnailResourceFileName` | `(String, String)` |
| `getTextureResourceFileName` | `(String, String)` |
| `getVisualResourceFileNameFromModelName` | `(String, String)` |
| `getVisualResourceFileNameFromModelName` | `(String)` |
| `getTextureResourceFileName` | `(ModelResource, String)` |
| `getTextureResourceFileName` | `(ModelResource)` |
| `getVisualResourceFileName` | `(ModelResource, String)` |
| `getThumbnailResourceFileName` | `(ModelResource, String)` |
| `getTextureURL` | `(ModelResource)` |
| `getThumbnailURL` | `(ModelResource, String)` |
| `getThumbnailURL` | `(Class<?>)` |

#### → ModelResourceLoader (13 delegates)
| Method | Signature |
|---|---|
| `decodeVisual` | `(URL)` |
| `decodeTexture` | `(URL)` |
| `encodeVisual` | `(SkeletonVisual, OutputStream)` |
| `encodeVisual` | `(SkeletonVisual, File)` |
| `encodeTexture` | `(TexturedAppearance[], OutputStream)` |
| `encodeTexture` | `(TexturedAppearance[], File)` |
| `getVisual` | `(ModelResource)` |
| `getVisualCopy` | `(ModelResource)` |
| `getTexturedAppearances` | `(ModelResource)` |
| `createCopy` | `(SkeletonVisual)` |
| `createReplaceVisualElements` | `(SkeletonVisual, ModelResource)` |
| `getOriginalJointTransformation` | `(ModelResource, JointId)` |
| `getOriginalJointOrientation` | `(ModelResource, JointId)` |

### Visibility Changes

Only two methods change visibility:

| Method | Before | After | Reason |
|---|---|---|---|
| `checkVisualAndTextureName` | `private` | package-private | Moves to `ResourceTextureManager`; called by `ResourceEnumResolver.findAndStoreResourceNames` |
| `getVisualURL` | `private` | package-private | Moves to `ResourceTextureManager`; called by `ModelResourceLoader.getVisual` |

`createTextureBaseName` remains private — it moves to `ResourceTextureManager` where
its only callers (`getThumbnailResourceFileName(String, String)` and
`getTextureResourceFileName(String, String)`) also reside.

No new `public` API surface is created. All three extracted classes are
**package-private** (no `public` modifier on the class declaration).

### External Callers — No Changes Required

| Caller | Module | Methods Used |
|---|---|---|
| `JointImplementationAndVisualDataFactory` | story-api | `getTexturedAppearances`, `getVisualCopy`, `getOriginalJointOrientation`, `getOriginalJointTransformation` |
| `JsonModelIo` | story-api-migration | `getTextureResourceName`, `getDefaultTextureEnumName`, `getModelResourceInfo`, `getVisualResourceName`, `encodeVisual` |
| `JointedModelAliceExporter` | model-loading | `MODEL_RESOURCE_EXTENSION`, `TEXTURE_RESOURCE_EXTENSION`, `getVisualResourceFileNameFromModelName`, `encodeVisual`, `getTextureResourceFileName`, `encodeTexture` |
| `PoseUtilities` | story-api | `getVisual` |
| `ModelResource` | story-api | `getBoundingBox`, `getPlaceOnGround`, `getDefaultInitialTransform` |
| Various IDE classes | ide | `getThumbnailURL`, `getModelClassName`, `getTags`, `getGroupTags`, `getThemeTags`, `getBoundingBox` |

All of these continue calling `AliceResourceUtilities` with unchanged
signatures. The delegates forward transparently.

### Reflection Target Updates

`JsonModelIoTest.registerModelResourceMetadata` uses reflection to access
three private internals. Two of the three must be updated after extraction:

| Line | Original Target | After Extraction | Change? |
|---|---|---|---|
| 212 | `AliceResourceUtilities.class.getDeclaredField("classToInfoMap")` | stays in `AliceResourceUtilities` | No |
| 218 | `Class.forName(AliceResourceUtilities.class.getName() + "$ResourceNames")` | `ResourceEnumResolver.class.getName() + "$ResourceNames"` | **Yes** |
| 223 | `AliceResourceUtilities.class.getDeclaredField("resourceIdentifierToResourceNamesMap")` | `ResourceEnumResolver.class.getDeclaredField(...)` | **Yes** |

Both the `ResourceNames` inner class and the `resourceIdentifierToResourceNamesMap`
cache move to `ResourceEnumResolver`.

## Configuration

No configuration changes are required. No new dependencies are introduced.
The three new classes live in the same package and the same Maven module
(`core/story-api`), so no `pom.xml` changes are needed.

## Build and Test Verification

```bash
# Full compile + test for story-api and its dependencies
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip test

# Quick compile-only check
mvn -pl core/story-api -am compile
```

The verification confirms:
1. All existing tests pass without modification (except `JsonModelIoTest`
   reflection string update).
2. No new compiler warnings introduced.
3. All delegate methods maintain identical signatures and behavior.

## Design Decisions

### Why delegates instead of updating callers?

Updating all 20+ caller files across 4 Maven modules would create a much
larger diff with higher risk. The delegate approach:
- **Zero import changes** in any caller
- Fully reversible — delegates can be inlined later
- No cross-module coordination needed

### Why package-private classes?

The extracted classes are implementation details. Only `AliceResourceUtilities`
should be the public API. Package-private prevents any new coupling to the
internal structure.

### Why not interfaces?

These are stateless utility classes with static methods. An interface/strategy
abstraction would be over-engineering for what is purely a code-organization
improvement.

### Why preserve unsynchronized HashMaps?

The four `HashMap` caches (`urlToVisualMap`, `urlToTextureMap`,
`classToInfoMap`, `resourceIdentifierToResourceNamesMap`) are unsynchronized in
the original code. This refactoring preserves existing behavior exactly.
Thread-safety improvements are a separate concern (and a separate issue).

## File Inventory

After extraction:

```
core/story-api/src/main/java/org/lgna/story/implementation/alice/
├── AliceResourceClassUtilities.java   (unchanged)
├── AliceResourceUtilities.java        (916 → ~440 lines)
├── ModelResourceLoader.java           (new, ~270 lines)
├── ResourceEnumResolver.java          (new, ~225 lines)
├── ResourceTextureManager.java        (new, ~140 lines)
├── ModelResourceIoUtilities.java      (unchanged)
└── JointImplementationAndVisualDataFactory.java  (unchanged)
```

**Total lines before:** 916  
**Total lines after:** ~440 (facade) + ~225 + ~140 + ~270 = ~1075  
**AliceResourceUtilities target:** Under 500 lines ✓  
**Net new lines:** ~159 (delegates + class boilerplate + imports)  

The modest line increase is the expected cost of the delegate pattern.
The key metric — `AliceResourceUtilities` itself — drops from 916 to ~440
lines, well under the 500-line target.
