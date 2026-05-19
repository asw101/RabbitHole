package org.alice.imageeditor.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SaveOperationTest {

  @Test
  public void construction_headlessFrame_succeeds() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      SaveOperation op = new SaveOperation(frame);
      assertNotNull(op);
    });
  }

  @Test
  public void getOwner_returnsConstructorArgument() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      SaveOperation op = new SaveOperation(frame);
      assertSame(frame, op.getOwner());
    });
  }

  @Test
  public void classExtendsSingleThreadIteratingOperation() {
    assertEquals(
        "org.lgna.croquet.SingleThreadIteratingOperation",
        SaveOperation.class.getSuperclass().getName());
  }

  @Test
  public void classIsFinal() {
    assertTrue(Modifier.isFinal(SaveOperation.class.getModifiers()));
  }

  @Test
  public void constructorAcceptsImageEditorFrame() throws NoSuchMethodException {
    Constructor<?> ctor = SaveOperation.class.getDeclaredConstructor(ImageEditorFrame.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void getOwnerMethodExists() throws NoSuchMethodException {
    Method m = SaveOperation.class.getDeclaredMethod("getOwner");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(ImageEditorFrame.class, m.getReturnType());
  }

  @Test
  public void publicMethodSurface() {
    Set<String> expected = Set.of("getOwner");
    Set<String> actual = Arrays.stream(SaveOperation.class.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toCollection(TreeSet::new));
    for (String name : expected) {
      assertTrue("Missing public method: " + name, actual.contains(name));
    }
  }
}
