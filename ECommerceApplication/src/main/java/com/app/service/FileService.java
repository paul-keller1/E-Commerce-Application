package com.app.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {



	/*@
		public normal_behavior
		  requires path != null;
		  requires file != null;
		  requires file.getOriginalFilename() != null;
		  ensures \result != null;

		also


		public exceptional_behavior
		  requires path != null;
		  requires file != null;
		  requires file.getOriginalFilename() != null;
		  signals (APIException e) true;
	@*/
	String uploadImage(String path, MultipartFile file) throws IOException;

	/*@
		public normal_behavior
		  requires path != null;
		  requires fileName != null;
		  ensures \result != null;

		also


		public exceptional_behavior
		  requires path != null;
		  requires fileName != null;
		  signals (FileNotFoundException e) true;

	@*/
	InputStream getResource(String path, String fileName) throws FileNotFoundException;
	
}
