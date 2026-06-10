package org.alice.netbeans.project;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ProjectCodeGeneratorGeneratedSourceOwnershipTest {

  @Test
  public void netBeansGeneratedSourceTestsDoNotOwnPureJavaSourceShapeAssertions() throws Exception {
    Map<String, List<String>> forbiddenAssertionFragments = Map.of(
        "ProjectCodeGeneratorGeneratedSourceTest.java",
        Arrays.asList(
            "contains(\"void sayHello()\")",
            "contains(\"final String greeting=\\\"hello alice\\\";\")",
            "contains(\"void remember(String message)\")",
            "contains(\"final String copy=message;\")",
            "contains(\"void callSayHello()\")",
            "contains(\"this.sayHello();\")",
            "contains(\"void callRemember()\")",
            "contains(\"this.remember(\\\"hello alice\\\");\")",
            "contains(\"void choose()\")",
            "contains(\"if(true)\")",
            "contains(\" else\")",
            "contains(\"void repeat()\")",
            "contains(\"for(Integer indexA=0;indexA<3;indexA++)\")",
            "contains(\"void spin()\")",
            "contains(\"while (true)\")",
            "contains(\"COUNT__\")",
            "contains(\"for(String itemA : new String[]{\\\"red\\\", \\\"blue\\\"})\")",
            "contains(\"final String copy=itemA;\")",
            "contains(\"void copyNamedItem()\")",
            "contains(\"for(String item : new String[]{\\\"red\\\", \\\"blue\\\"})\")",
            "contains(\"final String copy=item;\")",
            "contains(\"void visitIterable()\")",
            "contains(\"for(String item : Arrays.asList(\\\"red\\\",\\\"blue\\\"))\")"),
        "ProjectCodeGeneratorStoryApiGeneratedSourceTest.java",
        Arrays.asList(
            "contains(\"void configureStory()\")",
            "contains(\"this.setSimulationSpeedFactor(1.5);\")",
            "contains(\"void clearScene()\")",
            "contains(\"this.setActiveScene(null);\")",
            "contains(\"this.setActiveScene(this.scene);\")",
            "contains(\"this.box.setPaint(Color.RED);\")",
            "contains(\"this.box.setOpacity(0.5);\")",
            "contains(\"this.box.say(\\\"hello box\\\");\")",
            "contains(\"this.setAtmosphereColor(Color.BLUE);\")",
            "contains(\"this.setFogDensity(0.25);\")",
            "contains(\"this.addTimeListener(null,1);\")",
            "contains(\"this.addSceneActivationListener(null);\")",
            "contains(\"public void handleActiveChanged(Boolean isActive,Integer activationCount)\")",
            "contains(\"this.addSceneActivationListener((SceneActivationEvent p0) ->\")",
            "contains(\"this.addTimeListener((TimeEvent p0) ->\")",
            "contains(\"ProjectCodeGeneratorStoryApiGeneratedSourceTest.recordSceneActivationRuntimeDispatch(p0);\")",
            "contains(\"ProjectCodeGeneratorStoryApiGeneratedSourceTest.recordTimeEventElapsed(p0.getTimeSinceLastFire());\")"));

    Path sourceRoot = Path.of("src/test/java/org/alice/netbeans/project");
    List<String> violations = new ArrayList<>();
    for (Map.Entry<String, List<String>> entry : forbiddenAssertionFragments.entrySet()) {
      Path sourceFile = sourceRoot.resolve(entry.getKey());
      List<String> lines = Files.readAllLines(sourceFile);
      for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
        String line = lines.get(lineNumber);
        for (String fragment : entry.getValue()) {
          if (line.contains(fragment)) {
            violations.add(entry.getKey() + ":" + (lineNumber + 1) + " still asserts " + fragment);
          }
        }
      }
    }

    assertTrue(
        "Pure generated Java source-shape assertions belong in core/ast or story-api-migration tests; "
            + "NetBeans tests should keep packaging, launcher, resources, folding, compile, and runtime handoff coverage only: "
            + violations,
        violations.isEmpty());
  }
}
