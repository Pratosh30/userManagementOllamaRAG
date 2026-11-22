# Spring Boot User Management + Ollama AI

Java 21, Spring Boot 3.4, MySQL 8, Spring Security, BCrypt, JWT, Flyway and Ollama local model integration.

## Prerequisites
- JDK 21 and Maven 3.9+
- MySQL 8+ (no extensions needed; `docker compose up -d` starts one)
- Ollama and both a chat model and an embedding model
- Postman

## Setup
1. Start MySQL (`docker compose up -d`, or use your own server). The default URL has `createDatabaseIfNotExist=true`.
2. `cp .env.example .env` and edit it. The app loads `./.env` automatically on startup.
   Generate the JWT secret with `openssl rand -base64 32`. Never commit `.env`.
3. Start Ollama, then `ollama pull llama3.2` and `ollama pull nomic-embed-text`.
4. Run `mvn clean spring-boot:run`. Flyway creates the schema from `src/main/resources/db/migration`.
   The same SQL is in `database/schema.sql` if you prefer to create the tables by hand.
5. Import `postman/user-management-ollama.postman_collection.json`.

## API
Base URL: `http://localhost:8080/api`
| Method | Path | Auth | Purpose |
|---|---|---|---|
| GET | `/health` | Public | Health |
| POST | `/auth/register` | Public | Register |
| POST | `/auth/login` | Public | Get JWT |
| POST | `/auth/forgot-password` | Public | Request reset |
| POST | `/auth/reset-password` | Reset token | Set new password |
| GET | `/users/me` | Bearer JWT | Current profile |
| PUT | `/users/me` | Bearer JWT | Update name |
| POST | `/ai/query` | Bearer JWT | Direct Ollama prompt (existing endpoint) |
| POST | `/rag/documents/text` | Bearer JWT | Index a plain-text document for the current user |
| POST | `/rag/ask` | Bearer JWT | Retrieve this user's chunks and ask Ollama using that context |

Register body: `{"name":"Test User","email":"test@example.com","password":"StrongPass123!"}`
Login body: `{"email":"test@example.com","password":"StrongPass123!"}`. Use returned `accessToken` as `Authorization: Bearer <token>`.
AI body: `{"prompt":"Explain dependency injection in Spring Boot."}`.
Forgot body: `{"email":"test@example.com"}`. Without SMTP configured, development mode logs the reset token and link to the application console. Paste the token into `resetToken` in Postman.
Reset body: `{"token":"<token>","newPassword":"AnotherStrongPass456!"}`.

## Password reset behavior
Tokens are generated using a secure random generator, stored as SHA-256 hashes, expire after 30 minutes, and are marked used after successful reset. Configure SMTP environment variables to send email in a real deployment. The no-SMTP console link is for local development only.

## Security
- BCrypt hashes passwords; no plaintext passwords are stored.
- JWT is signed and expires after the configured duration.
- `/api/ai/**` and `/api/users/**` require authentication.
- Stateless bearer-token API; CSRF is disabled. Revisit this if switching to cookie-based authentication.
- Restrict Ollama to trusted local access. Add rate limiting, HTTPS, email delivery configuration and operational monitoring before production use.

## Postman checks
Test successful registration/login, duplicate registration (409), wrong password (401), protected API without token (401), AI query with valid token (200), invalid prompt (400), reset token expiry/reuse, and Ollama offline (503).

## RAG (first integration slice)

This version adds a minimal retrieval-augmented generation flow using Ollama embeddings and MySQL. It intentionally starts with text supplied as JSON; PDF/DOCX parsing and multipart uploads are not included yet.

### How it works
1. `POST /api/rag/documents/text` splits submitted text into overlapping chunks.
2. Ollama's `/api/embed` endpoint creates an embedding for each chunk using `OLLAMA_EMBEDDING_MODEL`.
3. Flyway creates `rag_documents` and `rag_chunks`; chunk vectors are stored as float32 bytes (LONGBLOB) with their model name and dimension.
4. `POST /api/rag/ask` embeds the question, scores the user's chunks by cosine similarity in the application and keeps the top 4, builds a grounded prompt, and sends it to the existing Ollama chat service.
5. The API returns the answer and source snippets. Retrieval is filtered by the authenticated user's email so users only search their own ingested documents.

### Test RAG in Postman
1. Register and log in; set the returned JWT in the collection variable `token`.
2. Send the request named `/api/rag/documents/text - ingest text (Bearer JWT)`. Expect `201 Created` and a document ID.
3. Send `/api/rag/ask - question with retrieved context (Bearer JWT)`. The response includes an answer and source references.
4. Try calling either endpoint without a JWT; expect `401 Unauthorized`.

### RAG limitations in this first slice
- Text is supplied in JSON (maximum 200,000 characters); no PDF/DOCX extraction yet.
- Similarity search loads every embedding the user owns and scores it in Java. This is fine for thousands of chunks; for much larger corpora use a dedicated vector store.
- The model can still produce unsupported content; evaluate no-context behavior and retrieval quality before relying on answers.
- Chunks remember the embedding model that produced them; if you change `OLLAMA_EMBEDDING_MODEL`, old chunks are ignored until re-ingested.

### Configuration
- `OLLAMA_BASE_URL=http://localhost:11434`
- `OLLAMA_MODEL=llama3.2`
- `OLLAMA_EMBEDDING_MODEL=nomic-embed-text`

For production, add upload limits, document deletion/re-indexing, stronger retrieval evaluation, rate limiting, monitoring, and an explicit document ownership policy.
