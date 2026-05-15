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

import edu.cmu.cs.dennisc.java.awt.ColorUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Tab strip layout panel, promoted from FolderTabbedPane.TitlesPanel.
 * Handles tab geometry painting and preferred size calculation.
 *
 * @author Dennis Cosgrove
 */
class FolderTitlesPanel extends LineAxisPanel {
  private static final int NORTH_AREA_PAD = 1;

  protected static class JTitlesPanel extends JPanel {
    @Override
    public Dimension getPreferredSize() {
      Dimension rv = super.getPreferredSize();
      rv.width += FolderTabbedPane.TRAILING_TAB_PAD;
      return rv;
    }

    private GeneralPath addToPath(GeneralPath rv, float x, float y, float width, float height) {
      float a = height * 0.25f;

      float xStart;
      float xEnd;
      float xA;
      float tabPad;
      if (this.getComponentOrientation().isLeftToRight()) {
        xStart = x;
        xEnd = (x + width) - 1;
        tabPad = FolderTabbedPane.TRAILING_TAB_PAD;
        xA = xStart + a;
      } else {
        xStart = (x + width) - 1;
        xEnd = x;
        tabPad = -FolderTabbedPane.TRAILING_TAB_PAD;
        xA = xStart - a;
      }

      float xCurve0 = xEnd - (tabPad / 2);
      float xCurve1 = xEnd + tabPad;
      float cx0 = xCurve0 + (tabPad * 0.75f);
      float cx1 = xCurve0;

      float y0 = y + NORTH_AREA_PAD;
      float y1 = y + height + 1;
      float cy0 = y0;
      float cy1 = y1;

      float yA = y + a;

      rv.moveTo(xCurve1, y1);

      rv.lineTo(xCurve1, y1 - 1);
      rv.curveTo(cx1, cy1, cx0, cy0, xCurve0, y0);
      rv.lineTo(xA, y0);
      rv.quadTo(xStart, y0, xStart, yA);
      rv.lineTo(xStart, y1);

      return rv;
    }

    private void paintTab(Graphics2D g2, javax.swing.AbstractButton button) {
      Color prevColor = g2.getColor();
      Shape prevClip = g2.getClip();

      try {
        int x = button.getX();
        int y = button.getY();
        int width = button.getWidth();
        int height = button.getHeight();

        Color color = button.getBackground();
        Color outlineColor = ColorUtilities.scaleHSB(color, 1, 4.8, .74);

        if (button.isSelected()) {
          // draw one more pixel down to connect with the panel outline
          Rectangle bounds = prevClip.getBounds();
          bounds.height += 1;
          g2.setClip(bounds);
        } else {
          color = button.getModel().isRollover() ? color : ColorUtilities.scaleHSB(color, 1, .5, 1);
        }
        g2.setColor(color);

        GeneralPath path = addToPath(new GeneralPath(), x, y, width, height);

        // draw the background before the outline
        g2.fill(path);
        g2.setColor(outlineColor);
        g2.draw(path);
      } finally {
        g2.setColor(prevColor);
      }
    }

    @Override
    protected void paintChildren(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      Object prevAntialiasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      javax.swing.AbstractButton selectedButton = null;
      Component[] components = this.getComponents();
      final int N = components.length;
      for (int i = 0; i < N; i++) {
        Component component = components[N - 1 - i];
        if (component instanceof javax.swing.AbstractButton button) {
          if (button.isSelected()) {
            selectedButton = button;
          } else {
            this.paintTab(g2, button);
          }
        }
      }
      // paint selected button last so that it shows up on top
      if (selectedButton != null) {
        this.paintTab(g2, selectedButton);
      }
      super.paintChildren(g2);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, prevAntialiasing == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : prevAntialiasing);
    }
  }

  @Override
  protected JPanel createJPanel() {
    return new JTitlesPanel();
  }
}
