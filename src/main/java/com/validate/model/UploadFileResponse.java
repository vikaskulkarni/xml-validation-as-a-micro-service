package com.validate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class UploadFileResponse {
	private String fileName;
	private String fileDownloadUri;
	private String fileType;
	private long size;
	private boolean valid;
}
