package com.realtors.springboot;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/box")
public class BoxController {

    private final BoxService boxService;

    public BoxController(BoxService boxService) {
        this.boxService = boxService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("folderId") String folderId,
                             @RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);
            String fileId = boxService.uploadFile(folderId, tempFile.getAbsolutePath(), file.getOriginalFilename());
            tempFile.delete();
            return fileId;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Box", e);
        }
    }

    @GetMapping("/list/{folderId}")
    public List<String> listFolderItems(@PathVariable String folderId) {
        return boxService.listFolderItems(folderId);
    }

    @GetMapping("/download/{fileId}")
    public void downloadFile(@PathVariable String fileId, @RequestParam("destination") String destination) {
        boxService.downloadFile(fileId, destination);
    }
}