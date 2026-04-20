# Contributing

Thanks for your interest in improving ZenithProxyOfflineUUID.

This project is small, so the goal is to keep contributions simple, readable, and easy to review.

## Development Setup

Requirements:

- Java 21 or newer for running the built plugin
- A working JDK that can build the project with Gradle
- Git

Build the project locally:

```powershell
.\gradlew.bat build
```

The output jar will be generated in `build/libs`.

## Project Scope

This plugin is intentionally focused on one job:

- overriding the offline UUID used during login

Please try to keep changes aligned with that scope. Small, focused improvements are preferred over broad feature creep.

## Before Opening a Pull Request

Please try to:

- keep changes minimal and targeted
- preserve compatibility with the current ZenithProxy target version
- update documentation when behavior changes
- update `CHANGELOG.md` for user-facing changes
- verify the project still builds successfully

## Coding Guidelines

- Prefer small and direct implementations
- Match the existing naming and formatting style
- Avoid adding new dependencies unless they are clearly necessary
- Keep command output and configuration names stable unless there is a strong reason to change them
- If using reflection, document why it is needed and keep the usage tightly scoped

## Commit Messages

Short, descriptive commit messages are preferred. Examples:

- `Fix login UUID override handling`
- `Update README and release workflow`
- `Add deterministic prefix UUID support`

## Pull Requests

When opening a pull request, include:

- what changed
- why the change was needed
- any config or behavior differences
- whether you tested a local build

If the change affects release artifacts or user-facing behavior, mention that clearly in the PR description.

## Reporting Issues

If you report a bug, please include:

- ZenithProxy version
- plugin version
- Java version
- what you expected
- what actually happened
- logs or stack traces if available

## Security

If you think you found a security issue, avoid posting exploit details publicly right away. Share enough information for the issue to be reproduced safely and responsibly.
