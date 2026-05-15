# IDE.java decomposition: SceneSetupManager extraction

The scene-setup code generation and field reorganization logic has been extracted from `org.alice.ide.IDE` into a new package-private delegate class `org.alice.ide.SceneSetupManager`. This reduces `IDE.java` from 539 lines to ~436 lines (well under the 500-line target) while preserving all runtime behavior.

The extraction follows the same delegate pattern used elsewhere in the IDE module: `SceneSetupManager` holds a back-reference to the `IDE` instance and is wired in IDE's constructor. No public API surface changes. No new module or POM dependencies.

## Finished behavior

### SceneSetupManager

`SceneSetupManager` is a package-private, final class that owns two responsibilities previously embedded in `IDE`:

1. **Scene setup code generation** — clearing and repopulating the `performEditorGeneratedSetUp` method body with auto-generated statements from the scene editor.
2. **Field reorganization** — detecting and fixing field ordering problems where a field's initializer references a field declared after it, which would cause a runtime failure.

| Method | Original IDE lines | Purpose |
| --- | --- | --- |
| `generateCodeForSceneSetUp()` | L498–507 | Clears the setup method body, optionally inserts an inner comment, then delegates to `SceneEditor.generateCodeForSetUp()` to populate statements. |
| `reorganizeFieldsIfNecessary()` | L314–326 | Iterates all `NamedUserType` instances in the project and calls `reorganizeTypeFieldsIfNecessary` for each. Shows an error dialog on cycle detection. |
| `reorganizeTypeFieldsIfNecessary(NamedUserType, int, Set<UserField>)` | L249–312 | Recursively reorders fields so that no field's initializer references a not-yet-initialized field. Returns a cycle-detection warning message or null on success. |

The private static nested class `UnacceptableFieldAccessCrawler` (originally L235–247) moves into `SceneSetupManager` as a private static inner class, unchanged.

### IDE (reduced)

`IDE` retains all of its existing public and protected API:

- `createVirtualMachineForSceneEditor()` (protected, overridable)
- `createRegisteredVirtualMachineForSceneEditor()` (public final)
- `getPerformEditorGeneratedSetUpMethod()` (public abstract)
- `getSceneEditor()` (public abstract)
- `crawlFilteredProgramType()`, `getProgramType()`, `getUpToDateProgramType()`
- `ensureProjectCodeUpToDate()`, `forceProjectCodeUpToDate()`
- `updateProject()` — still on IDE, still holds `synchronized(project.getLock())`
- All other abstract methods, the `AccessorAndMutatorDisplayStyle` enum, theme management, locale handling, and crash detection

The only structural change to `IDE` is:

1. A new `private final SceneSetupManager sceneSetupManager` field.
2. Constructor initialization: `this.sceneSetupManager = new SceneSetupManager(this)` in the constructor, after `super(apiConfigurationManager)` and `crashDetector` assignment.
3. `updateProject()` now delegates to the manager instead of calling the methods directly.

### What does NOT move

- `createVirtualMachineForSceneEditor` — protected and overridden by subclasses; cannot move to a delegate.
- `createRegisteredVirtualMachineForSceneEditor` — public final, calls the protected factory above.
- `getPerformEditorGeneratedSetUpMethod` — abstract, defines the IDE contract.
- `getSceneEditor` — abstract, used broadly.
- `updateHistoryIndexSceneSetUpSync` — called in `updateProject()` but not part of the scene-setup concern.

## Delegation pattern

`IDE.updateProject()` is the integration point. It remains on IDE because it holds the project lock and sequences three operations:

```java
private void updateProject(Project project) {
    synchronized (project.getLock()) {
        sceneSetupManager.generateCodeForSceneSetUp();
        sceneSetupManager.reorganizeFieldsIfNecessary();
        updateHistoryIndexSceneSetUpSync();
    }
}
```

The lock stays on IDE because `updateProject` coordinates work beyond scene setup (the history sync). The manager methods are not independently synchronized — they execute within IDE's lock.

## SceneSetupManager constructor and IDE access

`SceneSetupManager` receives the `IDE` instance through its constructor and uses it to access:

| IDE method called | Used by | Purpose |
| --- | --- | --- |
| `getPerformEditorGeneratedSetUpMethod()` | `generateCodeForSceneSetUp` | Obtains the `UserMethod` whose body will be regenerated |
| `getInnerCommentForMethodName(String)` | `generateCodeForSceneSetUp` | Gets the comment text to insert at the top of the generated method body |
| `getSceneEditor()` | `generateCodeForSceneSetUp` | Delegates to the scene editor for statement generation |
| `getProject()` | `reorganizeFieldsIfNecessary` | Obtains the current project to iterate its types |
| `getApplicationName()` | `reorganizeTypeFieldsIfNecessary` | Used in cycle-detection warning message |

