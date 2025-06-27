# Generative AI Recommender

This is a backend for a Generative AI recommender that includes:
- PostgresML
- PGVector
- Spring Boot and AI

## Structure of the Project

Project has two endpoints:
- `/ingest` where receives a message and put it into a Kafka topic
- `/recommend` where serves a prompt and query similar data in vector store by considering `cosine distance`

And a Kafka listener which listens to a topic for messages for ingestion and generate an embedding for storing in Vectore store.
Vector store uses PostgresML to generate the embeddings of the messages and store it in Postgres Database.

### TODO
- [ ] add tests
- [ ] dockerize project
- [ ] github actions
- [ ] architecture diagram
