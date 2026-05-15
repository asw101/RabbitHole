# StorytellingResources Decomposition

> **Issue:** #648 — Reduce `StorytellingResources.java` from 611 lines to under 500  
> **Module:** `core/story-api`  
> **Package:** `org.lgna.story.resourceutilities`

## Overview

`StorytellingResources` was a 611-line enum singleton that handled three distinct
responsibilities: loading `ModelResource` classes from jar/directory trees, managing
JSON model manifests, and orchestrating gallery discovery with UI fallback. This
decomposition extracts two focused helper classes while keeping
`StorytellingResources` as the public-facing facade.

### Before

```
StorytellingResources.java  (611 lines, enum singleton)
├── Class discovery & loading (URLClassLoader, zip scanning)
├── Manifest parsing & caching (JSON ↔ ModelManifest)
├── Gallery path resolution & preference management
└── UI fallback (FindResourcesPanel, JOptionPane)
```

### After

```
StorytellingResources.java     (~410 lines, enum singleton — thin facade)
├── Gallery path resolution & preference management (unchanged)
├── UI fallback (FindResourcesPanel, JOptionPane — unchanged)
├── Delegates to ResourceClassLoader for class discovery
└── Delegates to ModelManifestManager for manifest operations

ResourceClassLoader.java       (~130 lines, static utility)
├── getClassNamesFromResources()   — scan dirs/zips for XML class descriptors
├── loadClassesFromResourceFiles() — URLClassLoader-based class loading
├── getAndLoadModelResourceClasses() — full pipeline: discover → load
└── Inner: LoadResult              — value class bundling classes + classloaders

ModelManifestManager.java      (~100 lines, stateful manager)
├── findAndLoadUserGalleryResources()    — lazy-load user gallery manifests
├── findAndLoadInternalResources()       — lazy-load internal model manifests
├── findNewUserGalleryResources()        — detect new additions
├── getModelManifest(name)               — look up user gallery manifest by name
├── getInternalModelManifest(name)       — look up internal manifest by name
└── getDynamicModelFiles(dirs)           — find JSON model files in directories
```

## New Classes

### ResourceClassLoader

**File:** `core/story-api/src/main/java/org/lgna/story/resourceutilities/ResourceClassLoader.java`

A static utility class (private constructor, no state). All methods are `static`.
Extracts the class-discovery and class-loading logic that was previously interleaved
with gallery management in `StorytellingResources`.

#### API

```java
package org.lgna.story.resourceutilities;

public final class ResourceClassLoader {

    /**
     * Scan resource directories and jar/zip files for XML model descriptors.
     * Returns a map from each resource file to the list of fully-qualified
     * Alice resource class names found within it.
     *
     * For directories: walks descendants looking for .xml files (excluding
     * inner-class files containing '$'), converts relative paths to class
     * names with the AliceResourceClassUtilities.RESOURCE_SUFFIX convention.
     *
     * For jar/zip files: enumerates zip entries with the same filtering.
     *
     * @param resourceFiles  directories or jar files to scan
     * @return map of resource-file → class-name list; never null
     */
    public static Map<File, List<String>> getClassNamesFromResources(
            File... resourceFiles);

    /**
     * Load ModelResource classes from the given resource files using a fresh
     * URLClassLoader. Falls back to the system classloader on failure.
     *
     * @param classNames     fully-qualified class names to load
     * @param resourceFiles  jars/directories providing the classpath
     * @return a LoadResult containing the loaded classes and the classloader
     */
    public static LoadResult loadClassesFromResourceFiles(
            List<String> classNames, File... resourceFiles);

    /**
     * Full pipeline: resolve resource paths → discover class names → load.
     * Handles the common pattern of expanding directories into their
     * contained jars and sub-directories before scanning.
     *
     * @param resourcePaths  top-level directories or jar files
     * @return a LoadResult containing all discovered ModelResource classes
     */
    public static LoadResult getAndLoadModelResourceClasses(
            List<File> resourcePaths);

    /**
     * Value class bundling a list of loaded ModelResource classes with
     * the URLClassLoader(s) that own them. The classloaders must be
     * retained for the lifetime of the loaded classes to prevent
     * garbage collection of the class definitions.
     */
    public static final class LoadResult {
        public List<Class<? extends ModelResource>> classes();
        public List<URLClassLoader> classLoaders();
        public boolean isEmpty();
    }
}
```

#### Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| Static utility, no instance state | The `resourceClassLoaders` list was only state because `StorytellingResources` needed to retain `URLClassLoader` references. `LoadResult` makes this explicit and movable. |
| `LoadResult` value class | Decouples the side-effect of creating `URLClassLoader` instances from the caller's storage decision. `StorytellingResources` accumulates them; a future test could discard them. |
| Preserves `getClassNamesFromResources` public static signature | Exact same method signature as before — existing callers (including any reflective access) continue to work. The method on `StorytellingResources` becomes a one-line delegate. |
| Instance → static transformation | `getClassNamesFromResources` was already static. `loadClassesFromResourceFiles` and `getAndLoadModelResourceClasses` were instance methods using `this.resourceClassLoaders`. The `LoadResult` return type eliminates this instance dependency, making both methods safely static. |

#### Usage

```java
// Direct use (rare — most callers go through StorytellingResources)
ResourceClassLoader.LoadResult result =
    ResourceClassLoader.getAndLoadModelResourceClasses(resourcePaths);

// StorytellingResources internal delegation
this.installedAliceClassesLoaded = result.classes();
this.resourceClassLoaders.addAll(result.classLoaders());
```

---

### ModelManifestManager

**File:** `core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelManifestManager.java`

A stateful (non-static) class that owns the lazy-loaded manifest caches. One
instance is held by `StorytellingResources.INSTANCE`. Encapsulates the
null-check / lazy-init pattern and the JSON parsing via `ManifestEncoderDecoder`.

#### API

```java
package org.lgna.story.resourceutilities;

public final class ModelManifestManager {

    /**
     * Lazily load all user gallery model manifests. Subsequent calls return
     * the cached list. Scans StoryApiDirectoryUtilities.getUserGalleryDirectory()
     * for .json model files and parses each via ManifestEncoderDecoder.
     *
     * @return unmodifiable view of user gallery manifests; never null
     */
    public List<ModelManifest> findAndLoadUserGalleryResources();

    /**
     * Lazily load all internal (non-user-visible) model manifests from
     * StoryApiDirectoryUtilities.getInternalModelsDirectory().
     *
     * @return unmodifiable view of internal manifests; never null
     */
    public List<ModelManifest> findAndLoadInternalResources();

    /**
     * Re-scan the user gallery directory and return only manifests that
     * were not present in the previously cached set (compared by name).
     * Newly found manifests are also added to the cache.
     *
     * Returns null if findAndLoadUserGalleryResources() has never been called
     * (preserves original behavior contract).
     *
     * @return list of newly discovered manifests, or null if never initialized
     */
    public List<ModelManifest> findNewUserGalleryResources();

    /**
     * Look up a user gallery manifest by model name.
     * Triggers lazy load if not yet initialized.
     *
     * @param modelName  the manifest name to search for
     * @return matching manifest, or null if not found
     */
    public ModelManifest getModelManifest(String modelName);

    /**
     * Look up an internal manifest by model name.
     * Triggers lazy load if not yet initialized.
     *
     * @param modelName  the manifest name to search for
     * @return matching manifest, or null if not found
     */
    public ModelManifest getInternalModelManifest(String modelName);

    /**
     * Find all .json model files within the given directories.
     *
     * @param directoriesToSearch  directories to scan (non-directories ignored)
     * @return list of .json files found; never null
     */
    public List<File> getDynamicModelFiles(File... directoriesToSearch);
}
```

#### State Ownership

```
ModelManifestManager
├── userGalleryModelManifests: List<ModelManifest>   (lazy, nullable → empty list)
└── internalModelManifests:    List<ModelManifest>   (lazy, nullable → empty list)
```

These fields move from `StorytellingResources` into `ModelManifestManager`. The
manager is the single owner and the single writer.

#### Usage

```java
// StorytellingResources holds a single instance
private final ModelManifestManager manifestManager = new ModelManifestManager();

// Delegation example (in StorytellingResources)
public ModelManifest getModelManifest(String modelName) {
    return manifestManager.getModelManifest(modelName);
}
```

---

## Changes to StorytellingResources

### What Stays

