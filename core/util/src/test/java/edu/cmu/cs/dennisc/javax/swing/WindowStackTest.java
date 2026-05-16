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
package edu.cmu.cs.dennisc.javax.swing;

import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

/**
 * TDD tests for WindowStack headless guard.
 *
 * <p>Before the fix, {@code WindowStack} eagerly creates a {@code new JFrame()}
 * in a static initializer, which throws {@code HeadlessException} in headless
 * CI environments (macOS runners). After the fix, {@code getRootFrame()} returns
 * {@code null} when {@code GraphicsEnvironment.isHeadless()} is true.
 *
 * <p>In headless mode, the current (unfixed) code causes an
 * {@code ExceptionInInitializerError}/{@code HeadlessException} when this test
 * class loads WindowStack — that's the expected TDD failure.
 */
public class WindowStackTest {

  @Test
  public void getRootFrame_returnsNullInHeadless() {
    if (GraphicsEnvironment.isHeadless()) {
      // In headless environments, getRootFrame() must return null
      // instead of crashing with HeadlessException during class init.
      assertNull("getRootFrame() should return null in headless environments",
          WindowStack.getRootFrame());
    }
    // In headed environments this test is a no-op — the headed assertion
    // is covered by getRootFrame_returnsJFrameInHeaded.
  }

  @Test
  public void getRootFrame_returnsJFrameInHeaded() {
    if (!GraphicsEnvironment.isHeadless()) {
      assertNotNull("getRootFrame() should return a JFrame in headed environments",
          WindowStack.getRootFrame());
    }
  }

  @Test
  public void peek_returnsNullWhenStackEmptyInHeadless() {
    if (GraphicsEnvironment.isHeadless()) {
      // peek() falls back to rootFrame when stack is empty.
      // In headless mode, rootFrame is null so peek() should return null.
      assertNull("peek() should return null when stack is empty in headless",
          WindowStack.peek());
    }
  }

  @Test
  public void peek_returnsRootFrameWhenStackEmptyInHeaded() {
    if (!GraphicsEnvironment.isHeadless()) {
      // In headed mode, peek() should return the rootFrame when stack is empty.
      assertNotNull("peek() should return rootFrame when stack is empty",
          WindowStack.peek());
      assertSame("peek() should return getRootFrame() when stack is empty",
          WindowStack.getRootFrame(), WindowStack.peek());
    }
  }
}
