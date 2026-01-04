package ru.eqour.audioextractorbackend.model;

public class ConversionStatus {

    private String fileId;
    private String fileName;
    private ConversionStage stage;

    public ConversionStatus() {
    }

    public ConversionStatus(String fileId, String fileName, ConversionStage stage) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.stage = stage;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ConversionStage getStage() {
        return stage;
    }

    public void setStage(ConversionStage stage) {
        this.stage = stage;
    }
}
