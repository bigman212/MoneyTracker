# О каком тренде речь

Это **Harness Engineering** — новая инженерная дисциплина вокруг работы с кодинг-агентами. Термин ввёл Mitchell Hashimoto (создатель Terraform/HashiCorp) в феврале 2026 в посте, который ты дал. Через пару недель OpenAI и Anthropic выпустили свои статьи, Martin Fowler опубликовал mental model — и термин закрепился.

## Идея в одну строку

**Agent = Model + Harness.** Модель (LLM) — это "мозг", harness — это всё остальное: tools, system prompts, skills, rules, memory, feedback loops, валидация, hooks, MCP-серверы. И вот в этой "обвязке" сейчас сосредоточено почти всё качество работы агента, а не в выборе модели.

Hashimoto формулирует так: каждый раз, когда агент делает ошибку, ты не правишь её руками — ты инженеришь окружение так, чтобы агент **больше никогда не мог сделать эту ошибку**. Это и есть steering loop.

OpenAI в своём эксперименте за 5 месяцев командой из 3 человек собрали продукт на ~1М строк кода, где **ноль строк написаны людьми** — всё сделали Codex-агенты. Их вывод: "наши самые сложные задачи теперь — проектирование окружений, обратных связей и систем контроля", а не написание кода.

## Mental model от Martin Fowler

Harness состоит из двух типов контролей:

- **Guides (feedforward)** — что агент видит ДО действия: AGENTS.md/CLAUDE.md, skills, ref docs, MCP-tools, language servers. Снижают вероятность ошибки на старте.
- **Sensors (feedback)** — что наблюдает ПОСЛЕ действия: линтеры, тесты, type checkers, structural tests, code-review агенты. Помогают самокоррекции.

И два типа исполнения:
- **Computational** — детерминированные, дешёвые, быстрые (линтеры, тесты, ArchUnit-style проверки)
- **Inferential** — LLM-as-judge, ревью-агенты — дорогие, но с семантическим пониманием

Без feedforward агент бьётся в одни и те же ошибки. Без feedback он не знает, сработали ли его изменения. Нужны оба.

## Где BitGN

BitGN Arena (Rinat Abdullin) — публичный бенчмарк, где harness-архитектуры соревнуются на реальных задачах. PAC1 уже прошёл (победители — `codex-on-rails` и `Operation Pangolin`, оба с открытым кодом). **ECOM challenge — 30 мая 2026**, симулятор e-commerce с warehouse, политиками, fraud, returns. Это твоя возможность не теоретизировать, а сразу выйти на лидерборд.

---

# План пет-проекта (8 недель, выстроен по сложности)

## Фаза 0. Подготовка контекста (2–3 вечера)

**Цель:** прочитать первоисточники до того, как писать код. Без этого дальнейшие шаги сольются в карго-культ.

1. Прочитай в таком порядке:
  - Hashimoto — "My AI Adoption Journey" (мотивация, шаги 1–6)
  - Fowler — "Harness engineering" (mental model: guides/sensors, computational/inferential, regulation categories)
  - OpenAI — "Harness engineering: leveraging Codex" (что делает индустрия)
  - Insights BitGN про `codex-on-rails` и `Operation Pangolin` (две разные архитектуры — REPL vs MCP rails)
2. Заведи себе `harness-notes.md` — конспект по каждой статье в 5–10 буллетов. Это станет основой твоего собственного AGENTS.md позже.

**Критерий выхода:** ты можешь своими словами объяснить разницу между feedforward и feedback control, и между computational и inferential sensor.

---

## Фаза 1. Минимальный harness на существующем проекте (неделя 1)

**Цель:** не строить новое, а ощутить разницу на привычном коде. Hashimoto на этом этапе настаивает — "reproduce your own work".

Возьми любой свой действующий проект (или форкни что-то на ~3–10к строк, что ты понимаешь).

