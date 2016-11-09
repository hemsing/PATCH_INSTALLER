package com.bmc.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.bmc.util.misc.OperatingSystem;
import com.bmc.util.misc.SystemProperties;
import com.bmc.util.os.OperatingSystemHelper;

public class GenericFileHelper {
	public static List<String> parseSQLFile(File file, boolean trim, String sqlStatementsDelimeter, int skipBytes,
			List<String> skipLineDelimeter, String comment) throws IOException {
		logger.info("parseSQLFile skipLineDelimeter=" + skipLineDelimeter + " sqlStatementsDelimeter="
				+ sqlStatementsDelimeter);
		List<String> result = new ArrayList<String>();
		FileInputStream fis = null;
		BufferedReader reader = null;
		String sql = "";
		boolean foundDelimeter = false;
		try {
			fis = new FileInputStream(file);
			if (skipBytes > 0) {
				fis.skip(skipBytes);
			}
			reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				if (trim) {
					line = line.trim();
				}

				logger.trace(line);
				if (line.equals(sqlStatementsDelimeter)) {

					foundDelimeter = true;
				}
				boolean shouldBeCommented = false;
				for (String s : skipLineDelimeter) {

					if (s!=null&&!s.trim().isEmpty()&&line.toUpperCase().startsWith(s.toUpperCase()))
					{
						logger.trace(line+" starts with "+s.toUpperCase() +" so commenting");
						shouldBeCommented = true;
					}
				}
				if (shouldBeCommented) {
					// TODO Handle
					// java.sql.SQLException: Invalid SQL type: sqlKind =
					// UNINITIALIZED
					line = comment + line;
					 logger.info("commenting "+line);
					line = "";
				}

				// logger.info(line);

				if (foundDelimeter) {
					
					sql = sql.trim();
					if (sql != null && !sql.isEmpty())
						result.add(sql);
					logger.trace("Found delimeter "+sql);
					foundDelimeter = false;

					sql = "";
				} else {
					sql = sql + "\n" + line;
				}
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable t) {

					// ignore
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Throwable t) {

					// ignore
				}
			}
		}
		logger.trace("Created the SQL file script )\n " + result + "\n");
		logger.info("Created the SQL file script with "+result.size()+" SQL statements");
		return result;
	}

	/**
	 * Creates a directory if it doesn't already exist
	 *
	 * @param directory
	 *            directory to create
	 *
	 * @return true if the directory was created successfully, false otherwise
	 *
	 * @throws IOException
	 *             if any I/O errors occur
	 */

	public static boolean createDirectory(String directory) throws IOException {
		return createDirectory(new File(directory));
	}

	public static boolean createDirectory(File directory) throws IOException {
		boolean result = true;
		if (directory.exists()) {
			if (directory.isDirectory()) {
				result = true;
			} else {
				result = false;
			}
		} else {
			File file = new File(directory, "DummyFile.extension");
			result = createFile(file);
			result &= deleteFile(file);
		}
		return result;
	}

	/**
	 * Creates a File if it doesn't already exist
	 *
	 * @param file
	 *            File to create
	 *
	 * @return true if the File was created successfully, false otherwise
	 *
	 * @throws IOException
	 *             if any I/O errors occur
	 */

	public static boolean createFile(String File) throws IOException {
		return createFile(new File(File));
	}

	public static boolean createFile(File file) throws IOException {
		boolean result = false;
		if (file != null) {
			result = true;
			if (!file.exists()) {
				File parentFile = file.getParentFile();
				if (parentFile != null) {
					if (!parentFile.exists()) {
						result = parentFile.mkdirs();
					}
				}
				if (result) {

					// if file is still deleting is may take a second
					// till it is safe to recreate, so catch an I/O
					// problem and retry create 1 more time after a
					// 1 second pause
					try {
						result = file.createNewFile();
					} catch (IOException ioe) {
						try {
							Thread.sleep(1000);
						} catch (Throwable t) {

							// ignore
						}
						result = file.createNewFile();
					}
				}
			}
		}
		return result;
	}

	public static File getParentDirectory(File file) {
		File parent = file.getParentFile();
		if (parent == null) {
			if (file.isAbsolute()) {
				parent = new File(File.separator);
			} else {
				parent = new File(SystemProperties.getUserDir());
			}
		}
		return parent;
	}

	public static boolean copyFile(String sourceFilePattern, String destinationFile, boolean overwrite, boolean isRegex)
			throws IOException {

		String folderName = sourceFilePattern.substring(0, sourceFilePattern.lastIndexOf("/"));
		String regexPattern = sourceFilePattern.substring(sourceFilePattern.lastIndexOf("/") + 1,
				sourceFilePattern.length());

		logger.info("Found the folder " + folderName + " derived from " + sourceFilePattern
				+ " by getting last index of / Regex pattern is " + regexPattern);

		File root = new File(folderName);
		if (!root.isDirectory()) {
			throw new IllegalArgumentException(root + " is no directory.");
		}
		final Pattern p = Pattern.compile(regexPattern); // careful: could also
															// throw an
															// exception!
		File[] files = root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return p.matcher(file.getName()).matches();
			}
		});

		boolean successFullCopied = true;
		boolean noFilestoCopy = true;
		for (int i = 0; i < files.length; i++) {

			if (!copyFile(files[i], new File(destinationFile + "/" + files[i].getName()), overwrite)) {
				noFilestoCopy = true;
				successFullCopied = false;
			}

		}
		if (successFullCopied == true && noFilestoCopy == false) {
			logger.info("All files matching regex pattern are copied successfully");
		} else if (noFilestoCopy == true) {
			logger.warn("No files matching regex pattern are copied for sourceFilePattern= " + sourceFilePattern
					+ " destinationFile =" + destinationFile);
		}

		return successFullCopied;
	}

	public static boolean copyFile(String sourceFile, String destinationFile, boolean overwrite) throws IOException {
		return copyFile(new File(sourceFile), new File(destinationFile), overwrite);
	}

	public static boolean copyFile(File sourceFile, File destinationFile, boolean overwrite) throws IOException {
		boolean result = false;
		FileInputStream source = null;
		logger.info("Copying File " + sourceFile.getCanonicalPath() + " to " + destinationFile.getCanonicalPath()
				+ " with overwrite set to " + overwrite);

		FileOutputStream destination = null;

		try {

			// first make sure the specified source file
			// exists, is a file, and is readable
			if (!(sourceFile.exists()) || !(sourceFile.isFile())) {
				throw new IOException("No such source file: " + sourceFile.getAbsolutePath());
			}
			if (!(sourceFile.canRead())) {
				throw new IOException("Source file is unreadable: " + sourceFile.getAbsolutePath());
			}

			// If the destination exists, make sure it is a writable file
			// and ask before overwriting it. If the destination doesn't
			// exist, make sure the directory exists and is writable.
			if (destinationFile.exists()) {
				if (destinationFile.isFile()) {
					if (!(destinationFile.canWrite())) {
						throw new IOException("Destination file is unwriteable: " + destinationFile.getAbsolutePath());
					}
				} else {
					throw new IOException("Destination is not a file: " + destinationFile.getAbsolutePath());
				}
			} else {
				File parent = getParentDirectory(destinationFile);
				if (!(parent.exists())) {
					parent.mkdirs();
				}
			}

			// if we've gotten this far, then everything is okay, so we can
			// copy the file
			source = new FileInputStream(sourceFile);
			destination = new FileOutputStream(destinationFile);
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = source.read(buffer)) != -1) {
				destination.write(buffer, 0, bytesRead);
			}
		} finally // no matter what happens, always close any streams we've
					// opened
		{
			if (source != null) {
				try {
					source.close();
				} catch (IOException ioe) {

					// do nothing
				}
			}
			if (destination != null) {
				try {
					destination.close();
				} catch (IOException ioe) {

					// do nothing
				}
			}
		}

		// maintain last modification time if requested to do so
		if (overwrite) {
			long lastModified = sourceFile.lastModified();
			destinationFile.setLastModified(lastModified);
		}
		if (destinationFile.exists()) {
			result = true;
			logger.info("File Copied " + sourceFile.getCanonicalPath() + " to " + destinationFile.getCanonicalPath()
					+ " successful");
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * Copies a directory and its sub-directories
	 *
	 * @param sourceDirectory
	 *            source directory
	 * @param destinationDirectory
	 *            destination directory
	 * @param keepDate
	 *            flag to maintain last modification date
	 * @param preserve
	 *            flag to preserve existing target files
	 *
	 * @throws IOException
	 *             if any I/O errors occur
	 */

	public static boolean copyDirectory(String sourceDirectory, String destinationDirectory, boolean overwrite,
			boolean preserve) throws IOException {
		return copyDirectory(new File(sourceDirectory), new File(destinationDirectory), overwrite, preserve);

	}

	public static boolean copyDirectory(File sourceDirectory, File destinationDirectory, boolean overwrite,
			boolean preserve) throws IOException {
		logger.info("Copying Directory " + sourceDirectory.getCanonicalPath() + " to "
				+ destinationDirectory.getCanonicalPath() + " with overwrite set to " + overwrite);
		boolean result = false;
		File source = null;
		File destination = null;

		// first make sure the specified source directory
		// exists, is a directory, and is readable
		if ((!sourceDirectory.exists()) || (!sourceDirectory.isDirectory())) {
			throw new IOException("No such source directory: " + sourceDirectory.getAbsolutePath());
		}
		if (!sourceDirectory.canRead()) {
			throw new IOException("Source is unreadable: " + sourceDirectory.getAbsolutePath());
		}

		// If the destination exists, make sure it is a writeable directory
		// and ask before overwriting it. If the destination doesn't
		// exist, make sure the directory exists and is writeable.
		if (destinationDirectory.exists()) {
			if (destinationDirectory.isDirectory()) {
				if (!destinationDirectory.canWrite()) {
					throw new IOException(
							"Destination directory is unwriteable: " + destinationDirectory.getAbsolutePath());
				}
			} else {
				throw new IOException("Destination is not a directory: " + destinationDirectory.getAbsolutePath());
			}
		} else {
			File parent = getParentDirectory(destinationDirectory);
			if (!parent.exists()) {
				parent.mkdirs();
			}
			if (!parent.canWrite()) {
				throw new IOException("Destination directory is unwriteable: " + parent.getPath());
			}

			// make the destination directory
			destinationDirectory.mkdir();
		}

		logger.info("creating the new directory");
		File[] entries = sourceDirectory.listFiles();
		if (entries != null) {
			for (int i = 0; i < entries.length; i++) {
				source = entries[i];
				destination = new File(destinationDirectory, source.getName());
				if (source.isDirectory()) {
					copyDirectory(source, destination, overwrite, preserve);
				} else if ((!preserve) || (!destination.exists())) {
					copyFile(source, destination, overwrite);
				}
			}
		}

		// maintain last modification time if requested to do so
		if (overwrite) {
			long lastModified = sourceDirectory.lastModified();
			destinationDirectory.setLastModified(lastModified);
		}
		if (destinationDirectory.exists()) {
			logger.info("Copied Directory " + sourceDirectory.getCanonicalPath() + " to "
					+ destinationDirectory.getCanonicalPath() + "successfully");
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public static boolean deleteFile(String file) throws IOException {
		return deleteFile(new File(file));
	}

		
	public static boolean deleteFile(File file) throws IOException {

		boolean result = false;

		if (!file.exists()) {
			logger.warn("Trying to delete " + file.getName() + " but file//folder not found");
			return true;
		}
		if(!suppress_loggings)
		logger.info("Deleting File " + file.getCanonicalPath());
		
		if (file != null) {
			result = deleteFileInternal(file);
			if (!result) {
				if(!OperatingSystemHelper.isWindows())
				{
					
						String rmCommand = "rm";
						File rmCommandFile = new File("/usr/bin", rmCommand);
						if (rmCommandFile.exists()) {
							rmCommand = rmCommandFile.getAbsolutePath();
						}
						ProcessBuilder processBuilder = new ProcessBuilder(rmCommand, "-rf", file.getAbsolutePath());
						processBuilder.directory(new File("/"));
						Process process = processBuilder.start();
						try{
						process.waitFor();
						}catch(InterruptedException e)
						{
							logger.fatal("Error in deleting file "+file.getCanonicalPath(), e);
							throw new IOException("deleteFile "+e);
						}
						if (!file.exists()) {
							result = true;
						}
					
				}
			}
		}
		return result;
	}

	private static boolean deleteFileInternal(File file) {
		boolean result = true;
		if (file != null) {
			boolean x = file.isDirectory();
			if (x) {
				File[] children = file.listFiles();
				if (children != null) {
					int i = 0;
					while (i < children.length) {
						result &= deleteFileInternal(children[i]);
						++i;
					}
				}
			}
			result &= file.delete();
		}
		return result;
	}

	public static void unzipFile(String zipFilePath, String destDirectory) throws IOException {
		logger.debug("Unzipping file name " + zipFilePath + " to " + destDirectory);
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + "/" + entry.getName();
			logger.debug(entry.getName());
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	/**
	 * Extracts a zip entry (file entry)
	 * 
	 * @param zipIn
	 * @param destFile
	 * @throws IOException
	 */
	private static void extractFile(ZipInputStream zipIn, String destFile) throws IOException {
		logger.debug("Destination file =" + destFile);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	public static boolean moveFile(String oldfile, String newfile) throws IOException {

		return moveFile(new File(oldfile), new File(newfile));

	}

	public static boolean moveFile(File oldFile, File newFile) throws IOException

	{
		logger.info("Moving File " + oldFile.getCanonicalPath() + " to " + newFile.getCanonicalPath());
		boolean result = false;

		Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		if (newFile.exists()) {
			logger.info("File moved from " + oldFile.getCanonicalPath() + "to" + newFile.getCanonicalPath()
					+ "successfully");
			result = true;
		}
		return result;
	}

	public static boolean moveDirectory(String srcDir, String destDir) throws IOException {
		return moveDirectory(new File(srcDir), new File(destDir));
	}

	public static boolean moveDirectory(File srcDir, File destDir) throws IOException {
		logger.info("Moving Directory " + srcDir.getCanonicalPath() + " to " + destDir.getCanonicalPath());

		if (!(srcDir.exists()) || !(srcDir.isDirectory())) {
			throw new IOException("No such source Directory: " + srcDir.getAbsolutePath());
		}

		boolean success = srcDir.renameTo(new File(destDir, srcDir.getName()));

		logger.info("Directory  moved from " + srcDir.getCanonicalPath() + " to " + destDir.getCanonicalPath()
				+ "successfully");

		if (!success) {

			logger.info(" Directory movement failure");
		}
		return success;

	}

	public static boolean renameFile(String sourceFile, String newName) throws IOException {

		return renameFile(new File(sourceFile), newName);
	}

	public static boolean renameFile(File sourceFile, String newName) throws IOException {
		logger.info("Renaming File " + sourceFile.getCanonicalPath() + " to " + newName);
		boolean result = false;
		File destFile = new File(sourceFile.getParentFile().getCanonicalPath() + "/" + newName);
		if (!(sourceFile.exists())) {
			throw new IOException("No such source file or folder: " + sourceFile.getCanonicalPath());
		}
		if (destFile.exists()) {
			throw new IOException(" file or folder already exit: " + destFile.getCanonicalPath());
		}
		// Rename file (or directory)
		result = sourceFile.renameTo(destFile);
		if (!result) {
			logger.info(
					"File did not rename from " + sourceFile.getCanonicalPath() + " to " + newName + "successfully");
		}
		return result;
	}

	public static List<String> readLinesFromFile(String filename) throws IOException {
		File file = new File(filename);
		List contents = new LinkedList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {

			contents.add(line);
		}
		return contents;

	}

	public static void extractFolder(String zipFile, String extractFolder) {

		logger.info("Extracting the zip file: " + zipFile + " into extractfolder: " + extractFolder);
		try {
			int BUFFER = 2048;
			File file = new File(zipFile);

			ZipFile zip = new ZipFile(file);
			String newPath = extractFolder;

			new File(newPath).mkdir();
			Enumeration zipFileEntries = zip.entries();

			// Process each entry
			while (zipFileEntries.hasMoreElements()) {
				// grab a zip file entry
				ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
				String currentEntry = entry.getName();

				File destFile = new File(newPath, currentEntry);
				// destFile = new File(newPath, destFile.getName());
				File destinationParent = destFile.getParentFile();

				// create the parent directory structure if needed
				destinationParent.mkdirs();

				if (!entry.isDirectory()) {
					BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
					int currentByte;
					// establish buffer for writing file
					byte data[] = new byte[BUFFER];

					// write the current file to disk
					FileOutputStream fos = new FileOutputStream(destFile);
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

					// read and write until last byte is encountered
					while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, currentByte);
					}
					dest.flush();
					dest.close();
					is.close();
				}

			}
		} catch (Exception e) {
			logger.error(e);
		}

	}
	
	public static boolean checkFileExistance (String fileName) throws IOException {
		File fileToBeChecked = new File (fileName);
		if (fileToBeChecked != null && fileToBeChecked.exists()) {
			return true;
		}
		return false;
		
	}

	public static void replaceAndWriteToFile(String fileName, String contents)
			throws FileNotFoundException, UnsupportedEncodingException, IOException {
		File file = new File(fileName);
		if (!file.exists())
			file.createNewFile();

		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		writer.println(contents);
		writer.close();
	}
	
	/*
	 * public static void main(String[] arg) { try{ initLogger();
	 * 
	 * copyFile("c:/temp/regex/.*txt","c:/temp/dest", true,true);
	 * copyFile("c:/temp/regex/patsdk*.jar","c:/temp/dest", true,true);
	 * copyFile("c:/temp/regex/patsdk.*.jar","c:/temp/dest", true,true);
	 * copyFile("c:/temp/regex/patsdk-impl.*jar","c:/temp/dest", true,true);
	 * }catch(Exception ae) { ae.printStackTrace(); } } public static void
	 * initLogger() {
	 * DOMConfigurator.configure(GenericFileHelper.class.getResource(
	 * "/conf/log4j-config.xml")); // Log in console in and log file
	 * logger.debug("Log4j appender configuration is successful !!");
	 * logger.info("hello info"); }
	 */
	static Logger logger = Logger.getLogger(GenericFileHelper.class);
	static boolean suppress_loggings=false;
	public static boolean isSuppressLoggings() {
		return suppress_loggings;
	}

	public static void setSuppressLoggings(boolean suppress_loggings) {
		GenericFileHelper.suppress_loggings = suppress_loggings;
	}

	private static final int BUFFER_SIZE = 4096;
}
