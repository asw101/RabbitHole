package org.alice.ide.croquet.edits.ast;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.ide.croquet.codecs.NodeCodec;
import org.junit.Test;
import org.lgna.project.ast.ArrayInstanceCreation;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.StringLiteral;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class AddExpressionEditCoverageTest {
  @Test
  public void encodeDecode_roundTrip_preservesExpressionButDropsListProperty() throws Exception {
    Expression expression = new StringLiteral("hello");
    AddExpressionEdit edit = new AddExpressionEdit(null, new ArrayInstanceCreation(Object[].class, new Integer[]{0}).expressions, expression);
    NodeCodec.addNodeToGlobalMap(expression);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      edit.encode(encoder);
      encoder.flush();
      AddExpressionEdit decoded = new AddExpressionEdit(new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray())), null);
      Field expressionField = AddExpressionEdit.class.getDeclaredField("expression");
      expressionField.setAccessible(true);
      Field listField = AddExpressionEdit.class.getDeclaredField("expressionListProperty");
      listField.setAccessible(true);
      assertSame(expression, expressionField.get(decoded));
      assertNull(listField.get(decoded));
    } finally {
      NodeCodec.removeNodeFromGlobalMap(expression);
    }
  }

  @Test
  public void terseDescription_mentionsAddedExpression() {
    AddExpressionEdit edit = new AddExpressionEdit(null, new ArrayInstanceCreation(Object[].class, new Integer[]{0}).expressions, new IntegerLiteral(5));
    assertTrue(edit.getTerseDescription().contains("add:"));
  }

  @Test
  public void redoPresentation_includesRedoPrefix() {
    AddExpressionEdit edit = new AddExpressionEdit(null, new ArrayInstanceCreation(Object[].class, new Integer[]{0}).expressions, new IntegerLiteral(5));
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }
}
