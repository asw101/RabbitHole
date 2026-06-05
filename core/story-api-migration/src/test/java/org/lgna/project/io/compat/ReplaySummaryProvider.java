package org.lgna.project.io.compat;

@FunctionalInterface
interface ReplaySummaryProvider {
  ReplaySummary summarize(ReplayCase replayCase) throws Exception;
}
