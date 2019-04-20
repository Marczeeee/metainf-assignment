package hu.metainf.jiracsvuploader.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.metainf.jiracsvuploader.stat.StatData;
import hu.metainf.jiracsvuploader.util.StatTypeKeys;

/**
 * Class reading a CSV input file, checking the lines read and forwarding data for processing.
 *
 */
public class JiraCsvReader {
    /** {@link Logger} instance. */
    private final Logger logger = LoggerFactory.getLogger(JiraCsvReader.class);

    /**
     * Regex pattern to match a whole data record in the CSV file, even if the record is in multiple
     * lines.
     */
    private final String recordPattern =
            "^[a-zA-Z0-9_-]*[,][0-9]*[,]\\w*(\\s\\w*)*[,][0-9]{2}[\\/][\\w]{3}[\\/][0-9]{2}\\s[0-9]+[\\:][0-9]+\\s[\\w]{2}[,][0-9]{2}[\\/][\\w]{3}[\\/][0-9]{2}\\s[0-9]+[\\:][0-9]+\\s[\\w]{2}[,]\\w*[,]\\w*[,][\\s\\w[\\\"*._()-\\\\'\\\\']]*[,][\\s\\w[\\\"*._()-\\\\'\\\\']]*";

    /**
     * Processes Jira CSV file, creating processable tasks from raw data.
     *
     * @param sourceFilePath
     *            path of the source CSV file
     * @param lineRegex
     *            optional regular expression to be matched by CSV row part(s)
     * @param csvRecordProcessor
     *            CSV record processor object
     */
    public void doJiraCSVProcessing(
            final String sourceFilePath,
            final String lineRegex,
            final CsvLineProcessor csvRecordProcessor) {
        final File csvFile = new File(sourceFilePath);
        try {
            final Scanner scanner = new Scanner(csvFile, StandardCharsets.UTF_8.name());
            final String headerRow = scanner.nextLine();
            csvRecordProcessor.setHeaderRow(headerRow);
            logger.debug("Set header row in CSV line processor: {}", headerRow);
            while (scanner.hasNextLine()) {
                String csvLine = scanner.nextLine();
                StatData.addIncrementedValue(StatTypeKeys.PROCESSED_ROW_NR);
                while (!csvLine.matches(recordPattern) && scanner.hasNextLine()) {
                    final String nextCsvLine = scanner.nextLine();
                    csvLine = csvLine.concat(nextCsvLine);
                }
                logger.debug("Read complete line from CSV: {}", csvLine);
                if (isRecordMatchingRegex(csvLine, lineRegex)) {
                    logger.debug("Send CSV line for processing ({})", csvLine);
                    csvRecordProcessor.add4Task(csvLine);
                }
            }
            scanner.close();
        } catch (final FileNotFoundException e) {
            logger.error("Failed to find CSV file to read");
        }
    }

    /**
     * Checks if any part of the line loaded from CSV file matches the regular expression given at
     * application start. If no regular expression was given, it's treated as successful match.
     *
     * @param csvLine
     *            CSV line
     * @param lineRegex
     *            regular expression to match
     * @return Returns <code>true</code> if regular expression is matched or no regular expression
     *         was given, <code>false</code> if the regular expression wasn't matched.
     */
    private boolean isRecordMatchingRegex(final String csvLine, final String lineRegex) {
        logger.debug("Checking if regex given ({}) is matched by any part of CSV line: {}",
                lineRegex, csvLine);
        boolean hasMatch = false;
        if (lineRegex != null && !lineRegex.isEmpty()) {
            final StringTokenizer tokenizer = new StringTokenizer(csvLine, ",");
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                if (token.matches(lineRegex)) {
                    hasMatch = true;
                    break;
                }
            }
        } else {
            hasMatch = true;
        }
        return hasMatch;
    }
}
