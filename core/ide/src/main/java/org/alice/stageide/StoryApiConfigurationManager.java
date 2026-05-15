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

import edu.cmu.cs.dennisc.java.util.Lists;
import org.alice.ide.ApiConfigurationManager;
import org.alice.ide.ast.CurrentThisExpression;
import org.alice.ide.ast.ExpressionCreator;
import org.alice.ide.ast.components.DeclarationNameLabel;
import org.alice.ide.common.BeveledShapeForType;
import org.alice.ide.croquet.models.ui.formatter.FormatterState;
import org.alice.ide.croquet.models.ui.preferences.IsIncludingImportAndExportType;
import org.alice.ide.croquet.models.ui.preferences.IsIncludingProgramType;
import org.alice.ide.croquet.models.ui.preferences.IsIncludingThisForFieldAccessesState;
import org.alice.ide.icons.*;
import org.alice.ide.instancefactory.InstanceFactory;
import org.alice.ide.instancefactory.ThisFieldAccessMethodInvocationFactory;
import org.alice.ide.instancefactory.croquet.InstanceFactoryFillIn;
import org.alice.ide.member.FilteredMethodsSubComposite;
import org.alice.nonfree.NebulousIde;
import org.alice.stageide.ast.BootstrapUtilities;
import org.alice.stageide.ast.JointedTypeInfo;
import org.alice.stageide.instancefactory.croquet.joint.all.*;
import org.alice.stageide.member.*;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.CascadeMenuModel;
import org.lgna.croquet.icon.SVGIconFactory;
import org.lgna.croquet.imp.cascade.BlankNode;
import org.lgna.croquet.views.SwingComponentView;
import org.lgna.project.ast.*;
import org.lgna.story.*;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resourceutilities.StorytellingResourcesTreeUtils;

import java.util.*;

/**
 * @author Dennis Cosgrove
 */
public class StoryApiConfigurationManager extends ApiConfigurationManager {
  public static final JavaMethod SET_ACTIVE_SCENE_METHOD = JavaMethod.getInstance(SProgram.class, "setActiveScene", SScene.class);
  private static final JavaType CAMERA_TYPE = JavaType.getInstance(SCamera.class);
  private static final JavaType VR_USER_TYPE = JavaType.getInstance(SVRUser.class);
  private CascadeMenuModel<InstanceFactory> cameraFieldsMenuModel;
  private CascadeMenuModel<InstanceFactory> vrUserFieldsMenuModel;

  private static class SingletonHolder {
    private static StoryApiConfigurationManager instance = NebulousIde.nonfree.newStoryApiConfigurationManager();
  }

  public static StoryApiConfigurationManager getInstance() {
    return SingletonHolder.instance;
  }

  private final org.alice.stageide.ast.ExpressionCreator expressionCreator = NebulousIde.nonfree.newExpressionCreator();
  private final List<FilteredMethodsSubComposite> categoryProcedureSubComposites;
  private final List<FilteredMethodsSubComposite> categoryFunctionSubComposites;
  private final List<FilteredMethodsSubComposite> categoryOrAlphabeticalProcedureSubComposites;
  private final List<FilteredMethodsSubComposite> categoryOrAlphabeticalFunctionSubComposites;

  private static List<FilteredMethodsSubComposite> createUnmodifiableSubCompositeList(FilteredMethodsSubComposite... subComposites) {
    return Collections.unmodifiableList(Lists.newLinkedList(subComposites));
  }

