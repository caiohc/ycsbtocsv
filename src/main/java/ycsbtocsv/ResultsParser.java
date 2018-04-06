package ycsbtocsv;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ResultsParser {
	
	private String blockStartMarker;
	private String filePath;
	private Charset fileEncoding;
	private BufferedReader reader;
	private String currentLine;
	private Boolean endOfFile = Boolean.TRUE;
	
	protected String findValue(final String text, final String key) {
		
		final Integer keyIndex = text.indexOf(key);
		
		if (keyIndex == -1) {
			return null;
		}
		
		final Integer beginIndex = text.indexOf(" ", keyIndex + key.length()) + 1;
		final Integer endIndex = text.indexOf(" ", beginIndex);
		
		if (endIndex != -1) {
			return text.substring(beginIndex, endIndex);
		} else {
			return text.substring(beginIndex);
		}
		
	}
	
	protected String findResultAttribute(final String text, final String section, final String attribute) {
		
		if (text.contains(section)) {
			final String value = findValue(text, attribute);
			
			if (value != null) {
				return value; 
			}
			
		}
		
		return null;
		
	}
	
	protected String findTestParam(final String text, final String param) { return findValue(text, param); }
	
	protected Map<String, List<Map<String, String>>> parseBlock(
			final String log, final List<String> paramKeys , final List<String> attributeKeys) {
		
		Integer paramsCount = 0;
		Integer attributesCount = 0;
		
		final List<Map<String, String>> testParams = new ArrayList<Map<String, String>>(paramKeys.size());
		final List<Map<String, String>> resultsAttributes = new ArrayList<Map<String, String>>(attributeKeys.size());
		
		final StringTokenizer tokenizer = new StringTokenizer(log, "\n");
		
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();
			
			if (paramsCount < paramKeys.size()) {
				
				for (String paramKey : paramKeys) {
					String paramValue = findTestParam(line, paramKey);
					
					if (paramValue != null) {
						Map<String, String> param= new HashMap<String, String>();
						param.put("paramName", paramKey);
						param.put("paramValue", paramValue);
						testParams.add(param);
						paramsCount++;
						break;
					}
					
				}
				
			} else if (attributesCount < attributeKeys.size()) {
				
				for (String attributeKey : attributeKeys) {
					Integer separatorIndex = attributeKey.indexOf('.');
					String attributeSection = attributeKey.substring(0, separatorIndex);
					String attributeName = attributeKey.substring(separatorIndex + 1);
					String attributeValue = findResultAttribute(line, attributeSection, attributeName);
					
					if (attributeValue != null) {
						Map<String, String> attribute= new HashMap<String, String>();
						attribute.put("attributeSection", attributeSection);
						attribute.put("attributeName", attributeName);
						attribute.put("attributeValue", attributeValue);
						resultsAttributes.add(attribute);
						attributesCount++;
						break;
					}
					
				}
				
			}
			
		}
		
		Map<String, List<Map<String, String>>> results = new HashMap<String, List<Map<String, String>>>();
		results.put("params", testParams);
		results.put("attributes", resultsAttributes);
		
		return results;
		
	}
	
	protected String nextBlock() throws IOException {
		
		if (isEndOfFile()) {
			setReader(Files.newBufferedReader(Paths.get(getFilePath()), getFileEncoding()));
			setEndOfFile(Boolean.FALSE);
		}
		
		final BufferedReader reader = getReader();
		String line = null;
		
		if (getCurrentLine() == null) {
					
			while ((line = reader.readLine()) != null) {
			
				if (line.startsWith(getBlockStartMarker())) {
					setCurrentLine(line);
					break;
				}
				
			}
		
			if (line == null) {
				setCurrentLine(null);
				getReader().close();	
				setEndOfFile(Boolean.TRUE);
				return null;
			}
			
		}
		
		final StringBuilder block = new StringBuilder(getCurrentLine());
		block.append("\n");
		
		while ((line = reader.readLine()) != null) {
			
			if (!line.startsWith(getBlockStartMarker())) {
				block.append(line);
				block.append("\n");				
			} else {
				setCurrentLine(line);
				break;
			}
			
		}
		
		if (line == null) {
			setCurrentLine(null);			
		}
		
		return block.toString();
		
	}
	
	public void close() throws IOException { getReader().close(); }
	
	public String getBlockStartMarker() { return blockStartMarker; }

	public void setBlockStartMarker(String blockStartMarker) { this.blockStartMarker = blockStartMarker; }

	public String getFilePath() { return filePath; }

	public void setFilePath(String filePath) { this.filePath = filePath; }

	public Charset getFileEncoding() { return fileEncoding; }

	public void setFileEncoding(Charset fileEncoding) { this.fileEncoding = fileEncoding; }

	private BufferedReader getReader() { return reader; }

	private void setReader(BufferedReader reader) { this.reader = reader; }

	private String getCurrentLine() { return currentLine; }

	private void setCurrentLine(String currentLine) { this.currentLine = currentLine; }

	private Boolean isEndOfFile() { return endOfFile; }

	private void setEndOfFile(Boolean endOfFile) { this.endOfFile = endOfFile; }	
	
}