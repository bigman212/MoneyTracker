# `:core:database`

Room persistence module.

Keep here:

- Room database definitions.
- DAO interfaces.
- Room entities and database migrations.
- Persistence-only mapping helpers.
- Local data sources that proxy DAO access without exposing DAO or Entity types.

Rules:

- DAO interfaces and Room entities must be `internal`.
- DAO and Entity types must not be imported outside `:core:database`.
- Do not use `autoGenerate = true`; repositories generate ids in code before writing.
- Prefer `@Upsert` for DAO writes. Do not use `OnConflictStrategy.ABORT`.

Do not keep here:

- UI code.
- Feature-specific business rules.
- Repository orchestration that belongs in a data module.
- Raw SQLite APIs.
