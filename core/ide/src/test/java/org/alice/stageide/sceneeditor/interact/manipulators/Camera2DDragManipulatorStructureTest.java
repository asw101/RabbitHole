package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.ide.testsupport.AbstractLoadedClassStructureTest;

public class Camera2DDragManipulatorStructureTest extends AbstractLoadedClassStructureTest {
  @Override
  protected String getClassName() {
    return "org.alice.stageide.sceneeditor.interact.manipulators.Camera2DDragManipulator";
  }

  @Override
  protected String getExpectedSuperclassSimpleName() {
    return "CameraManipulator2D";
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
  }}
