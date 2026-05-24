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

package org.alice.stageide.custom;

import edu.cmu.cs.dennisc.java.lang.DoubleUtilities;
import edu.cmu.cs.dennisc.media.javafx.MediaFactory;
import edu.cmu.cs.dennisc.media.javafx.Player;
import org.alice.ide.custom.CustomExpressionCreatorComposite;
import org.alice.stageide.custom.components.AudioSourceCustomExpressionCreatorView;
import org.lgna.common.resources.AudioResource;
import org.lgna.croquet.BoundedIntegerState;
import org.lgna.croquet.CancelException;
import org.lgna.croquet.Operation;
import org.lgna.croquet.PlainStringValue;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.story.AudioSource;

import java.util.UUID;

/**
 * @author Dennis Cosgrove
 */
public final class AudioSourceCustomExpressionCreatorComposite extends CustomExpressionCreatorComposite<AudioSourceCustomExpressionCreatorView> {
  private static class SingletonHolder {
    private static AudioSourceCustomExpressionCreatorComposite instance = new AudioSourceCustomExpressionCreatorComposite();
  }

  public static AudioSourceCustomExpressionCreatorComposite getInstance() {
    return SingletonHolder.instance;
  }

  private static final int MARKER_MAX = 1000;

  private final PlainStringValue resourceSidekickLabel = this.createStringValue("resourceState.sidekickLabel");
  private final BoundedIntegerState volumeState = this.createBoundedIntegerState("volumeState", VolumeLevelUtilities.createDetails());
  private final BoundedIntegerState startMarkerState = this.createBoundedIntegerState("startMarkerState", new BoundedIntegerDetails().minimum(0).maximum(MARKER_MAX).initialValue(0));
  private final BoundedIntegerState stopMarkerState = this.createBoundedIntegerState("stopMarkerState", new BoundedIntegerDetails().minimum(0).maximum(MARKER_MAX).initialValue(MARKER_MAX));

  private ValueListener<Integer> startValueListiner = new ValueListener<Integer>() {
    @Override
    public void valueChanged(ValueEvent<Integer> e) {
      updateStopValueIfNecessary();
    }
  };
  private ValueListener<Integer> stopValueListiner = new ValueListener<Integer>() {
    @Override
    public void valueChanged(ValueEvent<Integer> e) {
      updateStartValueIfNecessary();
    }
  };

  private double toDouble(int markerValue, double defaultValue) {
    AudioResource audioResource = this.getAudioResourceExpressionState().getAudioResource();
    double duration;
    if (audioResource != null) {
      duration = audioResource.getDuration();
    } else {
      duration = Double.NaN;
    }
    if (Double.isNaN(duration)) {
      return defaultValue;
    } else {
      double value = markerValue * 0.001 * duration;
      value = DoubleUtilities.round(value, 3);
      return value;
    }
  }

  private boolean isIgnoringValueChanges;

  private void updateStartValueIfNecessary() {
    if (!this.isIgnoringValueChanges) {
      int start = this.startMarkerState.getValue();
      int stop = this.stopMarkerState.getValue();
      if (start > stop) {
        this.isIgnoringValueChanges = true;
        try {
          this.startMarkerState.setValueTransactionlessly(stop);
        } finally {
          this.isIgnoringValueChanges = false;
        }
        this.getView().updatePreview();
      }
    }
  }

  private void updateStopValueIfNecessary() {
    if (!this.isIgnoringValueChanges) {
      int start = this.startMarkerState.getValue();
      int stop = this.stopMarkerState.getValue();
      if (start > stop) {
        this.isIgnoringValueChanges = true;
        try {
          this.stopMarkerState.setValueTransactionlessly(start);
        } finally {
          this.isIgnoringValueChanges = false;
        }
        this.getView().updatePreview();
      }
    }
  }

  private double getStartMarkerTime() {
    int value = this.startMarkerState.getValue();
    return toDouble(value, 0.0);
  }

  private double getStopMarkerTime() {
    int value = this.stopMarkerState.getValue();
    if (value == MARKER_MAX) {
      return Double.NaN;
    } else {
      return toDouble(value, Double.NaN);
    }
  }

