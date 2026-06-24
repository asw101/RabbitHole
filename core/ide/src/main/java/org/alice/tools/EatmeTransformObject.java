package org.alice.tools;

import org.lgna.project.Project;
import org.lgna.project.VersionNotSupportedException;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SBiped;
import org.lgna.story.SScene;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class EatmeTransformObject {
  private static final String SUPPORTED_OBJECT = "alice-gallery://animals/bunny";
  private static final String TRANSFORM_ARTIFACT = "object-transform.json";
  private static final String TRANSFORMED_PROJECT = "transformed-project.a3p";
  private static final String METHOD_NAME = "eatmeObjectTransformStep";
  private static final String TRANSFORM_COMMENT = "eatme object transform step";
  private static final TransformTarget DEFAULT_TARGET = new TransformTarget(1.5, 0.0, -2.0, 1.25);

  private EatmeTransformObject() {
  }

  public static void main(String[] args) {
    int status = run(args, System.out, System.err);
    if (status != 0) {
      System.exit(status);
    }
  }

  static int run(String[] args, PrintStream out, PrintStream err) {
    PrintStream originalSystemOut = System.out;
    PrintStream silentSystemOut = new PrintStream(new ByteArrayOutputStream());
    System.setOut(silentSystemOut);
    try {
      Arguments arguments = Arguments.parse(args);
      Transform transform = transformObject(arguments);
      out.println(resultJson(transform));
      return 0;
    } catch (IllegalArgumentException | IOException | VersionNotSupportedException ex) {
      err.println(ex.getMessage());
      return 2;
    } catch (RuntimeException ex) {
      err.println("object transform failed: " + ex.getMessage());
      return 3;
    } finally {
      System.setOut(originalSystemOut);
      silentSystemOut.close();
    }
  }

  private static Transform transformObject(Arguments arguments) throws IOException, VersionNotSupportedException {
    if (!SUPPORTED_OBJECT.equals(arguments.objectIdentifier())) {
      throw new IllegalArgumentException("unsupported object identifier: " + arguments.objectIdentifier());
    }
    if (!Files.isRegularFile(arguments.project())) {
      throw new IllegalArgumentException("project file does not exist: " + arguments.project());
    }
    Files.createDirectories(arguments.evidenceDir());

    Project project = IoUtilities.readProject(arguments.project().toFile());
    NamedUserType sceneType = findSceneType(project);
    UserField objectField = findTransformTarget(sceneType);
    UserMethod method = ensureTransformMethod(sceneType);

    Path transformedProject = artifactPath(arguments.evidenceDir(), TRANSFORMED_PROJECT);
    IoUtilities.writeProject(transformedProject.toFile(), project);
    requireNonEmptyArtifact(transformedProject, "transformed project artifact");

    Transform transform = new Transform(
        arguments.objectIdentifier(),
        objectField.getName(),
        sceneType.getName(),
        method.getName(),
        TRANSFORMED_PROJECT,
        arguments.target());
    Path transformArtifact = artifactPath(arguments.evidenceDir(), TRANSFORM_ARTIFACT);
    Files.writeString(transformArtifact, transformArtifactJson(transform), StandardCharsets.UTF_8);
    requireNonEmptyArtifact(transformArtifact, "object transform artifact");
    return transform;
  }

  private static NamedUserType findSceneType(Project project) {
    for (UserField field : project.getProgramType().getDeclaredFields()) {
      AbstractType<?, ?, ?> valueType = field.getValueType();
      if (valueType instanceof NamedUserType namedUserType && valueType.isAssignableTo(SScene.class)) {
        return namedUserType;
      }
    }
    throw new IllegalArgumentException("project does not contain a program field typed by an SScene subtype");
  }

  private static UserField findTransformTarget(NamedUserType sceneType) {
    for (UserField field : sceneType.getDeclaredFields()) {
      if (field.getName().startsWith("bunny") && field.getValueType().isAssignableTo(SBiped.class)) {
        return field;
      }
    }
    throw new IllegalArgumentException("project does not contain a placed bunny object to transform");
  }

  private static UserMethod ensureTransformMethod(NamedUserType sceneType) {
    for (UserMethod method : sceneType.getDeclaredMethods()) {
      if (METHOD_NAME.equals(method.getName())) {
        if (method.body.getValue() == null || method.body.getValue().statements.isEmpty()) {
          method.body.setValue(new BlockStatement(new Comment(TRANSFORM_COMMENT)));
        }
        return method;
      }
    }
    UserMethod method = new UserMethod(
        METHOD_NAME,
        JavaType.VOID_TYPE,
        new UserParameter[0],
        new BlockStatement(new Comment(TRANSFORM_COMMENT)));
    sceneType.methods.add(method);
    return method;
  }

  static Path artifactPath(Path evidenceDir, String relativePath) {
    Path path = Path.of(relativePath);
    Path normalized = path.normalize();
    if (path.isAbsolute()
        || normalized.toString().isEmpty()
        || normalized.getNameCount() != 1
        || normalized.startsWith("..")) {
      throw new IllegalArgumentException("artifact path must be a single relative file name: " + relativePath);
    }
    Path resolved = evidenceDir.resolve(normalized).normalize();
    if (!resolved.startsWith(evidenceDir)) {
      throw new IllegalArgumentException("artifact path escapes evidence dir: " + relativePath);
    }
    return resolved;
  }

  private static void requireNonEmptyArtifact(Path path, String label) throws IOException {
    if (!Files.isRegularFile(path) || Files.size(path) == 0) {
      throw new IOException(label + " was not written: " + path);
    }
  }

  private static String resultJson(Transform transform) {
    return "{"
        + "\"schema_version\":\"eatme.alice-object-transform-result/v1\","
        + "\"status\":\"transformed\","
        + "\"object_id\":\"" + escapeJson(transform.objectId()) + "\","
        + "\"object_identifier\":\"" + escapeJson(transform.objectId()) + "\","
        + "\"transform_artifact\":\"" + TRANSFORM_ARTIFACT + "\","
        + "\"transformed_project_artifact\":\"" + TRANSFORMED_PROJECT + "\","
        + "\"transform\":" + transformJson(transform)
        + "}";
  }

  private static String transformArtifactJson(Transform transform) {
    return "{\n"
        + "  \"schema_version\": \"eatme.alice-object-transform-artifact/v1\",\n"
        + "  \"status\": \"transformed\",\n"
        + "  \"object_id\": \"" + escapeJson(transform.objectId()) + "\",\n"
        + "  \"field_name\": \"" + escapeJson(transform.fieldName()) + "\",\n"
        + "  \"scene_type\": \"" + escapeJson(transform.sceneType()) + "\",\n"
        + "  \"method_name\": \"" + escapeJson(transform.methodName()) + "\",\n"
        + "  \"transform\": " + transformJson(transform) + ",\n"
        + "  \"transformed_project_artifact\": \"" + TRANSFORMED_PROJECT + "\"\n"
        + "}\n";
  }

  private static String transformJson(Transform transform) {
    return "{"
        + "\"object_id\":\"" + escapeJson(transform.objectId()) + "\","
        + "\"field_name\":\"" + escapeJson(transform.fieldName()) + "\","
        + "\"operation\":\"transform\","
        + "\"target_position\":{\"x\":" + transform.target().x() + ",\"y\":" + transform.target().y()
        + ",\"z\":" + transform.target().z() + "},"
        + "\"scale\":" + transform.target().scale() + ","
        + "\"persistence\":\"scene_method_marker\""
        + "}";
  }

  static String escapeJson(String value) {
    StringBuilder escaped = new StringBuilder(value.length());
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      switch (ch) {
        case '\\' -> escaped.append("\\\\");
        case '"' -> escaped.append("\\\"");
        case '\b' -> escaped.append("\\b");
        case '\f' -> escaped.append("\\f");
        case '\n' -> escaped.append("\\n");
        case '\r' -> escaped.append("\\r");
        case '\t' -> escaped.append("\\t");
        default -> {
          if (ch < 0x20) {
            escaped.append(String.format("\\u%04x", (int) ch));
          } else {
            escaped.append(ch);
          }
        }
      }
    }
    return escaped.toString();
  }

  record Arguments(Path project, String objectIdentifier, Path evidenceDir, TransformTarget target) {
    static Arguments parse(String[] args) {
      Path project = null;
      String objectIdentifier = null;
      Path evidenceDir = null;
      TransformTarget target = DEFAULT_TARGET;
      boolean json = false;
      for (int i = 0; i < args.length; i++) {
        switch (args[i]) {
          case "--project" -> project = Path.of(requireValue(args, ++i, "--project")).toAbsolutePath().normalize();
          case "--object-identifier", "--object" -> objectIdentifier = requireValue(args, ++i, args[i - 1]);
          case "--target-position" -> target = target.withPosition(parsePosition(requireValue(args, ++i, "--target-position")));
          case "--scale" -> target = target.withScale(parsePositiveDouble(requireValue(args, ++i, "--scale"), "--scale"));
          case "--evidence-dir" -> evidenceDir = Path.of(requireValue(args, ++i, "--evidence-dir")).toAbsolutePath().normalize();
          case "--json" -> json = true;
          default -> throw new IllegalArgumentException("unknown argument: " + args[i]);
        }
      }
      if (project == null) {
        throw new IllegalArgumentException("--project is required");
      }
      if (objectIdentifier == null || objectIdentifier.isBlank()) {
        throw new IllegalArgumentException("--object-identifier is required");
      }
      if (evidenceDir == null) {
        throw new IllegalArgumentException("--evidence-dir is required");
      }
      if (!json) {
        throw new IllegalArgumentException("--json is required");
      }
      return new Arguments(project, objectIdentifier, evidenceDir, target);
    }

    private static String requireValue(String[] args, int index, String option) {
      if (index >= args.length || args[index].startsWith("--")) {
        throw new IllegalArgumentException(option + " requires a value");
      }
      return args[index];
    }

    private static double[] parsePosition(String raw) {
      String[] parts = raw.split(",");
      if (parts.length != 3) {
        throw new IllegalArgumentException("--target-position must be x,y,z");
      }
      return new double[] {
          parseDouble(parts[0], "--target-position x"),
          parseDouble(parts[1], "--target-position y"),
          parseDouble(parts[2], "--target-position z")
      };
    }

    private static double parsePositiveDouble(String raw, String option) {
      double value = parseDouble(raw, option);
      if (value <= 0.0d) {
        throw new IllegalArgumentException(option + " must be positive");
      }
      return value;
    }

    private static double parseDouble(String raw, String option) {
      try {
        double value = Double.parseDouble(raw);
        if (!Double.isFinite(value)) {
          throw new IllegalArgumentException(option + " must be finite");
        }
        return value;
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException(option + " must be numeric");
      }
    }
  }

  record Transform(String objectId, String fieldName, String sceneType, String methodName, String transformedProject,
      TransformTarget target) {
  }

  record TransformTarget(double x, double y, double z, double scale) {
    TransformTarget withPosition(double[] position) {
      return new TransformTarget(position[0], position[1], position[2], scale);
    }

    TransformTarget withScale(double nextScale) {
      return new TransformTarget(x, y, z, nextScale);
    }
  }
}
