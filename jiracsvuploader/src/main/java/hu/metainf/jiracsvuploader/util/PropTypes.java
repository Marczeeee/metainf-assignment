package hu.metainf.jiracsvuploader.util;

/**
 * Utility class holding property type identifier values.
 *
 */
public final class PropTypes {
    /** Ctor. */
    private PropTypes() {}

    /** Property identifier for application start timestamp value. */
    public static final String APP_START_TIMESTAMP = "app.start.timestamp";
    /** Property identifier for application ending timestamp value. */
    public static final String APP_END_TIMESTAMP = "app.end.timestamp";
    /** Shortest execution time. */
    public static final String MIN_EXEC_TIME = "exec.time.min";
    /** Longest execution time. */
    public static final String MAX_EXEC_TIME = "exec.time.max";
    /** Average execution time. */
    public static final String AVG_EXEC_TIME = "exec.time.avg";
    /** Total time taken by waiting in queue. */
    public static final String TOTAL_QUEUE_TIME = "queue.time.total";
    /** Number of processed rows. */
    public static final String PROCESSED_ROW_NR = "row.processed.nr";
    /** Number of uploaded rows. */
    public static final String UPLOADED_ROW_NR = "row.uploaded.nr";
    /** Number of failed row uploads. */
    public static final String FAILED_ROW_UPLOAD_NR = "row.failed.upload.nr";
}
