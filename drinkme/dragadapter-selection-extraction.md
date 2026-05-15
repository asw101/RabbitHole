# DragAdapter selection extraction into DragSelectionController

The `org.alice.interact.DragAdapter` class has been decomposed from a 515-line class into a focused 461-line class by extracting selection state and selection logic into a new `DragSelectionController` delegate. This follows the same delegate pattern already established by `DragEventHandler` (event handling) and `DragCameraController` (camera management): `DragSelectionController` receives a `DragAdapter` reference in its constructor and owns the four selection-related fields and four selection methods that previously inflated `DragAdapter` beyond its core purpose of coordinating drag interaction.

After extraction, `DragAdapter` retains drag coordination, manipulator management, input state, handle management, camera delegation, and event delegation. Selection concerns — which object is selected, marker selection, silhouette management — are owned by `DragSelectionController`.

## Finished behavior

### DragSelectionController

`DragSelectionController` is a package-private class in `org.alice.interact`. It is not `final` (to allow test doubles if needed) but has no public constructor — only a package-private constructor that takes a `DragAdapter`. It owns four fields and four methods extracted from `DragAdapter`.

#### Fields

| Field | Type | Purpose |
| --- | --- | --- |
| `selectedObject` | `AbstractTransformableImp` | The currently selected scene object. Initially `null`. |
| `sgSilhouette` | `Silhouette` | The silhouette applied to the selected model's visuals for highlighting. Initially `null`. |
| `selectedCameraMarker` | `CameraMarkerImp` | The currently selected camera marker. Initially `null`. |
| `selectedObjectMarker` | `ObjectMarkerImp` | The currently selected object marker. Initially `null`. |

#### Methods

| Method | Visibility | Purpose |
| --- | --- | --- |
| `setSelectedCameraMarker(CameraMarkerImp)` | package-private | Manages camera marker selection: creates `new SelectionEvent(dragAdapter, selected)` and fires it via `dragAdapter.fireSelecting()`, adjusts opacity on old/new markers (0.3f / 1.0f), and toggles `setDetailedViewShowing` on `PerspectiveCameraMarkerImp` when `dragAdapter.hasSceneEditor()` is true. |
| `setSelectedObjectMarker(ObjectMarkerImp)` | package-private | Manages object marker selection: creates `new SelectionEvent(dragAdapter, selected)` and fires it via `dragAdapter.fireSelecting()`, adjusts opacity on old/new markers (0.3f / 1.0f). |
| `setSelectedObjectSilhouetteIfAppropriate(boolean)` | `private` | Applies or removes the `sgSilhouette` on all visuals of the selected object, if the selected object is a `ModelImp`. |
| `setSelectedSceneObjectImplementation(AbstractTransformableImp)` | package-private | Core selection logic: creates `new SelectionEvent(dragAdapter, selected)` and fires it, updates silhouette, configures handle manager via `dragAdapter.getHandleManager()`, updates `dragAdapter.currentInputState` (calls `setCurrentlySelectedObject()` and `setTimeCaptured()`), stores selected object, fires state change via `dragAdapter.fireStateChange()`. Uses `HandleManager.isSelectable()` (static) for handle-eligibility check. |

**Implementation note — `SelectionEvent` source**: `SelectionEvent extends Event<DragAdapter>` (see `SelectionEvent.java:53`). All three event-firing methods must construct `new SelectionEvent(dragAdapter, ...)` — not `new SelectionEvent(this, ...)`. The compiler enforces this (the controller is not a `DragAdapter`), but it is worth noting since the original code uses `new SelectionEvent(this, selected)` where `this` is the `DragAdapter`.

#### Getters

| Method | Return type | Purpose |
| --- | --- | --- |
| `getSelectedObject()` | `AbstractTransformableImp` | Returns the currently selected object. Used by `DragAdapter` methods that read selection state. |
| `getSelectedCameraMarker()` | `CameraMarkerImp` | Returns the currently selected camera marker. |
| `getSelectedObjectMarker()` | `ObjectMarkerImp` | Returns the currently selected object marker. |

#### Setter

| Method | Parameter type | Purpose |
| --- | --- | --- |
| `setSgSilhouette(Silhouette)` | `Silhouette` | Stores the silhouette used for highlighting. Called from `DragAdapter.setSgSilhouette()`. |

### DragAdapter (after extraction)

`DragAdapter` retains all original public and protected API. The class Javadoc is updated to reference the new delegate:

```java
/**
 * @author Dennis Cosgrove
 *
 * inherited by RuntimeDragAdapter, CroquetSupporting/Global DragAdapter,
 * CreateAPersonDragAdapter, PoserAnimatorDragAdapter, SingleViewerDragAdapter
 *
 * Event handling delegated to {@link DragEventHandler}.
 * Camera management delegated to {@link DragCameraController}.
 * Selection state delegated to {@link DragSelectionController}.
 */
```

