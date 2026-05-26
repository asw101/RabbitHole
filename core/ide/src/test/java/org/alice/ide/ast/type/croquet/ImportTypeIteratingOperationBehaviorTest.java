package org.alice.ide.ast.type.croquet;

import org.alice.ide.ast.export.type.TypeSummary;
import org.alice.ide.ast.export.type.TypeSummaryDataSource;
import org.alice.ide.testing.ProjectContextFixture;
import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.croquet.FileDialogValueCreator;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.io.IoUtilities;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ImportTypeIteratingOperationBehaviorTest extends ProjectContextTestCase {
  @Test
  public void hasNextOnlyAllowsChooseFileAndImportSteps() throws Exception {
    ImportTypeIteratingOperation operation = new ImportTypeIteratingOperation(fixture.sceneType);

    assertTrue(invokeHasNext(operation, Collections.emptyList()));
    assertTrue(invokeHasNext(operation, Collections.singletonList(new UserActivity())));
    assertFalse(invokeHasNext(operation, java.util.Arrays.asList(new UserActivity(), new UserActivity())));
  }

  @Test
  public void secondStepReturnsNullWhenFileSelectionWasCancelled() throws Exception {
    ImportTypeIteratingOperation operation = new ImportTypeIteratingOperation(fixture.sceneType);

    assertNull(invokeGetNext(operation, Collections.singletonList(new UserActivity())));
  }

  @Test
  public void secondStepLaunchesImportWizardWhenArchiveContainsMatchingType() throws Exception {
    ProjectContextFixture donorFixture = ProjectContextFixture.create();
    File typeFile = createTypeArchive("direct-match", donorFixture.sceneType);
    ImportTypeIteratingOperation operation = new ImportTypeIteratingOperation(fixture.sceneType);

    Triggerable triggerable = invokeGetNext(operation, Collections.singletonList(activityWithProducedValue(typeFile)));

    assertNotNull(triggerable);
    assertFalse(triggerable instanceof FileDialogValueCreator);
  }

  @Test
  public void getNextReturnsNullOnceIterationIsAlreadyComplete() throws Exception {
    ImportTypeIteratingOperation operation = new ImportTypeIteratingOperation(fixture.sceneType);

    assertNull(invokeGetNext(operation, java.util.Arrays.asList(new UserActivity(), new UserActivity())));
  }

  private static boolean invokeHasNext(ImportTypeIteratingOperation operation, List<UserActivity> finishedSteps) throws Exception {
    Method method = ImportTypeIteratingOperation.class.getDeclaredMethod("hasNext", List.class);
    method.setAccessible(true);
    return (Boolean) method.invoke(operation, finishedSteps);
  }

  private static Triggerable invokeGetNext(ImportTypeIteratingOperation operation, List<UserActivity> finishedSteps) throws Exception {
    Method method = ImportTypeIteratingOperation.class.getDeclaredMethod("getNext", List.class);
    method.setAccessible(true);
    return (Triggerable) method.invoke(operation, finishedSteps);
  }

  private static UserActivity activityWithProducedValue(Object value) {
    UserActivity activity = new UserActivity();
    activity.setProducedValue(value);
    return activity;
  }

  private static File createTypeArchive(String name, org.lgna.project.ast.NamedUserType type) throws Exception {
    Path directory = Path.of("target", "test-artifacts", "import-type-iterating-operation");
    Files.createDirectories(directory);
    Path typeFile = directory.resolve(name + "." + IoUtilities.TYPE_EXTENSION);
    IoUtilities.writeType(
        typeFile.toFile(),
        type,
        new TypeSummaryDataSource(new TypeSummary(type)));
    return typeFile.toFile();
  }
}
