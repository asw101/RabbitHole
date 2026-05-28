## Bug: drinkme drag-adapter notes still reference deleted CreateAPersonDragAdapter

**Layer**: stale documentation
**Severity**: Low
**Pass**: 1

**Evidence**:

- `drinkme/dragadapter-selection-extraction.md:162` says the subclass grep was verified across `CreateAPersonDragAdapter` as if it were still a current implementation.
- The current `DragAdapter` comment in `core/story-api/src/main/java/org/alice/interact/DragAdapter.java:84-85` also still names `CreateAPersonDragAdapter`.
- Current production subclasses visible in source are `RuntimeDragAdapter`, `GlobalDragAdapter`, `CroquetSupportingDragAdapter`, `OnscreenLookingGlassDragAdapter`, `PoserAnimatorDragAdapter`, and `SingleViewerDragAdapter`; there is no `CreateAPersonDragAdapter` class definition in the repository.
- code_quote: `drinkme/dragadapter-selection-extraction.md:162`
  ```text
  ... grep-verified across `RuntimeDragAdapter`, `GlobalDragAdapter`, `CroquetSupportingDragAdapter`, `OnscreenLookingGlassDragAdapter`, `SingleViewerDragAdapter`, `PoserAnimatorDragAdapter`, `CreateAPersonDragAdapter` ...
  ```
- code_quote: `core/story-api/src/main/java/org/alice/interact/DragAdapter.java:84-85`
  ```java
   * inherited by RuntimeDragAdapter, CroquetSupporting/Global DragAdapter,
   * CreateAPersonDragAdapter, PoserAnimatorDragAdapter, SingleViewerDragAdapter
  ```

**Impact**: Investigation notes point readers at a class that no longer exists, so anyone following the drag-adapter lineage has to stop and reconcile stale names before they can trust the document.

**Fix**: Remove `CreateAPersonDragAdapter` from `drinkme/dragadapter-selection-extraction.md` and from the `DragAdapter` class comment, then regenerate the subclass list from current source.
