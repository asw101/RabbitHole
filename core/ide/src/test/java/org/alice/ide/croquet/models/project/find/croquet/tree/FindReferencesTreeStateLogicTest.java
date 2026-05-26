package org.alice.ide.croquet.models.project.find.croquet.tree;

import org.alice.ide.croquet.models.project.find.core.SearchResult;
import org.alice.ide.croquet.models.project.find.croquet.tree.nodes.SearchTreeNode;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserLambda;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FindReferencesTreeStateLogicTest {
  @Test
  public void groupReferences_groupsByOwningDeclarationAndSkipsOrphans() {
    SearchResult result = new SearchResult(new UserMethod("target", void.class, new UserParameter[0], new BlockStatement()));
    UserMethod ownerMethod = new UserMethod("owner", void.class, new UserParameter[0], new BlockStatement());
    MethodInvocation firstMethodReference = new MethodInvocation(new NullLiteral(), JavaMethod.getInstance(Object.class, "toString"));
    MethodInvocation secondMethodReference = new MethodInvocation(new NullLiteral(), JavaMethod.getInstance(Object.class, "hashCode"));
    ownerMethod.body.getValue().statements.add(new ExpressionStatement(firstMethodReference));
    ownerMethod.body.getValue().statements.add(new ExpressionStatement(secondMethodReference));

    UserLambda ownerLambda = new UserLambda(void.class, new UserParameter[0], new BlockStatement());
    MethodInvocation lambdaReference = new MethodInvocation(new NullLiteral(), JavaMethod.getInstance(Object.class, "notify"));
    ownerLambda.body.getValue().statements.add(new ExpressionStatement(lambdaReference));

    MethodInvocation orphanReference = new MethodInvocation(new NullLiteral(), JavaMethod.getInstance(Object.class, "wait"));

    result.addReference(firstMethodReference);
    result.addReference(secondMethodReference);
    result.addReference(lambdaReference);
    result.addReference(orphanReference);

    List<FindReferencesTreeStateLogic.ReferenceGroup> groups = FindReferencesTreeStateLogic.groupReferences(result);

    assertEquals(2, groups.size());
    assertSame(ownerMethod, groups.get(0).getDeclaration());
    assertEquals(List.of(firstMethodReference, secondMethodReference), groups.get(0).getReferences());
    assertSame(ownerLambda, groups.get(1).getDeclaration());
    assertEquals(List.of(lambdaReference), groups.get(1).getReferences());
  }

  @Test
  public void groupReferencesReturnsEmptyListForNullSearchObject() {
    assertTrue(FindReferencesTreeStateLogic.groupReferences(null).isEmpty());
  }

  @Test
  public void treeNavigation_movesBetweenDeclarationAndReferenceRows() {
    SearchTreeNode root = new SearchTreeNode(null);
    SearchTreeNode firstDeclaration = new SearchTreeNode(root);
    SearchTreeNode firstReference = new SearchTreeNode(firstDeclaration);
    firstDeclaration.addChild(firstReference);
    root.addChild(firstDeclaration);
    SearchTreeNode secondDeclaration = new SearchTreeNode(root);
    SearchTreeNode secondReference = new SearchTreeNode(secondDeclaration);
    secondDeclaration.addChild(secondReference);
    root.addChild(secondDeclaration);

    assertSame(firstReference, FindReferencesTreeStateLogic.moveSelectedUpOne(root, secondDeclaration));
    assertSame(secondDeclaration, FindReferencesTreeStateLogic.moveSelectedDownOne(root, firstReference));
    assertSame(secondDeclaration, FindReferencesTreeStateLogic.selectAtCoordinates(root, 1, -1));
    assertSame(secondReference, FindReferencesTreeStateLogic.selectAtCoordinates(root, 1, 0));
    assertTrue(FindReferencesTreeStateLogic.hasRows(root));
    assertSame(firstDeclaration, FindReferencesTreeStateLogic.getTopValue(root));
  }

  @Test
  public void treeNavigation_handlesBoundarySelections() {
    SearchTreeNode root = new SearchTreeNode(null);
    SearchTreeNode firstDeclaration = new SearchTreeNode(root);
    SearchTreeNode firstReference = new SearchTreeNode(firstDeclaration);
    firstDeclaration.addChild(firstReference);
    root.addChild(firstDeclaration);
    SearchTreeNode secondDeclaration = new SearchTreeNode(root);
    SearchTreeNode secondReference = new SearchTreeNode(secondDeclaration);
    SearchTreeNode thirdReference = new SearchTreeNode(secondDeclaration);
    secondDeclaration.addChild(secondReference);
    secondDeclaration.addChild(thirdReference);
    root.addChild(secondDeclaration);

    assertSame(firstDeclaration, FindReferencesTreeStateLogic.moveSelectedUpOne(root, firstDeclaration));
    assertSame(secondDeclaration, FindReferencesTreeStateLogic.moveSelectedUpOne(root, secondReference));
    assertSame(firstReference, FindReferencesTreeStateLogic.moveSelectedDownOne(root, firstDeclaration));
    assertSame(thirdReference, FindReferencesTreeStateLogic.moveSelectedDownOne(root, secondReference));
    assertSame(thirdReference, FindReferencesTreeStateLogic.moveSelectedDownOne(root, thirdReference));
  }

  @Test
  public void emptyTreeReportsNoRows() {
    assertFalse(FindReferencesTreeStateLogic.hasRows(new SearchTreeNode(null)));
  }

  @Test
  public void getSelectedCoordinates_distinguishesDeclarationAndReferenceRows() {
    SearchTreeNode root = new SearchTreeNode(null);
    SearchTreeNode declaration = new SearchTreeNode(root);
    SearchTreeNode reference = new SearchTreeNode(declaration);
    declaration.addChild(reference);
    root.addChild(declaration);

    FindReferencesTreeStateLogic.TreeCoordinate declarationCoordinate = FindReferencesTreeStateLogic.getSelectedCoordinates(root, declaration);
    FindReferencesTreeStateLogic.TreeCoordinate referenceCoordinate = FindReferencesTreeStateLogic.getSelectedCoordinates(root, reference);

    assertEquals(0, declarationCoordinate.getA());
    assertEquals(-1, declarationCoordinate.getB());
    assertEquals(0, referenceCoordinate.getA());
    assertEquals(0, referenceCoordinate.getB());
  }
}
