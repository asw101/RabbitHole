package edu.cmu.cs.dennisc.render.gl;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class TextureBindingStructureTest {

  @Test
  public void classIsFinal() {
    assertTrue(Modifier.isFinal(TextureBinding.class.getModifiers()));
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(TextureBinding.class.getModifiers()));
  }

  @Test
  public void implementsForgettableBinding() {
    boolean implementsInterface = false;
    for (Class<?> iface : TextureBinding.class.getInterfaces()) {
      if (iface.getSimpleName().equals("ForgettableBinding")) {
        implementsInterface = true;
        break;
      }
    }
    assertTrue("TextureBinding should implement ForgettableBinding", implementsInterface);
  }

  @Test
  public void hasMethodsRelatedToTexture() {
    boolean found = false;
    for (Method m : TextureBinding.class.getDeclaredMethods()) {
      if (m.getName().contains("ensureUpToDate") || m.getName().contains("forget") || m.getName().contains("getData")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have texture-management methods", found);
  }

  @Test
  public void hasStaticInnerClass() {
    Class<?>[] innerClasses = TextureBinding.class.getDeclaredClasses();
    assertTrue("Should have inner Data class", innerClasses.length > 0);
  }
}
