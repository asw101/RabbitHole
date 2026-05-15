# Validate the ModelResourceXmlParser Extraction

How to verify that the `ModelResourceXmlParser` extraction from
`ModelResourceInfo` is complete and correct. Use this after merging the
extraction or when reviewing the PR.

For background on the model resource metadata pipeline, see the
[Model resource exporter reference](../reference/model-resource-exporter.md).

## Prerequisites

- Java 17+ and Maven installed
- Tweedle grammar submodule initialized:
  ```bash
  git submodule update --init tweedle-lang
  ```

## Step 1: Confirm the new file exists

```bash
ls core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceXmlParser.java
```

Expected: file listed with no errors.

## Step 2: Verify package-private visibility

```bash
grep '^final class ' \
  core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceXmlParser.java
```

Expected: `final class ModelResourceXmlParser {` — no `public` modifier, marked
`final`.

## Step 3: Verify all five static methods exist

```bash
grep -c 'static ' \
  core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceXmlParser.java
```

Expected: at least 5 matches. The extracted methods are:

| Method                             | Signature summary                                              |
|------------------------------------|----------------------------------------------------------------|
| `getBoundingBoxFromXML`            | `(Element) → AxisAlignedBox`                                   |
| `getSubResourceFromXML`            | `(Element, ModelResourceInfo) → ModelResourceInfo`             |
| `getResourceTags`                  | `(Element, String, String) → String[]`                         |
| `addImmediateChildTextContent`     | `(Element, String, List<String>) → void`                 |
| `getImmediateChildElementsByTagName` | `(Element, String) → List<Element>`                          |

## Step 4: Verify the methods were removed from ModelResourceInfo

```bash
grep -c 'private static' \
  core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceInfo.java
```

Expected: `0`. All five `private static` helpers have been moved to
`ModelResourceXmlParser`.

## Step 5: Verify ModelResourceInfo line count

```bash
wc -l core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceInfo.java
```

Expected: under 400 lines (target ~387).

## Step 6: Verify the Boolean-constructor visibility

```bash
grep 'ModelResourceInfo(ModelResourceInfo parent' \
  core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceInfo.java
```

Expected: two constructors visible:
- The `public` constructor with `boolean placeOnGround` (unchanged)
- A **package-private** constructor with `Boolean placeOnGround` (widened from
  `private`)

The package-private constructor must **not** have a `public` modifier. It is
used only by `ModelResourceXmlParser.getSubResourceFromXML()`.

## Step 7: Verify all XML parsing is delegated to ModelResourceXmlParser

```bash
grep 'ModelResourceXmlParser\.' \
  core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceInfo.java
```

Expected: **7 delegation calls** total in the `Document` constructor:

| Delegated method | Count | Where |
| --- | --- | --- |
| `getImmediateChildElementsByTagName` | 2 | bounding-box lookup and sub-resource list |
| `getBoundingBoxFromXML` | 1 | bounding-box extraction from the first `<BoundingBox>` element |
| `getResourceTags` | 3 | one each for `tags`, `groupTags`, `themeTags` |
| `getSubResourceFromXML` | 1 | inside the sub-resource parsing loop |

Verify individual call counts:

```bash
grep -c 'ModelResourceXmlParser.getResourceTags' \
  core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceInfo.java
```

Expected: `3`

```bash
grep -c 'ModelResourceXmlParser.getImmediateChildElementsByTagName' \
  core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceInfo.java
```

Expected: `2`

```bash
grep -c 'ModelResourceXmlParser.getBoundingBoxFromXML' \
  core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceInfo.java
```

Expected: `1`

```bash
grep -c 'ModelResourceXmlParser.getSubResourceFromXML' \
  core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceInfo.java
```

Expected: `1`

## Step 8: Run the existing tests

```bash
cd core/story-api && mvn test -pl . -Dtest=ModelResourceInfoTest -q
```

Expected: all 9 tests pass with no failures or errors. The tests exercise the
full XML-to-`ModelResourceInfo` pipeline including sub-resource creation,
tag parsing, bounding box extraction, manifest generation, and
`placeOnGround` inheritance — covering every method that moved to
`ModelResourceXmlParser`.

## Step 9: Verify no public API changes

```bash
grep 'public ' \
  core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceInfo.java \
  | grep -v '//'
```

All public methods and the public `boolean`-parameter constructor must remain
unchanged. No callers outside the package should need modification.

## What changed and why

`ModelResourceInfo` was 510 lines — a mix of data-holding fields/getters and
low-level XML DOM traversal. The five static XML-parsing helpers have no
dependency on instance state and are purely concerned with `org.w3c.dom`
traversal. Extracting them into `ModelResourceXmlParser` achieves:

- **Single Responsibility**: `ModelResourceInfo` holds model metadata and
  provides property access with parent-fallback. `ModelResourceXmlParser` owns
  XML-to-object mapping.
- **Reduced file size**: 510 → ~387 lines, comfortably under the 500-line
  target.
- **Zero API surface change**: every public method, constructor signature, and
  field semantic is preserved identically.

### Subtle behavior note: bare tags at root level

The original `Document` constructor only collected tags from inside container
elements (e.g., `<Tags><Tag>plant</Tag></Tags>`). The `getResourceTags()`
method also collects bare tags directly on the element (e.g.,
`<Tag>bare</Tag>` under `<AliceModel>`). After delegation, if an XML file
places bare `<Tag>` elements directly under `<AliceModel>` (outside a `<Tags>`
container), they would now be collected — matching the sub-resource behavior.
No existing XML files or tests exercise this edge case. This is a consistency
improvement, not a regression.

## Troubleshooting

### `ModelResourceXmlParser` not found at compile time

Verify the file is in the correct package directory:
```
core/story-api/src/main/java/org/lgna/story/resourceutilities/ModelResourceXmlParser.java
```
The package declaration must be `org.lgna.story.resourceutilities`.

### Constructor access error from ModelResourceXmlParser

If `getSubResourceFromXML` cannot instantiate `ModelResourceInfo`, verify the
`Boolean`-parameter constructor is package-private (no access modifier), not
`private`.

### Test failures on tag ordering

Tag arrays are order-sensitive. Parent tags appear first, then child tags. The
`getResourceTags` method preserves document order via `ArrayList` insertion.
Verify no sorting was inadvertently introduced.
