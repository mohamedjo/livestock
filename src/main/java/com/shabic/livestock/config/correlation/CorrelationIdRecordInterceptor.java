package com.shabic.livestock.config.correlation;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.RecordInterceptor;

public class CorrelationIdRecordInterceptor implements RecordInterceptor<Object, Object> {

	@Override
	public ConsumerRecord<Object, Object> intercept(ConsumerRecord<Object, Object> record, Consumer<Object, Object> consumer) {
		CorrelationIdKafka.fromRecord(record).ifPresent(CorrelationIdContext::set);
		return record;
	}

	@Override
	public void success(ConsumerRecord<Object, Object> record, Consumer<Object, Object> consumer) {
		CorrelationIdContext.clear();
	}

	@Override
	public void failure(ConsumerRecord<Object, Object> record, Exception exception, Consumer<Object, Object> consumer) {
		CorrelationIdContext.clear();
	}
}
