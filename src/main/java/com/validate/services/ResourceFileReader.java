package com.validate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.validate.exception.FileNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.xml.transform.Source;

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

    public ArrayList<String> getXsdFilesList() {

        ArrayList<String> filesList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(getResourceFilePath("schemas")))) {

            stream.forEach(path -> {
                String fileNameWithoutExt = path.getFileName().toString().replaceFirst("[.][^.]+$", "");
                filesList.add(fileNameWithoutExt);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesList;
    }

    public String getResourceFilePath(String fileName) throws IOException {
        return getResourceFile(fileName).getPath();
    }

    private Resource getResource(String fileName) {
        return resourceLoader.getResource((new StringBuilder("classpath:").append(fileName)).toString());
    }

    public File loadXSDFile(String xsdFileName) {
        String fileName = xsdFileName + ".xsd";
        try {
            File xsdFile = getResourceFile("/schemas/" + fileName);
            if (xsdFile.exists()) {
                return xsdFile;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (IOException ex) {
            throw new FileNotFoundException("File not found " + fileName, ex);
        }
    }
}
