package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.AbstractParameter;
import org.lgna.project.ast.ConstructorInvocationStatement;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.JavaField;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.SuperConstructorInvocationStatement;
import org.lgna.story.SBiped;
import org.lgna.story.resources.DynamicBipedResource;

import java.util.List;

import static org.junit.Assert.*;

public class TypeManagerSmallGapBehaviorTest {
  public enum SingleOptionResource {
    ONLY
  }

  public static class SingleOptionAncestor {
    public SingleOptionAncestor(SingleOptionResource resource) {
    }
  }

  private static InstanceCreation dynamicBipedCreation(String className, String resourceName) {
    JavaConstructor constructor = JavaConstructor.getInstance(DynamicBipedResource.class, String.class, String.class);
    List<? extends AbstractParameter> parameters = constructor.getRequiredParameters();
    return new InstanceCreation(
        constructor,
        new SimpleArgument(parameters.get(0), new StringLiteral(className)),
        new SimpleArgument(parameters.get(1), new StringLiteral(resourceName)));
  }

  @Test
  public void namedUserTypeFromArgumentFieldUsesSingleEnumConstantAsSuperArgument() {
    NamedUserType type = TypeManager.getNamedUserTypeFromArgumentField(
        JavaType.getInstance(SingleOptionAncestor.class),
        JavaField.getInstance(SingleOptionResource.class, "ONLY"));

    assertEquals("SingleOption", type.getName());
    assertEquals(JavaType.getInstance(SingleOptionAncestor.class), type.superType.getValue());
    NamedUserConstructor constructor = type.getDeclaredConstructor();
    ConstructorInvocationStatement invocation = constructor.body.getValue().constructorInvocationStatement.getValue();
    Expression expression = invocation.requiredArguments.get(0).expression.getValue();
    assertTrue(expression instanceof FieldAccess);
    assertEquals("ONLY", ((FieldAccess) expression).field.getValue().getName());
  }

  @Test
  public void namedUserTypeFromDynamicResourceInstanceCreationHonorsExplicitClassName() {
    NamedUserType type = TypeManager.getNamedUserTypeFromDynamicResourceInstanceCreation(
        JavaType.getInstance(SBiped.class),
        dynamicBipedCreation("ogre", "ogre"),
        "GalleryOgre");

    assertEquals("GalleryOgre", type.getName());
    assertNotNull(type.superType.getValue());
    assertFalse(type.constructors.isEmpty());
  }

  @Test
  public void namedUserTypeFromPersonResourceInstanceCreationBuildsBipedSpecificTypeChain() {
    NamedUserType type = TypeManager.getNamedUserTypeFromPersonResourceInstanceCreation(dynamicBipedCreation("ogre", "ogre"));

    assertEquals("DynamicBiped", type.getName());
    assertTrue(type.superType.getValue() instanceof NamedUserType);
    NamedUserType generatedSuperType = (NamedUserType) type.superType.getValue();
    assertEquals(JavaType.getInstance(SBiped.class), generatedSuperType.superType.getValue());
    assertFalse(type.constructors.isEmpty());
  }

  @Test
  public void singleConstantEnumFieldIsDetectedAsTheOnlyEnumConstant() {
    JavaField onlyField = TypeManager.getEnumConstantFieldIfOneAndOnly(JavaType.getInstance(SingleOptionResource.class));

    assertNotNull(onlyField);
    assertEquals("ONLY", onlyField.getName());
    assertNull(TypeManager.getEnumConstantFieldIfOneAndOnly(JavaType.getInstance(Thread.State.class)));
  }
}
