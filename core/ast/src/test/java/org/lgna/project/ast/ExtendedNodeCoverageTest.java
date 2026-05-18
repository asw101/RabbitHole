package org.lgna.project.ast;

import edu.cmu.cs.dennisc.pattern.Crawlable;
import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.event.AddListPropertyEvent;
import edu.cmu.cs.dennisc.property.event.ClearListPropertyEvent;
import edu.cmu.cs.dennisc.property.event.ListPropertyListener;
import edu.cmu.cs.dennisc.property.event.RemoveListPropertyEvent;
import edu.cmu.cs.dennisc.property.event.SetListPropertyEvent;
import org.junit.Test;
import org.lgna.project.ast.localizer.AstLocalizer;
import org.lgna.project.ast.localizer.DefaultAstLocalizer;
import org.lgna.project.ast.localizer.DefaultAstLocalizerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Extended deep tests for AbstractNode, Element, FieldAccess, DefaultAstLocalizer,
 * and various expression/statement coverage gaps.
 */
public class ExtendedNodeCoverageTest {

  // ── DefaultAstLocalizer ──────────────────────────────────

  @Test
  public void defaultAstLocalizerAppendDeclaration() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    UserMethod method = AstUtilities.createProcedure("myMethod");
    localizer.appendDeclaration(method);
    assertEquals("myMethod", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendDeclarationNullName() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    // UserLocal with null name
    UserLocal local = new UserLocal(null, JavaType.OBJECT_TYPE, true);
    localizer.appendDeclaration(local);
    assertEquals("", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendBoolean() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendBoolean(true);
    assertEquals("true", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendChar() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendChar('x');
    assertEquals("x", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendInt() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendInt(42);
    assertEquals("42", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendLong() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendLong(123456789L);
    assertEquals("123456789", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendFloat() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendFloat(3.14f);
    assertTrue(sb.toString().startsWith("3.14"));
  }

  @Test
  public void defaultAstLocalizerAppendDouble() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendDouble(2.718);
    assertTrue(sb.toString().startsWith("2.718"));
  }

  @Test
  public void defaultAstLocalizerAppendNullLiteral() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendNullLiteral();
    assertEquals("null", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendNull() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendNull();
    assertEquals("null", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendThis() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendThis();
    assertEquals("this", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendSpace() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendSpace();
    assertEquals(" ", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendDot() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendDot();
    assertEquals(".", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendText() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendText("hello world");
    assertEquals("hello world", sb.toString());
  }

  @Test
  public void defaultAstLocalizerAppendLocalizedText() {
    StringBuilder sb = new StringBuilder();
    DefaultAstLocalizer localizer = new DefaultAstLocalizer(sb);
    localizer.appendLocalizedText(Comment.class, "myKey");
    assertEquals("myKey", sb.toString());
  }

  @Test
  public void defaultAstLocalizerFactory() {
    DefaultAstLocalizerFactory factory = new DefaultAstLocalizerFactory();
    StringBuilder sb = new StringBuilder();
    AstLocalizer localizer = factory.createInstance(sb);
    assertNotNull(localizer);
    assertTrue(localizer instanceof DefaultAstLocalizer);
  }

  // ── FieldAccess deep ──────────────────────────────────────

  @Test
  public void fieldAccessIsValid_static() {
    FieldAccess fa = AstUtilities.createStaticFieldAccess(System.class, "out");
    assertTrue(fa.isValid());
  }

  @Test
  public void fieldAccessIsValid_nullExpression() {
    FieldAccess fa = new FieldAccess();
    assertFalse(fa.isValid());
  }

  @Test
  public void fieldAccessIsValid_nullField() {
    FieldAccess fa = new FieldAccess();
    fa.expression.setValue(new NullLiteral());
    assertFalse(fa.isValid());
  }

  @Test
  public void fieldAccessGetType() {
    FieldAccess fa = AstUtilities.createStaticFieldAccess(System.class, "out");
    assertNotNull(fa.getType());
  }

  @Test
  public void fieldAccessGetType_nullField() {
    FieldAccess fa = new FieldAccess();
    assertNull(fa.getType());
  }

  @Test
  public void fieldAccessLevelOfPrecedence() {
    FieldAccess fa = new FieldAccess();
    assertEquals(16, fa.getLevelOfPrecedence());
  }

  @Test
  public void fieldAccessAppendRepr() {
    FieldAccess fa = AstUtilities.createStaticFieldAccess(System.class, "out");
    String repr = fa.getRepr();
    assertNotNull(repr);
    assertTrue(repr.length() > 0);
  }

  @Test
  public void fieldAccessInstanceField() {
    UserField field = new UserField("myField", JavaType.getInstance(String.class), new NullLiteral());
    NamedUserType type = AstUtilities.createType("MyType", JavaType.OBJECT_TYPE);
    type.fields.add(field);
    FieldAccess fa = new FieldAccess(new ThisExpression(), field);
    assertNotNull(fa.field.getValue());
  }

  // ── MethodInvocation deep ─────────────────────────────────

  @Test
  public void methodInvocationGetType() {
    JavaMethod method = JavaMethod.getInstance(String.class, "length");
    MethodInvocation mi = AstUtilities.createMethodInvocation(new StringLiteral("x"), method);
    assertNotNull(mi.getType());
  }

  @Test
  public void methodInvocationArgumentProperties() {
    JavaMethod method = JavaMethod.getInstance(String.class, "length");
    MethodInvocation mi = AstUtilities.createMethodInvocation(new StringLiteral("x"), method);
    assertNotNull(mi.getRequiredArgumentsProperty());
    assertNotNull(mi.getVariableArgumentsProperty());
    assertNotNull(mi.getKeyedArgumentsProperty());
  }

  @Test
  public void methodInvocationIsValid() {
    JavaMethod method = JavaMethod.getInstance(String.class, "length");
    MethodInvocation mi = AstUtilities.createMethodInvocation(new StringLiteral("x"), method);
    assertTrue(mi.isValid());
  }

  @Test
  public void methodInvocationGetRepr() {
    JavaMethod method = JavaMethod.getInstance(String.class, "length");
    MethodInvocation mi = AstUtilities.createMethodInvocation(new StringLiteral("x"), method);
    String repr = mi.getRepr();
    assertNotNull(repr);
  }

  // ── Element list listeners ──────────────────────────────

  @Test
  @SuppressWarnings("unchecked")
  public void listPropertyListenerFired() {
    BlockStatement block = new BlockStatement();
    final List<String> events = new ArrayList<>();
    ListPropertyListener<Statement> listener = new ListPropertyListener<>() {
      @Override public void added(AddListPropertyEvent<Statement> e) { events.add("added"); }
      @Override public void cleared(ClearListPropertyEvent<Statement> e) { events.add("cleared"); }
      @Override public void removed(RemoveListPropertyEvent<Statement> e) { events.add("removed"); }
      @Override public void set(SetListPropertyEvent<Statement> e) { events.add("set"); }
    };
    block.addListPropertyListener(listener);
    Comment c = new Comment();
    block.statements.add(c);
    assertTrue(events.contains("added"));
    block.statements.remove(0);
    assertTrue(events.contains("removed"));
    block.removeListPropertyListener(listener);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void listPropertyListenerClearing() {
    BlockStatement block = new BlockStatement();
    Comment c = new Comment();
    block.statements.add(c);
    final boolean[] cleared = {false};
    ListPropertyListener<Statement> listener = new ListPropertyListener<>() {
      @Override public void added(AddListPropertyEvent<Statement> e) {}
      @Override public void cleared(ClearListPropertyEvent<Statement> e) { cleared[0] = true; }
      @Override public void removed(RemoveListPropertyEvent<Statement> e) {}
      @Override public void set(SetListPropertyEvent<Statement> e) {}
    };
    block.addListPropertyListener(listener);
    block.statements.clear();
    assertTrue(cleared[0]);
    assertNull(c.getParent());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void accessListPropertyListeners() {
    BlockStatement block = new BlockStatement();
    ListPropertyListener<Statement> listener = new ListPropertyListener<>() {
      @Override public void added(AddListPropertyEvent<Statement> e) {}
      @Override public void cleared(ClearListPropertyEvent<Statement> e) {}
      @Override public void removed(RemoveListPropertyEvent<Statement> e) {}
      @Override public void set(SetListPropertyEvent<Statement> e) {}
    };
    block.addListPropertyListener(listener);
    boolean found = false;
    for (ListPropertyListener<?> l : block.accessListPropertyListeners()) {
      if (l == listener) { found = true; }
    }
    assertTrue(found);
  }

  // ── AbstractNode crawl with different policies ────────

  @Test
  public void crawlWithIncludeReferences() {
    NamedUserType type = AstUtilities.createType("CrawlType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("proc");
    type.methods.add(method);
    List<Crawlable> visited = new ArrayList<>();
    type.crawl(crawlable -> visited.add(crawlable), CrawlPolicy.COMPLETE);
    assertTrue(visited.size() > 1);
  }

  @Test
  public void crawlExcludeReferences() {
    NamedUserType type = AstUtilities.createType("CrawlType2", JavaType.OBJECT_TYPE);
    List<Crawlable> visited = new ArrayList<>();
    type.crawl(crawlable -> visited.add(crawlable), CrawlPolicy.EXCLUDE_REFERENCES_ENTIRELY);
    assertTrue(visited.contains(type));
  }

  // ── NamedUserType additional coverage ─────────────────

  @Test
  public void namedUserTypeFields() {
    NamedUserType type = AstUtilities.createType("WithFields", JavaType.OBJECT_TYPE);
    UserField field = new UserField("x", JavaType.getInstance(int.class), new IntegerLiteral(0));
    type.fields.add(field);
    assertEquals(1, type.getDeclaredFields().size());
  }

  @Test
  public void namedUserTypeMethods() {
    NamedUserType type = AstUtilities.createType("WithMethods", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("doIt");
    type.methods.add(method);
    assertFalse(type.getDeclaredMethods().isEmpty());
  }

  @Test
  public void namedUserTypeIsUserAuthored() {
    NamedUserType type = AstUtilities.createType("User", JavaType.OBJECT_TYPE);
    assertTrue(type.isUserAuthored());
  }

  @Test
  public void namedUserTypeSuperType() {
    NamedUserType type = AstUtilities.createType("Child", JavaType.OBJECT_TYPE);
    assertSame(JavaType.OBJECT_TYPE, type.getSuperType());
  }

  // ── UserMethod additional coverage ──────────────────────

  @Test
  public void userMethodBody() {
    UserMethod method = AstUtilities.createProcedure("bodyTest");
    assertNotNull(method.body.getValue());
    assertEquals(0, method.body.getValue().statements.size());
  }

  @Test
  public void userMethodReturnType() {
    UserMethod method = AstUtilities.createFunction("getter", String.class);
    assertNotNull(method.getReturnType());
  }

  @Test
  public void userMethodParameters() {
    UserMethod method = AstUtilities.createProcedure("noParams");
    assertTrue(method.getRequiredParameters().isEmpty());
  }

  // ── AstUtilities completeMethodInvocation ──────────────

  @Test
  public void completeMethodInvocation() {
    JavaMethod method = JavaMethod.getInstance(String.class, "charAt", int.class);
    MethodInvocation mi = new MethodInvocation();
    mi.method.setValue(method);
    for (AbstractParameter p : method.getRequiredParameters()) {
      mi.requiredArguments.add(new SimpleArgument(p, new NullLiteral()));
    }
    MethodInvocation completed = AstUtilities.completeMethodInvocation(
        mi, new StringLiteral("test"), new IntegerLiteral(0));
    assertSame(mi, completed);
    assertTrue(completed.expression.getValue() instanceof StringLiteral);
  }

  // ── AstUtilities fixRequiredArguments ──────────────────

  @Test
  public void fixRequiredArgumentsIfNecessary() {
    JavaMethod method = JavaMethod.getInstance(String.class, "charAt", int.class);
    Expression target = new StringLiteral("test");
    Expression arg = new IntegerLiteral(0);
    MethodInvocation mi = AstUtilities.createMethodInvocation(target, method, arg);
    // Should not throw - args already match
    AstUtilities.fixRequiredArgumentsIfNecessary(mi);
    assertEquals(1, mi.requiredArguments.size());
  }

  // ── Statement isEnabled ──────────────────────────────────

  @Test
  public void statementIsEnabled() {
    Comment c = new Comment();
    assertTrue(c.isEnabled.getValue());
    c.isEnabled.setValue(false);
    assertFalse(c.isEnabled.getValue());
  }

  // ── Arithmetic/Logic expressions ─────────────────────

  @Test
  public void arithmeticInfixExpression() {
    ArithmeticInfixExpression aie = new ArithmeticInfixExpression(
        new IntegerLiteral(1),
        ArithmeticInfixExpression.Operator.PLUS,
        new IntegerLiteral(2),
        int.class);
    assertNotNull(aie);
    assertNotNull(aie.getType());
  }

  @Test
  public void logicalComplement() {
    LogicalComplement lc = new LogicalComplement(new BooleanLiteral(true));
    assertNotNull(lc);
    assertNotNull(lc.getType());
  }

  @Test
  public void relationalInfixExpression() {
    RelationalInfixExpression rie = new RelationalInfixExpression(
        new IntegerLiteral(1),
        RelationalInfixExpression.Operator.LESS_EQUALS,
        new IntegerLiteral(2),
        JavaType.getInstance(int.class),
        JavaType.getInstance(int.class));
    assertNotNull(rie);
  }

  @Test
  public void conditionalInfixExpression() {
    ConditionalInfixExpression cie = new ConditionalInfixExpression(
        new BooleanLiteral(true),
        ConditionalInfixExpression.Operator.AND,
        new BooleanLiteral(false));
    assertNotNull(cie);
  }

  // ── ArrayInstanceCreation ────────────────────────────

  @Test
  public void arrayInstanceCreationCollections() {
    List<Expression> exprs = new ArrayList<>();
    exprs.add(new StringLiteral("a"));
    exprs.add(new StringLiteral("b"));
    ArrayInstanceCreation aic = AstUtilities.createArrayInstanceCreation(
        String[].class, exprs);
    assertNotNull(aic);
  }

  @Test
  public void arrayInstanceCreationWithTypeAndCollection() {
    List<Expression> exprs = new ArrayList<>();
    exprs.add(new IntegerLiteral(1));
    ArrayInstanceCreation aic = AstUtilities.createArrayInstanceCreation(
        JavaType.getInstance(int[].class), exprs);
    assertNotNull(aic);
  }

  // ── LgnaVmException (via VM) ──────────────────────────

  @Test
  public void namedUserTypeGetRepr() {
    NamedUserType type = AstUtilities.createType("ReprType", JavaType.OBJECT_TYPE);
    String repr = type.getRepr();
    assertNotNull(repr);
    assertTrue(repr.length() > 0);
  }

  @Test
  public void doInOrderGetRepr() {
    DoInOrder dio = AstUtilities.createDoInOrder();
    assertNotNull(dio.getRepr());
  }

  @Test
  public void countLoopGetRepr() {
    CountLoop cl = AstUtilities.createCountLoop(new IntegerLiteral(3));
    assertNotNull(cl.getRepr());
  }

  @Test
  public void whileLoopGetRepr() {
    WhileLoop wl = AstUtilities.createWhileLoop(new BooleanLiteral(true));
    assertNotNull(wl.getRepr());
  }

  @Test
  public void methodInvocationGetReprFull() {
    JavaMethod method = JavaMethod.getInstance(String.class, "charAt", int.class);
    MethodInvocation mi = AstUtilities.createMethodInvocation(
        new StringLiteral("test"), method, new IntegerLiteral(0));
    String repr = mi.getRepr();
    assertNotNull(repr);
    assertTrue(repr.contains("charAt"));
  }
}
