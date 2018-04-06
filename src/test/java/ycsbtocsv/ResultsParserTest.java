package ycsbtocsv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ResultsParserTest {
	
	@Test
	public void findValueTest() {
		
		final ResultsParser parser = new ResultsParser();		
		
		final StringBuilder textLine1 = new StringBuilder("Command line: -db com.yahoo.ycsb.db.JdbcDBClient ");
		textLine1.append("-P /Users/caiohc/Desenvolvimento/ycsb-0.7.0/workloads/workloadc -p db.driver=org.postgresql.Driver ");
		textLine1.append("-p db.url=jdbc:postgresql://aquarius.lan:5432/test -p db.user=postgres -p db.passwd=postgres -threads 21 -t");
		
		final String textLine2 = "[OVERALL], RunTime(ms), 2871.1023445";
		
		final String value1 = parser.findValue(textLine1.toString(), "threads");
		final String value2 = parser.findValue(textLine2.toString(), "RunTime");
		
		assertEquals("21", value1);
		assertEquals("2871.1023445", value2);
		
	}
	
	@Test
	public void findValueFailureTest() {
		
		final StringBuilder textLine1 = new StringBuilder("Command line: -db com.yahoo.ycsb.db.JdbcDBClient ");
		textLine1.append("-P /Users/caiohc/Desenvolvimento/ycsb-0.7.0/workloads/workloadc -p db.driver=org.postgresql.Driver ");
		textLine1.append("-p db.url=jdbc:postgresql://aquarius.lan:5432/test -p db.user=postgres -p db.passwd=postgres -threads 21 -t");
		
		final String textLine2 = "[OVERALL], RunTime(ms), 2871.0";
		
		final ResultsParser parser = new ResultsParser();
				
		final String value1 = parser.findValue(textLine1.toString(), "RunTime");
		final String value2 = parser.findValue(textLine2.toString(), "threads");
		
		assertNull(value1);
		assertNull(value2);
		
	}
	
	@Test
	public void findResultAttributeTest() {
		
		final ResultsParser parser = new ResultsParser();
				
		final String value = parser.findResultAttribute(
				"[OVERALL], Throughput(ops/sec), 348.31069313827936", "OVERALL", "Throughput(ops/sec)");
		
		assertEquals("348.31069313827936", value);
		
	}
	
	@Test
	public void findResultAttributeFailureTest() {
		
		final ResultsParser parser = new ResultsParser();
		
		final String value = parser.findResultAttribute("[OVERALL], RunTime(ms), 2871.0", "OVERALL", "Throughput(ops/sec)");
		
		assertNull(value);
		
	}
	
	@Test 
	public void findTestParamTest() {
		
		final ResultsParser parser = new ResultsParser();
		
		final StringBuilder text = new StringBuilder("Command line: -db com.yahoo.ycsb.db.JdbcDBClient ");
		text.append("-P /Users/caiohc/Desenvolvimento/ycsb-0.7.0/workloads/workloadc -p db.driver=org.postgresql.Driver ");
		text.append("-p db.url=jdbc:postgresql://aquarius.lan:5432/test -p db.user=postgres -p db.passwd=postgres -threads 1 -t");
		
		final String param = parser.findTestParam(text.toString(), "threads");
		
		assertEquals("1", param);
		
	}
	
	@Test 
	public void findTestParamFailureTest() {
		
		final ResultsParser parser = new ResultsParser();
				
		final String text = "Adding shard node URL: jdbc:postgresql://aquarius.lan:5432/test";
		
		final String param = parser.findTestParam(text, "threads");
		
		assertNull(param);
		
	}
	
	@Test
	public void parseBlockTest() {
		
		final StringBuilder log = new StringBuilder();
		log.append("YCSB Client 0.1\n");
		log.append("Command line: -db com.yahoo.ycsb.db.JdbcDBClient -P /Users/caiohc/Desenvolvimento/ycsb-0.7.0/workloads/workloadc -p db.driver=org.postgresql.Driver -p db.url=jdbc:postgresql://aquarius.lan:5432/test -p db.user=postgres -p db.passwd=postgres -threads 1 -t\n");
		log.append("Adding shard node URL: jdbc:postgresql://aquarius.lan:5432/test\n");
		log.append("Using 1 shards\n");
		log.append("[OVERALL], RunTime(ms), 2871.0\n");
		log.append("[OVERALL], Throughput(ops/sec), 348.31069313827936\n");
		log.append("[READ], Operations, 1000.0\n");
		log.append("[READ], AverageLatency(us), 1300.665\n");
		log.append("[READ], MinLatency(us), 654.0\n");
		log.append("[READ], MaxLatency(us), 68799.0\n");
		log.append("[READ], 95thPercentileLatency(us), 2573.0\n");
		log.append("[READ], 99thPercentileLatency(us), 5291.0\n");
		log.append("[READ], Return=OK, 1000\n");
		log.append("[CLEANUP], Operations, 1.0\n");
		log.append("[CLEANUP], AverageLatency(us), 1029.0\n");
		log.append("[CLEANUP], MinLatency(us), 1029.0\n");
		log.append("[CLEANUP], MaxLatency(us), 1029.0\n");
		log.append("[CLEANUP], 95thPercentileLatency(us), 1029.0\n");
		log.append("[CLEANUP], 99thPercentileLatency(us), 1029.0\n");
		log.append("[UPDATE-FAILED], Operations, 3.0\n");
		log.append("[UPDATE-FAILED], AverageLatency(us), 2528256.0\n");
		log.append("[UPDATE-FAILED], MinLatency(us), 2500608.0\n");
		log.append("[UPDATE-FAILED], MaxLatency(us), 2580479.0\n");
		log.append("[UPDATE-FAILED], 95thPercentileLatency(us), 2580479.0\n");
		log.append("[UPDATE-FAILED], 99thPercentileLatency(us), 2580479.0");
		
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
		
		Map<String, String> updateFailedLatencyAttribute = new HashMap<String, String>();
		updateFailedLatencyAttribute.put("attributeSection", "UPDATE-FAILED");
		updateFailedLatencyAttribute.put("attributeName", "AverageLatency(us)");
		updateFailedLatencyAttribute.put("attributeValue", "2528256.0");
		
		Map<String, List<Map<String, String>>> sampleResults = new HashMap<String, List<Map<String, String>>>();
		
		List<Map<String, String>> params = Arrays.asList(threadsParam); 
		
		sampleResults.put("params", params);
		
		List<Map<String, String>> attributes =
				Arrays.asList(runtimeAttribute, throughputAttribute, latencyAttribute, updateFailedLatencyAttribute);
		
		sampleResults.put("attributes", attributes);
		
		final ResultsParser parser = new ResultsParser();
				
		Map<String, List<Map<String, String>>> actualResults = parser.parseBlock(log.toString(), Arrays.asList("threads"),
				Arrays.asList("OVERALL.RunTime(ms)", "OVERALL.Throughput(ops/sec)",
						"READ.AverageLatency(us)", "UPDATE-FAILED.AverageLatency(us)"));
		
		assertEquals(sampleResults, actualResults);
		
	}
	
	@Test
	public void nextBlockTest() {
		
		//Setup------------------------------------------------------------------------------		
		final List<String> sampleBlocks = new ArrayList<String>(5);
		final URL url = getClass().getClassLoader().getResource("ycsb_output.log");
						
		try {
			final BufferedReader reader = Files.newBufferedReader(Paths.get(url.getPath()), StandardCharsets.UTF_8);
			
			String line = null;
			StringBuilder block = null;
			
			while ((line = reader.readLine()) != null) {
				Boolean isBlockStart = line.startsWith("YCSB Client 0.1"); 
				
				if (isBlockStart && block == null) {
					block = new StringBuilder(line);
					block.append("\n");
				} else if (isBlockStart && block != null) {
					sampleBlocks.add(block.toString());
					block = new StringBuilder(line);
					block.append("\n");
				} else {
					block.append(line);
					block.append("\n");
				}
					
			}
			
			if (block != null) {
				sampleBlocks.add(block.toString());
			}
			
			reader.close();
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		//Test--------------------------------------------------------------------------------------
		final ResultsParser parser = new ResultsParser();
		parser.setBlockStartMarker("YCSB Client 0.1");
		parser.setFilePath(url.getPath());
		parser.setFileEncoding(StandardCharsets.UTF_8);
		
		String actualBlock = null;
		List<String> actualBlocks = new ArrayList<String>();
		
		try {
			
			while ((actualBlock = parser.nextBlock()) != null) {
				actualBlocks.add(actualBlock);
			}
			
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		assertEquals(sampleBlocks.size(), actualBlocks.size());
		
		for (int i = 0 ; i < sampleBlocks.size() ; i++) {
			assertEquals(sampleBlocks.get(i), actualBlocks.get(i));
		}
		
	}
	
	@Test
	public void parseFileTest() {
		
		ResultsParser parser = new ResultsParser();
		parser.setBlockStartMarker("YCSB Client 0.1");
		parser.setFileEncoding(StandardCharsets.UTF_8);
		parser.setFilePath(getClass().getClassLoader().getResource("ycsb_output.log").getPath());
		
		List<Map<String, List<Map<String, String>>>> results = new ArrayList<Map<String, List<Map<String, String>>>>(5);
		String block = null;
		
		try {
			
			while ((block = parser.nextBlock()) != null) {
				results.add(parser.parseBlock(block, Arrays.asList("threads"), Arrays.asList("OVERALL.RunTime(ms)",
						"OVERALL.Throughput(ops/sec)", "READ.AverageLatency(us)")));
			}
			
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		assertEquals(prepareSampleResults(), results);
		
	}
	
	private List<Map<String, List<Map<String, String>>>> prepareSampleResults() {
		
		//First block--------------------------------------------------------------------------
		
		Map<String, String> threadsParam1 = new HashMap<String, String>();
		threadsParam1.put("paramName", "threads");
		threadsParam1.put("paramValue", "1");
		
		Map<String, String> runtimeAttribute1 = new HashMap<String, String>();
		runtimeAttribute1.put("attributeSection", "OVERALL");
		runtimeAttribute1.put("attributeName", "RunTime(ms)");
		runtimeAttribute1.put("attributeValue", "2871.0");
		
		Map<String, String> throughputAttribute1 = new HashMap<String, String>();
		throughputAttribute1.put("attributeSection", "OVERALL");
		throughputAttribute1.put("attributeName", "Throughput(ops/sec)");
		throughputAttribute1.put("attributeValue", "348.31069313827936");
		
		Map<String, String> latencyAttribute1 = new HashMap<String, String>();
		latencyAttribute1.put("attributeSection", "READ");
		latencyAttribute1.put("attributeName", "AverageLatency(us)");
		latencyAttribute1.put("attributeValue", "1300.665");
		
		Map<String, List<Map<String, String>>> sampleResults1 = new HashMap<String, List<Map<String, String>>>();
		
		List<Map<String, String>> params1 = Arrays.asList(threadsParam1); 
		
		sampleResults1.put("params", params1);
		
		List<Map<String, String>> attributes1 = Arrays.asList(runtimeAttribute1, throughputAttribute1, latencyAttribute1);
		
		sampleResults1.put("attributes", attributes1);
		
		//Second block--------------------------------------------------------------------------
		
		Map<String, String> threadsParam2 = new HashMap<String, String>();
		threadsParam2.put("paramName", "threads");
		threadsParam2.put("paramValue", "2");
		
		Map<String, String> runtimeAttribute2 = new HashMap<String, String>();
		runtimeAttribute2.put("attributeSection", "OVERALL");
		runtimeAttribute2.put("attributeName", "RunTime(ms)");
		runtimeAttribute2.put("attributeValue", "1122.0");
		
		Map<String, String> throughputAttribute2 = new HashMap<String, String>();
		throughputAttribute2.put("attributeSection", "OVERALL");
		throughputAttribute2.put("attributeName", "Throughput(ops/sec)");
		throughputAttribute2.put("attributeValue", "891.2655971479501");
		
		Map<String, String> latencyAttribute2 = new HashMap<String, String>();
		latencyAttribute2.put("attributeSection", "READ");
		latencyAttribute2.put("attributeName", "AverageLatency(us)");
		latencyAttribute2.put("attributeValue", "1434.555");
		
		Map<String, List<Map<String, String>>> sampleResults2 = new HashMap<String, List<Map<String, String>>>();
		
		List<Map<String, String>> params2 = Arrays.asList(threadsParam2); 
		
		sampleResults2.put("params", params2);
		
		List<Map<String, String>> attributes2 = Arrays.asList(runtimeAttribute2, throughputAttribute2, latencyAttribute2);
		
		sampleResults2.put("attributes", attributes2);
		
		//Third block--------------------------------------------------------------------------
		
		Map<String, String> threadsParam3 = new HashMap<String, String>();
		threadsParam3.put("paramName", "threads");
		threadsParam3.put("paramValue", "3");
		
		Map<String, String> runtimeAttribute3 = new HashMap<String, String>();
		runtimeAttribute3.put("attributeSection", "OVERALL");
		runtimeAttribute3.put("attributeName", "RunTime(ms)");
		runtimeAttribute3.put("attributeValue", "920.0");
		
		Map<String, String> throughputAttribute3 = new HashMap<String, String>();
		throughputAttribute3.put("attributeSection", "OVERALL");
		throughputAttribute3.put("attributeName", "Throughput(ops/sec)");
		throughputAttribute3.put("attributeValue", "1086.9565217391305");
		
		Map<String, String> latencyAttribute3 = new HashMap<String, String>();
		latencyAttribute3.put("attributeSection", "READ");
		latencyAttribute3.put("attributeName", "AverageLatency(us)");
		latencyAttribute3.put("attributeValue", "1554.953");
		
		Map<String, List<Map<String, String>>> sampleResults3 = new HashMap<String, List<Map<String, String>>>();
		
		List<Map<String, String>> params3 = Arrays.asList(threadsParam3); 
		
		sampleResults3.put("params", params3);
		
		List<Map<String, String>> attributes3 = Arrays.asList(runtimeAttribute3, throughputAttribute3, latencyAttribute3);
		
		sampleResults3.put("attributes", attributes3);
		
		//Fourth block--------------------------------------------------------------------------
		
		Map<String, String> threadsParam4 = new HashMap<String, String>();
		threadsParam4.put("paramName", "threads");
		threadsParam4.put("paramValue", "4");
		
		Map<String, String> runtimeAttribute4 = new HashMap<String, String>();
		runtimeAttribute4.put("attributeSection", "OVERALL");
		runtimeAttribute4.put("attributeName", "RunTime(ms)");
		runtimeAttribute4.put("attributeValue", "929.0");
		
		Map<String, String> throughputAttribute4 = new HashMap<String, String>();
		throughputAttribute4.put("attributeSection", "OVERALL");
		throughputAttribute4.put("attributeName", "Throughput(ops/sec)");
		throughputAttribute4.put("attributeValue", "1076.4262648008612");
		
		Map<String, String> latencyAttribute4 = new HashMap<String, String>();
		latencyAttribute4.put("attributeSection", "READ");
		latencyAttribute4.put("attributeName", "AverageLatency(us)");
		latencyAttribute4.put("attributeValue", "2131.643");
		
		Map<String, List<Map<String, String>>> sampleResults4 = new HashMap<String, List<Map<String, String>>>();
		
		List<Map<String, String>> params4 = Arrays.asList(threadsParam4); 
		
		sampleResults4.put("params", params4);
		
		List<Map<String, String>> attributes4 = Arrays.asList(runtimeAttribute4, throughputAttribute4, latencyAttribute4);
		
		sampleResults4.put("attributes", attributes4);
		
		//Fifth block--------------------------------------------------------------------------
		
		Map<String, String> threadsParam5 = new HashMap<String, String>();
		threadsParam5.put("paramName", "threads");
		threadsParam5.put("paramValue", "5");
		
		Map<String, String> runtimeAttribute5 = new HashMap<String, String>();
		runtimeAttribute5.put("attributeSection", "OVERALL");
		runtimeAttribute5.put("attributeName", "RunTime(ms)");
		runtimeAttribute5.put("attributeValue", "919.0");
		
		Map<String, String> throughputAttribute5 = new HashMap<String, String>();
		throughputAttribute5.put("attributeSection", "OVERALL");
		throughputAttribute5.put("attributeName", "Throughput(ops/sec)");
		throughputAttribute5.put("attributeValue", "1088.139281828074");
		
		Map<String, String> latencyAttribute5 = new HashMap<String, String>();
		latencyAttribute5.put("attributeSection", "READ");
		latencyAttribute5.put("attributeName", "AverageLatency(us)");
		latencyAttribute5.put("attributeValue", "2462.835");
		
		Map<String, List<Map<String, String>>> sampleResults5 = new HashMap<String, List<Map<String, String>>>();
		
		List<Map<String, String>> params5 = Arrays.asList(threadsParam5); 
		
		sampleResults5.put("params", params5);
		
		List<Map<String, String>> attributes5 = Arrays.asList(runtimeAttribute5, throughputAttribute5, latencyAttribute5);
		
		sampleResults5.put("attributes", attributes5);
		
		List<Map<String, List<Map<String, String>>>> sampleResults = new ArrayList<Map<String, List<Map<String, String>>>>(5);
		sampleResults.add(sampleResults1);
		sampleResults.add(sampleResults2);
		sampleResults.add(sampleResults3);
		sampleResults.add(sampleResults4);
		sampleResults.add(sampleResults5);
		
		return sampleResults;
		
	}
	
}