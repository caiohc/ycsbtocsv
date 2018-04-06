package ycsbtocsv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {
	
	private static List<Map<String, String>> applicationParams;
	
	public static void main(final String[] args) {
		
		initApplicationParams();
		
		Map<String, String> applicationProperties = null;
				
		String propertiesFilePath = getApplicationArg(args, "P", "propertiesFile");
				
		if (propertiesFilePath == null) {
			propertiesFilePath = Main.class.getClassLoader().getResource("config.properties").getPath();			
		} 
		
		try {
			applicationProperties = loadApplicationProperties(getApplicationParams(), propertiesFilePath);
		} catch (IOException e) {
			System.err.println(String.format("Error trying to read properties file \"%1$s\"", propertiesFilePath));
			System.exit(0);
		}
		
		final Map<String, String> applicationArgs = parseApplicationArgs(getApplicationParams(), args);
		
		final Map<String, String> appParams = new HashMap<String, String>();
		Integer count = 0;
		
		for (Map<String, String> param : getApplicationParams()) {
			String value = applicationArgs.get(param.get("property"));
			
			if (value != null) {
				appParams.put(param.get("property"), value);
				count++;
			} else {
				appParams.put(param.get("property"), applicationProperties.get(param.get("property")));
				count++;
			}
			
		}
		
		if (count != getApplicationParams().size()) {
			System.err.println("Wrong number of arguments.");
			System.exit(1);
		}
		
		try {
			Application.execute(appParams);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private static void initApplicationParams() {
		
		final Map<String, String> blockStartMarker = new HashMap<String, String>();
		blockStartMarker.put("property", "blockStartMarker");
		blockStartMarker.put("arg", "m");
		
		final Map<String, String> charset = new HashMap<String, String>();
		charset.put("property", "charset");
		charset.put("arg", "e");
		
		final Map<String, String> inputFile = new HashMap<String, String>();
		inputFile.put("property", "inputFile");
		inputFile.put("arg", "i");
		
		final Map<String, String> decimalPlaces = new HashMap<String, String>();
		decimalPlaces.put("property", "decimalPlaces");
		decimalPlaces.put("arg", "d");
		
		final Map<String, String> decimalSymbol = new HashMap<String, String>();
		decimalSymbol.put("property", "decimalSymbol");
		decimalSymbol.put("arg", "s");
		
		final Map<String, String> csvSeparator = new HashMap<String, String>();
		csvSeparator.put("property", "csvSeparator");
		csvSeparator.put("arg", "c");
		
		final Map<String, String> outputFile = new HashMap<String, String>();
		outputFile.put("property", "outputFile");
		outputFile.put("arg", "o");
		
		final Map<String, String> ycsbParams = new HashMap<String, String>();
		ycsbParams.put("property", "ycsbParams");
		ycsbParams.put("arg", "p");
		
		final Map<String, String> ycsbResults = new HashMap<String, String>();
		ycsbResults.put("property", "ycsbResults");
		ycsbResults.put("arg", "r");		
		
		setApplicationParams(Arrays.asList(blockStartMarker, charset, inputFile,
				decimalPlaces, decimalSymbol, csvSeparator, outputFile, ycsbParams, ycsbResults));
		
	}
	
	private static Map<String, String> loadApplicationProperties( 
			final List<Map<String, String>> applicationParams, final String propertiesFilePath) throws IOException {
		
		final Map<String, String> applicationProperties = new HashMap<String, String>();
		final Properties properties = new Properties();
		
		properties.load(Files.newBufferedReader(Paths.get(propertiesFilePath)));
			
		for (Map<String, String> param : applicationParams) {
			String property = param.get("property"); 
			applicationProperties.put(property, properties.getProperty(property));
		}
		
		return applicationProperties;
		
	}
	
	private static Map<String, String> parseApplicationArgs(
			final List<Map<String, String>>applicationParams, final String[] commandLineArgs) {
		
		final Map<String, String> applicationArgs = new HashMap<String, String>();
		
		for (Map<String, String> param : applicationParams) {
			String shortArg = param.get("arg");
			String longArg = param.get("property");
			applicationArgs.put(longArg, getApplicationArg(commandLineArgs, shortArg, longArg));
		}
		
		return applicationArgs;
		
	}
	
	private static String getApplicationArg(final String[] args, final String shortArg, final String longArg) {
		
		for (int i = 0 ; i < args.length - 1 ; i += 2) {
			String loopArg = args[i];
			
			if (loopArg.startsWith("--")) {
				
				if (loopArg.substring(2).equals(longArg)) {
					return args[i + 1];
				}
				
			} else if (loopArg.charAt(0) == '-') {
				
				if (loopArg.substring(1).equals(shortArg)) {
					return args[i + 1];
				}
				
			}
			
		}
		
		return null;
		
	}
	
	private static void setApplicationParams(final List<Map<String, String>> applicationParamsArg) {
		
		applicationParams = applicationParamsArg;
		
	}
	
	private static List<Map<String, String>> getApplicationParams() { return applicationParams; } 
	
}