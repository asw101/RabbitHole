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

import edu.cmu.cs.dennisc.java.util.Lists;
import org.lgna.ik.core.solver.Bone.Direction;
import org.lgna.story.resources.JointId;

import java.util.*;
import java.util.function.Function;

/**
 * Static utility for computing IK (inverse kinematics) joint chains between
 * two joints in a hierarchy. Finds inclusive paths with direction annotations.
 *
 * <p>Extracted from JointHierarchyManager for maintainability. Joint lookup
 * is parameterized via {@code Function<JointId, JointImp>} to decouple from
 * the hierarchy manager's internal map.</p>
 */
final class IkChainHelper {

  private IkChainHelper() {
    // utility class
  }

  private enum AddOp {
    PREPEND {
      @Override
      public List<JointImp> add(List<JointImp> rv, JointImp joint, List<Direction> directions, Direction direction) {
        rv.addFirst(joint);
        if (directions != null) {
          directions.addFirst(direction);
        }
        return rv;
      }
    },
    APPEND {
      @Override
      public List<JointImp> add(List<JointImp> rv, JointImp joint, List<Direction> directions, Direction direction) {
        rv.add(joint);
        if (directions != null) {
          directions.add(direction);
        }
        return rv;
      }
    };

    public abstract List<JointImp> add(List<JointImp> rv, JointImp joint, List<Direction> directions, Direction direction);
  }

  static List<JointImp> getInclusiveListOfJointsBetween(JointImp jointA, JointImp jointB,
      List<Direction> directions, EntityImp owner, Function<JointId, JointImp> jointLookup) {
    assert jointA != null;
    assert jointB != null;
    List<JointImp> rv = Lists.newLinkedList();
    if (jointA == jointB) {
      rv.add(jointA);
      directions.add(Direction.DOWNSTREAM);
    } else {
      if (jointA.isDescendantOf(jointB)) {
        updateJointsBetween(rv, directions, jointA, jointB, AddOp.PREPEND, jointLookup);
      } else if (jointB.isDescendantOf(jointA)) {
        updateJointsBetween(rv, directions, jointB, jointA, AddOp.APPEND, jointLookup);
      } else {
        //It shouldn't even use the joint on which direction is changed (the common ancestor)
        //that's what the below call does
        updateJointsUpToAndExcludingCommonAncestor(rv, directions, jointA, jointB, owner, jointLookup);
      }
    }
    return rv;
  }

  private static List<JointImp> updateJointsBetween(List<JointImp> rv, List<Direction> directions,
      JointImp joint, EntityImp ancestorToReach, AddOp addOp, Function<JointId, JointImp> jointLookup) {
    if (joint != ancestorToReach) {
      JointId parentId = joint.getJointId().getParent();
      if (parentId != null) {
        JointImp parent = jointLookup.apply(parentId);
        updateJointsBetween(rv, directions, parent, ancestorToReach, addOp, jointLookup);
      }
    }
    Direction direction;
    if (addOp == AddOp.APPEND) {
      direction = Direction.DOWNSTREAM;
    } else {
      direction = Direction.UPSTREAM;
    }
    addOp.add(rv, joint, directions, direction);
    return rv;
  }

  private static void updateJointsUpToAndExcludingCommonAncestor(List<JointImp> rvPath,
      List<Direction> rvDirections, JointImp jointA, JointImp jointB, EntityImp owner,
      Function<JointId, JointImp> jointLookup) {
    List<JointImp> pathA = Lists.newLinkedList();
    List<JointImp> pathB = Lists.newLinkedList();

    List<Direction> directionsA = new ArrayList<>();
    List<Direction> directionsB = new ArrayList<>();

    updateJointsBetween(pathA, directionsA, jointA, owner, AddOp.PREPEND, jointLookup);
    updateJointsBetween(pathB, directionsB, jointB, owner, AddOp.APPEND, jointLookup);

    // O(1) membership test instead of O(n) LinkedList.contains
    Set<JointImp> pathBSet = new HashSet<>(pathB);
    JointImp commonAncestor = null;

    for (JointImp jointInA : pathA) {
      if (pathBSet.contains(jointInA)) {
        commonAncestor = jointInA;
        break;
      }
    }

    if (commonAncestor == null) {
      throw new RuntimeException("Probably not connected with a chain.");
    }

    ListIterator<JointImp> pathAIterator = pathA.listIterator(pathA.size());
    ListIterator<Direction> directionsAIterator = directionsA.listIterator(directionsA.size());
    for (; pathAIterator.hasPrevious(); ) {
      JointImp jointImp = pathAIterator.previous();
      directionsAIterator.previous();

      pathAIterator.remove();
      directionsAIterator.remove();

      if (jointImp == commonAncestor) {
        break;
      }
    }

    ListIterator<JointImp> pathBIterator = pathB.listIterator();
    ListIterator<Direction> directionsBIterator = directionsB.listIterator();
    for (; pathBIterator.hasNext(); ) {
      JointImp jointImp = pathBIterator.next();
      directionsBIterator.next();

      pathBIterator.remove();
      directionsBIterator.remove();

      if (jointImp == commonAncestor) {
        break;
      }
    }

    rvPath.addAll(pathA);
    rvPath.addAll(pathB);
    rvDirections.addAll(directionsA);
    rvDirections.addAll(directionsB);
  }
}
