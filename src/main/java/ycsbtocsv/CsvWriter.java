package ycsbtocsv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Map;

public class CsvWriter {
	
	private BufferedWriter writer = null;
	private Character separator;
	private DecimalFormat df;
	private Charset fileEncoding;

	public void openFile(final String filePath) throws IOException {
		
		File file = new File(filePath);
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		setWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()), getFileEncoding())));
		
	}
	
	public void writeHeaders(final List<String> headers) throws IOException {
		
		for (int i = 0 ; i < headers.size() - 1 ; i++) {
			getWriter().write(headers.get(i));
			getWriter().write(getSeparator());			
		}
		
		getWriter().write(headers.get(headers.size() - 1));
		getWriter().newLine();
		getWriter().flush();
		
	}
	
	public void writeResults(final List<String> headers, final Map<String, List<Map<String, String>>> block) throws IOException {
		
		final List<Map<String, String>> params = block.get("params");
		final List<Map<String, String>> attributes = block.get("attributes");
		
		if (headers.size() != (block.get("params").size() + block.get("attributes").size())) {
			throw new IllegalArgumentException("Headers list and results list must have same size.");
		}
		
		final StringBuilder csvLine = new StringBuilder();
		Integer count = 0;
		
		headersLoop:
			for (String header : headers) {
				count++;
				
				for (Map<String, String> param : params) {
					
					if (param.get("paramName").equals(header)) {
						csvLine.append(param.get("paramValue"));
						 
						if (count != headers.size()) {
							csvLine.append(getSeparator());
						}
						
						continue headersLoop;
					}
					
				}
				
				for (Map<String, String> attribute : attributes) {
					
					if (header.contains(".")) {
						final String[] headerPieces =  header.split("\\.");
						
						if (attribute.get("attributeSection").equals(headerPieces[0]) && attribute.get("attributeName").equals(headerPieces[1])) {
							csvLine.append(formatNumber(attribute.get("attributeValue")));
							
							if (count != headers.size()) {
								csvLine.append(getSeparator());
							}
							
							continue headersLoop;
						}
						
					} else if (attribute.get("attributeName").equals(header)) {
						csvLine.append(formatNumber(attribute.get("attributeValue")));
						
						if (count != headers.size()) {
							csvLine.append(getSeparator());
						}
						
						continue headersLoop;
					}
					
				}
				
			}
		
		getWriter().write(csvLine.toString());
		getWriter().newLine();
		getWriter().flush();
		
	}
	
	public void close() throws IOException { getWriter().close(); }
	
	public void setDecimalPlaces(final Integer quantity, final Character decimalFormatSymbol) {
		
		if (quantity < 1) {
			throw new IllegalArgumentException("Quantity of decimal places should not be less than 1.");
		} 
		
		final StringBuilder pattern = new StringBuilder("#.");
		
		for (int i = 1 ; i <= quantity ; i++) {
			pattern.append("#");			
		}
			
		df = new DecimalFormat(pattern.toString());
		df.setMinimumFractionDigits(1);
		setDecimalFormatSymbol(df, decimalFormatSymbol);
		
	}
	
	private void setDecimalFormatSymbol(final DecimalFormat decimalFormat, final Character decimalFormatSymbol) {
		
		final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(decimalFormatSymbol);
		decimalFormat.setDecimalFormatSymbols(dfs );
		
	}
	
	protected String formatNumber(final String number) {
		
		return df.format(Double.parseDouble(number));
		
	}

	private BufferedWriter getWriter() { return writer; }

	private void setWriter(BufferedWriter writer) { this.writer = writer; }

	public Character getSeparator() { return separator; }

	public void setSeparator(Character separator) { this.separator = separator; }

	public Charset getFileEncoding() { return fileEncoding; }

	public void setFileEncoding(Charset fileEncoding) { this.fileEncoding = fileEncoding; }
	
}