1. **Базовый AGENTS.md / CLAUDE.md.** Опиши:
  - Как запускать тесты, линтер, форматтер (точные команды)
  - Структуру проекта в 5–10 строках
  - 3–5 конвенций, которые "load-bearing" (без них всё ломается)
  - Что НЕ трогать
2. **Фиксированный набор команд для агента** — Makefile или `justfile`: `test`, `lint`, `typecheck`, `format`, `check` (= всё разом). Чтобы агент не изобретал каждый раз, как запустить тесты.
3. **Pre-commit hook** — настрой минимум: форматтер + линтер + быстрые тесты. Это твой первый computational sensor.
4. **Дневник ошибок агента.** Заведи `agent-mistakes.md`. Каждый раз, когда агент облажался — записывай: что попросил, что сделал, что должен был. **Не правь поведение моментально.** Накопи 10–15 кейсов.

**Критерий выхода:** есть проект, где агент работает с твоей обвязкой, и есть ≥10 записанных ошибок.

---

## Фаза 2. Steering loop — закрываем ошибки (неделя 2)

**Цель:** превратить дневник ошибок в harness-фиксы. Это и есть инженерия харнесса.

Возьми каждую запись из `agent-mistakes.md` и реши, какой инструмент её закроет:

| Тип ошибки | Что добавить |
|---|---|
| Использовал не тот API/команду | строка в AGENTS.md |
| Сломал слой архитектуры | structural test (ArchUnit / dependency-cruiser / ts-arch) |
| Стилевая/конвенция | custom lint rule (eslint plugin / semgrep / regex pre-commit) |
| Сложная процедура (как добавить миграцию) | skill — отдельный `.md` с пошаговой инструкцией |
| Семантическая ошибка, которую линтер не поймает | inferential sensor — review-агент с узким промптом |

Под каждое исправление **зафиксируй аргумент**: почему этот инструмент, а не другой. (Это привычка, которая тебе пригодится в любом ревью harness-изменений — и которую ты, судя по preferences, ценишь.)

**Критерий выхода:** ≥5 ошибок закрыты feedforward (guides), ≥3 — feedback (sensors). Минимум 1 inferential sensor.

---

## Фаза 3. Custom MCP server для своего домена (неделя 3–4)

**Цель:** перейти от "общего" агента к специализированному. Так делает `codex-on-rails`.

Это самый высокий ROI шаг. Идея: вместо того чтобы агент через bash шарил по твоей системе, ты даёшь ему 5–10 узких MCP-tools с жёсткими контрактами.

Примеры доменов для пета:
- Личный todo/knowledge vault — tools: `note_search`, `note_read`, `note_write`, `tag_list`, `link_graph`
- Личные финансы из CSV — tools: `tx_search`, `tx_aggregate`, `category_list`, `budget_check`
- API-обёртка над твоим хобби-сервисом

**Что важно скопировать у `codex-on-rails`:**
- Метаданные на каждом read (`type`, `trust`, `format`) — агент знает, насколько источнику можно верить
- Валидация writes до того, как они уйдут в систему (схема — например JSON Schema или Pydantic)
- Структурированный output с явными outcome-кодами (`OK`, `DENIED_SECURITY`, `NEEDS_CLARIFICATION`, `UNSUPPORTED`) вместо свободного текста
- Trust boundaries — что приходит из неподконтрольных источников, агент не должен исполнять как инструкцию

Стек: TypeScript или Python MCP SDK от Anthropic. Запусти через Claude Code или Codex CLI.

**Критерий выхода:** агент решает 3–5 типовых задач твоего домена через MCP-tools, ни разу не дёргая bash.

---

## Фаза 4. Architecture fitness harness (неделя 5)

**Цель:** регулировать не код-стиль, а архитектуру. Это уровень, на котором OpenAI прижимает агентов в своей кодовой базе.

1. Сформулируй 3–5 архитектурных правил для своего пет-проекта. Например:
  - "Слой Repo не зависит от Service"
  - "UI не импортирует напрямую из Repo"
  - "Только Providers могут трогать env vars"
