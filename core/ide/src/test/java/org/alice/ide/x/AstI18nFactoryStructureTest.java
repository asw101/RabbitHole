package org.alice.ide.x;

import org.alice.ide.testsupport.AbstractLoadedClassStructureTest;

public class AstI18nFactoryStructureTest extends AbstractLoadedClassStructureTest {
  @Override
  protected String getClassName() {
    return "org.alice.ide.x.AstI18nFactory";
  }

  @Override
  protected String getExpectedSuperclassSimpleName() {
    return "I18nFactory";
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
