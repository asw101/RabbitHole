package org.alice.ide.type;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import static org.junit.Assert.*;

public class ExtendsTypeKeyTest {

  private static final class OtherExtendsTypeKey extends AbstractExtendsTypeKey {
    private OtherExtendsTypeKey(AbstractType<?, ?, ?> superType) {
      super(superType);
    }
  }

  @Test
  public void constructor_storesSuperType() {
    AbstractType<?, ?, ?> superType = JavaType.getInstance(String.class);
    ExtendsTypeKey key = new ExtendsTypeKey(superType);

    assertSame(superType, key.getSuperType());
  }

  @Test
  public void createType_returnsNamedUserType() {
    NamedUserType type = new ExtendsTypeKey(JavaType.getInstance(Object.class)).createType();

    assertNotNull(type);
  }

  @Test
  public void equals_sameSuperType_returnsTrue() {
    AbstractType<?, ?, ?> superType = JavaType.getInstance(String.class);

    assertEquals(new ExtendsTypeKey(superType), new ExtendsTypeKey(superType));
  }

  @Test
  public void equals_differentSuperType_returnsFalse() {
    assertNotEquals(new ExtendsTypeKey(JavaType.getInstance(String.class)), new ExtendsTypeKey(JavaType.getInstance(Object.class)));
  }

  @Test
  public void equals_differentSubclass_returnsFalse() {
    AbstractType<?, ?, ?> superType = JavaType.getInstance(String.class);

    assertNotEquals(new ExtendsTypeKey(superType), new OtherExtendsTypeKey(superType));
  }

  @Test
  public void equals_null_returnsFalse() {
    assertNotEquals(new ExtendsTypeKey(JavaType.getInstance(String.class)), null);
  }

  @Test
  public void hashCode_usesSuperTypeHashCode() {
    AbstractType<?, ?, ?> superType = JavaType.getInstance(String.class);
    ExtendsTypeKey key = new ExtendsTypeKey(superType);

    assertEquals((37 * 17) + superType.hashCode(), key.hashCode());
  }
}
