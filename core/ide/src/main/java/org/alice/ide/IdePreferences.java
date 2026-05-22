package org.alice.ide;

import org.alice.ide.perspectives.ProjectPerspective;

import java.util.Locale;

final class IdePreferences {
  Locale getForcedLocale(String forcedLocaleString) {
    return forcedLocaleString != null ? Locale.of(forcedLocaleString) : null;
  }

  ProjectPerspective getDefaultPerspective(boolean isScenePerspectiveDesiredByDefault,
      ProjectPerspective setupScenePerspective, ProjectPerspective codePerspective) {
    return isScenePerspectiveDesiredByDefault ? setupScenePerspective : codePerspective;
  }
}
