package com.example.demo.repository;

import com.example.demo.entity.Document;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
  List<Document> findByTranscriptId(UUID transcriptId);

  List<Document> findByGraduationListId(UUID graduationListId);

  List<Document> findByDocumentType(String documentType);
}
