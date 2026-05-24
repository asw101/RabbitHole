/*
 * Copyright (c) 2006-2010, Carnegie Mellon University. All rights reserved.
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
 */

package test.ik;

import edu.cmu.cs.dennisc.print.PrintUtilities;
import edu.cmu.cs.dennisc.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.dennisc.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.dennisc.ui.lookingglass.CameraNavigationDragAdapter;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.lgna.croquet.State;
import org.lgna.ik.core.IkConstants;
import org.lgna.ik.core.enforcer.JointedModelIkEnforcer;
import org.lgna.ik.core.enforcer.TightPositionalIkEnforcer;
import org.lgna.ik.core.enforcer.PositionConstraint;
import org.lgna.ik.core.solver.Bone;
import org.lgna.story.Position;
import org.lgna.story.SBiped;
import org.lgna.story.SCamera;
import org.lgna.story.SProgram;
import org.lgna.story.SSphere;
import org.lgna.story.implementation.AsSeenBy;
import org.lgna.story.implementation.JointImp;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.implementation.SphereImp;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.DynamicBipedResource;
import org.lgna.story.resources.JointId;
import test.ik.croquet.*;



/**
 * @author Dennis Cosgrove
 */
class IkProgram extends SProgram {
  private final SCamera camera = new SCamera();
  private final SBiped ogre = new SBiped(new DynamicBipedResource("ogre", "ogre"));
  private final SSphere target = new SSphere();
  private final IkScene scene = new IkScene(camera, ogre, target);
  private final CameraNavigationDragAdapter cameraNavigationDragAdapter = new CameraNavigationDragAdapter();
  private final NiceDragAdapter modelManipulationDragAdapter = new NiceDragAdapter();

  private final State.ValueListener<Boolean> linearAngularEnabledListener = new State.ValueListener<Boolean>() {
    @Override
    public void changing(State<Boolean> state, Boolean prevValue, Boolean nextValue) {
    }

    @Override
    public void changed(State<Boolean> state, Boolean prevValue, Boolean nextValue) {
    }
  };
  private final State.ValueListener<JointId> jointIdListener = new State.ValueListener<JointId>() {
    @Override
    public void changing(State<JointId> state, JointId prevValue, JointId nextValue) {
      IkProgram.this.handleChainChanging();
    }

    @Override
    public void changed(State<JointId> state, JointId prevValue, JointId nextValue) {
      IkProgram.this.handleChainChanged();
    }
  };
  //SOLVER this is for printing out the chain
  private final State.ValueListener<Bone> boneListener = new State.ValueListener<Bone>() {
    @Override
    public void changing(State<Bone> state, Bone prevValue, Bone nextValue) {
    }

    @Override
    public void changed(State<Bone> state, Bone prevValue, Bone nextValue) {
      IkProgram.this.handleBoneChanged();
    }
  };
  private final AbsoluteTransformationListener targetTransformListener = new AbsoluteTransformationListener() {
    @Override
    public void absoluteTransformationChanged(AbsoluteTransformationEvent absoluteTransformationEvent) {
      IkProgram.this.handleTargetTransformChanged();
    }
  };
  private JointedModelIkEnforcer ikEnforcer;
  private TightPositionalIkEnforcer tightIkEnforcer;

  private boolean useTightIkEnforcer = false;
  private PositionConstraint myPositionConstraint;

  private SphereImp getTargetImp() {
    return target.getImplementation();
  }

  private JointedModelImp<?, ?> getSubjectImp() {
    return ogre.getImplementation();
  }

  private JointImp getAnchorImp() {
    JointId anchorId = AnchorJointIdState.getInstance().getValue();
    return this.getSubjectImp().getJointImplementation(anchorId);
  }

  private JointImp getEndImp() {
    JointId endId = EndJointIdState.getInstance().getValue();
    return this.getSubjectImp().getJointImplementation(endId);
  }

  private void handleTargetTransformChanged() {
    //    edu.cmu.cs.dennisc.math.AffineMatrix4x4 m = this.getTargetImp().getTransformation( this.getAnchorImp() );
    //    edu.cmu.cs.dennisc.print.PrintUtilities.printlns( m );
    this.updateInfo();
  }

