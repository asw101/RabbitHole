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
      if (m.getName().toLowerCase().contains("texture") || m.getName().toLowerCase().contains("bind")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have texture-related methods", found);
  }

  @Test
  public void hasStaticInnerClass() {
    Class<?>[] innerClasses = TextureBinding.class.getDeclaredClasses();
    assertTrue("Should have inner Data class", innerClasses.length > 0);
  }
}
