# Provider Guide

Providers must map native source data into canonical project models without leaking source-specific assumptions into UI or player modules.

Provider classes must declare:

- Trust level.
- Capabilities.
- Stream expiration behavior.
- Cache and rate-limit rules.
- Legal and maintenance risks.

Unofficial providers must remain isolated, opt-in, disabled by default, and absent from Play-compatible builds.
