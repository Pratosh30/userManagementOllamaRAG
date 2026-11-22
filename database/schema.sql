-- MySQL 8+ (InnoDB, utf8mb4)
CREATE TABLE app_users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(30) NOT NULL DEFAULT 'USER',
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_app_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE password_reset_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token_hash VARCHAR(64) NOT NULL,
  expires_at DATETIME(6) NOT NULL,
  used_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_reset_token_hash UNIQUE (token_hash),
  CONSTRAINT fk_reset_tokens_user FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX idx_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX idx_reset_tokens_expiry ON password_reset_tokens(expires_at);

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
