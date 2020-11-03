package com.trunkfit.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    public void uploadFile(MultipartFile file) {

//        try {
//            Path copyLocation = Paths
//                .get(uploadDir + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
//            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new FileStorageException("Could not store file " + file.getOriginalFilename()
//                + ". Please try again!");
//        }
    }
}