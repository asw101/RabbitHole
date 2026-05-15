# ASGEncoder decomposition: extract BinaryArrayEncoder

The `edu.cmu.cs.dennisc.scenegraph.io.ASGEncoder` class has been reduced from 503 lines to 443 lines by extracting three binary array encoding methods into a new `BinaryArrayEncoder` helper class. This mirrors the existing `BinaryArrayDecoder` pattern already present in the same package.

After decomposition, `ASGEncoder` retains its role as the top-level encoding orchestrator for ASG scene graph serialization (XML document assembly, property encoding, image encoding, ZIP output). The low-level binary stream writing for vertex, int, and double arrays now lives in `BinaryArrayEncoder`, keeping each file focused on a single level of abstraction.

## Finished behavior

### BinaryArrayEncoder

`BinaryArrayEncoder` is a package-private class (not `final`, matching `BinaryArrayDecoder`) with three static methods that write typed arrays to binary streams. It has no mutable state and no public API — it is an internal implementation detail of the `scenegraph.io` package. The file must carry the CMU copyright header, identical to `BinaryArrayDecoder.java`.

| Method | Purpose |
| --- | --- |
| `encodeVertexArray(Vertex[], OutputStream)` | Writes vertex array in version-3 binary format. Each vertex is written as a format bitmask followed by position (3 doubles), normal (3 doubles), diffuse color (4 floats), specular highlight color (4 floats), and texture coordinate (2 floats), depending on which format flags are set. |
| `encodeIntArray(int[], OutputStream)` | Writes int array in version-2 binary format. Header is version (int) + count (int), followed by each element as a raw int. |
| `encodeDoubleArray(double[], OutputStream)` | Writes double array in version-2 binary format. Header is version (int) + count (int), followed by each element as a raw double. |

All three methods wrap the output stream in `BufferedOutputStream` → `DataOutputStream`, write version and length headers, then iterate over the array elements. `IOException` is caught and re-thrown as `RuntimeException`, matching the convention used throughout the ASG I/O layer.

### ASGEncoder (reduced)

`ASGEncoder` retains all high-level encoding responsibilities:

- `encode(Component, OutputStream)` — top-level entry point that writes a ZIP file containing the XML scene graph and binary property data (overloads for `File` and `String` path)
- `encodeInternal(Component, OutputStream, HashMap, boolean)` — assembles the XML document and transformer output
- `encodeComponent(Component, Document, String, HashMap, HashMap, boolean)` — recursive XML builder that walks the scene graph hierarchy
- `encodeElement(Element, Document, String, HashMap, HashMap, boolean)` — XML element builder with inline property encoding (handles matrices, images, colors, arrays, vertices, and element references)
- Text-based encoding helpers (`encodeIntArray`, `encodeDoubleArray`, `encodeTuple3d`, `encodeTuple3f`, `encodeTexCoord2f`, `encodeColor4f`)
- Inner `MatrixUtilities` class for affine and 3×3 matrix row extraction
- `getKey(Element)` — hash-based key generation for element identity

Three thin wrapper methods remain in `ASGEncoder` to preserve the package-internal call-site contract:

```java
static void encodeVertexArrayInBinary(Vertex[] vertices, OutputStream os) {
    BinaryArrayEncoder.encodeVertexArray(vertices, os);
}

static void encodeIntArrayInBinary(int[] array, OutputStream os) {
    BinaryArrayEncoder.encodeIntArray(array, os);
}

static void encodeDoubleArrayInBinary(double[] array, OutputStream os) {
    BinaryArrayEncoder.encodeDoubleArray(array, os);
}
```

These wrappers exist because `ASGDecompositionTest` uses reflection to verify that encoding methods are present on `ASGEncoder`. The wrappers are one-line delegations and add negligible code.

## Symmetry with BinaryArrayDecoder

The extraction deliberately mirrors the existing `BinaryArrayDecoder` class in the same package:

| Aspect | BinaryArrayDecoder | BinaryArrayEncoder |
| --- | --- | --- |
| Visibility | Package-private (`class`) | Package-private (`class`) |
| Modifier | Not `final` | Not `final` |
| Methods | `decodeVertexArray`, `decodeIntArray`, `decodeDoubleArray` | `encodeVertexArray`, `encodeIntArray`, `encodeDoubleArray` |
| State | Stateless, all `static` | Stateless, all `static` |
| Error handling | `IOException` → `RuntimeException` | `IOException` → `RuntimeException` |
| Stream wrapping | `BufferedInputStream` → `DataInputStream` | `BufferedOutputStream` → `DataOutputStream` |
| Extracted from | `ASGDecoder` | `ASGEncoder` |

