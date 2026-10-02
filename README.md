# DirtyFrag-Android-Root/Jailbreak

DirtyFrag Android Root is an Android app and helper
module chain based on DirtyFrag (CVE-2026-43284).
Tested on iQOO Z9 5G.

It is an independent DFRoot fork and device-port. The core DirtyFrag exploit chain is inherited and credited to DFRoot, LSPromise, DFReroot, and DirtyInit. This project adds a manual standalone flow, manager-neutral ksud loading, KMI/module selection, vendor-target fallbacks, diagnostics, and a redesigned Android interface.

[![Views](https://hits.sh/github.com/ankitrawatgit/DirtyFrag-Android-Root-Jailbreak.svg?label=views&color=3A56D4)](https://github.com/ankitrawatgit/DirtyFrag-Android-Root-Jailbreak)

[![Buy Me a Coffee](https://www.buymeacoffee.com/assets/img/custom_images/yellow_img.png)](https://coff.ee/ankitrawatbmac)

Built with the help of [Codex](https://openai.com/codex/).

## Device support

The bundled helper modules cover **8 Android KMI/kernel-version combinations**
(`android12-5.10`, `android13-5.10`, `android13-5.15`, `android14-5.15`,
`android14-6.1`, `android15-6.6`, `android16-6.12`, and `android17-6.18`). That
is a list of module build targets, **not eight confirmed devices**. A matching
KMI is only an initial compatibility check. Kernel configuration, exported
symbols and symbol versions, SELinux behavior, and the device paths used by
the exploit also matter.

## Use

Download the latest APK from the repository's [Releases](https://github.com/ankitrawatgit/DirtyFrag-Android-Root-Jailbreak/releases) page.

### Root manager downloads

Install a compatible manager before launching the app. The automatic loader
checks the installed manager's `ksud` in this order: KernelSU, ReSukiSU, then
the system `ksud` path.

- [KernelSU Manager releases](https://github.com/tiann/KernelSU/releases)
- [ReSukiSU Manager releases](https://github.com/ReSukiSU/ReSukiSU/releases)

1. Install the APK and open **DirtyFrag Android Root**.
2. Review the device profile and detected KMI in Settings.
3. Leave automatic `ksud` selection on for KernelSU or ReSukiSU, or choose a
   compatible custom loader.
4. Tap **Launch root** and review the run log.

## If a device is not supported

Use the run log to locate the failing stage:

- `patch #1 failed` or `patch #1 verify FAILED`: the `crash_dump64` page was
  not modified or cannot be executed on that firmware. Check its SELinux label,
  APEX/verity state, and page-cache behavior. Vendor-library fallback paths do
  not fix this stage; the carrier or patch method needs a firmware-specific
  port.
- `read_vendor ... exit 1/3`: the helper ran but could not open the selected
  vendor file or its pipe was sanitized. Check `ls -lZ` for the candidate
  libraries and choose an existing `vendor_file` carrier.
- `read_vendor ... signal 6` for every candidate: collect logcat and the
  crash/tombstone entry for `crash_dump64`. This usually means the helper was
  not the code that executed, or the firmware's crash handler aborted before
  vendor access.
- Patch #2 succeeds but `ksud` fails: use a compatible arm64 `ksud` from the
  installed KernelSU/ReSukiSU manager, or select a custom loader in Settings.

## Exploit outline

The exploit uses crafted ESP traffic and a page-cache write primitive to place
helper code into mapped system files, trigger a privileged hook, and stage the
DirtyFrag helper module. The helper invokes the staged `ksud` loader. The app
restores its temporary library patches during cleanup.

This project builds on work from:

- DirtyFrag PoC and related code: <https://github.com/lsposed/lspromise>
- SELinux helper module work: <https://github.com/polygraphene/DFReroot>
- Unprivileged XFRM socket method: <https://github.com/combeng6th/DirtyInit>
