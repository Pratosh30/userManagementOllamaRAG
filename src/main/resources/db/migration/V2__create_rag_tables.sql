-- No pgvector needed: embeddings are stored as packed float32 bytes (LONGBLOB)
-- and similarity is computed in the application.
CREATE TABLE rag_documents (
  id BIGINT NOT NULL AUTO_INCREMENT,
  owner_email VARCHAR(255) NOT NULL,
  filename VARCHAR(255) NOT NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  CONSTRAINT fk_rag_documents_owner FOREIGN KEY (owner_email) REFERENCES app_users(email) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE rag_chunks (
  id BIGINT NOT NULL AUTO_INCREMENT,
  document_id BIGINT NOT NULL,
  owner_email VARCHAR(255) NOT NULL,
  chunk_index INT NOT NULL,
  content LONGTEXT NOT NULL,
  embedding LONGBLOB NOT NULL,
  embedding_model VARCHAR(100) NOT NULL,
  embedding_dim INT NOT NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_rag_chunk UNIQUE (document_id, chunk_index),
  CONSTRAINT fk_rag_chunks_document FOREIGN KEY (document_id) REFERENCES rag_documents(id) ON DELETE CASCADE,
  CONSTRAINT fk_rag_chunks_owner FOREIGN KEY (owner_email) REFERENCES app_users(email) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX idx_rag_documents_owner ON rag_documents(owner_email);
CREATE INDEX idx_rag_chunks_owner ON rag_chunks(owner_email, embedding_model);