  public StoryApiConfigurationManager() {
    BeveledShapeForType.addRoundType(SThing.class);
    IconFactoryManager.registerIconFactory(SSphere.class, SceneIconFactory.getInstance());
    IconFactoryManager.registerIconFactory(SCylinder.class, new ShapeIconFactory(CylinderIcon::new));
    IconFactoryManager.registerIconFactory(SCone.class, new ShapeIconFactory(ConeIcon::new));
    IconFactoryManager.registerIconFactory(SDisc.class, new ShapeIconFactory(DiscIcon::new));
    IconFactoryManager.registerIconFactory(SSphere.class, new ShapeIconFactory(SphereIcon::new));
    IconFactoryManager.registerIconFactory(STorus.class, new ShapeIconFactory(TorusIcon::new));
    IconFactoryManager.registerIconFactory(SAxes.class, new ShapeIconFactory(AxesIcon::new));
    IconFactoryManager.registerIconFactory(STextModel.class, new ShapeIconFactory(TextModelIcon::new));
    IconFactoryManager.registerIconFactory(SBillboard.class, new ShapeIconFactory(BillboardIcon::new));
    IconFactoryManager.registerIconFactory(SBox.class, new ShapeIconFactory(BoxIcon::new));
    IconFactoryManager.registerIconFactory(SGround.class, new ShapeIconFactory(GroundIcon::new));

    IconFactoryManager.registerIconFactory(SJoint.class, new ShapeIconFactory(JointIcon::new));
    IconFactoryManager.registerIconFactory(SCamera.class, new SVGIconFactory(Icons.class.getResource("images/Camera.svg")));
    IconFactoryManager.registerIconFactory(SVRHand.class, new SVGIconFactory(Icons.class.getResource("images/LeftHand.svg")));
    IconFactoryManager.registerIconFactory(SVRHeadset.class, new SVGIconFactory(Icons.class.getResource("images/VRHeadset.svg")));
    IconFactoryManager.registerIconFactory(SVRUser.class, new SVGIconFactory(Icons.class.getResource("images/VRUser.svg")));

    this.categoryProcedureSubComposites = createUnmodifiableSubCompositeList(TextProceduresComposite.getInstance(), AtmosphereProceduresComposite.getInstance(), SayThinkProceduresComposite.getInstance(), PositionProceduresComposite.getInstance(), OrientationProceduresComposite.getInstance(), PositionAndOrientationProceduresComposite.getInstance(), SizeProceduresComposite.getInstance(), AppearanceProceduresComposite.getInstance(), FieldOfViewProceduresComposite.getInstance(), VehicleProceduresComposite.getInstance(), AudioProceduresComposite.getInstance(), TimingProceduresComposite.getInstance());

    this.categoryFunctionSubComposites = createUnmodifiableSubCompositeList(AtmosphereFunctionsComposite.getInstance(), AppearanceFunctionsComposite.getInstance(), SizeFunctionsComposite.getInstance(), PromptUserFunctionsComposite.getInstance(), FieldOfViewFunctionsComposite.getInstance());

    this.categoryOrAlphabeticalProcedureSubComposites = createUnmodifiableSubCompositeList(AddListenerProceduresComposite.getInstance());

    this.categoryOrAlphabeticalFunctionSubComposites = createUnmodifiableSubCompositeList(JointFunctionsComposite.getInstance());
  }

  @Override
  public Comparator<AbstractType<?, ?, ?>> getTypeComparator() {
    return StoryTypeComparator.SINGLETON;
  }

  @Override
  protected boolean isNamedUserTypesAcceptableForSelection(NamedUserType type) {
    return (!type.isAssignableTo(SProgram.class)) || IsIncludingProgramType.getInstance().getValue();
  }

  @Override
  public List<FilteredMethodsSubComposite> getCategoryProcedureSubComposites() {
    return this.categoryProcedureSubComposites;
  }

  @Override
  public List<FilteredMethodsSubComposite> getCategoryFunctionSubComposites() {
    return this.categoryFunctionSubComposites;
  }

  @Override
  public List<FilteredMethodsSubComposite> getCategoryOrAlphabeticalProcedureSubComposites() {
    return this.categoryOrAlphabeticalProcedureSubComposites;
  }

  @Override
  public List<FilteredMethodsSubComposite> getCategoryOrAlphabeticalFunctionSubComposites() {
    return this.categoryOrAlphabeticalFunctionSubComposites;
  }

  @Override
  public boolean isDeclaringTypeForManagedFields(UserType<?> type) {
    return type.isAssignableTo(SScene.class);
  }

  @Override
  public boolean isInstanceFactoryDesiredForType(AbstractType<?, ?, ?> type) {
    return type != null
        && (type.isAssignableTo(SThing.class) && !type.isAssignableTo(SMarker.class)
        || type.isAssignableTo(SProgram.class));
  }

  protected static final JavaType BIPED_RESOURCE_TYPE = JavaType.getInstance(BipedResource.class);

  @Override
  public JavaType getGalleryResourceParentFor(JavaType type) {
    return StorytellingResourcesTreeUtils.INSTANCE.getGalleryResourceParentFor(type);
  }

