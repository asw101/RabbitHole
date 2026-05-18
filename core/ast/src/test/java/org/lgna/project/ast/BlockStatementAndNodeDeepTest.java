package org.lgna.project.ast;

import edu.cmu.cs.dennisc.pattern.Crawlable;
import edu.cmu.cs.dennisc.pattern.Crawler;
import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.event.PropertyEvent;
import edu.cmu.cs.dennisc.property.event.PropertyListener;
import org.lgna.project.code.CodeOrganizer;
import org.junit.Test;
import org.lgna.project.ast.localizer.AstLocalizer;
import org.lgna.project.ast.localizer.AstLocalizerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for BlockStatement, UserArrayType, AbstractNode, and Element.
 */
public class BlockStatementAndNodeDeepTest {

  // ── BlockStatement ───────────────────────────────────────────

  @Test
  public void emptyBlockStatementHasNoStatements() {
    BlockStatement block = new BlockStatement();
    assertEquals(0, block.statements.size());
  }

  @Test
  public void blockStatementVarArgsConstructor() {
    Comment c1 = new Comment();
    Comment c2 = new Comment();
    BlockStatement block = new BlockStatement(c1, c2);
    assertEquals(2, block.statements.size());
  }

  @Test
  public void processCallsProcessorProcessBlock() {
    BlockStatement block = new BlockStatement();
    final boolean[] called = {false};
    AstProcessor processor = new AstProcessor() {
      @Override public void processBlock(BlockStatement b) { called[0] = true; }
      @Override public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) { return null; }
    };
    block.process(processor);
    assertTrue(called[0]);
  }

  @Test
  public void containsReturnStatement_disabledBlock() {
    BlockStatement block = new BlockStatement();
    block.isEnabled.setValue(false);
    assertFalse(block.containsAtLeastOneEnabledReturnStatement());
  }

  @Test
  public void containsReturnStatement_emptyEnabled() {
    BlockStatement block = new BlockStatement();
    assertFalse(block.containsAtLeastOneEnabledReturnStatement());
  }

  @Test
  public void containsReturnStatement_withReturn() {
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    BlockStatement block = new BlockStatement(ret);
    assertTrue(block.containsAtLeastOneEnabledReturnStatement());
  }

  @Test
  public void containsReturnForEveryPath_disabled() {
    BlockStatement block = new BlockStatement();
    block.isEnabled.setValue(false);
    assertFalse(block.containsAReturnForEveryPath());
  }

  @Test
  public void containsReturnForEveryPath_empty() {
    BlockStatement block = new BlockStatement();
    assertFalse(block.containsAReturnForEveryPath());
  }

  @Test
  public void containsReturnForEveryPath_withReturn() {
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    BlockStatement block = new BlockStatement(ret);
    assertTrue(block.containsAReturnForEveryPath());
  }

  @Test
  public void containsUnreachableCode_disabled() {
    BlockStatement block = new BlockStatement();
    block.isEnabled.setValue(false);
    assertFalse(block.containsUnreachableCode());
  }

  @Test
  public void containsUnreachableCode_empty() {
    BlockStatement block = new BlockStatement();
    assertFalse(block.containsUnreachableCode());
  }

  @Test
  public void containsUnreachableCode_returnFollowedByStatement() {
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    // Comment.isEnabledNonComment() returns false, so use an ExpressionStatement
    ExpressionStatement after = new ExpressionStatement(new NullLiteral());
    BlockStatement block = new BlockStatement(ret, after);
    assertTrue(block.containsUnreachableCode());
  }

  @Test
  public void containsUnreachableCode_noReturnNoUnreachable() {
    Comment c1 = new Comment();
    Comment c2 = new Comment();
    BlockStatement block = new BlockStatement(c1, c2);
    assertFalse(block.containsUnreachableCode());
  }

  // ── UserArrayType ──────────────────────────────────────────

  @Test
  public void userArrayTypeInstance() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("MyType");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    assertNotNull(uat);
    assertEquals(leafType, uat.getLeafType());
    assertEquals(1, uat.getDimensionCount());
  }

  @Test
  public void userArrayTypeSameInstanceReturned() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("MySingleton");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType a = UserArrayType.getInstance(leafType, 2);
    UserArrayType b = UserArrayType.getInstance(leafType, 2);
    assertSame(a, b);
  }

  @Test
  public void userArrayTypeIsAlwaysArray() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("T");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    assertTrue(uat.isArray());
  }

  @Test
  public void userArrayTypeNameAppendsBrackets() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("MyClass");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat1 = UserArrayType.getInstance(leafType, 1);
    assertEquals("MyClass[]", uat1.getName());
    UserArrayType uat2 = UserArrayType.getInstance(leafType, 2);
    assertEquals("MyClass[][]", uat2.getName());
  }

  @Test
  public void userArrayTypeComponentTypeDimension1() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("Leaf");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    assertSame(leafType, uat.getComponentType());
  }

  @Test
  public void userArrayTypeComponentTypeMultiDimensional() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("Leaf");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 3);
    AbstractType<?, ?, ?> comp = uat.getComponentType();
    assertTrue(comp instanceof UserArrayType);
    assertEquals(2, ((UserArrayType) comp).getDimensionCount());
  }

  @Test
  public void userArrayTypeIsUserAuthored() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("UA");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    assertTrue(UserArrayType.getInstance(leafType, 1).isUserAuthored());
  }

  @Test
  public void userArrayTypeNotPrimitive() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("NP");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    assertFalse(UserArrayType.getInstance(leafType, 1).isPrimitive());
  }

  @Test
  public void userArrayTypeNotEnum() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("NE");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    assertFalse(UserArrayType.getInstance(leafType, 1).isEnum());
  }

  @Test
  public void userArrayTypeDeclaredMembersEmpty() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("E");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    assertTrue(uat.getDeclaredConstructors().isEmpty());
    assertTrue(uat.getDeclaredFields().isEmpty());
    assertTrue(uat.getDeclaredMethods().isEmpty());
  }

  @Test
  public void userArrayTypeInterfaces() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("I");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    assertEquals(0, UserArrayType.getInstance(leafType, 1).getInterfaces().length);
  }

  @Test
  public void userArrayTypeKeywordFactoryNull() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("K");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    assertNull(UserArrayType.getInstance(leafType, 1).getKeywordFactoryType());
  }

  @Test
  public void userArrayTypeNamePropertyNull() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("N");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    assertNull(UserArrayType.getInstance(leafType, 1).getNamePropertyIfItExists());
  }

  @Test
  public void userArrayTypeGetPackage() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("P");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    assertEquals(leafType.getPackage(), uat.getPackage());
  }

  @Test
  public void userArrayTypeDelegatesModifiers() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("Mod");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    assertEquals(leafType.isInterface(), uat.isInterface());
    assertEquals(leafType.isAbstract(), uat.isAbstract());
    assertEquals(leafType.isFinal(), uat.isFinal());
    assertEquals(leafType.isStatic(), uat.isStatic());
    assertEquals(leafType.isStrictFloatingPoint(), uat.isStrictFloatingPoint());
  }

  @Test
  public void userArrayTypeGetArrayType() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("AT");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    AbstractType<?, ?, ?> arrayType = uat.getArrayType();
    assertTrue(arrayType instanceof UserArrayType);
    assertEquals(2, ((UserArrayType) arrayType).getDimensionCount());
  }

  @Test
  public void userArrayTypeAccessLevel() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("AL");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    assertEquals(leafType.getAccessLevel(), uat.getAccessLevel());
  }

  @Test
  public void userArrayTypeAssignableFromSelf() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("Self");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    assertTrue(uat.isAssignableFrom(uat));
  }

  @Test
  public void userArrayTypeSuperTypeWithJavaSuperType() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("JS");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    AbstractType<?, ?, ?> superType = uat.getSuperType();
    assertNotNull(superType);
  }

  @Test
  public void userArrayTypeFollowToSuperClassDesired() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("FC");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    assertEquals(leafType.isFollowToSuperClassDesired(), uat.isFollowToSuperClassDesired());
  }

  @Test
  public void userArrayTypeConsumptionBySubClass() {
    NamedUserType leafType = new NamedUserType();
    leafType.name.setValue("CS");
    leafType.superType.setValue(JavaType.OBJECT_TYPE);
    UserArrayType uat = UserArrayType.getInstance(leafType, 1);
    assertEquals(leafType.isConsumptionBySubClassDesired(), uat.isConsumptionBySubClassDesired());
  }

  // ── AbstractNode ──────────────────────────────────────────

  @Test
  public void nodeHasUniqueId() {
    Comment c1 = new Comment();
    Comment c2 = new Comment();
    assertNotNull(c1.getId());
    assertNotNull(c2.getId());
    assertNotEquals(c1.getId(), c2.getId());
  }

  @Test
  public void nodeSetId() {
    Comment c = new Comment();
    UUID custom = UUID.fromString("12345678-1234-1234-1234-123456789abc");
    c.setId(custom);
    assertEquals(custom, c.getId());
  }

  @Test
  public void nodeParentSetByStatementList() {
    BlockStatement block = new BlockStatement();
    Comment c = new Comment();
    block.statements.add(c);
    assertSame(block, c.getParent());
  }

  @Test
  public void nodeParentClearedOnRemove() {
    BlockStatement block = new BlockStatement();
    Comment c = new Comment();
    block.statements.add(c);
    assertSame(block, c.getParent());
    block.statements.remove(0);
    assertNull(c.getParent());
  }

  @Test
  public void getFirstAncestorAssignableTo_includesThis() {
    Comment c = new Comment();
    BlockStatement block = new BlockStatement(c);
    Statement found = c.getFirstAncestorAssignableTo(Statement.class, true);
    assertSame(c, found);
  }

  @Test
  public void getFirstAncestorAssignableTo_excludesThis() {
    Comment c = new Comment();
    BlockStatement block = new BlockStatement(c);
    BlockStatement found = c.getFirstAncestorAssignableTo(BlockStatement.class, false);
    assertSame(block, found);
  }

  @Test
  public void getFirstAncestorAssignableTo_noAncestor() {
    Comment c = new Comment();
    DoInOrder found = c.getFirstAncestorAssignableTo(DoInOrder.class, false);
    assertNull(found);
  }

  @Test
  public void getFirstAncestorAssignableTo_defaultExcludesThis() {
    Comment c = new Comment();
    Statement found = c.getFirstAncestorAssignableTo(Statement.class);
    assertNull(found);
  }

  @Test
  public void nodeGetRepr() {
    Comment c = new Comment();
    String repr = c.getRepr();
    assertNotNull(repr);
  }

  @Test
  public void nodeToString() {
    Comment c = new Comment();
    String s = c.toString();
    assertTrue(s.startsWith("Comment["));
    assertTrue(s.endsWith("]"));
  }

  @Test
  public void nodeGenerateLocalName() {
    BlockStatement block = new BlockStatement();
    String name = block.generateLocalName(null);
    assertEquals("unusedName", name);
  }

  @Test
  public void createDeclarationSet() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    type.superType.setValue(JavaType.OBJECT_TYPE);
    Set<AbstractDeclaration> decls = type.createDeclarationSet();
    assertNotNull(decls);
    assertFalse(decls.isEmpty());
  }

  @Test
  public void removeDeclarationsThatNeedToBeCopied() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("CopyType");
    type.superType.setValue(JavaType.OBJECT_TYPE);
    Set<AbstractDeclaration> decls = type.createDeclarationSet();
    UserMethod method = new UserMethod("m", Void.TYPE, new UserParameter[0], new BlockStatement());
    type.methods.add(method);
    Set<AbstractDeclaration> result = method.removeDeclarationsThatNeedToBeCopied(decls);
    assertNotNull(result);
  }

  @Test
  public void astLocalizerFactory() {
    assertNotNull(AbstractNode.getAstLocalizerFactory());
    AstLocalizerFactory original = AbstractNode.getAstLocalizerFactory();
    AbstractNode.setAstLocalizerFactory(original);
    assertSame(original, AbstractNode.getAstLocalizerFactory());
  }

  @Test
  public void crawlVisitsNodes() {
    BlockStatement block = new BlockStatement();
    Comment c = new Comment();
    block.statements.add(c);
    List<Crawlable> visited = new ArrayList<>();
    block.crawl(crawlable -> visited.add(crawlable), CrawlPolicy.EXCLUDE_REFERENCES_ENTIRELY);
    assertFalse(visited.isEmpty());
    assertTrue(visited.contains(block));
  }

  @Test
  public void crawlWithNullCriterion() {
    Comment c = new Comment();
    List<Crawlable> visited = new ArrayList<>();
    c.crawl(crawlable -> visited.add(crawlable), CrawlPolicy.EXCLUDE_REFERENCES_ENTIRELY, null);
    assertTrue(visited.contains(c));
  }

  // ── Element ─────────────────────────────────────────────

  @Test
  public void addRemovePropertyListener() {
    Comment c = new Comment();
    final int[] count = {0};
    PropertyListener listener = e -> count[0]++;
    c.addPropertyListener(listener);
    c.text.setValue("hello");
    assertTrue(count[0] > 0);
    int countBefore = count[0];
    c.removePropertyListener(listener);
    c.text.setValue("world");
    assertEquals(countBefore, count[0]);
  }

  @Test
  public void accessPropertyListeners() {
    Comment c = new Comment();
    PropertyListener l = e -> {};
    c.addPropertyListener(l);
    boolean found = false;
    for (PropertyListener pl : c.accessPropertyListeners()) {
      if (pl == l) found = true;
    }
    assertTrue(found);
  }

  @Test
  public void getPropertyNamed() {
    Comment c = new Comment();
    InstanceProperty<?> prop = c.getPropertyNamed("Text");
    assertNotNull(prop);
    assertSame(c.text, prop);
  }

  @Test
  public void getPropertyNamedNotFound() {
    Comment c = new Comment();
    assertNull(c.getPropertyNamed("Nonexistent"));
  }

  @Test
  public void getProperties() {
    Comment c = new Comment();
    List<InstanceProperty<?>> props = c.getProperties();
    assertNotNull(props);
    assertFalse(props.isEmpty());
  }

  @Test
  public void lookupNameFor() {
    Comment c = new Comment();
    String name = c.lookupNameFor(c.text);
    assertEquals("text", name);
  }

  @Test
  public void lookupNameForUnknownProperty() {
    Comment c = new Comment();
    Comment other = new Comment();
    String name = c.lookupNameFor(other.text);
    assertNull(name);
  }

  @Test
  public void isEquivalentToSelf() {
    Comment c = new Comment();
    c.text.setValue("test");
    assertTrue(c.isEquivalentTo(c));
  }

  @Test
  public void isEquivalentToNonElement() {
    Comment c = new Comment();
    assertFalse(c.isEquivalentTo("not an element"));
  }

  @Test
  public void isEquivalentToSameContent() {
    Comment c1 = new Comment();
    c1.text.setValue("same");
    Comment c2 = new Comment();
    c2.text.setValue("same");
    assertTrue(c1.isEquivalentTo(c2));
  }

  @Test
  public void isEquivalentToDifferentContent() {
    Comment c1 = new Comment();
    c1.text.setValue("one");
    Comment c2 = new Comment();
    c2.text.setValue("two");
    assertFalse(c1.isEquivalentTo(c2));
  }

  @Test
  public void safeAppendReprWithAbstractNode() {
    Comment c = new Comment();
    StringBuilder sb = new StringBuilder();
    AstLocalizer localizer = AbstractNode.getAstLocalizerFactory().createInstance(sb);
    AbstractNode.safeAppendRepr(localizer, c);
    assertTrue(sb.length() > 0);
  }

  @Test
  public void safeAppendReprWithNull() {
    StringBuilder sb = new StringBuilder();
    AstLocalizer localizer = AbstractNode.getAstLocalizerFactory().createInstance(sb);
    AbstractNode.safeAppendRepr(localizer, null);
    assertTrue(sb.length() > 0);
  }

  @Test
  public void parentSetViaNodeProperty() {
    DoInOrder doInOrder = new DoInOrder(new BlockStatement());
    assertNotNull(doInOrder.body.getValue());
    assertSame(doInOrder, doInOrder.body.getValue().getParent());
  }

  @Test
  public void parentClearedViaNodePropertyChange() {
    BlockStatement body1 = new BlockStatement();
    BlockStatement body2 = new BlockStatement();
    DoInOrder doInOrder = new DoInOrder(body1);
    assertSame(doInOrder, body1.getParent());
    doInOrder.body.setValue(body2);
    assertNull(body1.getParent());
    assertSame(doInOrder, body2.getParent());
  }
}
