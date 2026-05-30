package org.alice.ide.coverage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;

public class DeclarationsEditorHeadlessSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(120);

  @Test
  public void exerciseDeclarationsEditorClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.declarationseditor.ClassesSeparator",
        "org.alice.ide.declarationseditor.DeclarationCompositeHistory",
        "org.alice.ide.declarationseditor.DeclarationCompositeHistoryLogic",
        "org.alice.ide.declarationseditor.DeclarationTabState",
        "org.alice.ide.declarationseditor.DeclarationTabStateLogic",
        "org.alice.ide.declarationseditor.FieldsSeparator",
        "org.alice.ide.declarationseditor.FunctionsSeparator",
        "org.alice.ide.declarationseditor.ManagedFieldsSeparator",
        "org.alice.ide.declarationseditor.ProcedureTabSelection",
        "org.alice.ide.declarationseditor.ProceduresSeparator",
        "org.alice.ide.declarationseditor.TypeMenuLogic",
        "org.alice.ide.declarationseditor.UnmanagedFieldsSeparator"
    );
    assertTrue("Should load at least 3 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 3);
  }

  @Test
  public void exerciseDeclarationsEditorTypeSubpackage() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.declarationseditor.type.AbstractManagedFieldState",
        "org.alice.ide.declarationseditor.type.ConstructorMenuModel",
        "org.alice.ide.declarationseditor.type.ConstructorState",
        "org.alice.ide.declarationseditor.type.FieldMenuModel",
        "org.alice.ide.declarationseditor.type.FieldState",
        "org.alice.ide.declarationseditor.type.FilteredMemberState",
        "org.alice.ide.declarationseditor.type.FunctionState",
        "org.alice.ide.declarationseditor.type.ManagedCameraMarkerFieldState",
        "org.alice.ide.declarationseditor.type.ManagedFieldState",
        "org.alice.ide.declarationseditor.type.ManagedObjectMarkerFieldState",
        "org.alice.ide.declarationseditor.type.MemberMenuModel",
        "org.alice.ide.declarationseditor.type.MethodMenuModel",
        "org.alice.ide.declarationseditor.type.MethodState",
        "org.alice.ide.declarationseditor.type.ProcedureState",
        "org.alice.ide.declarationseditor.type.UnmanagedFieldState",
        "org.alice.ide.declarationseditor.type.data.AbstractManagedFieldData",
        "org.alice.ide.declarationseditor.type.data.ConstructorData",
        "org.alice.ide.declarationseditor.type.data.FieldData",
        "org.alice.ide.declarationseditor.type.data.FilteredMemberData",
        "org.alice.ide.declarationseditor.type.data.FunctionData",
        "org.alice.ide.declarationseditor.type.data.ManagedCameraMarkerFieldData",
        "org.alice.ide.declarationseditor.type.data.ManagedFieldData",
        "org.alice.ide.declarationseditor.type.data.ManagedObjectMarkerFieldData",
        "org.alice.ide.declarationseditor.type.data.MethodData",
        "org.alice.ide.declarationseditor.type.data.ProcedureData",
        "org.alice.ide.declarationseditor.type.data.UnmanagedFieldData"
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }
}
