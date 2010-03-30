/**
 * License changer
 * Copyright (C) 2010 Stefan Handschuh, Philipp Wähnert
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package de.shandschuh.licensechanger;

import java.io.File;
import java.util.ArrayList;

class LicenseChanger {

	public static void printUsage() {
		System.out.println("Usage:\n" +
			"licensechanger [-L <license file>|--license=<license file>]\n" +
			"               [-l <languages>|--languages=<languages>]\n" +
			"               [-n <program name>|--name=<program name> -c <copyright>|--copyright=<copyright>]\n" +
			"               [-v|--verbose] [--list] <path>");
	}

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			// At least the path must be given!
			System.out.println("Error: No <path> given");
			printUsage();
			return;
		}
		
		String license = "LICENSE";
		String name = "";
		String copyright = "";
		String languagesString = "java,cpp";
		boolean verbose = false;
		boolean list = false;
		
		// Parsing the command line arguments:
		for (int i = 0; i < args.length - 1; i++) {
			// Since the last argument must be the path the loops only goes to 'args.length - 1'
			if (args[i].charAt(0) != '-' && i < args.length-1) {
				// Only the path can be passed without any dash
				System.out.println("Error: Invalid option format '" + args[i] + "'");
				printUsage();
				return;
			} else {
				// A real argument might come here
				if (args[i].charAt(1) == '-') {
					// It's a dash-dash-option
					int equal = args[i].indexOf('=');
					String option = null;
					if (equal == -1) {
						option = args[i].substring(2);
					} else if (equal > 2) {
						option = args[i].substring(2, equal);
					} else {
						// Misplaced equal sign
						System.out.println("Error: Misplaced '=' in option '" + args[i] + "'");
						printUsage();
						return;
					}
					if (option.equals("verbose")) {
						verbose = true;
						continue;
					} else if (option.equals("list")) {
						list = true;
						continue;
					}
					if (equal == -1) {
						// No equal sign 
						System.out.println("Error: Missing '=' or unknown option '" + args[i] + "'");
						printUsage();
						return;
					} else if (equal == args[i].length() - 1) {
						// No value appended to option
						System.out.println("Error: Missing value in option '" + args[i] + "'");
						printUsage();
						return;
					}
					String value = args[i].substring(equal+1);
					if (option.equals("license")) {
						license = value;
						continue;
					} else if (option.equals("languages")) {
						languagesString = value;
						continue;
					} else if (option.equals("name")) {
						name = value;
						continue;
					} else if (option.equals("license")) {
						license = value;
						continue;
					} else if (option.equals("copyright")) {
						copyright = value;
						continue;
					} else {
						// Unknown option
						System.out.println("Error: Unkown option '" + args[i] + "'");
						printUsage();
						return;
					}
				} else {
					// It's dash-option
					if (args[i].charAt(1) == 'v') {
						verbose = true;
						continue;
					} else {
						// Since now only options remain with following value ...
						if (i == args.length - 2 || args[i+1].charAt(0) == '-') {
							// ... there must be a next argument containing a value
							System.out.println("Error: No value given to the option '" + args[i].charAt(0) + "'");
							printUsage();
							return;
						} else {
							switch(args[i].charAt(1)) {
								case 'L':
									i++;
									license = args[i];
									continue;
								case 'l':
									i++;
									languagesString = args[i];
									continue;
								case 'n':
									i++;
									name = args[i];
									continue;
								case 'c':
									i++;
									copyright = args[i];
									continue;
								default:
									System.out.println("Error: Unknown option '" + args[i] + "'");
									printUsage();
									return;
							}
						}
					}
				}
			}
		} // end: for(int index = 1; index < args.length - 1; index++)
		// Hopefully didn't miss something...
		
		license = license.replace("~", System.getProperty("user.home"));
		File licenseFile = new File(license);
		if (licenseFile.isDirectory()) {
			// Given license file is a directory...
			System.out.println("Error: Invalid license file '" + license + "'");
			printUsage();
			return;
		}
		if (!licenseFile.exists()) {
			// Given license file doesn't exist
			System.out.println("Error: License file '" + license + "' doesn't exist");
			printUsage();
			return;
		}
		
		String path = args[args.length - 1];
		path = path.replace("~", System.getProperty("user.home"));
		File pathFile = new File(path);
		if (!pathFile.isDirectory()) {
			// Given path isn't a directory...
			System.out.println("Error: Invalid path '" + path + "'");
			printUsage();
			return;
		}
		
		String[] languagesStringArray = languagesString.split(",");
		ArrayList<Language> languages = new ArrayList<Language>(2);
		for (String language : languagesStringArray) {
			language = language.toLowerCase().trim();
			if (language.equals("cpp")) {
				languages.add(new Cpp());
			} else if (language.equals("java")) {
				languages.add(new Java());
			} else {
				// Unkwown language
				System.out.println("Error: Unknown language '" + language + "'");
				printUsage();
				return;
			}
		}
		
		if (verbose) {
			System.out.println("License changer\n" + 
			  "Copyright (C) 2010 Stefan Handschuh, Philipp Wähnert\n\n" + 
			  "This program is free software: you can redistribute it and/or modify\n" +
			  "it under the terms of the GNU Lesser General Public License as published by\n" +
			  "the Free Software Foundation, either version 3 of the License, or\n" +
			  "(at your option) any later version.\n");
		}
		
		Changer changer = new Changer(pathFile, licenseFile, languages, name, copyright, verbose, list);
		changer.changeLicense();
	}

}
