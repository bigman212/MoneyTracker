# `:app`

Android application module.

Keep here:

- Android entry points: `Application`, `Activity`, app-level manifest wiring.
- App navigation and top-level UI composition.
- DI wiring that belongs to the application boundary.
- Dependencies on feature and core modules.

Do not keep here:

- Reusable design-system primitives.
- Database implementation details.
- Business rules that can live in core or feature modules.
