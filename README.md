# lvl-timelimit

A Paper/Spigot plugin that limits each player's playtime in minutes.

## Features

- Stores each player's remaining time in `config.yml`
- Decreases online players' time every 1 minute
- Automatically kicks players when their remaining time reaches `0` or below
- Admin commands for `set`, `add`, and `remove` (single player or all players)
- Player command to check remaining time (`/timelimit left`)

## Requirements

- Java 21
- Paper/Spigot API 1.21.x
- Maven (for build)

## Build

```bash
mvn clean package
```

The output JAR is generated in the `target/` directory.

## Installation

1. Put the built JAR file in your server `plugins/` folder.
2. Restart the server.
3. The file `plugins/lvl-timelimit/config.yml` will be generated.

## Configuration (`config.yml`)

```yml
default-time: 5
players: {}
```

- `default-time`: Initial time for new players (in minutes)
- `players`: Per-player remaining time storage

## Commands

- `/timelimit help`
- `/timelimit left`
- `/timelimit set <player|all> <min>`
- `/timelimit add <player|all> <min>`
- `/timelimit remove <player|all> <min>`

## Permission

- `lvl.timelimit.admin` (default: OP)

## Recommended GitHub Files

To keep the repository maintainable, include:

- A complete `README.md` (setup, configuration, commands)
- `CHANGELOG.md` for version history
- `CONTRIBUTING.md` for contributor guidance
- Issue and pull request templates under `.github/`
- Versioned GitHub Releases (instead of only `SNAPSHOT` builds)

## License

This project is licensed under the terms of the LICENSE file.
