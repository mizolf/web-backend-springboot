package com.mcesnik.backend.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfExtractorService {
    private static final long MAX_PDF_SIZE = 10 * 1024 * 1024;

    private void validatePdf(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw new RuntimeException("PDF file is empty.");
        }
        if(file.getSize() > MAX_PDF_SIZE){
            throw new RuntimeException("PDF file exceeds maximum size of 10MB.");
        }
        String contentType = file.getContentType();
        if(contentType == null || !contentType.equals("application/pdf")){
            throw new RuntimeException("File must be PDF.");
        }
    }

    public String extractText(MultipartFile file){
        validatePdf(file);

        try (PDDocument document = Loader.loadPDF(file.getBytes())){
            PDFTextStripper textStripper = new PDFTextStripper();

            String text = textStripper.getText(document);

            if(text == null || text.isBlank()){
                throw new RuntimeException("PDF contains no extractable text");
            }

            if(text.length() > 50000){
                text = text.substring(0, 50000);
            }

            return text;
        } catch (RuntimeException e){
            throw e;
        } catch(Exception e){
            throw new RuntimeException("Failed to extract text from PDF: " + e.getMessage());
        }
    }
}
