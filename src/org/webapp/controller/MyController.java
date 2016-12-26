package org.webapp.controller;

import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.webapp.service.Constants;
import org.webapp.service.Message;
import org.webapp.service.Status;

import au.com.bytecode.opencsv.CSVReader;

@Controller
@RequestMapping("/")
public class MyController {

	static final Logger logger = LogManager.getLogger(MyController.class.getName());

	@Autowired
	Status status;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	void setStatus(String msg) {
		status.setMessage(msg);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String viewApplication() {
		return "index";
	}

	public void sleepIntentionally() {
		try {
			// sleeping the process to get to know the difference between status
			// in production, remove it
			Thread.sleep(1000);
		} catch (Exception io) {
			logger.error("Ex while sleep", io);
		}
	}

	@RequestMapping(path = "/getRequestHitTime", method = RequestMethod.GET)
	public String getRequestHitTime(ModelMap model) {
		logger.debug("inside getRequestHitTime");
		Date date = new Date(System.currentTimeMillis());
		logger.debug("date: {}", date);
		model.addAttribute("hitTime", date);
		return "first";
	}

	@RequestMapping(path = "/getCSVPage", method = RequestMethod.GET)
	public String getCSVPage(ModelMap model) {
		return "uploadCsv";
	}

	@ResponseBody
	@RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
	public List<String[]> uploadFile(ModelMap model, @RequestParam("file") MultipartFile file,
			@RequestParam("username") String username, HttpServletRequest request) throws IOException {
		if (file.isEmpty()) {
			logger.warn("Empty file");
			status.setMessage(Constants.SELECT_FILE_FIRST);
			status.setStatusChanged(false);
			return null;
		}

		String rootPath = request.getSession().getServletContext().getRealPath("/");
		File dir = new File(rootPath + File.separator + "uploadedfile");
		if (!dir.exists()) {
			dir.mkdirs();
		}

		logger.debug("absolutePath: {} | originalFileName: {}", dir.getAbsolutePath(), file.getOriginalFilename());
		File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
		setStatus(Constants.FILE_UPLOADING);
		sleepIntentionally();
		try {
			try (InputStream is = file.getInputStream();
					BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
				int i;
				while ((i = is.read()) != -1) {
					stream.write(i);
				}
				stream.flush();
			}
		} catch (IOException e) {
			logger.error("Ex: ", e);
			status.setMessage(Constants.FILE_UPLOAD_FAILED);
			status.setStatusChanged(false);
			return null;
		}
		setStatus(Constants.FILE_UPLOAD_SUCCESSFULL);
		sleepIntentionally();
		List<String[]> myEntries = null;
		try (FileReader fileReader = new FileReader(serverFile); CSVReader reader = new CSVReader(fileReader);) {
			setStatus(Constants.FILE_READING);
			sleepIntentionally();
			myEntries = reader.readAll();
		} catch (IOException e) {
			logger.error("Ex: ", e);
		}
		status.setStatusChanged(false);
		return myEntries;
	}

	@MessageMapping("/fileUploadingStatus")
	public void handleWebSocket(Message message) {
		new Thread(new RunProcess(status)).start();
	}

	private class RunProcess implements Runnable {
		private Status status;

		RunProcess(Status status) {
			this.status = status;
			this.status.setMessage(Constants.NO_FILE_SELECTED_YET);
			this.status.setStatusChanged(true);
		}

		public void run() {
			simpMessagingTemplate.convertAndSend("/topic/message", status.getMessage());
			String tempStatus = status.getMessage();
			while (status.isStatusChanged()) {
				if (!tempStatus.equals(status.getMessage())) {
					logger.debug("status changed to -> {}", status.getMessage());
					simpMessagingTemplate.convertAndSend("/topic/message", status.getMessage());
					tempStatus = status.getMessage();
				}
				try {
					Thread.sleep(100);
				} catch (Exception io) {
					logger.error("Ex while sleep", io);
				}
			}
		}
	}
}
