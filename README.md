# rest-assured-demo

A small API test-automation project I put together to show how I structure Java API tests: REST Assured, TestNG, Jackson, AssertJ, Maven, and GitHub Actions.

Honest note: this is a portfolio demo. It runs against [JSONPlaceholder](https://jsonplaceholder.typicode.com), a free public mock API. Writes (POST/PUT/PATCH/DELETE) are faked and don't persist, so the tests assert on what the mock promises, not on real business rules. Because it's a shared public service over the internet, a test can occasionally fail for network reasons; I added a retry for that (see below).

## What's in it

- 18 test methods (25 executed cases with the data providers) covering status codes, JSON-path shape checks, POJO deserialization, headers, filter and pagination queries, nested resources, create/replace/patch/delete, negative cases (404, wrong method), a soft response-time budget, and JSON Schema contract tests.
- Test groups: `smoke`, `regression`, `negative`, `contract`.
- Retry for flaky network: a TestNG `IRetryAnalyzer` (max 2 retries) attached to every test through an `IAnnotationTransformer` listener.
- Parallel execution: Surefire runs test methods on 4 threads. The client is stateless, so that's safe.

## Requirements

Java 17 and Maven 3.9+.

## Run it

```bash
mvn test                      # everything
mvn test -Dgroups=smoke       # just the fast checks
mvn test -Dgroups=contract    # schema tests only
mvn test -Dbase.uri=https://jsonplaceholder.typicode.com   # point at another host
mvn -B verify                 # what CI runs
```

Reports land in `target/surefire-reports/`.

## Structure

```
src/test/java/com/jhankarimov/restdemo/
  client/   ApiClient (thin wrapper per endpoint) + Specs (request/response spec builders)
  model/    Post, Comment, User (Jackson records)
  support/  RetryAnalyzer, RetryListener, TestData (data providers)
  tests/    BaseApiTest, PostsTest
src/test/resources/schemas/   JSON Schema files used by the contract tests
.github/workflows/ci.yml      push, PR and weekly run; uploads the surefire reports
```

## CI

GitHub Actions: Temurin 17, `mvn -B verify`, surefire reports uploaded as an artifact. It also runs every Monday so I notice if the public API changes under me.

## License

MIT, see [LICENSE](LICENSE).
