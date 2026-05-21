package org.alice.ide.ast.type.merge.core;

import org.junit.Test;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;

import static org.junit.Assert.assertFalse;

public class MergeUtilitiesEquivalenceBodyTest {
  @Test
  public void isEquivalent_detectsDifferentGeneratedBodies() {
    NamedUserType left = MergeUtilitiesTestSupport.namedType("Left");
    UserMethod a = MergeUtilitiesTestSupport.addMethod(left, "perform");
    NamedUserType right = MergeUtilitiesTestSupport.namedType("Right");
    UserMethod b = MergeUtilitiesTestSupport.addMethod(right, "perform");
    b.body.getValue().statements.add(new ExpressionStatement(new MethodInvocation(new org.lgna.project.ast.NullLiteral(), MergeUtilitiesTestSupport.objectToString())));

    assertFalse(MergeUtilities.isEquivalent(a, b));
  }
}
