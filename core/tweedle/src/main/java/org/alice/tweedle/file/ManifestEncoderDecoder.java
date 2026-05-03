package org.alice.tweedle.file;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.cmu.cs.dennisc.java.util.logging.Logger;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public class ManifestEncoderDecoder {
  static final String PROGRESSIVE_8601_FORMAT = "yyyy[-MM[-dd['T'HH:mm:ss[Z][z]]]]";

  public static String toJson(Manifest manifest) {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(Temporal.class, new ProgressiveTemporalSerializer());
    mapper.registerModule(module);
      try {
          return mapper.writeValueAsString(manifest);
      } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
      }
  }

  public static <T extends Manifest> T fromJson(String string, Class<T> manifestClass) {
    try {
      return readJson(string, manifestClass);
    } catch (Throwable e) {
      Logger.warning("Skipping over error: Unable to read manifest from save file due to \"" + e.getMessage() + "\"\n" + e.getStackTrace());
      return null;
    }
  }

  /**
   * Decode manifest JSON for callers that must treat malformed manifests as
   * errors.
   *
   * @param <T> decoded manifest type
   * @param string JSON content
   * @param manifestClass manifest type
   * @return decoded manifest
   * @throws IOException when JSON cannot be decoded into a manifest
   */
  public static <T extends Manifest> T fromJsonOrThrow(
      final String string,
      final Class<T> manifestClass) throws IOException {
    try {
      T manifest = readJson(string, manifestClass);
      if (manifest == null) {
        throw new IOException("Decoder returned null manifest");
      }
      return manifest;
    } catch (IOException | RuntimeException e) {
      throw new IOException("Unable to read manifest from save file", e);
    }
  }

  private static <T extends Manifest> T readJson(
      final String string,
      final Class<T> manifestClass) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(
        Temporal.class,
        new ProgressiveTemporalDeserializer());
    mapper.registerModule(module);
    return mapper.readValue(string, manifestClass);
  }

  private static class ProgressiveTemporalSerializer extends JsonSerializer<Temporal> {
    @Override
    public void serialize(Temporal temporal, JsonGenerator generator, SerializerProvider provider) throws IOException {
      DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(PROGRESSIVE_8601_FORMAT);
      generator.writeString(timeFormatter.format(temporal));
    }
  }

  private static class ProgressiveTemporalDeserializer extends JsonDeserializer<Temporal> {
    @Override
    public Temporal deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException {
      String savedValue = parser.getValueAsString();
      DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(PROGRESSIVE_8601_FORMAT);
      return (Temporal) timeFormatter.parseBest(savedValue,
              ZonedDateTime::from,
              OffsetDateTime::from,
              LocalDateTime::from,
              LocalDate::from,
              YearMonth::from,
              Year::from);
    }
  }
}
