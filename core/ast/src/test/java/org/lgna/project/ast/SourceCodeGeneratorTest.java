package org.lgna.project.ast;

import org.junit.Test;

import static org.junit.Assert.*;

public class SourceCodeGeneratorTest {

  @Test
  public void repairsCachedCountNameBeforeForEachHeaderAndBodyEmission() {
    ForEachInArrayLoop loop = forEachLoop("COUNT__");
    UserLocal copy = new UserLocal("copy", String.class, true);
    loop.body.getValue().statements.add(new LocalDeclarationStatement(copy, new LocalAccess(loop.item.getValue())));

    String source = generate(loop);

    assertFalse(source, source.contains("COUNT__"));
    assertTrue(source, source.contains("for(String itemA : new String[]{\"red\", \"blue\"})"));
    assertTrue(source, source.contains("final String copy=itemA;"));
  }

  @Test
  public void preservesExplicitForEachItemName() {
    ForEachInArrayLoop loop = forEachLoop("item");
    UserLocal copy = new UserLocal("copy", String.class, true);
    loop.body.getValue().statements.add(new LocalDeclarationStatement(copy, new LocalAccess(loop.item.getValue())));

    String source = generate(loop);

    assertTrue(source, source.contains("for(String item : new String[]{\"red\", \"blue\"})"));
    assertTrue(source, source.contains("final String copy=item;"));
  }

  private static ForEachInArrayLoop forEachLoop(String itemName) {
    return new ForEachInArrayLoop(
        new UserLocal(itemName, String.class, true),
        AstUtilities.createArrayInstanceCreation(
            String[].class,
            new StringLiteral("red"),
            new StringLiteral("blue")),
        new BlockStatement());
  }

  private static String generate(Statement statement) {
    JavaCodeGenerator generator = new JavaCodeGenerator.Builder().build();
    statement.process(generator);
    return generator.getText();
  }
}
