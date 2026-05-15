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

import edu.cmu.cs.dennisc.java.util.Maps;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import org.alice.math.immutable.Dimension3;
import org.alice.math.immutable.UnitQuaternion;
import org.lgna.ik.core.solver.Bone;
import org.lgna.story.implementation.JointedModelImp.TreeWalkObserver;
import org.lgna.story.resources.JointArrayId;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;

import java.util.*;

/**
 * Manages joint hierarchy: JointImpWrappers, joint maps, tree walking,
 * IK chain computation, and pose/straighten operations extracted from JointedModelImp.
 */
class JointHierarchyManager<R extends JointedModelResource> {

  private final JointedModelResourceBinder<R> resourceBinder;
  private final Map<JointId, JointImpWrapper> mapIdToJoint = Maps.newHashMap();
  private final Map<JointArrayId, JointId[]> mapArrayIdToJointIdArray = Maps.newHashMap();
  // Cached derived structures, invalidated on build/update
  private List<JointImp> cachedRootJoints;
  private List<JointImp> cachedJointsDfs;
  private Map<String, JointImpWrapper> mapNameToJoint;

  JointHierarchyManager(JointedModelResourceBinder<R> resourceBinder) {
    this.resourceBinder = Objects.requireNonNull(resourceBinder, "resourceBinder");
  }

