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
import org.lgna.croquet.*;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.triggers.Trigger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.UUID;

/**
 * A tabbed pane with folder-style curved tabs. Delegates tab painting to
 * {@link FolderTitlesPanel}, tab title UI to {@link FolderTabTitleUI} /
 * {@link JFolderTabTitle}, and drag-scroll behaviour to {@link TabScrollListener}.
 *
 * @author Dennis Cosgrove
 */
public class FolderTabbedPane<E extends TabComposite<?>> extends CardBasedTabbedPane<E> {
  static final int TRAILING_TAB_PAD = 32;
  private static final int OUTLINE_THICKNESS = 1;

  private class FolderTabTitle extends BooleanStateButton<javax.swing.AbstractButton> {
    private final JButton closeButton;

    public FolderTabTitle(final E item, BooleanState booleanState) {
      super(booleanState);

      if (item.isPotentiallyCloseable()) {
        ActionListener closeButtonActionListener = e -> FolderTabbedPane.this.getModel().removeItemAndSelectAppropriateReplacement(item);
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

  private final FolderTitlesPanel titlesPanel = this.createTitlesPanel();
  private final ScrollPane titlesScrollPane = new ScrollPane(this.titlesPanel);
  private final BorderPanel innerHeaderPanel = new BorderPanel();
  private final BorderPanel outerHeaderPanel = new BorderPanel();

  protected FolderTitlesPanel createTitlesPanel() {
    return new FolderTitlesPanel();
  }

  private Action getActionFor(E item) {
    Operation operation = this.getModel().getItemSelectionOperation(item);
    operation.initializeIfNecessary();
    return operation.getImp().getSwingModel().getAction();
  }

  //todo: PopupOperation
  private class PopupOperation extends ActionOperation {
    public PopupOperation() {
      super(Application.DOCUMENT_UI_GROUP, UUID.fromString("7923b4c8-6a9f-4c8b-99b5-909ae6c0889a"));
    }

    @Override
    protected void localize() {
      super.localize();
      this.setName(">>");
    }

    @Override
    protected void perform(UserActivity activity) {
      Trigger trigger = activity.getTrigger();
      JPopupMenu popupMenu = new JPopupMenu();
      ButtonGroup buttonGroup = new ButtonGroup();
      for (E item : FolderTabbedPane.this.getModel()) {
        if (item != null) {
          JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(getActionFor(item));
          checkBox.setSelected(FolderTabbedPane.this.getModel().getValue() == item);
          popupMenu.add(checkBox);
          buttonGroup.add(checkBox);
        } else {
          popupMenu.addSeparator();
        }
      }
      ViewController<?, ?> viewController = trigger.getViewController();
      popupMenu.show(viewController.getAwtComponent(), 0, viewController.getHeight());
    }
  }
  private class PopupButton extends OperationButton<JButton, Operation> {
    public PopupButton(Operation operation) {
      super(operation);
    }

    @Override
    protected final JButton createAwtComponent() {
      JButton rv = new JButton() {
        @Override
        public String getText() {
          if (isTextClobbered()) {
            return getClobberText();
          } else {
            return super.getText();
          }
        }

        private boolean isNecessary() {
          // have we opened so many tabs that we need this button to show up for selecting tabs that are out of view?
          Container parent = this.getParent();
          if (parent != null) {
            int width = parent.getWidth();
            int preferredWidth = parent.getPreferredSize().width;
            return width < (preferredWidth - TRAILING_TAB_PAD);
          } else {
            return false;
          }
        }

        @Override
        public void paint(Graphics g) {
          if (isNecessary()) {
            super.paint(g);
          } else {
            g.setColor(FolderTabbedPane.this.getBackgroundColor());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
          }
        }

        @Override
        public boolean contains(int x, int y) {
          if (isNecessary()) {
            return super.contains(x, y);
          } else {
            return false;
          }
        }
      };
      rv.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
      return rv;
    }
  }

  public FolderTabbedPane(TabState<E, ?> model) {
    super(model);
    CardOwnerComposite cardOwner = this.getCardOwner();
    this.titlesScrollPane.setHorizontalScrollbarPolicy(ScrollPane.HorizontalScrollbarPolicy.NEVER);
    this.titlesScrollPane.setVerticalScrollbarPolicy(ScrollPane.VerticalScrollbarPolicy.NEVER);

    TabScrollListener scrollListener = new TabScrollListener(this.titlesScrollPane);
    this.titlesScrollPane.addMouseListener(scrollListener);
    this.titlesScrollPane.addMouseMotionListener(scrollListener);

    this.titlesScrollPane.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
    cardOwner.getView().setBorder(new Border() {
      @Override
      public Insets getBorderInsets(Component c) {
        return new Insets(OUTLINE_THICKNESS, OUTLINE_THICKNESS, OUTLINE_THICKNESS, OUTLINE_THICKNESS);
      }

      @Override
      public boolean isBorderOpaque() {
        return true;
      }

      // this is for the border around the area under the tabs
      @Override
      public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (titlesPanel.getComponentCount() <= 0) {
          return;
        }
        for (AwtComponentView<?> component : titlesPanel.getComponents()) {
          if (component instanceof AbstractButton<?, ?> button && button.getAwtComponent().getModel().isSelected()) {
            Rectangle bounds = button.getBounds(AwtContainerView.lookup(c));
            Color background = button.getBackgroundColor();
            g.setColor(ColorUtilities.scaleHSB(background, 1, 4.8, .74));
            // top
            g.fillRect(x, y, width, OUTLINE_THICKNESS);
            // bottom
            g.fillRect(x, y + height - 1, width, OUTLINE_THICKNESS);
            // right
            g.fillRect(x + width - 1, y, OUTLINE_THICKNESS, height);
            // left
            g.fillRect(x, y, OUTLINE_THICKNESS, height);

            // re-draw over the top of the border where the tab will be so it looks seamless
            g.setColor(background);
            int x0 = c.getComponentOrientation().isLeftToRight() ? bounds.x : bounds.x - TRAILING_TAB_PAD;
            g.fillRect(x0, y, (bounds.width - 1) + TRAILING_TAB_PAD, OUTLINE_THICKNESS);
            break;
          }
        }
      }
    });
    PopupOperation popupOperation = new PopupOperation();
    this.setInnerHeaderTrailingComponent(new PopupButton(popupOperation));

  }
  public void setHeaderLeadingComponent(SwingComponentView<?> component) {
    if (component != null) {
      component.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      this.innerHeaderPanel.addLineStartComponent(component);
    } else {
      AwtComponentView<?> prevComponent = this.innerHeaderPanel.getLineStartComponent();
      if (prevComponent != null) {
        this.innerHeaderPanel.removeComponent(prevComponent);
      }
    }
    this.innerHeaderPanel.revalidateAndRepaint();
  }

  private void setInnerHeaderTrailingComponent(SwingComponentView<?> component) {
    if (component != null) {
      if (!component.isOpaque()) {
        component.setBackgroundColor(this.getBackgroundColor());
      }
      component.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      this.innerHeaderPanel.addLineEndComponent(component);
    } else {
      AwtComponentView<?> prevComponent = this.innerHeaderPanel.getLineEndComponent();
      if (prevComponent != null) {
        this.innerHeaderPanel.removeComponent(prevComponent);
      }
    }
    this.innerHeaderPanel.revalidateAndRepaint();
  }

  public void setHeaderTrailingComponent(SwingComponentView<?> component) {
    if (component != null) {
      if (!component.isOpaque()) {
        component.setBackgroundColor(this.getBackgroundColor());
      }
      component.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      this.outerHeaderPanel.addLineEndComponent(component);
    } else {
      AwtComponentView<?> prevComponent = this.outerHeaderPanel.getLineEndComponent();
      if (prevComponent != null) {
        this.outerHeaderPanel.removeComponent(prevComponent);
      }
    }
    this.outerHeaderPanel.revalidateAndRepaint();
  }

  @Override
  protected JPanel createAwtComponent() {
    JPanel rv = super.createAwtComponent();
    this.innerHeaderPanel.addCenterComponent(this.titlesScrollPane);
    this.outerHeaderPanel.addCenterComponent(this.innerHeaderPanel);
    rv.add(this.outerHeaderPanel.getAwtComponent(), BorderLayout.PAGE_START);
    rv.add(this.getCardOwner().getView().getAwtComponent(), BorderLayout.CENTER);
    return rv;
  }

  @Override
  protected LayoutManager createLayoutManager(JPanel jPanel) {
    return new BorderLayout();
  }

  @Override
  protected BooleanStateButton<? extends javax.swing.AbstractButton> createTitleButton(E item, BooleanState itemSelectedState) {
    return new FolderTabTitle(item, itemSelectedState);
  }

  @Override
  protected void removeAllDetails() {
    super.removeAllDetails();
    this.titlesPanel.removeAllComponents();
  }

  @Override
  protected void addItem(E item, BooleanStateButton<?> button) {
    super.addItem(item, button);
    button.updateFor(item);
    this.titlesPanel.addComponent(button);
  }

  @Override
  protected void addSeparator() {
    super.addSeparator();
    this.titlesPanel.addComponent(BoxUtilities.createHorizontalSliver(16));
  }

  @Override
  public void setBackgroundColor(Color color) {
    super.setBackgroundColor(color);
    this.titlesPanel.setBackgroundColor(color);
    this.titlesScrollPane.setBackgroundColor(color);
  }

  @Override
  public void setForegroundColor(Color color) {
    super.setForegroundColor(color);
    this.titlesPanel.setForegroundColor(color);
    this.titlesScrollPane.setForegroundColor(color);
  }
}
