package org.alice.ide.croquet.edits.ast;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.ide.croquet.codecs.NodeCodec;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class ParameterEditCoverageTest {
  private static final class RoundTripParameterEdit extends ParameterEdit {
    private RoundTripParameterEdit(UserMethod method, UserParameter parameter) {
      super(null, method, parameter);
    }

    private RoundTripParameterEdit(BinaryDecoder binaryDecoder) {
      super(binaryDecoder, null);
    }

    @Override
    protected void doOrRedoInternal(boolean isDo) {
    }

    @Override
    protected void undoInternal() {
    }

    @Override
    protected void appendDescription(StringBuilder rv, DescriptionStyle descriptionStyle) {
      rv.append("round-trip");
    }
  }

  @Test
  public void encodeDecode_roundTrip_preservesCodeAndParameter() {
    UserParameter parameter = new UserParameter("value", String.class);
    UserMethod method = new UserMethod("sample", Object.class, new UserParameter[]{parameter}, new BlockStatement());
    RoundTripParameterEdit edit = new RoundTripParameterEdit(method, parameter);
    NodeCodec.addNodeToGlobalMap(method);
    NodeCodec.addNodeToGlobalMap(parameter);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      edit.encode(encoder);
      encoder.flush();
      RoundTripParameterEdit decoded = new RoundTripParameterEdit(new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray())));
      assertSame(method, decoded.getCode());
      assertSame(parameter, decoded.getParameter());
    } finally {
      NodeCodec.removeNodeFromGlobalMap(parameter);
      NodeCodec.removeNodeFromGlobalMap(method);
    }
  }

  @Test
  public void terseDescription_comesFromSubclassAppendDescription() {
    UserParameter parameter = new UserParameter("value", String.class);
    UserMethod method = new UserMethod("sample", Object.class, new UserParameter[]{parameter}, new BlockStatement());
    assertEquals("round-trip", new RoundTripParameterEdit(method, parameter).getTerseDescription());
  }

  @Test
  public void canUndoAndRedo_areAlwaysTrue() {
    UserParameter parameter = new UserParameter("value", String.class);
    UserMethod method = new UserMethod("sample", Object.class, new UserParameter[]{parameter}, new BlockStatement());
    RoundTripParameterEdit edit = new RoundTripParameterEdit(method, parameter);
    assertTrue(edit.canUndo());
    assertTrue(edit.canRedo());
  }
}
