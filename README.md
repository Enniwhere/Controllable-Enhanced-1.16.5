![Controllable Banner](https://i.imgur.com/ILkyAfn.png)

[![Download](https://img.shields.io/static/v1?label=&message=Download&color=2d2d2d&labelColor=dddddd&style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAALEQAACxEBf2RfkQAAAAd0SU1FB98BHA41LJJkRpIAAAAYdEVYdFNvZnR3YXJlAHBhaW50Lm5ldCA0LjAuMvvhp8YAAAGGSURBVDhPjZK9SgNBFIVHBEUE8QeMhLAzdzdYBFL7CmnstEjjO4iFjWClFiGJm42VFpYSUptKRfABFBQE7UUI2AmGmPXM5k6cjFE8cJjZ3fPduTOzIiJarvl+bPmuLuWS+K/KmcwUoK4pEGoTnetvYTY7GUpZOCTa0sZi+QRyhQ83pkBShKjDUBvzGKNxr6rU8W4uN8FoX4CKEcB6EMR61OZOEtApEqNIndFvAbiNuAB30QeU+sDYBXSCdw3MrzHvlnx/kdFkC3kU6NkwF3iM0ukFnNM8R0UplZquKHWE85nhV0Kg4o4GrDMwre5x5G9h9aaBHL/VPI849rtqRFfwqALaIcd+F87gYnDy1ha0sY12VcoiR0cLq5/ae3e3gyLbHB2tMAgKQ1fXh+z5a1mpDXQyy8hPIdgyAEMDW8+fWOQJHbfgzXUhxhkXYh/3jcC9C7s2V43c+9DPpHXgeXMIXbqQbV7gAV5hbFixEGMVolUEmvjjXuBeRcoOnp/R/hm81hi0LsQX8OcRBvBjZ8YAAAAASUVORK5CYII=)](https://mrcrayfish.com/mods?id=controllable) ![Minecraft](https://img.shields.io/static/v1?label=&message=1.16%20|%201.15%20|%201.14%20|%201.12&color=2d2d2d&labelColor=dddddd&style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAZdEVYdFNvZnR3YXJlAHBhaW50Lm5ldCA0LjAuMjCGJ1kDAAACoElEQVQ4T22SeU8aURTF/ULGtNRWWVQY9lXABWldIDPIMgVbNgEVtaa0damiqGBdipXaJcY2ofEf4ycbTt97pVAabzK5b27u+Z377kwXgK77QthRy7OfXbeJM+ttqKSXN8sdwbT/A0L7elmsYqrPHZmROLPh5YkV4oEBwaKuHj+yyJptLDoAhbq3O1V1XCVObY3FL24mfn5oRPrcwSCRfQOyNWcjVjZdCbtcdwcgXrXUspdOKbDN/XE9tiBJMhXHT60gUIT2dMhcDLMc3NVKQklz0QIkf5qlyEcO6Qs7yPhMJB4amDMFimQSmqNlE8SKAZFzDfxHfVILIIZ10sJ3OwIbcqSuiOjchkzNCboHev9o2YhgiUP8mxnLN24I6/3ghYdtQG5iUMpFBuCP9iKwLsfiLyeCp2rMnZgwX3NArGoxW1Ridl+BzLEVKa8KSxOqNmDdz0kFnxaLHhWEgAyZigWhHXL+pEDy2ozsDxv8vAzTnh7w5kcghqCaFmCT10of4iPIT2mRdPUh4HoCcVwBH/8Ac2kzUkEV5r3EfVSOvbAJa5NDyI0r2oDtWb1EClh+OoC3Pg7v/Bw7p939yI4rsRW2Y3lKh01eh7WpIRyKZqzyjjYgPdIvlaMWRqYuG7wWryYHsRM0sFolZiPvQ3jheIwSmSBPdkByG/B6Wi3RYiVmRX7GiAPiUCRisii8D+jZNKvPBrHCW1GY0bAz6WkDCtOaSyKQFsi4K5NqNiZtehN2Y5uAShETqolhBqJXpfdPuPsuWwAaRdHSkxdc11mPqkGnyY4pyKbpl1GyJ0Pel7yqBoFcF3zqno5f+d8ohYy9Sx7lzQpxo1eirluCDgt++00p6uxttrG4F/A39sJGZWZMfrcp6O6+5kaVzXJHAOj6DeSs8qw5o8oxAAAAAElFTkSuQmCC) ![Curseforge](http://cf.way2muchnoise.eu/full_controllable_downloads.svg?badge_style=for_the_badge)

# Controllable

> **This is a fork.** This repository is a fork of [MrCrayfish's Controllable](https://github.com/MrCrayfish/Controllable) at version `0.16.5-1.16.5`, maintained on the [`1_16_5-enhancements`](https://github.com/Enniwhere/Controllable-Enhanced-1.16.5/tree/1_16_5-enhancements) branch for one specific purpose: **running two Minecraft 1.16.5 instances side by side (split-screen) on a Raspberry Pi 5 / RetroPie, with one controller assigned to each instance.** All fork-specific changes are described in [Fork: Per-Instance Controller Selection](#fork-per-instance-controller-selection) below. The original upstream documentation follows after that.

I noticed a lack of support for controller for the Java Edition of Minecraft, this is where Controllable comes in. Controllable adds that ability into the game. This mod has been heavily influenced by the controls in the Bedrock Edition of the game, however it is much more configurable (coming soon) and supports more controllers (coming soon)! There is also an API available for mod developers to add controller support to your own mod.

### Features:
* On-screen button indicators (just like Bedrock Edition)
* Easy inventory management
* An easy controller mapping system to add new controllers (Coming soon)
* Many options in config to change to your liking
* A simple API with events for integration into third party mods.

### Supported Controllers:
* Sony PS4 Wireless Controller (via USB)
* Other controllers coming soon!

**Note:** Support for other controllers will be a community effort. Once the controller mapping system is added, I will be accepting pull requests on GitHub for controller mappings. This is simply because I do not have access to different types of controllers.

### Developers:
If you are a developer and want to add Controllable support to your own mod, you can simply do so by adding this to your build.gradle file.

```gradle
repositories {
    maven {
        name = "CurseForge"
        url = "https://minecraft.curseforge.com/api/maven/"
    }
}

dependencies {
    compile 'controllable:Controllable:1.12.1:0.2.1'
}

minecraft {
    useDepAts = true
}
```

You will then need to run gradlew setupDecompWorkspace again as Controllable uses an access transformer. Once completed, you can start implementing controller support to your mod. The available events you can use are:

* **ControllerEvent.Move** - This event is fired when the player is moved when using a controller. This can be cancelled. An example can be found in MrCrayfish's Vehicle Mod
* **ControllerEvent.Turn** - This event is fired when the player turns it's view with a controller. This can be cancelled. 
* **ControllerEvent.ButtonInput** - This event is fired when a button is either pressed down initially and when it's released. This event can be cancelled and is useful for overriding default behavior. An example can be found in MrCrayfish's Vehicle Mod
* **AvailableActionsEvent** - This event allows you to control the button actions that are rendered to the screen. This allows you to remove or add your own actions. This event can not be cancelled. An example can be found in MrCrayfish's Vehicle Mod
* **RenderAvailableActionsEvent** - This event is fired every time the available actions are rendered. This event can be cancelled.
* **RenderPlayerPreviewEvent** - The event is fired every time the player preview in the top left corner is rendered. In case this is drawing over your GUI elements, this event can be cancelled, which stops it from renderering. An example can be found in MrCrayfish's Vehicle Mod

It's best practice that when you override any of the default controls that they should be based on a certain condition. For instance, in MrCrayfish's Vehicle Mod, controls are only overridden when riding a vehicle. It does not affect normal gameplay in any way.

---

# Fork: Per-Instance Controller Selection

## Purpose

This fork exists to run **two Minecraft Java 1.16.5 instances simultaneously** on a Raspberry Pi 5 / RetroPie setup (split-screen), with **one controller assigned to each instance**. It is based on Controllable `0.16.5-1.16.5` and all changes live on the `1_16_5-enhancements` branch.

- Repository: [Enniwhere/Controllable-Enhanced-1.16.5](https://github.com/Enniwhere/Controllable-Enhanced-1.16.5)
- Branch: [`1_16_5-enhancements`](https://github.com/Enniwhere/Controllable-Enhanced-1.16.5/tree/1_16_5-enhancements)

## The problem with upstream Controllable

Upstream Controllable `0.16.5-1.16.5` automatically selected GLFW joystick ID `0` at startup:

```java
new Controller(GLFW.GLFW_JOYSTICK_1)   // GLFW_JOYSTICK_1 == 0
```

Both Minecraft processes therefore grabbed the **same physical controller**. Additionally, Controllable only persisted the controller name/mapping — not the physical joystick ID — so with two identical controllers (e.g. two `PS4 Controller` devices), the config file could not distinguish them either.

## The solution

The fork adds a JVM system property, `controllable.controller`, to select the preferred controller per Minecraft instance.

```text
-Dcontrollable.controller=<value>
```

`<value>` is a **controller name** (case-insensitive, partial match), optionally followed by `#n` to pick the n-th matching controller when two identical models are connected:

| Form | Example | Meaning |
|---|---|---|
| Controller name | `-Dcontrollable.controller=PS4` | First gamepad whose name contains `PS4` (case-insensitive, partial match) |
| Name + occurrence | `-Dcontrollable.controller=PS4#2` | Second gamepad matching `PS4` — for two identical controllers |

Name-based matching is used instead of GLFW joystick IDs because IDs are *process-local* and their assignment depends on device enumeration order, which makes them unreliable across multiple game instances (and the numeric form did not work reliably in practice).

### Example split-screen setup

| Instance | JVM argument | Controller |
|---|---|---|
| `1.16.5` | `-Dcontrollable.controller=PS4#1` | First matching controller |
| `1.16.5-P2` | `-Dcontrollable.controller=PS4#2` | Second matching controller |

### Behavior details

- **Startup selection is deferred and retried.** The selection runs on the client tick loop (not during early mod setup), because GLFW joystick state is only refreshed by the event pump and Linux may enumerate devices as gamepads a few ticks after startup. Selection retries each tick until it succeeds.
- **Explicit requests are exclusive.** When `controllable.controller` is set, the mod *never* falls back to an arbitrary controller. Auto-select fallbacks (first connected controller on connection/disconnect) only run when no property is set. This prevents one instance from stealing the other's controller on hotplug or disconnect.
- **Diagnostics.** While waiting for the requested controller, the mod logs the full GLFW joystick table every 5 seconds to `logs/latest.log` (search for `Still waiting for requested controller`).

## Building the fork

Requires **Java 8** for this ForgeGradle / Minecraft 1.16.5 setup.

```bash
# Use a full JDK 8 (e.g. ~/java/jdk8u504-b01), not only a bundled JRE
export JAVA_HOME="$HOME/java/jdk8u504-b01"
export PATH="$JAVA_HOME/bin:$PATH"
java -version && javac -version   # verify 1.8

git fetch
git reset --hard origin/1_16_5-enhancements

chmod +x gradlew
./gradlew clean --no-daemon --console=plain
./gradlew build --no-daemon --console=plain \
    -Dhttp.socketTimeout=300000 -Dhttp.connectionTimeout=120000 \
    -Dhttps.protocols=TLSv1.2
```

> **Important:** run `clean` and `build` as **separate commands**. Running them together is a known Gradle issue in this setup and breaks compilation. Expected output: `BUILD SUCCESSFUL`.

The built jar keeps the upstream filename/version (`controllable-0.16.5-1.16.5.jar`) so Forge treats it as a drop-in replacement.

## Installation

The built jar can be dropped into the `mods` folder of any launcher that supports multiple instances (e.g. [Prism Launcher](https://prismlauncher.org/) or Freesm Launcher). Each instance has its own Minecraft directory and `mods` folder. Back up the original Controllable JAR before replacing it, then add the per-instance JVM argument as shown above.

## Related setup

- Launcher: any multi-instance launcher (e.g. Prism Launcher or FreeSM Launcher)
- Minecraft: Java Edition 1.16.5, Forge
- Two Minecraft instances: `1.16.5-P1` and `1.16.5-P2`
- Display/window management: XINIT, Openbox, `xdotool`, `wmctrl`
- Launching: RetroPie Runcommand + split-screen shell scripts — see [Enniwhere/retropie-scripts](https://github.com/Enniwhere/retropie-scripts)

## Maintenance checklist

1. Keep the fork based on the 1.16.5 Controllable source.
2. Build with a full Java 8 JDK, not only the bundled Java 8 runtime.
3. Preserve the mod ID and version (`controllable-0.16.5-1.16.5.jar`) unless intentionally changing compatibility.
4. Keep the per-instance JVM arguments distinct (e.g. `PS4#1` / `PS4#2`).
5. Connect both controllers **before** launching the instances.
6. If assignments change, check the diagnostic log line to confirm which physical controller matches which name/occurrence.
