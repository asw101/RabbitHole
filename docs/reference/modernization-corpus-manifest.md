# Modernization corpus manifest

The modernization corpus manifest is a small, checked-in text index for representative Alice archive shapes. It documents generated fixture expectations without committing `.a3p`, `.a3w`, or `.a3c` payloads.

The authoritative machine-readable manifest is [`modernization-corpus-manifest.json`](./modernization-corpus-manifest.json). Its scope is representative evidence only; it is not full historical archive coverage.

## Review contract

- Manifest entries point under `generated-fixtures/` and never to checked-in binary payloads.
- Each entry names the generated fixture expectations that tests or QA runs must satisfy.
- The scorecard treats the manifest as corpus evidence that can be inspected without Git LFS.
