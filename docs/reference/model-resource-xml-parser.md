# ModelResourceXmlParser reference

`ModelResourceXmlParser` is a package-private, final utility class in
`org.lgna.story.resourceutilities` that owns XML DOM traversal and
element-to-object mapping for Alice model resource descriptors.

The class was extracted from `ModelResourceInfo` to separate XML parsing
concerns from metadata storage and property access. All methods are static. The
class has no state and cannot be instantiated.

For the data model and property-access API, see
[ModelResourceInfo](#modelresourceinfo-after-extraction) below. For the export
pipeline that consumes model metadata, see the
[Model resource exporter reference](model-resource-exporter.md).

## When to use this class

Use `ModelResourceXmlParser` when you need to:

- parse an `<AliceModel>` XML element into a `ModelResourceInfo` instance;
- extract bounding-box geometry from a `<BoundingBox>` XML element;
- collect tagged metadata (`<Tags>`, `<GroupTags>`, `<ThemeTags>`) from a
  resource element;
- traverse immediate child elements without descending into nested containers.

Do not use this class directly from code outside
`org.lgna.story.resourceutilities`. The class is package-private. External
callers should use `ModelResourceInfo` constructors, which delegate to these
utilities internally.

## API

| Method | Purpose |
| --- | --- |
| `getBoundingBoxFromXML(Element)` | Extracts min/max coordinates from a `<BoundingBox>` element and returns an `AxisAlignedBox`. Returns `null` if the element is null. |
| `getSubResourceFromXML(Element, ModelResourceInfo)` | Parses a `<Resource>` element into a child `ModelResourceInfo` linked to the given parent. Returns `null` if the element is null. |
| `getResourceTags(Element, String, String)` | Collects tag text content from immediate children matching `tagName`, both directly on the element and inside a container element named `containerTagName`. Returns a `String[]`. |
| `addImmediateChildTextContent(Element, String, LinkedList<String>)` | Appends the text content of each immediate child matching `tagName` to the provided list. |
| `getImmediateChildElementsByTagName(Element, String)` | Returns immediate child elements matching `tagName`, without descending into nested elements (unlike `getElementsByTagName`). |

## Method details

### getBoundingBoxFromXML

```java
static AxisAlignedBox getBoundingBoxFromXML(Element bboxElement)
```

Parses a `<BoundingBox>` element containing `<Min>` and `<Max>` child elements
with `x`, `y`, `z` attributes. Returns an `AxisAlignedBox` or `null` if the
input is null.

Example XML:
```xml
<BoundingBox>
  <Min x="-1.0" y="0.0" z="-2.0"/>
  <Max x="1.0" y="4.0" z="2.0"/>
</BoundingBox>
```

### getSubResourceFromXML

```java
static ModelResourceInfo getSubResourceFromXML(Element resourceElement, ModelResourceInfo parent)
```

Parses a `<Resource>` element into a `ModelResourceInfo` instance. Reads
optional attributes: `resourceName`, `modelName`, `textureName`, `creator`,
`creationYear`, `deprecated`, `placeOnGround`. Extracts tags via
`getResourceTags`. Links the result to the given `parent`.

Returns `null` if the element is null.

### getResourceTags

```java
static String[] getResourceTags(Element resourceElement, String containerTagName, String tagName)
```

Collects tag values from two locations:

1. Immediate children of `resourceElement` matching `tagName` (e.g., bare
   `<Tag>` elements directly under `<Resource>`).
2. Immediate children of container elements matching `containerTagName` (e.g.,
   `<Tag>` elements inside `<Tags>`).

Returns a `String[]` preserving document order (bare tags first, then container
tags). Returns an empty array if no matching elements are found.

> **Note:** When used at root level (`<AliceModel>`), this method also collects
> bare `<Tag>` elements directly on the element — a consistent behavior with
> sub-resource parsing that the original inline code did not have. No existing
> XML files exercise this edge case.

Example — both bare and contained tags are collected:
```xml
<Resource resourceName="DEFAULT" modelName="TreeModel">
  <Tag>bare-tag</Tag>
  <Tags><Tag>contained-tag</Tag></Tags>
</Resource>
```
Result: `["bare-tag", "contained-tag"]`

### addImmediateChildTextContent

```java
static void addImmediateChildTextContent(Element parent, String tagName, LinkedList<String> textContent)
```

Iterates immediate child elements of `parent` matching `tagName` and appends
each element's text content to `textContent`. Does not recurse into nested
elements.

### getImmediateChildElementsByTagName

```java
static List<Element> getImmediateChildElementsByTagName(Element node, String tagName)
```

Returns a list of immediate child elements whose node name matches `tagName`.
Unlike `Element.getElementsByTagName()`, this method only inspects direct
children — it does not descend into nested elements. This is critical for
correct tag parsing: a `<Tag>` inside a `<Nested>` wrapper must not be picked
up when scanning the parent `<Resource>`.

---

## ModelResourceInfo after extraction

After the extraction, `ModelResourceInfo` retains all public API:

### Constructors

| Constructor | Visibility | Purpose |
| --- | --- | --- |
| `ModelResourceInfo(ModelResourceInfo, String, String, int, AxisAlignedBox, String[], String[], String[], String, String, boolean, boolean)` | `public` | Creates an instance with explicit `boolean placeOnGround`. Used by external callers. |
| `ModelResourceInfo(ModelResourceInfo, String, String, int, AxisAlignedBox, String[], String[], String[], String, String, boolean, Boolean)` | package-private | Creates an instance with nullable `Boolean placeOnGround`. Used only by `ModelResourceXmlParser.getSubResourceFromXML()`. |
| `ModelResourceInfo(Document)` | `public` | Parses an XML document. Makes 7 delegation calls to `ModelResourceXmlParser`: 2× `getImmediateChildElementsByTagName`, 1× `getBoundingBoxFromXML`, 3× `getResourceTags`, 1× `getSubResourceFromXML`. |

### Public methods (unchanged)

| Method | Return type | Purpose |
| --- | --- | --- |
| `getBoundingBox()` | `AxisAlignedBox` | Bounding box, falls back to parent. |
| `getCreationYear()` | `int` | Creation year, falls back to parent. `-1` if unset. |
| `getCreator()` | `String` | Creator name, falls back to parent. |
| `getResourceName()` | `String` | Resource identifier, falls back to parent. |
| `getModelName()` | `String` | Model file name, falls back to parent. |
| `getTextureName()` | `String` | Texture identifier, falls back to parent. |
| `getPlaceOnGround()` | `boolean` | Whether to place on ground. Falls back to parent if unset. |
| `getTags()` | `String[]` | Tags merged with parent tags (parent first). |
| `getGroupTags()` | `String[]` | Group tags merged with parent. |
| `getThemeTags()` | `String[]` | Theme tags merged with parent. |
| `getSubResource(String)` | `ModelResourceInfo` | Lookup sub-resource by resource name. |
| `getSubResource(String, String)` | `ModelResourceInfo` | Lookup sub-resource by model+texture, with model-only fallback. |
| `getParent()` | `ModelResourceInfo` | Parent resource info, or `null` for root. |
| `addSubResource(ModelResourceInfo)` | `void` | Adds a child sub-resource. |
| `createShallowCopy()` | `ModelResourceInfo` | Copy without parent link. |
| `createModelManifest()` | `ModelManifest` | Generates a Tweedle `ModelManifest` from this info and sub-resources. |

## File locations

| Class | Path |
| --- | --- |
| `ModelResourceXmlParser` | `core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceXmlParser.java` |
| `ModelResourceInfo` | `core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceInfo.java` |
| `ModelResourceInfoTest` | `core/story-api/src/test/java/org/lgna/story/resourceutilities/ModelResourceInfoTest.java` |
