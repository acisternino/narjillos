package org.nusco.narjillos.serializer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.nusco.narjillos.experiment.Experiment;
import org.nusco.narjillos.genomics.DNA;
import org.nusco.narjillos.genomics.GenePool;

public class Persistence {

	private static final String EXPERIMENT_EXT = ".exp";
	private static final String PETRIDISH_EXT = ".petridish";
	private static final String ANCESTRY_EXT = ".ancestry";
	private static final String TEMP_EXT = ".tmp";

	public static void save(Experiment experiment) {
		try {
			String tempFileName = experiment.getId() + TEMP_EXT;
			ZipOutputStream zipFile = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File(tempFileName))));
			writeZipEntry(zipFile, experiment.getId() + PETRIDISH_EXT, JSON.toJson(experiment, Experiment.class));
			writeZipEntry(zipFile, experiment.getId() + ANCESTRY_EXT, JSON.toJson(experiment.getGenePool(), GenePool.class));
			zipFile.close();

			forceMoveFile(tempFileName, experiment.getId() + EXPERIMENT_EXT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Experiment loadExperimentWithGenePool(String fileName) {
		Experiment experiment;
		GenePool genePool;
		
		try {
			checkVersion(fileName);

			ZipFile zipFile = new ZipFile(new File(fileName));

			String experimentFileName = stripExtension(fileName) + PETRIDISH_EXT;
			experiment = JSON.fromJson(readEntry(zipFile, experimentFileName), Experiment.class);
			
			String ancestryFileName = stripExtension(fileName) + ANCESTRY_EXT;
			genePool = JSON.fromJson(readEntry(zipFile, ancestryFileName), GenePool.class);

			zipFile.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		experiment.timeStamp();
		experiment.setGenePool(genePool);
		return experiment;
	}

	public static DNA loadDNA(String fileName) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(fileName));
			String dnaDocument = new String(encoded, Charset.defaultCharset());
			return new DNA(dnaDocument);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String readApplicationVersion() {
		try {
			return Files.readAllLines(Paths.get("version")).get(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void checkVersion(String fileName) {
		String fileNameWithoutExtension = stripExtension(fileName);
		if (!fileNameWithoutExtension.matches("\\d+\\-\\d+\\.\\d+.\\d+")) {
			System.out.println("WARNING: This experiment doesn't contain a version in the filename. " +
					"I cannot check that it was generated by the same version of Narjillos that you're using now.");
			return;
		}
		
		String experimentVersion = extractVersion(fileNameWithoutExtension);
		String applicationVersion = readApplicationVersion();
		if (!experimentVersion.equals(applicationVersion))
			System.out.println("WARNING: This experiment was started with version " + experimentVersion + ", not the current "
					+ applicationVersion + ". The results might be non-deterministic.");
	}

	private static void writeZipEntry(ZipOutputStream zip, String fileName, String data) throws IOException {
		byte[] dataBytes = data.getBytes(Charset.forName("UTF-8"));
		zip.putNextEntry(new ZipEntry(fileName));
		zip.write(dataBytes, 0, dataBytes.length);
		zip.closeEntry();
	}

	private static String readEntry(ZipFile zipFile, String name) throws IOException {
		ZipEntry zipEntry = zipFile.getEntry(name);
		InputStream inputStream = zipFile.getInputStream(zipEntry);
		// From "Stupid Scanner Tricks", weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
		Scanner s = new Scanner(inputStream);
		s.useDelimiter("\\A");
		String result = s.next();
		s.close();
		inputStream.close();
		return result;
	}

	private static String extractVersion(String fileName) {
		return fileName.substring(fileName.indexOf("-") + 1);
	}

	private static String stripExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}

	private static void forceMoveFile(String source, String destination) throws IOException {
		Path filePath = Paths.get(destination);
		if (Files.exists(filePath))
			Files.delete(filePath);
		Files.move(Paths.get(source), filePath);
	}
}
