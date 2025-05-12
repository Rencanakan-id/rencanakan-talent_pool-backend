package rencanakan.id.talentpool.service;

import com.timgroup.statsd.NonBlockingStatsDClientBuilder;
import com.timgroup.statsd.StatsDClient;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final StatsDClient statsd;

    public MetricsService() {
        this.statsd = new NonBlockingStatsDClientBuilder()
                .prefix("talentpool")
                .hostname("localhost")
                .port(8125)
                .build();
    }

    public void trackApiRequest(String endpoint, String method, String userId) {
        statsd.incrementCounter("api.request", "endpoint:" + endpoint, "method:" + method, "user_id:" + userId);
    }

    public void trackApiResponse(String endpoint, int statusCode, long durationMs) {
        statsd.incrementCounter("api.response", "endpoint:" + endpoint, "status_code:" + statusCode);
        statsd.recordExecutionTime("api.response.time", durationMs, "endpoint:" + endpoint, "status_code:" + statusCode);
    }

    public void trackError(String endpoint, String errorType) {
        statsd.incrementCounter("api.error", "endpoint:" + endpoint, "type:" + errorType);
    }

    public void trackCertificateOperation(String operation, String userId) {
        statsd.incrementCounter("certificate.operation", "type:" + operation, "user_id:" + userId);
    }

    public void trackCertificateCount(String userId, int count) {
        statsd.gauge("certificate.count", count, "user_id:" + userId);
    }

    public void histogram(String metricName, int value, String... tags) {
        statsd.histogram(metricName, value, tags);
    }
}
