package gov.va.vro.bip.service;

import com.datadog.api.client.ApiClient;
import com.datadog.api.client.v1.model.*;
import com.datadog.api.client.v1.api.MetricsApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@Slf4j
public class MetricLoggerService {

    @Value("${env:dev}")
    private String ENV_VALUE;
    private static final String APP_PREFIX = "vro_bip";
    private static final String SERVICE_TAG = "service:vro-svc-bip-api";

    public enum METRIC {
        REQUEST,
        REQUEST_DURATION,
        RESPONSE_COMPLETE,
        RESPONSE_ERROR
    }

    private final MetricsApi metricsApi;

    public MetricLoggerService(){
        metricsApi = new MetricsApi(ApiClient.getDefaultApiClient());
    }

    private static String getFullMetricString(METRIC metric){
        return String.format("%s.%s", APP_PREFIX, metric.name().toLowerCase());
    }

    public void submitCount(METRIC metric, String[] tags){
        submitCount(metric, 1.0, tags);
    }

    public void submitCount(METRIC metric, double value, String[] tags){
        Series dataPointSeries = new Series(getFullMetricString(metric),
                Collections.singletonList(Collections.singletonList(value)));
        dataPointSeries.setType("count");

        List<String> tagList = Arrays.asList(ENV_VALUE, SERVICE_TAG);
        if(tags != null){
            tagList.addAll(Arrays.asList(tags));
        }
        dataPointSeries.setTags(tagList);

        MetricsPayload metricsPayload = new MetricsPayload()
                .series(Collections.singletonList(dataPointSeries));

        try {
            IntakePayloadAccepted payloadResult = metricsApi.submitMetrics(metricsPayload);
            log.info(String.format("submitted %s: %s", dataPointSeries.getMetric(), payloadResult.getStatus()));
        } catch (Exception e) {
            log.error(String.format("exception submitting %s: %s", dataPointSeries.getMetric(), e.getMessage()));
        }
    }

    public void submitDistribution(METRIC metric, double value, String[] tags){
        DistributionPointItem distributionPointItem = new DistributionPointItem(value);
        DistributionPointsSeries dataPointSeries = new DistributionPointsSeries(getFullMetricString(metric, true),
                Collections.singletonList(Collections.singletonList(distributionPointItem)));
        dataPointSeries.setType(DistributionPointsType.DISTRIBUTION);

        List<String> tagList = Arrays.asList(ENV_VALUE, SERVICE_TAG);
        if(tags != null){
            tagList.addAll(Arrays.asList(tags));
        }
        dataPointSeries.setTags(tagList);

        try {
            DistributionPointsPayload distributionPointsPayload =
                    new DistributionPointsPayload()
                            .series(
                                    Collections.singletonList(dataPointSeries));
            IntakePayloadAccepted payloadResult = metricsApi.submitDistributionPoints(distributionPointsPayload);
            log.info(String.format("submitted %s: %s", dataPointSeries.getMetric(), payloadResult.getStatus()));
        } catch (Exception e) {
            log.error(String.format("exception submitting %s: %s", dataPointSeries.getMetric(), e.getMessage()));
        }
    }
}
