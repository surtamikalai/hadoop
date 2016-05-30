package vk.vkreader;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

public class VKReaderTest {
	static VKReader vkr = new VKReader();
	static String args[];
	static String rightResultFolder = "src/test/resources/RightResult";

	@Before
	public void setUp() {
		args = new String[] { "100", "101", "src/test/resources/TestResult" };
	}

	@SuppressWarnings("static-access")
	@Test
	public void checkArgsTest() {
		args = new String[] { "100", "101", "src/test/resources/TestResult" };
		assertTrue(vkr.checkArgs(args));

		args = new String[] { "100", "101", "src/test/resources/TestResult",
				" " };
		assertFalse("Wrong number of parameters!", vkr.checkArgs(args));

		args = new String[] { "100", "101",
				"src/main/java/vk/vkreader/VKReader.java" };
		assertFalse("Output is not a folder!", vkr.checkArgs(args));

		args = new String[] { "0", "101", "src/test/resources/TestResult" };
		assertFalse("Wrong first parameter!", vkr.checkArgs(args));

		args = new String[] { "100", "-1", "src/test/resources/TestResult" };
		assertFalse("Wrong second parameter!", vkr.checkArgs(args));

		args = new String[] { "102", "101", "src/test/resources/TestResult" };
		assertFalse("First parameter less than second one!",
				vkr.checkArgs(args));

		args = new String[] { "100", "1001", "src/test/resources/TestResult" };
		assertFalse("Number of ids should be less or equal than 900 !",
				vkr.checkArgs(args));
	}

	@Test
	public void getFromVKTest() {
		String rightJsonStringOutput;
		try {
			rightJsonStringOutput = readFromFolder(rightResultFolder);
			Object rightObj = StringToJsonArray(rightJsonStringOutput);
			Object currObj = StringToJsonArray(VKReader.getFromVK("100,101"));
			assertEquals(rightObj, currObj);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	@Test
	public void printJsonObjectsTest() {
		try {
			vkr.main(args);
			assertEquals("Wrong number of files has been written!",
					numberOfFiles(rightResultFolder), numberOfFiles(args[2]));
			assertEquals("Wrong name of files has been written!",
					listOfFiles(rightResultFolder), listOfFiles(args[2]));
			assertEquals("Wrong content of files has been written",
					StringToJsonArray(readFromFolder(rightResultFolder)),
					StringToJsonArray(readFromFolder(args[2])));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<File> listOfFiles(String folder) {
		File rightResultFolder = new File(folder);
		ArrayList<File> listOfFiles = new ArrayList<File>();
		for (File f : rightResultFolder.listFiles())
			if (f.isFile()) {
				File fileName = new File(f.getName());
				listOfFiles.add(fileName);
			}
		Collections.sort(listOfFiles);
		return listOfFiles;
	}

	private Object numberOfFiles(String folder) {
		File ffolder = new File(folder);
		int numOfFilesInDirectory = 0;
		for (File f : ffolder.listFiles())
			if (f.isFile()) {
				++numOfFilesInDirectory;
			}
		return numOfFilesInDirectory;
	}

	private String readFromFolder(String arg) throws IOException {
		String rightJsonStringOutput = "[";
		File rightResultFolder = new File(arg);
		for (File f : rightResultFolder.listFiles())
			if (f.isFile()) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line = br.readLine();
				rightJsonStringOutput += line + ",";
				br.close();

			}
		rightJsonStringOutput = rightJsonStringOutput.substring(0,
				rightJsonStringOutput.length() - 1);
		rightJsonStringOutput += "]";
		return rightJsonStringOutput;
	}

	private Object StringToJsonArray(String jsonString) throws ParseException {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(jsonString);
		return obj;
	}

}
