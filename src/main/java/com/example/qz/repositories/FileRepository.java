package com.example.qz.repositories;

import com.example.qz.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="files", collectionResourceRel="files")
public interface FileRepository extends JpaRepository<File, Long> {

}
