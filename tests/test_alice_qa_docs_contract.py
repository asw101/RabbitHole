"""Documentation contract for the neutral Alice QA command surface."""

from __future__ import annotations

import re
import unittest
from functools import lru_cache
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
WHITESPACE_RE = re.compile(r"\s+")
BRANDED_QA_RE = re.compile(r"\b(?:amplihack|gadugi|gadugi-test|copilot)\b", re.IGNORECASE)

QA_DOCS = (
    REPO_ROOT / "README.md",
    REPO_ROOT / "docs" / "getting-started.md",
    REPO_ROOT / "docs" / "testing.md",
    REPO_ROOT / "docs" / "repository-hygiene.md",
    REPO_ROOT / "qa" / "outside-in" / "alice-desktop" / "README.md",
)

NEGATIVE_CONTRACT_DOC = REPO_ROOT / "qa" / "outside-in" / "alice-desktop" / "README.md"
PYPROJECT_PATH = REPO_ROOT / "pyproject.toml"


@lru_cache(maxsize=None)
def read_doc(path: Path) -> str:
    return path.read_text(encoding="utf-8")


@lru_cache(maxsize=None)
def normalized_doc(path: Path) -> str:
    return WHITESPACE_RE.sub(" ", read_doc(path))


class AliceQaDocsContractTest(unittest.TestCase):
    def test_qa_docs_name_neutral_local_wrapper(self) -> None:
        for path in QA_DOCS:
            text = read_doc(path)

            with self.subTest(path=path.relative_to(REPO_ROOT)):
                self.assertIn("python3 alice_qa.py", text)
                self.assertNotRegex(text, BRANDED_QA_RE)
                self.assertNotIn("alice_qa_amplihack", text)
                self.assertNotIn("branch-installable", text.lower())
                self.assertNotIn("uvx --from git+", text)

    def test_negative_save_contract_docs_keep_cli_wrapper_bounded_to_artifact_validation(self) -> None:
        text = read_doc(NEGATIVE_CONTRACT_DOC)
        normalized = normalized_doc(NEGATIVE_CONTRACT_DOC)

        self.assertIn("python3 alice_qa.py alice-qa save-negative-contract", text)
        self.assertIn(
            "qa/outside-in/alice-desktop/tests/test-save-menu-dialog-negative-artifact-contract.sh",
            text,
        )
        self.assertIn("accepts no extra arguments", normalized)
        self.assertIn(
            "success proves only that invalid Save proof artifacts fail closed",
            normalized,
        )

    def test_shared_qa_docs_do_not_claim_wrapper_proves_desktop_save_completion(self) -> None:
        normalized = normalized_doc(NEGATIVE_CONTRACT_DOC)

        self.assertIn(
            "Proves invalid Save proof artifacts fail closed with explicit diagnostics; "
            "it is not desktop Save completion evidence.",
            normalized,
        )

    def test_pyproject_and_docs_agree_on_neutral_wrapper_module(self) -> None:
        pyproject = read_doc(PYPROJECT_PATH)

        self.assertIn('alice-qa = "alice_qa:main"', pyproject)
        self.assertIn('py-modules = ["alice_qa"]', pyproject)
        self.assertNotIn("amplihack", pyproject.lower())
        self.assertNotIn("alice_qa_amplihack", pyproject)

    def test_documented_wrapper_commands_are_local_and_do_not_automerge(self) -> None:
        combined = "\n".join(read_doc(path) for path in QA_DOCS)

        for command in (
            "python3 alice_qa.py getting-started validate --headless",
            "python3 alice_qa.py alice-qa validate",
            "python3 alice_qa.py alice-qa run alice-desktop-launch",
            "python3 alice_qa.py alice-qa save-negative-contract",
        ):
            with self.subTest(command=command):
                self.assertIn(command, combined)

        self.assertNotIn("gh pr merge", combined)
        self.assertNotIn("merge when green", combined.lower())


if __name__ == "__main__":
    unittest.main()
