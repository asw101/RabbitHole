package org.lgna.project.ast;

import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractTypeDeepTest {
  private static final class HierarchyFixture {
    private final NamedUserType parentType;
    private final NamedUserType childType;
    private final NamedUserConstructor parentStringConstructor;
    private final UserMethod parentDescribe;
    private final UserMethod parentDescribeWithCount;
    private final UserField parentField;
    private final UserField childField;

    private HierarchyFixture() {
      parentField = new UserField("message", String.class, new StringLiteral("parent"));
      childField = new UserField("count", Integer.class, new IntegerLiteral(1));

      parentDescribe = new UserMethod(
          "describe",
          String.class,
          new UserParameter[0],
          new BlockStatement(AstUtilities.createReturnStatement(String.class, new StringLiteral("parent"))));
      parentDescribeWithCount = new UserMethod(
          "describe",
          String.class,
          new UserParameter[] {new UserParameter("count", Integer.class)},
          new BlockStatement(AstUtilities.createReturnStatement(String.class, new StringLiteral("count"))));

      NamedUserConstructor parentNoArgConstructor = new NamedUserConstructor(
          new UserParameter[0],
          new ConstructorBlockStatement());
      parentStringConstructor = new NamedUserConstructor(
          new UserParameter[] {new UserParameter("text", String.class)},
          new ConstructorBlockStatement());

      parentType = new NamedUserType(
          "ParentType",
          null,
          Object.class,
          new NamedUserConstructor[] {parentStringConstructor, parentNoArgConstructor},
          new UserMethod[] {parentDescribe, parentDescribeWithCount},
          new UserField[] {parentField});

      UserMethod childOnly = new UserMethod(
          "childOnly",
          Void.TYPE,
          new UserParameter[0],
          new BlockStatement());
      NamedUserConstructor childConstructor = new NamedUserConstructor(
          new UserParameter[0],
          new ConstructorBlockStatement());

      childType = new NamedUserType(
          "ChildType",
          null,
          parentType,
          new NamedUserConstructor[] {childConstructor},
          new UserMethod[] {childOnly},
          new UserField[] {childField});
    }
  }

  private static final class StaticFixture {
  }

  @Test
  public void firstEncounteredJavaTypeWalksThroughUserHierarchy() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertSame(JavaType.OBJECT_TYPE, fixture.parentType.getFirstEncounteredJavaType());
    assertSame(JavaType.OBJECT_TYPE, fixture.childType.getFirstEncounteredJavaType());
  }

  @Test
  public void userTypesAreAssignableAcrossHierarchy() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertTrue(fixture.parentType.isAssignableFrom(fixture.childType));
    assertTrue(fixture.childType.isAssignableTo(fixture.parentType));
    assertFalse(fixture.childType.isAssignableFrom(fixture.parentType));
  }

  @Test
  public void userAndJavaTypesRemainUnrelatedWhenAppropriate() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertFalse(fixture.childType.isAssignableFrom(JavaType.STRING_TYPE));
    assertFalse(JavaType.STRING_TYPE.isAssignableFrom(fixture.childType));
    assertFalse(fixture.childType.isAssignableTo(JavaType.STRING_TYPE));
  }

  @Test
  public void primitiveWrapperAssignabilityFlowsThroughAbstractTypeHelpers() {
    assertTrue(JavaType.INTEGER_PRIMITIVE_TYPE.isAssignableTo(JavaType.INTEGER_OBJECT_TYPE));
    assertTrue(JavaType.INTEGER_OBJECT_TYPE.isAssignableFrom(JavaType.INTEGER_PRIMITIVE_TYPE));
    assertFalse(JavaType.INTEGER_PRIMITIVE_TYPE.isAssignableTo(JavaType.DOUBLE_OBJECT_TYPE));
  }

  @Test
  public void getDeclaredConstructorMatchesByParameterTypes() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertSame(fixture.parentStringConstructor, fixture.parentType.getDeclaredConstructor(String.class));
    assertNotNull(fixture.parentType.getDeclaredConstructor());
    assertNull(fixture.parentType.getDeclaredConstructor(Integer.class));
  }

  @Test
  public void firstDeclaredConstructorAndFirstParameterTypeReflectInsertionOrder() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertSame(fixture.parentStringConstructor, fixture.parentType.getFirstDeclaredConstructor());
    assertSame(JavaType.STRING_TYPE, fixture.parentType.getFirstParameterType());
    assertNull(fixture.childType.getFirstParameterType());
  }

  @Test
  public void getDeclaredMethodResolvesExactSignature() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertSame(fixture.parentDescribe, fixture.parentType.getDeclaredMethod("describe"));
    assertSame(fixture.parentDescribeWithCount, fixture.parentType.getDeclaredMethod("describe", Integer.class));
    assertNull(fixture.parentType.getDeclaredMethod("describe", String.class));
  }

  @Test
  public void findMethodWalksUpTheSuperTypeChain() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertSame(fixture.parentDescribe, fixture.childType.findMethod("describe"));
    assertSame(fixture.parentDescribeWithCount, fixture.childType.findMethod("describe", Integer.class));
    assertNull(fixture.childType.findMethod("missing"));
  }

  @Test
  public void getDeclaredFieldAndFindFieldResolveByNameAndType() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertSame(fixture.parentField, fixture.parentType.getDeclaredField(JavaType.STRING_TYPE, "message"));
    assertSame(fixture.childField, fixture.childType.getDeclaredField("count"));
    assertSame(fixture.parentField, fixture.childType.findField("message"));
    assertNull(fixture.childType.findField(JavaType.STRING_TYPE, "count"));
  }

  @Test
  public void formatNameUsesLocalizerForSimpleTypes() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertEquals("localized-ChildType", fixture.childType.formatName((internal, fallback) -> "localized-" + fallback));
    assertEquals("localized-message", fixture.parentField.formatName((internal, fallback) -> "localized-" + fallback));
  }

  @Test
  public void formatNameUsesComponentTypeForArrays() {
    HierarchyFixture fixture = new HierarchyFixture();

    AbstractType<?, ?, ?> arrayType = fixture.childType.getArrayType();

    assertTrue(arrayType instanceof UserArrayType);
    assertEquals("boxed-ChildType[]", arrayType.formatName((internal, fallback) -> "boxed-" + fallback));
    assertSame(fixture.childType, arrayType.getComponentType());
  }

  @Test
  public void hierarchyDepthCountsAllSuperTypes() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertEquals(1, fixture.parentType.hierarchyDepth());
    assertEquals(2, fixture.childType.hierarchyDepth());
    assertEquals(0, JavaType.OBJECT_TYPE.hierarchyDepth());
  }

  @Test
  public void addModifiersIncludesFinalAbstractAndStaticWhenPresent() {
    HierarchyFixture fixture = new HierarchyFixture();
    List<Modifier> modifiers = new ArrayList<>();
    fixture.childType.finalAbstractOrNeither.setValue(TypeModifierFinalAbstractOrNeither.ABSTRACT);
    fixture.childType.addModifiers(modifiers);

    assertTrue(modifiers.contains(Modifier.PUBLIC));
    assertTrue(modifiers.contains(Modifier.ABSTRACT));
    assertFalse(modifiers.contains(Modifier.FINAL));

    List<Modifier> staticModifiers = new ArrayList<>();
    JavaType staticType = JavaType.getInstance(StaticFixture.class);
    staticType.addModifiers(staticModifiers);

    assertTrue(staticModifiers.contains(Modifier.PRIVATE));
    assertTrue(staticModifiers.contains(Modifier.STATIC));
  }

  @Test
  public void keywordFactoryAndSubclassFlagsUseUserTypeDefaults() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertNull(fixture.childType.getKeywordFactoryType());
    assertTrue(fixture.childType.isFollowToSuperClassDesired());
    assertFalse(fixture.childType.isConsumptionBySubClassDesired());
  }

  @Test
  public void userTypesExposeExpectedPrimitiveAndArrayCharacteristics() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertFalse(fixture.childType.isPrimitive());
    assertFalse(fixture.childType.isArray());
    assertFalse(fixture.childType.isInterface());
    assertNull(fixture.childType.getComponentType());
  }

  @Test
  public void userArrayTypeRetainsLeafTypeAndDimensionInformation() {
    HierarchyFixture fixture = new HierarchyFixture();
    UserArrayType oneDimensional = (UserArrayType) fixture.childType.getArrayType();
    UserArrayType twoDimensional = (UserArrayType) oneDimensional.getArrayType();

    assertSame(fixture.childType, oneDimensional.getLeafType());
    assertEquals(1, oneDimensional.getDimensionCount());
    assertEquals(2, twoDimensional.getDimensionCount());
    assertSame(oneDimensional, twoDimensional.getComponentType());
  }

  @Test
  public void declaredCollectionsComeDirectlyFromUserTypeStorage() {
    HierarchyFixture fixture = new HierarchyFixture();

    assertEquals(2, fixture.parentType.getDeclaredConstructors().size());
    assertEquals(2, fixture.parentType.getDeclaredMethods().size());
    assertEquals(1, fixture.parentType.getDeclaredFields().size());
  }
}
