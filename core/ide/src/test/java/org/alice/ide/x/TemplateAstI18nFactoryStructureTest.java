package org.alice.ide.x;

import org.alice.ide.testsupport.AbstractLoadedClassStructureTest;

public class TemplateAstI18nFactoryStructureTest extends AbstractLoadedClassStructureTest {
  @Override
  protected String getClassName() {
    return "org.alice.ide.x.TemplateAstI18nFactory";
  }

  @Override
  protected String getExpectedSuperclassSimpleName() {
    return "IdeAstI18nFactory";
  }

  @Override
  protected boolean isExpectedAbstract() {
    return false;
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
