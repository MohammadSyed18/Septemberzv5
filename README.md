# Septemberzv5

A small Spring Boot service for campus announcements. Checks deadlines and duplicate title/audience pairs, fills templates, and produces text previews.

```sh
mvn test
mvn spring-boot:run
```

```sh
curl -X POST localhost:8080/api/announcements \
  -H 'Content-Type: application/json' \
  --data @demo/announcement.json
```

Uses in-memory data and a synthetic example notice. Authentication, moderation, and durable storage aren't implemented yet.
