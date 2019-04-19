package hu.metainf.jiracsvuploader.stat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.metainf.jiracsvuploader.util.PropTypes;

/**
 * Class printing statistical information about an application run.
 *
 */
public class StatPrinter {
    /** {@link Logger} instance. */
    private final Logger logger = LoggerFactory.getLogger(StatPrinter.class);

    /**
     * Prints all statistical information available.
     */
    public void printAppStats() {
        printExecutionStats();
        printQueueStats();
        printRowStats();
    }

    /**
     * Prints uploading job execution stats.
     */
    private void printExecutionStats() {
        logger.info("Shortest execution time of a record: {}",
                StatData.getValue(PropTypes.MIN_EXEC_TIME));
        logger.info("Longest execution time of a record: {}",
                StatData.getValue(PropTypes.MAX_EXEC_TIME));
        logger.info("Average execution time of a record: {}",
                StatData.getValue(PropTypes.AVG_EXEC_TIME));
    }

    /**
     * Prints job queue stats.
     */
    private void printQueueStats() {
        logger.info("Total time in milliseconds jobs spent waiting in the queue: {}",
                StatData.getValue(PropTypes.TOTAL_QUEUE_TIME));
    }

    /**
     * Prints row processing stats.
     */
    private void printRowStats() {
        logger.info("Number of total processed rows: {}",
                StatData.getValue(PropTypes.PROCESSED_ROW_NR));
        logger.info("Number of total uploaded rows: {}",
                StatData.getValue(PropTypes.UPLOADED_ROW_NR));
        logger.info("Number of total failed row uploads: {}",
                StatData.getValue(PropTypes.FAILED_ROW_UPLOAD_NR));
    }
}
