package ru.eqour.audioextractorbackend.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.eqour.audioextractorbackend.model.ConversionStage;
import ru.eqour.audioextractorbackend.model.ConversionStatus;
import ru.eqour.audioextractorbackend.model.FileModel;
import ru.eqour.audioextractorbackend.model.ProcessingConversionStatus;
import ru.eqour.audioextractorbackend.service.AudioExtractorService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class AudioExtractorServiceImpl implements AudioExtractorService {

    private final Map<String, List<String>> userFilesMap = new HashMap<>();
    private final Map<String, FileModel> inputFilesMap = new HashMap<>();
    private final Map<String, FileModel> outputFilesMap = new HashMap<>();
    private final Map<String, Integer> conversionProgressMap = new ConcurrentHashMap<>();

    @Override
    public String startFileConversion(String userId, FileModel file) {
        if (!userFilesMap.containsKey(userId)) userFilesMap.put(userId, new ArrayList<>());
        String fileId = UUID.randomUUID().toString();
        inputFilesMap.put(fileId, file);
        userFilesMap.get(userId).add(fileId);
        return fileId;
    }

    @Override
    public List<ConversionStatus> getAllStatuses(String userId) {
        if (!userFilesMap.containsKey(userId)) return new ArrayList<>();
        List<ConversionStatus> result = new ArrayList<>();
        for (String fileId : userFilesMap.get(userId)) {
            FileModel file = inputFilesMap.get(fileId);
            if (file == null) continue;
            Integer progress = conversionProgressMap.get(fileId);
            result.add(getStatus(fileId, file.getName(), progress));
        }
        return result;
    }

    @Override
    public ConversionStatus getStatus(String fileId) {
        FileModel file = inputFilesMap.get(fileId);
        if (file == null) return null;
        Integer progress = conversionProgressMap.get(fileId);
        return getStatus(fileId, file.getName(), progress);
    }

    private ConversionStatus getStatus(String fileId, String fileName, Integer progress) {
        ConversionStage stage = getStage(progress);
        if (ConversionStage.PROCESSING == stage) return new ProcessingConversionStatus(fileId, fileName, progress);
        return new ConversionStatus(fileId, fileName, stage);
    }

    private ConversionStage getStage(Integer progress) {
        if (progress == null) return ConversionStage.PENDING;
        return progress == 100 ? ConversionStage.COMPLETED : ConversionStage.PROCESSING;
    }

    @Override
    public FileModel getAudioFile(String fileId) {
        return outputFilesMap.get(fileId);
    }

    @Override
    @Async("extractAudioTaskExecutor")
    public CompletableFuture<Void> extractAudio(String fileId) {
        FileModel fileModel = inputFilesMap.get(fileId);
        byte[] audioFileBytes = extractAudioToMp3(fileModel.getBytes(),
                percent -> conversionProgressMap.put(fileId, percent));
        String newName = fileModel.getName().substring(0, fileModel.getName().length() - 1) + "3";
        outputFilesMap.put(fileId, new FileModel(newName, audioFileBytes));
        return CompletableFuture.completedFuture(null);
    }

    private byte[] extractAudioToMp3(byte[] mp4Data, Consumer<Integer> progressCallback) {
        for (int i = 0; i <= 100; i += 5) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            progressCallback.accept(i);
        }
        return mp4Data;
    }
}
