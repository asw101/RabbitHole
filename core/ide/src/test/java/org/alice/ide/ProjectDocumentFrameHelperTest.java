package org.alice.ide;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ProjectDocumentFrameHelperTest {
  @Test
  public void shouldRegisterRectangleCaptureOnlyForSecondaryWindows() {
    assertFalse(ProjectDocumentFrameHelper.shouldRegisterRectangleCapture(true));
    assertTrue(ProjectDocumentFrameHelper.shouldRegisterRectangleCapture(false));
  }

  @Test
  public void getFocusedCodeReturnsOnlyCodeDeclarations() {
    UserMethod method = new UserMethod("run", void.class, new UserParameter[0], new BlockStatement());

    assertSame(method, ProjectDocumentFrameHelper.getFocusedCode(method));
    assertNull(ProjectDocumentFrameHelper.getFocusedCode(new UserField()));
  }

  @Test
  public void canEnableRenderingRequiresDisableReason() {
    assertFalse(ProjectDocumentFrameHelper.canEnableRendering(0));
    assertTrue(ProjectDocumentFrameHelper.canEnableRendering(1));
  }
}
