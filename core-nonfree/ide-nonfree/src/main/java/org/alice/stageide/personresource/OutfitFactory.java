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

import org.lgna.story.resources.sims2.*;

/**
 * Stateless factory for constructing outfit instances from UI state.
 * Extracted from IngredientsComposite to reduce its size.
 */
class OutfitFactory {

  private OutfitFactory() {
  }

  /**
   * Creates a TopAndBottomOutfit for the given LifeStage and Gender.
   * Returns null for TODDLER (which has no top-and-bottom option).
   */
  static Outfit createTopAndBottomOutfit(LifeStage lifeStage, Gender gender, TopPiece top, BottomPiece bottom) {
    return switch (lifeStage) {
      case TODDLER -> null;
      case CHILD -> switch (gender) {
        case MALE -> new MaleChildTopAndBottomOutfit((MaleChildTopPiece) top, (MaleChildBottomPiece) bottom);
        case FEMALE -> new FemaleChildTopAndBottomOutfit((FemaleChildTopPiece) top, (FemaleChildBottomPiece) bottom);
      };
      case TEEN -> switch (gender) {
        case MALE -> new MaleTeenTopAndBottomOutfit((MaleTeenTopPiece) top, (MaleTeenBottomPiece) bottom);
        case FEMALE -> new FemaleTeenTopAndBottomOutfit((FemaleTeenTopPiece) top, (FemaleTeenBottomPiece) bottom);
      };
      case ADULT -> switch (gender) {
        case MALE -> new MaleAdultTopAndBottomOutfit((MaleAdultTopPiece) top, (MaleAdultBottomPiece) bottom);
        case FEMALE -> new FemaleAdultTopAndBottomOutfit((FemaleAdultTopPiece) top, (FemaleAdultBottomPiece) bottom);
      };
      case ELDER -> switch (gender) {
        case MALE -> new MaleElderTopAndBottomOutfit((MaleElderTopPiece) top, (MaleElderBottomPiece) bottom);
        case FEMALE -> new FemaleElderTopAndBottomOutfit((FemaleElderTopPiece) top, (FemaleElderBottomPiece) bottom);
      };
    };
  }

  /**
   * Selects the appropriate outfit based on tab state and data availability.
   *
   * @param lifeStage             current life stage
   * @param gender                current gender
   * @param fullbody              currently selected full-body outfit (may be null)
   * @param top                   currently selected top piece (may be null)
   * @param bottom                currently selected bottom piece (may be null)
   * @param topsAndBottomsAvailable whether top-and-bottom data lists have items
   * @param lastActiveIsTopAndBottom whether the top-and-bottom tab was last active
   * @return the resolved Outfit
   */
  static Outfit getOutfit(LifeStage lifeStage, Gender gender,
                          FullBodyOutfit fullbody, TopPiece top, BottomPiece bottom,
                          boolean topsAndBottomsAvailable, boolean lastActiveIsTopAndBottom) {
    // TODDLER has no top-and-bottom option; always use fullbody
    if (lifeStage == LifeStage.TODDLER) {
      return fullbody;
    }
    if ((!topsAndBottomsAvailable || !lastActiveIsTopAndBottom) && fullbody != null) {
      return fullbody;
    }
    return createTopAndBottomOutfit(lifeStage, gender, top, bottom);
  }
}