| Member | Why |
|--------|-----|
| `INSTANCE` singleton | Public API entry point |
| All preference/path methods (`getGalleryDirectory`, `getGalleryRootDirectory`, `findResourcePath`, `getDirsFromPref`, `makeDirectoryPreferenceString`, `setAliceResourceDirs`, `getAliceDirsFromPref`, `setGalleryResourceDirs`, `getGalleryPathFromResourcePath`, `getGalleryPathsFromResourcePath`, `getPreference`) | Used by `NebulousStorytellingResources` (package-private access) and own gallery logic |
| `findAliceResources()` | Gallery discovery with `ResourcePathManager` |
| `findAndLoadInstalledAliceResourcesIfNecessary()` | Heavy UI/prefs coupling (`FindResourcesPanel`, `JOptionPane`) — not extractable without wider refactoring |
| `getAliceResource()`, `getAliceResourceAsStream()` | Public API; uses `resourceClassLoaders` |
| `getGalleryLocationFromUser()`, `clearAliceResourceInfo()` | UI fallback flow |
| `userGalleryResourceFiles` field | Only used by `findAliceResources()` |
| `GALLERY_DIRECTORY_PREF_KEY` | Package-private constant used by `NebulousStorytellingResources` |

### What Changes In Place

These methods stay on `StorytellingResources` but require small modifications:

| Member | Change |
|--------|--------|
| `getGalleryDirectory()` | Replace `DIR_FILE_FILTER` with `File::isDirectory` method reference |
| `findAndLoadInstalledAliceResourcesIfNecessary()` | Two call sites (L506, L521) change from `this.getAndLoadModelResourceClasses(resourcePaths)` to `ResourceClassLoader.getAndLoadModelResourceClasses(resourcePaths)` with `LoadResult` unpacking (add `this.resourceClassLoaders.addAll(result.classLoaders())`) |
| Imports | Remove `FileFilter`, `ZipEntry`, `ZipFile` (no longer needed); `Field` and `Method` imports move to `ResourceClassLoader` only if `//TEST` code is kept, otherwise removed entirely |

### What Moves Out

| Member | Destination | Notes |
|--------|-------------|-------|
| `getClassNamesFromResources()` | `ResourceClassLoader` | Public static delegate stays on `StorytellingResources` |
| `getClassNamesFromResourceFiles()` | `ResourceClassLoader` (private helper) | Was public, only called internally |
| `loadClassesFromResourceFiles()` | `ResourceClassLoader` | — |
| `getAndLoadModelResourceClasses()` | `ResourceClassLoader` | — |
| `getAliceResourceClassName()` | `ResourceClassLoader` (private) | — |
| `getDynamicModelFiles()` | `ModelManifestManager` | — |
| `findAndLoadUserGalleryResourcesIfNecessary()` | `ModelManifestManager` | — |
| `findAndLoadInternalResourcesIfNecessary()` | `ModelManifestManager` | — |
| `findNewUserGalleryResources()` | `ModelManifestManager` | — |
| `manifestFor()` | `ModelManifestManager` (private) | — |
| `manifestIsNew()` | `ModelManifestManager` (private) | — |
| `getModelManifest()` | Delegates to `ModelManifestManager` | Public signature preserved |
| `getInternalModelManifest()` | Delegates to `ModelManifestManager` | Public signature preserved |
| `userGalleryModelManifests` field | `ModelManifestManager` | — |
| `internalModelManifests` field | `ModelManifestManager` | — |

### What Gets Removed

| Item | Lines | Justification |
|------|-------|---------------|
| `DIR_FILE_FILTER` anonymous class | 84–89 (6 lines) | Simplification: replaced with `File::isDirectory` method reference in `getGalleryDirectory()` (not dead — actively used, but unnecessary with method references) |
| Commented-out `getPathFromProperties` block | 113–123 (11 lines) | Dead code since original codebase. No callers, no references. |
| Commented-out `//DEBUG` static initializer block | 413–421 (9 lines) | Dead debug code. No callers, no references. |
| Redundant `//TEST` reflection lines in `loadClassesFromResourceFiles` | 353–358 (6 lines) | Unused `Field[]`/`Method[]` local variables + comment — debug leftovers |

**Total removed:** ~32 lines. Breakdown: ~20 lines dead commented-out code (L113–123,
L413–421), ~6 lines dead debug code inside `loadClassesFromResourceFiles` (L353–358,
removed during extraction to `ResourceClassLoader`), ~6 lines simplification
(`DIR_FILE_FILTER` → `File::isDirectory`).

### Delegate Pattern

Public **and package-private** methods that move to helper classes retain a one-line
delegate on `StorytellingResources` to preserve the existing API. This includes
package-private methods called by `StorytellingResourcesTreeUtils` in `core/ide`:

