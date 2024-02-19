import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class main {

	static Map<String, ArrayList<String>> cfg = new HashMap<String, ArrayList<String>>();
	static ArrayList<String> keys = new ArrayList<>();
	static ArrayList<String> language = new ArrayList<>();

	private static void fileReading(String txt) {
		String[] word;
		try {
			File file = new File(txt);
			Scanner x = new Scanner(file);
			while (x.hasNextLine()) {
				String line = x.nextLine();
				String cfgstring = line.substring(2);
				cfgstring = cfgstring.replace('|', ',');
				word = cfgstring.split(",");
				if (line.charAt(0) == 'E') {
					language.addAll(Arrays.asList(word));
					continue;
				}
				ArrayList<String> arraylist = new ArrayList<>();
				arraylist.addAll(Arrays.asList(word));
				keys.add(String.valueOf(line.charAt(0)));
				cfg.put(String.valueOf(line.charAt(0)), arraylist);
			}
			x.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		fileReading("CFG.txt");
		String epsilonkey = "";
		boolean epsilon = false;
		while (true) {
			for (int i = 0; i < keys.size(); i++) {
				epsilon = false;
				ArrayList<String> variables = cfg.get(keys.get(i));
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).equals("E")) {
						epsilonkey = keys.get(i);
						variables.remove(j);
						removeEpsilonSubstrings(epsilonkey);
						epsilon = true;
					}
				}
			}
			if (!epsilon) {
				break;
			}
		}
		System.out.println("epsilonremove");
		for (int i = 0; i < keys.size(); i++) {
			System.out.println("key = " + keys.get(i));
			for (int j = 0; j < cfg.get(keys.get(i)).size(); j++) {
				System.out.print(cfg.get(keys.get(i)).get(j) + "|");
			}
			System.out.println();
		}
		///// UNIT PRODUCTION
		unitProduction();
		System.out.println("unitProduction");
		for (int i = 0; i < keys.size(); i++) {
			System.out.println("key = " + keys.get(i));
			for (int j = 0; j < cfg.get(keys.get(i)).size(); j++) {
				System.out.print(cfg.get(keys.get(i)).get(j) + "|");
			}
			System.out.println();
		}

		// END UNIT PRODUCTION
		// -----------------
		// LONGER THAN 2
		System.out.println("LONGER 2");
		longerThan2();
		for (int i = 0; i < keys.size(); i++) {
			System.out.println("key = " + keys.get(i));
			for (int j = 0; j < cfg.get(keys.get(i)).size(); j++) {
				System.out.print(cfg.get(keys.get(i)).get(j) + "|");
			}
			System.out.println();
		}
		// END LONGER THAN 2
		// -----------------
		// ELEMINATE TERMINALS
		eleminateTerminals();
		System.out.println("ElIMINATE TERMINALS");
		for (int i = 0; i < keys.size(); i++) {
			System.out.println("key = " + keys.get(i));
			for (int j = 0; j < cfg.get(keys.get(i)).size(); j++) {
				System.out.print(cfg.get(keys.get(i)).get(j) + "|");
			}
			System.out.println();
		}

	}

	private static void unitProduction() {

		for (int i = 0; i < keys.size(); i++) {
			ArrayList<String> variables = cfg.get(keys.get(i));
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).length() == 1 && keys.contains(variables.get(j))) {
					if (!variables.get(j).equals(keys.get(i))) {
						variables.addAll(cfg.get(keys.get(keys.indexOf(variables.get(j)))));
					}
					variables.remove(j);
					j--;
				}
			}
		}

	}

	private static void eleminateTerminals() {
		int count = 85;
		for (int k = 0; k < language.size(); k++) {
			for (int i = 0; i < keys.size(); i++) {
				ArrayList<String> variables = cfg.get(keys.get(i));
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).length() > 1 && variables.get(j).contains(language.get(k))) {

						if (letterkeyindex(language.get(k)) < 0) {
							ArrayList<String> list = new ArrayList<>();
							list.add(language.get(k));
							keys.add(String.valueOf((char) count));
							cfg.put(String.valueOf((char) count), list);
							variables.set(j,
									variables.get(j).replaceAll(language.get(k), String.valueOf((char) count)));
						} else {
							variables.set(j, variables.get(j).replaceAll(language.get(k),
									keys.get(letterkeyindex(language.get(k)))));

						}

					}
				}
			}
			count++;
		}

	}

	private static int letterkeyindex(String letter) {
		for (int i = 0; i < keys.size(); i++) {
			if (cfg.get(keys.get(i)).size() == 1 && cfg.get(keys.get(i)).get(0).equals(letter)) {
				return i;
			}
		}
		return -5;
	}

	private static void longerThan2() {
		int count = 70;
		for (int i = 0; i < keys.size(); i++) {
			ArrayList<String> variables = cfg.get(keys.get(i));
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).length() > 2) {

					if (search(variables.get(j).substring(1)) != null) {
						String key = search(variables.get(j).substring(1));
						variables.set(j, variables.get(j).replaceAll(variables.get(j).substring(1), key));
					} else {
						ArrayList<String> list = new ArrayList<>();
						list.add(variables.get(j).substring(1));
						variables.set(j, variables.get(j).replaceAll(variables.get(j).substring(1),
								String.valueOf((char) count)));
						keys.add(String.valueOf((char) count));
						cfg.put(String.valueOf((char) count), list);
						count++;
					}

				}
			}
		}
	}

	private static String search(String value) {
		for (int i = 0; i < keys.size(); i++) {
			ArrayList<String> variables = cfg.get(keys.get(i));
			for (int j = 0; j < variables.size(); j++) {
				if (variables.size() < 2 && variables.get(j).equalsIgnoreCase(value)) {
					return keys.get(i);
				}
			}

		}
		return null;
	}

	public static void removeEpsilonSubstrings(String epsilonKey) {
		for (int i = 0; i < keys.size(); i++) {
			// ARRAYLIST<STRÄ°NG> size
			int size = cfg.get(keys.get(i)).size();
			for (int j = 0; j < size; j++) {
				// ARRAYLIST ELEMEANLARI STRING
				if (cfg.get(keys.get(i)).get(j).contains(epsilonKey)) {
					// ArrayList<Integer> IndexToRemoveAsSubstring = new ArrayList<>();
					String IndexToRemoveAsSubstring = "";
					for (int k = 0; k < cfg.get(keys.get(i)).get(j).length(); k++) {
						if (cfg.get(keys.get(i)).get(j).charAt(k) == epsilonKey.charAt(0)) {
							IndexToRemoveAsSubstring = IndexToRemoveAsSubstring + k;
						}
					}
					/////
					int len = IndexToRemoveAsSubstring.length();

					int temp = 0;
					// Total possible subsets for string of size n is n*(n+1)/2
					String arr[] = new String[len * (len + 1) / 2];
					// This loop maintains the starting character
					for (int a = 0; a < len; a++) {
						// This loop adds the next character every iteration for the subset to form and
						// add it to the array
						for (int l = a; l < len; l++) {
							arr[temp] = IndexToRemoveAsSubstring.substring(a, l + 1);
							temp++;
						}
					}
					//// arr = all substrings;
					for (int k = 0; k < arr.length; k++) {
						String resultString = "";
						// System.out.println("ALOOO "+arr[k]);
						StringBuilder sBuilder = new StringBuilder(cfg.get(keys.get(i)).get(j));
						for (int k2 = 0; k2 < arr[k].length(); k2++) {

							sBuilder.deleteCharAt(Integer.parseInt("" + arr[k].charAt(k2)) - k2);
							resultString = sBuilder.toString();
							resultString = resultString.trim();
						}
						if (resultString.equals("")) {
							cfg.get(keys.get(i)).add("E");
							// cfg.get(keys.get(i)).remove(epsilonKey);
						} else {
							if (!cfg.get(keys.get(i)).contains(resultString)) {
								cfg.get(keys.get(i)).add(resultString);
							}
						}

					}
				}
				// j++;
			}

		}
	}

}