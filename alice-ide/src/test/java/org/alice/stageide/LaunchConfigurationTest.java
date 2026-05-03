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
package org.alice.stageide;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LaunchConfigurationTest {
  @Test
  public void emptyArgumentsUseDefaultWindowAndMaximize() {
    LaunchConfiguration configuration = LaunchConfiguration.parse(new String[0]);

    assertEquals(0, configuration.getXLocation());
    assertEquals(0, configuration.getYLocation());
    assertEquals(LaunchConfiguration.DEFAULT_WIDTH, configuration.getWidth());
    assertEquals(LaunchConfiguration.DEFAULT_HEIGHT, configuration.getHeight());
    assertTrue(configuration.isMaximizationDesired());
    assertNull(configuration.getProjectFile());
    assertNull(configuration.getLocaleString());
  }

  @Test
  public void projectPathIsFirstNonLocaleArgument() {
    LaunchConfiguration configuration = LaunchConfiguration.parse(new String[] {"world.a3p"});

    assertEquals("world.a3p", configuration.getProjectFile().getPath());
    assertNull(configuration.getLocaleString());
    assertTrue(configuration.isMaximizationDesired());
  }

  @Test
  public void localeFlagConsumesLocaleBeforeProjectPath() {
    LaunchConfiguration configuration = LaunchConfiguration.parse(new String[] {"-l", "es", "world.a3p"});

    assertEquals("es", configuration.getLocaleString());
    assertEquals("world.a3p", configuration.getProjectFile().getPath());
  }

  @Test
  public void localeFlagIsCaseInsensitive() {
    LaunchConfiguration configuration = LaunchConfiguration.parse(new String[] {"-L", "fr"});

    assertEquals("fr", configuration.getLocaleString());
    assertNull(configuration.getProjectFile());
  }

  @Test
  public void validPositionDisablesMaximizationAndKeepsDefaultSizeWhenSizeIsMissing() {
    LaunchConfiguration configuration = LaunchConfiguration.parse(new String[] {"world.a3p", "11", "22"});

    assertEquals(11, configuration.getXLocation());
    assertEquals(22, configuration.getYLocation());
    assertEquals(LaunchConfiguration.DEFAULT_WIDTH, configuration.getWidth());
    assertEquals(LaunchConfiguration.DEFAULT_HEIGHT, configuration.getHeight());
    assertFalse(configuration.isMaximizationDesired());
  }

  @Test
  public void validPositionAndSizeDisableMaximization() {
    LaunchConfiguration configuration = LaunchConfiguration.parse(new String[] {"world.a3p", "11", "22", "333", "444"});

    assertEquals(11, configuration.getXLocation());
    assertEquals(22, configuration.getYLocation());
    assertEquals(333, configuration.getWidth());
    assertEquals(444, configuration.getHeight());
    assertFalse(configuration.isMaximizationDesired());
  }

  @Test
  public void invalidPositionFallsBackToDefaultWindowButKeepsProjectAndLocale() {
    LaunchConfiguration configuration = LaunchConfiguration.parse(new String[] {"-l", "de", "world.a3p", "bad", "22", "333", "444"});

    assertEquals("de", configuration.getLocaleString());
    assertEquals("world.a3p", configuration.getProjectFile().getPath());
    assertEquals(0, configuration.getXLocation());
    assertEquals(0, configuration.getYLocation());
    assertEquals(LaunchConfiguration.DEFAULT_WIDTH, configuration.getWidth());
    assertEquals(LaunchConfiguration.DEFAULT_HEIGHT, configuration.getHeight());
    assertTrue(configuration.isMaximizationDesired());
  }

  @Test
  public void localeFlagWithoutLocaleTreatsNoProjectAsPresent() {
    LaunchConfiguration configuration = LaunchConfiguration.parse(new String[] {"-l"});

    assertNull(configuration.getLocaleString());
    assertNull(configuration.getProjectFile());
    assertTrue(configuration.isMaximizationDesired());
  }
}

