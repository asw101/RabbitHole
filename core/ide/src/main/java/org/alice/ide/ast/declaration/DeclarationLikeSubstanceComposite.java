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
package org.alice.ide.ast.declaration;

import edu.cmu.cs.dennisc.tree.DefaultNode;
import org.alice.ide.IDE;
import org.alice.ide.ast.declaration.views.DeclarationLikeSubstanceView;
import org.alice.ide.croquet.codecs.NodeCodec;
import org.alice.ide.croquet.models.ast.declaration.OtherTypesMenuModel;
import org.alice.ide.croquet.models.ast.declaration.TypeFillIn;
import org.alice.ide.croquet.models.declaration.InitializerStateOwner;
import org.alice.ide.croquet.models.ui.preferences.IsIncludingProgramType;
import org.alice.ide.custom.ArrayCustomExpressionCreatorComposite;
import org.alice.ide.preview.PreviewContainingOperationInputDialogCoreComposite;
import org.alice.stageide.type.croquet.OtherTypeDialog;
import org.lgna.croquet.AbstractSeverityStatusComposite;
import org.lgna.croquet.Application;
import org.lgna.croquet.BooleanState;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.CascadeFillIn;
import org.lgna.croquet.CascadeLineSeparator;
import org.lgna.croquet.CustomItemState;
import org.lgna.croquet.Operation;
import org.lgna.croquet.StringState;
import org.lgna.croquet.imp.cascade.BlankNode;
import org.lgna.croquet.views.Dialog;
import org.lgna.project.Project;
import org.lgna.project.annotations.ValueDetails;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.ArrayInstanceCreation;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.Node;
import org.lgna.project.ast.StaticAnalysisUtilities;
import org.lgna.project.ast.UserType;
import org.lgna.story.SThing;

import java.util.List;
import java.util.UUID;

/**
 * @author Dennis Cosgrove
 */
public abstract class DeclarationLikeSubstanceComposite<N extends Node> extends PreviewContainingOperationInputDialogCoreComposite<DeclarationLikeSubstanceView, N> implements InitializerStateOwner {
  protected static enum ApplicabilityStatus {
    EDITABLE(3), DISPLAYED(2), APPLICABLE_BUT_NOT_DISPLAYED(1), NOT_APPLICABLE(0);
    private final int value;

    private ApplicabilityStatus(int value) {
      this.value = value;
    }

    public boolean isEditable() {
      return this.value >= EDITABLE.value;
    }

    public boolean isDisplayed() {
      return this.value >= DISPLAYED.value;
    }

    public boolean isApplicable() {
      return this.value >= APPLICABLE_BUT_NOT_DISPLAYED.value;
    }
  }

  protected static class Details {
    private ApplicabilityStatus isFinalStatus = ApplicabilityStatus.NOT_APPLICABLE;
    private boolean inFinalInitialValue;
    private ApplicabilityStatus valueComponentTypeStatus = ApplicabilityStatus.NOT_APPLICABLE;
    private AbstractType<?, ?, ?> valueComponentTypeInitialValue;
    private ApplicabilityStatus valueIsArrayTypeStatus = ApplicabilityStatus.NOT_APPLICABLE;
    private boolean valueIsArrayTypeInitialValue;
    private ApplicabilityStatus nameStatus = ApplicabilityStatus.NOT_APPLICABLE;
    private String nameInitialValue;
    private ApplicabilityStatus initializerStatus = ApplicabilityStatus.NOT_APPLICABLE;
    private Expression initializerInitialValue;

    public Details isFinal(ApplicabilityStatus status, boolean initialValue) {
      this.isFinalStatus = status;
      this.inFinalInitialValue = initialValue;
      return this;
    }

    public Details valueComponentType(ApplicabilityStatus status, AbstractType<?, ?, ?> initialValue) {
      this.valueComponentTypeStatus = status;
      this.valueComponentTypeInitialValue = initialValue;
      return this;
    }

