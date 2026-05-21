package org.alice.ide.type;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for type extension key classes.
 */
public class ExtendsTypeKeyStructureTest {

  // ---- AbstractExtendsTypeKey ----

  @Test
  public void abstractExtendsTypeKey_classIsAccessible() {
    assertNotNull(AbstractExtendsTypeKey.class);
  }

  @Test
  public void abstractExtendsTypeKey_isPublic() {
    assertTrue(Modifier.isPublic(AbstractExtendsTypeKey.class.getModifiers()));
  }

  @Test
  public void abstractExtendsTypeKey_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractExtendsTypeKey.class.getModifiers()));
  }

  // ---- ExtendsTypeKey ----

  @Test
  public void extendsTypeKey_classIsAccessible() {
    assertNotNull(ExtendsTypeKey.class);
  }

  @Test
  public void extendsTypeKey_isPublic() {
    assertTrue(Modifier.isPublic(ExtendsTypeKey.class.getModifiers()));
  }

  @Test
  public void extendsTypeKey_extendsAbstractBase() {
    assertTrue(AbstractExtendsTypeKey.class.isAssignableFrom(ExtendsTypeKey.class));
  }

  // ---- ExtendsTypeWithNamedType ----

  @Test
  public void extendsTypeWithNamedType_classIsAccessible() {
    assertNotNull(ExtendsTypeWithNamedType.class);
  }

  @Test
  public void extendsTypeWithNamedType_isPublic() {
    assertTrue(Modifier.isPublic(ExtendsTypeWithNamedType.class.getModifiers()));
  }

  @Test
  public void extendsTypeWithNamedType_extendsAbstractBase() {
    assertTrue(AbstractExtendsTypeKey.class.isAssignableFrom(ExtendsTypeWithNamedType.class));
  }

  // ---- ExtendsTypeWithSuperArgumentFieldKey ----

  @Test
  public void extendsTypeWithSuperArgumentFieldKey_classIsAccessible() {
    assertNotNull(ExtendsTypeWithSuperArgumentFieldKey.class);
  }

  @Test
  public void extendsTypeWithSuperArgumentFieldKey_isPublic() {
    assertTrue(Modifier.isPublic(ExtendsTypeWithSuperArgumentFieldKey.class.getModifiers()));
  }

  @Test
  public void extendsTypeWithSuperArgumentFieldKey_extendsAbstractBase() {
    assertTrue(AbstractExtendsTypeKey.class.isAssignableFrom(ExtendsTypeWithSuperArgumentFieldKey.class));
  }

  // ---- ExtendsTypeWithConstructorParameterTypeKey ----

  @Test
  public void extendsTypeWithConstructorParameterTypeKey_classIsAccessible() {
    assertNotNull(ExtendsTypeWithConstructorParameterTypeKey.class);
  }

  @Test
  public void extendsTypeWithConstructorParameterTypeKey_isPublic() {
    assertTrue(Modifier.isPublic(ExtendsTypeWithConstructorParameterTypeKey.class.getModifiers()));
  }

  @Test
  public void extendsTypeWithConstructorParameterTypeKey_extendsAbstractBase() {
    assertTrue(AbstractExtendsTypeKey.class.isAssignableFrom(ExtendsTypeWithConstructorParameterTypeKey.class));
  }

  // ---- TypeCache ----

  @Test
  public void typeCache_classIsAccessible() {
    assertNotNull(TypeCache.class);
  }

  @Test
  public void typeCache_isPublic() {
    assertTrue(Modifier.isPublic(TypeCache.class.getModifiers()));
  }

  // ---- All type key subclasses extend AbstractExtendsTypeKey ----

  @Test
  public void allTypeKeys_extendAbstractBase() {
    Class<?> base = AbstractExtendsTypeKey.class;
    assertTrue(base.isAssignableFrom(ExtendsTypeKey.class));
    assertTrue(base.isAssignableFrom(ExtendsTypeWithNamedType.class));
    assertTrue(base.isAssignableFrom(ExtendsTypeWithSuperArgumentFieldKey.class));
    assertTrue(base.isAssignableFrom(ExtendsTypeWithConstructorParameterTypeKey.class));
  }

  // ---- All type key classes are public ----

  @Test
  public void allTypeKeyClasses_arePublic() {
    Class<?>[] classes = {
      AbstractExtendsTypeKey.class, ExtendsTypeKey.class,
      ExtendsTypeWithNamedType.class, ExtendsTypeWithSuperArgumentFieldKey.class,
      ExtendsTypeWithConstructorParameterTypeKey.class, TypeCache.class
    };
    for (Class<?> cls : classes) {
      assertTrue(cls.getSimpleName() + " should be public",
        Modifier.isPublic(cls.getModifiers()));
    }
  }
}
