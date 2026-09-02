## ZenithProxyOfflineUUID __VERSION__

### English

This release includes the latest updates to ZenithProxyOfflineUUID.

#### Highlights
- Client and server UUID behavior can be configured separately
- Supports `original`, `fixed`, `random`, and `generated` modes
- Supports generated UUIDs with or without a prefix
- Client-side UUID rewriting only applies while ZenithProxy is using offline authentication

#### Recommended usage
```text
offlineuuid on
offlineuuid server off
offlineuuid client on
```

#### Notes
- `server` UUID rewriting is not recommended for security-sensitive use
- This plugin is primarily intended for client-side UUID control when ZenithProxy connects outward with offline authentication

#### Artifact
- `__JAR_NAME__`

### 中文

这个版本包含 ZenithProxyOfflineUUID 的最新更新。

#### 主要内容
- `client` 和 `server` 两侧的 UUID 行为可以分开配置
- 支持 `original`、`fixed`、`random`、`generated` 四种模式
- 支持“带前缀”或“不带前缀”的生成式 UUID
- `client` 侧 UUID 改写仅在 ZenithProxy 使用离线登录时生效

#### 推荐用法
```text
offlineuuid on
offlineuuid server off
offlineuuid client on
```

#### 说明
- 出于安全考虑，不建议把 `server` 侧 UUID 改写作为常规方案使用
- 这个插件的主要用途是 ZenithProxy 在离线认证向目标服务器发起连接时，对 `client` 侧 UUID 进行控制

#### 构建产物
- `__JAR_NAME__`