  //SOLVER this prints to the yellow area right under the chain display
  private void updateInfo() {
    Bone bone = BonesState.getInstance().getValue();
    String jointIdText = null;
    String jointTransformText = null;
    if (bone != null) {
      JointImp joint = bone.getA();
      jointIdText = joint.getJointId().toString();
      StringBuilder jointTransform = new StringBuilder();
      PrintUtilities.appendLines(jointTransform, joint.getLocalTransformation());
      jointTransformText = jointTransform.toString();
    }
    StringBuilder targetTransform = new StringBuilder();
    PrintUtilities.appendLines(targetTransform, this.getTargetImp().getLocalTransformation());
    InfoState.getInstance().setValueTransactionlessly(IkProgramLogic.buildInfoText(jointIdText, jointTransformText, targetTransform.toString()));
  }

  protected void handleChainChanging() {
    JointId endId = EndJointIdState.getInstance().getValue();
    JointId anchorId = AnchorJointIdState.getInstance().getValue();

    if ((endId != null) && (anchorId != null)) {
      if (useTightIkEnforcer) {
        //TODO
        //should I remove? depends. when is this called?
      } else {
        ikEnforcer.clearChainBetween(anchorId, endId);
      }
    }
  }

  private void handleChainChanged() {
    JointId endId = EndJointIdState.getInstance().getValue();
    JointId anchorId = AnchorJointIdState.getInstance().getValue();

    if ((endId != null) && (anchorId != null)) {
      if (useTightIkEnforcer) {
        //TODO
      } else {
        ikEnforcer.setChainBetween(anchorId, endId);
      }
      setDragAdornmentsVisible(true);
      Point3 ap = getSubjectImp().getJointImplementation(anchorId).getAbsoluteTransformation().translation();
      scene.anchor.setPositionRelativeToVehicle(new Position(ap));
    } else {
      setDragAdornmentsVisible(false);
    }

    if (useTightIkEnforcer) {
      //TODO
    } else {
      BonesState.getInstance().setChain(ikEnforcer.getChainForPrinting(anchorId, endId));
    }

    //    updateInfo();
  }

  protected void targetDragStarted() {
  }

  private void setDragAdornmentsVisible(boolean visible) {
    if (visible) {
      scene.anchor.setVehicle(scene);
      scene.ee.setVehicle(scene);
    } else {
      scene.anchor.setVehicle(null);
      scene.ee.setVehicle(null);
    }
  }

  void initializeTest() {
    this.setActiveScene(this.scene);
    this.modelManipulationDragAdapter.setOnClickRunnable(new Runnable() {
      @Override
      public void run() {
        targetDragStarted();
      }
    });

    this.modelManipulationDragAdapter.setOnscreenRenderTarget(this.getImplementation().getOnscreenRenderTarget());
    this.cameraNavigationDragAdapter.setOnscreenRenderTarget(this.getImplementation().getOnscreenRenderTarget());
    this.cameraNavigationDragAdapter.requestTarget(new Point3(0.0, 1.0, 0.0));
    this.cameraNavigationDragAdapter.requestDistance(8.0);

    AnchorJointIdState.getInstance().addValueListener(this.jointIdListener);
    EndJointIdState.getInstance().addValueListener(this.jointIdListener);
    BonesState.getInstance().addValueListener(this.boneListener);
    IsLinearEnabledState.getInstance().addValueListener(this.linearAngularEnabledListener);
    IsAngularEnabledState.getInstance().addValueListener(this.linearAngularEnabledListener);

    this.getTargetImp().setTransformation(this.getEndImp());
    this.getTargetImp().getSgComposite().addAbsoluteTransformationListener(this.targetTransformListener);

    Thread calculateThread;

    if (useTightIkEnforcer) {
      calculateThread = initializeTightIkEnforcer();
    } else {
      calculateThread = initializeOldIkEnforcer();
    }

    this.handleChainChanged();

    calculateThread.setDaemon(true);
    calculateThread.start();
  }

