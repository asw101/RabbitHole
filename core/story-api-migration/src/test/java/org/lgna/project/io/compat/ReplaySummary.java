package org.lgna.project.io.compat;

import java.util.Objects;

record ReplaySummary(String caseId, String providerName, String text) {
  ReplaySummary {
    caseId = ReplaySourceInput.requireSafeIdentifier(caseId, "summary case id");
    providerName = Objects.requireNonNull(providerName, "providerName").trim();
    if (providerName.isEmpty()) {
      throw new IllegalArgumentException("summary provider name must not be blank");
    }
    text = ReplaySourceInput.normalizeText(Objects.requireNonNull(text, "text"));
    String expectedHeader = "case: " + caseId + "\nsummary-schema: " + ReplaySummaryWriter.SCHEMA + "\n";
    if (!text.startsWith(expectedHeader)) {
      throw new IllegalArgumentException(
          "Replay summary for case " + caseId + " must start with:\n" + expectedHeader);
    }
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if ((ch != '\n') && (ch != '\t') && Character.isISOControl(ch)) {
        throw new IllegalArgumentException(
            "Replay summary for case " + caseId + " contains control character U+"
                + String.format("%04X", (int) ch));
      }
    }
  }
}
