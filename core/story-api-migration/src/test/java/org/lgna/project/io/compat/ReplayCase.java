package org.lgna.project.io.compat;

import org.lgna.project.Project;

import java.util.List;
import java.util.Objects;

record ReplayCase(String id, Project project, List<ReplaySourceInput> sourceInputs) {
  ReplayCase {
    id = ReplaySourceInput.requireSafeIdentifier(id, "replay case id");
    project = Objects.requireNonNull(project, "project");
    sourceInputs = List.copyOf(Objects.requireNonNull(sourceInputs, "sourceInputs"));
  }
}