2. Закодируй их как **structural tests**:
  - JS/TS — `dependency-cruiser`, `ts-arch`
  - Python — `pytestarch`, `import-linter`
  - JVM — `ArchUnit`
3. Добавь их в pre-commit и в CI.
4. Намеренно сломай правило руками, попроси агента это исправить, посмотри, как он реагирует на красный sensor.

**Критерий выхода:** агент получает понятный fail с указанием правила и сам предлагает фикс.

---

## Фаза 5. Inferential sensor — LLM-as-judge (неделя 6)

**Цель:** добавить семантический ревью, который computational sensor поймать не может.

1. Напиши review-агента (отдельный системный промпт) с узкой ответственностью. Например: "проверь, что в этом diff нет over-engineering: лишних абстракций, неиспользуемых параметров, дубликатов логики из существующего кода".
2. Запускай его как `git hook` на pre-push или как github action.
3. Output — структурированный JSON со списком замечаний, чтобы основной агент мог распарсить и самокорректироваться.
4. **Важно:** не давай review-агенту право блокировать всё подряд. Один-два узких критерия. Иначе шум задушит сигнал.

**Критерий выхода:** review-агент поймал хотя бы один кейс, который computational tools пропустили, и основной агент исправился без твоего участия.

---

## Фаза 6. Участие в BitGN ECOM Challenge (неделя 7–8)

**Цель:** проверить свою обвязку на чужом, объективно оцениваемом бенчмарке. Это закроет петлю обратной связи всему обучению.

1. Прочитай overview, participant quickstart, scoring rubric (ссылки на github лежат на странице challenge).
2. Запусти ECOM Python Sample agent — увидишь, какой baseline.
3. Изучи source двух победителей PAC1 (`codex-on-rails`, `Operation Pangolin`) — они дают две разные архитектурные парадигмы.
4. Построй свой агент, переиспользуя то, что собрал в фазах 3–5: MCP rails, валидация writes, структурированные outcome-коды, dispute-резолюция.
5. Гоняй по DEV-leaderboard, итерируй harness под конкретные failure modes (как Igor Inozemtsev делал — у него 6 промахов было на финансовых задачах, 7 — на YAML serialization).
6. Сабмить blind run к 30 мая.

**Критерий выхода:** твой агент стабильно набирает в DEV ≥80% от максимума и ты понимаешь, какие именно failure modes его ограничивают.

---

# Что отличает этот план от "ещё один туториал по AI"

- **Каждая фаза начинается с дневника ошибок, а не с фич.** Harness строится реактивно: видишь ошибку → инженеришь так, чтобы её больше не было. Без этого получится полка из непротестированных skills и MCP-серверов.
- **Computational перед inferential.** Линтеры и structural tests дают 80% результата. LLM-as-judge — последние 20%, и они дорогие.
- **Узкие tools, не широкие.** `codex-on-rails` выиграл за счёт того, что у модели было 6 vault-команд с жёсткими контрактами, а не bash + filesystem.
- **Объективный финал.** BitGN ECOM — это не self-assessment, а лидерборд с детерминированной оценкой. Без него легко обмануть себя, что harness "работает".

# Что меняется при переходе на Android

Главное: harness engineering — это про **обвязку для агента, который пишет код твоего приложения**, не про агента *внутри* приложения. То есть ты обустраиваешь Claude Code / Codex / Cursor, чтобы они эффективно делали тебе Android-разработку. Это меняет:

- Стек обвязки — Gradle, Kotlin tooling, AndroidX, Compose
- Главный feedback-канал помимо тестов — **скриншоты эмулятора и Compose previews** (это то, что Hashimoto явно отмечает как высокоценный sensor)
- Architecture fitness — модули, Hilt-графы, Compose-стабильность
- Финальная фаза смещается с BitGN ECOM на **свой benchmark внутри проекта** (ECOM остаётся как опциональный side-quest)

