package org.alice.ide.croquet.codecs.typeeditor;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.ide.declarationseditor.CodeComposite;
import org.alice.ide.declarationseditor.DeclarationComposite;
import org.alice.ide.declarationseditor.TypeComposite;
import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertSame;

public class DeclarationCompositeCodecProjectContextTest extends ProjectContextTestCase {
  @Test
  public void decodeUsesLoadedProjectMethodDeclarations() {
    DeclarationComposite<?, ?> composite = CodeComposite.getInstance(fixture.sceneProcedure);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    DeclarationCompositeCodec.SINGLETON.encodeValue(encoder, composite);
    encoder.flush();

    DeclarationComposite<?, ?> decoded = DeclarationCompositeCodec.SINGLETON.decodeValue(
        new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray())));
    assertSame(composite, decoded);
  }

  @Test
  public void decodeUsesLoadedProjectTypeDeclarations() {
    DeclarationComposite<?, ?> composite = TypeComposite.getInstance(fixture.sceneType);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    DeclarationCompositeCodec.SINGLETON.encodeValue(encoder, composite);
    encoder.flush();

    DeclarationComposite<?, ?> decoded = DeclarationCompositeCodec.SINGLETON.decodeValue(
        new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray())));
    assertSame(composite, decoded);
  }
}
