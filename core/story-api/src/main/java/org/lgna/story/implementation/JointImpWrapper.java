/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.render.gl.imp.adapters.AdapterFactory;
import edu.cmu.cs.dennisc.scenegraph.*;
import edu.cmu.cs.dennisc.scenegraph.bound.CumulativeBound;
import org.alice.math.immutable.*;
import org.lgna.story.SJoint;
import org.lgna.story.resources.JointId;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Wraps an inner JointImp to support resource swapping without changing the
 * wrapper identity seen by the rest of the system. Extracted from
 * JointHierarchyManager to keep that file focused on hierarchy logic.
 */
class JointImpWrapper extends JointImp {

  private final BooleanSupplier isSimsSupplier;
  private JointImp internalJointImp;
  private JointImp jointParentWrapper;
  private final List<JointImp> jointChildrenWrapper = new ArrayList<>();

  JointImpWrapper(JointedModelImp<?, ?> jointedModelImp, JointImp joint, BooleanSupplier isSimsSupplier) {
    super(jointedModelImp);
    this.internalJointImp = joint;
    this.isSimsSupplier = isSimsSupplier;
  }

  @Override
  public final void setAbstraction(SJoint abstraction) {
    super.setAbstraction(abstraction);
    if (this.internalJointImp != null) {
      this.internalJointImp.setAbstraction(abstraction);
    }
  }

  @Override
  public JointImp getJointParent() {
    return jointParentWrapper;
  }

  @Override
  public List<JointImp> getJointChildren() {
    return jointChildrenWrapper;
  }

  @Override
  void setJointParent(JointImp jointParent) {
    if (this.jointParentWrapper != null) {
      this.jointParentWrapper.getJointChildren().remove(this);
    }
    this.jointParentWrapper = jointParent;
    if (this.jointParentWrapper != null) {
      jointParent.getJointChildren().add(this);
    }
  }

  @Override
  public void setScale(Dimension3 scale) {
    internalJointImp.setScale(scale);
  }

  @Override
  public boolean isReoriented() {
    return internalJointImp.isReoriented();
  }

  @Override
  public boolean isRelocated() {
    return internalJointImp.isRelocated();
  }

  @Override
  protected void copyOnto(JointImp newJoint) {
    internalJointImp.copyOnto(newJoint);
    if (isSimsSupplier.getAsBoolean()) {
      // Alice models reuse joints, but Sims regenerate them.
      // Creating the adapter is the public mechanism to rebuild internal listeners and show joint changes
      AdapterFactory.getAdapterFor(newJoint.getSgComposite());
    }
    if (getAbstraction() != null) {
      newJoint.setAbstraction(getAbstraction());
    }
  }

  @Override
  public String getName() {
    return internalJointImp.getJointId().toString();
  }

  @Override
  public SceneImp getScene() {
    return this.internalJointImp.getScene();
  }

  @Override
  public JointId getJointId() {
    return internalJointImp.getJointId();
  }

  @Override
  public boolean isFreeInX() {
    return internalJointImp.isFreeInX();
  }

  @Override
  public boolean isFreeInY() {
    return internalJointImp.isFreeInY();
  }

  @Override
  public boolean isFreeInZ() {
    return internalJointImp.isFreeInZ();
  }

  void replaceWithJoint(JointImp newJoint) {
    copyOnto(newJoint);
    AbstractTransformable oldSgComposite = internalJointImp.getSgComposite();
    AbstractTransformable newSgComposite = newJoint.getSgComposite();
    for (Component child : oldSgComposite.getComponents()) {
      if (!(child instanceof ModelJoint)) {
        child.setParent(newJoint.getSgComposite());
      }
    }
    // Sims models, with regenerated joints, need to be reconnected at their root, indicated by a null parent.
    if (newSgComposite.getParent() == null) {
      // Without this, things riding on the Sim or its joints when the resource changes will go out of the scene graph and disappear.
      newSgComposite.setParent(oldSgComposite.getParent());
    }
    internalJointImp = newJoint;
  }

  @Override
  public AxisAlignedBox getAxisAlignedMinimumBoundingBox(ReferenceFrame asSeenBy) {
    return internalJointImp.getAxisAlignedMinimumBoundingBox(asSeenBy);
  }

  @Override
  public AbstractTransformable getSgComposite() {
    return internalJointImp.getSgComposite();
  }

  @Override
  protected void updateCumulativeBound(CumulativeBound rv, AffineMatrix4x4 trans) {
    internalJointImp.updateCumulativeBound(rv, trans);
  }

  @Override
  public UnitQuaternion getOriginalOrientation() {
    return internalJointImp.getOriginalOrientation();
  }

  @Override
  public AffineMatrix4x4 getScaledOriginalTransformation() {
    return internalJointImp.getScaledOriginalTransformation();
  }

  @Override
  public AffineMatrix4x4 getLocalTransformation() {
    return internalJointImp.getLocalTransformation();
  }

  @Override
  public void setLocalTransformation(AffineMatrix4x4 transformation) {
    internalJointImp.setLocalTransformation(transformation);
  }

  @Override
  protected void postCheckSetVehicle(EntityImp vehicle) {
    internalJointImp.postCheckSetVehicle(vehicle);
  }

  @Override
  public boolean isFacing(EntityImp other) {
    return this.internalJointImp.isFacing(other);
  }

  @Override
  public void applyTranslation(double x, double y, double z, ReferenceFrame asSeenBy) {
    this.internalJointImp.applyTranslation(x, y, z, asSeenBy);
  }

  @Override
  public void applyRotationInRadians(Vector3 axis, double angleInRadians, ReferenceFrame asSeenBy) {
    this.internalJointImp.applyRotationInRadians(axis, angleInRadians, asSeenBy);
  }

  @Override
  public boolean isPivotVisible() {
    return internalJointImp.isPivotVisible();
  }

  @Override
  public void setPivotVisible(boolean isPivotVisible) {
    internalJointImp.setPivotVisible(isPivotVisible);
  }
}
