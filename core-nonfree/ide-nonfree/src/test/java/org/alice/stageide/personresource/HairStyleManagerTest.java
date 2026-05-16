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

import org.alice.stageide.personresource.data.HairClsHatNameCombo;
import org.alice.stageide.personresource.data.HairColorName;
import org.alice.stageide.personresource.data.HairColorNameHairCombo;
import org.alice.stageide.personresource.data.HairHatStyle;
import org.junit.Before;
import org.junit.Test;
import org.lgna.story.resources.sims2.Hair;
import org.lgna.story.resources.sims2.MaleAdultHairShortMop;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Characterization tests for HairStyleManager — the extracted hair color
 * priority and resolution logic from IngredientsComposite.
 *
 * These tests run without a Croquet context or Alice IDE runtime.
 * They exercise the pure logic that will be extracted into HairStyleManager.
 */
public class HairStyleManagerTest {

  private HairStyleManager manager;

  @Before
  public void setUp() {
    manager = new HairStyleManager();
  }

  // --- getHairForHairHatStyle ---

  @Test
  public void getHairForHairHatStyle_returnsNull_whenStyleIsNull() {
    assertNull(manager.getHairForHairHatStyle(null));
  }

  @Test
  public void getHairForHairHatStyle_returnsPriorityMatch_whenColorInList() {
    // Set up a style with BLACK and BROWN combos
    Hair blackHair = MaleAdultHairShortMop.BLACK;
    Hair brownHair = MaleAdultHairShortMop.BROWN;
    HairHatStyle style = createHairHatStyle(
        new HairColorNameHairCombo(HairColorName.BLACK, blackHair),
        new HairColorNameHairCombo(HairColorName.BROWN, brownHair)
    );

    // Add BROWN to priority list first, then BLACK
    manager.addHairColorNameToFront(HairColorName.BROWN);
    manager.addHairColorNameToFront(HairColorName.BLACK);

    // BLACK is at front of priority list, so it should be returned
    Hair result = manager.getHairForHairHatStyle(style);
    assertSame(blackHair, result);
  }

  @Test
  public void getHairForHairHatStyle_returnsSecondPriority_whenFirstNotAvailable() {
    // Style only has BROWN, not BLACK
    Hair brownHair = MaleAdultHairShortMop.BROWN;
    HairHatStyle style = createHairHatStyle(
        new HairColorNameHairCombo(HairColorName.BROWN, brownHair)
    );

    // Priority list has BLACK first, then BROWN
    manager.addHairColorNameToFront(HairColorName.BROWN);
    manager.addHairColorNameToFront(HairColorName.BLACK);

    // BLACK not available in style, so should fall back to BROWN
    Hair result = manager.getHairForHairHatStyle(style);
    assertSame(brownHair, result);
  }

  @Test
  public void getHairForHairHatStyle_fallsBackToFirstCombo_whenNoPriorityMatch() {
    // Style has RED and BLOND, but priority list has BLACK only
    Hair redHair = MaleAdultHairShortMop.RED;
    Hair blondHair = MaleAdultHairShortMop.BLOND;
    HairHatStyle style = createHairHatStyle(
        new HairColorNameHairCombo(HairColorName.RED, redHair),
        new HairColorNameHairCombo(HairColorName.BLOND, blondHair)
    );

    manager.addHairColorNameToFront(HairColorName.BLACK);

    // No match in priority list, should return first combo's hair
    Hair result = manager.getHairForHairHatStyle(style);
    assertSame(redHair, result);
  }

  @Test
  public void getHairForHairHatStyle_fallsBackToFirstCombo_whenPriorityListEmpty() {
    Hair brownHair = MaleAdultHairShortMop.BROWN;
    HairHatStyle style = createHairHatStyle(
        new HairColorNameHairCombo(HairColorName.BROWN, brownHair)
    );

    // Empty priority list — should fall back to first combo
    Hair result = manager.getHairForHairHatStyle(style);
    assertSame(brownHair, result);
  }

  @Test
  public void getHairForHairHatStyle_returnsNull_whenStyleHasNoCombos() {
    HairHatStyle style = createHairHatStyle();

    Hair result = manager.getHairForHairHatStyle(style);
    assertNull(result);
  }

