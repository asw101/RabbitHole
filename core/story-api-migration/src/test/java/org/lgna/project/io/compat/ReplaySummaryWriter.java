package org.lgna.project.io.compat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

final class ReplaySummaryWriter {
  static final String SCHEMA = "rabbithole.dual-baseline-summary/v1";

  ReplaySummary write(ReplayCase replayCase, Input input) throws IOException {
    Objects.requireNonNull(replayCase, "replayCase");
    Objects.requireNonNull(input, "input");
    StringBuilder builder = new StringBuilder();
    builder.append("case: ").append(replayCase.id()).append('\n');
    builder.append("summary-schema: ").append(SCHEMA).append("\n\n");
    appendSourceTree(builder, replayCase.sourceInputs());
    appendSourceHashes(builder, replayCase.sourceInputs());
    appendArchiveSections(builder, input.archiveEntries);
    appendManifestSections(builder, input.manifestFields);
    appendResources(builder, input.resources);
    String text = builder.toString();
    validateSummaryHygiene(text);
    try {
      return new ReplaySummary(replayCase.id(), input.providerName, text);
    } catch (IllegalArgumentException e) {
      throw new IOException("Unable to write replay summary for case " + replayCase.id(), e);
    }
  }

  private static void appendSourceTree(StringBuilder builder, List<ReplaySourceInput> sourceInputs) {
    builder.append("[source-tree]\n");
    TreeSet<String> paths = new TreeSet<>();
    for (ReplaySourceInput sourceInput : sourceInputs) {
      paths.add(sourceInput.path());
    }
    for (String path : paths) {
      builder.append(path).append('\n');
    }
    builder.append('\n');
  }

  private static void appendSourceHashes(StringBuilder builder, List<ReplaySourceInput> sourceInputs) {
    builder.append("[source-hashes]\n");
    TreeMap<String, String> hashes = new TreeMap<>();
    for (ReplaySourceInput sourceInput : sourceInputs) {
      hashes.put(sourceInput.path(), sha256(sourceInput.text().getBytes(StandardCharsets.UTF_8)));
    }
    for (Map.Entry<String, String> entry : hashes.entrySet()) {
      builder.append(entry.getKey()).append(" sha256:").append(entry.getValue()).append('\n');
    }
    builder.append('\n');
  }

  private static void appendArchiveSections(
      StringBuilder builder,
      Map<String, List<String>> archiveEntries) throws IOException {
    for (String archiveType : sectionOrder(archiveEntries.keySet(), List.of("a3p", "a3w"))) {
      builder.append("[archives:").append(archiveType).append("]\n");
      TreeSet<String> entries = new TreeSet<>();
      for (String entry : archiveEntries.getOrDefault(archiveType, List.of())) {
        entries.add(requireSafeRelativePath(entry, "archive entry"));
      }
      for (String entry : entries) {
        builder.append(entry).append('\n');
      }
      builder.append('\n');
    }
  }

  private static void appendManifestSections(
      StringBuilder builder,
      Map<String, Map<String, String>> manifestFields) throws IOException {
    for (String archiveType : sectionOrder(manifestFields.keySet(), List.of("a3p", "a3w"))) {
      builder.append("[manifest:").append(archiveType).append("]\n");
      TreeMap<String, String> fields = new TreeMap<>(manifestFields.getOrDefault(archiveType, Map.of()));
      for (Map.Entry<String, String> entry : fields.entrySet()) {
        validateManifestField(entry.getKey(), entry.getValue());
        builder.append(entry.getKey()).append('=').append(normalizeScalar(entry.getValue())).append('\n');
      }
      builder.append('\n');
    }
  }

  private static void appendResources(StringBuilder builder, List<ResourceIdentity> resources) throws IOException {
    builder.append("[resources]\n");
    List<ResourceIdentity> sortedResources = new ArrayList<>(resources);
    sortedResources.sort((left, right) -> left.sortKey().compareTo(right.sortKey()));
    for (ResourceIdentity resource : sortedResources) {
      validateResource(resource);
      builder.append(resource.name())
          .append(" type=").append(resource.contentType())
          .append(" path=").append(resource.archivePath())
          .append(" bytes=").append(resource.data().length)
          .append(" sha256:").append(sha256(resource.data()))
          .append('\n');
    }
  }

  private static List<String> sectionOrder(Collection<String> keys, List<String> requiredFirst) {
    TreeSet<String> ordered = new TreeSet<>(keys);
    List<String> result = new ArrayList<>();
    for (String required : requiredFirst) {
      result.add(required);
      ordered.remove(required);
    }
    result.addAll(ordered);
    return result;
  }

  private static void validateManifestField(String key, String value) throws IOException {
    if ((key == null) || key.isBlank() || !key.matches("[A-Za-z0-9._-]+")) {
      throw new IOException("Unsafe manifest field key: " + key);
    }
    String normalizedValue = normalizeScalar(Objects.requireNonNull(value, "manifest value"));
    if ("environment".equalsIgnoreCase(key) || normalizedValue.contains("PATH=") || normalizedValue.contains("JAVA_HOME=")) {
      throw new IOException("Manifest field " + key + " looks like an environment dump");
    }
    validatePortableText("manifest field " + key, normalizedValue);
  }

