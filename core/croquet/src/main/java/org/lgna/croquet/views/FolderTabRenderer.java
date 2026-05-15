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
import edu.cmu.cs.dennisc.javax.swing.SpringUtilities;
import edu.cmu.cs.dennisc.javax.swing.components.JCloseButton;
import org.lgna.croquet.BooleanState;
import org.lgna.croquet.SingleSelectListState;
import org.lgna.croquet.TabComposite;

import javax.swing.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

/**
 * Tab rendering classes extracted from FolderTabbedPane.
 * Contains FolderTabTitleUI, JFolderTabTitle, and FolderTabTitle.
 *
 * @author Dennis Cosgrove
 */

class FolderTabTitleUI extends BasicToggleButtonUI {
  @Override
  public Dimension getPreferredSize(JComponent c) {
    javax.swing.AbstractButton button = (javax.swing.AbstractButton) c;
    Font font = button.getFont();
    FontMetrics fm = button.getFontMetrics(font);
    String text = button.getText();
    Icon icon = button.getIcon();
    Dimension size;
    if (icon != null) {
      int verticalAlignment = button.getVerticalAlignment();
      int horizontalAlignment = button.getHorizontalAlignment();
      int verticalTextPosition = button.getVerticalTextPosition();
      int horizontalTextPosition = button.getHorizontalTextPosition();
      Rectangle viewR = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
      Rectangle iconR = new Rectangle();
      Rectangle textR = new Rectangle();
      int textIconGap = button.getIconTextGap();
      SwingUtilities.layoutCompoundLabel(c, fm, text, icon, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition, viewR, iconR, textR, textIconGap);

      size = iconR.union(textR).getSize();
    } else {
      size = fm.getStringBounds(text, button.getGraphics()).getBounds().getSize();
    }

    Insets insets = button.getInsets();
    size.width += insets.left + insets.right;
    size.height += insets.top + insets.bottom;

    if (button.getComponentCount() > 0) {
      for (Component component : button.getComponents()) {
        size.width += 4;
        size.width += component.getPreferredSize().width;
      }
    }

    return size;
  }

  @Override
  public void paint(Graphics g, JComponent c) {
    javax.swing.AbstractButton button = (javax.swing.AbstractButton) c;
    Icon icon = button.getIcon();
    if (icon != null) {
      super.paint(g, c);
    } else {
      String text = button.getText();
      Insets insets = button.getInsets();
      int x = insets.left;
      if (!button.getComponentOrientation().isLeftToRight()) {
        for (Component component : button.getComponents()) {
          x += component.getPreferredSize().width;
          x += 4;
        }
      }
      Color outlineColor = ColorUtilities.scaleHSB(button.getBackground(), 1, 4.8, .74);
      g.setColor(button.isSelected() ? UIManager.getColor("TabbedPane.foreground") :  button.getModel().isRollover() ? UIManager.getColor("TabbedPane.disabledForeground") : outlineColor);
      g.drawString(text, x, button.getBaseline(c.getWidth(), c.getHeight()));
    }
  }
}

class JFolderTabTitle extends JToggleButton {
  private final ItemListener itemListener = e -> JFolderTabTitle.this.revalidate();

  JFolderTabTitle() {
    this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 8));
    this.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.setAlignmentY(Component.BOTTOM_ALIGNMENT);
    this.setHorizontalTextPosition(SwingConstants.LEADING);
    this.setHorizontalAlignment(SwingConstants.LEADING);
    this.setLayout(new SpringLayout());
  }

  @Override
  public boolean isOpaque() {
    return false;
  }

  @Override
  public void updateUI() {
    this.setUI(new FolderTabTitleUI());
  }

  @Override
  public void addNotify() {
    super.addNotify();
    this.getModel().addItemListener(this.itemListener);
  }

  @Override
  public void removeNotify() {
    this.getModel().removeItemListener(this.itemListener);
    super.removeNotify();
  }

  @Override
  public void repaint() {
    Container parent = this.getParent();
    if (parent != null) {
      parent.repaint(this.getX(), this.getY(), this.getWidth() + FolderTabbedPane.TRAILING_TAB_PAD, this.getHeight());
    } else {
      super.repaint();
    }
  }
}

@SuppressWarnings({"rawtypes", "unchecked"})
class FolderTabTitle extends BooleanStateButton<javax.swing.AbstractButton> {
  private final JButton closeButton;
  private final SingleSelectListState model;

  FolderTabTitle(final TabComposite<?> item, BooleanState booleanState, SingleSelectListState model) {
    super(booleanState);
    this.model = model;

    if (item.isPotentiallyCloseable()) {
      ActionListener closeButtonActionListener = e -> this.model.removeItemAndSelectAppropriateReplacement(item);
      this.closeButton = new JCloseButton();
      this.closeButton.addActionListener(closeButtonActionListener);
    } else {
      this.closeButton = null;
    }
  }

  @Override
  public <F extends TabComposite<?>> void updateFor(F item) {
    setCloseable(item.isCloseable());
  }

  public void setCloseable(boolean isCloseable) {
    if (this.closeButton != null) {
      if (isCloseable == (this.closeButton.getParent() == null)) {
        javax.swing.AbstractButton awtButton = this.getAwtComponent();
        if (isCloseable) {
          SpringUtilities.Horizontal hOrientation = this.getComponentOrientation().isLeftToRight() ?  SpringUtilities.Horizontal.EAST : SpringUtilities.Horizontal.WEST;
          SpringUtilities.add(awtButton, this.closeButton, hOrientation, -1, SpringUtilities.Vertical.NORTH, 5);
        } else {
          awtButton.remove(this.closeButton);
        }
        awtButton.revalidate();
        awtButton.repaint();
      }
    }
  }

  @Override
  protected javax.swing.AbstractButton createAwtComponent() {
    return new JFolderTabTitle();
  }
}
