package org.alice.ide.project;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.ide.ProjectDocument;
import org.alice.ide.project.codecs.ProjectDocumentCodec;
import org.alice.ide.project.codecs.ProjectSnapshotCodec;
import org.alice.ide.project.events.ProjectChangeOfInterestListener;
import org.alice.ide.projecturi.ProjectSnapshot;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ProjectSmallGapBehaviorTest {
  private static ProjectDocument createDocument() {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    NamedUserType programType = AstUtilities.createType("Program", JavaType.getInstance(SProgram.class));
    programType.fields.add(new UserField("myScene", sceneType));
    return new ProjectDocument(new Project(programType, Project.SceneCameraType.WindowCamera), new UserActivity());
  }

  private static InputStreamBinaryDecoder decoderFor(ProjectSnapshot snapshot) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(outputStream);
    ProjectSnapshotCodec.SINGLETON.encodeValue(encoder, snapshot);
    encoder.flush();
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(outputStream.toByteArray()));
  }

  @Test
  public void projectDocumentStateStoresAndReleasesDocumentTransactionlessly() {
    ProjectDocumentState state = ProjectDocumentState.getInstance();
    ProjectDocument previous = state.getValue();
    ProjectDocument document = createDocument();
    try {
      state.setValueTransactionlessly(document);
      assertSame(document, state.getValue());

      state.setValueTransactionlessly(null);
      assertNull(state.getValue());
    } finally {
      state.setValueTransactionlessly(previous);
    }
  }

  @Test
  public void projectChangeManagerNotifiesActiveListenersOnly() {
    AtomicInteger notifications = new AtomicInteger();
    ProjectChangeOfInterestListener first = notifications::incrementAndGet;
    ProjectChangeOfInterestListener second = () -> notifications.addAndGet(10);

    ProjectChangeOfInterestManager.SINGLETON.addProjectChangeOfInterestListener(first);
    ProjectChangeOfInterestManager.SINGLETON.addProjectChangeOfInterestListener(second);
    try {
      ProjectChangeOfInterestManager.SINGLETON.fireProjectChangeOfInterestListeners();
      assertEquals(11, notifications.get());
      ProjectChangeOfInterestManager.SINGLETON.removeProjectChangeOfInterestListener(second);
      ProjectChangeOfInterestManager.SINGLETON.fireProjectChangeOfInterestListeners();
      assertEquals(12, notifications.get());
    } finally {
      ProjectChangeOfInterestManager.SINGLETON.removeProjectChangeOfInterestListener(first);
      ProjectChangeOfInterestManager.SINGLETON.removeProjectChangeOfInterestListener(second);
    }
  }

  @Test
  public void projectSnapshotCodecTreatsSnapshotWithoutUriLikeMissingValue() throws Exception {
    ProjectSnapshot snapshot = new ProjectSnapshot((URI) null);

    assertNull(ProjectSnapshotCodec.SINGLETON.decodeValue(decoderFor(snapshot)));
  }

  @Test
  public void projectSnapshotCodecRoundTripsUriOnlySnapshot() throws Exception {
    ProjectSnapshot snapshot = new ProjectSnapshot(URI.create("file:///virtual/test.a3p"));
    ProjectSnapshot decoded = ProjectSnapshotCodec.SINGLETON.decodeValue(decoderFor(snapshot));

    assertNotNull(decoded);
    assertEquals(snapshot.getUri(), decoded.getUri());
    assertTrue(decoded.hasUri());
  }

  @Test
  public void projectDocumentCodecAppendsCurrentDocumentRepresentation() {
    ProjectDocument document = createDocument();
    StringBuilder sb = new StringBuilder("doc=");

    ProjectDocumentCodec.SINGLETON.appendRepresentation(sb, document);

    assertEquals("doc=" + document, sb.toString());
  }
}
