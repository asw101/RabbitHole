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

import org.junit.Test;
import org.lgna.story.resources.sims2.*;

import static org.junit.Assert.*;

/**
 * Characterization tests for OutfitFactory — the extracted stateless outfit
 * construction logic from IngredientsComposite.
 *
 * These tests run without a Croquet context or Alice IDE runtime.
 * They exercise the pure switch-expression logic that will be extracted
 * into OutfitFactory.
 */
public class OutfitFactoryTest {

  // --- createTopAndBottomOutfit: all LifeStage × Gender combinations ---

  @Test
  public void createTopAndBottomOutfit_adultMale() {
    MaleAdultTopPiece top = MaleAdultTopPieceCowboyShirt.PATTERN_RED;
    MaleAdultBottomPiece bottom = MaleAdultBottomPiecePants.KHAKI_SLACKS_BROWN;

    Outfit result = OutfitFactory.createTopAndBottomOutfit(LifeStage.ADULT, Gender.MALE, top, bottom);

    assertNotNull(result);
    assertTrue("Expected MaleAdultTopAndBottomOutfit", result instanceof MaleAdultTopAndBottomOutfit);
    MaleAdultTopAndBottomOutfit outfit = (MaleAdultTopAndBottomOutfit) result;
    assertSame(top, outfit.getTopPiece());
    assertSame(bottom, outfit.getBottomPiece());
  }

  @Test
  public void createTopAndBottomOutfit_adultFemale() {
    FemaleAdultTopPiece top = FemaleAdultTopPieceTShirt.BLACK;
    FemaleAdultBottomPiece bottom = FemaleAdultBottomPieceCapriSneaks.BLACK;

    Outfit result = OutfitFactory.createTopAndBottomOutfit(LifeStage.ADULT, Gender.FEMALE, top, bottom);

    assertNotNull(result);
    assertTrue("Expected FemaleAdultTopAndBottomOutfit", result instanceof FemaleAdultTopAndBottomOutfit);
  }

  @Test
  public void createTopAndBottomOutfit_childMale() {
    MaleChildTopPiece top = MaleChildTopPieceCowboyShirt.RED;
    MaleChildBottomPiece bottom = MaleChildBottomPieceCowboyJeans.BLACK;

    Outfit result = OutfitFactory.createTopAndBottomOutfit(LifeStage.CHILD, Gender.MALE, top, bottom);

    assertNotNull(result);
    assertTrue("Expected MaleChildTopAndBottomOutfit", result instanceof MaleChildTopAndBottomOutfit);
  }

  @Test
  public void createTopAndBottomOutfit_childFemale() {
    FemaleChildTopPiece top = FemaleChildTopPieceTShirt.BLACK;
    FemaleChildBottomPiece bottom = FemaleChildBottomPieceMiniSkirt.STRIPY;

    Outfit result = OutfitFactory.createTopAndBottomOutfit(LifeStage.CHILD, Gender.FEMALE, top, bottom);

    assertNotNull(result);
    assertTrue("Expected FemaleChildTopAndBottomOutfit", result instanceof FemaleChildTopAndBottomOutfit);
  }

  @Test
  public void createTopAndBottomOutfit_teenMale() {
    MaleTeenTopPiece top = MaleTeenTopPieceShortSleeveCollar.BLACK;
    MaleTeenBottomPiece bottom = MaleTeenBottomPieceCargoPants.GREY;

    Outfit result = OutfitFactory.createTopAndBottomOutfit(LifeStage.TEEN, Gender.MALE, top, bottom);

    assertNotNull(result);
    assertTrue("Expected MaleTeenTopAndBottomOutfit", result instanceof MaleTeenTopAndBottomOutfit);
  }

  @Test
  public void createTopAndBottomOutfit_teenFemale() {
    FemaleTeenTopPiece top = FemaleTeenTopPieceHalter.PINK;
    FemaleTeenBottomPiece bottom = FemaleTeenBottomPieceBaggyPants.GREEN;

    Outfit result = OutfitFactory.createTopAndBottomOutfit(LifeStage.TEEN, Gender.FEMALE, top, bottom);

    assertNotNull(result);
    assertTrue("Expected FemaleTeenTopAndBottomOutfit", result instanceof FemaleTeenTopAndBottomOutfit);
  }

  @Test
  public void createTopAndBottomOutfit_elderMale() {
    MaleElderTopPiece top = MaleElderTopPieceShortSleeveCollar.BLACK;
    MaleElderBottomPiece bottom = MaleElderBottomPieceLongShorts.NAVY_WHITE;

    Outfit result = OutfitFactory.createTopAndBottomOutfit(LifeStage.ELDER, Gender.MALE, top, bottom);

    assertNotNull(result);
    assertTrue("Expected MaleElderTopAndBottomOutfit", result instanceof MaleElderTopAndBottomOutfit);
  }

  @Test
  public void createTopAndBottomOutfit_elderFemale() {
    FemaleElderTopPiece top = FemaleElderTopPieceSweaterSet.BLUE;
    FemaleElderBottomPiece bottom = FemaleElderBottomPieceSlacksHeels.BEIGE;

    Outfit result = OutfitFactory.createTopAndBottomOutfit(LifeStage.ELDER, Gender.FEMALE, top, bottom);

    assertNotNull(result);
    assertTrue("Expected FemaleElderTopAndBottomOutfit", result instanceof FemaleElderTopAndBottomOutfit);
  }

