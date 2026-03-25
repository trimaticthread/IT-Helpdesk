package com.helpdesk.domain.entity;

import java.time.LocalDateTime;

/**
 * Ticket'lara eklenen dosyalari temsil eder.
 * Kullanicilar ekran goruntusu, log dosyasi gibi dosyalar yukleyebilir.
 * Saf POJO — framework bagimliligi yok.
 *
 * Iliskiler:
 * - Her ek bir ticket'a aittir (N:1)
 * - Her eki bir kullanici yuklemistir (N:1)
 */
public class Attachment {

    private Long id;
    private Ticket ticket;
    private String filename;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private User uploadedBy;
    private LocalDateTime createdAt;

    public Attachment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