  private final Operation testOperation = this.createActionOperation("test", new Action() {
    @Override
    public Edit perform(UserActivity userActivity, InternalActionOperation source) throws CancelException {
      AudioResource audioResource = getAudioResourceExpressionState().getAudioResource();
      double volume = VolumeLevelUtilities.toDouble(getVolumeState().getValue());
      double startTime = getStartMarkerTime();
      double stopTime = getStopMarkerTime();
      MediaFactory mediaFactory = MediaFactory.getSingleton();
      Player player = mediaFactory.createPlayer(audioResource, volume, startTime, stopTime);
      new Thread(player::playUntilStop).start();
      return null;
    }
  });

  private AudioSourceCustomExpressionCreatorComposite() {
    super(UUID.fromString("786280be-fdba-4135-bcc4-b0548ded2e50"));
    this.startMarkerState.addNewSchoolValueListener(this.startValueListiner);
    this.stopMarkerState.addNewSchoolValueListener(this.stopValueListiner);
  }

  public AudioResourceExpressionState getAudioResourceExpressionState() {
    return AudioResourceExpressionState.getInstance();
  }

  public BoundedIntegerState getVolumeState() {
    return this.volumeState;
  }

  public BoundedIntegerState getStartMarkerState() {
    return this.startMarkerState;
  }

  public BoundedIntegerState getStopMarkerState() {
    return this.stopMarkerState;
  }

  public Operation getTestOperation() {
    return this.testOperation;
  }

  public PlainStringValue getResourceSidekickLabel() {
    return this.resourceSidekickLabel;
  }

  @Override
  protected Expression createValue() {
    ResourceExpression resourceExpression = (ResourceExpression) this.getAudioResourceExpressionState().getValue();
    if (resourceExpression != null) {
      AudioResource audioResource = (AudioResource) resourceExpression.resource.getValue();
      Expression arg0Expression;
      if (audioResource != null) {
        arg0Expression = new ResourceExpression(AudioResource.class, audioResource);
      } else {
        arg0Expression = new NullLiteral();
      }

      double volume = VolumeLevelUtilities.toDouble(this.getVolumeState().getValue());
      double startTime = this.getStartMarkerTime();
      double stopTime = this.getStopMarkerTime();

      int optionalArgumentCount = AudioSourceCustomExpressionCreatorCompositeLogic.getOptionalArgumentCount(volume, startTime, stopTime);
      if (optionalArgumentCount == 3) {
        JavaConstructor constructor = JavaConstructor.getInstance(AudioSource.class, AudioResource.class, Number.class, Number.class, Number.class);
        return AstUtilities.createInstanceCreation(constructor, arg0Expression, new DoubleLiteral(volume), new DoubleLiteral(startTime), new DoubleLiteral(stopTime));
      } else if (optionalArgumentCount == 2) {
        JavaConstructor constructor = JavaConstructor.getInstance(AudioSource.class, AudioResource.class, Number.class, Number.class);
        return AstUtilities.createInstanceCreation(constructor, arg0Expression, new DoubleLiteral(volume), new DoubleLiteral(startTime));
      } else if (optionalArgumentCount == 1) {
        JavaConstructor constructor = JavaConstructor.getInstance(AudioSource.class, AudioResource.class, Number.class);
        return AstUtilities.createInstanceCreation(constructor, arg0Expression, new DoubleLiteral(volume));
      } else {
        JavaConstructor constructor = JavaConstructor.getInstance(AudioSource.class, AudioResource.class);
        return AstUtilities.createInstanceCreation(constructor, arg0Expression);
      }
    } else {
      return null;
    }
  }

  @Override
  protected void handlePostHideDialog() {
    super.handlePostHideDialog();

    if (isCommitted) {
      double volume = VolumeLevelUtilities.toDouble(this.getVolumeState().getValue());

      VolumeLevelCustomExpressionCreatorComposite.getInstance().updateRecentValues(volume);
    }
  }

  @Override
  protected Status getStatusPreRejectorCheck() {
    return null;
  }

  @Override
  protected void initializeToPreviousExpression(Expression expression) {
    AudioSourceCustomExpressionCreatorCompositeLogic.Selection selection = AudioSourceCustomExpressionCreatorCompositeLogic.decodeSelection(expression);
    this.getVolumeState().setValueTransactionlessly(VolumeLevelUtilities.toInt(selection.getVolumeLevel()));
    this.getAudioResourceExpressionState().setValueTransactionlessly(selection.getResourceExpression());
  }

  @Override
  protected AudioSourceCustomExpressionCreatorView createView() {
    return new AudioSourceCustomExpressionCreatorView(this);
  }
}
