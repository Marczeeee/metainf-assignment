package hu.metainf.jiracsvuploader;

import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.metainf.jiracsvuploader.process.CsvLineProcessor;
import hu.metainf.jiracsvuploader.process.JiraCsvReader;
import hu.metainf.jiracsvuploader.stat.StatData;
import hu.metainf.jiracsvuploader.stat.StatPrinter;
import hu.metainf.jiracsvuploader.util.StatTypeKeys;

/**
 * Application main class.
 *
 */
public final class AppMain {
    /** {@link Logger} instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AppMain.class);

    /** Ctor. */
    private AppMain() {}

    /**
     * Application main method.
     *
     * @param args
     *            command line arguments array
     */
    public static void main(final String[] args) {
        LOGGER.info("Jira CSV Uploader application is starting");
        StatData.addValue(StatTypeKeys.APP_START_TIMESTAMP, new Date().getTime());

        final CommandLine cmd = AppMain.parseCmdArgs(args);
        final String csvFilePath = cmd.getOptionValue("f");
        final int threadNr = Integer.parseInt(cmd.getOptionValue("t"));
        final String lineRegex = cmd.getOptionValue("r");
        LOGGER.debug("Start Jira CSV record processing");
        final CsvLineProcessor csvRecordProcessor = new CsvLineProcessor(threadNr);
        new JiraCsvReader().doJiraCSVProcessing(csvFilePath, lineRegex, csvRecordProcessor);
        LOGGER.info("JIRA CSV Uploader application finished CSV data processing");
        StatData.addValue(StatTypeKeys.APP_END_TIMESTAMP, new Date().getTime());
        try {
            LOGGER.debug("Initiating worker thread pool shutdown, waiting for tasks to complete");
            csvRecordProcessor.shutdown();
        } catch (final InterruptedException e) {
            LOGGER.warn("Background worker thread pool shutdown waiting was interrupted: {}",
                    e.getMessage());
        }
        LOGGER.info("JIRA CSV Uploader application finished CSV data uploading");
        LOGGER.debug("Start printing statistical information");
        new StatPrinter().printAppStats();
        LOGGER.debug("JIRA CSV Uploader application exiting");
    }

    /** Exit code used when exiting with an error. */
    private static final int ERROR_EXIT_CODE = 127;

    /**
     * Parses command line arguments and transfers them to a {@link CommandLine} instance. If
     * parsing fails displays the application help and quits with an error status code.
     *
     * @param args
     *            array of command line arguments
     * @return The {@link CommandLine} object created
     */
    private static CommandLine parseCmdArgs(final String[] args) {
        final CommandLineParser parser = new DefaultParser();
        final Options options = AppMain.createAppOptions();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption('h')) {
                AppMain.printAppHelp(options, 0);
            }
            return cmd;
        } catch (final ParseException e) {
            LOGGER.error("{}", e.getMessage());
            AppMain.printAppHelp(options, ERROR_EXIT_CODE);
        }
        return null;
    }

    /**
     * Creates all available application command line options.
     *
     * @return {@link Options} object created
     */
    private static Options createAppOptions() {
        final Options options = new Options();
        options.addRequiredOption("f", "csvFile", true,
                "Absolute or relative path of the CSV file to be read");
        options.addRequiredOption("t", "threadsNr", true,
                "Number of parallel threads to be used when processing data");
        options.addOption("r", "regex", true,
                "Regular expression for matching lines to be processed");
        options.addOption("h", "help", false, "Prints this help");
        return options;
    }

    /**
     * Prints the application help and quits the application with the exit code given.
     *
     * @param options
     *            object containing application options
     * @param exitCode
     *            exit code to be used for application ending
     */
    private static void printAppHelp(final Options options, final int exitCode) {
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("AppMain", options);
        System.exit(exitCode);
    }
}
