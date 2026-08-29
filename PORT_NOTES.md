# Port notes

Renamed from the original `SecretOPPowers-26.2` build: mod id, package (`net.mcreator.toolkitutils`),
class names, item/block names (`safe`->`toolbox`, `invader`->`widget`), and all resource/asset
paths now use `toolkit_utils`.

Login is no longer hardcoded in source. `ToolkitConfig` reads/writes
`config/toolkit_utils-client.json` (Fabric config dir). On first run it writes a placeholder
`{"id":"CHANGE_ME","code":"CHANGE_ME","trigger":"CHANGE_ME"}` and saves it to that file —
nothing is generated or printed to chat; edit the file directly to set your real credentials
and chat trigger phrase.

The old MCreator menu/container layer remains simplified to direct `Screen` classes. Login
success opens the command screen directly (no intermediate decoy screen or hidden button).
The secondary page is retained as a navigation page; disruptive controls are absent.

Three ways to reach it, all wired in `ClientInit`:
- Right-click a placed Toolbox block (`UseBlockCallback`) - opens login, or the command screen
  directly if already authenticated this session.
- Type the configured `trigger` phrase in chat - caught by `ClientSendMessageEvents.ALLOW_CHAT`
  and cancelled before it sends, so it never reaches the server or the local chat/log.
- Hold Ctrl+Alt and click the middle mouse button - polled via raw GLFW state each client tick,
  not a registered `KeyMapping`, so it doesn't appear in Options > Controls. Always opens
  `LoginScreen`, which now renders the "ERROR: PAGE COULD NOT LOAD" text over its own login
  fields instead of hopping through a separate decoy screen.
