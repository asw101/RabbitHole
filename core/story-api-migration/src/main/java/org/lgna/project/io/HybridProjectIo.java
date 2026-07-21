/*******************************************************************************
 * Copyright (c) 2018, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.lgna.project.io;

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.java.util.zip.DataSource;
import edu.cmu.cs.dennisc.xml.XMLUtilities;
import org.alice.tweedle.file.AudioReference;
import org.alice.tweedle.file.ImageReference;
import org.alice.tweedle.file.Manifest;
import org.alice.tweedle.file.ManifestEncoderDecoder;
import org.alice.tweedle.file.ProjectManifest;
import org.alice.tweedle.file.ResourceReference;
import org.alice.tweedle.file.TypeManifest;
import org.alice.tweedle.file.TypeReference;
import org.lgna.common.Resource;
import org.lgna.project.Project;
import org.lgna.project.Version;
import org.lgna.project.VersionNotSupportedException;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.resourceutilities.ResourceTypeHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Within-archive hybrid project reader/writer for the readable {@code .a3c}
 * (class) and {@code .a3p} (project) formats.
 *
 * <p>A hybrid archive is <em>additive</em> to the legacy XML container: every
 * archive contains <em>both</em> the legacy XML AST payload
 * ({@code programType.xml}/{@code type.xml} + {@code resources.xml}) <em>and</em>
 * per-type Tweedle source ({@code src/&lt;Type&gt;.twe}) referenced from
 * {@code manifest.json}. This keeps exports backward compatible while enabling a
 * bounded, name-only Tweedle representation (references to other user types are
 * emitted as bare names rather than dragging the whole referenced object graph
 * into the archive).</p>
 *
 * <p><b>Writer:</b> composes the existing {@link XmlProjectIo} and
 * {@link JsonProjectIo} writers. Each is run to an in-memory ZIP; the entries are
 * then merged into a single archive. The <em>XML</em> side is authoritative for
 * the shared resource-binary namespace ({@code resources/}, {@code resources.xml})
 * because it faithfully persists every resource type (the Tweedle side only
 * carries image/audio/model resources). The Tweedle side supplies
 * {@code src/*.twe}, {@code models/}, and the manifest {@code TypeReference}s. To
 * keep both readers consistent over the single, XML-authoritative resource
 * binaries, the merged manifest's image/audio references are repointed to the XML
 * resource entry names (matched by UUID). If the Tweedle encode step fails for
 * any reason, the writer degrades to an XML-only archive so exports are never
 * worse than the legacy format.</p>
 *
 * <p><b>Reader:</b> prefers the Tweedle representation. When every manifest-declared
 * type decodes <em>and</em> every resource the archive declares is recovered, the
 * archive is read entirely from Tweedle and the XML AST is never decoded. If any
 * type hits a decoder gap (or the Tweedle read otherwise fails or drops a
 * declared resource), the reader transparently falls back to the legacy XML
 * payload in the same archive, so decoding is never worse than today. The
 * fallback is whole-archive rather than per-type: mixing Tweedle-decoded and
 * XML-decoded types into one project cannot preserve the object identity that
 * cross-type references rely on, so any gap routes the whole read through the
 * (loud, migration-aware) XML reader.</p>
 */
final class HybridProjectIo {

  private HybridProjectIo() {
    throw new AssertionError();
  }

  static ProjectIo.ProjectWriter writer() {
    return new HybridProjectWriter();
  }

  static ProjectIo.ProjectReader reader(ZipEntryContainer container) {
    return new HybridProjectReader(container);
  }

  /**
   * True when the archive is a within-archive hybrid: its manifest declares at
   * least one Tweedle {@link TypeReference} <em>and</em> the archive also carries
   * a legacy XML AST payload ({@code programType.xml} or {@code type.xml}).
   */
  static boolean isHybridArchive(Manifest manifest, ZipEntryContainer container) throws IOException {
    if ((manifest == null) || (manifest.resources == null)) {
      return false;
    }
    boolean hasTypeReference = false;
    for (ResourceReference resourceReference : manifest.resources) {
      if (resourceReference instanceof TypeReference) {
        hasTypeReference = true;
        break;
      }
    }
    if (!hasTypeReference) {
      return false;
    }
    return container.hasEntry(XmlProjectIo.PROGRAM_TYPE_ENTRY_NAME)
        || container.hasEntry(XmlProjectIo.TYPE_ENTRY_NAME);
  }