  @Override
  public List<AbstractDeclaration> getGalleryResourceChildrenFor(AbstractType<?, ?, ?> type) {
    return StorytellingResourcesTreeUtils.INSTANCE.getGalleryResourceChildrenFor(type);
  }

  @Override
  public CascadeMenuModel<InstanceFactory> getInstanceFactorySubMenuForThis(AbstractType<?, ?, ?> type) {
    if (JointedTypeInfo.isJointed(type)) {
      return ThisJointedTypeMenuModel.getInstance(type);
    } else {
      return null;
    }
  }

  @Override
  public CascadeMenuModel<InstanceFactory> getInstanceFactorySubMenuForThisFieldAccess(UserField field) {
    AbstractType<?, ?, ?> type = field.getValueType();
    if (JointedTypeInfo.isJointed(type)) {
      return ThisFieldAccessJointedTypeMenuModel.getInstance(field);
    }
    if (CAMERA_TYPE.isAssignableFrom(type)) {
      return getCameraFieldsMenu(field);
    }
    if (VR_USER_TYPE.isAssignableFrom(type)) {
      return getVrUserFieldsMenu(field);
    }
    return null;
  }

  private CascadeMenuModel<InstanceFactory> getCameraFieldsMenu(UserField cameraField) {
    if (cameraFieldsMenuModel == null) {
      cameraFieldsMenuModel = new CascadeMenuModel<>(UUID.fromString("2b2c901a-22f3-4080-8a28-381f3d3b26c8")) {
        @Override
        protected void updateBlankChildren(List<CascadeBlankChild> blankChildren, BlankNode<InstanceFactory> blankNode) {
          for (AbstractMethod method : SCamera.getHandMethods(cameraField.getValueType())) {
            blankChildren.add(InstanceFactoryFillIn.getInstance(ThisFieldAccessMethodInvocationFactory.getInstance(cameraField, method)));
          }
        }
      };
    }
    return cameraFieldsMenuModel;
  }

  private CascadeMenuModel<InstanceFactory> getVrUserFieldsMenu(UserField vrField) {
    if (vrUserFieldsMenuModel == null) {
      vrUserFieldsMenuModel = new CascadeMenuModel<>(UUID.fromString("2b2c901a-22f3-4050-8a28-331f32bb26a8")) {
        @Override
        protected void updateBlankChildren(List<CascadeBlankChild> blankChildren, BlankNode<InstanceFactory> blankNode) {
          for (AbstractMethod method : SVRUser.getDeviceMethods(vrField.getValueType())) {
            blankChildren.add(InstanceFactoryFillIn.getInstance(ThisFieldAccessMethodInvocationFactory.getInstance(vrField, method)));
          }
        }
      };
    }
    return vrUserFieldsMenuModel;
  }

  @Override
  public CascadeMenuModel<InstanceFactory> getInstanceFactorySubMenuForParameterAccess(UserParameter parameter) {
    AbstractType<?, ?, ?> type = parameter.getValueType();
    if (JointedTypeInfo.isJointed(type)) {
      return ParameterAccessJointedTypeMenuModel.getInstance(parameter);
    } else {
      return null;
    }
  }

  @Override
  public CascadeMenuModel<InstanceFactory> getInstanceFactorySubMenuForLocalAccess(UserLocal local) {
    AbstractType<?, ?, ?> type = local.getValueType();
    if (JointedTypeInfo.isJointed(type)) {
      return LocalAccessJointedTypeMenuModel.getInstance(local);
    } else {
      return null;
    }
  }

  @Override
  public CascadeMenuModel<InstanceFactory> getInstanceFactorySubMenuForParameterAccessMethodInvocation(UserParameter parameter, AbstractMethod method) {
    AbstractType<?, ?, ?> type = method.getReturnType();
    if (JointedTypeInfo.isJointed(type)) {
      return ParameterAccessMethodInvocationJointedTypeMenuModel.getInstance(parameter, method);
    } else {
      return null;
    }
  }

  private DeclarationNameLabel createDeclarationNameLabel(AbstractField field) {
    //todo: better name
    class ThisFieldAccessNameLabel extends DeclarationNameLabel {
      private ThisFieldAccessNameLabel(AbstractField field) {
        super(field);
      }

      @Override
      protected String getNameText() {
        if (IsIncludingThisForFieldAccessesState.getInstance().getValue()) {
          return FormatterState.getInstance().getValue().getTextForThis() + "." + super.getNameText();
        } else {
          return super.getNameText();
        }
      }
    }
    return new ThisFieldAccessNameLabel(field);
  }

