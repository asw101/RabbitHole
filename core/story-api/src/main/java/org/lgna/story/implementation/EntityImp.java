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

package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.animation.DurationBasedAnimation;
import edu.cmu.cs.dennisc.animation.Style;
import edu.cmu.cs.dennisc.animation.TraditionalStyle;
import edu.cmu.cs.dennisc.java.lang.ClassUtilities;
import edu.cmu.cs.dennisc.java.lang.reflect.ReflectionUtilities;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.media.MediaFactory;
import edu.cmu.cs.dennisc.media.Player;
import edu.cmu.cs.dennisc.media.animation.MediaPlayerAnimation;
import edu.cmu.cs.dennisc.property.PropertyUtilities;
import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.Element;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.bound.CumulativeBound;
import edu.cmu.cs.dennisc.scenegraph.qa.Mender;
import edu.cmu.cs.dennisc.scenegraph.qa.QualityAssuranceUtilities;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Vector4;
import org.lgna.common.LgnaIllegalArgumentException;
import org.lgna.story.AudioSource;
import org.lgna.story.SThing;
import org.lgna.story.implementation.eventhandling.PolygonPrismHull;
import org.lgna.story.implementation.eventhandling.VerticalPrismCollisionHull;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Dennis Cosgrove
 */
public abstract class EntityImp extends PropertyOwnerImp implements ReferenceFrame {
  protected static final Element.Key<EntityImp> ENTITY_IMP_KEY = Element.Key.createInstance("ENTITY_IMP_KEY");

  public static EntityImp getInstance(Element sgElement) {
    return sgElement != null ? sgElement.getBonusDataFor(ENTITY_IMP_KEY) : null;
  }

  protected void putInstance(Element sgElement) {
    sgElement.putBonusDataFor(ENTITY_IMP_KEY, this);
  }

  public static <T extends EntityImp> T getInstance(Element sgElement, Class<T> cls) {
    return ClassUtilities.getInstance(getInstance(sgElement), cls);
  }

  public static SThing getAbstractionFromSgElement(Element sgElement) {
    EntityImp imp = getInstance(sgElement);
    if (imp != null) {
      return imp.getAbstraction();
    } else {
      return null;
    }
  }

  public static <T extends SThing> T getAbstractionFromSgElement(Element sgElement, Class<T> cls) {
    return ClassUtilities.getInstance(getAbstractionFromSgElement(sgElement), cls);
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
    this.getSgComposite().setName(name + ".sgComposite");
  }

  public Property<?> getPropertyForAbstractionGetter(Method getterMthd) {
    String propertyName = PropertyUtilities.getPropertyNameForGetter(getterMthd);
    Field fld = ReflectionUtilities.getField(this.getClass(), propertyName);
    return (Property<?>) ReflectionUtilities.get(fld, this);
  }

  protected abstract void updateCumulativeBound(CumulativeBound rv, AffineMatrix4x4 trans);

  public AxisAlignedBox getAxisAlignedMinimumBoundingBox(ReferenceFrame asSeenBy) {
    AffineMatrix4x4 trans = this.getTransformation(asSeenBy);
    CumulativeBound cumulativeBound = new CumulativeBound();
    this.updateCumulativeBound(cumulativeBound, trans);
    return cumulativeBound.getBoundingBox();
  }

  public VerticalPrismCollisionHull getCollisionHull() {
    AxisAlignedBox myBox = getDynamicAxisAlignedMinimumBoundingBox();
    AxisAlignedBox sceneBox = getDynamicAxisAlignedMinimumBoundingBox(AsSeenBy.SCENE);
    return new PolygonPrismHull(sceneBox.getCenterOfBottomFace(), sceneBox.getHeight(), getAbsoluteTransformation(), myBox);
  }

  public AxisAlignedBox getAxisAlignedMinimumBoundingBox() {
    return getAxisAlignedMinimumBoundingBox(AsSeenBy.SELF);
  }

  //Returns a bounding box that reflects any changes to the given entity. Namely any changes to the skeleton of jointed models
  public AxisAlignedBox getDynamicAxisAlignedMinimumBoundingBox(ReferenceFrame asSeenBy) {
    return this.getAxisAlignedMinimumBoundingBox(asSeenBy);
  }

  public AxisAlignedBox getDynamicAxisAlignedMinimumBoundingBox() {
    return getDynamicAxisAlignedMinimumBoundingBox(AsSeenBy.SELF);
  }

