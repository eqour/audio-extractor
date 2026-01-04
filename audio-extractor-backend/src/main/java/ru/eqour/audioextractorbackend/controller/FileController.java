package ru.eqour.audioextractorbackend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.eqour.audioextractorbackend.model.ConversionStatus;
import ru.eqour.audioextractorbackend.model.FileModel;
import ru.eqour.audioextractorbackend.service.AudioExtractorService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FileController {

    private final AudioExtractorService audioExtractorService;

    public FileController(AudioExtractorService audioExtractorService) {
        this.audioExtractorService = audioExtractorService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestPart("file") MultipartFile file,
                                                          @RequestPart("userId") String userId) throws IOException {
        String id = audioExtractorService.startFileConversion(userId, new FileModel(file.getOriginalFilename(),
                file.getBytes()));
        audioExtractorService.extractAudio(id);
        Map<String, String> response = new HashMap<>();
        response.put("id", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) {
        FileModel file = audioExtractorService.getAudioFile(id);
        String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
        String headerValue = "attachment; filename=" + encodedFileName;
        return ResponseEntity.ok()
                .contentLength(file.getBytes().length)
                .header("Content-Disposition", headerValue)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file.getBytes());
    }

    @GetMapping("/user/{id}/statuses")
    public List<ConversionStatus> getAllStatuses(@PathVariable String id) {
        return audioExtractorService.getAllStatuses(id);
    }

    @GetMapping("/statuses/{id}")
    public ConversionStatus getStatus(@PathVariable String id) {
        return audioExtractorService.getStatus(id);
    }
}