    public Details valueIsArrayType(ApplicabilityStatus status, boolean initialValue) {
      this.valueIsArrayTypeStatus = status;
      this.valueIsArrayTypeInitialValue = initialValue;
      return this;
    }

    public Details name(ApplicabilityStatus status, String initialValue) {
      this.nameStatus = status;
      this.nameInitialValue = initialValue;
      return this;
    }

    public Details name(ApplicabilityStatus status) {
      return this.name(status, "");
    }

    public Details initializer(ApplicabilityStatus status, Expression initialValue) {
      this.initializerStatus = status;
      this.initializerInitialValue = initialValue;
      return this;
    }
  }

  private final BooleanState isFinalState;
  private final CustomItemState<AbstractType> valueComponentTypeState;
  private final BooleanState valueIsArrayTypeState;
  private final StringState nameState;
  private final CustomItemState<Expression> initializerState;

  private final ErrorStatus errorStatus = this.createErrorStatus("errorStatus");

  private final DeclarationValidationDelegate validationDelegate = new DeclarationValidationDelegate(this);
  private final DeclarationDialogLifecycleDelegate lifecycleDelegate = new DeclarationDialogLifecycleDelegate(this);

  private final Details details;

  private static class ValueComponentTypeCustomizer implements ItemStateCustomizer<AbstractType> {
    @Override
    public CascadeFillIn getFillInFor(AbstractType type) {
      return TypeFillIn.getInstance(type);
    }

    @Override
    public void prologue() {
    }

    @Override
    public void epilogue() {
    }

    private void appendBlankChildren(List<CascadeBlankChild> blankChildren, NamedUserType programType, DefaultNode<NamedUserType> node) {
      NamedUserType type = node.getValue();
      if (type != null) {
        final boolean IS_PROGRAM_TYPE_EVER_A_GOOD_IDEA_TO_INCLUDE = false;
        boolean isProgramTypeIncluded = IS_PROGRAM_TYPE_EVER_A_GOOD_IDEA_TO_INCLUDE && IsIncludingProgramType.getInstance().getValue();
        if (isProgramTypeIncluded || (type != programType)) {
          blankChildren.add(this.getFillInFor(type));
        }
      }
      for (DefaultNode<NamedUserType> child : node.getChildren()) {
        appendBlankChildren(blankChildren, programType, child);
      }
    }

    @Override
    public void appendBlankChildren(List<CascadeBlankChild> blankChildren, BlankNode<AbstractType> blankNode) {
      for (JavaType type : IDE.getActiveInstance().getApiConfigurationManager().getPrimeTimeSelectableJavaTypes()) {
        blankChildren.add(this.getFillInFor(type));
      }
      blankChildren.add(CascadeLineSeparator.getInstance());
      Project project = IDE.getActiveInstance().getProject();

      //org.lgna.project.ast.NamedUserType programType = project.getProgramType();
      //edu.cmu.cs.dennisc.tree.DefaultNode<org.lgna.project.ast.NamedUserType> root = org.lgna.project.ProgramTypeUtilities.getNamedUserTypesAsTree( project );
      //appendBlankChildren( blankChildren, programType, root );

      blankChildren.add(CascadeLineSeparator.getInstance());
      blankChildren.add(OtherTypeDialog.getInstance().getValueCreator(SThing.class).getFillIn());
      OtherTypesMenuModel otherTypesMenuModel = OtherTypesMenuModel.getInstance();
      if (!otherTypesMenuModel.isEmpty()) {
        blankChildren.add(CascadeLineSeparator.getInstance());
        blankChildren.add(otherTypesMenuModel);
      }
    }
  }

