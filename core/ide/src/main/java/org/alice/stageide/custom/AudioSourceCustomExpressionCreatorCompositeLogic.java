package org.alice.stageide.custom;

import org.lgna.common.Resource;
import org.lgna.common.resources.AudioResource;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.story.AudioSource;

final class AudioSourceCustomExpressionCreatorCompositeLogic {
  static final class Selection {
    private final ResourceExpression resourceExpression;
    private final AudioResource audioResource;
    private final double volumeLevel;
    private final double startTime;
    private final double stopTime;

    Selection(ResourceExpression resourceExpression, AudioResource audioResource, double volumeLevel, double startTime, double stopTime) {
      this.resourceExpression = resourceExpression;
      this.audioResource = audioResource;
      this.volumeLevel = volumeLevel;
      this.startTime = startTime;
      this.stopTime = stopTime;
    }

    ResourceExpression getResourceExpression() {
      return this.resourceExpression;
    }

    AudioResource getAudioResource() {
      return this.audioResource;
    }

    double getVolumeLevel() {
      return this.volumeLevel;
    }

    double getStartTime() {
      return this.startTime;
    }

    double getStopTime() {
      return this.stopTime;
    }
  }

  private AudioSourceCustomExpressionCreatorCompositeLogic() {
    throw new AssertionError();
  }

  static int getOptionalArgumentCount(double volume, double startTime, double stopTime) {
    boolean isNotDefaultVolume = !AudioSource.isWithinReasonableEpsilonOfDefaultVolume(volume);
    boolean isNotDefaultStartTime = !AudioSource.isWithinReasonableEpsilonOfDefaultStartTime(startTime);
    boolean isNotDefaultStopTime = !AudioSource.isDefaultStopTime_aka_NaN(stopTime);
    if (isNotDefaultStopTime) {
      return 3;
    }
    if (isNotDefaultStartTime) {
      return 2;
    }
    return isNotDefaultVolume ? 1 : 0;
  }

  static Selection decodeSelection(Expression expression) {
    ResourceExpression resourceExpression = null;
    AudioResource audioResource = null;
    double volumeLevel = 1.0;
    double startTime = 0.0;
    double stopTime = Double.NaN;

    if (expression instanceof InstanceCreation instanceCreation) {
      int n = instanceCreation.requiredArguments.size();
      if (n > 0) {
        Expression expression0 = getArgumentExpressionAt(instanceCreation, 0);
        if (expression0 instanceof ResourceExpression resExp) {
          resourceExpression = resExp;
          Resource resource = resourceExpression.resource.getValue();
          if (resource instanceof AudioResource audioRes) {
            audioResource = audioRes;
          }
          if (n > 1) {
            if (n > 2) {
              if (n > 3) {
                Expression expression3 = getArgumentExpressionAt(instanceCreation, 3);
                if (expression3 instanceof DoubleLiteral stopTimeLiteral) {
                  stopTime = stopTimeLiteral.value.getValue();
                }
              }
              Expression expression2 = getArgumentExpressionAt(instanceCreation, 2);
              if (expression2 instanceof DoubleLiteral startTimeLiteral) {
                startTime = startTimeLiteral.value.getValue();
              }
            }
            Expression expression1 = getArgumentExpressionAt(instanceCreation, 1);
            if (expression1 instanceof DoubleLiteral volumeLevelLiteral) {
              volumeLevel = volumeLevelLiteral.value.getValue();
            }
          }
        }
      }
    }
    return new Selection(resourceExpression, audioResource, volumeLevel, startTime, stopTime);
  }

  private static Expression getArgumentExpressionAt(InstanceCreation instanceCreation, int index) {
    SimpleArgument arg = instanceCreation.requiredArguments.get(index);
    return arg.expression.getValue();
  }
}
