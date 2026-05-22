package org.lgna.croquet.structural;

import org.junit.Test;

public class CroquetStructuralSweep18Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    org.lgna.croquet.ReflectionCoverageSupport.inspectClasses(
        "org.lgna.croquet.importer.Importer",
        "org.lgna.croquet.meta.MetaState",
        "org.lgna.croquet.meta.StateTrackingMetaState",
        "org.lgna.croquet.preferences.PreferenceBooleanState",
        "org.lgna.croquet.preferences.PreferenceManager",
        "org.lgna.croquet.preferences.PreferenceMutableDataSingleSelectListState",
        "org.lgna.croquet.preferences.PreferenceStringState",
        "org.lgna.croquet.preferences.PreferencesManager",
        "org.lgna.croquet.resolvers.RuntimeResolver",
        "org.lgna.croquet.simple.SimpleApplication",
        "org.lgna.croquet.tools.UUIDGenerator",
        "org.lgna.croquet.triggers.AbstractMouseEventTrigger",
        "org.lgna.croquet.triggers.ActionEventTrigger"
    );
  }
}
