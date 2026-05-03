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

import java.io.File;

final class LaunchConfiguration {
  static final int DEFAULT_WIDTH = 1000;
  static final int DEFAULT_HEIGHT = 740;

  private final int xLocation;
  private final int yLocation;
  private final int width;
  private final int height;
  private final boolean maximizationDesired;
  private final File projectFile;
  private final String localeString;

  private LaunchConfiguration(int xLocation, int yLocation, int width, int height, boolean maximizationDesired, File projectFile, String localeString) {
    this.xLocation = xLocation;
    this.yLocation = yLocation;
    this.width = width;
    this.height = height;
    this.maximizationDesired = maximizationDesired;
    this.projectFile = projectFile;
    this.localeString = localeString;
  }

  static LaunchConfiguration parse(String[] args) {
    int xLocation = 0;
    int yLocation = 0;
    int width = DEFAULT_WIDTH;
    int height = DEFAULT_HEIGHT;
    boolean maximizationDesired = true;
    File projectFile = null;
    String localeString = null;
    int index = 0;
    if (args.length > 0) {
      if ("-l".equalsIgnoreCase(args[0])) {
        index = 1;
        if (args.length > 1) {
          localeString = args[1];
          index = 2;
        }
      }
      if (args.length > index) {
        projectFile = new File(args[index]);
      }
      if (args.length > (index + 2)) {
        try {
          xLocation = Integer.parseInt(args[index + 1]);
          yLocation = Integer.parseInt(args[index + 2]);
          if (args.length > (index + 4)) {
            width = Integer.parseInt(args[index + 3]);
            height = Integer.parseInt(args[index + 4]);
          }
          maximizationDesired = false;
        } catch (NumberFormatException nfe) {
          xLocation = 0;
          yLocation = 0;
          width = DEFAULT_WIDTH;
          height = DEFAULT_HEIGHT;
        }
      }
    }
    return new LaunchConfiguration(xLocation, yLocation, width, height, maximizationDesired, projectFile, localeString);
  }

  int getXLocation() {
    return xLocation;
  }

  int getYLocation() {
    return yLocation;
  }

  int getWidth() {
    return width;
  }

  int getHeight() {
    return height;
  }

  boolean isMaximizationDesired() {
    return maximizationDesired;
  }

  File getProjectFile() {
    return projectFile;
  }

  String getLocaleString() {
    return localeString;
  }
}