Стек по умолчанию ниже: Kotlin + Jetpack Compose + Gradle Kotlin DSL + Hilt + Coroutines/Flow. Если у тебя другой — скажи, поправлю.

И сразу зафиксируем: **detekt не используем нигде** (по твоим preferences). Везде, где обычно ставят detekt, я подобрал альтернативы.

---

## Фаза 0. Подготовка контекста (2–3 вечера)

То же, что было: прочитать Hashimoto → Fowler → OpenAI → BitGN insights. Дополнительно:

- Прочти **Anthropic "Effective harnesses for long-running agents"** — у них есть Android-релевантные паттерны
- Полистай AGENTS.md из реальных Android open-source проектов (Now in Android от Google, Tivi от chrisbanes) — увидишь, как опытные команды формулируют правила

**Критерий выхода:** в `harness-notes.md` есть раздел "что специфично для Android" — минимум 5 пунктов про то, где Android отличается от веба/бэкенда (recomposition, lifecycle, конфиг-чейнджи, эмулятор, R8/ProGuard, multi-module).

---

## Фаза 1. Минимальный harness на Android-проекте (неделя 1)

**Цель:** свести ручные действия к набору команд, которые агент знает наизусть.

**Выбор стартового пет-проекта.** Чтобы harness имел смысл, проект должен быть достаточно нетривиальным. Подойдут:
- Habit/budget tracker с Room + Compose + WorkManager
- RSS/podcast reader с офлайн-синком
- Локальная заметочница с FTS-поиском
- Что угодно с ≥3 экранами, локальной БД и фоновой работой

Однотипный TodoMVP не даст увидеть отдачу от harness — там агент справится и без обвязки.

**Конкретные шаги:**

1. **`AGENTS.md` в корне проекта.** Минимум:
  - Точные Gradle-команды: `./gradlew testDebugUnitTest`, `./gradlew lintDebug`, `./gradlew ktlintCheck`, `./gradlew assembleDebug`
  - Структура модулей (`:app`, `:core:data`, `:core:ui`, `:feature:*` и т.д.) и правила зависимостей между ними
  - Какие версии: minSdk, targetSdk, AGP, Kotlin, Compose Compiler — чтобы агент не предлагал API из несовместимых версий
  - 5–10 load-bearing-конвенций: например, "ViewModels не знают про Composable", "Repository всегда возвращает Flow, не suspend", "Resources только через `:core:designsystem`"
  - Что НЕ трогать: `versionCode`, `signingConfigs`, `proguard-rules.pro`, ключи в `local.properties`

2. **`gradle.properties` + конфиг для скорости.** Агент будет гонять билды десятками раз — медленный билд убьёт цикл:
   ```
   org.gradle.parallel=true
   org.gradle.caching=true
   org.gradle.configuration-cache=true
   kotlin.incremental=true
   ```
   Configuration cache — если поддерживается твоими плагинами.

3. **Single-command check.** Сделай Gradle-task `check` (или скрипт `./scripts/agent-check.sh`), который объединяет: `ktlintCheck → lintDebug → testDebugUnitTest`. Агент должен знать одну команду для "всё ли ок".

4. **Линтеры — стартовый набор без detekt:**
  - **Android Lint** (встроенный) — обязательный baseline. Включи `warningsAsErrors true` для CI и набора issues, которые тебе важны
  - **ktlint** через `org.jlleitschuh.gradle.ktlint` или **Spotless** — стиль кода. Spotless удобнее, потому что одной командой `./gradlew spotlessApply` форматирует всё

   *Аргумент за такой выбор:* у тебя стоит запрет на detekt, и две функции, которые он обычно закрывает (стиль и code smells), хорошо распределяются: ktlint берёт стиль, Android Lint + Konsist (см. фазу 4) берут структурные проверки. Разделение даже чище, чем единый detekt.

