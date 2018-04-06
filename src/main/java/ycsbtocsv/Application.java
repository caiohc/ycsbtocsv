package ycsbtocsv;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Application {
	
	public static void execute(final Map<String, String> applicationParams) throws IOException {
		
		final String blockStartMarker = applicationParams.get("blockStartMarker");
		final Charset charset = parseCharset(applicationParams.get("charset"));
		final String inputFilePath = applicationParams.get("inputFile");
		final Integer	decimalPlaces = Integer.parseInt(applicationParams.get("decimalPlaces"));
		final Character	decimalFormatSymbol = applicationParams.get("decimalSymbol").charAt(0);
		final Character	csvSeparator = applicationParams.get("csvSeparator").charAt(0);
		final String outputFilePath = applicationParams.get("outputFile");
		final List<String> params = parseCommaSeparatedString(applicationParams.get("ycsbParams"));
		final List<String> attributes = parseCommaSeparatedString(applicationParams.get("ycsbResults"));
		
		final ResultsParser parser = new ResultsParser();
		parser.setBlockStartMarker(blockStartMarker);
		parser.setFileEncoding(charset);
		parser.setFilePath(inputFilePath);
		
		final CsvWriter writer = new CsvWriter();
		writer.setDecimalPlaces(decimalPlaces, decimalFormatSymbol);
		writer.setSeparator(csvSeparator);
		writer.setFileEncoding(charset);
		
		try {
			writer.openFile(outputFilePath);
			
			final List<String> headers = new ArrayList<String>(params.size() + attributes.size());
			headers.addAll(params);
			headers.addAll(attributes);
			
			writer.writeHeaders(headers);
			
			String block = null;
			
			while ((block = parser.nextBlock()) != null) {
				writer.writeResults(headers, parser.parseBlock(block, params, attributes));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			parser.close();
			writer.close();
		}
		
	}
	
private static Charset parseCharset(final String charset) {
		
		switch (charset) {
		case "ISO_8859_1":
			return StandardCharsets.ISO_8859_1;
		case "US_ASCII":
			return StandardCharsets.US_ASCII;
		case "UTF_16":
			return StandardCharsets.UTF_16;
		case "UTF_16BE":
			return StandardCharsets.UTF_16BE;
		case "UTF_16LE":
			return StandardCharsets.UTF_16LE;
		case "UTF_8":
			return StandardCharsets.UTF_8;
		default:
			return null;
		}
		
	}

	private static List<String> parseCommaSeparatedString(final String strings) {
	
		final StringTokenizer tokenizer = new StringTokenizer(strings, ",");
	
		final List<String> values = new ArrayList<String>(tokenizer.countTokens());
	
		while (tokenizer.hasMoreTokens()) {
			values.add(tokenizer.nextToken());
		}
	
		return values;
	
	}

}