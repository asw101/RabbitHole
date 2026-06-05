package org.lgna.project.io.compat;

import org.lgna.common.Resource;
import org.lgna.common.resources.ImageResource;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.story.SProgram;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

final class ReplayCaseFactory {
  List<ReplayCase> createCases() {
    return List.of(
        emptyProject(),
        projectWithTextResource(),
        projectWithMultipleResources(),
        projectWithGeneratedSourceShape());
  }

  ReplayCase caseNamed(String id) {
    for (ReplayCase replayCase : createCases()) {
      if (replayCase.id().equals(id)) {
        return replayCase;
      }
    }
    throw new IllegalArgumentException("Replay case not found: " + id);
  }

  private static ReplayCase emptyProject() {
    return new ReplayCase(
        "empty-project",
        project("EmptyProject"),
        List.of());
  }

  private static ReplayCase projectWithTextResource() {
    ImageResource resource = imageResource(
        "generated-text-resource.png",
        "generated text resource\n".getBytes(StandardCharsets.UTF_8),
        1,
        1);
    return new ReplayCase(
        "project-with-text-resource",
        projectReferencingResources("ProjectWithTextResource", resource),
        List.of());
  }

  private static ReplayCase projectWithMultipleResources() {
    ImageResource first = imageResource(
        "generated-a.png",
        "generated resource a\n".getBytes(StandardCharsets.UTF_8),
        1,
        1);
    ImageResource second = imageResource(
        "generated-b.png",
        "generated resource b\n".getBytes(StandardCharsets.UTF_8),
        1,
        1);
    return new ReplayCase(
        "project-with-multiple-resources",
        projectReferencingResources("ProjectWithMultipleResources", first, second),
        List.of());
  }

  private static ReplayCase projectWithGeneratedSourceShape() {
    String projectName = "ProjectWithGeneratedSourceShape";
    return new ReplayCase(
        "project-with-generated-source-shape",
        project(projectName),
        List.of(
            new ReplaySourceInput(
                "src/" + projectName + ".twe",
                "class " + projectName + " extends SProgram {\n"
                    + "  method initialize() {\n"
                    + "  }\n"
                    + "}\n"),
            new ReplaySourceInput(
                "src/" + projectName + "Helper.java",
                "final class " + projectName + "Helper {\n"
                    + "  private " + projectName + "Helper() {\n"
                    + "  }\n"
                    + "}\n")));
  }

  private static Project project(String name) {
    return new Project(programType(name), Project.SceneCameraType.WindowCamera);
  }

  private static Project projectReferencingResources(String name, ImageResource... resources) {
    NamedUserType programType = programTypeReferencingResources(name, resources);
    Project project = new Project(programType, Project.SceneCameraType.WindowCamera);
    for (ImageResource resource : resources) {
      project.addResource(resource);
    }
    return project;
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    return type;
  }

  private static NamedUserType programTypeReferencingResources(String name, ImageResource... resources) {
    NamedUserType type = programType(name);
    BlockStatement body = new BlockStatement();
    for (int i = 0; i < resources.length; i++) {
      UserLocal resource = new UserLocal("resource" + i, ImageResource.class, true);
      body.statements.add(new LocalDeclarationStatement(
          resource,
          new ResourceExpression(ImageResource.class, resources[i])));
    }
    type.methods.add(new UserMethod(
        "rememberGeneratedResources",
        Void.TYPE,
        new org.lgna.project.ast.UserParameter[0],
        body));
    return type;
  }

  private static ImageResource imageResource(String fileName, byte[] data, int width, int height) {
    ImageResource resource = new ImageResource(UUID.nameUUIDFromBytes(
        ("rabbithole-dual-baseline:" + fileName).getBytes(StandardCharsets.UTF_8)));
    resource.setOriginalFileName(fileName);
    resource.setName(fileName);
    resource.setContent("png", data);
    resource.setWidth(width);
    resource.setHeight(height);
    return resource;
  }
}
