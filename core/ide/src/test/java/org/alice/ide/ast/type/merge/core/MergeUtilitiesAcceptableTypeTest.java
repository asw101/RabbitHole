package org.alice.ide.ast.type.merge.core;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MergeUtilitiesAcceptableTypeTest {
  @Test
  public void isAcceptableType_acceptsJavaTypesAndKnownNamedUserTypes() throws Exception {
    NamedUserType local = MergeUtilitiesTestSupport.namedType("Local");
    NamedUserType remote = MergeUtilitiesTestSupport.namedType("Remote");

    assertTrue(MergeUtilitiesTestSupport.invokeIsAcceptableType(JavaType.getInstance(String.class), MergeUtilitiesTestSupport.list(local)));
    assertTrue(MergeUtilitiesTestSupport.invokeIsAcceptableType(local, MergeUtilitiesTestSupport.list(local)));
    assertFalse(MergeUtilitiesTestSupport.invokeIsAcceptableType(remote, MergeUtilitiesTestSupport.list(local)));
    assertFalse(MergeUtilitiesTestSupport.invokeIsAcceptableType(null, MergeUtilitiesTestSupport.list(local)));
  }
}