  @Test
  public void createTopAndBottomOutfit_toddler_returnsNull() {
    // TODDLER has no top-and-bottom option
    Outfit result = OutfitFactory.createTopAndBottomOutfit(LifeStage.TODDLER, Gender.MALE, null, null);
    assertNull("TODDLER should return null — no top-and-bottom option", result);
  }

  // --- getOutfit: selection logic ---

  @Test
  public void getOutfit_prefersFullBody_whenTopAndBottomUnavailable() {
    FullBodyOutfit fullbody = MaleAdultFullBodyOutfitGardener.MALEOUTFIT;
    MaleAdultTopPiece top = MaleAdultTopPieceCowboyShirt.PATTERN_RED;
    MaleAdultBottomPiece bottom = MaleAdultBottomPiecePants.KHAKI_SLACKS_BROWN;

    Outfit result = OutfitFactory.getOutfit(
        LifeStage.ADULT, Gender.MALE,
        fullbody, top, bottom,
        false,  // topsAndBottomsAvailable = false
        false   // lastActiveIsTopAndBottom = false
    );

    assertSame("Should return fullbody when tops-and-bottoms unavailable", fullbody, result);
  }

  @Test
  public void getOutfit_prefersFullBody_whenLastActiveTabIsNotTopAndBottom() {
    FullBodyOutfit fullbody = MaleAdultFullBodyOutfitGardener.MALEOUTFIT;
    MaleAdultTopPiece top = MaleAdultTopPieceCowboyShirt.PATTERN_RED;
    MaleAdultBottomPiece bottom = MaleAdultBottomPiecePants.KHAKI_SLACKS_BROWN;

    Outfit result = OutfitFactory.getOutfit(
        LifeStage.ADULT, Gender.MALE,
        fullbody, top, bottom,
        true,   // topsAndBottomsAvailable = true
        false   // lastActiveIsTopAndBottom = false (full body tab was last active)
    );

    assertSame("Should return fullbody when fullbody tab was last active", fullbody, result);
  }

  @Test
  public void getOutfit_prefersTopAndBottom_whenLastActiveTabIsTopAndBottom() {
    FullBodyOutfit fullbody = MaleAdultFullBodyOutfitGardener.MALEOUTFIT;
    MaleAdultTopPiece top = MaleAdultTopPieceCowboyShirt.PATTERN_RED;
    MaleAdultBottomPiece bottom = MaleAdultBottomPiecePants.KHAKI_SLACKS_BROWN;

    Outfit result = OutfitFactory.getOutfit(
        LifeStage.ADULT, Gender.MALE,
        fullbody, top, bottom,
        true,  // topsAndBottomsAvailable = true
        true   // lastActiveIsTopAndBottom = true
    );

    assertNotNull(result);
    assertTrue("Should return TopAndBottomOutfit when that tab was last active",
        result instanceof MaleAdultTopAndBottomOutfit);
  }

  @Test
  public void getOutfit_returnsFullBody_whenFullBodyNotNull_andTopAndBottomNotAvailable() {
    FullBodyOutfit fullbody = MaleAdultFullBodyOutfitGardener.MALEOUTFIT;

    Outfit result = OutfitFactory.getOutfit(
        LifeStage.ADULT, Gender.MALE,
        fullbody, null, null,
        false,  // topsAndBottomsAvailable = false
        true    // lastActiveIsTopAndBottom = true, but no data
    );

    assertSame(fullbody, result);
  }

  @Test
  public void getOutfit_toddler_alwaysReturnsFullBody() {
    FullBodyOutfit fullbody = MaleAdultFullBodyOutfitGardener.MALEOUTFIT;

    // Even when topAndBottom tab is active, TODDLER must use fullbody
    Outfit result = OutfitFactory.getOutfit(
        LifeStage.TODDLER, Gender.MALE,
        fullbody, null, null,
        true,  // topsAndBottomsAvailable
        true   // lastActiveIsTopAndBottom
    );

    assertSame("TODDLER should always return fullbody outfit", fullbody, result);
  }

  @Test
  public void getOutfit_toddler_returnsNull_whenNoFullBody() {
    Outfit result = OutfitFactory.getOutfit(
        LifeStage.TODDLER, Gender.MALE,
        null, null, null,
        true, true
    );

    assertNull("TODDLER with no fullbody should return null", result);
  }

  @Test
  public void getOutfit_returnsTopAndBottom_whenFullBodyIsNull() {
    MaleAdultTopPiece top = MaleAdultTopPieceCowboyShirt.PATTERN_RED;
    MaleAdultBottomPiece bottom = MaleAdultBottomPiecePants.KHAKI_SLACKS_BROWN;

    Outfit result = OutfitFactory.getOutfit(
        LifeStage.ADULT, Gender.MALE,
        null, top, bottom,
        true,  // topsAndBottomsAvailable = true
        false  // lastActiveIsTopAndBottom = false, but no fullbody
    );

    // fullbody is null so the guard clause won't trigger; should construct top-and-bottom
    assertTrue("Should construct TopAndBottomOutfit when fullbody is null",
        result instanceof MaleAdultTopAndBottomOutfit);
  }
}
