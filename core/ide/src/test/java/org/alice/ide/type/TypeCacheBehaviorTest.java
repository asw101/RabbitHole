package org.alice.ide.type;

import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.project.Project;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserParameter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TypeCacheBehaviorTest {
  private static final AbstractType<?, ?, ?> OBJECT_TYPE = JavaType.getInstance(Object.class);

  private static NamedUserType createNamedType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(OBJECT_TYPE);
    return type;
  }

  private static NamedUserConstructor addNoArgConstructor(NamedUserType type, Expression... superArguments) {
    NamedUserConstructor constructor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    for (Expression superArgument : superArguments) {
      body.constructorInvocationStatement.getValue().requiredArguments.add(new SimpleArgument(null, superArgument));
    }
    constructor.body.setValue(body);
    type.constructors.add(constructor);
    return constructor;
  }

  private static NamedUserType createParameterizedType(String name, AbstractType<?, ?, ?> parameterType) {
    NamedUserType type = createNamedType(name);
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.requiredParameters.add(new UserParameter("resource", parameterType));
    constructor.body.setValue(new ConstructorBlockStatement());
    type.constructors.add(constructor);
    return type;
  }

  private static Project createProject(NamedUserType... types) {
    NamedUserType programType = createNamedType("Program");
    addNoArgConstructor(programType);
    Set<NamedUserType> namedUserTypes = new LinkedHashSet<>();
    namedUserTypes.add(programType);
    Collections.addAll(namedUserTypes, types);
    return new Project(programType, namedUserTypes, Collections.<Resource>emptySet(), Project.SceneCameraType.WindowCamera);
  }

  @Test
  public void getTypeFor_returnsFreshTypeForSeededExtendsKey() {
    NamedUserType storedType = createNamedType("StoredType");
    addNoArgConstructor(storedType);

    TypeCache cache = new TypeCache(createProject(storedType));

    NamedUserType firstLookup = cache.getTypeFor(new ExtendsTypeKey(OBJECT_TYPE));
    NamedUserType secondLookup = cache.getTypeFor(new ExtendsTypeKey(OBJECT_TYPE));

    assertNotNull(firstLookup);
    assertNotSame(storedType, firstLookup);
    assertNotSame(firstLookup, secondLookup);
  }

  @Test
  public void getTypeFor_matchesFieldBackedSuperArgumentKey() {
    NamedUserType storedType = createNamedType("FieldBackedType");
    UserField field = new UserField("resource", JavaType.STRING_TYPE, new NullLiteral());
    storedType.fields.add(field);
    addNoArgConstructor(storedType, new FieldAccess(field));

    TypeCache cache = new TypeCache(createProject(storedType));

    assertNotNull(cache.getTypeFor(new ExtendsTypeWithSuperArgumentFieldKey(OBJECT_TYPE, field)));
  }

  @Test
  public void getTypeFor_matchesConstructorParameterKey() {
    AbstractType<?, ?, ?> parameterType = JavaType.getInstance(String.class);
    NamedUserType storedType = createParameterizedType("ParameterizedType", parameterType);

    TypeCache cache = new TypeCache(createProject(storedType));

    assertNotNull(cache.getTypeFor(new ExtendsTypeWithConstructorParameterTypeKey(OBJECT_TYPE, parameterType)));
  }

  @Test
  public void getTypeFor_matchesNamedTypeKeyForDynamicResourceStyleConstructor() {
    NamedUserType storedType = createNamedType("Alien2");
    addNoArgConstructor(storedType, new InstanceCreation(JavaConstructor.getInstance(StringBuilder.class)));

    TypeCache cache = new TypeCache(createProject(storedType));

    assertNotNull(cache.getTypeFor(new ExtendsTypeWithNamedType(OBJECT_TYPE, storedType.name.getName())));
  }

  @Test
  public void getTypeFor_returnsNullWhenKeyWasNeverSeeded() {
    TypeCache cache = new TypeCache(createProject());

    assertNull(cache.getTypeFor(new ExtendsTypeKey(JavaType.getInstance(Number.class))));
  }
}
