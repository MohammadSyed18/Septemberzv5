# Septemberzv5

A small Spring Boot service for drafting and organizing campus announcements. It validates deadlines, prevents duplicate audience/title pairs, filters notices, and produces a plain-text preview.

Run `mvn test`, then `mvn spring-boot:run`. Create a notice with `curl -X POST localhost:8080/api/announcements -H 'Content-Type: application/json' --data @demo/announcement.json`.

Data is in memory and the included notice is synthetic. Authentication, moderation workflows, and durable storage are not implemented yet.
