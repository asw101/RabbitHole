package org.alice.netbeans.project;

import org.junit.Assert;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public class GenerationValidatorBehaviorTest {
  @Test
  public void getJavaSourceFileForTypeUsesSourceRootAndJavaName() throws Exception {
    Path sourceRoot = newWorkDir("java-file-success");
    NamedUserType type = namedType("Scene");

    java.io.File javaFile = GenerationValidator.getJavaSourceFileForType(sourceRoot.toFile(), sourceRoot, type);

    Assert.assertEquals(sourceRoot.resolve("Scene.java").toFile().getCanonicalFile(), javaFile);
  }

  @Test
  public void getJavaSourceFileForTypeRejectsUnsafeTypeNames() throws Exception {
    Path sourceRoot = newWorkDir("java-file-invalid-name");
    NamedUserType type = namedType("bad-name");

    IOException error = org.junit.Assert.assertThrows(
        IOException.class,
        () -> GenerationValidator.getJavaSourceFileForType(sourceRoot.toFile(), sourceRoot, type));

    Assert.assertTrue(error.getMessage().contains("Unsafe Alice type name"));
    Assert.assertTrue(error.getMessage().contains("bad-name"));
  }

  @Test
  public void ensureDestinationFilesRejectsDuplicateGeneratedSourceName() throws Exception {
    Path sourceRoot = newWorkDir("duplicate-generated-source");
    NamedUserType duplicateLauncher = namedType("AliceJavaFXLauncher");

    IOException error = org.junit.Assert.assertThrows(
        IOException.class,
        () -> GenerationValidator.ensureDestinationFilesAreAvailable(
            sourceRoot.toFile(),
            sourceRoot,
            Set.of(duplicateLauncher),
            Collections.emptySet(),
            null));

    Assert.assertTrue(error.getMessage().contains("Duplicate generated Java source file"));
    Assert.assertTrue(error.getMessage().contains("AliceJavaFXLauncher.java"));
  }

  @Test
  public void ensureDestinationFilesRejectsExistingDestinationAndInvalidDeclarationNames() throws Exception {
    Path sourceRoot = newWorkDir("existing-destination");
    NamedUserType existingType = namedType("Scene");
    Files.writeString(sourceRoot.resolve("Scene.java"), "existing", StandardCharsets.UTF_8);

    IOException existingError = org.junit.Assert.assertThrows(
        IOException.class,
        () -> GenerationValidator.ensureDestinationFilesAreAvailable(
            sourceRoot.toFile(),
            sourceRoot,
            Set.of(existingType),
            Collections.emptySet(),
            null));
    Assert.assertTrue(existingError.getMessage().contains("Generated destination already exists"));

    Path invalidRoot = newWorkDir("invalid-declaration-name");
    NamedUserType invalidType = namedType("ValidType");
    invalidType.fields.add(new UserField("bad-field-name", String.class));

    IOException invalidError = org.junit.Assert.assertThrows(
        IOException.class,
        () -> GenerationValidator.ensureDestinationFilesAreAvailable(
            invalidRoot.toFile(),
            invalidRoot,
            Set.of(invalidType),
            Collections.emptySet(),
            null));
    Assert.assertTrue(invalidError.getMessage().contains("Unsafe Alice declaration name"));
    Assert.assertTrue(invalidError.getMessage().contains("bad-field-name"));
  }

  @Test
  public void ensureDestinationFilesAcceptsSafeTypesAndMethods() throws Exception {
    Path sourceRoot = newWorkDir("valid-generation");
    NamedUserType type = namedType("ProgramScene");
    type.methods.add(new UserMethod("sayHello", Void.TYPE, new UserParameter[0], new BlockStatement()));

    GenerationValidator.ensureDestinationFilesAreAvailable(
        sourceRoot.toFile(),
        sourceRoot,
        Set.of(type),
        Collections.emptySet(),
        null);

    Assert.assertFalse(Files.exists(sourceRoot.resolve("ProgramScene.java")));
  }

  private static NamedUserType namedType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    return type;
  }

  private static Path newWorkDir(String name) throws IOException {
    Path dir = Path.of("target", "test-work", GenerationValidatorBehaviorTest.class.getSimpleName(), name).toAbsolutePath();
    deleteRecursively(dir);
    Files.createDirectories(dir);
    return dir;
  }

  private static void deleteRecursively(Path path) throws IOException {
    if (Files.notExists(path)) {
      return;
    }
    try (java.util.stream.Stream<Path> stream = Files.walk(path)) {
      stream.sorted(java.util.Comparator.reverseOrder()).forEach(current -> {
        try {
          Files.deleteIfExists(current);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }
}
