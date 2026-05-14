# COLLADA Exporter Decomposition

> **Module:** `core/model-loading`
> **Package:** `org.lgna.story.resourceutilities`
> **Issue:** #576 — Reduce JointedModelColladaExporter.java (1181 → ≤500 lines)

## Overview

The COLLADA exporter converts Alice 3 `SkeletonVisual` scene-graph models into
COLLADA 1.4.1 (`.dae`) XML files. The original `JointedModelColladaExporter`
handled XML document assembly, skeleton traversal, mesh/geometry construction,
skin weighting, material wiring, and texture I/O — all in a single 1181-line
class.

The decomposition extracts three focused helpers while preserving the public
`JointedModelExporter` contract and the `protected createCollada()` extension
point.

## Architecture

```
┌──────────────────────────────────────────┐
│       JointedModelColladaExporter        │  426 lines
│  (coordinator, public API, texture I/O)  │
│                                          │
│  implements JointedModelExporter         │
│  protected createCollada()               │
├──────────────┬───────────┬───────────────┤
│              │           │               │
│   delegates  │ delegates │   delegates   │
│              ▼           ▼               │
│  ┌───────────────┐ ┌──────────────────┐  │
│  │ ColladaParser  │ │ColladaJoint-     │  │
│  │               │ │  Extractor       │  │
│  │ XML doc       │ │                  │  │
│  │ assembly,     │ │ skeleton tree,   │  │
│  │ texture/      │ │ joint matrices   │  │
│  │ material/     │ │                  │  │
│  │ effect libs   │ └──────────────────┘  │
│  └───────────────┘                       │
│              │                           │
│              ▼                           │
│  ┌──────────────────────────────────┐    │
│  │      ColladaMeshProcessor        │    │
│  │                                  │    │
│  │ geometry, skins, controllers,    │    │
│  │ visual-scene nodes               │    │
│  └──────────────────────────────────┘    │
└──────────────────────────────────────────┘
```

## Class Reference

### JointedModelColladaExporter

**Role:** Thin coordinator. Owns construction state (`meshNameMap`,
`materialNameMap`, `textureAppearanceMap`), implements `JointedModelExporter`,
and handles texture file I/O and JAXB serialization. Uses a static
`JAXBContext` to avoid expensive per-export context creation.

| Visibility | Member | Purpose |
|---|---|---|
| `public` | `JointedModelColladaExporter(SkeletonVisual, ModelVariant, String, String, Map)` | Full constructor |
| `public` | `JointedModelColladaExporter(SkeletonVisual, ModelVariant, String)` | Convenience constructor (empty resource path, no renamed joints) |
| `public` | `writeCollada(OutputStream)` | JAXB-marshals COLLADA document to stream via cached `JAXB_CONTEXT` |
| `public` | `getTextureFileNames()` | Lists PNG texture file names |
| `public` | `createTextureIdToImageMap()` | Maps texture IDs → unique image names |
| `public` | `createImageResourceForTexture(Integer)` | Creates `ImageResource` for a texture ID |
| `public` | `getTextureIdForName(String)` | Reverse-lookup: image filename → texture ID |
| `public` | `createImageDataSources()` | Wraps textures as `DataSource` list |
| `public` | `saveTexturesToDirectory(File)` | Writes texture PNGs to disk (try-with-resources) |
| `protected` | `createCollada()` | Assembles full COLLADA document; delegates to parser, joint extractor, mesh processor |
| `@Override` | `createStructureDataSource()` | `JointedModelExporter` — wraps `writeCollada` as `DataSource` |
| `@Override` | `getStructureFileName(DataSource)` | `JointedModelExporter` — relative `.dae` path |
| `@Override` | `getStructureExtension()` | `JointedModelExporter` — returns `"png"` |
| `@Override` | `addImageDataSources(List, ModelManifest, Map)` | `JointedModelExporter` — appends texture data sources and manifest refs |

**Naming helpers** (private, called by delegates via method references):

| Method | Returns |
|---|---|
| `getUserJointIdentifier(String)` | Joint name after rename-map lookup (public, delegates to `ColladaJointExtractor`) |
| `getImageNameForIndex(Integer)` | `"material_N_diffuseMap"` |
| `getExternallyUniqueImageNameForID(Integer)` | `fullResourceName + "_" + imageName` (without extension) |
| `getImageFileNameForIndex(Integer)` | `getExternallyUniqueImageNameForID(index) + ".png"` |
| `getImageIDForIndex(Integer)` | Image name + `"-image"` |
| `getMaterialIDForIndex(Integer)` | `"material_N_shader"` |
| `getEffectIDForIndex(Integer)` | `"material_N_fx"` |
| `getFullResourceName()` | Model variant texture set or model name |

**Private helpers:**
`initializeMeshNameMap`, `initializeMaterialNameMap`, `addMeshToNameMap`,
`createFlippedImage`, `writeTexture`, `getColladaFileName`,
`getTextureAppearance` (O(1) HashMap lookup), `addTextureIds`,
`createAndAddMeshComponents`.

**Local testing code** (private static, retained for developer convenience):
`saveColladaToDirectory` (try-with-resources), `exportAliceModelToDir`,
`loadAliceModel`, `exportAliceModelResourceToDir`.

### ColladaParser

**Role:** Assembles the COLLADA XML document structure — `Asset` metadata,
`LibraryImages`, `LibraryMaterials`, `LibraryEffects`.

**Visibility:** Package-private (no `public` keyword on class).