This symmetry makes the binary format self-documenting: reading the encoder and decoder side by side shows exactly what bytes are written and in what order.

## Binary format reference

### Vertex array (version 3)

```
[int: version = 3]
[int: vertexCount]
for each vertex:
    [int: format bitmask]
    if FORMAT_POSITION:    [double: x] [double: y] [double: z]
    if FORMAT_NORMAL:      [double: nx] [double: ny] [double: nz]
    if FORMAT_DIFFUSE_COLOR:
        [float: red] [float: green] [float: blue] [float: alpha]
    if FORMAT_SPECULAR_HIGHLIGHT_COLOR:
        [float: red] [float: green] [float: blue] [float: alpha]
    if FORMAT_TEXTURE_COORDINATE_0:
        [float: u] [float: v]
```

### Int array (version 2)

```
[int: version = 2]
[int: count]
for each element:
    [int: value]
```

### Double array (version 2)

```
[int: version = 2]
[int: count]
for each element:
    [double: value]
```

These formats are consumed by `BinaryArrayDecoder` and must remain wire-compatible. The encoder always writes the latest version; the decoder reads all historical versions.

> **Note:** The encoder writes normals as `writeDouble` (8 bytes each), but the V3 decoder reads them with `readFloat` (4 bytes each). This is a pre-existing mismatch in the original `ASGEncoder`/`BinaryArrayDecoder` code — not introduced by this extraction. It may indicate that the vertex-with-normal code path is never exercised in practice. Flagged for future investigation (out of scope for this decomposition).

## API reference

### BinaryArrayEncoder

```
package edu.cmu.cs.dennisc.scenegraph.io;

// Package-private — not part of the public API
class BinaryArrayEncoder {
    static void encodeVertexArray(Vertex[] vertices, OutputStream os)
    static void encodeIntArray(int[] array, OutputStream os)
    static void encodeDoubleArray(double[] array, OutputStream os)
}
```

### ASGEncoder wrapper methods (retained)

```
package edu.cmu.cs.dennisc.scenegraph.io;

class ASGEncoder {
    // Delegations to BinaryArrayEncoder
    static void encodeVertexArrayInBinary(Vertex[] vertices, OutputStream os)
    static void encodeIntArrayInBinary(int[] array, OutputStream os)
    static void encodeDoubleArrayInBinary(double[] array, OutputStream os)

    // ... all other encoding methods unchanged ...
}
```

## Design rationale

### Why a separate class instead of private methods

The three binary encoding methods form a cohesive unit: they all follow the same pattern (buffer → data stream → version header → element loop → flush → catch IOException). They have no dependency on `ASGEncoder`'s XML document, property maps, or image handling. Extracting them reduces `ASGEncoder` to a single level of abstraction (XML orchestration) and gives the binary format a discoverable home.

### Why package-private, not public

`BinaryArrayEncoder` is used only by `ASGEncoder` within the same package. The binary format is an internal serialization detail, not part of any published API. Package-private visibility prevents accidental external coupling.

### Why wrapper methods instead of direct callers

`ASGEncoder`'s call sites (property encoding logic) already reference `encodeVertexArrayInBinary`, `encodeIntArrayInBinary`, and `encodeDoubleArrayInBinary`. Retaining thin wrapper methods avoids modifying those call sites and preserves compatibility with `ASGDecompositionTest`, which uses reflection to assert that encoding methods exist on `ASGEncoder`. The wrappers are a single line each and add 9 lines total (3 methods × 3 lines each).

### Why not extract MatrixUtilities too

The inner `MatrixUtilities` class (lines 82–118) is tightly coupled to `ASGEncoder`'s property encoding logic — it converts `AffineMatrix4x4` and `Matrix3x3` values into row arrays for XML text encoding. It has only two methods and no independent use case. Extracting it would create a tiny class with no cohesive purpose beyond serving `ASGEncoder`.

## Validation

The decomposition is validated by:

1. **Line count** — `ASGEncoder.java` drops from 503 to 443 lines (60 lines removed). Well under the 500-line target.
2. **Maven compile** — `mvn compile -pl core/scenegraph -Dcheckstyle.skip=true` succeeds with no errors.
3. **Existing tests** — All 47 tests in `ASGDecompositionTest` and `ASGOutsideInTest` continue to pass, confirming that encoding/decoding round-trips and reflection-based method discovery work correctly.
4. **No behavioral change** — Every extracted method preserves its original implementation verbatim, including stream buffering, version headers, format bitmask handling, and `RuntimeException` wrapping of `IOException`.
5. **Wire compatibility** — The binary format written by `BinaryArrayEncoder` is byte-identical to what `ASGEncoder` previously wrote. `BinaryArrayDecoder` reads it without modification.
