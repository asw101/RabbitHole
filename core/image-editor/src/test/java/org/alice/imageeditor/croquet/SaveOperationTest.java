package org.alice.imageeditor.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.BeforeClass;

import static org.junit.Assert.*;

public class SaveOperationTest {

  private static TestSupport.HeadlessImageEditorFrame sharedFrame;
  private static SaveOperation sharedOp;

  @BeforeClass
  public static void setUpClass() throws Exception {
    TestSupport.onEdt(() -> {
      sharedFrame = new TestSupport.HeadlessImageEditorFrame();
      sharedOp = new SaveOperation(sharedFrame);
    });
  }

  @Test
  public void construction_headlessFrame_succeeds() {
    assertNotNull(sharedOp);
  }

  @Test
  public void getOwner_returnsConstructorArgument() {
    assertSame(sharedFrame, sharedOp.getOwner());
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
