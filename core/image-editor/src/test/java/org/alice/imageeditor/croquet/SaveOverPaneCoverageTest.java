package org.alice.imageeditor.croquet;

import org.alice.imageeditor.croquet.views.SaveOverPane;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SaveOverPaneCoverageTest {

  @Test
  public void construction_throughCompositeChain_succeeds() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      SaveOperation op = new SaveOperation(frame);
      SaveOverComposite composite = new SaveOverComposite(op);
      SaveOverPane pane = new SaveOverPane(composite);
      assertNotNull(pane);
    });
  }

  @Test
  public void getComposite_returnsExpectedComposite() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      SaveOperation op = new SaveOperation(frame);
      SaveOverComposite composite = new SaveOverComposite(op);
      SaveOverPane pane = new SaveOverPane(composite);
      assertSame(composite, pane.getComposite());
    });
  }

  @Test
  public void compositeOwner_isOriginalOperation() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      SaveOperation op = new SaveOperation(frame);
      SaveOverComposite composite = new SaveOverComposite(op);
      assertSame(op, composite.getOwner());
    });
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
