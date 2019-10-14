package com.code.async.message.client.util;


import com.code.async.message.client.constants.Config;
import com.code.async.message.client.constants.Constants;
import io.opentracing.*;
import io.opentracing.propagation.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class CodeTracer implements Tracer {
    private static final Logger logger = LoggerFactory.getLogger(CodeTracer.class);

    private final String ip;

    private final String hostName;

    private final String serviceName;

    private final String version;

    private final PropagationRegistry propagationRegistry;

    private final Reporter reporter;
    private final Clock clock;
    private final ActiveSpanSource activeSpanSource;
    private Sampler sampler;

    public CodeTracer(Builder builder) {
        this.propagationRegistry = builder.propagationRegistry;
        this.activeSpanSource = builder.activeSpanSource;
        this.serviceName = builder.serviceName;
        this.version = Version.getVersion();
        this.reporter = builder.reporter;
        this.sampler = builder.sampler;
        this.clock = builder.clock;
        this.ip = NetUtils.getLocalHost();
        this.hostName = NetUtils.getHostName();
    }

    @Override
    public io.opentracing.Tracer.SpanBuilder buildSpan(String operationName) {
        return new SpanBuilder(operationName);
    }

    @Override
    public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
        Injector<C> injector = propagationRegistry.getInjector(format);
        if (injector == null) {
            logger.error("Unsupported format:{}", format.toString());
            return;
        }
        if (!(spanContext instanceof CodeSpanContext)) {
            logger.error("Unsupported spanContext:{}", spanContext.getClass().getName());
            return;
        }
        injector.inject((CodeSpanContext) spanContext, carrier);
    }

    @Override
    public <C> SpanContext extract(Format<C> format, C carrier) {
        Extractor<C> extractor = propagationRegistry.getExtractor(format);
        if (extractor == null) {
            logger.error("Unsupported format:{}", format.toString());
            return null;
        }
        return extractor.extract(carrier);
    }

    @Override
    public ActiveSpan activeSpan() {
        return activeSpanSource.activeSpan();
    }

    @Override
    public ActiveSpan makeActive(Span span) {
        return activeSpanSource.makeActive(span);
    }

    public void close() {
        reporter.close();
    }

    public static final class Builder {

        private final String serviceName;

        private final Reporter reporter;

        private final Sampler sampler;

        private Clock clock = new SystemClock();

        private PropagationRegistry propagationRegistry = new PropagationRegistry();

        private ActiveSpanSource activeSpanSource = new ThreadLocalActiveSpanSource();

        public Builder(String serviceName, Reporter reporter, Sampler sampler) {
            this.serviceName = serviceName;
            this.reporter = reporter;
            this.sampler = sampler;
            TextMapCodec textMapCodec = new TextMapCodec(false);
            this.registerInjector(Format.Builtin.TEXT_MAP, textMapCodec);
            this.registerExtractor(Format.Builtin.TEXT_MAP, textMapCodec);
            TextMapCodec httpCodec = new TextMapCodec(true);
            this.registerInjector(Format.Builtin.HTTP_HEADERS, httpCodec);
            this.registerExtractor(Format.Builtin.HTTP_HEADERS, httpCodec);
        }

        public <T> Builder registerInjector(Format<T> format, Injector<T> injector) {
            this.propagationRegistry.register(format, injector);
            return this;
        }

        public <T> Builder registerExtractor(Format<T> format, Extractor<T> extractor) {
            this.propagationRegistry.register(format, extractor);
            return this;
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public CodeTracer build() {
            return new CodeTracer(this);
        }
    }

    private static class PropagationRegistry {
        private final Map<Format<?>, Injector<?>> injectors = new HashMap<Format<?>, Injector<?>>();
        private final Map<Format<?>, Extractor<?>> extractors = new HashMap<Format<?>, Extractor<?>>();

        @SuppressWarnings("unchecked")
        <T> Injector<T> getInjector(Format<T> format) {
            return (Injector<T>) injectors.get(format);
        }

        @SuppressWarnings("unchecked")
        <T> Extractor<T> getExtractor(Format<T> format) {
            return (Extractor<T>) extractors.get(format);
        }

        public <T> void register(Format<T> format, Injector<T> injector) {
            injectors.put(format, injector);
        }

        public <T> void register(Format<T> format, Extractor<T> extractor) {
            extractors.put(format, extractor);
        }
    }

    public class SpanBuilder implements io.opentracing.Tracer.SpanBuilder {

        private final String operationName;
        private final Map<String, String> stringTags = new HashMap<>();
        private final Map<String, Boolean> booleanTags = new HashMap<>();
        private final Map<String, Number> numberTags = new HashMap<>();
        private List<Reference> references = new ArrayList<Reference>();
        private boolean ignoreActiveSpan = false;

        private String traceId;

        /**
         * microseconds
         */
        private Long startTimestamp;

        public SpanBuilder(String operationName) {
            this.operationName = operationName;
        }

        @Override
        public io.opentracing.Tracer.SpanBuilder asChildOf(SpanContext parent) {
            return addReference(References.CHILD_OF, parent);
        }

        @Override
        public io.opentracing.Tracer.SpanBuilder asChildOf(BaseSpan<?> parent) {
            return addReference(References.CHILD_OF, parent.context());
        }

        @Override
        public io.opentracing.Tracer.SpanBuilder addReference(String referenceType, SpanContext referencedContext) {
            if (!(referencedContext instanceof CodeSpanContext)) {
                return this;
            }
            if (!References.CHILD_OF.equals(referenceType)
                    && !References.FOLLOWS_FROM.equals(referenceType)) {
                return this;
            }
            references.add(new Reference(referenceType, (CodeSpanContext) referencedContext));
            return this;
        }

        @Override
        public io.opentracing.Tracer.SpanBuilder ignoreActiveSpan() {
            ignoreActiveSpan = true;
            return this;
        }

        public io.opentracing.Tracer.SpanBuilder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        @Override
        public io.opentracing.Tracer.SpanBuilder withTag(String key, String value) {
            stringTags.put(key, value);
            return this;
        }

        @Override
        public io.opentracing.Tracer.SpanBuilder withTag(String key, boolean value) {
            booleanTags.put(key, value);
            return this;
        }

        @Override
        public io.opentracing.Tracer.SpanBuilder withTag(String key, Number value) {
            numberTags.put(key, value);
            return this;
        }

        @Override
        public io.opentracing.Tracer.SpanBuilder withStartTimestamp(long microseconds) {
            startTimestamp = microseconds;
            return this;
        }

        @Override
        public ActiveSpan startActive() {
            return makeActive(startManual());
        }

        @Override
        public Span startManual() {
            CodeSpanContext spanContext;
            if (references.isEmpty() && !ignoreActiveSpan && activeSpan() != null) {
                addReference(References.CHILD_OF, activeSpan().context());
            }
            CodeSpanContext parentContext = getParentContext();
            if (parentContext == null) {
                spanContext = createNewContext();
            } else {
                spanContext = createChildContext(parentContext);
            }
            Span span = new CodeSpan(
                    spanContext.getTraceId(),
                    spanContext.getSpanId(),
                    spanContext.getParentId(),
                    spanContext.getParentName(),
                    operationName,
                    startTimestamp,
                    spanContext,
                    reporter,
                    clock)
                    .setTag(Constants.CLIENT_VERSION_TAG_KEY, version)
                    .setTag(Constants.TRACER_HOSTNAME_TAG_KEY, hostName)
                    .setTag(Constants.TRACER_IP_TAG_KEY, ip)
                    .setTag(Constants.SERVICE_NAME, serviceName)
                    .setTag(Constants.RESPONSE_STATUS, Config.STATUS.OK.getType());
            stringTags.entrySet().forEach((entry) -> span.setTag(entry.getKey(), entry.getValue()));
            booleanTags.entrySet().forEach((entry) -> span.setTag(entry.getKey(), entry.getValue()));
            numberTags.entrySet().forEach((entry) -> span.setTag(entry.getKey(), entry.getValue()));
            return span;
        }

        private CodeSpanContext createChildContext(CodeSpanContext parentContext) {
            return new CodeSpanContext(
                    parentContext.getTraceId(),
                    Ids.spanId(),
                    parentContext.getSpanId(),
                    serviceName,
                    parentContext.getServiceName(),
                    parentContext.isSampler(),
                    createChildBaggage(parentContext));
        }

        private Map<String, String> createChildBaggage(SpanContext spanContext) {
            Map<String, String> baggages = new HashMap<>();
            Iterator<Map.Entry<String, String>> baggageItems = spanContext.baggageItems().iterator();
            if (baggageItems.hasNext()) {
                Map.Entry<String, String> baggage = baggageItems.next();
                baggages.put(baggage.getKey(), baggage.getValue());
            }
            return baggages;
        }

        private CodeSpanContext getParentContext() {
            for (Reference reference : references) {
                if (References.CHILD_OF.equals(reference.getType())) {
                    return reference.getDfireSpanContext();
                }
            }
            return null;
        }

        private CodeSpanContext createNewContext() {
            if (StringUtils.isEmpty(traceId)) {
                traceId = Ids.uniqueId();
            }
            return new CodeSpanContext(
                    traceId,
                    Ids.spanId(),
                    Constants.SPAN_ID_INIT,
                    serviceName,
                    null,
                    sampler.isSampled());
        }

        /**
         * @deprecated use {@link #startManual} or {@link #startActive} instead.
         */
        @Override
        @Deprecated
        public Span start() {
            return startManual();
        }
    }

}
