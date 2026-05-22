package org.alice.ide.x;

import org.alice.ide.testsupport.AbstractLoadedClassStructureTest;

public class ClipboardAstI18nFactoryStructureTest extends AbstractLoadedClassStructureTest {
  @Override
  protected String getClassName() {
    return "org.alice.ide.x.ClipboardAstI18nFactory";
  }

  @Override
  protected String getExpectedSuperclassSimpleName() {
    return "Object";
  }

  @Override
  protected boolean isExpectedAbstract() {
    return false;
  }

  @Override
  protected int getMinimumDeclaredMethodCount() {
    return 0;
  }

  @Override
  protected int getMinimumDeclaredConstructorCount() {
    return 1;
  }
}
