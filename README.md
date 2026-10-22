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
