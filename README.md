# Runorama Next

Runorama Next is a Minecraft mod for Fabric that continues the work of
the [Runorama mod](https://www.curseforge.com/minecraft/mc-mods/runorama) by Liachmodded. The mod lets you
take panoramic screenshots that replaces the normal start screen background with an image from your own world. It also
allows you to easily create a resource pack with your own custom screenshots.

## Installation

1. Download and install Fabric Loader following the instructions on
   the [Fabric website](https://fabricmc.net/use/installer/).
2. Download the Runorama Next mod JAR file from the [releases page](https://modrinth.com/mod/runorama-next/versions).
3. Move the JAR file to the mods folder in your Minecraft installation directory.
4. Launch Minecraft with the Fabric profile.

## Usage

The H button is bound to take a panoramic screenshot that will replace the normal start screen background with a
randomized image from your collection of panoramic screenshots. The default keybind can be changed in the controls. See
the [commands](#commands) section for more information.

## Commands

The following commands can be used to configure the mod:

- `/runoramanext config setpanoramafov [fov]`: Sets the field of view (FOV) for the panoramic screenshot. FOV must be a
  number between 30 and 110.
- `/runoramanext config rotationspeed [speed]`: Sets the rotation speed for the panoramic screenshot. The default value
  is 1, which is a multiplier of the standard minecraft rotation speed.
- `/runoramanext config poolsize [size]`: Sets the pool size for the panoramic screenshot.
- `/runoramanext config includevanillapanorama [true/false]`: Sets whether to include vanilla Minecraft panorama images
  in the pool of images used for the start screen background.
- `/runoramanext config replacetitlescreen [true/false]`: Sets whether to replace the vanilla Minecraft title screen
  with the modded version.

Additionally, the following command can be used to create a resource pack with your custom panoramic screenshots:

- `/runoramanext createresourcepack [name] [description]`: Creates a resource pack with your custom panoramic
  screenshots. You can provide a name and description for the resource pack.

## Source Code

The source code can be found on [GitHub](https://github.com/WinterWolfSV/runorama_next) under the MPL-2.0
License. More information about the license can be found under the [License](#license) section.

## License

The Runorama Next mod is released under the MPL-2.0 license. This allows you to, modify and redistribute the mod
as long as it adheres to the license terms. The license requires that any modifications to the code are made
publicly available. It also requires that any original or modified code that is distributed must be released under the
same license.

## Bugs and Feedback

Bug reports can be submitted either through
the [GitHub Issues](https://github.com/WinterWolfSV/runorama_next/issues) page of the Runorama Next mod repository or by
contacting me
personally over Discord (WinterWolfSV#9577). Please provide as much information as
possible, including steps to reproduce the issue, error messages, and any relevant screenshots or log files. Your
feedback is greatly appreciated and will help improve the mod for all users.

## Credits

The Runorama Next mod is a continuation of the original Runorama mod, created
by [liachmodded](https://github.com/liachmodded).

Here are some other mod links you may be interested in:

- Runorama equivalent for Forge created by
  suppergerrie2: [Panorama Creator](https://www.curseforge.com/minecraft/mc-mods/panorama-creator)
- Runorama for versions below 1.15 created by
  liachmodded: [Runorama](https://www.curseforge.com/minecraft/mc-mods/runorama)