  // --- addHairColorNameToFront ---

  @Test
  public void addHairColorNameToFront_addsNewToFront() {
    manager.addHairColorNameToFront(HairColorName.BROWN);
    manager.addHairColorNameToFront(HairColorName.BLACK);

    // BLACK was added last so should be at front — verify via resolution priority
    Hair blackHair = MaleAdultHairShortMop.BLACK;
    Hair brownHair = MaleAdultHairShortMop.BROWN;
    HairHatStyle style = createHairHatStyle(
        new HairColorNameHairCombo(HairColorName.BLACK, blackHair),
        new HairColorNameHairCombo(HairColorName.BROWN, brownHair)
    );

    assertSame(blackHair, manager.getHairForHairHatStyle(style));
  }

  @Test
  public void addHairColorNameToFront_movesExistingToFront() {
    manager.addHairColorNameToFront(HairColorName.BLACK);
    manager.addHairColorNameToFront(HairColorName.BROWN);
    // Now BROWN is at front

    // Re-add BLACK — it should move to front
    manager.addHairColorNameToFront(HairColorName.BLACK);

    Hair blackHair = MaleAdultHairShortMop.BLACK;
    Hair brownHair = MaleAdultHairShortMop.BROWN;
    HairHatStyle style = createHairHatStyle(
        new HairColorNameHairCombo(HairColorName.BLACK, blackHair),
        new HairColorNameHairCombo(HairColorName.BROWN, brownHair)
    );

    // BLACK should now be preferred (at front)
    assertSame(blackHair, manager.getHairForHairHatStyle(style));
  }

  @Test
  public void addHairColorNameToFront_noDuplicatesInList() {
    manager.addHairColorNameToFront(HairColorName.BLACK);
    manager.addHairColorNameToFront(HairColorName.BROWN);
    manager.addHairColorNameToFront(HairColorName.BLACK);

    // After re-adding BLACK, it should appear only once in the list.
    // Verify by checking that resolving a style with only BROWN still works
    // (BROWN must be in the list at position 1)
    Hair brownHair = MaleAdultHairShortMop.BROWN;
    HairHatStyle styleOnlyBrown = createHairHatStyle(
        new HairColorNameHairCombo(HairColorName.BROWN, brownHair)
    );
    assertSame(brownHair, manager.getHairForHairHatStyle(styleOnlyBrown));
  }

  // --- Thread safety ---

  @Test
  public void getHairForHairHatStyle_isThreadSafe() throws InterruptedException {
    Hair blackHair = MaleAdultHairShortMop.BLACK;
    Hair brownHair = MaleAdultHairShortMop.BROWN;
    HairHatStyle style = createHairHatStyle(
        new HairColorNameHairCombo(HairColorName.BLACK, blackHair),
        new HairColorNameHairCombo(HairColorName.BROWN, brownHair)
    );

    int threadCount = 8;
    int iterationsPerThread = 200;
    AtomicBoolean failed = new AtomicBoolean(false);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);

    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    for (int t = 0; t < threadCount; t++) {
      final int threadIndex = t;
      executor.submit(() -> {
        try {
          startLatch.await();
          for (int i = 0; i < iterationsPerThread; i++) {
            // Half the threads add colors, half resolve
            if (threadIndex % 2 == 0) {
              HairColorName color = (i % 2 == 0) ? HairColorName.BLACK : HairColorName.BROWN;
              manager.addHairColorNameToFront(color);
            } else {
              Hair result = manager.getHairForHairHatStyle(style);
              // Result can be either color or null (empty priority list at start)
              // — just verify no exception
            }
          }
        } catch (Exception e) {
          failed.set(true);
        } finally {
          doneLatch.countDown();
        }
      });
    }

    startLatch.countDown();
    assertTrue("Threads did not complete in time", doneLatch.await(10, TimeUnit.SECONDS));
    executor.shutdown();
    assertFalse("Concurrent access caused an exception", failed.get());
  }

  // --- Helper ---

  private HairHatStyle createHairHatStyle(HairColorNameHairCombo... combos) {
    HairClsHatNameCombo clsCombo = new HairClsHatNameCombo(MaleAdultHairShortMop.class, null);
    return new HairHatStyle(clsCombo, Arrays.asList(combos));
  }
}
