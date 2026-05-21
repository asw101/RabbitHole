package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeManagerPrivateCriteriaTest {
  @Test
  public void matchesNameCriterion_acceptsOnlyMatchingTypeNames() throws Exception {
    Class<?> criterionClass = Class.forName("org.alice.ide.typemanager.TypeManager$MatchesNameTypeCriterion");
    Constructor<?> ctor = criterionClass.getDeclaredConstructor(String.class);
    ctor.setAccessible(true);
    Object criterion = ctor.newInstance("WantedName");
    Method accept = criterionClass.getDeclaredMethod("accept", NamedUserType.class);
    accept.setAccessible(true);

    NamedUserType matching = new NamedUserType();
    matching.name.setValue("WantedName");
    matching.superType.setValue(JavaType.getInstance(Object.class));
    NamedUserType other = new NamedUserType();
    other.name.setValue("OtherName");
    other.superType.setValue(JavaType.getInstance(Object.class));

    assertTrue((Boolean) accept.invoke(criterion, matching));
    assertFalse((Boolean) accept.invoke(criterion, other));
  }
}
