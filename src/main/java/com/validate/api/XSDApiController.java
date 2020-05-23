package com.validate.api;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.validate.services.ResourceFileReader;

@RestController
public class XSDApiController implements XSDApi {

	private static final Logger logger = LoggerFactory.getLogger(XSDApiController.class);

	@Autowired
	private ResourceFileReader resourceFileReader;

	@Override
	public ResponseEntity<ArrayList<String>> getXsdFiles() {
		logger.info("Getting XSD Files List");

		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/json")).body(resourceFileReader.getXsdFilesList());
	}

}
