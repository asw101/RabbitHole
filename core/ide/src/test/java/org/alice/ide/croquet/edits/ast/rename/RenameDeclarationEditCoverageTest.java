package org.alice.ide.croquet.edits.ast.rename;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.ide.croquet.codecs.NodeCodec;
import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserField;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class RenameDeclarationEditCoverageTest {
  @Test
  public void doUndoRedo_cyclesDeclarationName() {
    UserField field = new UserField("score", JavaType.getInstance(Integer.class), new StringLiteral("1"));
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, field, "score", "points");
    edit.doOrRedoInternal(true);
    assertEquals("points", field.name.getValue());
    edit.undoInternal();
    assertEquals("score", field.name.getValue());
    edit.doOrRedoInternal(false);
    assertEquals("points", field.name.getValue());
  }

  @Test
  public void encodeDecode_roundTrip_preservesDeclarationAndNames() throws Exception {
    UserField field = new UserField("score", JavaType.getInstance(Integer.class), new StringLiteral("1"));
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, field, "score", "points");
    NodeCodec.addNodeToGlobalMap(field);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      edit.encode(encoder);
      encoder.flush();
      RenameDeclarationEdit decoded = new RenameDeclarationEdit(
          new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray())), null);
      Field declarationField = RenameDeclarationEdit.class.getDeclaredField("declaration");
      declarationField.setAccessible(true);
      assertSame(field, declarationField.get(decoded));
      decoded.doOrRedoInternal(true);
      assertEquals("points", field.name.getValue());
    } finally {
      NodeCodec.removeNodeFromGlobalMap(field);
    }
  }

  @Test
  public void terseDescription_mentionsRenameArrow() {
    UserField field = new UserField("score", JavaType.getInstance(Integer.class), new StringLiteral("1"));
    RenameDeclarationEdit edit = new RenameDeclarationEdit(null, field, "score", "points");
    assertTrue(edit.getTerseDescription().contains("===>"));
  }
}
