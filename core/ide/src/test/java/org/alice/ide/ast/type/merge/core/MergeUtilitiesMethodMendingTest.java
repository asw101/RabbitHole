package org.alice.ide.ast.type.merge.core;

import org.junit.Test;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;

import static org.junit.Assert.assertSame;

public class MergeUtilitiesMethodMendingTest {
  @Test
  public void mendMethodInvocationsAndFieldAccesses_retargetsMethodToLocalTypeMatch() {
    NamedUserType external = MergeUtilitiesTestSupport.namedType("External");
    UserMethod externalMethod = MergeUtilitiesTestSupport.addMethod(external, "perform");
    NamedUserType local = MergeUtilitiesTestSupport.namedType("Local");
    UserMethod localMethod = MergeUtilitiesTestSupport.addMethod(local, "perform");
    MethodInvocation invocation = MergeUtilitiesTestSupport.invocation(externalMethod);
    local.methods.get(0).body.getValue().statements.add(new ExpressionStatement(invocation));

    MergeUtilities.mendMethodInvocationsAndFieldAccesses(local);

    assertSame(localMethod, invocation.method.getValue());
  }
}