#### New field

```java
final DragSelectionController selectionController = new DragSelectionController(this);
```

Follows the same pattern as `eventHandler` and `cameraController`.

#### Visibility change

`fireSelecting(SelectionEvent)` is widened from `private` to package-private (no modifier keyword). This is required because `DragSelectionController` — in the same package — must fire selecting events when marker or scene object selection changes. `fireSelected` is already called only from `DragAdapter` and remains `private`.

#### Methods modified to use delegate

| Method | Change |
| --- | --- |
| `setSelectedImplementation(AbstractTransformableImp)` | Reads `selectionController.getSelectedObject()` for joint-detection logic. Calls `this.setSelectedObjectMarker()` and `this.setSelectedCameraMarker()` (forwarding stubs — preserves subclass override semantics). Calls `selectionController.setSelectedSceneObjectImplementation()` directly (no forwarding stub exists for this private-origin method). |
| `setHandleShowingForSelectedImplementation(AbstractTransformableImp, boolean)` | Reads `selectionController.getSelectedObject()` instead of `this.selectedObject`. |
| `triggerImplementationSelection(AbstractTransformableImp)` | Reads `selectionController.getSelectedObject()` instead of `this.selectedObject`. |
| `setCurrentInteractionState(InteractionGroup)` | Reads `selectionController.getSelectedObject()` instead of `this.selectedObject`. |

#### Forwarding stubs (API compatibility)

```java
public void setSelectedCameraMarker(CameraMarkerImp selected) {
  this.selectionController.setSelectedCameraMarker(selected);
}

public void setSelectedObjectMarker(ObjectMarkerImp selected) {
  this.selectionController.setSelectedObjectMarker(selected);
}

protected void setSgSilhouette(Silhouette sgSilhouette) {
  this.selectionController.setSgSilhouette(sgSilhouette);
}
```

Each is a single-line method. The original multi-line bodies are removed.

## Public API changes

**None.** All public and protected method signatures on `DragAdapter` are unchanged. Subclasses (`RuntimeDragAdapter`, `CreateAPersonDragAdapter`, etc.) that call `setSelectedCameraMarker()`, `setSelectedObjectMarker()`, or `setSgSilhouette()` continue to call the same methods on `DragAdapter`. The forwarding is invisible to callers.

`DragSelectionController` is package-private and is not part of the public API.

## File inventory

| File | Lines (approx) | Status |
| --- | --- | --- |
| `core/story-api/src/main/java/org/alice/interact/DragAdapter.java` | ~461 | Modified (delegate field + forwarders, removed extracted bodies) |
| `core/story-api/src/main/java/org/alice/interact/DragSelectionController.java` | ~120 (75 + header) | New |
| `core/story-api/src/test/java/org/alice/interact/DragSelectionControllerTest.java` | ~100 (60 + header) | New (characterization test) |

## Build and test

No POM changes are required. `DragSelectionController` is in the same package and module as `DragAdapter`.

```bash
# Full module test from repository root
mvn -pl core/story-api -am test -q

# Run only the new characterization test
mvn -pl core/story-api -am test -Dtest="DragSelectionControllerTest" -q
```

Both commands must exit 0 before the change is considered complete.

## Characterization tests

### DragSelectionControllerTest

Tests verify delegate behavior matches the original `DragAdapter` behavior. All tests use mocks or minimal stubs since `DragAdapter` is abstract and its dependencies (`HandleManager`, `OnscreenRenderTarget`) are heavyweight.

| Test | Asserts |
| --- | --- |
| `getSelectedObject_initiallyNull` | `selectionController.getSelectedObject()` returns `null` after construction. |
| `setSelectedSceneObjectImplementation_updatesSelectedObject` | After calling `setSelectedSceneObjectImplementation(mockImpl)`, `getSelectedObject()` returns `mockImpl`. |
| `setSelectedSceneObjectImplementation_updatesInputState` | After calling `setSelectedSceneObjectImplementation(mockImpl)`, `dragAdapter.currentInputState.getCurrentlySelectedObject()` matches the impl's sgComposite. |
| `setSelectedSceneObjectImplementation_firesSelectingEvent` | Calling `setSelectedSceneObjectImplementation(mockImpl)` invokes `dragAdapter.fireSelecting()` exactly once. |
| `setSelectedSceneObjectImplementation_firesStateChange` | Calling `setSelectedSceneObjectImplementation(mockImpl)` invokes `dragAdapter.fireStateChange()` exactly once. |
| `setSelectedSceneObjectImplementation_skipsWhenSameObject` | Calling `setSelectedSceneObjectImplementation` twice with the same object only fires once. |
| `setSelectedCameraMarker_updatesMarker` | After calling `setSelectedCameraMarker(mockMarker)`, `getSelectedCameraMarker()` returns `mockMarker`. |
| `setSelectedCameraMarker_setsOpacityOnOldMarker` | When changing from marker A to marker B, marker A's opacity is set to 0.3f. |
| `setSelectedCameraMarker_setsOpacityOnNewMarker` | When setting marker B, its opacity is set to 1.0f. |
| `setSelectedCameraMarker_skipsWhenSameMarker` | Calling with the same marker twice does not re-fire selecting event. |
| `setSelectedObjectMarker_updatesMarker` | After calling `setSelectedObjectMarker(mockMarker)`, `getSelectedObjectMarker()` returns `mockMarker`. |
| `setSelectedObjectMarker_setsOpacityOnOldMarker` | When changing from marker A to marker B, marker A's opacity is set to 0.3f. |
| `setSelectedObjectMarker_setsOpacityOnNewMarker` | When setting marker B, its opacity is set to 1.0f. |
| `setSgSilhouette_storesSilhouette` | After calling `setSgSilhouette(mockSilhouette)`, the silhouette is used when `setSelectedObjectSilhouetteIfAppropriate(true)` is later invoked. |

