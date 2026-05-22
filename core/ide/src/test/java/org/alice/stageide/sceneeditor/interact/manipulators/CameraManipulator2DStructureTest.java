package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.ide.testsupport.AbstractLoadedClassStructureTest;

public class CameraManipulator2DStructureTest extends AbstractLoadedClassStructureTest {
  @Override
  protected String getClassName() {
    return "org.alice.stageide.sceneeditor.interact.manipulators.CameraManipulator2D";
  }

  @Override
  protected String getExpectedSuperclassSimpleName() {
    return "CameraManipulator";
  }

  @Override
  protected boolean isExpectedAbstract() {
    return true;
  }

  @Override
  protected int getMinimumDeclaredMethodCount() {
    return 0;
  }

  @Override
  protected int getMinimumDeclaredConstructorCount() {
    return 1;
  }}
