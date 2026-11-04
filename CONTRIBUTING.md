# Contributing

Mintbox is a personal portfolio project, but it follows the workflow of a team project:
every change goes through a branch and a pull request, even when I'm the only one working
on it.

## Branches

- `main` always builds and passes its tests. Nobody pushes to it directly.
- Each change gets its own short-lived branch, named after its type and topic:

  | Prefix | For |
  |--------|-----|
  | `feat/` | A new capability, for example `feat/catalog-search` |
  | `fix/` | A bug fix |
  | `docs/` | Documentation only, including ADRs |
  | `build/`, `ci/` | Build or CI changes |
  | `refactor/`, `test/`, `chore/` | Everything else |

- A branch matches one step of a milestone, small enough to review in 15 minutes.

## Commits

Commit messages follow the
[Karma convention](http://karma-runner.github.io/latest/dev/git-commit-msg.html):

```
<type>(<scope>): <subject>
```

- **type**: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci` or
  `chore`.
- **scope**: the module or area, such as `catalog`, `shared`, `adr`, `backend`, `infra`.
- **subject**: imperative, lower case, no final period, for example
  `feat(catalog): add typo-tolerant search`.

A pull request may contain several commits, each one meaningful on its own: they are kept
as they are when merging.

## Pull requests

1. Create the branch from an up-to-date `main`: `git switch main`, `git pull`,
   `git switch -c feat/my-topic`.
2. Commit, push with `git push -u origin feat/my-topic`, and open the pull request.
3. Fill in the template: what changed, why, and the
   [definition of done](docs/definition-of-done.md) checklist.
4. Merge with **Rebase and merge** once the CI is green, so that `main` keeps a linear
   history of meaningful commits. Then delete the branch.

## Continuous integration

The [CI workflow](.github/workflows/ci.yml) only runs the jobs whose area changed: the
backend build and tests when `backend/` changes, and a start of the local stack with an S3
smoke test when `compose.yaml` or `infra/` changes. Its **CI result** job always runs and
fails if any other job failed: it is the only check that branch protection requires.

## Running the project

See the [README](README.md#getting-started) and the [backend README](backend/README.md).