## Design decisions

1. **Delegate pattern, not utility class**: Unlike `JavaTypePrimitiveMapping` (static utility), `DragSelectionController` holds mutable instance state (the four selection fields). It follows the same constructor-injection delegate pattern as `DragEventHandler` and `DragCameraController`. The `DragAdapter` reference enables callbacks (`fireSelecting`, `fireStateChange`, `hasSceneEditor`, etc.) without introducing new interfaces.

2. **Package-private, non-final**: The class has no access modifier (package-private). It is not marked `final` to allow test subclasses if needed, though the current tests use mock `DragAdapter` instances instead. The constructor is package-private, preventing instantiation from outside `org.alice.interact`.

3. **Single visibility widening**: Only `fireSelecting` changes from `private` to package-private. All other `DragAdapter` methods accessed by `DragSelectionController` — `hasSceneEditor()`, `getHandleManager()`, `fireStateChange()`, and `currentInputState` — are already `protected`, `public`, or package-private. The `org.alice.interact` package is not a published API surface; widening a single method within it carries no risk.

4. **Forwarding preserves API and override semantics**: The three public/protected methods (`setSelectedCameraMarker`, `setSelectedObjectMarker`, `setSgSilhouette`) become one-line forwarders. No subclass overrides any of these methods (grep-verified across `RuntimeDragAdapter`, `GlobalDragAdapter`, `CroquetSupportingDragAdapter`, `OnscreenLookingGlassDragAdapter`, `SingleViewerDragAdapter`, `PoserAnimatorDragAdapter`, `CreateAPersonDragAdapter`). External callers in `SceneEditorFieldManager` and `StorytellingSceneEditor` invoke them on `DragAdapter` instances via the public API and are unaffected. Internal calls from `setSelectedImplementation` go through the forwarding stubs (not directly to the controller) to preserve future override semantics.

5. **No new interfaces**: The delegate holds a direct `DragAdapter` reference rather than an interface. Introducing a callback interface (e.g., `SelectionHost`) was considered and rejected — it would add a file and abstraction for exactly one implementation, violating the project's preference for simplicity over speculative generality.

## Risks and mitigations

| Risk | Likelihood | Mitigation |
| --- | --- | --- |
| Subclass accesses extracted private field directly | None | Grep-verified: no subclass accesses `selectedObject`, `sgSilhouette`, `selectedCameraMarker`, or `selectedObjectMarker`. All are `private` fields — only accessible via methods on the declaring class. |
| `fireSelecting` visibility widened to package-private | Low | The `org.alice.interact` package is internal to `core/story-api`. No external module references `fireSelecting`. Package-private is the minimum visibility required. |
| Initialization order between `DragAdapter` and `DragSelectionController` | None | `DragSelectionController` is constructed in the field initializer of `DragAdapter`, after all super-class initialization. The delegate's constructor only stores the `DragAdapter` reference — it does not call any methods on it. |
| Test infrastructure — `DragAdapter` is abstract | Low | Tests create a minimal concrete subclass (anonymous inner class) that implements the one abstract method (none currently — `DragAdapter` is abstract but has no abstract methods, so an empty `{}` body suffices). Alternatively, tests use Mockito to create a partial mock. |

## Usage example

Callers of `DragAdapter` see no change. Selection continues to work through the same public methods:

```java
// In a subclass of DragAdapter (e.g., RuntimeDragAdapter):
this.setSelectedImplementation(someModelImpl);       // unchanged API
this.setSelectedCameraMarker(someCameraMarkerImpl);  // unchanged API
this.setSgSilhouette(someSilhouette);                // unchanged API

// Internal to DragAdapter, selection state is now accessed via delegate:
AbstractTransformableImp current = this.selectionController.getSelectedObject();
```

No configuration changes, no POM changes, no module-info changes. The extraction is purely structural.
