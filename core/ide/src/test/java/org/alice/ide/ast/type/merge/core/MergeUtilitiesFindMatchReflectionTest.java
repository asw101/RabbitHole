package org.alice.ide.ast.type.merge.core;

import org.junit.Test;
import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import static org.junit.Assert.assertSame;

public class MergeUtilitiesFindMatchReflectionTest {
  @Test
  public void privateFindMatch_recursesSuperTypeForMethodsAndFields() throws Exception {
    NamedUserType base = MergeUtilitiesTestSupport.namedType("Base");
    UserMethod baseMethod = MergeUtilitiesTestSupport.addMethod(base, "ping");
    UserField baseField = MergeUtilitiesTestSupport.addField(base, "value", org.lgna.project.ast.JavaType.getInstance(String.class));
    NamedUserType child = MergeUtilitiesTestSupport.namedType("Child");
    child.superType.setValue(base);

    AbstractMethod matchedMethod = MergeUtilitiesTestSupport.invokeFindMethodMatch(child, baseMethod);
    AbstractField matchedField = MergeUtilitiesTestSupport.invokeFindFieldMatch(child, baseField);

    assertSame(baseMethod, matchedMethod);
    assertSame(baseField, matchedField);
  }
}
