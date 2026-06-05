package org.lgna.project.io.compat;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SProgram;

import java.util.List;

import static org.junit.Assert.*;

public class ReplayModelContractTest {
  @Test
  public void replaySourceInputNormalizesTextAndRejectsUnsafePaths() {
    ReplaySourceInput input = new ReplaySourceInput("src/Sample.twe", "class Sample {\r\n}\r\n");

    assertEquals("src/Sample.twe", input.path());
    assertEquals("class Sample {\n}\n", input.text());
    assertFalse(input.text().contains("\r"));

    assertThrows(IllegalArgumentException.class, () -> new ReplaySourceInput("../evil.twe", "class Evil {}\n"));
    assertThrows(IllegalArgumentException.class, () -> new ReplaySourceInput("/tmp/evil.twe", "class Evil {}\n"));
    assertThrows(IllegalArgumentException.class, () -> new ReplaySourceInput("C:/tmp/evil.twe", "class Evil {}\n"));
    assertThrows(IllegalArgumentException.class, () -> new ReplaySourceInput("src\\Evil.twe", "class Evil {}\n"));
  }

  @Test
  public void replayCaseIsImmutableAndRequiresSafeStableId() {
    ReplaySourceInput input = new ReplaySourceInput("src/ImmutableProgram.twe", "class ImmutableProgram {}\n");
    ReplayCase replayCase = new ReplayCase("immutable-case", project("ImmutableProgram"), List.of(input));

    assertEquals("immutable-case", replayCase.id());
    assertEquals("ImmutableProgram", replayCase.project().getProgramType().getName());
    assertEquals(List.of(input), replayCase.sourceInputs());
    assertThrows(UnsupportedOperationException.class,
        () -> replayCase.sourceInputs().add(new ReplaySourceInput("src/Other.twe", "class Other {}\n")));

    assertThrows(IllegalArgumentException.class, () -> new ReplayCase("../bad", project("Bad"), List.of()));
    assertThrows(IllegalArgumentException.class, () -> new ReplayCase("bad path", project("Bad"), List.of()));
    assertThrows(NullPointerException.class, () -> new ReplayCase("missing-project", null, List.of()));
  }

  @Test
  public void replaySummaryRequiresMatchingCaseHeaderAndProviderName() {
    String text = """
        case: summary-case
        summary-schema: rabbithole.dual-baseline-summary/v1

        [source-tree]
        """;

    ReplaySummary summary = new ReplaySummary("summary-case", "RabbitHole", text);

    assertEquals("summary-case", summary.caseId());
    assertEquals("RabbitHole", summary.providerName());
    assertEquals(text, summary.text());
    assertThrows(IllegalArgumentException.class, () -> new ReplaySummary("different-case", "RabbitHole", text));
    assertThrows(IllegalArgumentException.class, () -> new ReplaySummary("summary-case", "", text));
    assertThrows(IllegalArgumentException.class,
        () -> new ReplaySummary("summary-case", "RabbitHole", text.replace("summary-case", "other-case")));
  }

  @Test
  public void replaySummaryProviderIsAThrowingDeterministicSeam() throws Exception {
    ReplayCase replayCase = new ReplayCase("provider-case", project("ProviderProgram"), List.of());
    ReplaySummaryProvider provider = input -> new ReplaySummary(
        input.id(),
        "FakeProvider",
        """
            case: provider-case
            summary-schema: rabbithole.dual-baseline-summary/v1

            [source-tree]
            """);

    ReplaySummary first = provider.summarize(replayCase);
    ReplaySummary second = provider.summarize(replayCase);

    assertEquals("FakeProvider", first.providerName());
    assertEquals(first.text(), second.text());
  }

  private static Project project(String name) {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue(name);
    programType.superType.setValue(JavaType.getInstance(SProgram.class));
    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }
}
