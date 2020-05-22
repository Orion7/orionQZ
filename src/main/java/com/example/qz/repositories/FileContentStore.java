package com.example.qz.repositories;

import com.example.qz.entities.File;
import org.springframework.content.commons.repository.ContentStore;

public interface FileContentStore extends ContentStore<File, String> {
}
