package ru.eqour.audioextractorbackend.service;

import ru.eqour.audioextractorbackend.model.ConversionStatus;
import ru.eqour.audioextractorbackend.model.FileModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AudioExtractorService {

    String startFileConversion(String userId, FileModel file);
    List<ConversionStatus> getAllStatuses(String userId);
    ConversionStatus getStatus(String fileId);
    FileModel getAudioFile(String fileId);
    CompletableFuture<Void> extractAudio(String fileId);
}
