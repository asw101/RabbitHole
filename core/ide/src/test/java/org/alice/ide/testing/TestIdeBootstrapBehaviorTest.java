package org.alice.ide.testing;

import org.alice.ide.IDE;
import org.alice.ide.ProjectDocumentFrame;
import org.alice.ide.project.ProjectDocumentState;
import org.alice.stageide.StageIDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.Application;

import static org.junit.Assert.*;

public class TestIdeBootstrapBehaviorTest {
  @Before
  public void setUp() {
    TestIdeBootstrap.ensureInstalled();
  }

  @After
  public void tearDown() {
    TestIdeBootstrap.reset();
  }

  @Test
  public void ensureInstalled_registersActiveApplicationAndIde() {
    StageIDE ide = TestIdeBootstrap.ensureInstalled();

    assertSame(ide, Application.getActiveInstance());
    assertSame(ide, IDE.getActiveInstance());
    assertSame(ide, StageIDE.getActiveInstance());
  }

  @Test
  public void ensureInstalled_providesProjectDocumentFrameAndProject() {
    ProjectDocumentFrame documentFrame = TestIdeBootstrap.getDocumentFrame();

    assertNotNull(documentFrame);
    assertNotNull(documentFrame.getMetaDeclarationFauxState());
    assertNotNull(ProjectDocumentState.getInstance().getValue());
    assertNotNull(IDE.getActiveInstance().getProject());
    assertEquals("myScene", IDE.getActiveInstance().getProject().getProgramType().fields.get(0).getName());
  }

  @Test
  public void reset_restoresProjectDocumentAndFrame() {
    StageIDE ide = TestIdeBootstrap.ensureInstalled();
    ProjectDocumentState.getInstance().setValueTransactionlessly(null);
    TestIdeBootstrap.setActiveApplication(null);

    TestIdeBootstrap.reset();

    assertNotNull(ProjectDocumentState.getInstance().getValue());
    assertNotNull(IDE.getActiveInstance().getProject());
    assertNotNull(ide.getDocumentFrame().getFrame());
  }

  @Test
  public void frame_isConstructedUnderXvfb() {
    ProjectDocumentFrame documentFrame = TestIdeBootstrap.getDocumentFrame();

    assertNotNull(documentFrame.getFrame());
    assertTrue(documentFrame.getFrame().getAwtComponent().getWidth() >= 0);
  }
}