```java
// Public delegate (preserves public API)
public static Map<File, List<String>> getClassNamesFromResources(File... resourceFiles) {
    return ResourceClassLoader.getClassNamesFromResources(resourceFiles);
}

// Package-private delegate (preserves StorytellingResourcesTreeUtils access)
List<ModelManifest> findAndLoadUserGalleryResourcesIfNecessary() {
    return manifestManager.findAndLoadUserGalleryResources();
}

List<ModelManifest> findNewUserGalleryResources() {
    return manifestManager.findNewUserGalleryResources();
}
```

---

## Cross-Module Compatibility

### NebulousStorytellingResources (`core-nonfree/story-api-nonfree`)

A separate enum singleton in the same Java package. It calls these package-private
members of `StorytellingResources`:

- `StorytellingResources.getDirsFromPref(key, relativeDir)` — stays, unchanged
- `StorytellingResources.makeDirectoryPreferenceString(dirs)` — stays, unchanged
- `StorytellingResources.findResourcePath(relativePath)` — stays, unchanged
- `StorytellingResources.GALLERY_DIRECTORY_PREF_KEY` — stays, unchanged
- `StorytellingResources.INSTANCE.setGalleryResourceDirs(dirs)` — stays, unchanged

**No changes required to `NebulousStorytellingResources`.** All accessed members
remain on `StorytellingResources` with their original visibility.

### StorytellingResourcesTreeUtils (`core/ide`)

Another enum singleton in the same Java package (`org.lgna.story.resourceutilities`)
but in a different Maven module (`core/ide`). It calls these package-private methods
on `StorytellingResources.INSTANCE`:

- `findAndLoadInstalledAliceResourcesIfNecessary()` — stays on `StorytellingResources`, unchanged
- `findAndLoadUserGalleryResourcesIfNecessary()` — **delegate required**: stays as package-private one-line delegate forwarding to `manifestManager.findAndLoadUserGalleryResources()`
- `findNewUserGalleryResources()` — **delegate required**: stays as package-private one-line delegate forwarding to `manifestManager.findNewUserGalleryResources()`

**No changes required to `StorytellingResourcesTreeUtils`.** The original method
names and package-private visibility are preserved via delegates.

---

## Line Count Budget

| Component | Estimated Lines | Notes |
|-----------|----------------|-------|
| `ResourceClassLoader.java` | ~130 | 5 methods + `LoadResult` inner class + license header |
| `ModelManifestManager.java` | ~100 | 8 methods + 2 fields + license header |
| `StorytellingResources.java` | ~410 | Original 611 − ~195 extracted + ~20 delegates/field − ~26 dead/simplified code in place |

**Target: under 500 lines.** Budget allows ~90 lines margin. Exact count will be validated
post-implementation (verification checklist item 6).

---

## Security Notes

This decomposition makes no changes to security posture:

- **No visibility widening on existing types.** No existing package-private or private member
  on `StorytellingResources` or `NebulousStorytellingResources` becomes public.
  Methods on the new `ModelManifestManager` class are public, but that class is new
  and not part of the pre-existing API surface. The practical exposure is unchanged
  since callers already access these behaviors through `StorytellingResources`.
- **No new I/O paths.** All file system access, zip parsing, and `URLClassLoader`
  creation patterns are preserved exactly as-is.
- **Dynamic classloading preserved.** The `Class.forName()` / `URLClassLoader` pattern
  in `ResourceClassLoader` is identical to the original — no new trust boundaries.
- **Existing findings preserved.** The six known security-relevant patterns
  (dynamic classloading, zip file parsing, preference reads, file system traversal,
  `JOptionPane` from non-EDT context, `System.out.println` diagnostic output) are
  unchanged.

---

## Verification Checklist

1. **Maven compile:** `mvn compile -pl core/story-api` passes
2. **NebulousStorytellingResources unchanged:** zero-diff on
   `core-nonfree/story-api-nonfree/src/main/java/org/lgna/story/resourceutilities/NebulousStorytellingResources.java`
3. **StorytellingResourcesTreeUtils unchanged:** zero-diff on
   `core/ide/src/main/java/org/lgna/story/resourceutilities/StorytellingResourcesTreeUtils.java`
4. **Public API preserved:** all public methods on `StorytellingResources` retain
   their original signatures (delegates where implementation moved)
5. **Package-private API preserved:** `getDirsFromPref`, `makeDirectoryPreferenceString`,
   `findResourcePath`, `GALLERY_DIRECTORY_PREF_KEY` remain accessible
6. **Line count:** `wc -l StorytellingResources.java` reports under 500
7. **No new warnings:** compile with existing warning settings produces no regressions
