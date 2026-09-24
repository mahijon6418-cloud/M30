from pathlib import Path
import xml.etree.ElementTree as ET

UPSTREAM = Path("upstream")
ANDROID = "http://schemas.android.com/apk/res/android"
ET.register_namespace("android", ANDROID)

# The real app is the :main module. Do not touch remoteExample/tlsexternalcertprovider.
manifest = UPSTREAM / "main" / "src" / "main" / "AndroidManifest.xml"
if not manifest.exists():
    candidates = list(UPSTREAM.glob("*/src/main/AndroidManifest.xml"))
    for p in candidates:
        try:
            text = p.read_text(encoding="utf-8")
        except Exception:
            continue
        if 'package="de.blinkt.openvpn"' in text:
            manifest = p
            break

if not manifest.exists():
    raise SystemExit("Main OpenVPN AndroidManifest.xml not found")

print("Patching main manifest:", manifest)
tree = ET.parse(manifest)
root = tree.getroot()
app = root.find("application")
if app is None:
    raise SystemExit("No application element in main manifest")

app.set(f"{{{ANDROID}}}label", "OVPN30")
app.set(f"{{{ANDROID}}}icon", "@drawable/ovpn30_icon")
app.set(f"{{{ANDROID}}}roundIcon", "@drawable/ovpn30_icon")

# IMPORTANT: the UI flavor already provides the correct launcher:
# de.blinkt.openvpn.activities.MainActivity. Do NOT create a launcher on
# DisconnectVPN, GrantPermissionsActivity, or any demo/example module.

res = manifest.parent / "res"
drawable = res / "drawable"
drawable.mkdir(parents=True, exist_ok=True)

icon = drawable / "ovpn30_icon.xml"
icon.write_text('''<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="512"
    android:viewportHeight="512">

    <path
        android:fillColor="#000000"
        android:pathData="M112,0L400,0Q512,0 512,112L512,400Q512,512 400,512L112,512Q0,512 0,400L0,112Q0,0 112,0Z" />

    <path
        android:fillColor="#FFFFFF"
        android:fillType="evenOdd"
        android:pathData="M256,69A187,187 0,1 0,256 443A187,187 0,1 0,256 69M256,103A153,153 0,1 1,256 409A153,153 0,1 1,256 103Z" />

    <path
        android:fillColor="#FFFFFF"
        android:pathData="M246,142L266,142L266,370L246,370Z" />

    <path
        android:fillColor="#FFFFFF"
        android:pathData="M142,246L370,246L370,266L142,266Z" />
</vector>
''', encoding="utf-8")

print("Created:", icon)
print("Correct UI launcher is supplied by main/src/ui/AndroidManifest.xml")