  private void invalidateCaches() {
    cachedRootJoints = null;
    cachedJointsDfs = null;
    mapNameToJoint = null;
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  Hierarchy construction and update
  // ════════════════════════════════════════════════════════════════════════════

  void buildJointHierarchy(JointedModelImp<?, R> owner) {
    Map<JointId, JointImp> jointMap = createJointImps(owner);
    //Make all the joint wrappers and put them in the map
    for (Map.Entry<JointId, JointImp> entry : jointMap.entrySet()) {
      JointImpWrapper wrapper = new JointImpWrapper(owner, entry.getValue(), resourceBinder);
      mapIdToJoint.put(entry.getKey(), wrapper);
    }
    fillInJointArrays();
    //Go through all the wrappers and link up the parent/child relationship
    for (Map.Entry<JointId, JointImpWrapper> entry : mapIdToJoint.entrySet()) {
      entry.getValue().setJointParent(mapIdToJoint.get(entry.getKey().getParent()));
    }
    invalidateCaches();
  }

  private Map<JointId, JointImp> createJointImps(JointedModelImp<?, R> owner) {
    List<JointId> allJointIds = resolveAllJointIds(owner);
    Map<JointId, JointImp> jointMap = new HashMap<>();
    for (JointId jointId : allJointIds) {
      jointMap.put(jointId, resourceBinder.createJointImplementation(owner, jointId));
    }
    for (JointId jointId : allJointIds) {
      JointImp jointImp = jointMap.get(jointId);
      JointImp parentImp = jointMap.get(jointId.getParent());
      jointImp.setJointParent(parentImp);
      if (jointImp.getSgVehicle() == null) {
        jointImp.setVehicle(parentImp);
      }
    }
    return jointMap;
  }

  private List<JointId> resolveAllJointIds(JointedModelImp<?, R> owner) {
    List<JointId> allJointIds = resourceBinder.getAllJointIds();
    for (JointArrayId arrayId : resourceBinder.getJointArrayIds()) {
      JointId[] jointArrayIds = resourceBinder.getJointArrayIdsFromFactory(owner, arrayId);
      Collections.addAll(allJointIds, jointArrayIds);
    }
    return allJointIds;
  }

  private void fillInJointArrays() {
    for (JointArrayId arrayId : resourceBinder.getJointArrayIds()) {
      mapArrayIdToJointIdArray.put(arrayId, findJointsMatching(arrayId.getElementNamePattern()));
    }
  }

  private JointId[] findJointsMatching(String prefix) {
    return mapIdToJoint.keySet().stream()
                       .filter(jointId -> jointId.toString().startsWith(prefix))
                       .sorted(Comparator.comparing(JointId::toString))
                       .toArray(JointId[]::new);
  }

  void updateSkeleton(JointedModelImp<?, R> owner) {
    Map<JointId, JointImp> newJoints = createJointImps(owner);
    matchNewDataToExistingJoints(newJoints);
    //Make joint wrappers for new entries and put them in the map
    for (Map.Entry<JointId, JointImp> entry : newJoints.entrySet()) {
      if (!mapIdToJoint.containsKey(entry.getKey())) {
        mapIdToJoint.put(entry.getKey(), new JointImpWrapper(owner, entry.getValue(), resourceBinder));
      }
    }
    mapArrayIdToJointIdArray.clear();
    fillInJointArrays();
    invalidateCaches();
  }

  private void matchNewDataToExistingJoints(Map<JointId, JointImp> newJoints) {
    List<JointId> toRemove = new ArrayList<>();
    for (JointId oldJointId : mapIdToJoint.keySet()) {
      if (newJoints.containsKey(oldJointId)) {
        mapIdToJoint.get(oldJointId).replaceWithJoint(newJoints.get(oldJointId));
      } else {
        toRemove.add(oldJointId);
      }
    }
    // Order from outer-most toward root to always remove a leaf
    toRemove.sort(JointId::descendantComparison);
    for (JointId id : toRemove) {
      JointImpWrapper impToRemove = this.mapIdToJoint.remove(id);
      AbstractTransformable sgJoint = impToRemove.getSgComposite();
      if (!impToRemove.getJointChildren().isEmpty()) {
        Logger.severe("Removing a joint with child joints. There is a problem with this resource and may lead to errors.");
      }
      impToRemove.setJointParent(null);
      sgJoint.setParent(null);
    }
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  Joint lookup and queries
  // ════════════════════════════════════════════════════════════════════════════

  JointImp getJointImplementation(JointId jointId) {
    return this.mapIdToJoint.get(jointId);
  }

  //String based lookup for DynamicJointIds — O(1) via lazily-built name index
  JointImp getJointImplementation(String jointName) {
    if (mapNameToJoint == null) {
      Map<String, JointImpWrapper> nameMap = new HashMap<>();
      for (Map.Entry<JointId, JointImpWrapper> entry : mapIdToJoint.entrySet()) {
        nameMap.put(entry.getKey().toString(), entry.getValue());
      }
      mapNameToJoint = nameMap;
    }
    return mapNameToJoint.get(jointName);
  }

  JointId[] getJointIdArray(JointArrayId jointArrayId) {
    return this.mapArrayIdToJointIdArray.get(jointArrayId);
  }

  boolean isEmpty() {
    return mapIdToJoint.isEmpty();
  }

  Collection<? extends JointImp> getJointWrappers() {
    return mapIdToJoint.values();
  }

  List<JointImp> getRootJointImps() {
    if (cachedRootJoints == null) {
      List<JointImp> rootJoints = new ArrayList<>();
      for (Map.Entry<JointId, JointImpWrapper> entry : mapIdToJoint.entrySet()) {
        if (entry.getKey().getParent() == null) {
          rootJoints.add(entry.getValue());
        }
      }
      cachedRootJoints = Collections.unmodifiableList(rootJoints);
    }
    return cachedRootJoints;
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  Tree walk
  // ════════════════════════════════════════════════════════════════════════════

  private void treeWalk(JointImp parentImp, TreeWalkObserver observer) {
    if (parentImp != null) {
      observer.pushJoint(parentImp);
      for (JointImp childImp : parentImp.getJointChildren()) {
        if (childImp != null) {
          observer.handleBone(parentImp, childImp);
        }
      }
      observer.popJoint(parentImp);
      for (JointImp childImp : parentImp.getJointChildren()) {
        treeWalk(childImp, observer);
      }
    }
  }

  void treeWalk(TreeWalkObserver observer) {
    for (JointImp root : this.getRootJointImps()) {
      this.treeWalk(root, observer);
    }
  }

  Iterable<JointImp> getJoints() {
    if (cachedJointsDfs == null) {
      List<JointImp> rv = new ArrayList<>();
      this.treeWalk(new TreeWalkObserver() {
        @Override
        public void pushJoint(JointImp joint) {
          rv.add(joint);
        }

        @Override
        public void handleBone(JointImp parent, JointImp child) {
        }

        @Override
        public void popJoint(JointImp joint) {
        }
      });
      cachedJointsDfs = Collections.unmodifiableList(rv);
    }
    return cachedJointsDfs;
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  Joint property operations
  // ════════════════════════════════════════════════════════════════════════════

  void setAllJointPivotsVisible(boolean isPivotVisible) {
    for (JointImpWrapper joint : this.mapIdToJoint.values()) {
      joint.setPivotVisible(isPivotVisible);
    }
  }

  void setScaleOnJoints(Dimension3 scale) {
    for (JointImp jointImp : this.mapIdToJoint.values()) {
      jointImp.setScale(scale);
    }
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  IK chain computation (delegated to IkChainHelper)
  // ════════════════════════════════════════════════════════════════════════════

  List<JointImp> getInclusiveListOfJointsBetween(JointImp jointA, JointImp jointB, List<Bone.Direction> directions, EntityImp owner) {
    return IkChainHelper.getInclusiveListOfJointsBetween(jointA, jointB, directions, owner, this::getJointImplementation);
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  Straighten / Pose support
  // ════════════════════════════════════════════════════════════════════════════

  static class JointData {
    private final JointImp jointImp;
    private final UnitQuaternion q0;
    private final UnitQuaternion q1;

    public JointData(JointImp jointImp) {
      this.jointImp = jointImp;
      this.q0 = this.jointImp.getLocalOrientation().asUnitQuaternion();
      UnitQuaternion q = this.jointImp.getOriginalOrientation();
      this.q1 = ((q == null) || this.q0.isAlignedWith(q)) ? null : q;
    }

    public void setPortion(double portion) {
      if (this.q1 != null) {
        this.jointImp.setLocalOrientationOnly(this.q0.interpolate(this.q1, portion).asMatrix3x3());
      }
    }

    public void epilogue() {
      if (this.q1 != null) {
        this.jointImp.setLocalOrientationOnly(this.q1.asMatrix3x3());
      }
    }
  }

  private static class StraightenTreeWalkObserver implements TreeWalkObserver {
    private final List<JointData> list = new ArrayList<>();

    @Override
    public void pushJoint(JointImp jointImp) {
      list.add(new JointData(jointImp));
    }

    @Override
    public void handleBone(JointImp parent, JointImp child) {
    }

    @Override
    public void popJoint(JointImp joint) {
    }
  }

  List<JointData> collectStraightenData() {
    StraightenTreeWalkObserver treeWalkObserver = new StraightenTreeWalkObserver();
    this.treeWalk(treeWalkObserver);
    return treeWalkObserver.list;
  }

  void straightenOutJoints() {
    for (JointData jointData : collectStraightenData()) {
      jointData.epilogue();
    }
  }
}