All five methods are accessible from within the `org.alice.ide` package. No visibility changes are required on IDE.

## Import changes

### Imports removed from IDE.java

These imports were used exclusively by the extracted code and are no longer needed in IDE:

| Import | Used by (now in SceneSetupManager) |
| --- | --- |
| `edu.cmu.cs.dennisc.java.util.Sets` | `reorganizeFieldsIfNecessary`, `reorganizeTypeFieldsIfNecessary` |
| `edu.cmu.cs.dennisc.javax.swing.option.Dialogs` | `reorganizeFieldsIfNecessary` |
| `edu.cmu.cs.dennisc.pattern.IsInstanceCrawler` | `UnacceptableFieldAccessCrawler` |
| `org.lgna.project.ast.StatementListProperty` | `generateCodeForSceneSetUp` |
| `org.lgna.project.ast.UserField` | `UnacceptableFieldAccessCrawler`, `reorganizeTypeFieldsIfNecessary`, `reorganizeFieldsIfNecessary` |
| `java.util.Set` | `UnacceptableFieldAccessCrawler`, `reorganizeTypeFieldsIfNecessary`, `reorganizeFieldsIfNecessary` |

### Imports retained on IDE.java

| Import | Retained because |
| --- | --- |
| `org.lgna.project.ast.Comment` | Used by `commentThatWantsFocus` field and accessors (L321–329) |
| `org.lgna.project.ast.FieldAccess` | Used by `getFieldAccesses` (L267) and `getPrefixPaneForFieldAccessIfAppropriate` (L423) |
| `org.lgna.project.ast.UserMethod` | Used by abstract `getPerformEditorGeneratedSetUpMethod()` declaration (L221) |
| `edu.cmu.cs.dennisc.pattern.Crawler` | Used by `crawlFilteredProgramType` (L225) |
| `edu.cmu.cs.dennisc.pattern.Criterion` | Used by `getDeclarationFilter` (L223) |
| `org.lgna.project.ast.CrawlPolicy` | Used by `crawlFilteredProgramType` (L228) |
| `org.lgna.project.ast.Expression` | Used by `isDropDownDesiredFor` (L279) |

## API reference

```
package org.alice.ide;

final class SceneSetupManager {
    SceneSetupManager(IDE ide)

    void generateCodeForSceneSetUp()
    void reorganizeFieldsIfNecessary()
    private String reorganizeTypeFieldsIfNecessary(NamedUserType namedUserType,
                                                   int startIndex,
                                                   Set<UserField> alreadyMovedFields)
}
```

The class and its constructor and public-facing methods (`generateCodeForSceneSetUp`, `reorganizeFieldsIfNecessary`) are package-private (no access modifier). `reorganizeTypeFieldsIfNecessary` is `private` since it is only called internally. The class is `final` and cannot be subclassed. `UnacceptableFieldAccessCrawler` is a `private static` nested class inside `SceneSetupManager`, invisible to all other code.

## Thread safety

Thread safety is unchanged. The `synchronized(project.getLock())` block remains in `IDE.updateProject()`. The `SceneSetupManager` methods execute within that lock — they do not introduce their own synchronization and must not be called outside the project lock.

## Recursive field reordering

`reorganizeTypeFieldsIfNecessary` calls itself recursively when it slides a field to the end and needs to re-check from the new position. This recursive call was previously `this.reorganizeTypeFieldsIfNecessary(...)` on IDE; it is now a regular method call within `SceneSetupManager` — no delegation back to IDE is needed. The `alreadyMovedFields` set prevents infinite recursion by detecting cycles.

## Line count accounting

| Change | Lines |
| --- | --- |
| Lines removed from IDE.java (L235–326, L498–507) | −102 |
| Lines added to IDE.java (field declaration, constructor init, delegate calls) | +5 |
| Imports removed from IDE.java | −6 |
| **Net reduction** | **~103** |
| **IDE.java final line count** | **~436** |

`SceneSetupManager.java` is approximately 180 lines including the license header, imports, and class body.

## Build and test

```bash
# Compile the module
mvn compile -pl core/ide

# Run module tests
mvn test -pl core/ide
```

The extraction is a pure structural refactoring — all existing tests exercise the same code paths through IDE's unchanged public API. A `SceneSetupManagerExtractionContractTest` verifies the structural contract (class visibility, method signatures, delegation field, import cleanup, and IDE line count) via reflection. The `ensureProjectCodeUpToDate()` → `forceProjectCodeUpToDate()` → `updateProject()` call chain is the only entry point to the extracted logic, and it continues to work identically.
