package rencanakan.id.talentpool.configs;

import datadog.opentracing.DDTracer;
import io.opentracing.util.GlobalTracer;import io.opentracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Bean
    public Tracer tracer() {
        // Initialize the Datadog tracer
        Tracer tracer = new DDTracer();
        // Register the tracer with GlobalTracer (this makes it accessible globally)
        GlobalTracer.registerIfAbsent(tracer);
        return tracer;
    }
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", c -> true);
    }
}