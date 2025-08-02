package com.home.simplewarehouse.patterns.mdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonJmsUtility {
	private static final Logger LOG = LogManager.getLogger(CommonJmsUtility.class);

	private static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	private static final String WIN_ASADMIN = "/asadmin.bat";
	private static final String OTHER_ASADMIN = "/asadmin";
	private static final String JMS_QUEUE_CREATE_CMD = " create-jms-resource --enabled=true --property=Name=Queue1 --restype=javax.jms.Queue queue/Queue1";
	private static final String JMS_TOPIC_CREATE_CMD = " create-jms-resource --enabled=true --property=Name=Topic1 --restype=javax.jms.Topic topic/Topic1";
	private static final String PAYARA_PROP = "payara.home";
	private static final String PAYARA_ENV = "PAYARA_HOME";
	private static final String FALLBACK_PAYARA_PATH = "";

	static {
		ProcessBuilder builder = new ProcessBuilder();

		builder.directory(new File(System.getProperty("user.home")));

		String payaraPath = System.getProperty(PAYARA_PROP, "");

		if (payaraPath.isEmpty()) {
			LOG.warn("Property " + PAYARA_PROP + " is NOT SET or EMPTY");

			payaraPath = System.getenv(PAYARA_ENV);
			if (payaraPath == null || payaraPath.isEmpty()) {
				LOG.warn("Environment var " + PAYARA_ENV + " is NOT SET or EMPTY");

				payaraPath = FALLBACK_PAYARA_PATH;
			}
		}

		// Each command separate is a must
		try {
			if (isWindows) {
			    builder.command("cmd.exe", "/c", payaraPath + "/bin" + WIN_ASADMIN + JMS_QUEUE_CREATE_CMD);
			}
			else {
			    builder.command("sh", "-c", payaraPath + "/bin" + OTHER_ASADMIN + JMS_QUEUE_CREATE_CMD);
			}

			LOG.info("Create queue with command [" + builder.command().toString() + ']');

			Process process = builder.start();
			StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
			Executors.newSingleThreadExecutor().submit(streamGobbler);
			int exitCode = process.waitFor();
			LOG.info("Exit code [" + exitCode + "]");

			if (isWindows) {
				builder.command("cmd.exe", "/c", payaraPath + "/bin" + WIN_ASADMIN + JMS_TOPIC_CREATE_CMD);
			}
			else {
			    builder.command("sh", "-c", payaraPath + "/bin" + OTHER_ASADMIN + JMS_TOPIC_CREATE_CMD);
			}

			LOG.info("Create topic with command [" + builder.command().toString() + ']');

			process = builder.start();
			Executors.newSingleThreadExecutor().submit(streamGobbler);
			exitCode = process.waitFor();
			LOG.info("Exit code [" + exitCode + "]");
		}
		catch (IOException | InterruptedException ex) {
			LOG.error(ex.getMessage());
		}
	}

	private static class StreamGobbler implements Runnable {
	    private InputStream inputStream;
	    private Consumer<String> consumer;

	    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
	        this.inputStream = inputStream;
	        this.consumer = consumer;
	    }

	    @Override
	    public void run() {
	        new BufferedReader(new InputStreamReader(inputStream)).lines()
	          .forEach(consumer);
	    }
	}

	public static void main(String[] args){
	}
}
