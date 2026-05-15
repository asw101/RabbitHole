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

package org.lgna.croquet.views;

import edu.cmu.cs.dennisc.print.PrintUtilities;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.HierarchyEvent;

/**
 * Package-private handler owning hierarchy-lifecycle state and dispatch logic
 * extracted from {@link AwtComponentView}. Tracks displayability transitions
 * and parent-change events, delegating to the owner's protected hooks.
 */
final class AwtHierarchyHandler {

  private final AwtComponentView<?> owner;
  private boolean isDisplayableState = false;
  private Container awtParent;
  private static boolean isWarningAlreadyPrinted = false;

  AwtHierarchyHandler(AwtComponentView<?> owner) {
    this.owner = owner;
  }

  void trackDisplayability(Component awtComponent) {
    boolean displayable = awtComponent.isDisplayable();
    if (!isDisplayableState && displayable) {
      owner.handleDisplayable();
      this.isDisplayableState = true;
    } else if (isDisplayableState && !displayable) {
      owner.handleUndisplayable();
      this.isDisplayableState = false;
    }
  }

  void handleParentChange(Container newParent) {
    if (this.awtParent != null) {
      owner.handleRemovedFrom(AwtComponentView.lookup(this.awtParent));
    }
    this.awtParent = newParent;
    if (this.awtParent != null) {
      owner.handleAddedTo(AwtComponentView.lookup(this.awtParent));
    }
  }

  private static final long HANDLED_FLAGS = HierarchyEvent.DISPLAYABILITY_CHANGED | HierarchyEvent.PARENT_CHANGED;

  void processHierarchyEvent(HierarchyEvent e) {
    long flags = e.getChangeFlags();
    if ((flags & HANDLED_FLAGS) == 0) {
      return;
    }
    Component ownerComponent = owner.getAwtComponent();
    if ((flags & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
      if (e.getComponent() == ownerComponent) {
        this.trackDisplayability(ownerComponent);
      } else {
        PrintUtilities.println("handleDisplayabilityChanged:", ownerComponent.hashCode(), ownerComponent.isDisplayable());
      }
    }
    if ((flags & HierarchyEvent.PARENT_CHANGED) != 0 && e.getComponent() == e.getChanged()) {
      Container eventAwtParent = e.getChangedParent();
      if (eventAwtParent != this.awtParent) {
        handleParentChange(eventAwtParent);
      } else {
        if (!isWarningAlreadyPrinted) {
          PrintUtilities.println("investigate: hierarchyChanged seems to not be actually changing the parent");
          isWarningAlreadyPrinted = true;
        }
      }
    }
  }
}
