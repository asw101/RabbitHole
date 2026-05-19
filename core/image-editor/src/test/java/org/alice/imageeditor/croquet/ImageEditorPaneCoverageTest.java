package org.alice.imageeditor.croquet;

import org.alice.imageeditor.croquet.views.ImageEditorPane;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ImageEditorPaneCoverageTest {

  private static TestSupport.HeadlessImageEditorFrame sharedFrame;
  private static TestSupport.HeadlessImageEditorPane sharedPane;

  @BeforeClass
  public static void setUpClass() throws Exception {
    TestSupport.onEdt(() -> {
      sharedFrame = new TestSupport.HeadlessImageEditorFrame();
      sharedPane = new TestSupport.HeadlessImageEditorPane(sharedFrame);
    });
  }

  @Test
  public void headlessConstruction_succeeds() {
    assertNotNull(sharedPane);
  }

  @Test
  public void headlessPane_getComposite_returnsFrame() {
    assertSame(sharedFrame, sharedPane.getComposite());
  }

  @Test
  public void setDefaultButtonToSave_exists() throws NoSuchMethodException {
    Method m = ImageEditorPane.class.getDeclaredMethod("setDefaultButtonToSave");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void setDefaultButtonToCrop_exists() throws NoSuchMethodException {
    Method m = ImageEditorPane.class.getDeclaredMethod("setDefaultButtonToCrop");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void render_exists() throws NoSuchMethodException {
    Method m = ImageEditorPane.class.getDeclaredMethod("render");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(java.awt.Image.class, m.getReturnType());
  }

  @Test
  public void handleCompositePreActivation_exists() throws NoSuchMethodException {
    Method m = ImageEditorPane.class.getDeclaredMethod("handleCompositePreActivation");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void handleCompositePostDeactivation_exists() throws NoSuchMethodException {
    Method m = ImageEditorPane.class.getDeclaredMethod("handleCompositePostDeactivation");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void constructorAcceptsImageEditorFrame() throws NoSuchMethodException {
    ImageEditorPane.class.getDeclaredConstructor(ImageEditorFrame.class);
  }

  @Test
  public void publicMethodSurface_containsExpected() {
    Set<String> expected = Set.of(
        "render", "setDefaultButtonToSave", "setDefaultButtonToCrop",
        "handleCompositePreActivation", "handleCompositePostDeactivation",
        "getComposite"
    );
    Set<String> actual = Arrays.stream(ImageEditorPane.class.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toCollection(TreeSet::new));
    for (String name : expected) {
      assertTrue("Missing public method: " + name, actual.contains(name));
    }
  }
}
