package org.alice.ide.member;

import org.junit.Test;

import static org.junit.Assert.*;

public class MethodsSubCompositeTest {

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(MethodsSubComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(java.lang.reflect.Modifier.isPublic(MethodsSubComposite.class.getModifiers()));
  }

  @Test
  public void class_hasMethods() throws Exception {
    assertNotNull(MethodsSubComposite.class.getDeclaredMethod("getMethods"));
  }

  @Test
  public void class_hasUpdateTabTitle() throws Exception {
    assertNotNull(MethodsSubComposite.class.getDeclaredMethod("updateTabTitle"));
  }

  @Test
  public void class_hasIsShowingDesired() throws Exception {
    assertNotNull(MethodsSubComposite.class.getDeclaredMethod("isShowingDesired"));
  }

  @Test
  public void getMethodsReturnType_isList() throws Exception {
    var m = MethodsSubComposite.class.getDeclaredMethod("getMethods");
    assertTrue(java.util.List.class.isAssignableFrom(m.getReturnType()));
  }
}
