# Characterize Project Save and Export Operations

Use this guide to build the first compatibility tests for Alice project Save, Save As, and Export operations without changing user-visible behavior.

## Contents

- [Prerequisites](#prerequisites)
- [Feature scope](#feature-scope)
- [Choose the behavior](#choose-the-behavior)
- [Add a direct operation test](#add-a-direct-operation-test)
- [Add a flow-level test after a seam exists](#add-a-flow-level-test-after-a-seam-exists)
- [Run the focused tests](#run-the-focused-tests)

## Prerequisites

Work in the `core/ide` module and keep tests in the same Java package as the operation classes:

```text
core/ide/src/test/java/org/alice/ide/croquet/models/projecturi/
```

Use JUnit 4 and existing test fixtures. Do not add a mocking library for this package.

## Feature scope

The initial feature is a direct characterization-test suite for the operation classes. It should cover:

| Class | First-pass coverage |
| --- | --- |
| `SaveProjectOperation` | `null` file prompts, writable file does not prompt, non-reusable file prompts, project extension, toolbar text clobbering. |
| `SaveAsProjectOperation` | Always prompts and uses the project extension inherited from `AbstractSaveProjectOperation`. |
| `ExportProjectOperation` | Always prompts, uses the export extension, and clobbers toolbar text. |
| `AbstractSaveProjectOperation` | Shared project extension as observed through Save and Save As operations. |

The initial feature should not test `AbstractSaveOperation.perform(UserActivity)` directly. That flow needs a later package-private seam before tests can supply the active application, save dialog result, error reporter, wait cursor hooks, and save/export callback without static or UI interception.

## Choose the behavior

Start with the highest-value untested behavior in the operation layer:

| Behavior | Test target |
| --- | --- |
| Save prompt routing | `SaveProjectOperation` |
| Save As always prompts | `SaveAsProjectOperation` |
| Export always prompts and uses export extension | `ExportProjectOperation` |
| Shared project extension | `AbstractSaveProjectOperation` as observed through Save and Save As operations |
| Finish, cancel, wait cursor, and retry flow | Deferred until `AbstractSaveOperation` has a narrow seam |

Keep archive-content tests in lower-level project IO test classes. First-pass operation tests should verify prompt routing and extensions. Activity outcome and delegation decisions belong in later flow tests after a seam exists.

## Add a direct operation test

Direct operation tests live in the operation package so they can exercise protected methods without widening production APIs.

Example: characterize Save prompt routing.

```java
package org.alice.ide.croquet.models.projecturi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.io.IoUtilities;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SaveProjectOperationTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void promptsWhenCurrentFileIsMissing() {
    assertTrue(SaveProjectOperation.getInstance().isPromptNecessary(null));
  }

  @Test
  public void doesNotPromptForWritableCurrentFile() throws Exception {
    File projectFile = temporaryFolder.newFile("RobotDance.a3p");

    assertFalse(SaveProjectOperation.getInstance().isPromptNecessary(projectFile));
  }

  @Test
  public void promptsWhenCurrentFileCannotBeReused() {
    File missingProjectFile = new File(temporaryFolder.getRoot(), "MissingRobotDance.a3p");

    assertTrue(SaveProjectOperation.getInstance().isPromptNecessary(missingProjectFile));
  }

  @Test
  public void usesProjectExtension() {
    assertEquals(IoUtilities.PROJECT_EXTENSION, SaveProjectOperation.getInstance().getExtension());
  }

  @Test
  public void clobbersToolbarText() {
    assertTrue(SaveProjectOperation.getInstance().isToolBarTextClobbered());
  }
}
```

Use a missing file as the portable "cannot be reused" case. A file with permissions changed to non-writable can also characterize the same rule, but that assertion is more sensitive to the operating system and test user.

## Add a flow-level test after a seam exists

Use flow-level tests only after the behavior crosses a testable seam around the shared template in `AbstractSaveOperation`. The current `perform(UserActivity)` method directly calls static or UI-bound collaborators:

| Collaborator | Why it needs a seam |
| --- | --- |
| `StageIDE.getActiveInstance()` | Static active-application lookup. |
| `DocumentFrame.showSaveFileDialog(...)` | Opens the real save dialog. |
| `Dialogs.showError(...)` | Opens the real error dialog. |
| `ProjectApplication.saveProjectTo(File)` and `exportProjectTo(File)` | Final methods, so simple subclass spying is not available. |

Add the direct operation tests first. In a later refactor, introduce a package-private context or runner that lets tests supply the current file, backup state, dialog result, wait cursor hooks, save callback, and error reporter without widening the public API.

Example scenario:

```text
Given a writable current project file
When Save runs
Then no save dialog is shown
And ProjectApplication.saveProjectTo(currentFile) is called
And the user activity is finished
```

For retry behavior:

```text
Given the first save attempt throws IOException
And the next dialog selection returns a writable destination
When Save runs
Then the error is reported
And the wait cursor is hidden after the failed attempt
And Save prompts again
And the user activity is finished after the successful retry
```

## Run the focused tests

Run the existing module test goal:

```bash
mvn -pl core/ide test
```

If a broader Maven command reaches `core/tweedle` and generated Tweedle parser classes are missing, initialize the grammar submodule:

```bash
git submodule update --init tweedle-lang
```