  @Override
  public SwingComponentView<?> createReplacementForFieldAccessIfAppropriate(FieldAccess fieldAccess) {
    Expression fieldExpression = fieldAccess.expression.getValue();
    if ((fieldExpression instanceof ThisExpression) || (fieldExpression instanceof CurrentThisExpression)) {
      AbstractField field = fieldAccess.field.getValue();
      AbstractType<?, ?, ?> declaringType = field.getDeclaringType();
      if ((declaringType != null) && declaringType.isAssignableTo(SScene.class)) {
        if (field.getValueType().isAssignableTo(SThing.class)) {
          return this.createDeclarationNameLabel(field);
        }
      }
    }
    return null;
  }

  @Override
  public ExpressionCreator getExpressionCreator() {
    return this.expressionCreator;
  }

  @Override
  public boolean isSignatureLocked(Code code) {
    //todo: check to see if only referenced from Program and Program type is hidden
    return super.isSignatureLocked(code) || BootstrapUtilities.MY_FIRST_PROCEDURE_NAME.equalsIgnoreCase(code.getName());
  }

  @Override
  public boolean isTabClosable(AbstractCode code) {
    return !BootstrapUtilities.MY_FIRST_PROCEDURE_NAME.equalsIgnoreCase(code.getName());
  }

  @Override
  protected List<? super JavaType> addSecondaryJavaTypes(List<? super JavaType> rv) {
    super.addSecondaryJavaTypes(rv);

    rv.add(JavaType.getInstance(SJoint.class));
    // the nulls added to the list become line breaks in dialogs where you can choose a type
    rv.add(null);
    if (StageIDE.getActiveInstance().getSceneEditor().isVrActive()) {
      rv.add(JavaType.getInstance(SVRHand.class));
      rv.add(JavaType.getInstance(SVRHeadset.class));
      rv.add(null);
    }
    rv.add(JavaType.getInstance(Paint.class));
    rv.add(JavaType.getInstance(Color.class));
    rv.add(null);
    rv.add(JavaType.getInstance(MoveDirection.class));
    rv.add(JavaType.getInstance(TurnDirection.class));
    rv.add(JavaType.getInstance(RollDirection.class));
    rv.add(JavaType.getInstance(Key.class));
    rv.add(null);
    rv.add(JavaType.getInstance(AudioSource.class));
    return rv;
  }

  @Override
  public UserType<?> augmentTypeIfNecessary(UserType<?> rv) {
    return JointMethodAugmentor.augment(rv);
  }

  @Override
  public boolean isExportTypeDesiredFor(NamedUserType type) {
    if (IsIncludingImportAndExportType.getValue()) {
      return (!type.isAssignableTo(SScene.class)) && (!type.isAssignableTo(SProgram.class));
    } else {
      return false;
    }
  }

  private AbstractType<?, ?, ?> getSpecificPoseBuilderType(Expression expression) {
    if (expression instanceof MethodInvocation methodInvocation) {
      return getSpecificPoseBuilderType(methodInvocation.expression.getValue());
    } else if (expression instanceof InstanceCreation instanceCreation) {
      return instanceCreation.getType();
    } else {
      return null;
    }
  }

  private AbstractType<?, ?, ?> getBuildMethodPoseBuilderType(MethodInvocation methodInvocation, boolean isSpecificPoseBuilderTypeRequired) {
    AbstractMethod method = methodInvocation.method.getValue();
    if ("build".equals(method.getName())) {
      AbstractType<?, ?, ?> type = methodInvocation.expression.getExpressionType();
      if (type.isAssignableTo(PoseBuilder.class)) {
        if (isSpecificPoseBuilderTypeRequired) {
          return getSpecificPoseBuilderType(methodInvocation.expression.getValue());
        } else {
          return type;
        }
      }
    }
    return null;
  }

  public AbstractType<?, ?, ?> getBuildMethodPoseBuilderType(MethodInvocation methodInvocation) {
    return getBuildMethodPoseBuilderType(methodInvocation, true);
  }

  public boolean isBuildMethod(MethodInvocation methodInvocation) {
    return getBuildMethodPoseBuilderType(methodInvocation, false) != null;
  }
}
