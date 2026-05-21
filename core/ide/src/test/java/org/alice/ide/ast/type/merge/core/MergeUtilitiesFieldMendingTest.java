package org.alice.ide.ast.type.merge.core;

import org.junit.Test;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.assertSame;

public class MergeUtilitiesFieldMendingTest {
  @Test
  public void mendMethodInvocationsAndFieldAccesses_retargetsFieldAccessToLocalTypeMatch() {
    NamedUserType external = MergeUtilitiesTestSupport.namedType("External");
    UserField externalField = MergeUtilitiesTestSupport.addField(external, "sharedField", org.lgna.project.ast.JavaType.getInstance(String.class));
    NamedUserType local = MergeUtilitiesTestSupport.namedType("Local");
    UserField localField = MergeUtilitiesTestSupport.addField(local, "sharedField", org.lgna.project.ast.JavaType.getInstance(String.class));
    FieldAccess access = MergeUtilitiesTestSupport.access(externalField);
    MethodInvocation wrapper = new MethodInvocation(access, MergeUtilitiesTestSupport.objectToString());
    MergeUtilitiesTestSupport.addMethod(local, "host").body.getValue().statements.add(new ExpressionStatement(wrapper));

    MergeUtilities.mendMethodInvocationsAndFieldAccesses(local);

    assertSame(localField, access.field.getValue());
  }
}
