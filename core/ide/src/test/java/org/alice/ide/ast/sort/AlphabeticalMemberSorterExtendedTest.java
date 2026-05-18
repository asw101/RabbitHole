package org.alice.ide.ast.sort;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.UserMethod;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AlphabeticalMemberSorterExtendedTest {

  private UserMethod method(String name) {
    UserMethod m = new UserMethod();
    m.name.setValue(name);
    m.returnType.setValue(JavaType.VOID_TYPE);
    m.managementLevel.setValue(ManagementLevel.NONE);
    return m;
  }

  // Only unique tests remain here — duplicates moved to AlphabeticalMemberSorterTest

  @Test
  public void sorting_specialCharacterNames() {
    UserMethod m1 = method("_private");
    UserMethod m2 = method("public");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(m2, m1));
    assertEquals("_private", sorted.get(0).getName());
  }
}