| Method | Purpose |
|---|---|
| `ColladaParser(ObjectFactory)` | Constructor; receives shared JAXB factory |
| `createAsset()` | Builds `Asset` with current timestamp, meter units, Y-up axis |
| `createAndAddTextureComponents(COLLADA, SkeletonVisual, Map, Function×6)` | Populates `library_images`, `library_materials`, `library_effects` using naming callbacks |
| `createEffect(TexturedAppearance, Map)` | Builds Lambert effect with diffuse texture or color, optional transparency |

### ColladaJointExtractor

**Role:** Walks the `Joint` hierarchy of a `SkeletonVisual` and produces
COLLADA `Node` elements with type=JOINT, including local transformation
matrices flipped from Alice coordinate space to COLLADA space.

**Visibility:** Package-private.

| Method | Purpose |
|---|---|
| `ColladaJointExtractor(ObjectFactory, Map<String, String>)` | Constructor; receives factory and joint rename map |
| `createSkeletonNodes(SkeletonVisual)` | Entry point: returns root `Node` for the skeleton, or `null` if no skeleton |
| `createNodeForJoint(Joint)` | Recursive: builds `Node` with SID, matrix, and child joints |
| `getUserJointIdentifier(String)` | Applies rename map to joint identifier |

**Coordinate-space behavior:** When `FLIP_COORDINATE_SPACE` is true (default),
all joint matrices are flipped via `ColladaTransformUtilities.createFlippedRowMajorTransform`.

### ColladaMeshProcessor

**Role:** Converts Alice `Mesh` and `WeightedMesh` geometry into COLLADA
`Geometry`, `Controller`, and visual-scene `Node` elements. Uses lambda-based
`Consumer<List<Double>>` initializers (replacing the former `ListInitializer`
class hierarchy) for converting Java NIO buffers to COLLADA float arrays.

**Visibility:** Package-private.

| Method | Purpose |
|---|---|
| `ColladaMeshProcessor(ObjectFactory, Map<Geometry, String>, Map<Integer, String>)` | Constructor; receives factory, mesh name map, material name map |
| `addGeometriesForMesh(List<Geometry>, Mesh)` | Adds one `Geometry` per referenced texture ID |
| `addControllersForMesh(List<Controller>, WeightedMesh, Function)` | Builds `Controller` with `Skin` for each texture ID |
| `addVisualSceneNodesForMesh(List<Node>, Mesh)` | Adds `instance_geometry` nodes to visual scene |
| `addVisualSceneNodesForWeightedMesh(List<Node>, WeightedMesh)` | Adds `instance_controller` nodes to visual scene |

**Performance:** Uses a static `IDENTITY_BIND_SHAPE_MATRIX` (avoids
per-skin `AffineMatrix4x4.IDENTITY` allocation) and caches per-vertex
`BigInteger` values in the triangle loop (3× fewer allocations per triangle).

**Inner types:**

| Type | Purpose |
|---|---|
| `ColladaSkinWeights` | Pairs `jointIndices` and `weightIndices` per vertex |

## Existing Related Classes

These classes predate this decomposition and remain unchanged:

| Class | Purpose |
|---|---|
| `ColladaTransformUtilities` | Static helpers to flip coordinates between Alice ↔ COLLADA spaces |
| `ModelExportDataSources` | Factory for `DataSource` wrappers around stream-writing lambdas |
| `JointedModelExporter` (interface) | Public contract for all model exporters (COLLADA, glTF, Alice) |
| `JointedModelGltfExporter` | Sibling exporter targeting glTF format |
| `JointedModelAliceExporter` | Sibling exporter targeting native Alice format |

## Coordinate Space

Alice and COLLADA/Maya use mirrored coordinate systems:

| Axis | Alice | COLLADA (Maya) |
|---|---|---|
| Forward | −Z | +Z |
| Right | +X | −X |
| Up | +Y | +Y |

The `FLIP_COORDINATE_SPACE` constant (default `true`) controls whether
joint matrices and vertex positions/normals are flipped during export.
The flip is performed by `ColladaTransformUtilities.createFlippedRowMajorTransform`
for 4×4 matrices and by negating X/Z in stride-3 vertex data.

## Constants

| Constant | Location | Value | Purpose |
|---|---|---|---|
| `COLLADA_EXTENSION` | `JointedModelColladaExporter` | `"dae"` | Output file extension |
| `IMAGE_EXTENSION` | `JointedModelColladaExporter` | `"png"` | Texture image format |
| `JAXB_CONTEXT` | `JointedModelColladaExporter` | (static) | Cached JAXBContext — avoids expensive per-export creation |
| `FLIP_COORDINATE_SPACE` | `ColladaTransformUtilities` | `true` | Enable Alice→COLLADA coordinate flip |
| `IDENTITY_BIND_SHAPE_MATRIX` | `ColladaMeshProcessor` | (static) | Cached identity bind-shape matrix — avoids per-skin allocation |

## Line Counts

| File | Lines | Role |
|---|---|---|
| `JointedModelColladaExporter.java` | 426 | Coordinator + public API |
| `ColladaMeshProcessor.java` | 430 | Geometry, skins, controllers, visual-scene nodes |
| `ColladaParser.java` | 174 | XML document assembly + texture/material/effect builders |
| `ColladaJointExtractor.java` | 67 | Skeleton tree traversal |
| `ColladaTransformUtilities.java` | 56 | Shared coordinate-flip utilities + `FLIP_COORDINATE_SPACE` constant |
| **Total** | **1153** | Slightly less than original 1181 |

The exporter itself drops from 1181 to 426 lines — well under the 500-line target.
