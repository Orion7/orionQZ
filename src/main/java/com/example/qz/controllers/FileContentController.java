package com.example.qz.controllers;

import java.io.IOException;
import java.util.Optional;

import com.example.qz.entities.File;
import com.example.qz.repositories.FileContentStore;
import com.example.qz.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileContentController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileContentStore contentStore;

    @RequestMapping(value = "/files/{fileId}", method = RequestMethod.PUT)
    public ResponseEntity<?> setContent(@PathVariable("fileId") Long id, @RequestParam("file") MultipartFile file)
        throws IOException {

        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isPresent()) {
            optionalFile.get().setMimeType(file.getContentType());

            contentStore.setContent(optionalFile.get(), file.getInputStream());

            // save updated content-related info
            fileRepository.save(optionalFile.get());

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return null;
    }

    @RequestMapping(value = "/files/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<?> getContent(@PathVariable("fileId") Long id) {

        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isPresent()) {
            InputStreamResource inputStreamResource =
                new InputStreamResource(contentStore.getContent(optionalFile.get()));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(optionalFile.get().getContentLength());
            headers.set("Content-Type", optionalFile.get().getMimeType());
            return new ResponseEntity<Object>(inputStreamResource, headers, HttpStatus.OK);
        }
        return null;
    }
}
