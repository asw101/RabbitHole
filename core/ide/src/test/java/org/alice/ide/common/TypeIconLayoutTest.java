package org.alice.ide.common;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TypeIconLayoutTest {
  @Test
  public void countDisplayableMembers_countsFieldsAndOnlyUnmanagedMethods() {
    NamedUserType userType = new NamedUserType();
    userType.fields.add(new UserField("firstField", JavaType.getInstance(String.class), null));
    userType.fields.add(new UserField("secondField", JavaType.getInstance(Integer.class), null));

    UserMethod visibleMethod = new UserMethod("visible", JavaType.VOID_TYPE, new org.lgna.project.ast.UserParameter[] {}, new BlockStatement());
    UserMethod managedMethod = new UserMethod("managed", JavaType.VOID_TYPE, new org.lgna.project.ast.UserParameter[] {}, new BlockStatement());
    managedMethod.managementLevel.setValue(ManagementLevel.MANAGED);

    userType.methods.add(visibleMethod);
    userType.methods.add(managedMethod);

    assertEquals(3, TypeIconLayout.countDisplayableMembers(userType));
  }

  @Test
  public void getBonusText_returnsNullWhenIndentIsDisabled() {
    NamedUserType userType = new NamedUserType();
    userType.fields.add(new UserField("field", JavaType.getInstance(String.class), null));

    assertNull(TypeIconLayout.getBonusText(userType, false));
  }

  @Test
  public void getBonusText_formatsVisibleMemberCount() {
    NamedUserType userType = new NamedUserType();
    userType.fields.add(new UserField("field", JavaType.getInstance(String.class), null));

    assertEquals("(1)", TypeIconLayout.getBonusText(userType, true));
  }

  @Test
  public void calculateExtraWidth_addsGapBonusWidthAndDepthIndent() {
    assertEquals(34, TypeIconLayout.calculateExtraWidth(true, 6, 2));
  }

  @Test
  public void calculateExtraWidth_returnsZeroWhenIndentIsDisabled() {
    assertEquals(0, TypeIconLayout.calculateExtraWidth(false, 99, 3));
  }
}
