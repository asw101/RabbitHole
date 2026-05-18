package org.lgna.project.ast;

import org.lgna.project.code.ProcessableNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class StatementCodeEmitterTest {
  @Test
  public void emitsReturnStatement() {
    ReturnStatement statement = AstUtilities.createReturnStatement(String.class, new StringLiteral("done"));

    assertEquals("return \"done\";", generate(statement));
  }

  @Test
  public void emitsAssignmentExpressionStatement() {
    UserLocal count = new UserLocal("count", Integer.class, false);
    ExpressionStatement statement = new ExpressionStatement(new AssignmentExpression(
        JavaType.getInstance(Integer.class),
        new LocalAccess(count),
        AssignmentExpression.Operator.ASSIGN,
        new IntegerLiteral(3)));

    assertEquals("count=3;", generate(statement));
  }

  @Test
  public void emitsConditionalWithElseIfAndElseBlocks() {
    ConditionalStatement conditional = new ConditionalStatement(
        new BooleanExpressionBodyPair[] {
            new BooleanExpressionBodyPair(
                new BooleanLiteral(false),
                new BlockStatement(AstUtilities.createReturnStatement(String.class, new StringLiteral("first")))),
            new BooleanExpressionBodyPair(
                new BooleanLiteral(true),
                new BlockStatement(AstUtilities.createReturnStatement(String.class, new StringLiteral("second"))))
        },
        new BlockStatement(AstUtilities.createReturnStatement(String.class, new StringLiteral("else"))));

    assertEquals(
        "if(false){return \"first\";} else if(true){return \"second\";} else{return \"else\";}",
        generate(conditional));
  }

  @Test
  public void emitsWhileLoopWithBody() {
    WhileLoop loop = new WhileLoop(
        new BooleanLiteral(true),
        new BlockStatement(new LocalDeclarationStatement(
            new UserLocal("message", String.class, true),
            new StringLiteral("hi"))));

    assertEquals("while (true){final String message=\"hi\";}", generate(loop));
  }

  @Test
  public void emitsForEachArrayLoopAndRepairsGeneratedItemName() {
    ForEachInArrayLoop loop = new ForEachInArrayLoop(
        new UserLocal("COUNT__", String.class, true),
        AstUtilities.createArrayInstanceCreation(String[].class, new StringLiteral("red"), new StringLiteral("blue")),
        new BlockStatement());
    loop.body.getValue().statements.add(new LocalDeclarationStatement(
        new UserLocal("copy", String.class, true),
        new LocalAccess(loop.item.getValue())));

    String source = generate(loop);

    assertFalse(source.contains("COUNT__"));
    assertEquals("for(String itemA : new String[]{\"red\", \"blue\"}){final String copy=itemA;}", source);
  }

  @Test
  public void emitsForEachIterableLoopHeaderAndBody() {
    UserLocal item = new UserLocal("item", String.class, true);
    UserLocal iterable = new UserLocal("items", Iterable.class, false);
    ForEachInIterableLoop loop = new ForEachInIterableLoop(
        item,
        new LocalAccess(iterable),
        new BlockStatement(new LocalDeclarationStatement(
            new UserLocal("copy", String.class, true),
            new LocalAccess(item))));

    assertEquals("for(String item : items){final String copy=item;}", generate(loop));
  }

  @Test
  public void emitsConstructorBlockWithThisInvocationAndBody() {
    UserParameter value = new UserParameter("value", Integer.class);
    NamedUserConstructor delegatedConstructor = new NamedUserConstructor(
        new UserParameter[] {value},
        new ConstructorBlockStatement());
    ConstructorBlockStatement block = new ConstructorBlockStatement(
        new ThisConstructorInvocationStatement(
            delegatedConstructor,
            new SimpleArgument(value, new IntegerLiteral(7))),
        new LocalDeclarationStatement(new UserLocal("copy", Integer.class, true), new IntegerLiteral(1)));

    assertEquals("{this(7);final Integer copy=1;}", generate(block));
  }

  @Test
  public void emitsMultilineCommentAsSeparateLineComments() {
    Comment comment = new Comment("alpha\nbeta");

    assertEquals("\n// alpha\n// beta\n", generate(comment));
  }

  @Test
  public void emitsUserMethodWithHeaderAndBody() {
    NamedUserType type = createSimpleType("Greeter");
    UserParameter name = new UserParameter("name", String.class);
    UserMethod method = new UserMethod(
        "greet",
        String.class,
        new UserParameter[] {name},
        new BlockStatement(AstUtilities.createReturnStatement(
            String.class,
            new StringConcatenation(new StringLiteral("hello "), new ParameterAccess(name)))));
    type.methods.add(method);

    assertEquals("public String greet(String name){return \"hello \" + name;}", generate(method));
  }

  @Test
  public void emitsGetterAndSetterForScalarField() {
    NamedUserType type = createSimpleType("Person");
    UserField field = new UserField("name", String.class, new StringLiteral("Alice"));
    type.fields.add(field);

    assertEquals("public String getName(){return this.name;}", generate(field.getGetter()));
    assertEquals("public void setName(String name){this.name=name;}", generate(field.getSetter()));
  }

  @Test
  public void emitsIndexedGetterAndSetterForArrayField() {
    NamedUserType type = createSimpleType("Names");
    UserField field = new UserField(
        "names",
        String[].class,
        AstUtilities.createArrayInstanceCreation(String[].class, new StringLiteral("A"), new StringLiteral("B")));
    type.fields.add(field);

    assertEquals("public String getNames(Integer index){return this.names[index];}", generate(field.getArrayItemGetter()));
    assertEquals("public void setNames(Integer index,String value){this.names[index]=value;}", generate(field.getArrayItemSetter()));
  }

  @Test
  public void emitsFieldDeclarationWithInitializer() {
    UserField field = new UserField("name", String.class, new StringLiteral("Alice"));

    assertEquals("public String name=\"Alice\";", generate(field));
  }

  @Test
  public void emitsDisabledStatementWithCommentMarkers() {
    LocalDeclarationStatement statement = new LocalDeclarationStatement(
        new UserLocal("hidden", String.class, true),
        new StringLiteral("secret"));
    statement.isEnabled.setValue(false);

    assertEquals("final String hidden=\"secret\";", generate(statement));
  }

  private static String generate(ProcessableNode node) {
    JavaCodeGenerator generator = new JavaCodeGenerator.Builder().build();
    node.process(generator);
    return generator.getText();
  }

  private static NamedUserType createSimpleType(String name) {
    return new NamedUserType(
        name,
        null,
        Object.class,
        new NamedUserConstructor[] {new NamedUserConstructor(new UserParameter[0], new ConstructorBlockStatement())},
        new UserMethod[0],
        new UserField[0]);
  }
}
