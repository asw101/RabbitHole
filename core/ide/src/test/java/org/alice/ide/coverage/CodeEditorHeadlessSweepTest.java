package org.alice.ide.coverage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;

public class CodeEditorHeadlessSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(120);

  @Test
  public void exerciseCodeEditorClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.codeeditor.CodeEditor",
        "org.alice.ide.codeeditor.CodeEditorLogic",
        "org.alice.ide.codeeditor.InstanceLine",
        "org.alice.ide.codeeditor.StatementListBorder",
        "org.alice.ide.codeeditor.StatementListPropertyPaneInfo"
    );
    assertTrue("Should load at least 2 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 2);
  }
}