  @FunctionalInterface
  private interface ArchiveWrite {
    void writeTo(OutputStream os) throws IOException;
  }

  private static byte[] toBytes(ArchiveWrite write) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    write.writeTo(baos);
    return baos.toByteArray();
  }

  private static LinkedHashMap<String, byte[]> readEntries(byte[] zipBytes) throws IOException {
    LinkedHashMap<String, byte[]> entries = new LinkedHashMap<>();
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          entries.put(entry.getName(), zis.readAllBytes());
        }
        zis.closeEntry();
      }
    }
    return entries;
  }

  private static void writeEntries(OutputStream os, Map<String, byte[]> entries) throws IOException {
    try (ZipOutputStream zos = new ZipOutputStream(os)) {
      for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
        zos.putNextEntry(new ZipEntry(entry.getKey()));
        zos.write(entry.getValue());
        zos.closeEntry();
      }
    }
  }

  /**
   * Merge the Tweedle archive's entries into the XML archive. XML is authoritative
   * for the shared resource-binary namespace; Tweedle-only entries
   * ({@code src/*.twe}, {@code models/}) are added. The combined manifest keeps the
   * authoritative XML/caller metadata but adopts the Tweedle {@code resources[]}
   * (type references + resource references), with image/audio references repointed
   * to the XML resource entry names by UUID so both readers agree on one set of
   * resource binaries.
   */
  private static void writeMerged(
      OutputStream os,
      byte[] tweedleZip,
      byte[] xmlZip,
      String fileType) throws IOException {
    LinkedHashMap<String, byte[]> entries = readEntries(xmlZip);
    LinkedHashMap<String, byte[]> tweedleEntries = readEntries(tweedleZip);

    byte[] xmlManifest = entries.get(ProjectIo.MANIFEST_ENTRY_NAME);
    byte[] tweedleManifest = tweedleEntries.get(ProjectIo.MANIFEST_ENTRY_NAME);

    for (Map.Entry<String, byte[]> tweedleEntry : tweedleEntries.entrySet()) {
      if (!ProjectIo.MANIFEST_ENTRY_NAME.equals(tweedleEntry.getKey())) {
        entries.putIfAbsent(tweedleEntry.getKey(), tweedleEntry.getValue());
      }
    }

    Map<UUID, String> xmlEntryNamesByUuid = resourceEntryNamesByUuid(entries.get(XmlProjectIo.RESOURCES_ENTRY_NAME));
    entries.put(
        ProjectIo.MANIFEST_ENTRY_NAME,
        mergeManifest(xmlManifest, tweedleManifest, fileType, xmlEntryNamesByUuid));
    writeEntries(os, entries);
  }

  private static byte[] mergeManifest(
      byte[] xmlManifest,
      byte[] tweedleManifest,
      String fileType,
      Map<UUID, String> xmlEntryNamesByUuid) {
    if (tweedleManifest == null) {
      // The Tweedle writer always emits a manifest; this only guards corruption.
      return xmlManifest;
    }
    try {
      boolean project = IoUtilities.PROJECT_EXTENSION.equals(fileType);
      Class<? extends Manifest> manifestClass = project ? ProjectManifest.class : TypeManifest.class;
      Manifest tweedle = ManifestEncoderDecoder.fromJsonOrThrow(
          new String(tweedleManifest, StandardCharsets.UTF_8), manifestClass);
      repointResourceReferences(tweedle.resources, xmlEntryNamesByUuid);
      Manifest base = tweedle;
      if (xmlManifest != null) {
        // Preserve the authoritative XML/caller metadata (name, icon, camera type),
        // but adopt the Tweedle type/resource references.
        base = ManifestEncoderDecoder.fromJsonOrThrow(
            new String(xmlManifest, StandardCharsets.UTF_8), manifestClass);
        base.resources = tweedle.resources;
      }
      base.metadata.fileType = fileType;
      base.prerequisites = new ArrayList<>();
      return ManifestEncoderDecoder.toJson(base).getBytes(StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void repointResourceReferences(
      List<ResourceReference> resources,
      Map<UUID, String> xmlEntryNamesByUuid) {
    if ((resources == null) || xmlEntryNamesByUuid.isEmpty()) {
      return;
    }
    for (ResourceReference resourceReference : resources) {
      UUID uuid = resourceReferenceUuid(resourceReference);
      if (uuid != null) {
        String xmlEntryName = xmlEntryNamesByUuid.get(uuid);
        if (xmlEntryName != null) {
          resourceReference.file = xmlEntryName;
        }
      }
    }
  }

  private static UUID resourceReferenceUuid(ResourceReference resourceReference) {
    if (resourceReference instanceof ImageReference imageReference) {
      return imageReference.uuid;
    }
    if (resourceReference instanceof AudioReference audioReference) {
      return audioReference.uuid;
    }
    return null;
  }

  private static Map<UUID, String> resourceEntryNamesByUuid(byte[] resourcesXml) throws IOException {
    Map<UUID, String> entryNamesByUuid = new LinkedHashMap<>();
    if (resourcesXml == null) {
      return entryNamesByUuid;
    }
    Document document = SecureXmlParser.readArchiveXml(
        new ByteArrayInputStream(resourcesXml), XmlProjectIo.RESOURCES_ENTRY_NAME);
    for (Element element : XMLUtilities.getChildElementsByTagName(document.getDocumentElement(), "resource")) {
      String uuidText = element.getAttribute("uuid");
      String entryName = element.getAttribute("entryName");
      if ((uuidText != null) && !uuidText.isEmpty() && (entryName != null) && !entryName.isEmpty()) {
        try {
          entryNamesByUuid.put(UUID.fromString(uuidText), entryName);
        } catch (IllegalArgumentException e) {
          // Skip malformed UUIDs; the reader's completeness check will trigger XML fallback.
        }
      }
    }
    return entryNamesByUuid;
  }

  private static final class HybridProjectWriter implements ProjectIo.ProjectWriter {
    private final ProjectIo.ProjectWriter tweedleWriter = JsonProjectIo.writer();
    private final ProjectIo.ProjectWriter xmlWriter = XmlProjectIo.writer();

    @Override
    public void writeProject(OutputStream os, Project project, DataSource[] dataSources) throws IOException {
      byte[] xmlZip = toBytes(o -> xmlWriter.writeProject(o, project, dataSources));
      byte[] tweedleZip;
      try {
        tweedleZip = toBytes(o -> tweedleWriter.writeProject(o, project, dataSourcesForTweedle(dataSources)));
      } catch (IOException | RuntimeException e) {
        Logger.throwable(e, "hybrid Tweedle project export failed; writing XML-only archive");
        os.write(xmlZip);
        return;
      }
      writeMerged(os, tweedleZip, xmlZip, IoUtilities.PROJECT_EXTENSION);
    }

    @Override
    public void writeType(OutputStream os, NamedUserType type, DataSource[] dataSources) throws IOException {
      byte[] xmlZip = toBytes(o -> xmlWriter.writeType(o, type, dataSources));
      byte[] tweedleZip;
      try {
        tweedleZip = toBytes(o -> tweedleWriter.writeType(o, type, dataSourcesForTweedle(dataSources)));
      } catch (IOException | RuntimeException e) {
        Logger.throwable(e, "hybrid Tweedle type export failed; writing XML-only archive");
        os.write(xmlZip);
        return;
      }
      writeMerged(os, tweedleZip, xmlZip, IoUtilities.TYPE_EXTENSION);
    }

    /**
     * The Tweedle writer always emits its own {@code manifest.json} and
     * {@code version.txt}. A caller that also supplies one of those as a
     * {@link DataSource} (notably the IDE, which passes its save {@code manifest.json}
     * plus a thumbnail) would trigger a duplicate-entry {@link IOException} inside the
     * Tweedle encode, silently degrading every such save to an XML-only archive. The
     * merged manifest is composed from the XML side regardless, so those caller entries
     * are not needed by the Tweedle side; the rest (e.g. the thumbnail) is preserved via
     * the XML archive.
     */
    private static DataSource[] dataSourcesForTweedle(DataSource[] dataSources) {
      List<DataSource> filtered = new ArrayList<>(dataSources.length);
      for (DataSource dataSource : dataSources) {
        String name = (dataSource == null) ? null : dataSource.getName();
        if (ProjectIo.MANIFEST_ENTRY_NAME.equals(name) || ProjectIo.VERSION_ENTRY_NAME.equals(name)) {
          continue;
        }
        filtered.add(dataSource);
      }
      return filtered.toArray(new DataSource[0]);
    }
  }

  private static final class HybridProjectReader implements ProjectIo.ProjectReader {
    private final ZipEntryContainer container;
    private final ProjectIo.ProjectReader tweedleReader;
    private final ProjectIo.ProjectReader xmlReader;

    HybridProjectReader(ZipEntryContainer container) {
      this.container = container;
      this.tweedleReader = JsonProjectIo.reader(container);
      this.xmlReader = XmlProjectIo.reader(container);
    }

    @Override
    public Project readProject(boolean makeVrReady) throws IOException, VersionNotSupportedException {
      if (makeVrReady) {
        return xmlReader.readProject(true);
      }
      Throwable tweedleIssue;
      try {
        Project project = tweedleReader.readProject(false);
        if (declaredResourcesRecovered(project.getResources())) {
          return project;
        }
        tweedleIssue = incompleteResourceRecovery();
      } catch (IOException | RuntimeException e) {
        tweedleIssue = e;
      }
      return fallBackToXml(tweedleIssue, () -> xmlReader.readProject(makeVrReady));
    }

    @Override
    public TypeResourcesPair readType() throws IOException, VersionNotSupportedException {
      Throwable tweedleIssue;
      try {
        TypeResourcesPair pair = tweedleReader.readType();
        if (declaredResourcesRecovered(pair.getResources())) {
          return pair;
        }
        tweedleIssue = incompleteResourceRecovery();
      } catch (IOException | RuntimeException e) {
        tweedleIssue = e;
      }
      return fallBackToXml(tweedleIssue, xmlReader::readType);
    }

    @Override
    public Version checkForFutureVersion() throws IOException {
      return xmlReader.checkForFutureVersion();
    }

    @Override
    public void setResourceTypeHelper(ResourceTypeHelper typeHelper) {
      tweedleReader.setResourceTypeHelper(typeHelper);
      xmlReader.setResourceTypeHelper(typeHelper);
    }

    /**
     * True when every resource the legacy {@code resources.xml} declares was
     * recovered by the Tweedle read. The Tweedle reader only reconstructs
     * image/audio resources from the manifest, so archives carrying other resource
     * types would silently drop them; detecting that here routes the read through
     * the complete XML fallback instead. Returns {@code false} (forcing fallback)
     * if the declared resources cannot be enumerated.
     */
    private boolean declaredResourcesRecovered(Collection<Resource> recoveredResources) {
      Set<UUID> declared;
      try {
        declared = declaredResourceUuids();
      } catch (IOException | RuntimeException e) {
        return false;
      }
      if (declared.isEmpty()) {
        return true;
      }
      Set<UUID> recovered = new HashSet<>();
      for (Resource resource : recoveredResources) {
        recovered.add(resource.getId());
      }
      return recovered.containsAll(declared);
    }

    private Set<UUID> declaredResourceUuids() throws IOException {
      Set<UUID> uuids = new HashSet<>();
      InputStream is = container.getInputStream(XmlProjectIo.RESOURCES_ENTRY_NAME);
      if (is == null) {
        return uuids;
      }
      Document document;
      try (InputStream resourcesStream = is) {
        document = SecureXmlParser.readArchiveXml(resourcesStream, XmlProjectIo.RESOURCES_ENTRY_NAME);
      }
      for (Element element : XMLUtilities.getChildElementsByTagName(document.getDocumentElement(), "resource")) {
        String uuidText = element.getAttribute("uuid");
        if ((uuidText != null) && !uuidText.isEmpty()) {
          uuids.add(UUID.fromString(uuidText));
        }
      }
      return uuids;
    }

    private static IOException incompleteResourceRecovery() {
      return new IOException(
          "Tweedle read did not recover all resources declared in "
              + XmlProjectIo.RESOURCES_ENTRY_NAME + "; using XML fallback");
    }

    private interface XmlRead<T> {
      T read() throws IOException, VersionNotSupportedException;
    }

    private static <T> T fallBackToXml(Throwable tweedleIssue, XmlRead<T> xmlRead)
        throws IOException, VersionNotSupportedException {
      try {
        return xmlRead.read();
      } catch (IOException | VersionNotSupportedException | RuntimeException xmlFailure) {
        if (tweedleIssue != null) {
          xmlFailure.addSuppressed(tweedleIssue);
        }
        if (xmlFailure instanceof VersionNotSupportedException versionNotSupportedException) {
          throw versionNotSupportedException;
        }
        if (xmlFailure instanceof IOException ioException) {
          throw ioException;
        }
        throw (RuntimeException) xmlFailure;
      }
    }
  }
}
