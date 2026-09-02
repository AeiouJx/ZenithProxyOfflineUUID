## ZenithProxyOfflineUUID __VERSION__

### English

This release includes the latest updates to ZenithProxyOfflineUUID.

#### Highlights
- Lightweight config-only approach for controlling offline UUID
- Supports `fixed`, `random`, and `generated` UUID modes
- Generated mode supports optional `prefix + username` UUID generation
- Sets `CONFIG.authentication.offlineUUID` directly (no packet interception)

#### Recommended usage
```text
offlineuuid on
offlineuuid mode generated
offlineuuid prefix OfflinePlayer:
offlineuuid usePrefix on
```

#### Notes
- This plugin is only for UUID control, not authentication bypass
- Does not bypass Mojang online-mode authentication

#### Artifact
- `__JAR_NAME__`

### 中文

这个版本包含 ZenithProxyOfflineUUID 的最新更新。

#### 主要内容
- 轻量级纯配置方式控制离线 UUID
- 支持 `fixed`、`random`、`generated` 三种模式
- generated 模式支持可选的 `前缀 + 用户名` 生成方式
- 直接设置 `CONFIG.authentication.offlineUUID`（无数据包拦截）

#### 推荐用法
```text
offlineuuid on
offlineuuid mode generated
offlineuuid prefix OfflinePlayer:
offlineuuid usePrefix on
```

#### 说明
- 本插件仅用于 UUID 控制，不能绕过身份验证
- 不能绕过 Mojang 在线模式认证

#### 构建产物
- `__JAR_NAME__`
