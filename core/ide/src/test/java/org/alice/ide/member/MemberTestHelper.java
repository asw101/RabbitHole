package org.alice.ide.member;

import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;

/**
 * Shared utilities for member-package tests — eliminates repeated method-lookup boilerplate.
 */
final class MemberTestHelper {

  private MemberTestHelper() {
  }

  static JavaMethod methodNamed(Class<?> cls, String name) {
    JavaType type = JavaType.getInstance(cls);
    for (var m : type.getDeclaredMethods()) {
      if (name.equals(m.getName())) {
        return (JavaMethod) m;
      }
    }
    return null;
  }
}
