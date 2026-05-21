package org.alice.stageide.perspectives;

import org.alice.ide.ProjectDocumentFrame;
import org.junit.Test;
import org.lgna.croquet.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.*;

public class ToolBarUtilitiesTest {

  private Constructor<ToolBarUtilities> getConstructor() throws Exception {
    return ToolBarUtilities.class.getDeclaredConstructor();
  }

  private Method getMethod(String name) throws Exception {
    return ToolBarUtilities.class.getDeclaredMethod(name, ProjectDocumentFrame.class, List.class);
  }

  @Test
  public void class_isLoadable() {
    assertNotNull(ToolBarUtilities.class);
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(ToolBarUtilities.class.getModifiers()));
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(ToolBarUtilities.class.getModifiers()));
  }

  @Test
  public void constructor_noArgs_exists() throws Exception {
    assertNotNull(getConstructor());
  }

  @Test
  public void constructor_noArgs_isPrivate() throws Exception {
    assertTrue(Modifier.isPrivate(getConstructor().getModifiers()));
  }

  @Test
  public void constructor_noArgs_hasNoParameters() throws Exception {
    assertEquals(0, getConstructor().getParameterTypes().length);
  }

  @Test
  public void constructors_onlySinglePrivateConstructor_exists() {
    assertEquals(1, ToolBarUtilities.class.getDeclaredConstructors().length);
  }

  @Test
  public void constructor_reflectionInvocation_throwsAssertionError() throws Exception {
    Constructor<ToolBarUtilities> constructor = getConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected AssertionError");
    } catch (InvocationTargetException exception) {
      assertTrue(exception.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void appendDocumentSubElements_signature_matchesExpectedParameters() throws Exception {
    Method method = getMethod("appendDocumentSubElements");
    assertArrayEquals(new Class<?>[] {ProjectDocumentFrame.class, List.class}, method.getParameterTypes());
  }

  @Test
  public void appendUndoRedoSubElements_signature_matchesExpectedParameters() throws Exception {
    Method method = getMethod("appendUndoRedoSubElements");
    assertArrayEquals(new Class<?>[] {ProjectDocumentFrame.class, List.class}, method.getParameterTypes());
  }

  @Test
  public void appendRunSubElements_signature_matchesExpectedParameters() throws Exception {
    Method method = getMethod("appendRunSubElements");
    assertArrayEquals(new Class<?>[] {ProjectDocumentFrame.class, List.class}, method.getParameterTypes());
  }

  @Test
  public void utilityMethods_declaredOnClass_arePublicAndStatic() throws Exception {
    assertTrue(Modifier.isPublic(getMethod("appendDocumentSubElements").getModifiers()));
    assertTrue(Modifier.isStatic(getMethod("appendDocumentSubElements").getModifiers()));
    assertTrue(Modifier.isPublic(getMethod("appendUndoRedoSubElements").getModifiers()));
    assertTrue(Modifier.isStatic(getMethod("appendUndoRedoSubElements").getModifiers()));
    assertTrue(Modifier.isPublic(getMethod("appendRunSubElements").getModifiers()));
    assertTrue(Modifier.isStatic(getMethod("appendRunSubElements").getModifiers()));
  }

  @Test
  public void utilityMethods_declaredOnClass_returnVoid() throws Exception {
    assertEquals(void.class, getMethod("appendDocumentSubElements").getReturnType());
    assertEquals(void.class, getMethod("appendUndoRedoSubElements").getReturnType());
    assertEquals(void.class, getMethod("appendRunSubElements").getReturnType());
  }

  @Test
  public void declaredFields_utilityClass_hasNoFields() {
    assertEquals(0, ToolBarUtilities.class.getDeclaredFields().length);
  }

  @Test
  public void declaredMethods_utilityClass_hasThreeHelpers() {
    assertEquals(3, ToolBarUtilities.class.getDeclaredMethods().length);
  }

  @Test
  public void appendDocumentSubElements_secondParameter_erasesToListOfElement() throws Exception {
    Method method = getMethod("appendDocumentSubElements");
    assertEquals(List.class, method.getParameterTypes()[1]);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void className_utilityClass_matchesExpectedName() {
    assertEquals("ToolBarUtilities", ToolBarUtilities.class.getSimpleName());
  }
}
