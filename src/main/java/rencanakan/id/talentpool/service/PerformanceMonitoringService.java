package rencanakan.id.talentpool.service;



import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

@Service
public class PerformanceMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringService.class);
    private final Tracer tracer;

    public PerformanceMonitoringService() {
        this.tracer = GlobalTracer.get();
    }

    /**
     * Track the performance of a method execution
     *
     * @param operationName The name of the operation being tracked
     * @param supplier      The function to execute and track
     * @param tags          Additional tags to add to the span
     * @return The result of the supplier function
     */
    public <T> T trackOperation(String operationName, Supplier<T> supplier, Map<String, Object> tags) {
        Span span = tracer.buildSpan(operationName).start();
        long startTime = System.currentTimeMillis();

        try {
            // Add tags to the span
            if (tags != null) {
                tags.forEach((key, value) -> {
                    if (value != null) {
                        span.setTag(key, value.toString());
                    }
                });
            }

            // Execute the operation
            T result = supplier.get();

            return result;
        } catch (Exception e) {
            // Tag error information
            span.setTag("error", true);
            span.setTag("error.message", e.getMessage());
            span.setTag("error.type", e.getClass().getName());

            // Log the error
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("event", "error");
            errorDetails.put("message", e.getMessage());
            errorDetails.put("operation", operationName);
            span.log(errorDetails);

            throw e;
        } finally {
            // Record execution time
            long executionTime = System.currentTimeMillis() - startTime;
            span.setTag("execution_time_ms", executionTime);

            // Log performance metrics
            logger.debug("{} execution time: {} ms", operationName, executionTime);

            span.finish();
        }
    }

    /**
     * Track the performance of a method execution that doesn't return a value
     *
     * @param operationName The name of the operation being tracked
     * @param runnable      The function to execute and track
     * @param tags          Additional tags to add to the span
     */
    public void trackOperation(String operationName, Runnable runnable, Map<String, Object> tags) {
        Span span = tracer.buildSpan(operationName).start();
        long startTime = System.currentTimeMillis();

        try {
            // Add tags to the span
            if (tags != null) {
                tags.forEach((key, value) -> {
                    if (value != null) {
                        span.setTag(key, value.toString());
                    }
                });
            }

            // Execute the operation
            runnable.run();
        } catch (Exception e) {
            // Tag error information
            span.setTag("error", true);
            span.setTag("error.message", e.getMessage());
            span.setTag("error.type", e.getClass().getName());

            // Log the error
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("event", "error");
            errorDetails.put("message", e.getMessage());
            errorDetails.put("operation", operationName);
            span.log(errorDetails);

            throw e;
        } finally {
            // Record execution time
            long executionTime = System.currentTimeMillis() - startTime;
            span.setTag("execution_time_ms", executionTime);

            // Log performance metrics
            logger.debug("{} execution time: {} ms", operationName, executionTime);

            span.finish();
        }
    }

    /**
     * Create a child span for a sub-operation
     *
     * @param parentSpan    The parent span
     * @param operationName The name of the sub-operation
     * @param callable      The function to execute and track
     * @return The result of the callable function
     */
    public <T> T trackSubOperation(Span parentSpan, String operationName, Callable<T> callable) {
        Span span = tracer.buildSpan(operationName).asChildOf(parentSpan).start();
        long startTime = System.currentTimeMillis();

        try {
            // Execute the operation
            return callable.call();
        } catch (Exception e) {
            // Tag error information
            span.setTag("error", true);
            span.setTag("error.message", e.getMessage());
            span.setTag("error.type", e.getClass().getName());

            // Log the error
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("event", "error");
            errorDetails.put("message", e.getMessage());
            errorDetails.put("operation", operationName);
            span.log(errorDetails);

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            // Record execution time
            long executionTime = System.currentTimeMillis() - startTime;
            span.setTag("execution_time_ms", executionTime);

            // Log performance metrics
            logger.debug("{} execution time: {} ms", operationName, executionTime);

            span.finish();
        }
    }
}