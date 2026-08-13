# Separate XCFramework Publication to `publish-spm.yml`

This plan outlines the steps to separate the SPM (Swift Package Manager) / XCFramework publication logic into its own GitHub Actions workflow.

## User Review Required

> [!IMPORTANT]
> The current `publish.yml` force-updates the tag after updating `Package.swift`. By splitting the workflows, both will trigger on the tag. `publish-spm.yml` will still force-update the tag, which shouldn't affect the parallel `publish.yml` (Maven Central) execution as it works on a local checkout, but it's worth noting that the tag in the remote repository will be moved.

## Proposed Changes

### GitHub Actions Workflows

#### [NEW] [publish-spm.yml](file:///home/shad/KMP/Whisper/.github/workflows/publish-spm.yml)
Create a new workflow that handles:
- Building the XCFramework.
- Zipping the XCFramework.
- Computing the checksum and updating `Package.swift`.
- Committing and pushing the updated `Package.swift` to `main`.
- Force-updating the tag to point to the new commit.
- Uploading the zipped XCFramework to the GitHub Release.

#### [MODIFY] [publish.yml](file:///home/shad/KMP/Whisper/.github/workflows/publish.yml)
Remove all SPM-related steps, leaving only the Maven Central publication logic.

## Verification Plan

### Manual Verification
- Verify that both `.github/workflows/publish.yml` and `.github/workflows/publish-spm.yml` are correctly defined.
- Once merged, triggering a new tag should start both workflows.
- `publish.yml` should handle Maven Central.
- `publish-spm.yml` should handle XCFramework build and SPM manifest update.
