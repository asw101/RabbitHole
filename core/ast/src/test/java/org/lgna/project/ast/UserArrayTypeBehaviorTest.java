package org.lgna.project.ast;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class UserArrayTypeBehaviorTest {
  private static final class TypeFixture {
    private final NamedUserType parentType;
    private final NamedUserType childType;

    private TypeFixture() {
      parentType = new NamedUserType(
          "ParentType",
          null,
          Object.class,
          new NamedUserConstructor[] {new NamedUserConstructor(new UserParameter[0], new ConstructorBlockStatement())},
          new UserMethod[0],
          new UserField[0]);
      childType = new NamedUserType(
          "ChildType",
          null,
          parentType,
          new NamedUserConstructor[] {new NamedUserConstructor(new UserParameter[0], new ConstructorBlockStatement())},
          new UserMethod[0],
          new UserField[0]);
    }
  }

  @Test
  public void userArrayTypeCachesInstancesAndTracksDimensions() {
    TypeFixture fixture = new TypeFixture();
    UserArrayType first = UserArrayType.getInstance(fixture.childType, 2);
    UserArrayType second = UserArrayType.getInstance(fixture.childType, 2);

    assertSame(first, second);
    assertSame(fixture.childType, first.getLeafType());
    assertEquals(2, first.getDimensionCount());
    assertEquals("ChildType[][]", first.getName());
    assertSame(UserArrayType.getInstance(fixture.childType, 1), first.getComponentType());
    assertSame(UserArrayType.getInstance(fixture.childType, 3), first.getArrayType());
  }

  @Test
  public void userArrayTypeFollowsLeafSuperTypesForUserAndJavaParents() {
    TypeFixture fixture = new TypeFixture();
    UserArrayType childArray = UserArrayType.getInstance(fixture.childType, 1);
    UserArrayType parentArray = UserArrayType.getInstance(fixture.parentType, 1);

    assertSame(parentArray, childArray.getSuperType());
    assertSame(JavaType.getInstance(Object[].class), parentArray.getSuperType());
  }

  @Test
  public void userArrayTypeAssignabilityAndFlagsFollowLeafType() {
    TypeFixture fixture = new TypeFixture();
    UserArrayType parentArray = UserArrayType.getInstance(fixture.parentType, 2);
    UserArrayType childArray = UserArrayType.getInstance(fixture.childType, 2);

    assertTrue(parentArray.isAssignableFrom(childArray));
    assertFalse(childArray.isAssignableFrom(parentArray));
    assertEquals(fixture.childType.getPackage(), childArray.getPackage());
    assertEquals(fixture.childType.getAccessLevel(), childArray.getAccessLevel());
    assertEquals(fixture.childType.isInterface(), childArray.isInterface());
    assertEquals(fixture.childType.isAbstract(), childArray.isAbstract());
    assertEquals(fixture.childType.isFinal(), childArray.isFinal());
    assertEquals(fixture.childType.isStatic(), childArray.isStatic());
    assertEquals(fixture.childType.isStrictFloatingPoint(), childArray.isStrictFloatingPoint());
  }
}
