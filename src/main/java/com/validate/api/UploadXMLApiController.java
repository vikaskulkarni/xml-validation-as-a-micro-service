package com.validate.api;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.validate.model.ValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.xml.sax.SAXException;

import com.validate.model.UploadFileResponse;
import com.validate.services.FileStorageService;
import com.validate.services.ResourceFileReader;

import io.swagger.annotations.ApiParam;

@RestController
public class UploadXMLApiController implements UploadXMLApi {

	private static final Logger logger = LoggerFactory.getLogger(UploadXMLApiController.class);

	@Autowired
	private FileStorageService fileStorageService;

	@Override
	public UploadFileResponse uploadSingleXML(@Valid @RequestPart("file") MultipartFile xmlFile,
			@RequestParam(value = "note", required = false) String note, @PathParam(value = "xsdFileName") String xsdFileName) {
		String xmlFileName = fileStorageService.storeFile(xmlFile);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(xmlFileName).toUriString();
		ValidationResponse validationResponse = fileStorageService.validateXMLSchema(xmlFileName, xsdFileName);

		return new UploadFileResponse(xmlFileName, fileDownloadUri, xmlFile.getContentType(), xmlFile.getSize(), validationResponse);
	}

	@Override
	public URL uploadXMLAsync(@ApiParam(value = "file detail") @Valid @RequestPart("file") MultipartFile xmlFile,
			@ApiParam(value = "Description of file contents.") @RequestParam(value = "note", required = false) String note)
			throws MalformedURLException {

		String uniqueID = UUID.randomUUID().toString();
		/*
		 * String getTokensUri =
		 * ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploadStatus/")
		 * .path(uniqueID).toUriString();
		 */
		URL getXMLUri = new URL("http://localhost:10003/" + uniqueID);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			fileStorageService.storeFile(xmlFile);
		});

		return getXMLUri;
	}

	@Override
	public List<UploadFileResponse> uploadMultipleXMLFiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.asList(files).stream().map(file -> uploadSingleXML(file, "", "")).collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
