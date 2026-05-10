# `:core:data`

Data orchestration module.

Keep here:

- Repository implementations.
- Data-to-domain mapping orchestration.

Do not keep here:

- Local data sources that proxy Room DAO access; keep those in `:core:database` so DAO and Entity types stay internal.
- Room database definitions, DAO interfaces, or Room entities.
- UI code.
- Domain-only contracts.
