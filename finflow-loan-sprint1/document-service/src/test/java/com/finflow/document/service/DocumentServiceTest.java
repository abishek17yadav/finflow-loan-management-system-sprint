package com.finflow.document.service;

import com.finflow.document.dto.DocumentResponse;
import com.finflow.document.entity.DocumentEntity;
import com.finflow.document.repository.DocumentRepository;
import com.finflow.document.exception.ResourceNotFoundException;
import com.finflow.document.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private DocumentService documentService;

    private DocumentEntity testEntity;
    private final String TEST_USERNAME = "testuser";
    private final Long TEST_LOAN_ID = 100L;

    @BeforeEach
    void setUp() {
        testEntity = DocumentEntity.builder()
                .id(1L)
                .username(TEST_USERNAME)
                .loanId(TEST_LOAN_ID)
                .fileName("test-doc.pdf")
                .fileType("application/pdf")
                .data(new byte[]{1, 2, 3})
                .build();
    }

    @Test
    void testUploadDocument() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("test-doc.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(testEntity);

        DocumentResponse response = documentService.uploadDocument(TEST_USERNAME, TEST_LOAN_ID, multipartFile);

        assertNotNull(response);
        assertEquals(testEntity.getId(), response.getId());
        assertEquals(testEntity.getFileName(), response.getFileName());
        assertEquals(TEST_LOAN_ID, response.getLoanId());

        verify(documentRepository, times(1)).save(any(DocumentEntity.class));
    }

    @Test
    void testGetDocumentsForLoan() {
        when(documentRepository.findByLoanId(TEST_LOAN_ID)).thenReturn(Collections.singletonList(testEntity));

        List<DocumentResponse> documents = documentService.getDocumentsForLoan(TEST_LOAN_ID);

        assertNotNull(documents);
        assertEquals(1, documents.size());
        assertEquals(testEntity.getFileName(), documents.get(0).getFileName());

        verify(documentRepository, times(1)).findByLoanId(TEST_LOAN_ID);
    }

    @Test
    void testGetMyDocuments() {
        when(documentRepository.findByUsername(TEST_USERNAME)).thenReturn(Collections.singletonList(testEntity));

        List<DocumentResponse> documents = documentService.getMyDocuments(TEST_USERNAME);

        assertNotNull(documents);
        assertEquals(1, documents.size());
        assertEquals(testEntity.getId(), documents.get(0).getId());

        verify(documentRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void testDelete_NotFound() {
        when(documentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            documentService.deleteDocument(1L, TEST_USERNAME);
        });
    }

    @Test
    void testDelete_Unauthorized() {
        testEntity.setUsername("otheruser");
        when(documentRepository.findById(anyLong())).thenReturn(Optional.of(testEntity));

        assertThrows(UnauthorizedException.class, () -> {
            documentService.deleteDocument(1L, TEST_USERNAME);
        });
    }
}