5. **Дневник ошибок.** `agent-mistakes.md`. Заводи запись каждый раз, когда агент:
  - Использовал API из другой версии Compose/AndroidX
  - Сломал recomposition (написал нестабильный параметр, прокинул лямбду без `remember`)
  - Перепутал `LaunchedEffect` / `DisposableEffect` / `SideEffect`
  - Импортнул что-то из соседнего модуля в обход правил
  - Запустил длинный `connectedAndroidTest`, когда хватало `testDebugUnitTest`
  - Проигнорировал `Hilt`-граф и сделал ручную инстанциацию

**Критерий выхода:** агент собирает приложение и проходит `check` одной командой; есть ≥10 записей в дневнике.

---

## Фаза 2. Steering loop — закрываем накопленное (неделя 2)

Берёшь каждую запись из дневника и решаешь, каким инструментом её закрыть. Для Android маппинг такой:

| Тип ошибки | Чем закрывать |
|---|---|
| Не та версия API / depricated method | `AGENTS.md` + custom Android Lint check |
| Импорт из чужого модуля | Konsist test (фаза 4) или Gradle dependency restriction |
| Compose-нестабильность | Compose Compiler Metrics + правило в AGENTS.md |
| ViewModel/UI смешались | Konsist + skill "how to add a feature" |
| Долгие тесты вместо unit | Skill "how to test X" + Gradle test filter alias |
| Неправильный Hilt scope | Skill про DI-граф + комментарии в `@Module` |
| Гонки в корутинах | LLM-as-judge (фаза 5) — это семантика, линтер не поймает |

**Skills для Android.** Заведи папку `.agent/skills/` (или как принято у твоего агента) с короткими how-to-файлами:
- `add-feature-module.md` — пошагово: создать модуль, добавить в `settings.gradle.kts`, прописать deps, навигацию
- `add-room-entity.md` — Entity → DAO → миграция → тест
- `add-screen.md` — Composable + ViewModel + контракт + preview + screenshot test
- `debug-recomposition.md` — как включить Layout Inspector, как читать Compose Compiler reports

**Под каждое исправление зафиксируй аргумент** в коммит-сообщении: почему именно этот инструмент. Потом будешь видеть эволюцию своего harness.

**Критерий выхода:** ≥5 ошибок закрыто feedforward (AGENTS.md/skills), ≥3 — feedback (lint/тесты/structural). Минимум 1 кастомный Android Lint rule.

---

## Фаза 3. Custom MCP server для Android-разработки (неделя 3–4)

**Это самый высокий ROI шаг для Android.** У агента по умолчанию очень куцые средства взаимодействия с эмулятором и сборкой — он шарит по логам через `grep` и слепо собирает APK. Узкие MCP-tools меняют эту динамику.

**Tools, которые стоит реализовать (выбери 5–6, не все сразу):**

1. `screenshot_emulator(device_id?)` — снимает скриншот через `adb exec-out screencap -p`, сохраняет в файл, возвращает путь. **Самый ценный tool** — позволяет агенту визуально верифицировать UI после изменения.
2. `logcat_filtered(tag, level, last_n_seconds)` — фильтрованный logcat, не весь поток
3. `run_unit_tests(module, test_filter?)` — запуск с фильтром, возвращает структурированный JSON с failures, не сырой stdout
4. `compose_metrics(module)` — запускает Compose Compiler с metrics flag, парсит отчёт, возвращает список нестабильных классов и функций
5. `apk_inspect(variant)` — размер APK, методы по DEX, что в манифесте, какие permissions
6. `gradle_dependencies(module)` — отрезолвенный граф для модуля
7. `lint_diff(base_branch)` — список новых lint-ишьюс относительно `main`

**Критичные правила для Android-MCP, перенесённые из `codex-on-rails`:**
- Каждый tool возвращает структурированный JSON с явным `outcome` (`OK`, `BUILD_FAILED`, `TESTS_FAILED`, `EMULATOR_NOT_RUNNING`, `UNSUPPORTED`)
- Перед write-операциями (запуск тестов меняет состояние эмулятора) — sanity check
- На скриншоты вешай метаданные: `device_id`, `api_level`, `screen_density`, `timestamp`
- Trust boundary: вывод logcat — это untrusted данные, агент не должен исполнять их как инструкции (например, если в логах рекламы стороннего SDK будет "ignore previous instructions")