  public DeclarationLikeSubstanceComposite(UUID migrationId, Details details) {
    super(migrationId, Application.PROJECT_GROUP);

    this.details = details;

    if (details.isFinalStatus.isApplicable()) {
      this.isFinalState = this.createBooleanState("isFinalState", details.inFinalInitialValue);
      if (details.isFinalStatus.isDisplayed()) {
        this.isFinalState.setEnabled(details.valueComponentTypeStatus.isEditable());
      }
    } else {
      this.isFinalState = null;
    }

    if (details.valueComponentTypeStatus.isApplicable()) {
      this.valueComponentTypeState = this.createCustomItemState("valueComponentTypeState", NodeCodec.getInstance(AbstractType.class), details.valueComponentTypeInitialValue, new ValueComponentTypeCustomizer());
      if (details.valueComponentTypeStatus.isDisplayed()) {
        this.valueComponentTypeState.setEnabled(details.valueComponentTypeStatus.isEditable());
      }
    } else {
      this.valueComponentTypeState = null;
    }

    if (details.valueIsArrayTypeStatus.isApplicable()) {
      this.valueIsArrayTypeState = this.createBooleanState("valueIsArrayTypeState", details.valueIsArrayTypeInitialValue);
      if (details.valueIsArrayTypeStatus.isDisplayed()) {
        this.valueIsArrayTypeState.setEnabled(details.valueIsArrayTypeStatus.isEditable());
      }
    } else {
      this.valueIsArrayTypeState = null;
    }

    if (details.nameStatus.isApplicable()) {
      this.nameState = this.createStringState("nameState", details.nameInitialValue);
      if (details.nameStatus.isDisplayed()) {
        this.nameState.setEnabled(details.nameStatus.isEditable());
      }
    } else {
      this.nameState = null;
    }

    if (details.initializerStatus.isApplicable()) {
      this.initializerState = this.createInitializerState(details.initializerInitialValue);
      if (details.initializerStatus.isDisplayed()) {
        this.initializerState.setEnabled(details.initializerStatus.isEditable());
      }
    } else {
      this.initializerState = null;
    }
  }

  private class InitializerCustomizer implements ItemStateCustomizer<Expression> {
    @Override
    public CascadeFillIn getFillInFor(Expression value) {
      //todo
      if (value instanceof ArrayInstanceCreation arrayInstanceCreation) {
        return ArrayCustomExpressionCreatorComposite.getInstance(arrayInstanceCreation.getType()).getValueCreator().getFillIn();
      } else {
        return null;
      }
    }

    @Override
    public void prologue() {
    }

    @Override
    public void epilogue() {
    }

    @Override
    public void appendBlankChildren(List<CascadeBlankChild> blankChildren, BlankNode<Expression> blankNode) {
      ValueDetails valueDetails = null;
      AbstractType<?, ?, ?> type = DeclarationLikeSubstanceComposite.this.getValueType();
      IDE.getActiveInstance().getExpressionCascadeManager().appendItems(blankChildren, blankNode, type, valueDetails);
    }
  }

  protected ItemStateCustomizer<Expression> createInitializerCustomizer() {
    return new InitializerCustomizer();
  }

  protected final CustomItemState<Expression> createInitializerState(Expression initialValue) {
    return this.createCustomItemState("initializerState", NodeCodec.getInstance(Expression.class), initialValue, this.createInitializerCustomizer());
  }

  public BooleanState getIsFinalState() {
    return this.isFinalState;
  }

  public CustomItemState<AbstractType> getValueComponentTypeState() {
    return this.valueComponentTypeState;
  }

  public BooleanState getValueIsArrayTypeState() {
    return this.valueIsArrayTypeState;
  }

  public StringState getNameState() {
    return this.nameState;
  }

  public CustomItemState<Expression> getInitializerState() {
    return this.initializerState;
  }

  public boolean isValueComponentTypeDisplayed() {
    return DeclarationLikeSubstanceCompositeHelper.isDisplayed(details.valueComponentTypeStatus);
  }

  public boolean isValueIsArrayTypeStateDisplayed() {
    return DeclarationLikeSubstanceCompositeHelper.isDisplayed(details.valueIsArrayTypeStatus);
  }

  public boolean isInitializerDisplayed() {
    return DeclarationLikeSubstanceCompositeHelper.isDisplayed(details.initializerStatus);
  }

  public abstract UserType<?> getDeclaringType();

  public AbstractType<?, ?, ?> getValueComponentType() {
    if (this.valueComponentTypeState != null) {
      return this.valueComponentTypeState.getValue();
    } else {
      return null;
    }
  }