  //  private edu.cmu.cs.dennisc.scenegraph.bound.CumulativeBound createCumulativeBound( ReferenceFrame asSeenBy, HowMuch howMuch, OriginInclusionPolicy originPolicy ) {
  //    java.util.List< Transformable > transformables = new java.util.LinkedList< Transformable >();
  //    updateHowMuch( transformables, howMuch.isThisACandidate(), howMuch.isChildACandidate(), howMuch.isGrandchildAndBeyondACandidate() );
  //    edu.cmu.cs.dennisc.scenegraph.bound.CumulativeBound rv = new edu.cmu.cs.dennisc.scenegraph.bound.CumulativeBound();
  //    ReferenceFrame actualAsSeenBy = asSeenBy.getActualReferenceFrame( this );
  //
  //    for( Transformable transformable : transformables ) {
  //      edu.cmu.cs.dennisc.math.AffineMatrix4x4 m = transformable.getTransformation( actualAsSeenBy );
  //      assert m.isNaN() == false;
  //      transformable.updateCumulativeBound( rv, m, originPolicy.isOriginIncluded() );
  //    }
  //    return rv;
  //  }
  //
  //  public edu.cmu.cs.dennisc.math.AxisAlignedBox getAxisAlignedMinimumBoundingBox( ReferenceFrame asSeenBy, HowMuch howMuch, OriginInclusionPolicy originPolicy ) {
  //    edu.cmu.cs.dennisc.scenegraph.bound.CumulativeBound cumulativeBound = createCumulativeBound( asSeenBy, howMuch, originPolicy );
  //    return cumulativeBound.getBoundingBox();
  //  }
  //  public edu.cmu.cs.dennisc.math.AxisAlignedBox getAxisAlignedMinimumBoundingBox( ReferenceFrame asSeenBy, HowMuch howMuch ) {
  //    return getAxisAlignedMinimumBoundingBox( asSeenBy, howMuch, DEFAULT_ORIGIN_INCLUSION_POLICY );
  //  }
  //  public edu.cmu.cs.dennisc.math.AxisAlignedBox getAxisAlignedMinimumBoundingBox( ReferenceFrame asSeenBy ) {
  //    return getAxisAlignedMinimumBoundingBox( asSeenBy, DEFAULT_HOW_MUCH );
  //  }
  //  public edu.cmu.cs.dennisc.math.AxisAlignedBox getAxisAlignedMinimumBoundingBox() {
  //    return getAxisAlignedMinimumBoundingBox( AsSeenBy.SELF );
  //  }

  public abstract SThing getAbstraction();

  public abstract Composite getSgComposite();

  @Override
  public edu.cmu.cs.dennisc.scenegraph.ReferenceFrame getSgReferenceFrame() {
    return this.getSgComposite();
  }

  @Override
  public EntityImp getActualEntityImplementation(EntityImp ths) {
    return this;
  }

  protected Composite getSgVehicle() {
    return this.getSgComposite().getParent();
  }

  protected void setSgVehicle(Composite sgVehicle) {
    this.getSgComposite().setParentWithoutMoving(sgVehicle);
  }

  //HACK
  private EntityImp getEntityImpForSgObject(Composite sgObject) {
    EntityImp rv = getInstance(sgObject);
    if (rv != null) {
      return rv;
    } else if (sgObject.getParent() != null) {
      return getEntityImpForSgObject(sgObject.getParent());
    }
    return null;
  }

  public final EntityImp getVehicle() {
    Composite sgVehicle = this.getSgVehicle();
    if (sgVehicle != null) {
      EntityImp rv = getInstance(sgVehicle);
      if (rv == null) {
        //this does happen when the vehicle is the ROOT bone of a biped
        //ROOT apparently has no scenegraph counterpart.
        //ROOT won't be the child of any joint. however it could be the child of something else in the scene.
        //Therefore I realize that this is not a good fix in a generic EntityImp class.
        //However I don't know what is. Therefore this is what I do for now.
        rv = getEntityImpForSgObject(sgVehicle);
        Logger.severe("No instance found for sgVehicle " + sgVehicle + ". Searched parent and got " + rv);
        if (rv == null) {
          Logger.severe(this, sgVehicle);
        }
      }
      return rv;
    } else {
      return null;
    }
  }

  protected void postCheckSetVehicle(EntityImp vehicle) {
    this.setSgVehicle(vehicle != null ? vehicle.getSgComposite() : null);
  }

  public final void setVehicle(EntityImp vehicle) {
    //    if( vehicle == null ) {
    //      throw new org.lgna.common.LgnaIllegalArgumentException( "vehicle argument is null", 0, null );
    //    }
    if (vehicle != null) {
      if (vehicle == this) {
        throw new LgnaIllegalArgumentException("vehicle argument \"" + vehicle.getAbstraction() + "\" is the same as the instance \"" + this.getAbstraction() + "\"", 0, vehicle.getAbstraction());
      }
      if (vehicle.isDescendantOf(this)) {
        throw new LgnaIllegalArgumentException("vehicle argument \"" + vehicle.getAbstraction() + "\" is descendant of the instance \"" + this.getAbstraction() + "\" which would cause a cycle", 0, vehicle.getAbstraction());
      }
    }

    this.postCheckSetVehicle(vehicle);
  }

