package ycsbtocsv;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  ResultsParserTest.class,
  CsvWriterTest.class
})

public class TestSuite {
  // the class remains empty,
  // used only as a holder for the above annotations
}