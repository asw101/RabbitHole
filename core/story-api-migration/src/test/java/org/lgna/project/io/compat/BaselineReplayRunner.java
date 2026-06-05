package org.lgna.project.io.compat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

final class BaselineReplayRunner implements ReplaySummaryProvider {
  static final String TIMEOUT_PROPERTY = "rabbithole.dualBaseline.timeoutSeconds";
  private static final String SCRIPT_RELATIVE_PATH = "scripts/rabbithole-replay-summary";

  private final BaselineCheckout checkout;
  private final Duration timeout;

  BaselineReplayRunner(BaselineCheckout checkout) {
    this(checkout, Duration.ofSeconds(Long.getLong(TIMEOUT_PROPERTY, 120L)));
  }

  BaselineReplayRunner(BaselineCheckout checkout, Duration timeout) {
    this.checkout = Objects.requireNonNull(checkout, "checkout");
    this.timeout = Objects.requireNonNull(timeout, "timeout");
    if (timeout.isZero() || timeout.isNegative()) {
      throw new IllegalArgumentException("Baseline replay timeout must be positive: " + timeout);
    }
  }

  @Override
  public ReplaySummary summarize(ReplayCase replayCase) throws IOException {
    Objects.requireNonNull(replayCase, "replayCase");
    validateRegisteredCase(replayCase);
    Path script = checkout.root().resolve(SCRIPT_RELATIVE_PATH);
    if (!Files.isRegularFile(script)) {
      throw new IOException("Baseline replay script not found: " + SCRIPT_RELATIVE_PATH + " in " + checkout.root());
    }
    if (!Files.isExecutable(script)) {
      throw new IOException("Baseline replay script is not executable: " + script);
    }

    List<String> command = List.of(
        script.toString(),
        "--case",
        replayCase.id(),
        "--schema",
        ReplaySummaryWriter.SCHEMA);
    Process process = new ProcessBuilder(command)
        .directory(checkout.root().toFile())
        .start();
    ExecutorService streamReaders = Executors.newFixedThreadPool(2);
    Future<byte[]> stdout = streamReaders.submit(() -> process.getInputStream().readAllBytes());
    Future<byte[]> stderr = streamReaders.submit(() -> process.getErrorStream().readAllBytes());
    try {
      if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
        process.destroyForcibly();
        process.waitFor(5, TimeUnit.SECONDS);
        String diagnostics = decodeBestEffort(readFuture(stderr));
        throw new IOException(
            "Baseline replay for case "
                + replayCase.id()
                + " timed out after "
                + timeout.toSeconds()
                + " seconds; command="
                + command
                + "; stderr="
                + diagnostics);
      }
      String stderrText = decodeBestEffort(readFuture(stderr));
      if (process.exitValue() != 0) {
        throw new IOException(
            "Baseline replay for case "
                + replayCase.id()
                + " exited with code "
                + process.exitValue()
                + "; stderr="
                + stderrText);
      }
      String stdoutText = decodeUtf8(readFuture(stdout), replayCase.id());
      try {
        return new ReplaySummary(replayCase.id(), "Baseline", stdoutText);
      } catch (IllegalArgumentException e) {
        throw new IOException(
            "Baseline replay for case " + replayCase.id() + " emitted malformed summary; stderr=" + stderrText,
            e);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while running baseline replay for case " + replayCase.id(), e);
    } finally {
      streamReaders.shutdownNow();
    }
  }

  private static void validateRegisteredCase(ReplayCase replayCase) throws IOException {
    try {
      new ReplayCaseFactory().caseNamed(replayCase.id());
    } catch (IllegalArgumentException e) {
      throw new IOException("Baseline replay case is not registered: " + replayCase.id(), e);
    }
  }

  private static byte[] readFuture(Future<byte[]> future) throws IOException {
    try {
      return future.get(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while reading baseline replay process output", e);
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof IOException ioException) {
        throw ioException;
      }
      if (cause instanceof UncheckedIOException uncheckedIOException) {
        throw uncheckedIOException.getCause();
      }
      throw new IOException("Unable to read baseline replay process output", cause);
    } catch (TimeoutException e) {
      throw new IOException("Timed out reading baseline replay process output", e);
    }
  }

  private static String decodeUtf8(byte[] bytes, String caseId) throws IOException {
    try {
      return StandardCharsets.UTF_8.newDecoder()
          .onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT)
          .decode(ByteBuffer.wrap(bytes))
          .toString();
    } catch (CharacterCodingException e) {
      throw new IOException("Baseline replay for case " + caseId + " emitted non-UTF-8 stdout", e);
    }
  }

  private static String decodeBestEffort(byte[] bytes) {
    return ReplaySourceInput.normalizeText(new String(bytes, StandardCharsets.UTF_8));
  }
}
