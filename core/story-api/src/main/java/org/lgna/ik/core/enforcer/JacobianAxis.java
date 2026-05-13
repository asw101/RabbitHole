package org.lgna.ik.core.enforcer;

import org.lgna.story.implementation.JointImp;

class JacobianAxis {
  final JointImp jointImp;
  final int axisInBoneIndex;
  private boolean isFree = true;

  public JacobianAxis(JointImp jointImp, int axisInBoneIndex) {
    this.jointImp = jointImp;
    this.axisInBoneIndex = axisInBoneIndex;
  }

  public boolean isFree() {
    return isFree;
  }

  public void setFree(boolean isFree) {
    this.isFree = isFree;
  }

  public void applyCorrespondingSingleDelta(double delta) {
    throw new RuntimeException("Not completed method"); // TODO Auto-generated method stub
  }

  public boolean wentOverLimit() {
    throw new RuntimeException("Not implemented method"); // TODO Auto-generated method stub
  }

  public double setToLimitAndReturnTheDifference() {
    throw new RuntimeException("Not implemented method"); // TODO Auto-generated method stub
  }
}
