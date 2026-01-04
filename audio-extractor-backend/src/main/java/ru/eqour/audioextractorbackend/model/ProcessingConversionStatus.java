package ru.eqour.audioextractorbackend.model;

public class ProcessingConversionStatus extends ConversionStatus {

    private int progress;

    public ProcessingConversionStatus() {
    }

    public ProcessingConversionStatus(String fileId, String fileName, int progress) {
        super(fileId, fileName, ConversionStage.PROCESSING);
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
