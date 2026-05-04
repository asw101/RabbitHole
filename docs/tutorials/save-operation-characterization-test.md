# Tutorial: Add a Save Operation Characterization Test

This tutorial walks through adding the first direct characterization test for the Alice project Save operation.

## Contents

- [Goal](#goal)
- [1. Create the test class](#1-create-the-test-class)
- [2. Characterize missing-file prompt behavior](#2-characterize-missing-file-prompt-behavior)
- [3. Characterize writable-file behavior](#3-characterize-writable-file-behavior)
- [4. Characterize a non-reusable current file](#4-characterize-a-non-reusable-current-file)
- [5. Characterize the extension and toolbar behavior](#5-characterize-the-extension-and-toolbar-behavior)
- [6. Run the module tests](#6-run-the-module-tests)

## Goal

Protect the current Alice behavior: Save prompts when there is no writable current project file, and otherwise reuses the current file. This tutorial intentionally stays below `AbstractSaveOperation.perform(UserActivity)` because that flow needs a small seam before it can be tested without UI/static interception.

## 1. Create the test class

Create this file:

```text
core/ide/src/test/java/org/alice/ide/croquet/models/projecturi/SaveProjectOperationTest.java
```

Use the same package as the production operation:

```java
package org.alice.ide.croquet.models.projecturi;
```

Keeping the test in this package lets the test call protected operation methods without changing production visibility.

## 2. Characterize missing-file prompt behavior

Add a test for the no-current-file case:

```java
@Test
public void promptsWhenCurrentFileIsMissing() {
  assertTrue(SaveProjectOperation.getInstance().isPromptNecessary(null));
}
```

This protects the Save behavior used by new projects and projects without a writable URI.

## 3. Characterize writable-file behavior

Use JUnit 4 `TemporaryFolder` to create a real writable file:

```java
@Rule
public TemporaryFolder temporaryFolder = new TemporaryFolder();

@Test
public void doesNotPromptForWritableCurrentFile() throws Exception {
  File projectFile = temporaryFolder.newFile("RobotDance.a3p");

  assertFalse(SaveProjectOperation.getInstance().isPromptNecessary(projectFile));
}
```

This protects the fast path where Save can reuse the current project file without showing a dialog.

## 4. Characterize a non-reusable current file

Use a missing file under the temporary folder for a portable cannot-write case:

```java
@Test
public void promptsWhenCurrentFileCannotBeReused() {
  File missingProjectFile = new File(temporaryFolder.getRoot(), "MissingRobotDance.a3p");

  assertTrue(SaveProjectOperation.getInstance().isPromptNecessary(missingProjectFile));
}
```

This protects the exact Save rule:

```java
((file != null) && file.canWrite()) == false
```

## 5. Characterize the extension and toolbar behavior

Assert that Save uses the Alice project extension constant:

```java
@Test
public void usesProjectExtension() {
  assertEquals(IoUtilities.PROJECT_EXTENSION, SaveProjectOperation.getInstance().getExtension());
}
```

Also assert that Save clobbers toolbar text:

```java
@Test
public void clobbersToolbarText() {
  assertTrue(SaveProjectOperation.getInstance().isToolBarTextClobbered());
}
```

Use prompt and extension checks for Save As. For Export, assert `IoUtilities.EXPORT_EXTENSION` and toolbar text clobbering. Leave finish/cancel/retry/wait-cursor flow scenarios for a later seam around `AbstractSaveOperation.perform(UserActivity)`.

## 6. Run the module tests

Run the focused Maven test command from the repository root:

```bash
mvn -pl core/ide test
```

The test documents compatibility before refactoring. If a later refactor changes prompt routing or extension selection, this test fails at the operation layer instead of relying on archive-content tests to catch the regression.