  private static void validateResource(ResourceIdentity resource) throws IOException {
    String name = resource.name();
    if ((name == null) || name.isBlank() || name.contains("/") || name.contains("\\") || name.contains("..")) {
      throw new IOException("Unsafe resource identity name: " + name);
    }
    if ((resource.contentType() == null) || resource.contentType().isBlank()) {
      throw new IOException("Resource " + name + " must have a content type");
    }
    requireSafeRelativePath(resource.archivePath(), "resource archive path");
    if (resource.isOpaqueBinary()) {
      throw new IOException("Resource " + name + " has opaque binary data that is not allowed in replay summaries");
    }
  }

  private static String requireSafeRelativePath(String path, String label) throws IOException {
    try {
      return ReplaySourceInput.requireSafeRelativePath(path, label);
    } catch (IllegalArgumentException e) {
      throw new IOException(e.getMessage(), e);
    }
  }

  private static String normalizeScalar(String value) {
    return ReplaySourceInput.normalizeText(value).replace('\n', ' ').trim();
  }

  private static void validateSummaryHygiene(String text) throws IOException {
    validatePortableText("summary", text);
    String userHome = System.getProperty("user.home");
    String tempDirectory = System.getProperty("java.io.tmpdir");
    if ((userHome != null) && !userHome.isBlank() && text.contains(userHome)) {
      throw new IOException("Replay summary contains the local user home path");
    }
    if ((tempDirectory != null) && !tempDirectory.isBlank() && text.contains(tempDirectory)) {
      throw new IOException("Replay summary contains the local temporary directory");
    }
    if (text.matches("(?s).*\\b[0-9]{4}-[0-9]{2}-[0-9]{2}\\b.*")) {
      throw new IOException("Replay summary contains a timestamp-like date");
    }
    if (text.matches("(?s).*@[0-9a-fA-F]{6,}\\b.*")) {
      throw new IOException("Replay summary contains a JVM identity-like value");
    }
  }

  private static void validatePortableText(String label, String text) throws IOException {
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if ((ch != '\n') && (ch != '\t') && Character.isISOControl(ch)) {
        throw new IOException(label + " contains control character U+" + String.format("%04X", (int) ch));
      }
    }
  }

  static String sha256(byte[] bytes) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(bytes);
      StringBuilder builder = new StringBuilder(hash.length * 2);
      for (byte value : hash) {
        builder.append(String.format("%02x", value));
      }
      return builder.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError("SHA-256 must be available", e);
    }
  }

  static final class Input {
    private final String providerName;
    private final Map<String, List<String>> archiveEntries;
    private final Map<String, Map<String, String>> manifestFields;
    private final List<ResourceIdentity> resources;

    private Input(
        String providerName,
        Map<String, List<String>> archiveEntries,
        Map<String, Map<String, String>> manifestFields,
        List<ResourceIdentity> resources) {
      this.providerName = providerName;
      this.archiveEntries = archiveEntries;
      this.manifestFields = manifestFields;
      this.resources = resources;
    }

    static Builder builder(String providerName) {
      return new Builder(providerName);
    }

    static final class Builder {
      private final String providerName;
      private final Map<String, List<String>> archiveEntries = new LinkedHashMap<>();
      private final Map<String, Map<String, String>> manifestFields = new LinkedHashMap<>();
      private final List<ResourceIdentity> resources = new ArrayList<>();

      private Builder(String providerName) {
        this.providerName = Objects.requireNonNull(providerName, "providerName");
      }

      Builder addArchiveEntries(String archiveType, List<String> entries) {
        archiveEntries.put(archiveType, List.copyOf(entries));
        return this;
      }

      Builder addManifestFields(String archiveType, Map<String, String> fields) {
        manifestFields.put(archiveType, Map.copyOf(fields));
        return this;
      }

      Builder addResource(ResourceIdentity resource) {
        resources.add(Objects.requireNonNull(resource, "resource"));
        return this;
      }

      Input build() {
        Map<String, List<String>> archiveCopy = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : archiveEntries.entrySet()) {
          archiveCopy.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        Map<String, Map<String, String>> manifestCopy = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : manifestFields.entrySet()) {
          manifestCopy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }
        return new Input(providerName, Map.copyOf(archiveCopy), Map.copyOf(manifestCopy), List.copyOf(resources));
      }
    }
  }

  static final class ResourceIdentity {
    private final String name;
    private final String contentType;
    private final String archivePath;
    private final byte[] data;

    ResourceIdentity(String name, String contentType, String archivePath, byte[] data) {
      this.name = Objects.requireNonNull(name, "name");
      this.contentType = Objects.requireNonNull(contentType, "contentType");
      this.archivePath = Objects.requireNonNull(archivePath, "archivePath");
      this.data = Objects.requireNonNull(data, "data").clone();
    }

    String name() {
      return name;
    }

    String contentType() {
      return contentType;
    }

    String archivePath() {
      return archivePath;
    }

    byte[] data() {
      return data.clone();
    }

    String sortKey() {
      return name + "\n" + contentType + "\n" + archivePath + "\n" + sha256(data);
    }

    private boolean isOpaqueBinary() {
      if (!"application/octet-stream".equalsIgnoreCase(contentType)) {
        return false;
      }
      for (byte value : data) {
        int unsigned = value & 0xFF;
        if ((unsigned < 0x20) && (unsigned != '\n') && (unsigned != '\r') && (unsigned != '\t')) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof ResourceIdentity that)) {
        return false;
      }
      return name.equals(that.name)
          && contentType.equals(that.contentType)
          && archivePath.equals(that.archivePath)
          && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
      int result = Objects.hash(name, contentType, archivePath);
      result = 31 * result + Arrays.hashCode(data);
      return result;
    }
  }
}
