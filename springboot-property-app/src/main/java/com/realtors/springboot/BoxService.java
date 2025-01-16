package com.realtors.springboot;

import com.box.sdk.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoxService {

    private static final String CLIENT_ID = "9hy239p6z48z2j4qe3ssgi0ncl5wnriu";
    private static final String CLIENT_SECRET = "Setfbxo95M4BCWGXyVNiP4MWf8878CVV";
    private static final String DEVELOPER_TOKEN = "MpZRAMo754cjOJ2Ol9FXwC2uGgf3L0ml";

    private final BoxAPIConnection api;

    public BoxService() {
        this.api = new BoxAPIConnection(DEVELOPER_TOKEN);
    }

    public String uploadFile(String folderId, String filePath, String fileName) {
        try {
            BoxFolder folder = new BoxFolder(api, folderId);
            try (InputStream stream = new FileInputStream(filePath)) {
                BoxFile.Info fileInfo = folder.uploadFile(stream, fileName);
                return fileInfo.getID();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Box", e);
        }
    }

    public List<String> listFolderItems(String folderId) {
        try {
            BoxFolder folder = new BoxFolder(api, folderId);
            List<String> items = new ArrayList<>();
            for (BoxItem.Info itemInfo : folder) {
                items.add(itemInfo.getName());
            }
            return items; // Return the List<String> directly
        } catch (BoxAPIException e) {
            throw new RuntimeException("Box API Error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during listing items: " + e.getMessage(), e);
        }
    }

    public void downloadFile(String fileId, String destinationPath) {
        try {
            BoxFile file = new BoxFile(api, fileId);
            try (FileOutputStream outputStream = new FileOutputStream(destinationPath + "/" + file.getInfo().getName())) {
                file.download(outputStream);
            } catch (IOException e) {
                throw new RuntimeException("Error writing file to destination: " + e.getMessage(), e);
            }
        } catch (BoxAPIException e) {
            throw new RuntimeException("Box API Error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during download: " + e.getMessage(), e);
        }
    }
}