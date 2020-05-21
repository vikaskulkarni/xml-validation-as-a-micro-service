package com.validate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class ResourceFileReader {
    @Autowired
    ResourceLoader resourceLoader;

    public File getResourceFile(String fileName) throws IOException {
        return getResource(fileName).getFile();
    }

    public InputStream getResourceFileStream(String fileName) throws IOException {
        return getResource(fileName).getInputStream();
    }

    public String readFileUsingStream(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public String getResourceFilePath(String fileName) throws IOException {
        return getResourceFile(fileName).getPath();
    }

    private Resource getResource(String fileName) {
        return resourceLoader.getResource((new StringBuilder("classpath:").append(fileName)).toString());
    }
}