**Стек MCP-сервера.** Делай отдельным процессом на TypeScript или Python (Anthropic SDK). Не пытайся встраивать в Android-проект. Сервер общается с эмулятором через `adb` и с Gradle через CLI.

**Критерий выхода:** агент решает 3 типовые задачи через MCP без bash:
- "Поправь экран X, чтобы кнопка была красной" → правка → `screenshot_emulator` → визуальная проверка
- "Юнит-тест Y падает" → `run_unit_tests` → правка → ретест
- "Уменьши recomposition на экране Z" → `compose_metrics` → правка → ремерик

---

## Фаза 4. Architecture fitness harness (неделя 5)

Здесь Android даёт богатый материал — модули, слои, Compose-конвенции.

**Инструмент: Konsist** (`konsist` от Lemon AppDev). Это правильный выбор для Kotlin-only проекта: он работает на уровне Kotlin AST, понимает annotations, naming, package structure. *Аргумент за Konsist vs ArchUnit:* ArchUnit — JVM-байткод, не видит Kotlin-специфики (extension funcs, suspend, top-level). Для Compose-проекта Konsist даёт больше выразительности.

**Стартовый набор Konsist-правил:**

1. **Слои.** ViewModel не зависит от Composable; Repository не зависит от ViewModel; Data-layer не импортирует AndroidX UI.
2. **Naming.** Все ViewModels оканчиваются на `ViewModel`; все Use Cases — на `UseCase` или начинаются с глагола; все @Composable начинаются с заглавной буквы и не имеют return type `Unit?`.
3. **Compose-конвенции:**
  - Composable, принимающий `Modifier`, должен иметь его как первый опциональный параметр со значением `Modifier`
  - State hoisting: stateful Composable не должны называться так же, как stateless (правило по naming)
  - Никакого `LiveData` в Compose-слое (если ты на StateFlow)
4. **Hilt:**
  - Все `@HiltViewModel` находятся в `feature/*/presentation` пакетах
  - `@Module` с `@InstallIn(SingletonComponent::class)` только в `:core:di`

**Где запускать:** `./gradlew :konsistTest:test` в pre-commit и в CI. Konsist-тесты — обычные JUnit-тесты, работают за секунды.

**Дополнительно:**
- **Modularization graph check** — скрипт, который рендерит `./gradlew projectDependencyGraph` и фейлит build, если появилась запрещённая стрелка (например, `:feature:a → :feature:b`)
- **Baseline для лагаси.** Если проект уже большой — Konsist умеет принимать baseline-файл, чтобы не блокировать существующий долг

**Критерий выхода:** ≥5 архитектурных правил закодировано, в CI все зелёные. Намеренно нарушь правило руками → агент получает понятный fail с указанием конкретного класса и предлагает фикс.

---

## Фаза 5. Inferential sensors — visual + semantic review (неделя 6)

На Android здесь два слоя.

**5a. Screenshot tests как computational visual sensor.** Это не LLM, но это сенсор, который computational tools обычно не имеют. Выбор:
- **Roborazzi** — быстрый, JVM-only, через Robolectric. Хорош для большинства экранов.
- **Paparazzi** (Square) — отрисовывает Compose без эмулятора через LayoutLib. Очень быстрый.
- **Compose Screenshot Tests** (официальный из Android Studio Iguana+) — если хочешь оставаться в Google-стеке.

*Аргумент за Roborazzi для пет-проекта:* активно развивается, дружит с Compose, есть golden-image diff, легко интегрируется в pre-commit. Paparazzi медленнее реагирует на новые версии Compose.

Workflow: агент меняет UI → screenshot test падает с diff → агент видит diff (через MCP-tool, который вернёт два изображения и pixel-diff) → решает, ожидаемо это или нет → если ожидаемо, обновляет golden.

