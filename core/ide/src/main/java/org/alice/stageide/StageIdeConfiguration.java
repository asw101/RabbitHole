package org.alice.stageide;

import edu.cmu.cs.dennisc.java.lang.ClassUtilities;
import org.lgna.project.ast.Declaration;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SCamera;
import org.lgna.story.SScene;
import org.lgna.story.SVRHand;
import org.lgna.story.SVRHeadset;
import org.lgna.story.SVRUser;

import java.awt.Frame;

final class StageIdeConfiguration {
  int getAutomaticDisplayDelta(int oldState, int newState) {
    int delta = 0;
    if ((oldState & Frame.ICONIFIED) == Frame.ICONIFIED) {
      delta++;
    }
    if ((newState & Frame.ICONIFIED) == Frame.ICONIFIED) {
      delta--;
    }
    return delta;
  }

  boolean isDeclarationIncluded(Declaration declaration) {
    return StageIDE.PERFORM_GENERATED_SET_UP_METHOD_NAME.equals(declaration.getName()) == false;
  }

  boolean isInstanceCreationAllowed(Class<?> type) {
    return !ClassUtilities.isAssignableToAtLeastOne(type, SScene.class, SCamera.class, SVRUser.class, SVRHand.class, SVRHeadset.class);
  }

  boolean isSceneScoped(NamedUserType sceneType, NamedUserType selectedType) {
    return (sceneType != null) && (sceneType == selectedType);
  }
}
