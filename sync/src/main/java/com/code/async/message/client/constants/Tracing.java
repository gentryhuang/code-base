package com.code.async.message.client.constants;

import com.code.async.message.client.util.*;
import io.opentracing.Tracer;


/**
 * @author shunhua
 * @date 2019-10-14
 */
public class Tracing {

    private static final String SERVICE_NAME = InitFactory.getServiceName().trim();

    private static final Sampler SAMPLER = InitFactory.getSampler();

    private static final Reporter REPORTER = SAMPLER instanceof NoopSampler ? InitFactory.DEFAULT_REPORTER : InitFactory.getReporter();

    private static volatile Tracing current = null;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (current != null) {
                if (current.tracer != null) {
                    current.tracer.close();
                }
            }
        }));

    }

    private CodeTracer tracer;
    private String serviceName;
    private Sampler sampler;
    private Reporter reporter;
    private Clock clock;

    private Tracing(Builder builder) {
        this.serviceName = builder.serviceName;
        this.sampler = builder.sampler;
        this.reporter = builder.reporter;
        this.clock = builder.clock;
        this.tracer = createTracer();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Tracing current() {
        if (current == null) {
            synchronized (Tracing.class) {
                current = newBuilder().build();
            }
        }
        return current;
    }

    public Tracer tracer() {
        return tracer;
    }

    private CodeTracer createTracer() {
        return new CodeTracer.Builder(serviceName, reporter, sampler).clock(clock).build();
    }

    public String getServiceName() {
        return serviceName;
    }

    public Sampler getSampler() {
        return sampler;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public Clock getClock() {
        return clock;
    }

    public static class Builder {

        private String serviceName;

        private Sampler sampler;

        private Reporter reporter;

        private Clock clock;

        public Builder serviceName(String serviceName) {
            if (serviceName == null || serviceName.trim().length() == 0) {
                throw new NullPointerException("serviceName is empty");
            }
            this.serviceName = serviceName;
            return this;
        }

        public Builder sampler(Sampler sampler) {
            if (sampler == null) {
                throw new NullPointerException("sampler == null");
            }
            this.sampler = sampler;
            return this;
        }

        public Builder reporter(Reporter reporter) {
            if (reporter == null) {
                throw new NullPointerException("reporter ==null");
            }
            this.reporter = reporter;
            return this;
        }

        public Builder clock(Clock clock) {
            if (clock == null) {
                throw new NullPointerException("clock == null");
            }
            this.clock = clock;
            return this;
        }

        public Tracing build() {
            if (current != null) {
                return current;
            }
            synchronized (Tracing.class) {
                if (this.serviceName == null || this.serviceName.trim().length() == 0) {
                    this.serviceName = SERVICE_NAME;
                }
                if (this.sampler == null) {
                    this.sampler = SAMPLER;
                }
                if (this.reporter == null) {
                    this.reporter = REPORTER;
                }
                if (this.clock == null) {
                    this.clock = new SystemClock();
                }
                current = new Tracing(this);
                return current;
            }
        }
    }
}
