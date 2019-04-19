package hu.metainf.jiracsvuploader.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.metainf.jiracsvuploader.stat.StatData;
import hu.metainf.jiracsvuploader.util.CustomBlockingQueue;
import hu.metainf.jiracsvuploader.util.PropTypes;

/**
 * Processes the lines of a CSV file.
 *
 */
public class CsvLineProcessor {
    /** {@link Logger} instance. */
    private final Logger logger = LoggerFactory.getLogger(CsvLineProcessor.class);
    /** {@link ExecutorService} instance. */
    private final ExecutorService executorService;
    /** Array of header values in the order it is in the originating CSV file. */
    private String[] headers;

    /**
     * Ctor.
     *
     * @param threadNr
     *            Number of parallel threads
     */
    public CsvLineProcessor(final int threadNr) {
        executorService = new ThreadPoolExecutor(threadNr, threadNr, 0L, TimeUnit.MILLISECONDS,
                new CustomBlockingQueue());
    }

    /**
     * Sets the header row containing header key values.
     *
     * @param headerRow
     *            row containing header values
     */
    public void setHeaderRow(final String headerRow) {
        if (headerRow != null) {
            headers = headerRow.split(",");
        }
    }

    /**
     * Time unit value of thread pool termination waiting.
     */
    private static final int TERMINATION_AWAIT_TIME_VALUE = 5;

    /**
     * Initiates shutdown of the background thread pool, and waits till the completion of all
     * submitted tasks.
     *
     * @throws InterruptedException
     *             If the shutdown process if interrupted before ordered termination if the thread
     *             pool
     */
    public void shutdown() throws InterruptedException {
        executorService.shutdown();
        boolean isFinished = false;
        while (!isFinished) {
            isFinished = executorService.awaitTermination(TERMINATION_AWAIT_TIME_VALUE,
                    TimeUnit.SECONDS);
        }
    }

    /**
     * Adds a CSV line to an uploader task and submits it to execution.
     *
     * @param csvLine
     *            CSV line value
     */
    public void add4Task(final String csvLine) {
        executorService.submit(new JiraTaskUploader(csvLine));
    }

    /**
     * Background task uploading a CSV line transformed to JSON to a Jira instance.
     *
     */
    private class JiraTaskUploader implements Runnable {
        /** CSV line to be processed. */
        private final String csvLine;
        /** Status code for successful response. */
        private static final int STATUS_CODE_SUCCESS = 200;
        /** Status code for internal error response. */
        private static final int STATUS_CODE_ERROR = 500;
        /** Status code for forbidden response. */
        private static final int STATUS_CODE_FORBIDDEN = 403;

        /**
         * Ctor.
         *
         * @param csvLine
         *            CSV line value
         */
        public JiraTaskUploader(final String csvLine) {
            super();
            this.csvLine = csvLine;
        }

        @Override
        public void run() {
            final long startTime = System.currentTimeMillis();
            logger.debug("Transforming CSV line to Jira JSON data: {}", csvLine);
            final String[] csvLineSplit = csvLine.split(",");
            final JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < headers.length; i++) {
                jsonObject.append(headers[i], csvLineSplit[i]);
            }
            int statusCode;
            try {
                statusCode = performJsonUpload(jsonObject);
            } catch (final InterruptedException e) {
                statusCode = STATUS_CODE_ERROR;
            }
            logger.info("Response status code received from Jira instance: {}", statusCode);
            if (statusCode == STATUS_CODE_SUCCESS) {
                StatData.addIncrementedValue(PropTypes.UPLOADED_ROW_NR);
            } else {
                StatData.addIncrementedValue(PropTypes.FAILED_ROW_UPLOAD_NR);
            }
            final long execTime = System.currentTimeMillis() - startTime;
            StatData.setMinValue(PropTypes.MIN_EXEC_TIME, execTime);
            StatData.setMaxValue(PropTypes.MAX_EXEC_TIME, execTime);
            StatData.setAverageValue(PropTypes.AVG_EXEC_TIME, execTime);
        }

        /**
         * Performs the JSON uploading to a Jira instance.
         *
         * @param jsonObject
         *            JSON object containing JSon to upload
         * @return Response status code of the uploading
         * @throws InterruptedException
         *             If the processing is interrupted.
         */
        private int performJsonUpload(final JSONObject jsonObject) throws InterruptedException {
            logger.info("Sending JSON to Jira instance: {}", jsonObject.toString());
            Thread.sleep(generateSleepValue());
            final int statusCode = getResponseStatusCode();
            return statusCode;
        }

        /** Lower bound for generated sleep value. */
        private static final int SLEEP_LOWER_BOUND = 10;
        /** Upper bound for generated sleep value. */
        private static final int SLEEP_UPPER_BOUND = 100;

        /**
         * Generates a random sleep value between 10 and 100 (milliseconds).
         *
         * @return randomly generated sleep value
         */
        private long generateSleepValue() {
            return SLEEP_LOWER_BOUND
                    + (long) (Math.random() * (SLEEP_UPPER_BOUND - SLEEP_LOWER_BOUND));
        }

        /** Probability of generating a status code of an error. */
        private static final double STATUS_CODE_ERROR_PROBABILITY = 0.01;

        /**
         * Returns a response status code. Returns status code 200 with a probability of 99%, and
         * status code 403 with a probability of 1%.
         *
         * @return response status code
         */
        private int getResponseStatusCode() {
            final double random = Math.random();
            int statusCode;
            if (random <= STATUS_CODE_ERROR_PROBABILITY) {
                statusCode = STATUS_CODE_FORBIDDEN;
            } else {
                statusCode = STATUS_CODE_SUCCESS;
            }
            return statusCode;
        }
    }
}
