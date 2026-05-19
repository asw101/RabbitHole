package org.alice.ide.croquet.models.declaration;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class GalleryResourceUtilitiesTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(GalleryResourceUtilities.class.getModifiers()));
  }
  @Test
  public void hasPrivateConstructor() {
    ReflectionTestHelper.assertAllConstructorsPrivate(GalleryResourceUtilities.class);
  }
  @Test
  public void privateConstructorThrowsAssertionError() throws Exception {
    ReflectionTestHelper.assertPrivateConstructorThrowsAssertionError(GalleryResourceUtilities.class);
  }
  @Test
  public void allPublicMethodsAreStatic() {
    ReflectionTestHelper.assertAllPublicMethodsStatic(GalleryResourceUtilities.class);
  }
}
