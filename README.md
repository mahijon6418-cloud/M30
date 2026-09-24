# OVPN30

OVPN30 is a custom Android build of the GPL `ics-openvpn-xor` project.

- Real OpenVPN engine
- XOR/Scramble support from the upstream fork
- Correct UI launcher (`main` module, UI flavor)
- `.ovpn` import through the OpenVPN UI
- OVPN30 icon
- Signed universal APK from GitHub Actions

The build intentionally targets the `:main:assembleUiRelease` task so the demo/example modules are not packaged as the app.
