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


}
