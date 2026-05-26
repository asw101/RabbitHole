package org.alice.ide;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ProjectDocumentFrameHelperTest {
  @Test
  public void constructorThrowsAssertionError() throws Exception {
    java.lang.reflect.Constructor<ProjectDocumentFrameHelper> constructor = ProjectDocumentFrameHelper.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail("Expected AssertionError");
    } catch (java.lang.reflect.InvocationTargetException exception) {
      assertTrue(exception.getCause() instanceof AssertionError);
    }
  }

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
  public void canEnableRenderingRequiresPositiveDisableReasonCount() {
    assertFalse(ProjectDocumentFrameHelper.canEnableRendering(-1));
    assertFalse(ProjectDocumentFrameHelper.canEnableRendering(0));
    assertTrue(ProjectDocumentFrameHelper.canEnableRendering(1));
  }
}
