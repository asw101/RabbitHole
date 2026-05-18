package org.alice.ide.issue;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import edu.cmu.cs.dennisc.javax.swing.components.JBrowserHtmlView;
import edu.cmu.cs.dennisc.system.graphics.ConformanceTestResults;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.IntBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AliceIssueConfigurationTest {
  private static final Field SHARED_DETAILS_FIELD = getAccessibleField("sharedDetails");
  private static final Field SYNCHRONOUS_PICK_DETAILS_FIELD = getAccessibleField("synchronousPickDetails");

  private Object originalSharedDetails;
  private Object originalSynchronousPickDetails;

  @BeforeClass
  public static void forceHeadlessMode() {
    System.setProperty("java.awt.headless", "true");
  }

  @Before
  public void snapshotGraphicsState() throws Exception {
    originalSharedDetails = SHARED_DETAILS_FIELD.get(ConformanceTestResults.SINGLETON);
    originalSynchronousPickDetails = SYNCHRONOUS_PICK_DETAILS_FIELD.get(ConformanceTestResults.SINGLETON);
  }

  @After
  public void restoreSharedState() throws Exception {
    UserProgramRunningStateUtilities.setUserProgramRunning(false);
    SHARED_DETAILS_FIELD.set(ConformanceTestResults.SINGLETON, originalSharedDetails);
    SYNCHRONOUS_PICK_DETAILS_FIELD.set(ConformanceTestResults.SINGLETON, originalSynchronousPickDetails);
  }

  @Test
  public void gettersExposeExpectedApplicationMetadata() {
    AliceIssueConfiguration configuration = new AliceIssueConfiguration();

    assertEquals("Alice", configuration.getApplicationName());
    assertEquals("http://www.alice.org/get-alice/alice-3", configuration.getDownloadUrlSpec());
    assertEquals(configuration.getDownloadUrlSpec(), configuration.getDownloadUrlText());
    assertEquals("submit bug report", configuration.getSubmitActionName());
  }

  @Test
  public void createHeaderPaneUsesStandardPaneForNonGraphicsFailures() {
    AliceIssueConfiguration configuration = new AliceIssueConfiguration();

    JPanel panel = configuration.createHeaderPane(Thread.currentThread(), new RuntimeException("boom"), new RuntimeException("boom"));

    assertTrue(panel instanceof JStandardHeaderPane);
  }

  @Test
  public void createHeaderPaneUsesGraphicsPaneForGlFailures() {
    AliceIssueConfiguration configuration = new AliceIssueConfiguration();

    JPanel panel = configuration.createHeaderPane(Thread.currentThread(), new GLException("boom"), new GLException("boom"));

    assertTrue(panel instanceof JGraphicsHeaderPane);
  }

  @Test
  public void standardHeaderPaneMentionsBugReportWhenProgramIsNotRunning() {
    UserProgramRunningStateUtilities.setUserProgramRunning(false);
    AliceIssueConfiguration configuration = new AliceIssueConfiguration();

    JStandardHeaderPane panel = new JStandardHeaderPane(configuration);

    assertEquals(Color.DARK_GRAY, panel.getBackground());
    assertEquals(4, panel.getComponentCount());
    assertNotNull(findLabelContaining(panel, "A bug has been found"));

    JBrowserHtmlView browserView = findBrowserView(panel);
    String text = browserView.getText();
    assertTrue(text.contains(configuration.getDownloadUrlSpec()));
    assertTrue(text.contains("this bug has already been fixed"));
  }

  @Test
  public void standardHeaderPaneMentionsRunningProgramWhenProgramIsRunning() {
    UserProgramRunningStateUtilities.setUserProgramRunning(true);
    AliceIssueConfiguration configuration = new AliceIssueConfiguration();

    JStandardHeaderPane panel = new JStandardHeaderPane(configuration);

    assertNotNull(findLabelContaining(panel, "An exception has been caught during the running of your program."));
    assertNotNull(findLabelContaining(panel, configuration.getApplicationName()));
  }

  @Test
  public void graphicsHeaderPaneReportsUnknownGraphicsWhenNoConformanceDataExists() throws Exception {
    SHARED_DETAILS_FIELD.set(ConformanceTestResults.SINGLETON, null);
    SYNCHRONOUS_PICK_DETAILS_FIELD.set(ConformanceTestResults.SINGLETON, null);
    AliceIssueConfiguration configuration = new AliceIssueConfiguration();

    JGraphicsHeaderPane panel = new JGraphicsHeaderPane(configuration);

    assertEquals(Color.WHITE, panel.getBackground());
    assertEquals(4, panel.getComponentCount());

    String text = findBrowserView(panel).getText();
    assertTrue(text.contains("Alice has encountered a graphics problem"));
    assertTrue(text.contains("Graphics information: <strong>unknown"));
    assertTrue(text.contains("There is no information on clicking into the scene."));
    assertTrue(text.contains("Troubleshooting-Known-Issues"));
  }

  @Test
  public void graphicsHeaderPaneExplainsHardwareAcceleratedPickWhenAvailable() throws Exception {
    installGraphicsState("NVIDIA GeForce GTX", true, false, true);
    AliceIssueConfiguration configuration = new AliceIssueConfiguration();

    JGraphicsHeaderPane panel = new JGraphicsHeaderPane(configuration);

    String text = findBrowserView(panel).getText();
    assertTrue(text.contains("NVIDIA GeForce GTX"));
    assertTrue(text.contains("Clicking into the scene appears to be functioning correctly in hardware."));
    assertTrue(text.contains("graphics+driver+GeForce"));
    assertFalse(text.contains("<strong>unknown"));
  }

  @Test
  public void graphicsHeaderPaneExplainsSoftwarePickAndDriverMismatch() throws Exception {
    installGraphicsState("Mesa Renderer", true, true, false);
    AliceIssueConfiguration configuration = new AliceIssueConfiguration();

    JGraphicsHeaderPane panel = new JGraphicsHeaderPane(configuration);

    String text = findBrowserView(panel).getText();
    assertTrue(text.contains("Mesa Renderer"));
    assertTrue(text.contains("Clicking into the scene appears to be functioning correctly in software (updating your video drivers might help)(video card reports hardware support but fails)."));
  }

  @Test
  public void graphicsHeaderPaneExplainsSuboptimalPickWhenPickTestFails() throws Exception {
    installGraphicsState("Fallback Renderer", false, false, false);
    AliceIssueConfiguration configuration = new AliceIssueConfiguration();

    JGraphicsHeaderPane panel = new JGraphicsHeaderPane(configuration);

    String text = findBrowserView(panel).getText();
    assertTrue(text.contains("Fallback Renderer"));
    assertTrue(text.contains("Clicking into the scene appears to be suboptimal (updating your video drivers might help)."));
  }

  private static Field getAccessibleField(String name) {
    try {
      Field field = ConformanceTestResults.class.getDeclaredField(name);
      field.setAccessible(true);
      return field;
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static JLabel findLabelContaining(JPanel panel, String fragment) {
    for (Component component : panel.getComponents()) {
      if (component instanceof JLabel) {
        JLabel label = (JLabel) component;
        if ((label.getText() != null) && label.getText().contains(fragment)) {
          return label;
        }
      }
    }
    fail("Did not find JLabel containing: " + fragment);
    return null;
  }

  private static JBrowserHtmlView findBrowserView(JPanel panel) {
    for (Component component : panel.getComponents()) {
      if (component instanceof JBrowserHtmlView) {
        return (JBrowserHtmlView) component;
      }
    }
    fail("Did not find JBrowserHtmlView");
    return null;
  }

  private static void installGraphicsState(String renderer, boolean successfulPick, boolean reportingHardwareAcceleration, boolean actualHardwareAcceleration) throws Exception {
    GL2 gl = createGl2Proxy(renderer, successfulPick);
    SHARED_DETAILS_FIELD.set(ConformanceTestResults.SINGLETON, createSharedDetails(gl));
    SYNCHRONOUS_PICK_DETAILS_FIELD.set(ConformanceTestResults.SINGLETON, createSynchronousPickDetails(gl, reportingHardwareAcceleration, actualHardwareAcceleration));
  }

  private static Object createSharedDetails(GL gl) throws Exception {
    Constructor<?> constructor = ConformanceTestResults.SharedDetails.class.getDeclaredConstructor(GL.class);
    constructor.setAccessible(true);
    return constructor.newInstance(gl);
  }

  private static Object createSynchronousPickDetails(GL2 gl, boolean reportingHardwareAcceleration, boolean actualHardwareAcceleration) throws Exception {
    Constructor<?> constructor = ConformanceTestResults.SynchronousPickDetails.class.getDeclaredConstructor(GL2.class, boolean.class, boolean.class);
    constructor.setAccessible(true);
    return constructor.newInstance(gl, reportingHardwareAcceleration, actualHardwareAcceleration);
  }

  private static GL2 createGl2Proxy(final String renderer, final boolean successfulPick) {
    final IntBuffer[] selectionBuffer = new IntBuffer[1];
    InvocationHandler handler = new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) {
        String name = method.getName();
        if ("glGetString".equals(name)) {
          int token = (Integer) args[0];
          if (token == GL.GL_VERSION) {
            return "4.6";
          } else if (token == GL.GL_VENDOR) {
            return "Test Vendor";
          } else if (token == GL.GL_RENDERER) {
            return renderer;
          } else if (token == GL.GL_EXTENSIONS) {
            return "";
          }
          return null;
        } else if ("glSelectBuffer".equals(name)) {
          selectionBuffer[0] = (IntBuffer) args[1];
          return null;
        } else if ("glRenderMode".equals(name)) {
          int mode = (Integer) args[0];
          if (mode == GL.GL_SELECT) {
            return 0;
          } else if (mode == GL.GL_RENDER) {
            if (successfulPick && (selectionBuffer[0] != null)) {
              selectionBuffer[0].put(0, 1);
              selectionBuffer[0].put(1, 1);
              selectionBuffer[0].put(2, 2);
              selectionBuffer[0].put(3, 11235);
              return 1;
            }
            return 0;
          }
        }

        Class<?> returnType = method.getReturnType();
        if (returnType == Boolean.TYPE) {
          return false;
        } else if (returnType == Integer.TYPE) {
          return 0;
        } else if (returnType == Long.TYPE) {
          return 0L;
        } else if (returnType == Float.TYPE) {
          return 0f;
        } else if (returnType == Double.TYPE) {
          return 0d;
        }
        return null;
      }
    };
    return (GL2) Proxy.newProxyInstance(AliceIssueConfigurationTest.class.getClassLoader(), new Class[] {GL2.class}, handler);
  }
}