**5b. LLM-as-judge для Compose-семантики.** Это inferential sensor — он ловит то, что Lint и Konsist пропускают.

Узкий ревью-агент с промптом вроде:
> "Проверь diff. Найди только: (1) лямбды в Composable, переданные без `remember`, что вызовет рекомпозицию; (2) использование `mutableStateOf` без `remember`; (3) тяжёлые операции (regex, парсинг) в теле Composable; (4) collect-ы Flow без `collectAsStateWithLifecycle`. Output — JSON со списком найденного, по одному пункту на находку. Не предлагай рефакторинги, только находки."

Запускай как pre-push hook или GitHub Action. Output структурированный, чтобы основной агент мог распарсить и сам исправить.

**Важно:** не давай judge-агенту широкий мандат. Один промпт — одна узкая задача. Иначе шум задушит сигнал и ты начнёшь его игнорировать. Hashimoto и Fowler оба на этом настаивают.

**Критерий выхода:** хотя бы один кейс, где judge поймал то, что Konsist и Lint пропустили, и основной агент исправил без твоего участия.

---

## Фаза 6. Свой benchmark внутри проекта (неделя 7–8)

**Цель:** замкнуть петлю обратной связи на самом проекте. Это адаптация финальной фазы — вместо BitGN ECOM (который не про Android) делаем свой mini-benchmark.

**Что строим:** набор из 10–15 типовых задач для твоего приложения, с эталонными решениями и автоматической оценкой.

Примеры задач:
- "Добавь экран настроек с переключателем тёмной темы, тема сохраняется через DataStore"
- "Найди и почини: на экране списка элементы рекомпозятся при каждом скролле"
- "Добавь миграцию Room с версии 3 на 4: новая колонка с дефолтом"
- "Экран X крашится при rotation — найди и почини"
- "Перенеси `:feature:foo` на новый паттерн ViewModel + state holder"

**Как оценивать (deterministic scoring):**
- Прошёл ли `check` (lint + konsist + тесты)
- Прошёл ли screenshot test (если есть)
- Не нарушены ли Konsist-правила
- Размер diff (за overengineering штрафовать)
- Время выполнения (опционально)

**Как использовать:**
1. Запусти baseline — текущий harness без новых улучшений → запиши счёт
2. Найди задачи, где агент проваливается → итерируй harness под них (как Igor Inozemtsev итерировал под YAML serialization и numeric reasoning)
3. Запиши до/после после каждого harness-улучшения

**Опциональный side-quest:** в параллель **поучаствуй в BitGN ECOM 30 мая**. Это не Android, но это объективный внешний бенчмарк, который проверит твоё понимание harness-паттернов на чужой задаче. Sample agent на Python берётся за пару часов; знания из фаз 3–5 (custom MCP, structured outcomes, trust boundaries, structured reviews) переносятся напрямую.

**Критерий выхода:** свой benchmark запускается одной командой; есть baseline-цифра; есть минимум одно harness-улучшение, которое подтверждённо подняло счёт.

---

# Что отличает этот план от обычной Android-разработки

- **AGENTS.md — это первоклассный артефакт**, не readme-файл. Он эволюционирует с каждой ошибкой агента, как тесты эволюционируют с каждым багом.
- **Скриншот эмулятора — это feedback-петля.** Без MCP-tool на скриншот агент работает вслепую и фиксит UI наугад. С ним — он видит, что сломал, и сам откатывается.
- **Konsist + Android Lint > detekt + всё остальное.** Не потому что detekt плох, а потому что для Compose-проекта структурные правила выразительнее в Konsist, а стиль закрывается ktlint/Spotless. Меньше дублирующегося тулинга — меньше конфликтов в CI.
- **Свой benchmark — не upfront, а финал.** Сначала набей шишек на реальной разработке (фазы 1–5), накопи понимание failure modes, и только потом оформи их в формальный benchmark. Иначе ты будешь оптимизировать harness под выдуманные задачи.
