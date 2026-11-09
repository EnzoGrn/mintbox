# API contract

[`openapi.json`](openapi.json) is the OpenAPI description of the backend's HTTP API, as the
backend serves it at `/v3/api-docs`. It is the contract between the backend and the
frontend:

- **The backend checks it.** `OpenApiContractTest` fails the build if the API no longer
  matches this file. After changing an endpoint, regenerate it from `backend/`:

  ```bash
  ./mvnw test -Dtest=OpenApiContractTest -Dmintbox.openapi.update=true
  ```

- **The frontend is typed from it.** The frontend generates its TypeScript types from this
  file, so a field renamed in the backend breaks the frontend build instead of the page.
  After changing this file, regenerate them from `frontend/`, and commit them with it:

  ```bash
  npm run api:types
  ```

  The CI checks that the committed types match this file.

Because the file is committed, every change of contract shows up in the pull request that
makes it.

To read the API comfortably, run the backend and open Swagger UI at
<http://localhost:8080/swagger-ui.html>.
