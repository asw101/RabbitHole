package org.alice.ide.project.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.ide.projecturi.ProjectSnapshot;
import org.junit.Test;

import javax.swing.ImageIcon;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;

import static org.junit.Assert.*;

public class ProjectSnapshotCodecTest {
  private InputStreamBinaryDecoder decoderFor(ProjectSnapshot snapshot) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(outputStream);
    ProjectSnapshotCodec.SINGLETON.encodeValue(encoder, snapshot);
    encoder.flush();
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(outputStream.toByteArray()));
  }

  @Test
  public void getValueClassReturnsProjectSnapshotClass() {
    assertEquals(ProjectSnapshot.class, ProjectSnapshotCodec.SINGLETON.getValueClass());
  }

  @Test
  public void roundTripWithUriPreservesUriAndHasUriFlag() throws Exception {
    ProjectSnapshot snapshot = new ProjectSnapshot(URI.create("file:///virtual/example.a3p"), "Example", new ImageIcon());
    ProjectSnapshot decoded = ProjectSnapshotCodec.SINGLETON.decodeValue(decoderFor(snapshot));

    assertNotNull(decoded);
    assertEquals(snapshot.getUri(), decoded.getUri());
    assertTrue(decoded.hasUri());
  }

  @Test
  public void roundTripWithNullValueProducesNull() throws Exception {
    assertNull(ProjectSnapshotCodec.SINGLETON.decodeValue(decoderFor(null)));
  }

  @Test
  public void appendRepresentationUsesProjectSnapshotToStringBehavior() {
    StringBuilder sb = new StringBuilder();
    ProjectSnapshotCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }
}
