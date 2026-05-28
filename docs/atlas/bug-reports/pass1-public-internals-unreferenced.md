## Bug: story-api exposes large internal classes that nothing outside the module uses

**Layer**: ast-lsp-bindings
**Severity**: Medium
**Pass**: 1

**Evidence**:

- The following large classes are still `public` in `core/story-api`, but a repo-wide grep across non-`story-api` production modules found no references to them: `SnapUtilities`, `MouseRelativeObjectDragManipulator`, `ObjectTranslateDragManipulator`, `GlrJointedModelVisualization`, and `EventManager`.
- `core/story-api/src/main/java/org/alice/interact/manipulator/SnapUtilities.java:71` declares `public class SnapUtilities`.
- `core/story-api/src/main/java/org/alice/interact/manipulator/MouseRelativeObjectDragManipulator.java:67` declares `public class MouseRelativeObjectDragManipulator ...`.
- `core/story-api/src/main/java/org/alice/interact/manipulator/ObjectTranslateDragManipulator.java:68` declares `public class ObjectTranslateDragManipulator ...`.
- `core/story-api/src/main/java/org/lgna/story/implementation/visualization/GlrJointedModelVisualization.java:70` declares `public class GlrJointedModelVisualization ...`.
- `core/story-api/src/main/java/org/lgna/story/implementation/eventhandling/EventManager.java:75` declares `public class EventManager`.
- code_quote: `core/story-api/src/main/java/org/alice/interact/manipulator/SnapUtilities.java:71-72`
  ```java
  public class SnapUtilities {
    public static final double SNAP_LINE_VISUAL_HEIGHT = .01d;
  }
  ```
- code_quote: `core/story-api/src/main/java/org/lgna/story/implementation/eventhandling/EventManager.java:75-78`
  ```java
  public class EventManager {

    private final SceneImp scene;
    private final KeyPressedHandler keyHandler = new KeyPressedHandler();
  }
  ```

**Impact**: The public surface of `core/story-api` is wider than the rest of the codebase actually consumes. That inflates the atlas' apparent API contract, makes implementation details look stable, and raises the cost of refactoring these internals.

**Fix**: Re-run the `ast-lsp-bindings` layer with a dead-public-symbol report and either reduce these classes to package-private/internal visibility or document why they must remain public despite having no external consumers.