  @Override
  public AbstractType<?, ?, ?> getValueType() {
    return DeclarationLikeSubstanceCompositeHelper.resolveValueType(this.getValueComponentType(), (this.valueIsArrayTypeState != null) && this.valueIsArrayTypeState.getValue());
  }

  public String getDeclarationLikeSubstanceName() {
    if (this.nameState != null) {
      return this.nameState.getValue();
    } else {
      return null;
    }
  }

  public Expression getInitializer() {
    if (this.initializerState != null) {
      return DeclarationLikeSubstanceCompositeHelper.normalizeInitializer(this.initializerState.getValue());
    } else {
      return null;
    }
  }

  String findLocalizedTextForDelegate(String key) {
    return this.findLocalizedText(key);
  }

  @Override
  protected AbstractSeverityStatusComposite.Status getStatusPreRejectorCheck() {
    if (validationDelegate.computeStatus(this.errorStatus)) {
      return this.errorStatus;
    } else {
      return IS_GOOD_TO_GO_STATUS;
    }
  }

  boolean isValueComponentTypeEditable() {
    return this.valueComponentTypeState != null && this.valueComponentTypeState.isEnabled();
  }

  boolean isValueIsArrayTypeEditable() {
    return this.valueIsArrayTypeState != null && this.valueIsArrayTypeState.isEnabled();
  }

  boolean isNameEditable() {
    return this.nameState != null && this.nameState.isEnabled();
  }

  boolean isInitializerEditable() {
    return this.initializerState != null && this.initializerState.isEnabled();
  }

  protected final boolean isNameValid(String name) {
    return StaticAnalysisUtilities.isValidIdentifier(name);
  }

  protected abstract boolean isNameAvailable(String name);

  protected boolean isNullAllowedForInitializer() {
    return false;
  }

  protected boolean getIsFinalInitialValue() {
    return this.details.inFinalInitialValue;
  }

  //perhaps for InitializerManagedFieldDeclaration
  protected AbstractType<?, ?, ?> getValueComponentTypeInitialValue() {
    return this.details.valueComponentTypeInitialValue;
  }

  protected String getNameInitialValue() {
    return this.details.nameInitialValue;
  }

  protected boolean getValueIsArrayTypeInitialValue() {
    return this.details.valueIsArrayTypeInitialValue;
  }

  protected Expression getInitializerInitialValue() {
    return this.details.initializerInitialValue;
  }

  @Override
  protected void handlePreShowDialog(Dialog dialog) {
    if (this.isFinalState != null) {
      boolean isFinal = this.getIsFinalInitialValue();
      this.isFinalState.setValueTransactionlessly(isFinal);
      if (details.isFinalStatus == ApplicabilityStatus.DISPLAYED) {
        Operation trueOperation = this.isFinalState.getSetToTrueOperation();
        Operation falseOperation = this.isFinalState.getSetToFalseOperation();
        trueOperation.setEnabled(isFinal);
        falseOperation.setEnabled(isFinal == false);
      }
    }
    if (this.initializerState != null) {
      //todo
      this.initializerState.setValueTransactionlessly(this.getInitializerInitialValue());
    }

    if (this.valueComponentTypeState != null) {
      this.valueComponentTypeState.setValueTransactionlessly(this.getValueComponentTypeInitialValue());
    }
    if (this.valueIsArrayTypeState != null) {
      this.valueIsArrayTypeState.setValueTransactionlessly(this.getValueIsArrayTypeInitialValue());
    }
    if (this.nameState != null) {
      this.nameState.setValueTransactionlessly(this.getNameInitialValue());
    }

    this.lifecycleDelegate.wireListeners();
    this.lifecycleDelegate.clearTypeToInitializerCache();

    this.getView().handleInitializerChanged(this.getInitializer());
    super.handlePreShowDialog(dialog);
  }

  @Override
  protected void handlePostHideDialog() {
    super.handlePostHideDialog();
    this.lifecycleDelegate.unwireListeners();
  }
}
