package org.lgna.project;

import edu.cmu.cs.dennisc.tree.DefaultNode;
import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.SuperConstructorInvocationStatement;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class ProgramTypeUtilitiesTest {
  @Test
  public void crawlersAndLookupMethodsFindExpectedNodes() {
    TestResource resource = new TestResource("asset.bin");
    NamedUserType helperType = createType("Helper");
    UserParameter ctorParameter = new UserParameter("seed", String.class);
    NamedUserConstructor constructor = new NamedUserConstructor(
        new UserParameter[] {ctorParameter},
        new ConstructorBlockStatement(new SuperConstructorInvocationStatement(JavaConstructor.getInstance(Object.class))));
    helperType.constructors.add(constructor);

    NamedUserType programType = createType("Program");
    UserField field = new UserField("message", String.class);
    programType.fields.add(field);

    UserParameter parameter = new UserParameter("value", String.class);
    UserMethod targetMethod = new UserMethod("target", Void.TYPE, new UserParameter[] {parameter}, new BlockStatement());
    programType.methods.add(targetMethod);

    BlockStatement body = new BlockStatement();
    UserLocal local = new UserLocal("counter", Integer.class, false);
    body.statements.add(new LocalDeclarationStatement(local, new IntegerLiteral(1)));
    body.statements.add(statement(new FieldAccess(new ThisExpression(), field)));

    MethodInvocation invocation = new MethodInvocation(new ThisExpression(), targetMethod);
    invocation.requiredArguments.add(new SimpleArgument(parameter, new StringLiteral("hello")));
    body.statements.add(statement(invocation));

    InstanceCreation creation = new InstanceCreation(constructor);
    creation.requiredArguments.add(new SimpleArgument(ctorParameter, new StringLiteral("seed")));
    body.statements.add(statement(creation));

    UserMethod owner = new UserMethod("owner", Void.TYPE, new UserParameter[0], body);
    programType.methods.add(owner);

    Set<NamedUserType> namedUserTypes = new HashSet<>(Arrays.asList(programType, helperType));
    Set<Resource> resources = new HashSet<>(List.of(resource));
    Project project = new Project(programType, namedUserTypes, resources, Project.SceneCameraType.WindowCamera);

    assertEquals(1, ProgramTypeUtilities.getLocals(owner).size());
    assertSame(local, ProgramTypeUtilities.getLocals(owner).get(0));

    assertEquals(1, ProgramTypeUtilities.getFieldAccesses(programType, field, null).size());
    assertSame(field, ProgramTypeUtilities.getFieldAccesses(programType, field, null).get(0).field.getValue());

    assertEquals(1, ProgramTypeUtilities.getMethodInvocations(programType, targetMethod, null).size());
    assertSame(invocation, ProgramTypeUtilities.getMethodInvocations(programType, targetMethod, null).get(0));

    assertEquals(1, ProgramTypeUtilities.getArgumentLists(programType, targetMethod, null).size());
    assertEquals(invocation.requiredArguments, ProgramTypeUtilities.getArgumentLists(programType, targetMethod, null).get(0));

    assertEquals(1, ProgramTypeUtilities.getArgumentLists(programType, constructor, null).size());
    assertEquals(creation.requiredArguments, ProgramTypeUtilities.getArgumentLists(programType, constructor, null).get(0));

    assertSame(invocation, ProgramTypeUtilities.lookupNode(project, invocation.getId()));
    assertSame(resource, ProgramTypeUtilities.lookupResource(project, resource.getId()));
    assertNull(ProgramTypeUtilities.lookupNode(project, UUID.randomUUID()));
    assertNull(ProgramTypeUtilities.lookupResource(project, UUID.randomUUID()));
  }

  @Test
  public void namedUserTypesAreReturnedAsInheritanceTree() {
    NamedUserType base = createType("Base");
    NamedUserType child = createType("Child");
    child.superType.setValue(base);
    Project project = new Project(base, new HashSet<>(Arrays.asList(base, child)), new HashSet<>(), Project.SceneCameraType.WindowCamera);

    DefaultNode<NamedUserType> root = ProgramTypeUtilities.getNamedUserTypesAsTree(project);
    DefaultNode<NamedUserType> baseNode = root.get(base);
    DefaultNode<NamedUserType> childNode = root.get(child);

    assertNotNull(baseNode);
    assertNotNull(childNode);
    assertTrue(baseNode.getChildren().contains(childNode));
  }

  @Test
  public void sanityCheckAllTypesReturnsEmptyStringForWellFormedTypes() {
    NamedUserType programType = createType("Program");
    programType.fields.add(new UserField("field", String.class));
    programType.methods.add(new UserMethod("method", Void.TYPE, new UserParameter[0], new BlockStatement()));
    programType.constructors.add(new NamedUserConstructor(
        new UserParameter[0],
        new ConstructorBlockStatement(new SuperConstructorInvocationStatement(JavaConstructor.getInstance(Object.class)))));

    Project project = new Project(programType, new HashSet<>(List.of(programType)), new HashSet<>(), Project.SceneCameraType.WindowCamera);

    assertEquals("", ProgramTypeUtilities.sanityCheckAllTypes(project));
  }

  private static NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.OBJECT_TYPE);
    return type;
  }

  private static Statement statement(org.lgna.project.ast.Expression expression) {
    return new ExpressionStatement(expression);
  }

  private static final class TestResource extends Resource {
    private TestResource(String name) {
      super(UUID.randomUUID());
      setOriginalFileName(name);
      setName(name);
      setContent("application/octet-stream", new byte[] {7});
    }
  }
}
