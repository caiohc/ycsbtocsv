package ycsbtocsv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class CsvWriterTest {

	private static String outputFilePath;

	@BeforeClass
	public static void init() {

		outputFilePath = Paths.get(Paths.get("").toAbsolutePath().toString(), "ycsb_output.csv").toString();

	}

	@Test
	public void openFileTest() {

		final CsvWriter writer = new CsvWriter();

		try {
			writer.setFileEncoding(StandardCharsets.UTF_8);
			writer.setDecimalPlaces(3, ',');
			writer.setSeparator(';');
			writer.openFile(outputFilePath);
		} catch (IOException e) {
			fail(e.getMessage());
		}

		assertTrue(new File(outputFilePath).exists());

	}

	@Test
	public void writeHeadersTest() {

		final String sampleCsvHeaders = "threads;RunTime(ms);Throughput(ops/sec);AverageLatency(us)";

		final CsvWriter writer = new CsvWriter();
		writer.setFileEncoding(StandardCharsets.UTF_8);
		writer.setDecimalPlaces(3, ',');
		writer.setSeparator(';');
		
		try {
			writer.openFile(outputFilePath);
			writer.writeHeaders(Arrays.asList("threads", "RunTime(ms)", "Throughput(ops/sec)", "AverageLatency(us)"));

			final BufferedReader reader = Files.newBufferedReader(Paths.get(outputFilePath), StandardCharsets.UTF_8);
			final String actualCsvHeaders = reader.readLine();

			assertEquals(sampleCsvHeaders, actualCsvHeaders);

			writer.close();
		} catch (IOException e) {
			fail(e.getMessage());
		}

	}
	
	@Test
	public void writeResultsTest() {
		
		//Setup --------------------------------------------------------------------------------------
		final List<String> headers = Arrays.asList(
				"threads", "OVERALL.RunTime(ms)", "OVERALL.Throughput(ops/sec)", "READ.AverageLatency(us)");
		
		Map<String, String> threadsParam = new HashMap<String, String>();
		threadsParam.put("paramName", "threads");
		threadsParam.put("paramValue", "1");
		
		Map<String, String> runtimeAttribute = new HashMap<String, String>();
		runtimeAttribute.put("attributeSection", "OVERALL");
		runtimeAttribute.put("attributeName", "RunTime(ms)");
		runtimeAttribute.put("attributeValue", "2871.0");
		
		Map<String, String> throughputAttribute = new HashMap<String, String>();
		throughputAttribute.put("attributeSection", "OVERALL");
		throughputAttribute.put("attributeName", "Throughput(ops/sec)");
		throughputAttribute.put("attributeValue", "348.31069313827936");
		
		Map<String, String> latencyAttribute = new HashMap<String, String>();
		latencyAttribute.put("attributeSection", "READ");
		latencyAttribute.put("attributeName", "AverageLatency(us)");
		latencyAttribute.put("attributeValue", "1300.665");
		
		Map<String, List<Map<String, String>>> sampleResults = new HashMap<String, List<Map<String, String>>>();
		
		List<Map<String, String>> params = Arrays.asList(threadsParam); 
		
		sampleResults.put("params", params);
		
		List<Map<String, String>> attributes = Arrays.asList(runtimeAttribute, throughputAttribute, latencyAttribute);
		
		sampleResults.put("attributes", attributes);
		
		final String sampleCsvResults = "1;2871,0;348,311;1300,665";
		
		final CsvWriter writer = new CsvWriter();
		writer.setFileEncoding(StandardCharsets.UTF_8);
		writer.setDecimalPlaces(3, ',');
		writer.setSeparator(';');
		
		try {
			writer.openFile(outputFilePath);
			writer.writeResults(headers, sampleResults);
			
			final BufferedReader reader = Files.newBufferedReader(Paths.get(outputFilePath), StandardCharsets.UTF_8);
			final String actualCsvHeaders = reader.readLine();
			
			assertEquals(sampleCsvResults, actualCsvHeaders);
			
			writer.close();			
		} catch (IOException e) {
			fail(e.getMessage());
		}
				
	}
	
	@After
	public void clean() {

		final File file = new File(outputFilePath);

		if (file.exists()) {
			file.delete();
		}

	}

}