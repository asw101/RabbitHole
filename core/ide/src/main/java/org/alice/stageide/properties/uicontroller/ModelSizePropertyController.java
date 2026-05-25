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

package org.alice.stageide.properties.uicontroller;

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import org.alice.ide.properties.adapter.AbstractPropertyAdapter;
import org.alice.ide.properties.adapter.croquet.ModelSizePropertyValueOperation;
import org.alice.ide.properties.uicontroller.AbstractAdapterController;
import org.alice.ide.properties.uicontroller.DoubleTextField;
import org.alice.math.immutable.Dimension3;
import org.alice.stageide.properties.*;
import org.lgna.croquet.BooleanState;
import org.lgna.croquet.Operation;
import org.lgna.croquet.State;
import org.lgna.croquet.triggers.ActionEventTrigger;
import org.lgna.croquet.views.BooleanStateButton;
import org.lgna.croquet.views.BoxUtilities;
import org.lgna.croquet.views.Button;
import org.lgna.croquet.views.Label;
import org.lgna.croquet.views.SwingAdapter;
import org.lgna.story.SModel;
import org.lgna.story.implementation.BillboardImp;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.implementation.ModelImp;

import javax.swing.AbstractButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModelSizePropertyController extends AbstractAdapterController<Dimension3> {

  private State.ValueListener<Boolean> linkStateValueObserver = new State.ValueListener<Boolean>() {
    @Override
    public void changing(State<Boolean> state, Boolean prevValue, Boolean nextValue) {
    }

    @Override
    public void changed(State<Boolean> state, Boolean prevValue, Boolean nextValue) {
      ModelSizePropertyController.this.updateUIFromLinkState(state, prevValue, nextValue);
    }
  };

  private ActionListener valueChangeListener;

  private DoubleTextField widthField;
  private DoubleTextField heightField;
  private DoubleTextField depthField;

  private Label widthLabel;
  private Label heightLabel;
  private Label depthLabel;

  private Button resetButton;

  private BooleanStateButton<AbstractButton> linkAllButton;
  private BooleanStateButton<AbstractButton> linkXYButton;
  private BooleanStateButton<AbstractButton> linkXZButton;
  private BooleanStateButton<AbstractButton> linkYZButton;

  private boolean hasLinkAll = false;
  private boolean hasLinkXY = false;
  private boolean hasLinkXZ = false;
  private boolean hasLinkYZ = false;

  private boolean isUpdatingState = false;
  private boolean doUpdateOnAdapter = true;

  private static final int GLUE_X_POS = 8;
  private static final int RESET_X_POS = 7;
  private static final int SCALE_ALL_X_POS = 6;
  private static final int SCALE_XZ_X_POS = 5;
  private static final int SCALE_YZ_X_POS = 4;
  private static final int SCALE_XY_X_POS = 3;

  private static final Insets INSETS_2 = new Insets(2, 2, 2, 2);
  private static final Insets INSETS_0 = new Insets(0, 0, 0, 0);
  private static final Insets INSETS_XY_OFFSET = new Insets(16, 2, 2, 2);
  private static final Insets INSETS_YZ_OFFSET = new Insets(48, 2, 2, 2);

  private static GridBagConstraints gbc(int gridX, int gridY, int gridHeight, int anchor, Insets insets) {
    return new GridBagConstraints(gridX, gridY, 1, gridHeight, 0.0, 0.0, anchor, GridBagConstraints.NONE, insets, 0, 0);
  }

  public ModelSizePropertyController(ModelSizeAdapter propertyAdapter) {
    super(propertyAdapter);
    IsAllScaleLinkedState.getInstance().addValueListener(linkStateValueObserver);
    IsXYScaleLinkedState.getInstance().addValueListener(linkStateValueObserver);
    IsXZScaleLinkedState.getInstance().addValueListener(linkStateValueObserver);
    IsYZScaleLinkedState.getInstance().addValueListener(linkStateValueObserver);
  }

  @Override
  public Class<?> getPropertyType() {
    return SModel.class;
  }

  @Override
  protected void initializeComponents() {
    super.initializeComponents();
    this.valueChangeListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        ModelSizePropertyController.this.updateAdapterFromUI(e);
      }
    };

    String widthString = AbstractPropertyAdapter.getLocalizedString("Width");
    String heightString = AbstractPropertyAdapter.getLocalizedString("Height");
    String depthString = AbstractPropertyAdapter.getLocalizedString("Depth");
    this.widthLabel = new Label(widthString + ":");
    this.heightLabel = new Label(heightString + ":");
    this.depthLabel = new Label(depthString + ":");

    this.widthField = new DoubleTextField(3);
    this.widthField.addActionListener(this.valueChangeListener);

    this.heightField = new DoubleTextField(3);
    this.heightField.addActionListener(this.valueChangeListener);

    this.depthField = new DoubleTextField(3);
    this.depthField.addActionListener(this.valueChangeListener);

    this.linkXYButton = new LinkScaleButton(IsXYScaleLinkedState.getInstance());
    this.linkXZButton = new LinkScaleButton(IsXZScaleLinkedState.getInstance());
    this.linkYZButton = new LinkScaleButton(IsYZScaleLinkedState.getInstance());
    this.linkAllButton = new LinkScaleButton(IsAllScaleLinkedState.getInstance());

    this.addComponent(this.widthLabel, gbc(0, 0, 1, GridBagConstraints.EAST, INSETS_2));
    this.addComponent(new SwingAdapter(this.widthField), gbc(1, 0, 1, GridBagConstraints.WEST, INSETS_2));
    this.addComponent(this.heightLabel, gbc(0, 1, 1, GridBagConstraints.EAST, INSETS_2));
    this.addComponent(new SwingAdapter(this.heightField), gbc(1, 1, 1, GridBagConstraints.WEST, INSETS_2));
    this.addComponent(this.depthLabel, gbc(0, 2, 1, GridBagConstraints.EAST, INSETS_2));
    this.addComponent(new SwingAdapter(this.depthField), gbc(1, 2, 1, GridBagConstraints.WEST, INSETS_2));
    this.addComponent(BoxUtilities.createHorizontalGlue(), new GridBagConstraints(GLUE_X_POS, //gridX
                                                                                  0, //gridY
                                                                                  1, //gridWidth
                                                                                  3, //gridHeight
                                                                                  1.0, //weightX
                                                                                  1.0, //weightY
                                                                                  GridBagConstraints.CENTER, //anchor
                                                                                  GridBagConstraints.BOTH, //fill
                                                                                  INSETS_0, //insets
                                                                                  0, //ipadX
                                                                                  0) //ipadY
    );

  }

  @Override
  protected void updateUIFromNewAdapter() {
    super.updateUIFromNewAdapter();

    this.removeComponent(this.linkAllButton);
    this.removeComponent(this.linkXYButton);
    this.removeComponent(this.linkXZButton);
    this.removeComponent(this.linkYZButton);

    hasLinkAll = false;
    hasLinkXY = false;
    hasLinkXZ = false;
    hasLinkYZ = false;
    ModelSizePropertyControllerLogic.ResizerConfiguration configuration = ModelSizePropertyControllerLogic.analyzeResizers(
        (this.propertyAdapter != null) && (this.propertyAdapter.getInstance() != null)
            ? java.util.Arrays.asList(((ModelImp) this.propertyAdapter.getInstance()).getResizers())
            : java.util.Collections.emptyList());
    hasLinkAll = configuration.hasLinkAll;
    hasLinkXY = configuration.hasLinkXY;
    hasLinkXZ = configuration.hasLinkXZ;
    hasLinkYZ = configuration.hasLinkYZ;

    if (hasLinkAll) {
      this.addComponent(this.linkAllButton, gbc(SCALE_ALL_X_POS, 0, 3, GridBagConstraints.WEST, INSETS_2));
    }
    if (hasLinkXY) {
      this.addComponent(this.linkXYButton, gbc(SCALE_XY_X_POS, 0, 3, GridBagConstraints.NORTHWEST, INSETS_XY_OFFSET));
    }
    if (hasLinkXZ) {
      this.addComponent(this.linkXZButton, gbc(SCALE_XZ_X_POS, 0, 3, GridBagConstraints.WEST, INSETS_2));
    }
    if (hasLinkYZ) {
      this.addComponent(this.linkYZButton, gbc(SCALE_YZ_X_POS, 0, 3, GridBagConstraints.NORTHWEST, INSETS_YZ_OFFSET));
    }
    isUpdatingState = true;

    widthField.setEnabled(configuration.hasX);
    widthField.setEditable(configuration.hasX);
    heightField.setEnabled(configuration.hasY);
    heightField.setEditable(configuration.hasY);
    depthField.setEnabled(configuration.hasZ);
    depthField.setEditable(configuration.hasZ);

    IsAllScaleLinkedState.getInstance().setValueTransactionlessly(hasLinkAll);
    IsAllScaleLinkedState.getInstance().setEnabled(configuration.enableLinkAll);

    IsXYScaleLinkedState.getInstance().setEnabled(configuration.enableLinkXY);
    IsXYScaleLinkedState.getInstance().setValueTransactionlessly(configuration.initialXyValue());

    IsXZScaleLinkedState.getInstance().setEnabled(configuration.enableLinkXZ);
    IsXZScaleLinkedState.getInstance().setValueTransactionlessly(configuration.initialXzValue());

    IsYZScaleLinkedState.getInstance().setEnabled(configuration.enableLinkYZ);
    IsYZScaleLinkedState.getInstance().setValueTransactionlessly(configuration.initialYzValue());

    isUpdatingState = false;
    setResetButton();
  }

  private void setResetButton() {
    if ((this.resetButton != null) && (this.resetButton.getAwtComponent().getParent() != null)) {
      this.removeComponent(this.resetButton);
    }
    this.resetButton = null;
    if ((this.propertyAdapter == null) || (this.propertyAdapter.getInstance() == null)) {
      return;
    }
    ModelImp baseModel = (ModelImp) this.propertyAdapter.getInstance();
    if (!ModelSizePropertyControllerLogic.shouldOfferResetButton(baseModel instanceof JointedModelImp, baseModel instanceof BillboardImp)) {
      return;
    }
    Operation operation = new ModelSizePropertyValueOperation(this.propertyAdapter, getOriginalSize());
    operation.setName(AbstractPropertyAdapter.getLocalizedString("Reset"));
    this.resetButton = operation.createButton();
    this.addComponent(this.resetButton, gbc(RESET_X_POS, 0, 3, GridBagConstraints.WEST, INSETS_2));
  }

  private Dimension3 getOriginalSize() {
    ModelImp baseModel = (ModelImp) this.propertyAdapter.getInstance();
    Dimension3 scale = baseModel.getScale();
    Dimension3 size = baseModel.getSize();
    return size.dividedBy(scale);
  }

  private Dimension3 getModelSize() {
    ModelImp baseModel = (ModelImp) this.propertyAdapter.getInstance();
    return baseModel.getSize();
  }

  @Override
  protected void setValueOnUI(Dimension3 value) {
    if (value != null) {
      this.doUpdateOnAdapter = false;
      this.widthField.setValue(value.x());
      this.heightField.setValue(value.y());
      this.depthField.setValue(value.z());
      this.doUpdateOnAdapter = true;
      return;
    }
    //If we haven't set the scale value, set it to null
    this.widthField.setValue(null);
    this.heightField.setValue(null);
    this.depthField.setValue(null);
  }

  private Dimension3 getSizeFromUI(Object source) {
    double desiredWidth = widthField.getValue();
    double desiredHeight = heightField.getValue();
    double desiredDepth = depthField.getValue();
    if (Double.isNaN(desiredWidth) || Double.isNaN(desiredHeight) || Double.isNaN(desiredDepth)) {
      return null;
    }
    ModelSizePropertyControllerLogic.SourceAxis sourceAxis = ModelSizePropertyControllerLogic.resolveSourceAxis(
        source == widthField,
        source == heightField,
        source == depthField);
    Dimension3 desiredSize = new Dimension3(desiredWidth, desiredHeight, desiredDepth);
    if (sourceAxis == ModelSizePropertyControllerLogic.SourceAxis.NONE) {
      return desiredSize;
    }
    return ModelSizePropertyControllerLogic.computeSizeFromUi(desiredSize, this.getModelSize(), sourceAxis,
        IsAllScaleLinkedState.getInstance().getValue(),
        IsXYScaleLinkedState.getInstance().getValue(),
        IsXZScaleLinkedState.getInstance().getValue(),
        IsYZScaleLinkedState.getInstance().getValue());
  }

  private void updateUIFromLinkState(State<Boolean> state, Boolean prevValue, Boolean nextValue) {
    if (!isUpdatingState && (nextValue != prevValue)) {
      isUpdatingState = true;
      if (nextValue) {
        if (state == IsAllScaleLinkedState.getInstance()) {
          updateLinkState(hasLinkXY, IsXYScaleLinkedState.getInstance());
          updateLinkState(hasLinkXZ, IsXZScaleLinkedState.getInstance());
          updateLinkState(hasLinkYZ, IsYZScaleLinkedState.getInstance());
        } else if (state == IsXYScaleLinkedState.getInstance()) {
          updateLinkState(hasLinkAll, IsAllScaleLinkedState.getInstance());
          updateLinkState(hasLinkXZ, IsXZScaleLinkedState.getInstance());
          updateLinkState(hasLinkYZ, IsYZScaleLinkedState.getInstance());
        } else if (state == IsXZScaleLinkedState.getInstance()) {
          updateLinkState(hasLinkAll, IsAllScaleLinkedState.getInstance());
          updateLinkState(hasLinkXY, IsXYScaleLinkedState.getInstance());
          updateLinkState(hasLinkYZ, IsYZScaleLinkedState.getInstance());
        } else if (state == IsYZScaleLinkedState.getInstance()) {
          updateLinkState(hasLinkAll, IsAllScaleLinkedState.getInstance());
          updateLinkState(hasLinkXZ, IsXZScaleLinkedState.getInstance());
          updateLinkState(hasLinkXY, IsXYScaleLinkedState.getInstance());
        }
      }
      isUpdatingState = false;
    }
  }

  private void updateLinkState(boolean isPresent, BooleanState xy) {
    if (isPresent) {
      xy.setValueTransactionlessly(!xy.isEnabled());
    }
  }

  protected void updateAdapterFromUI(ActionEvent e) {
    if (!this.doUpdateOnAdapter) {
      return;
    }
    Dimension3 newScale = getSizeFromUI(e.getSource());
    if (newScale == null || newScale.equals(this.propertyAdapter.getValue())) {
      return;
    }
    if ((this.propertyAdapter.getLastSetValue() != null) && this.propertyAdapter.getLastSetValue().equals(newScale)) {
      return;
    }
    if (newScale.hasNegativeComponents()) {
      String modelName = this.propertyAdapter.getInstance().getClass().getSimpleName();
      Logger.outln("Restricting size for " + modelName + " to a near-zero non-negative value");
      newScale = ModelSizePropertyControllerLogic.clampNegativeScale(propertyAdapter.getValue(), newScale);
    }
    Operation operation = new ModelSizePropertyValueOperation(this.propertyAdapter, newScale);
    operation.fire(ActionEventTrigger.createUserActivity(e));
  }
}
