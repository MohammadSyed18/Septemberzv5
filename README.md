# septemberzv5

a small Spring Boot service for campus announcements. checks deadlines and duplicate title/audience pairs, fills templates, and produces text previews.

```sh
mvn test
mvn spring-boot:run
```

```sh
curl -X POST localhost:8080/api/announcements \
  -H 'Content-Type: application/json' \
  --data @demo/announcement.json
```

uses in-memory data and a synthetic example notice. authentication, moderation, and durable storage aren't implemented yet.

## Implementation details

The announcement service supports an injectable clock, active-deadline filtering, withdrawal, and audience validation at the service boundary.

## Draft review workflow

[DraftWorkflow](src/main/java/dev/septemberz/DraftWorkflow.java) adds draft, in-review, approved, rejected, published, and withdrawn states with optimistic versions and immutable review events. Authors cannot approve their own drafts. Revisions return content to draft state; publication failures leave review state and events intact.

[DraftController](src/main/java/dev/septemberz/DraftController.java) exposes `/api/drafts`, `PUT /api/drafts/{id}`, `/api/drafts/{id}/actions`, and `/api/drafts/{id}/events`. Actions use `{version, action, actor, note}`. Create requests use `{notice, author}`. Supported actions are `submit`, `approve`, `reject`, `publish`, and `withdraw`.

The existing direct announcement endpoint remains a simple demo interface. Actor names are supplied by callers; the workflow is not an authenticated authorization system. State remains in memory. Tests verify versions, self-approval rejection, publication rollback, and withdrawal.