  public boolean isDescendantOf(EntityImp candidateAncestor) {
    assert candidateAncestor != null : this;
    EntityImp vehicle = this.getVehicle();
    if (vehicle != null) {
      if (vehicle == candidateAncestor) {
        return true;
      } else {
        return vehicle.isDescendantOf(candidateAncestor);
      }
    } else {
      return false;
    }
  }

  public SceneImp getScene() {
    EntityImp vehicle = this.getVehicle();
    return vehicle != null ? vehicle.getScene() : null;
  }

  @Override
  public ProgramImp getProgram() {
    SceneImp scene = this.getScene();
    return scene != null ? scene.getProgram() : null;
  }

  protected OnscreenRenderTarget getOnscreenRenderTarget() {
    ProgramImp program = this.getProgram();
    return program != null ? program.getOnscreenRenderTarget() : null;
  }

  public AffineMatrix4x4 getAbsoluteTransformation() {
    return this.getSgComposite().getAbsoluteTransformation();
  }

  public AffineMatrix4x4 getTransformation(ReferenceFrame asSeenBy) {
    return this.getSgComposite().getTransformation(asSeenBy.getSgReferenceFrame());
  }

  public StandInImp createStandIn() {
    StandInImp rv = new StandInImp();
    rv.setVehicle(this);
    return rv;
  }

  public StandInImp createOffsetStandIn(double x, double y, double z) {
    StandInImp rv = this.createStandIn();
    AffineMatrix4x4 m = AffineMatrix4x4.createTranslation(x, y, z);
    rv.setLocalTransformation(m);
    return rv;
  }

  public Point transformToAwt(Vector4 xyzw, CameraImp<?> cameraImp) {
    final AbstractCamera camera = cameraImp.getSgCamera();
    // get the position relative to the camera, first.
    Vector4 pos = this.getSgComposite().transformTo(xyzw, camera);
    // 3d -> 2d conversion
    return this.getOnscreenRenderTarget().transformFromCameraToAWT(pos, camera);

  }

  protected static final double DEFAULT_DURATION = 1.0;
  protected static final Style DEFAULT_STYLE = TraditionalStyle.BEGIN_AND_END_GENTLY;

  //  public static final Style DEFAULT_SPEED_STYLE = org.alice.apis.moveandturn.TraditionalStyle.BEGIN_AND_END_ABRUPTLY;
  //  public static final HowMuch DEFAULT_HOW_MUCH = HowMuch.THIS_AND_DESCENDANT_PARTS;

  public void alreadyAdjustedDelay(double duration) {
    if (duration == RIGHT_NOW) {
      //pass;
    } else {
      perform(new DurationBasedAnimation(duration) {
        @Override
        protected void prologue() {
        }

        @Override
        protected void setPortion(double portion) {
        }

        @Override
        protected void epilogue() {
        }
      });
    }
  }

  public void delay(double duration) {
    this.alreadyAdjustedDelay(this.adjustDurationIfNecessary(duration));
  }

  public void playAudio(AudioSource audioSource) {
    MediaFactory mediaFactory = edu.cmu.cs.dennisc.media.javafx.MediaFactory.getSingleton();
    Player player = mediaFactory.createPlayer(audioSource.getAudioResource(), audioSource.getVolume(), audioSource.getStartTime(), audioSource.getStopTime());
    this.perform(new MediaPlayerAnimation(player));
  }

  private final UserDialogDelegate userDialogDelegate = new UserDialogDelegate(this);

  public double getDoubleFromUser(String message) {
    return this.userDialogDelegate.getDoubleFromUser(message);
  }

  public int getIntegerFromUser(String message) {
    return this.userDialogDelegate.getIntegerFromUser(message);
  }

  public boolean getBooleanFromUser(String message) {
    return this.userDialogDelegate.getBooleanFromUser(message);
  }

  public String getStringFromUser(String message) {
    return this.userDialogDelegate.getStringFromUser(message);
  }

  public boolean isCollidingWith(SThing other) {
    return getScene().getEventManager().getCollisionHandler().doTheseCollide(this.getAbstraction(), other);
  }

  public void mendSceneGraphIfNecessary() {
    QualityAssuranceUtilities.inspectAndMendIfNecessary(this.getSgComposite(), new Mender() {
      @Override
      public AffineMatrix4x4 getMendTransformationFor(Joint sgJoint) {
        EntityImp imp = EntityImp.getInstance(sgJoint);
        if (imp instanceof JointImp jointImp) {
          return jointImp.getScaledOriginalTransformation();
        } else {
          return AffineMatrix4x4.IDENTITY;
        }
      }
    });
  }

  protected void appendRepr(StringBuilder sb) {
  }

  @Override
  public final String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName());
    sb.append("[");
    this.appendRepr(sb);
    sb.append("]");
    return sb.toString();
  }

  private String name;
}
