package org.alice.ide.x;

import org.alice.ide.testsupport.AbstractLoadedClassStructureTest;

public class I18nFactoryStructureTest extends AbstractLoadedClassStructureTest {
  @Override
  protected String getClassName() {
    return "org.alice.ide.x.I18nFactory";
  }

  @Override
  protected String getExpectedSuperclassSimpleName() {
    return "Object";
  }

  @Override
  protected boolean isExpectedAbstract() {
    return true;
  }

  @Override
  protected int getMinimumDeclaredMethodCount() {
    return 5;
  }

  @Override
  protected int getMinimumDeclaredConstructorCount() {
    return 1;
  }
}
