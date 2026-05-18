package org.alice.ide.issue;

import com.jogamp.opengl.GL;
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
import java.lang.reflect.Proxy;

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
    assertTrue(text.contains("functioning correctly"));
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
    assertTrue(text.contains("functioning correctly"));
    assertFalse(text.contains("<strong>unknown"));
  }

  @Test
  public void graphicsHeaderPaneExplainsSuboptimalPickWhenPickTestFails() throws Exception {
    installGraphicsState("Fallback Renderer", false, false, false);
    AliceIssueConfiguration configuration = new AliceIssueConfiguration();

    JGraphicsHeaderPane panel = new JGraphicsHeaderPane(configuration);

    String text = findBrowserView(panel).getText();
    assertTrue(text.contains("Fallback Renderer"));
    assertTrue(text.contains("appears to be suboptimal"));
    assertTrue(text.contains("video drivers might help"));
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
    SHARED_DETAILS_FIELD.set(ConformanceTestResults.SINGLETON, createSharedDetails(renderer));
    SYNCHRONOUS_PICK_DETAILS_FIELD.set(ConformanceTestResults.SINGLETON, createSynchronousPickDetails(successfulPick, reportingHardwareAcceleration, actualHardwareAcceleration));
  }

  // Uses GL Proxy to construct SharedDetails without a real OpenGL context.
  // The constructor only calls gl.glGetString() for version/vendor/renderer/extensions.
  private static Object createSharedDetails(String renderer) throws Exception {
    GL glProxy = (GL) Proxy.newProxyInstance(
        GL.class.getClassLoader(),
        new Class<?>[] { GL.class },
        (proxy, method, args) -> {
          if ("glGetString".equals(method.getName())) {
            int param = (int) args[0];
            if (param == GL.GL_VERSION) return "4.6";
            if (param == GL.GL_VENDOR) return "Test Vendor";
            if (param == GL.GL_RENDERER) return renderer;
            if (param == GL.GL_EXTENSIONS) return "";
          }
          return defaultReturnValue(method.getReturnType());
        });
    Constructor<?> ctor = ConformanceTestResults.SharedDetails.class.getDeclaredConstructor(GL.class);
    ctor.setAccessible(true);
    return ctor.newInstance(glProxy);
  }

  // Creates SynchronousPickDetails without a real OpenGL context by bypassing the
  // GL2-dependent constructor via ReflectionFactory (Java's serialization support API).
  // GL2 Proxy is infeasible here — GL2 has too many constants for the JVM's 64KB method limit.
  @SuppressWarnings("sunapi")
  private static Object createSynchronousPickDetails(boolean successfulPick, boolean reportingHardwareAcceleration, boolean actualHardwareAcceleration) throws Exception {
    sun.reflect.ReflectionFactory rf = sun.reflect.ReflectionFactory.getReflectionFactory();
    Constructor<?> ctor = rf.newConstructorForSerialization(
        ConformanceTestResults.SynchronousPickDetails.class,
        Object.class.getDeclaredConstructor());
    Object details = ctor.newInstance();
    setFieldValue(ConformanceTestResults.PickDetails.class, details, "isPickFunctioningCorrectly", successfulPick);
    setFieldValue(ConformanceTestResults.SynchronousPickDetails.class, details, "isReportingPickCanBeHardwareAccelerated", reportingHardwareAcceleration);
    setFieldValue(ConformanceTestResults.SynchronousPickDetails.class, details, "isPickActuallyHardwareAccelerated", actualHardwareAcceleration);
    return details;
  }

  private static void setFieldValue(Class<?> declaringClass, Object target, String name, Object value) throws Exception {
    Field field = declaringClass.getDeclaredField(name);
    field.setAccessible(true);
    field.set(target, value);
  }

  private static Object defaultReturnValue(Class<?> type) {
    if (type == void.class) return null;
    if (type == int.class) return 0;
    if (type == long.class) return 0L;
    if (type == boolean.class) return false;
    if (type == float.class) return 0.0f;
    if (type == double.class) return 0.0;
    if (type == byte.class) return (byte) 0;
    if (type == short.class) return (short) 0;
    if (type == char.class) return '\0';
    return null;
  }
}
