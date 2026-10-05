# DirtyFrag-Android-Root/Jailbreak

> **Archived:** Use the [DFRoot](https://github.com/diabl0w/DFRoot)
https://github.com/diabl0w/DFRoot
> instead. It is the active upstream project with newer fixes and improvements.

DirtyFrag Android Root is an Android app and helper
module chain based on DirtyFrag (CVE-2026-43284).
Tested on iQOO Z9 5G.

It is an independent DFRoot fork and device-port. The core DirtyFrag exploit chain is inherited and credited to DFRoot, LSPromise, DFReroot, and DirtyInit. This project adds a manual standalone flow, manager-neutral ksud loading, KMI/module selection, vendor-target fallbacks, diagnostics, and a redesigned Android interface.

[![Views](https://hits.sh/github.com/ankitrawatgit/DirtyFrag-Android-Root-Jailbreak.svg?label=views&color=3A56D4)](https://github.com/ankitrawatgit/DirtyFrag-Android-Root-Jailbreak)


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

For a device whose vendor library layout differs, open **Settings → Advanced
patch #2 targets** and enter one existing `/vendor/` or `/system/vendor/` library path per line. The
app tries the saved paths in order during patch #2. Restore the defaults when
finished testing; changing this list does not change patch #1, build a kernel
module, or prove compatibility.

### Finding a different patch #2 library

The target is only a file carrier for the helper module. Its name can differ
between vendors and Android releases. You can try the following using an ADB shell:

```bash
adb shell 'for d in /vendor/lib64 /vendor/lib /system/vendor/lib64 /system/vendor/lib; do
  [ -d "$d" ] && find "$d" -maxdepth 1 -type f -name "*.so" -size +16k 2>/dev/null
done'
```

For a candidate, inspect its size and SELinux label:

```bash
adb shell 'ls -lZ /vendor/lib64/example.so; stat -c "%s %n" /vendor/lib64/example.so'
```

Choose a regular library large enough for the selected `.ko`, normally with a
`vendor_file`-type label. Avoid `libc`, the linker, and other critical runtime
libraries. Enter the path under **Settings → Advanced patch #2 targets**, one
path per line. A shell may still receive `permission denied`; in that case the
app cannot prove the candidate before rooting, so use the run log and submit
the failing path and log for a device profile.

### Patch #1 portability

Patch #1 is a separate stage. It injects `splicehelper` into
`/apex/com.android.runtime/bin/crash_dump64` and depends on that file's ABI,
page-cache behavior, and SELinux transition. Replacing the filename alone is
not enough. A different firmware needs a tested patch profile or a new
backend; changing the patch #2 target list cannot repair a patch #1 failure.

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
- `libc++: mutex acquired, loading custom module` followed by
  `***FAILED***: check logs`: patch #1, the vendor carrier, and the trigger
  worked, but the kernel rejected `dirtyfrag.ko` or the module did not create
  its result marker. Check the kernel log with `dmesg` or `logcat` for
  `invalid module format`, `module_layout`, `vermagic`, `Unknown symbol`, or
  `CFI/UBSAN` errors. In that case the problem is the `.ko`, not the vendor
  library and not `ksud` yet.
- `Unknown symbol register_kprobe`, `Unknown symbol unregister_kprobe`, or
  `no symbol version for module_layout`: the bundled helper was built for a
  different kernel configuration or symbol table. Build
  `dirtyfrag-lkm/dirtyfrag.ko` against the device's exact kernel source,
  headers, configuration, compiler settings, and architecture. Matching only
  Android/KMI and `5.15` or `6.1` is not sufficient; if the kernel does not
  expose kprobes, the helper needs a different implementation.
- `***FAILED***: ksud exited with error` after a module-load marker: the
  module did initialize, but the staged loader could not find or run a
  compatible arm64 `ksud`. Select the matching KernelSU/ReSukiSU loader or a
  compatible custom file.
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
