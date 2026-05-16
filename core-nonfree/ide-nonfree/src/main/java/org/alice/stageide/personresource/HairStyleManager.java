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

package org.alice.stageide.personresource;

import edu.cmu.cs.dennisc.java.util.Lists;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import org.alice.stageide.personresource.data.HairColorName;
import org.alice.stageide.personresource.data.HairColorNameHairCombo;
import org.alice.stageide.personresource.data.HairHatStyle;
import org.alice.stageide.personresource.data.HairHatStyleHairColorName;
import org.lgna.story.resources.sims2.Gender;
import org.lgna.story.resources.sims2.Hair;
import org.lgna.story.resources.sims2.LifeStage;

import java.util.List;

/**
 * Manages hair color priority and resolution logic.
 * Extracted from IngredientsComposite to reduce its size.
 *
 * Thread-safe: all access to the priority list is synchronized.
 */
class HairStyleManager {
  private final List<HairColorName> hairColorNames = Lists.newLinkedList();

  /**
   * Resolves the best Hair for a given HairHatStyle based on the
   * user's color priority list. Falls back to the first available combo.
   */
  public Hair getHairForHairHatStyle(HairHatStyle hairHatStyle) {
    if (hairHatStyle != null) {
      synchronized (this.hairColorNames) {
        for (HairColorName hairColorName : this.hairColorNames) {
          Hair rv = hairHatStyle.getHair(hairColorName);
          if (rv != null) {
            return rv;
          }
        }
      }
      List<HairColorNameHairCombo> hairColorNameHairCombos = hairHatStyle.getHairColorNameHairCombos();
      if (!hairColorNameHairCombos.isEmpty()) {
        HairColorNameHairCombo hairColorNameHairCombo = hairColorNameHairCombos.getFirst();
        if (hairColorNameHairCombo != null) {
          return hairColorNameHairCombo.getHair();
        }
      }
    }
    return null;
  }

  /**
   * Moves the given color name to the front of the priority list,
   * removing any existing occurrence first.
   */
  public void addHairColorNameToFront(HairColorName hairColorName) {
    synchronized (this.hairColorNames) {
      int index = this.hairColorNames.indexOf(hairColorName);
      if (index != -1) {
        this.hairColorNames.remove(index);
      }
      this.hairColorNames.addFirst(hairColorName);
    }
  }

  /**
   * Applies a resolved hair style + color to the given hair tab, updating
   * list data, view, and state objects.
   */
  public void applyHairStyle(HairTabComposite hairTab, LifeStage lifeStage, Gender gender, HairHatStyleHairColorName hairHatStyleHairColorName) {
    if (hairHatStyleHairColorName != null) {
      HairHatStyle hairHatStyle = hairHatStyleHairColorName.getHairHatStyle();
      HairColorName hairColorName = hairHatStyleHairColorName.getHairColorName();

      hairTab.getHairHatStyleListData().setLifeStageAndGender(lifeStage, gender);
      hairTab.getHairColorNameData().setHairHatStyle(hairHatStyle);
      hairTab.getView().repaint();

      hairTab.getHairHatStyleState().setValueTransactionlessly(hairHatStyle);
      hairTab.getHairColorNameState().setValueTransactionlessly(hairColorName);
    } else {
      Logger.severe();
    }
  }
}
