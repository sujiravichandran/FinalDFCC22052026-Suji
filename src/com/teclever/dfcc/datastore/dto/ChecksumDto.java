package com.teclever.dfcc.datastore.dto;

public class ChecksumDto {
    private String fileName;
    private String filePath;
    private String checksum;
    private boolean singleChecksum; 

    public ChecksumDto(String fileName, String filePath, String checksum, boolean singleChecksum) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.checksum = checksum;
        this.singleChecksum = singleChecksum;
    }
    
    public ChecksumDto(String fileName, String filePath, String checksum) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.checksum = checksum;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public boolean isSingleChecksum() {
        return singleChecksum;
    }

    public void setSingleChecksum(boolean singleChecksum) {
        this.singleChecksum = singleChecksum;
    }
}

