import re
import unittest
from functools import lru_cache
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
WHITESPACE_RE = re.compile(r"\s+")
STALE_BRANCH_EXAMPLE_RE = re.compile(r"uvx --from git\+[^ \n]+@<branch>(?:\s|$)")

# Point-in-time docs under docs/howto/ and docs/reference/ were removed in #828.
# Only the qa/ README survives as a permanent CLI doc.
CLI_DOCS = [
    REPO_ROOT / "qa" / "outside-in" / "alice-desktop" / "README.md",
]


@lru_cache(maxsize=None)
def read_doc(path: Path) -> str:
    return path.read_text(encoding="utf-8")


@lru_cache(maxsize=None)
def normalized_doc(path: Path) -> str:
    return WHITESPACE_RE.sub(" ", read_doc(path))


def stale_branch_examples(relative_path: Path, text: str) -> list[str]:
    if "@<branch>" not in text:
        return []

    return [
        f"{relative_path}:{line_number}"
        for line_number, line in enumerate(text.splitlines(), start=1)
        if STALE_BRANCH_EXAMPLE_RE.search(line)
    ]


class AliceQaAmplihackDocsContractTest(unittest.TestCase):
    def test_branch_installable_examples_accept_branch_or_commit_tokens(self) -> None:
        stale_examples = []

        for path in CLI_DOCS:
            text = read_doc(path)
            relative_path = path.relative_to(REPO_ROOT)
            with self.subTest(path=relative_path):
                if "amplihack alice-qa" in text:
                    self.assertIn("<branch-or-commit>", text)

            stale_examples.extend(stale_branch_examples(relative_path, text))

        self.assertEqual(
            [],
            stale_examples,
            "Branch-installable Amplihack QA examples should name <branch-or-commit>, not <branch>.",
        )

    # Tests for negative-contract, shared-qa, positive-save-proof, and
    # silver-thread-claim docs were removed because those docs were deleted
    # as point-in-time artifacts in #828.


if __name__ == "__main__":
    unittest.main()
