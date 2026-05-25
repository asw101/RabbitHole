package org.alice.ide.ast.components;

import org.junit.Test;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;

import javax.swing.SwingUtilities;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class DeclarationNameLabelBehaviorTest {
  @Test
  public void declarationNameLabelUsesDeclarationNameImmediately() throws Exception {
    TestDeclarationNameLabel label = runOnEdt(() -> new TestDeclarationNameLabel(new UserField("bunny", String.class)));

    assertEquals("bunny", label.getText());
  }

  @Test
  public void blankNamesRenderAsBracketedUnsetText() throws Exception {
    UserField field = new UserField("", String.class);
    TestDeclarationNameLabel label = runOnEdt(() -> new TestDeclarationNameLabel(field));

    assertTrue(label.getText().startsWith("<"));
    assertTrue(label.getText().endsWith(">"));
    assertTrue(label.getText().length() > 2);
  }

  @Test
  public void displayableStateTracksFieldNameChangesUntilUndisplayable() throws Exception {
    UserField field = new UserField("bunny", String.class);
    TestDeclarationNameLabel label = runOnEdt(() -> new TestDeclarationNameLabel(field));

    runOnEdt(() -> {
      label.display();
      field.name.setValue("rabbit");
      return null;
    });
    assertEquals("rabbit", label.getText());

    runOnEdt(() -> {
      label.undisplay();
      field.name.setValue("hare");
      return null;
    });
    assertEquals("rabbit", label.getText());
  }

  @Test
  public void methodContainedByUserFieldListensToOwningFieldNameProperty() throws Exception {
    UserField field = new UserField("camera", String.class);
    TestDeclarationNameLabel label = runOnEdt(() -> new TestDeclarationNameLabel(field.getGetter()));

    runOnEdt(() -> {
      label.display();
      return null;
    });
    assertEquals("getCamera", label.getText());

    runOnEdt(() -> {
      field.name.setValue("sceneCamera");
      return null;
    });
    assertEquals("getSceneCamera", label.getText());
  }

  @Test
  public void localValidNameLabelFallsBackToGeneratedValidName() throws Exception {
    UserLocal local = new UserLocal(null, Object.class, false);
    new LocalDeclarationStatement(local, new NullLiteral());

    LocalValidNameLabel label = runOnEdt(() -> new LocalValidNameLabel(local));

    assertEquals("unusedName", label.getText());
    assertEquals("unusedName", local.getName());
  }

  private static <T> T runOnEdt(Callable<T> callable) throws Exception {
    AtomicReference<T> result = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> {
      try {
        result.set(callable.call());
      } catch (Throwable throwable) {
        failure.set(throwable);
      }
    });
    if (failure.get() != null) {
      if (failure.get() instanceof Exception exception) {
        throw exception;
      }
      throw new RuntimeException(failure.get());
    }
    return result.get();
  }

  private static final class TestDeclarationNameLabel extends DeclarationNameLabel {
    private TestDeclarationNameLabel(AbstractDeclaration declaration) {
      super(declaration);
    }

    private void display() {
      this.handleDisplayable();
    }

    private void undisplay() {
      this.handleUndisplayable();
    }
  }
}
