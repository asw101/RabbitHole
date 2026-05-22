package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.ide.testsupport.AbstractLoadedClassStructureTest;

public class OrthographicCameraDragStrafeManipulatorStructureTest extends AbstractLoadedClassStructureTest {
  @Override
  protected String getClassName() {
    return "org.alice.stageide.sceneeditor.interact.manipulators.OrthographicCameraDragStrafeManipulator";
  }

  @Override
  protected String getExpectedSuperclassSimpleName() {
    return "Camera2DDragManipulator";
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
  }}
