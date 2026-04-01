package com.finflow.document.controller;

import com.finflow.document.dto.DocumentResponse;
import com.finflow.document.entity.DocumentEntity;
import com.finflow.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // 1. Upload Doc
    @PostMapping("/upload/{loanId}")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestHeader("loggedInUser") String username,
            @PathVariable Long loanId,
            @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(documentService.uploadDocument(username, loanId, file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 2. Get Loan Docs
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsForLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(documentService.getDocumentsForLoan(loanId));
    }

    // 3. Download Doc
    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(
            @RequestHeader("loggedInUser") String username,
            @PathVariable Long documentId) {
        try {
            DocumentEntity doc = documentService.downloadDocument(documentId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(doc.getFileType()))
                    .body(doc.getData());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. Delete Doc
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(
            @RequestHeader("loggedInUser") String username,
            @PathVariable Long id) {
        documentService.deleteDocument(id, username);
        return ResponseEntity.ok("Document deleted successfully");
    }

    // 5. Replace Doc
    @PutMapping("/{id}/replace")
    public ResponseEntity<DocumentResponse> replaceDocument(
            @RequestHeader("loggedInUser") String username,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(documentService.replaceDocument(id, username, file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 6. List My Docs
    @GetMapping("/my")
    public ResponseEntity<List<DocumentResponse>> getMyDocuments(
            @RequestHeader("loggedInUser") String username) {
        return ResponseEntity.ok(documentService.getMyDocuments(username));
    }
}
