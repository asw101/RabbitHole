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
package org.alice.interact.handle;

import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.PickHint;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Behavioral tests for Not3dHandleCriterion.
 *
 * Verifies the extracted criterion maintains identical filtering logic to
 * the original anonymous inner class in ManipulationHandle3D:
 * - Accepts non-handle components (returns true)
 * - Rejects 3D-handle components (returns false)
 * - Walks parent chain to detect handles
 * - Handles null components safely
 * - Backward compatibility via ManipulationHandle3D.NOT_3D_HANDLE_CRITERION alias
 */
public class Not3dHandleCriterionTest {

  @Test
  public void accept_plainComponent_returnsTrue() {
    Not3dHandleCriterion criterion = new Not3dHandleCriterion();
    Transformable plain = new Transformable();
    assertTrue("Plain component without handle hint should be accepted",
        criterion.accept(plain));
  }

  @Test
  public void accept_componentWithHandleHint_returnsFalse() {
    Not3dHandleCriterion criterion = new Not3dHandleCriterion();
    Transformable handle = new Transformable();
    handle.putBonusDataFor(PickHint.PICK_HINT_KEY, PickHint.PickType.THREE_D_HANDLE.pickHint());
    assertFalse("Component with 3D handle pick hint should be rejected",
        criterion.accept(handle));
  }

  @Test
  public void accept_childOfHandle_returnsFalse() {
    Not3dHandleCriterion criterion = new Not3dHandleCriterion();
    Transformable parent = new Transformable();
    parent.putBonusDataFor(PickHint.PICK_HINT_KEY, PickHint.PickType.THREE_D_HANDLE.pickHint());
    Transformable child = new Transformable();
    child.setParent(parent);
    assertFalse("Child of a 3D handle component should be rejected",
        criterion.accept(child));
  }

  @Test
  public void accept_nullComponent_returnsTrue() {
    Not3dHandleCriterion criterion = new Not3dHandleCriterion();
    // Null should not be a handle, so accept returns true (not-handle = true)
    assertTrue("Null component should be accepted (not a handle)",
        criterion.accept(null));
  }

  @Test
  public void accept_deeplyNestedNonHandle_returnsTrue() {
    Not3dHandleCriterion criterion = new Not3dHandleCriterion();
    Transformable root = new Transformable();
    Transformable mid = new Transformable();
    mid.setParent(root);
    Transformable leaf = new Transformable();
    leaf.setParent(mid);
    assertTrue("Deeply nested component without any handle ancestor should be accepted",
        criterion.accept(leaf));
  }

  @Test
  public void backwardCompatibility_aliasIsSameInstance() {
    // ManipulationHandle3D.NOT_3D_HANDLE_CRITERION should delegate to Not3dHandleCriterion
    assertSame("NOT_3D_HANDLE_CRITERION should be a Not3dHandleCriterion instance",
        Not3dHandleCriterion.class,
        ManipulationHandle3D.NOT_3D_HANDLE_CRITERION.getClass());
  }

  @Test
  public void backwardCompatibility_aliasAcceptsPlain() {
    Transformable plain = new Transformable();
    assertTrue("Alias should accept plain components same as direct instance",
        ManipulationHandle3D.NOT_3D_HANDLE_CRITERION.accept(plain));
  }

  @Test
  public void backwardCompatibility_aliasRejectsHandle() {
    Transformable handle = new Transformable();
    handle.putBonusDataFor(PickHint.PICK_HINT_KEY, PickHint.PickType.THREE_D_HANDLE.pickHint());
    assertFalse("Alias should reject handle components same as direct instance",
        ManipulationHandle3D.NOT_3D_HANDLE_CRITERION.accept(handle));
  }
}
