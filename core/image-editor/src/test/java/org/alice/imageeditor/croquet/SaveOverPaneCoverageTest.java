package org.alice.imageeditor.croquet;

import org.alice.imageeditor.croquet.views.SaveOverPane;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SaveOverPaneCoverageTest {

  private static TestSupport.HeadlessImageEditorFrame sharedFrame;
  private static SaveOperation sharedOp;
  private static SaveOverComposite sharedComposite;
  private static SaveOverPane sharedPane;

  @BeforeClass
  public static void setUpClass() throws Exception {
    TestSupport.onEdt(() -> {
      sharedFrame = new TestSupport.HeadlessImageEditorFrame();
      sharedOp = new SaveOperation(sharedFrame);
      sharedComposite = new SaveOverComposite(sharedOp);
      sharedPane = new SaveOverPane(sharedComposite);
    });
  }

  @Test
  public void construction_throughCompositeChain_succeeds() {
    assertNotNull(sharedPane);
  }

  @Test
  public void getComposite_returnsExpectedComposite() {
    assertSame(sharedComposite, sharedPane.getComposite());
  }

  @Test
  public void compositeOwner_isOriginalOperation() {
    assertSame(sharedOp, sharedComposite.getOwner());
  }

  @Test
  public void constructorAcceptsSaveOverComposite() throws NoSuchMethodException {
    SaveOverPane.class.getDeclaredConstructor(SaveOverComposite.class);
  }

  @Test
  public void handleCompositePreActivation_exists() throws NoSuchMethodException {
    Method m = SaveOverPane.class.getDeclaredMethod("handleCompositePreActivation");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void publicMethodSurface_containsExpected() {
    Set<String> expected = Set.of("handleCompositePreActivation", "getComposite");
    Set<String> actual = Arrays.stream(SaveOverPane.class.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toCollection(TreeSet::new));
    for (String name : expected) {
      assertTrue("Missing public method: " + name, actual.contains(name));
    }
  }

  @Test
  public void classExtendsMigPanel() {
    assertEquals("org.lgna.croquet.views.MigPanel", SaveOverPane.class.getSuperclass().getName());
  }
}
