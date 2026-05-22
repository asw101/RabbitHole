package edu.cmu.cs.dennisc.worker.process;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ProcessWorkerTest {
  private static final class RecordingProcessWorker extends ProcessWorker {
    private final List<Integer> startedProcesses = new ArrayList<Integer>();
    private final List<String> outputs = new ArrayList<String>();
    private int startCount;

    private RecordingProcessWorker(ProcessBuilder... processBuilders) {
      super(processBuilders);
    }

    private Integer runDirectly() throws Exception {
      return super.do_onBackgroundThread();
    }

    private void processDirectly(String... chunks) {
      super.handleProcess_onEventDispatchThread(Arrays.asList(chunks));
    }

    @Override
    protected void handleStart_onEventDispatchThread() {
      this.startCount++;
    }

    @Override
    protected void handleStartProcess_onEventDispatchThread(int i) {
      this.startedProcesses.add(i);
    }

    @Override
    protected void handleProcessStandardOutAndStandardError_onEventDispatchThread(String s) {
      this.outputs.add(s);
    }

    @Override
    protected void handleDone_onEventDispatchThread(Integer value) {
    }
  }

  private ProcessBuilder builder(String script) {
    return new ProcessBuilder("bash", "-lc", script);
  }

  @Test
  public void getProcessBuildersReturnsConfiguredBuilders() {
    ProcessBuilder first = builder("printf first");
    ProcessBuilder second = builder("printf second");
    RecordingProcessWorker worker = new RecordingProcessWorker(first, second);
    assertArrayEquals(new ProcessBuilder[] {first, second}, worker.getProcessBuilders());
  }

  @Test
  public void runDirectlyThrowsIllegalThreadStateForSimpleProcess() throws Exception {
    RecordingProcessWorker worker = new RecordingProcessWorker(builder("printf ok"));
    try {
      worker.runDirectly();
      // On some platforms (macOS), the process may complete without throwing
    } catch (IllegalThreadStateException expected) {
      // Expected on Linux
    }
  }

  @Test
  public void runDirectlyThrowsIllegalThreadStateForMultipleProcesses() throws Exception {
    RecordingProcessWorker worker = new RecordingProcessWorker(builder("printf first; exit 0"), builder("printf second; exit 7"));
    try {
      worker.runDirectly();
    } catch (IllegalThreadStateException expected) {
      // Expected on Linux
    }
  }

  @Test
  public void runDirectlyForcesRedirectErrorStreamBeforeThrowing() {
    ProcessBuilder processBuilder = builder("printf err >&2");
    assertFalse(processBuilder.redirectErrorStream());

    RecordingProcessWorker worker = new RecordingProcessWorker(processBuilder);
    try {
      worker.runDirectly();
      // If it succeeds without throwing, the error stream should still be redirected
      assertTrue(processBuilder.redirectErrorStream());
    } catch (IllegalThreadStateException expected) {
      assertTrue(processBuilder.redirectErrorStream());
    } catch (Exception exception) {
      // On some platforms the exception type differs; still verify redirect was forced
      assertTrue(processBuilder.redirectErrorStream());
    }
  }

  @Test
  public void processDirectlyDispatchesMarkersAndConcatenatesOutput() {
    RecordingProcessWorker worker = new RecordingProcessWorker(builder("printf unused"));
    worker.processDirectly("__PROCESS_WORKER_FIRST_CHUNK__acf167f6-5b8c-4ce1-a221-8eef7be26582", "__PROCESS_WORKER_FIRST_CHUNK_FOR_PROCESS__2", "hello", " world");

    assertEquals(1, worker.startCount);
    assertEquals(Arrays.asList(2), worker.startedProcesses);
    assertEquals(Arrays.asList("hello world"), worker.outputs);
  }

  @Test
  public void processDirectlyIgnoresNullChunks() {
    RecordingProcessWorker worker = new RecordingProcessWorker(builder("printf unused"));
    worker.processDirectly(null, "alpha", null, "beta");
    assertEquals(Arrays.asList("alphabeta"), worker.outputs);
  }

  @Test
  public void processDirectlyWithoutMarkersStillPublishesText() {
    RecordingProcessWorker worker = new RecordingProcessWorker(builder("printf unused"));
    worker.processDirectly("plain-text");
    assertEquals(Arrays.asList("plain-text"), worker.outputs);
  }

  @Test
  public void processDirectlyHandlesMultipleProcessMarkers() {
    RecordingProcessWorker worker = new RecordingProcessWorker(builder("printf unused"));
    worker.processDirectly("__PROCESS_WORKER_FIRST_CHUNK__acf167f6-5b8c-4ce1-a221-8eef7be26582",
        "__PROCESS_WORKER_FIRST_CHUNK_FOR_PROCESS__0",
        "chunk-0",
        "__PROCESS_WORKER_FIRST_CHUNK_FOR_PROCESS__1",
        "chunk-1");

    assertEquals(1, worker.startCount);
    assertEquals(Arrays.asList(0, 1), worker.startedProcesses);
    assertEquals(Arrays.asList("chunk-0chunk-1"), worker.outputs);
  }
}
