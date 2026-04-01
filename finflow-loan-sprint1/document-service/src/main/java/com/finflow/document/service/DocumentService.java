package com.finflow.document.service;

import com.finflow.document.dto.DocumentResponse;
import com.finflow.document.entity.DocumentEntity;
import com.finflow.document.repository.DocumentRepository;
import com.finflow.document.exception.ResourceNotFoundException;
import com.finflow.document.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentResponse uploadDocument(String username, Long loanId, MultipartFile file) throws IOException {
        DocumentEntity entity = DocumentEntity.builder()
                .username(username)
                .loanId(loanId)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .data(file.getBytes())
                .build();
        entity = documentRepository.save(entity);
        return mapToResponse(entity);
    }

    public List<DocumentResponse> getDocumentsForLoan(Long loanId) {
        return documentRepository.findByLoanId(loanId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public DocumentEntity downloadDocument(Long documentId) {
        return documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
    }

    public void deleteDocument(Long documentId, String username) {
        DocumentEntity entity = documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        if (!entity.getUsername().equals(username)) throw new UnauthorizedException("Unauthorized access to document");
        documentRepository.delete(entity);
    }

    public DocumentResponse replaceDocument(Long documentId, String username, MultipartFile file) throws IOException {
        DocumentEntity entity = documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        if (!entity.getUsername().equals(username)) throw new UnauthorizedException("Unauthorized access to document");
        entity.setFileName(file.getOriginalFilename());
        entity.setFileType(file.getContentType());
        entity.setData(file.getBytes());
        return mapToResponse(documentRepository.save(entity));
    }


    public List<DocumentResponse> getMyDocuments(String username) {
        return documentRepository.findByUsername(username).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private DocumentResponse mapToResponse(DocumentEntity entity) {
        DocumentResponse res = new DocumentResponse();
        res.setId(entity.getId());
        res.setLoanId(entity.getLoanId());
        res.setFileName(entity.getFileName());
        res.setFileType(entity.getFileType());
        return res;
    }
}
