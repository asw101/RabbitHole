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
package org.alice.stageide.sceneeditor.interact;

import org.alice.interact.DragAdapter;
import org.alice.interact.handle.HandleManager;
import org.alice.interact.handle.ManipulationHandle;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Behavioral tests for HandleSetupDelegate.setupHandles(DragAdapter).
 *
 * These tests invoke the static delegate method on a minimal DragAdapter
 * subclass and verify that the correct number and types of handles are
 * registered. They complement the structural contract tests in
 * HandleSetupDelegateContractTest.
 *
 * TDD: these tests FAIL until HandleSetupDelegate is implemented.
 */
public class HandleSetupDelegateBehaviorTest {

  private TestDragAdapter adapter;

  @Before
  public void setUp() {
    adapter = new TestDragAdapter();
  }

  // ── Handle registration count ───────────────────────────────────

  @Test
  public void setupHandlesRegisters24Handles() {
    HandleSetupDelegate.setupHandles(adapter);
    List<ManipulationHandle> handles = getHandles(adapter);
    assertEquals("setupHandles must register exactly 24 handles via setDragAdapterAndAddHandle",
        24, handles.size());
  }

  // ── Manipulation listener count ─────────────────────────────────

  @Test
  public void setupHandlesAdds8ManipulationListeners() {
    int beforeCount = adapter.getManipulationListenerCount();
    HandleSetupDelegate.setupHandles(adapter);
    int addedCount = adapter.getManipulationListenerCount() - beforeCount;
    assertEquals("setupHandles must add exactly 8 manipulation listeners",
        8, addedCount);
  }

  // ── Named handle verification ───────────────────────────────────

  @Test
  public void handleAxisIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("handleAxis");
  }

  @Test
  public void rotateAboutYAxisStoodUpIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("rotateAboutYAxisStoodUp");
  }

  @Test
  public void rotateAboutYAxisIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("rotateAboutYAxis");
  }

  @Test
  public void rotateAboutXAxisIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("rotateAboutXAxis");
  }

  @Test
  public void rotateAboutZAxisIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("rotateAboutZAxis");
  }

  @Test
  public void rotateJointAboutZAxisIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("rotateJointAboutZAxis");
  }

  @Test
  public void rotateJointAboutYAxisIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("rotateJointAboutYAxis");
  }

  @Test
  public void rotateJointAboutXAxisIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("rotateJointAboutXAxis");
  }

  @Test
  public void translateJointYAxisIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("translateJointYAxis");
  }

  @Test
  public void translateJointXAxisIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("translateJointXAxis");
  }

  @Test
  public void translateJointZAxisIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("translateJointZAxis");
  }

  @Test
  public void translateUpIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("translateUp");
  }

  @Test
  public void translateDownIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("translateDown");
  }

  @Test
  public void translateXAxisRightIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("translateXAxisRight");
  }

  @Test
  public void translateXAxisLeftIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("translateXAxisLeft");
  }

  @Test
  public void translateForwardIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("translateForward");
  }

  @Test
  public void translateBackwardIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("translateBackward");
  }

  @Test
  public void scaleAxisUniformIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("scaleAxisUniform");
  }

  @Test
  public void scaleAxisXIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("scaleAxisX");
  }

  @Test
  public void scaleAxisYIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("scaleAxisY");
  }

  @Test
  public void scaleAxisZIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("scaleAxisZ");
  }

  @Test
  public void scaleAxisXYIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("scaleAxisXY");
  }

  @Test
  public void scaleAxisXZIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("scaleAxisXZ");
  }

  @Test
  public void scaleAxisYZIsRegistered() {
    HandleSetupDelegate.setupHandles(adapter);
    assertHandleExists("scaleAxisYZ");
  }

  // ── Idempotency ─────────────────────────────────────────────────

  @Test
  public void callingSetupHandlesTwiceDoublesHandles() {
    HandleSetupDelegate.setupHandles(adapter);
    int firstCount = getHandles(adapter).size();

    HandleSetupDelegate.setupHandles(adapter);
    int secondCount = getHandles(adapter).size();

    assertEquals("Calling setupHandles twice should add handles additively",
        firstCount * 2, secondCount);
  }

  // ── Null safety ─────────────────────────────────────────────────

  @Test(expected = NullPointerException.class)
  public void setupHandlesRejectsNull() {
    HandleSetupDelegate.setupHandles(null);
  }

  // ── Helpers ─────────────────────────────────────────────────────

  private void assertHandleExists(String expectedName) {
    List<ManipulationHandle> handles = getHandles(adapter);
    boolean found = handles.stream()
        .anyMatch(h -> expectedName.equals(h.getName()));
    assertTrue("Handle '" + expectedName + "' must be registered", found);
  }

  @SuppressWarnings("unchecked")
  private List<ManipulationHandle> getHandles(DragAdapter adapter) {
    try {
      HandleManager handleManager = getHandleManager(adapter);
      Field handlesField = HandleManager.class.getDeclaredField("handles");
      handlesField.setAccessible(true);
      return (List<ManipulationHandle>) handlesField.get(handleManager);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Failed to access handles via reflection", e);
    }
  }

  private HandleManager getHandleManager(DragAdapter adapter) {
    try {
      Method getHandleManager = DragAdapter.class.getDeclaredMethod("getHandleManager");
      getHandleManager.setAccessible(true);
      return (HandleManager) getHandleManager.invoke(adapter);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Failed to access HandleManager via reflection", e);
    }
  }

  /**
   * Minimal concrete DragAdapter for testing. DragAdapter is abstract,
   * so we need a thin subclass. This mirrors the TestDragAdapter pattern
   * used in DragEventHandlerTest.
   */
  static class TestDragAdapter extends DragAdapter {
    private int manipulationListenerCount = 0;

    @Override
    public void addManipulationListener(org.alice.interact.event.ManipulationListener listener) {
      super.addManipulationListener(listener);
      manipulationListenerCount++;
    }

    int getManipulationListenerCount() {
      return manipulationListenerCount;
    }
  }
}
