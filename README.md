> **厌倦了 squaremap 上所有 Towny 领地都是同一种颜色、根本分不清谁是谁？**
> 试试 **SquareTowny** —— 每个城镇都有专属颜色，同一国家的城镇同色，领土范围一眼就能看清。
>
> **Tired of every Towny claim looking the same on squaremap?**
> Try **SquareTowny** — each town gets its own color, towns in the same nation share one, and territory becomes clear at a glance.

# SquareTowny

> **English** · [**简体中文**](#简体中文)

SquareTowny is a [Spigot](https://www.spigotmc.org/) plugin that works as an add-on to [Towny](https://github.com/TownyAdvanced/Towny). It renders the claim areas of every town onto a web map, so players can see the territory of each town (and nation) directly on the map.

This repository is a **squaremap-oriented customized fork** of [TownyAdvanced/MapTowny](https://github.com/TownyAdvanced/MapTowny) (originally by Silverwolfg11, MIT licensed) with the following changes:

- **Automatic distinct colors** *(new feature)*: every town is rendered with its own color, and towns in the same nation share the same color.
- **Removed** the `maptowny-pl3xmap-v2` and `maptowny-pl3xmap-v3` modules, because their build dependencies are no longer published on Modrinth's Maven repository and can no longer be resolved.
- The remaining supported web maps are: **squaremap, dynmap, BlueMap and Pl3xMap v1** (squaremap is the primary target).

## Features

- Each town gets a **visually distinct color** (auto-generated). Towns belonging to the same **nation share one color**, so nation borders are easy to spot.
- Auto colors take priority over the map colors stored in Towny. Towny assigns a random map color to every new town/nation (those values are indistinguishable from manually chosen colors), so they would otherwise override the auto colors. Set `fill-style.use-auto-colors: false` to use the colors configured in Towny instead.
- Configurable marker options: fill/stroke colors, opacity, stroke width, icons, etc.
- Fully async processing; the server thread is never blocked.
- Multiple web-map support: squaremap, dynmap, BlueMap (limited), Pl3xMap v1.
- Unit-tested polygon outline (contour) and negative-space (hole-finding) algorithms.

## Supported Web Maps

| Map | Version | Support |
| --- | --- | --- |
| [squaremap](https://github.com/jpenilla/squaremap) | current | Full (primary) |
| [dynmap](https://www.spigotmc.org/resources/dynmap%C2%AE.274/) | current | Full |
| [BlueMap](https://www.spigotmc.org/resources/bluemap.83557/) | v3.3+ | Limited |
| [Pl3xMap](https://github.com/pl3xgaming/Pl3xMap) v1 | v1 (use this [fork](https://github.com/NeumimTo/Pl3xMap) for 1.18+) | Full |

## Dependencies

- [Towny](https://github.com/TownyAdvanced/Towny) 0.97.1 or later (**required**)
- One of the web-map plugins listed above (**required**)

## Installation

1. Put the plugin jar (`maptowny-plugin/target/SquareTowny-1.1.jar`) into your server's `plugins` folder.
2. Restart the server. A `SquareTowny/config.yml` file will be generated.
3. Set the `enabled-worlds` option to the worlds where you want town claims to show up.

## Usage & Configuration

The plugin works out of the box. The most common adjustment is `enabled-worlds`.

### Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/maptowny reload` | Reload the config and re-render all towns | `maptowny.reload` |
| `/maptowny render <town>` | Render a single town | `maptowny.render` |
| `/maptowny unrender <town or uuid>` | Remove a town from the map | `maptowny.unrender` |

All commands require `maptowny.use` (default: op). `maptowny.admin` grants all of the above.

### Config options (config.yml)

| Option | Default | Description |
| --- | --- | --- |
| `enabled-worlds` | `["world"]` | Worlds where town claims are shown |
| `update-period` | `5` | Interval (minutes) at which all towns are re-rendered |
| `layer.*` | — | Layer name, visibility controls, priority, z-index |
| `fill-style.fill-color` | `"#3388ff"` | Default fill color |
| `fill-style.fill-opacity` | `0.2` | Fill opacity |
| `fill-style.use-nation-color-fill` | `true` | Use the nation's color to fill claims (only when `use-auto-colors` is `false`) |
| `fill-style.stroke-color` | `"#3388ff"` | Default stroke color |
| `fill-style.use-town-color-fill` | `false` | Use the town's own color, taking priority over the nation color (only when `use-auto-colors` is `false`) |
| `fill-style.use-auto-colors` | `true` | **Auto colors (this fork)**: give every town/nation a distinct color (takes priority over the map colors stored in Towny) |
| `icon-info.*` | — | Town / capital / outpost icon images and sizes |
| `icon-info.capital-icon-size` | `24` | Capital icon size (smaller than the town icon by default, so it doesn't block the map when zoomed out) |

### How auto colors work

- A town **without a nation** gets a unique color derived from its UUID (stable across restarts).
- All towns **in the same nation** share the color of the nation's **capital town**. A town therefore keeps its color when it creates a nation, and towns that join later adopt that color.
- The nation color follows the capital; if the capital is transferred to another town, the nation's color changes with it.
- **Auto colors are enabled by default and take priority over the map colors stored in Towny.** Towny assigns a random map color to every town and nation when it is created (`global_town_settings.allowed_map_colors` / `global_nation_settings.allowed_map_colors`), and those colors cannot be told apart from colors set manually. Without this rule, a town would lose its auto color as soon as it creates or joins a nation and would fall back to the default marker color.
- To use the map colors set in Towny (`/town set mapcolor`, `/nation set mapcolor`) together with the `use-nation-color-*` / `use-town-color-*` options, set `fill-style.use-auto-colors: false`.
- Colors update automatically on the next periodic re-render (or with `/maptowny reload`).

## Plugin API

SquareTowny exposes a small API (`maptowny-api` module) to let other plugins integrate. See the original project's [wiki](https://github.com/TownyAdvanced/MapTowny/wiki/MapTowny-API) for details.

## Building

This is a standard Maven multi-module project that uses the [maven-toolchains-plugin](https://maven.apache.org/plugins/maven-toolchains-plugin/). The modules are compiled for different JDK versions:

| Module | JDK target |
| --- | --- |
| `maptowny-api`, `maptowny-plugin`, `maptowny-dynmap` | 8 |
| `maptowny-bluemap` | 11 |
| `maptowny-squaremap`, `maptowny-pl3xmap-v1` | 16 |

Two ways to build:

1. **Standard toolchains** — configure a `~/.m2/toolchains.xml` with JDK 8, 11 and 16 (see [the toolchains guide](https://maven.apache.org/guides/mini/guide-using-toolchains.html)), then run `mvn clean package`.
2. **Single modern JDK (included)** — this repository ships a `toolchains-local.xml` that maps every required version to one JDK (21 or newer). Edit the `jdkHome` path to point at your JDK, then run:

   ```
   mvn -t toolchains-local.xml clean package
   ```

The finished plugin jar is `maptowny-plugin/target/SquareTowny-1.1.jar`.

## License

MIT. This fork retains the original MIT license and copyright of the upstream project. Pull requests are welcome.

---

# 简体中文

> 厌倦了 squaremap 上所有 Towny 领地都是同一种颜色、根本分不清谁是谁？
> 试试 **SquareTowny** —— 每个城镇都有专属颜色，同一国家的城镇同色，领土范围一眼就能看清。

## 项目简介

SquareTowny 是一个基于 [Spigot](https://www.spigotmc.org/) 的插件，作为 [Towny](https://github.com/TownyAdvanced/Towny) 的扩展，把每个城镇的领地范围渲染到网页地图上，玩家可以直接在地图上看到各个城镇（以及国家）的领土。

本仓库是 [TownyAdvanced/MapTowny](https://github.com/TownyAdvanced/MapTowny)（原作者 Silverwolfg11，MIT 协议）的**面向 squaremap 的定制分支**，主要改动如下：

- **自动配色（新增功能）**：每个城镇在地图上显示不同的颜色，同一国家的城镇颜色相同。
- **移除**了 `maptowny-pl3xmap-v2` 和 `maptowny-pl3xmap-v3` 模块——这两个模块的构建依赖已从 Modrinth 的 Maven 仓库下架，无法再解析。
- 目前支持的地图插件为：**squaremap、dynmap、BlueMap 和 Pl3xMap v1**（squaremap 为主要目标）。

## 功能特性

- 每个城镇自动生成**独一无二的颜色**；同一**国家**内的城镇共用同一种颜色，国家边界一目了然。
- 自动配色优先于 Towny 中保存的地图颜色。Towny 会给每个新建城镇/国家随机分配一个地图颜色，这些值与玩家手动设置的颜色无法区分，因此若不优先自动配色，城镇加入国家后就会退回默认颜色。如需使用 Towny 中设置的颜色，请设置 `fill-style.use-auto-colors: false`。
- 可配置的标记样式：填充/描边颜色、透明度、描边粗细、图标等。
- 全异步处理，不阻塞主线程。
- 同时支持多种网页地图：squaremap、dynmap、BlueMap（支持有限）、Pl3xMap v1。
- 附带单元测试的多边形轮廓算法与负空间（"找洞"）算法。

## 支持的地图插件

| 地图 | 版本 | 支持程度 |
| --- | --- | --- |
| [squaremap](https://github.com/jpenilla/squaremap) | 最新 | 完整（主要目标） |
| [dynmap](https://www.spigotmc.org/resources/dynmap%C2%AE.274/) | 最新 | 完整 |
| [BlueMap](https://www.spigotmc.org/resources/bluemap.83557/) | v3.3+ | 有限 |
| [Pl3xMap](https://github.com/pl3xgaming/Pl3xMap) v1 | v1（1.18+ 请用这个[分支](https://github.com/NeumimTo/Pl3xMap)） | 完整 |

## 依赖

- [Towny](https://github.com/TownyAdvanced/Towny) 0.97.1 及以上（**必需**）
- 上表中的一个网页地图插件（**必需**）

## 安装

1. 把插件 jar（`maptowny-plugin/target/SquareTowny-1.1.jar`）放入服务器的 `plugins` 目录。
2. 重启服务器，会自动生成 `SquareTowny/config.yml`。
3. 把 `enabled-worlds` 改成你想显示领地范围的世界名。

## 使用与配置

开箱即用，最常需要调整的是 `enabled-worlds`。

### 命令

| 命令 | 说明 | 权限 |
| --- | --- | --- |
| `/maptowny reload` | 重载配置并重新渲染所有城镇 | `maptowny.reload` |
| `/maptowny render <城镇名>` | 渲染指定城镇 | `maptowny.render` |
| `/maptowny unrender <城镇名或UUID>` | 从地图上移除指定城镇 | `maptowny.unrender` |

所有命令都需要 `maptowny.use`（默认 op）；`maptowny.admin` 包含以上所有权限。

### 配置项（config.yml）

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `enabled-worlds` | `["world"]` | 显示领地范围的世界 |
| `update-period` | `5` | 全量重渲染周期（分钟） |
| `layer.*` | — | 图层名称、显示开关、优先级、z-index |
| `fill-style.fill-color` | `"#3388ff"` | 默认填充色 |
| `fill-style.fill-opacity` | `0.2` | 填充不透明度 |
| `fill-style.use-nation-color-fill` | `true` | 用国家颜色填充领地（仅在 `use-auto-colors` 为 `false` 时生效） |
| `fill-style.stroke-color` | `"#3388ff"` | 默认描边色 |
| `fill-style.use-town-color-fill` | `false` | 用城镇自己的颜色，优先于国家色（仅在 `use-auto-colors` 为 `false` 时生效） |
| `fill-style.use-auto-colors` | `true` | **自动配色（本分支新增）**：让每个城镇/国家颜色各不相同（优先于 Towny 中保存的地图颜色） |
| `icon-info.*` | — | 城镇/首都/前哨站图标及尺寸 |
| `icon-info.capital-icon-size` | `24` | 首都图标尺寸（默认小于城镇图标，缩小地图时不会遮挡画面） |

### 自动配色说明

- **没有国家**的城镇：按城镇 UUID 生成独一无二的颜色（重启后保持稳定）。
- **同一国家**内的城镇：共用**首都城镇**的颜色。城镇建国后颜色保持不变，之后加入的城镇也会变成首都的颜色。
- 国家颜色跟随首都；如果把首都转移到另一座城镇，国家颜色也会随之改变。
- **自动配色默认开启，并优先于 Towny 中保存的地图颜色。** 因为 Towny 会给每个新建城镇和国家随机分配地图颜色（`global_town_settings.allowed_map_colors` / `global_nation_settings.allowed_map_colors`），这些颜色与手动设置的颜色无法区分；若不这样处理，城镇建国或加入国家后就会丢失自动配色、退回默认颜色。
- 如需使用 Towny 中设置的颜色（`/town set mapcolor`、`/nation set mapcolor`）以及 `use-nation-color-*` / `use-town-color-*` 选项，请设置 `fill-style.use-auto-colors: false`。
- 颜色会在下一次周期重渲染时自动更新（或执行 `/maptowny reload`）。

## 插件 API

SquareTowny 提供小型 API（`maptowny-api` 模块）供其他插件集成，详见原项目的 [Wiki](https://github.com/TownyAdvanced/MapTowny/wiki/MapTowny-API)。

## 构建

这是一个标准 Maven 多模块项目，使用 [maven-toolchains-plugin](https://maven.apache.org/plugins/maven-toolchains-plugin/) 针对不同 JDK 版本编译各模块：

| 模块 | JDK 目标 |
| --- | --- |
| `maptowny-api`、`maptowny-plugin`、`maptowny-dynmap` | 8 |
| `maptowny-bluemap` | 11 |
| `maptowny-squaremap`、`maptowny-pl3xmap-v1` | 16 |

两种构建方式：

1. **标准 toolchains**：在 `~/.m2/toolchains.xml` 中配置 JDK 8、11、16（参考 [toolchains 指南](https://maven.apache.org/guides/mini/guide-using-toolchains.html)），然后执行 `mvn clean package`。
2. **单一现代 JDK（仓库自带配置）**：本仓库附带了 `toolchains-local.xml`，把所有需要的版本映射到同一个 JDK（21 或更新）。把其中 `jdkHome` 改成你本机的 JDK 路径后执行：

   ```
   mvn -t toolchains-local.xml clean package
   ```

构建产物为 `maptowny-plugin/target/SquareTowny-1.1.jar`。

## 许可证

MIT。本分支保留上游项目的 MIT 许可证与版权声明，欢迎提交 Pull Request。
