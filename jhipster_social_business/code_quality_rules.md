# Code Quality & Rating Rules

Use this checklist to rate and review code files. A "perfect" file must satisfy all criteria below.

## 1. Cleanliness & Structure

- [ ] **No Boilerplate / Coupled Code**: Avoid unnecessary getter/setter clutter if not needed, and ensure low coupling between components. Dependencies should be injected.
- [ ] **No Unused Elements**: Remove all unused imports, variables, private methods, and commented-out code.
- [ ] **Neat Structure**: Code should be well-indented, grouped logically (fields, constructor, public methods, private methods), and readable.

## 2. Logic & robustness

- [ ] **No Temporary Logic**: Remove any `System.out.println`, temporary flags, or debugging hacks.
- [ ] **No Hardcoded Values**: "Magic numbers" and string literals (e.g., `"255"`, `1000`) should be extracted to named constants or configuration.
- [ ] **Proper Return Handling**:
  - Methods must return useful values where appropriate.
  - Avoid `void` if a return status is needed for the caller.
  - Ensure returned `Optional`s or Objects are handled by the caller (no swallowing results).

## 3. Error Handling

- [ ] **Catch Blocks Handled**:
  - Never leave a `catch` block empty.
  - Never catch generic `Exception` without logging or wrapping it.
  - Translate technical DB exceptions into meaningful `BusinessException` or friendlier warnings where appropriate.

## 4. Best Practices

- [ ] **Standard Design Patterns**: Use patterns appropriate for the framework (e.g., Service Layer, Repository Pattern in Spring).
- [ ] **Concurrency Safety**: Use `@Lock`, atomic variables, or synchronization where race conditions are possible.

## 5. Database & Performance

- [ ] **Avoid N+1 Selects**: Do not fetch related entities inside a loop. Use `JOIN FETCH` queries or Entity Graphs to load data efficiently.
- [ ] **Read-Only Transactions**: Annotate data-fetching methods with `@Transactional(readOnly = true)` to optimize performance and database resource usage.
- [ ] **Pagination Mandatory**: Never expose a `findAll()` that returns a list without limits. Always use `Pageable` or `Limit`.
- [ ] **Secure Queries**: Always use parameter binding (`:paramName`) in custom JPQL/SQL. Never concatenate strings into queries (SQL Injection risk).
- [ ] **Efficient Aggregation**: Use DB-level math (`SUM`, `COUNT`) instead of loading entities into Java memory to calculate stats.