  private Thread initializeOldIkEnforcer() {
    //    solver = new org.lgna.ik.solver.Solver();
    ikEnforcer = new JointedModelIkEnforcer(getSubjectImp());
    ikEnforcer.addFullBodyDefaultPoseUsingCurrentPose();

    //I'm setting joint weights here
    ikEnforcer.setDefaultJointWeight(1);
    ikEnforcer.setJointWeight(BipedResource.RIGHT_ELBOW, 2);

    //using ikEnforcer's methods rather than dealing with chains.

    Thread calculateThread = new Thread("IK-OldEnforcer") {
      @Override
      public void run() {
        final double maxLinearSpeedForEe = IkConstants.MAX_LINEAR_SPEED_FOR_EE;
        final double maxAngularSpeedForEe = IkConstants.MAX_ANGULAR_SPEED_FOR_EE;
        final double deltaTime = IkConstants.DESIRED_DELTA_TIME;

        while (!interrupted()) {
          //not bad concurrent programming practice
          boolean isLinearEnabled = IsLinearEnabledState.getInstance().getValue();
          boolean isAngularEnabled = IsAngularEnabledState.getInstance().getValue();

          //these could be multiple. in this app it is one pair.
          final JointId eeId = EndJointIdState.getInstance().getValue();
          final JointId anchorId = AnchorJointIdState.getInstance().getValue();

          if (ikEnforcer.hasActiveChain() && (isLinearEnabled || isAngularEnabled)) {
            //I could make chain setter not race with this
            //However, racing is fine, as long as the old chain is still valid. It is.

            AffineMatrix4x4 targetTransformation = getTargetImp().getTransformation(AsSeenBy.SCENE);
            if (isLinearEnabled) {
              ikEnforcer.setEeDesiredPosition(eeId, targetTransformation.translation(), maxLinearSpeedForEe);
            }

            if (isAngularEnabled) {
              ikEnforcer.setEeDesiredOrientation(eeId, targetTransformation.orientation(), maxAngularSpeedForEe);
            }

            ikEnforcer.advanceTime(deltaTime);

            Point3 ep = ikEnforcer.getEndEffectorPosition(eeId);
            Point3 ap = ikEnforcer.getAnchorPosition(anchorId);
            scene.anchor.setPositionRelativeToVehicle(new Position(ap));
            scene.ee.setPositionRelativeToVehicle(new Position(ep));
          }

          try {
            sleep(10);
          } catch (InterruptedException e) {
            break;
          }
        }
      }
    };
    return calculateThread;
  }

  //TODO need to populate constraints

  private Thread initializeTightIkEnforcer() {
    tightIkEnforcer = new TightPositionalIkEnforcer(getSubjectImp());

    Thread calculateThread = new Thread("IK-TightEnforcer") {
      @Override
      public void run() {
        while (!interrupted()) {

          //not bad concurrent programming practice
          boolean isLinearEnabled = IsLinearEnabledState.getInstance().getValue();
          boolean isAngularEnabled = IsAngularEnabledState.getInstance().getValue();

          //these could be multiple. in this app it is one pair.
          final JointId eeId = EndJointIdState.getInstance().getValue();
          final JointId anchorId = AnchorJointIdState.getInstance().getValue();

          AffineMatrix4x4 targetTransformation = getTargetImp().getTransformation(AsSeenBy.SCENE);

          myPositionConstraint.setEeDesiredPosition(targetTransformation.translation());

          //this enforces the constraints immediately right now. so, there is no talk about deltatime or speed
          //had I had a maximum rotational speed for joints, then having time would make sense
          tightIkEnforcer.enforceConstraints();

          try {
            sleep(10);
          } catch (InterruptedException e) {
            break;
          }
        }
      }
    };
    //TODO do what's below to complete it
    // set its chain

    JointId endId = EndJointIdState.getInstance().getValue();
    JointId anchorId = AnchorJointIdState.getInstance().getValue();

    int level = 0;
    myPositionConstraint = tightIkEnforcer.createPositionConstraint(level, anchorId, endId);

    return calculateThread;
  }

  private void handleBoneChanged() {
    this.updateInfo();
  }
}
