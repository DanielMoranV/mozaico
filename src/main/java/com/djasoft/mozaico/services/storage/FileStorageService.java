package com.djasoft.mozaico.services.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String store(MultipartFile file);

    void delete(String filename);

    String getFileUrl(String filename);
}
