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
│       JointedModelColladaExporter        │  ~350 lines
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
│  │ visual-scene nodes, ListInit     │    │
│  └──────────────────────────────────┘    │
└──────────────────────────────────────────┘
```

## Class Reference

### JointedModelColladaExporter

**Role:** Thin coordinator. Owns construction state (`meshNameMap`,
`materialNameMap`), implements `JointedModelExporter`, and handles texture
file I/O and JAXB serialization.

| Visibility | Member | Purpose |
|---|---|---|
| `public` | `JointedModelColladaExporter(SkeletonVisual, ModelVariant, String, String, Map)` | Full constructor |
| `public` | `JointedModelColladaExporter(SkeletonVisual, ModelVariant, String)` | Convenience constructor (empty resource path, no renamed joints) |
| `public` | `writeCollada(OutputStream)` | JAXB-marshals COLLADA document to stream |
| `public` | `getTextureFileNames()` | Lists PNG texture file names |
| `public` | `createTextureIdToImageMap()` | Maps texture IDs → unique image names |
| `public` | `createImageResourceForTexture(Integer)` | Creates `ImageResource` for a texture ID |
| `public` | `getTextureIdForName(String)` | Reverse-lookup: image filename → texture ID |
| `public` | `createImageDataSources()` | Wraps textures as `DataSource` list |
| `public` | `saveTexturesToDirectory(File)` | Writes texture PNGs to disk |
| `protected` | `createCollada()` | Assembles full COLLADA document; delegates to parser, joint extractor, mesh processor |
| `@Override` | `createStructureDataSource()` | `JointedModelExporter` — wraps `writeCollada` as `DataSource` |
| `@Override` | `getStructureFileName(DataSource)` | `JointedModelExporter` — relative `.dae` path |
| `@Override` | `getStructureExtension()` | `JointedModelExporter` — returns `"png"` |
| `@Override` | `addImageDataSources(List, ModelManifest, Map)` | `JointedModelExporter` — appends texture data sources and manifest refs |

**Naming helpers** (widened from private to package-private for delegate access,
except `getUserJointIdentifier` which is already `public`):

| Method | Returns |
|---|---|
| `getUserJointIdentifier(String)` | Joint name after rename-map lookup (public) |
| `getMeshIdForMeshName(String)` | `meshName + "-id"` |
| `getMeshTextureId(Mesh, Integer)` | Mesh name, suffixed with texture ID when multi-textured |
| `getImageNameForIndex(Integer)` | `"material_N_diffuseMap"` |
| `getExternallyUniqueImageNameForID(Integer)` | `fullResourceName + "_" + imageName` (without extension) |
| `getImageFileNameForIndex(Integer)` | `getExternallyUniqueImageNameForID(index) + ".png"` |
| `getImageIDForIndex(Integer)` | Image name + `"-image"` |
| `getMaterialIDForIndex(Integer)` | `"material_N_shader"` |
| `getInstanceMaterialSymbolForIndex(Integer)` | Same as material ID (Alice constraint) |
| `getEffectIDForIndex(Integer)` | `"material_N_fx"` |
| `getFullResourceName()` | Model variant texture set or model name |

**Private helpers** (stay private, not listed exhaustively):
`initializeMeshNameMap`, `initializeMaterialNameMap`, `addMeshToNameMap`,
`createFlippedImage`, `writeTexture`, `getColladaFileName`,
`getTextureAppearance`, `addTextureIds`.

**Local testing code** (private static, retained for developer convenience):
`saveColladaToDirectory`, `exportAliceModelToDir`, `loadAliceModel`,
`exportAliceModelResourceToDir`.

### ColladaParser

**Role:** Assembles the COLLADA XML document structure — `Asset` metadata,
`LibraryImages`, `LibraryMaterials`, `LibraryEffects`, `VisualScene`,
and the top-level `COLLADA` element.

**Visibility:** Package-private (no `public` keyword on class).

| Method | Purpose |
|---|---|
| `ColladaParser(ObjectFactory, JointedModelColladaExporter)` | Constructor; receives factory and back-reference for naming helpers |
| `createAsset()` | Builds `Asset` with current timestamp, meter units, Y-up axis |
| `createCollada(SkeletonVisual, ColladaJointExtractor, ColladaMeshProcessor)` | Orchestrates full document: asset → texture libs → mesh/controller libs → visual scene |
| `createAndAddTextureComponents(COLLADA, SkeletonVisual)` | Populates `library_images`, `library_materials`, `library_effects` |
| `createEffect(TexturedAppearance)` | Builds Lambert effect with diffuse texture or color, optional transparency |
| `createCommonColor(String, double, double, double, double)` | Creates a COLLADA `Color` element with RGBA values |
| `createCommonColorType(String, double, double, double, double)` | Wraps `createCommonColor` in a `CommonColorOrTextureType` |
| `createSurfaceParam(Integer)` | `<newparam>` for surface initialization |
| `createSamplerParam(Integer, String)` | `<newparam>` for 2D texture sampler |

### ColladaJointExtractor

**Role:** Walks the `Joint` hierarchy of a `SkeletonVisual` and produces
COLLADA `Node` elements with type=JOINT, including local transformation
matrices flipped from Alice coordinate space to COLLADA space.

**Visibility:** Package-private.

| Method | Purpose |
|---|---|
| `ColladaJointExtractor(ObjectFactory, JointedModelColladaExporter)` | Constructor |
| `createSkeletonNodes(SkeletonVisual)` | Entry point: returns root `Node` for the skeleton, or `null` if no skeleton |
| `createNodeForJoint(Joint)` | Recursive: builds `Node` with SID, matrix, and child joints |

**Coordinate-space behavior:** When `FLIP_COORDINATE_SPACE` is true (default),
all joint matrices are flipped via `ColladaTransformUtilities.createFlippedRowMajorTransform`.
When `SCALE_MODEL` is true, translation components are scaled by `MODEL_SCALE`.

### ColladaMeshProcessor

**Role:** Converts Alice `Mesh` and `WeightedMesh` geometry into COLLADA
`Geometry`, `Controller`, and visual-scene `Node` elements. Contains the
`ListInitializer` hierarchy for converting Java NIO buffers to COLLADA
float arrays.

**Visibility:** Package-private.

| Method | Purpose |
|---|---|
| `ColladaMeshProcessor(ObjectFactory, JointedModelColladaExporter)` | Constructor |
| `createAndAddMeshComponents(COLLADA, VisualScene, SkeletonVisual)` | Top-level: builds `library_geometries` + `library_controllers`, adds visual-scene nodes |
| `addGeometriesForMesh(List<Geometry>, Mesh)` | Adds one `Geometry` per referenced texture ID |
| `geometryForMeshAndTexture(Mesh, Integer)` | Builds single `Geometry` containing positions, normals, UVs, triangles |
| `createMesh(Mesh, Integer, String)` | Constructs COLLADA `Mesh` with sources and triangle indices |
| `createTriangles(Mesh, String, String, String, Integer)` | Builds `Triangles` element from index buffer, filtering by texture ID |
| `createVertices(String, String)` | `Vertices` element referencing position source |
| `createFloatArraySourceFromInitializer(ListInitializer, String, int, double, boolean)` | Builds `Source` from a `ListInitializer`, optionally flipping X/Z and scaling |
| `createParam(String, String)` | Creates an `Accessor` `Param` (name + type) |
| `createAccessorForArray(String, String, int, int)` | Builds `Accessor` for a float/name array with stride |
| `createTechniqueCommonForArray(String, String, int, int)` | Wraps `createAccessorForArray` in a `TechniqueCommon` |
| `createInputLocal(String, String)` | `InputLocal` element (semantic + source ref) |
| `createInputLocalOffset(String, String, int)` | `InputLocalOffset` element (semantic + source ref + offset) |
| `getBindShapeMatrix(WeightedMesh)` | Returns identity bind-shape (Alice convention) |
| `createJointSource(WeightInfo, String)` | `Source` with joint name array |
| `createMatrixSource(WeightInfo, String)` | `Source` with inverse-bind matrices (flipped if `FLIP_COORDINATE_SPACE`) |
| `createWeightList(WeightedMesh)` | Flat `List<Float>` of all per-vertex weights |
| `createSkinWeights(WeightedMesh)` | Per-vertex `ColladaSkinWeights[]` mapping joint/weight indices |
| `addControllersForMesh(List<Controller>, WeightedMesh)` | Builds `Controller` with `Skin` for each texture ID |
| `createSkin(WeightedMesh, String)` | Full skin: bind-shape matrix, joint source, inverse-bind matrices, vertex weights |
| `createBindMaterialForMaterialIndex(Integer)` | `BindMaterial` wiring `instance_material` → `library_materials` |
| `createVisualSceneNode(String)` | Factory for a named visual-scene `Node` |
| `addVisualSceneNodesForMesh(List<Node>, Mesh)` | Adds `instance_geometry` nodes to visual scene |
| `addVisualSceneNodesForWeightedMesh(List<Node>, WeightedMesh)` | Adds `instance_controller` nodes to visual scene |

**Inner types (static, package-private):**

| Type | Purpose |
|---|---|
| `ListInitializer` | Interface: `initializeList(List<Double>)` |
| `DoubleArrayInitializeList` | Wraps `double[]` |
| `DoubleListInitializeList` | Wraps `List<Double>` |
| `FloatListInitializeList` | Wraps `List<Float>`, widens to double |
| `DoubleBufferInitializeList` | Wraps `DoubleBuffer` |
| `FloatBufferInitializeList` | Wraps `FloatBuffer`, widens to double |
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

All constants live on `JointedModelColladaExporter` and are shared via
package-private access:

| Constant | Value | Purpose |
|---|---|---|
| `COLLADA_EXTENSION` | `"dae"` | Output file extension |
| `IMAGE_EXTENSION` | `"png"` | Texture image format |
| `FLIP_COORDINATE_SPACE` | `true` | Enable Alice→COLLADA coordinate flip |
| `SCALE_MODEL` | `false` | Enable model scaling |
| `MODEL_SCALE` | `1.0` | Scale factor (when enabled) |

## Line Counts

| File | Lines | Role |
|---|---|---|
| `JointedModelColladaExporter.java` | ~350 | Coordinator + public API |
| `ColladaMeshProcessor.java` | ~500 | Geometry, skins, controllers, visual-scene nodes |
| `ColladaParser.java` | ~250 | XML document assembly + texture/material/effect builders |
| `ColladaJointExtractor.java` | ~110 | Skeleton tree traversal |
| **Total** | **~1210** | Slightly more than original 1181 due to new-file overhead (package, imports, class declarations) |

The exporter itself drops from 1181 to ~350 lines — well under the 500-line target.
The total line count is slightly higher than the original because each new file
carries its own copyright header, package statement, imports, and class declaration.
