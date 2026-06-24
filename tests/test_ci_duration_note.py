import os
import stat
import subprocess
import tempfile
import textwrap
import unittest
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
SCRIPT_PATH = REPO_ROOT / "scripts" / "ci-duration-note.sh"


def write_executable(path: Path, source: str) -> Path:
    path.write_text(textwrap.dedent(source).lstrip(), encoding="utf-8")
    mode = path.stat().st_mode
    path.chmod(mode | stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH)
    return path


class CiDurationNoteTest(unittest.TestCase):
    def setUp(self) -> None:
        self.temp_dir = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp_dir.cleanup)
        self.work_dir = Path(self.temp_dir.name)

    def run_script(
        self, *args: str, summary_path: Path | None = None
    ) -> subprocess.CompletedProcess[str]:
        env = os.environ.copy()
        if summary_path is not None:
            env["GITHUB_STEP_SUMMARY"] = str(summary_path)
        return subprocess.run(
            ["bash", str(SCRIPT_PATH), *args],
            cwd=REPO_ROOT,
            env=env,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            timeout=10,
            check=False,
        )

    def test_success_preserves_stdout_and_writes_duration_notice(self) -> None:
        command = write_executable(
            self.work_dir / "success-command",
            """\
            #!/usr/bin/env bash
            set -euo pipefail
            printf 'wrapped stdout\n'
            printf 'wrapped stderr\n' >&2
            """,
        )
        summary = self.work_dir / "summary.md"

        result = self.run_script("Wrapped validation", "--", str(command), summary_path=summary)

        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn("wrapped stdout\n", result.stdout)
        self.assertIn("::notice title=CI duration::Wrapped validation completed in ", result.stdout)
        self.assertIn("wrapped stderr\n", result.stderr)
        summary_text = summary.read_text(encoding="utf-8")
        self.assertIn("| Wrapped validation | completed |", summary_text)

    def test_failure_preserves_exit_status_and_records_failed_outcome(self) -> None:
        command = write_executable(
            self.work_dir / "failure-command",
            """\
            #!/usr/bin/env bash
            set -euo pipefail
            printf 'failure details\n' >&2
            exit 7
            """,
        )

        result = self.run_script("Failing validation", "--", str(command))

        self.assertEqual(result.returncode, 7)
        self.assertIn("::notice title=CI duration::Failing validation failed in ", result.stdout)
        self.assertIn("failure details\n", result.stderr)

    def test_requires_separator_before_command(self) -> None:
        result = self.run_script("Missing separator", "printf", "hello")

        self.assertEqual(result.returncode, 2)
        self.assertIn("separated", result.stderr)


if __name__ == "__main__":
    unittest.main()
