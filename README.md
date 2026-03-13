# MX-Project-Edited

本项目以 [MX](https://github.com/kireikosasha/MX-Project) 为基础进行构建与扩展，用途为 **Minecraft 反作弊**。

相对 **MX-Project**，本项目的优势在于提供更广泛/普适的 **Aim 检查** 和 更多方面的检查。

原MX缺少相关移动/发包检测，我会在后期开发逐渐加入相关内容。

## 兼容性说明
本项目基于 PaperAPI `paper-api 1.16.5-R0.1-SNAPSHOT` 进行开发，目标兼容 **Minecraft 1.16.5及以下版本** 生态。

在服务端类型上，**Paper** 为首选与主要测试目标；理论上 **Bukkit-based**（如 Spigot、Purpur 等）也可运行，但建议在与 1.16.5 对应的版本上完成实际验证与回归测试。

如服务端或依赖版本发生变化，需评估 API 兼容性与行为差异对检测逻辑的影响。

项目拥有者不保证在Paper/Spigot服务端外的稳定性。

## 使用说明
原使用守则与具体方法可参考MX-Project的描述

[查看原自述文件](https://github.com/kireikosasha/MX-Project/blob/master/README.md)

## 构建与使用注意事项
- 需要 JDK 8+，建议使用所述版本的 JDK/JRE 进行编译与运行。
- 使用 Maven 构建：`mvn clean package`。
- 构建产物位于 `target/` 目录。若你在多环境部署，建议区分不同构建的产物名称或打包目录。
- `paper-api`、`ProtocolLib`、`lombok` 为 `provided` 依赖：编译时使用，运行时需由服务端/环境提供。尤其是 **ProtocolLib 5.3.0** 。
- `src/main/resources` 下的 `.yml`、`.properties` 会参与过滤替换，`.dat` 不进行过滤。若新增资源类型，注意是否需要参与过滤。

## 已添加内容
- Aim检测: 机械化随机
- 其他: 修复部分已知问题

### 警告
包括但不限于 SKID 内容、 AI CODE、 项目逻辑与结构差、 非100%准确率...

您需要在使用过程中考虑 MX Edited 为服务器带来的**负面影响**！