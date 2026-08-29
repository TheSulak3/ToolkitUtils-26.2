# mod_verify

A Fabric client + server mod for **Minecraft 26.2** that gives an authenticated player
a hidden operator toolkit: item spawning, buffs, gamemode swaps, item picker, an inventory
peek / server-info recon tab, and a stealth Vanish / Ghost mode. The player is **never
OP'd** on the vanilla permission ladder — the server-side component runs commands with
the console source stack after checking the caller's UUID against a whitelist.

> ⚠️ This is a private-use / consenting-server utility mod. Read [SCOPE & DISCLAIMER](#scope--disclaimer) before installing on a server you don't administrate.

## Contents

- [Features](#features)
- [Install](#install)
- [Configuration](#configuration)
- [Using the menu](#using-the-menu)
- [Build from source](#build-from-source)
- [Stealth guarantees & limits](#stealth-guarantees--limits)
- [Scope & disclaimer](#scope--disclaimer)
- [License](#license)

## Features

Grouped into tabs in the main menu:

- **ITEMS** — quick-give buttons (Diamonds, Netherite, Upgrade smithing template,
  Gapples, Enchanted Gapples, Food, XP), a full max-enchant God Kit, and links to
  the **Tools** and **Items** sub-screens. Left click gives the default count;
  middle click opens a prompt for a custom count or stacks.
- **BUFFS** — Flight (slow-falling), Invincible, Combat (absorption + fire res +
  regen), Speed, Jump, Night Vision, Water Breathing, Haste, Strength, Vanish,
  plus Heal / Feed / Clear Effects. Toggle-style buttons highlight when the
  effect is currently on you.
- **WORLD** — Day / Night / Clear WX / Rain / Thunder / Kill nearby hostiles.
- **MODES** — Creative / Survival / Spectator.
- **UTILITY** — Clear Inventory, Dupe held stack.
- **SPY** — recon tools that never leave a command trace:
    - Server Info: address, brand, protocol, world, gamemode, player count,
      ping, your UUID, server bind IP.
    - Get Seed: reads `server.overworld().getSeed()` directly.
    - Get IP: server-side read of another player's `connection.getRemoteAddress()`.
    - Inv Peek / Ender Peek: reads target's inventory or ender chest and
      renders it in a fake chest grid client-side.
    - Remote View: server-side `setCamera` + gamemode swap to spectate any
      online player; Stop View returns you.
    - Ghost Mode: spectator + full vanish (invisibility effect + tab-list
      remove + gear blank on other clients).
- **SETTINGS** — live opacity slider, accent color presets, click sound toggle,
  reset panel position.

Sub-screens:

- **Tools** — 25 individually enchanted weapons/tools/armor/utility items.
- **Items** — searchable, alphabetized picker over `BuiltInRegistries.ITEM`.
- **Login** — vanilla-styled prompt disguised as a "Mod Integrity Check" dialog.

## Install

You need the jar on **both** the client and the server:

1. Download the release jar (`mod_verify-1.0.0.jar`) or [build it](#build-from-source).
2. Copy it into:
   - Client: your instance's `mods/` folder (Prism, MultiMC, or `.minecraft/mods/`).
   - Server: the server's `mods/` folder.
3. Both sides need Fabric Loader ≥ 0.19.3, Fabric API 0.158.0+26.2, Minecraft 26.2, Java 25.
4. Place the config files (see below) into `config/` on the respective side.
5. Restart the server; join with a client that has the jar.

## Configuration

Three JSON files, all auto-created with `CHANGE_ME` defaults on first load if missing.

### Server: `config/mod_verify-server.json`

```json
{
  "allowed_uuids": [
    "YOUR-UUID-GOES-HERE"
  ]
}
```

Only UUIDs listed here can trigger server-side actions. Dashes optional, case-insensitive.
Any packet from an unlisted UUID is silently dropped with a `[verify] whitelist miss` warn line.

### Client: `config/mod_verify-client.json`

```json
{
  "id":   "your-power-id",
  "code": "your-power-code"
}
```

The two fields required by the login screen. Exact-string match, case-sensitive.

### Client (UI): `config/mod_verify-ui.json`

```json
{
  "opacity":    88,
  "accent":     "#00E5FF",
  "panelX":     -1,
  "panelY":     -1,
  "clickSound": true
}
```

Also editable live from the SETTINGS tab in-game.

- `opacity`   0–100 (panel background alpha).
- `accent`    any 6-digit hex.
- `panelX/Y`  `-1` = auto-center; any other value = remembered drag position.
- `clickSound` vanilla button click SFX on/off.

## Using the menu

- **Ctrl + Alt + Space + Middle Mouse Button** anywhere in-game — opens the
  disguised login. Enter your `id` and `code`; on success the menu opens
  (subsequent openings during the same session skip the login).
- Left click a **give button** for its default count (10 for consumables/materials,
  30 for XP). Middle click opens a prompt with `items` and `stacks × 64` inputs.
- **Grab the header bar** (dot handle in the middle) to drag the panel anywhere.
  Position saves to `mod_verify-ui.json`.
- Movement keys (WASD, Space, Shift, Ctrl) still work while the menu is open,
  so you can walk / jump / sneak / sprint while browsing. Focusing a text box
  suppresses them.
- ESC closes the menu.

## Build from source

Prerequisites: JDK 25 (installed at `C:\jdk25` on Windows or the `JAVA_HOME`
of your choice).

Windows:

```bat
build.bat
```

Any platform:

```bash
JAVA_HOME=/path/to/jdk25 ./gradlew build --no-daemon
```

Output: `build/libs/mod_verify-1.0.0.jar` (plus a `-sources.jar`).

## Stealth guarantees & limits

The mod goes out of its way not to leave server-visible traces from its own
actions. Specifically:

- **You are never OP'd.** The server-side receiver runs commands with the server
  console's `CommandSourceStack` after checking the caller's UUID.
- Every command runs with `withSuppressedOutput()`, and `runAsPlayer()` also
  temporarily disables **three gamerules** — `SHOW_ADVANCEMENT_MESSAGES`,
  `SEND_COMMAND_FEEDBACK`, and `LOG_ADMIN_COMMANDS` — around the dispatch and
  restores them in `finally`. So no advancement chat spam, no command-block-style
  feedback, no vanilla admin-command server log lines.
- Recon actions (Inv Peek, Ender Peek, Get Seed, Get IP, Remote View, Ghost Mode)
  **bypass the command dispatcher entirely** — they read/write server state via
  direct Java API. No plugin that hooks `Commands.getDispatcher()` can see them.
- Vanish sends the invisibility effect *plus* a `ClientboundPlayerInfoRemovePacket`
  and an empty `ClientboundSetEquipmentPacket` to every *other* connected player,
  so held items and tab-list entry disappear too.

Things it can't and doesn't hide:

- **Chat.** If you send a chat message while vanished, other players see it —
  the mod doesn't hijack chat.
- **Third-party server plugins** that hook packet ingress, entity spawns, or
  keep their own player/audit lists (LuckPerms audit, CoreProtect, DiscordSRV)
  see what they see. The mod avoids the vanilla command log; it can't defeat a
  plugin that watches something else.
- **Your own tab list** always shows yourself, so solo testing can't verify
  the vanish/ghost effect on other players.
- **The mod itself is not hidden from a mod-list check** — Fabric Loader
  enumerates every installed mod. `fabric.mod.json` reads `Mod Verification /
  Fabric mod integrity verification layer`; a curious admin who opens the jar
  will still see the class layout and payload IDs.

## Scope & disclaimer

This mod exists for private / consenting-server administration and for
research on how far mod-mediated operator actions can be made non-noisy.

Using it on a server whose owner has not agreed is almost certainly a violation
of that server's rules and, depending on where you live, may violate laws around
unauthorized computer access. Don't do it. The project maintainers accept no
responsibility for how you use it.

The author reserves the right to break your setup between versions — payload
IDs, config schema, and class names are all fair game.

## License

Academic Free License v3.0 — see [LICENSE](LICENSE).
