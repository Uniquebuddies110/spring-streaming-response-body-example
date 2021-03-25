package com.dtech.api.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api")
public class DownloadController {

	final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

	@GetMapping("/download")
	public ResponseEntity<StreamingResponseBody> downloadZipFile(HttpServletResponse response) {

		LOGGER.info("Downloading starts...");
		response.setContentType("application/zip");
		response.setHeader("Content-Desposition", "attachment;filename=sample.zip");

		StreamingResponseBody stream = out -> {

			final String home = System.getProperty("user.home");
			final File directory = new File(home + File.separator + "Documents" + File.separator + "sample");
			LOGGER.info("directory{} ", directory);
			final ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

			if (directory.exists() && directory.isDirectory()) {
				try {
					for (final File file : directory.listFiles()) {
						final InputStream inputStream = new FileInputStream(file);
						final ZipEntry zipEntry = new ZipEntry(file.getName());
						zipOutputStream.putNextEntry(zipEntry);

						byte[] bytes = new byte[1024];
						int length;
						while ((length = inputStream.read(bytes)) >= 0) {
							zipOutputStream.write(bytes, 0, length);
						}
						inputStream.close();
					}
					zipOutputStream.close();
				} catch (final IOException e) {
					LOGGER.error("Exception while reading and streaming data {} ", e);
				}
			}
		};
		LOGGER.info("Streaming Response{} ", stream);
		return ResponseEntity.ok(stream);
	}

	@GetMapping("/video")
	public ResponseEntity<InputStreamResource> streamVideo(HttpServletResponse response) throws FileNotFoundException {
		File file = new File("E:/ajay/Videos/samantha.mp4");
		InputStream inputStream = new FileInputStream(file);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept-Ranges", "bytes");
		headers.set("Content-Type", "video/mp4");
		headers.set("Content-Range", "bytes 50-1025/17839845");
		headers.set("Content-Length", String.valueOf(file.length()));

//		return ResponseEntity.ok(new InputStreamResource(inputStream));
		return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
	}

}
