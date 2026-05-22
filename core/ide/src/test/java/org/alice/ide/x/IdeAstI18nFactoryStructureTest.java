package org.alice.ide.x;

import org.alice.ide.testsupport.AbstractLoadedClassStructureTest;

public class IdeAstI18nFactoryStructureTest extends AbstractLoadedClassStructureTest {
  @Override
  protected String getClassName() {
    return "org.alice.ide.x.IdeAstI18nFactory";
  }

  @Override
  protected String getExpectedSuperclassSimpleName() {
    return "AstI18nFactory";
  }

  @Override
  protected boolean isExpectedAbstract() {
    return true;
  }

  @Override
  protected int getMinimumDeclaredMethodCount() {
    return 1;
  }

  @Override
  protected int getMinimumDeclaredConstructorCount() {
    return 1;
  }
}
