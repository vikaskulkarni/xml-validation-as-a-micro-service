package com.validate.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.validate.config.FileStorageProperties;
import com.validate.exception.FileNotFoundException;
import com.validate.exception.FileStorageException;

@Service
public class FileStorageService {

	@Autowired
	private ResourceFileReader resourceFileReader;
	private final Path fileStorageLocation;

	private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

	public Path getFileStorageLocation() {
		return fileStorageLocation;
	}

	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public String storeFile(MultipartFile file) {
		logger.info("Storing File");
		String uniqueID = UUID.randomUUID().toString();
		String fileName = uniqueID + "_" + StringUtils.cleanPath(file.getOriginalFilename());

		try {
			validateFile(fileName);

			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileName;
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Exception:", ex);
		}
	}

	private void validateFile(String fileName) {
		if (fileName.contains("..")) {
			throw new FileStorageException("Filename contains invalid path: " + fileName);
		}
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new FileNotFoundException("File not found " + fileName, ex);
		}
	}

	public File loadFileFromResource(String fileName) throws IOException {
		return loadFileAsResource(fileName).getFile();
	}

	public boolean validateXMLSchema(String xmlFileName, String xsdFileName) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(resourceFileReader.loadXSDFile(xsdFileName));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(loadFileFromResource(xmlFileName)));
		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
			return false;
		} catch (SAXException e1) {
			System.out.println("SAX Exception: " + e1.getMessage());
			return false;
		}

		return true;
	}
}