package vk.vkreader;

import java.net.*;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VKReader {
	public static void main(String[] args) throws Exception {
		if (!checkArgs(args))
			System.exit(-1);
		;

		int firstUid = Integer.parseInt(args[0]);
		int lastUid = Integer.parseInt(args[1]);

		String user_ids = makeUids(firstUid, lastUid);
		String jsonString = getFromVK(user_ids);
		printJsonObjects(jsonString, args);

		System.out.println("Done");

	}

	private static void printJsonObjects(String jsonString, String[] args) {
		JSONParser parser = new JSONParser();
		int writtenFiles = 0;
		try {
			Object obj = parser.parse(jsonString);
			JSONArray jsonArray = (JSONArray) obj;
			for (Object jsonObject : jsonArray) {
				JSONObject person = (JSONObject) jsonObject;
				String uid = person.get("uid").toString();
				// System.out.println(uid);

				try {
					File output;
					if (args[2].startsWith("/"))
						output = new File(args[2]);
					else
						output = new File(System.getProperty("user.dir") + "/"
								+ args[2]);

					FileWriter file = new FileWriter(output + "/uid=" + uid
							+ ".json");
					file.write(person.toJSONString());
					++writtenFiles;
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(writtenFiles + "/"
				+ (Integer.parseInt(args[1]) - Integer.parseInt(args[0]) + 1)
				+ " were written");

	}

	private static String makeUids(int firstUid, int lastUid) {
		String user_ids = String.valueOf(firstUid);
		for (int i = firstUid + 1; i <= lastUid; ++i) {
			user_ids += "," + String.valueOf(i);
		}
		return user_ids;
	}

	public static String getFromVK(String user_ids) throws IOException {
		URL vk = new URL(
				"https://api.vk.com/method/users.get?user_ids="
						+ user_ids
						+ "&fields=sex,bday,city,country,hometown,has_mobile,"
						+ ""
						+ "contacts,site,education,universities,schools,relation,personal,activities,"
						+ ""
						+ "career,occupation,connections,activities,interests,music,movies,tv,books,games,about,quotes");
		URLConnection yc = vk.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String inputLine, vkinfo = "";

		while ((inputLine = in.readLine()) != null) {
			vkinfo = vkinfo.concat(inputLine);
		}
		in.close();
		vkinfo = vkinfo.substring(12, vkinfo.length() - 1);
		return vkinfo;
	}

	public static boolean checkArgs(String[] args) {
		if (args.length != 3) {
			System.err
					.println("Error! Usage: VKReader <first id> <last id>  <folder output path>");
			return false;
		}
		File output;
		if (args[2].startsWith("/"))
			output = new File(args[2]);
		else
			output = new File(System.getProperty("user.dir") + "/" + args[2]);
		if (output.isFile() && output.exists()) {
			System.err
					.println("Error! Existing output dirictory is not a folder!");
			return false;
		}
		if (!output.exists())
			output.mkdirs();
		if (Integer.parseInt(args[0]) <= 0) {
			System.err.println("Error! Wrong <first id> parameter");
			return false;
		}
		if (Integer.parseInt(args[1]) <= 0) {
			System.err.println("Error! Wrong <last id> parameter");
			return false;
		}
		if (Integer.parseInt(args[0]) > Integer.parseInt(args[1])) {
			System.err
					.println("Error! Parameter <first id>  should be less or equal than <last id> ");
			return false;
		}
		if (Integer.parseInt(args[1]) - Integer.parseInt(args[0]) > 900) {
			System.err
					.println("Error! Number of ids should be less or equal than 900 ");
			return false;
		}
		return true;
	}
}