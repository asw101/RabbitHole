package org.alice.ide.croquet.edits.ast;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.Test;
import org.lgna.project.ast.Statement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class DeclareGalleryFieldEditCoverageTest {
  @Test
  public void extendsDeclareFieldEdit() {
    assertTrue(DeclareFieldEdit.class.isAssignableFrom(DeclareGalleryFieldEdit.class));
  }

  @Test
  public void hasSceneEditorField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(DeclareGalleryFieldEdit.class, "sceneEditor");
  }

  @Test
  public void hasStatementArrayFields() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(DeclareGalleryFieldEdit.class, "doStatements");
    ReflectionTestHelper.assertFieldIsPrivateFinal(DeclareGalleryFieldEdit.class, "undoStatements");
  }

  @Test
  public void decodeConstructor_isPublic() throws Exception {
    Constructor<?> constructor = DeclareGalleryFieldEdit.class.getDeclaredConstructor(
        Class.forName("edu.cmu.cs.dennisc.codec.BinaryDecoder"), Object.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }
}
