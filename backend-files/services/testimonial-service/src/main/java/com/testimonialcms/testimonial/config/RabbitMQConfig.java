package com.testimonialcms.testimonial.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE    = "testimonial.events";
    public static final String QUEUE_SEARCH    = "search.testimonial.queue";
    public static final String QUEUE_ANALYTICS = "analytics.testimonial.queue";

    @Bean
    public TopicExchange testimonialExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue searchQueue() {
        return QueueBuilder.durable(QUEUE_SEARCH).build();
    }

    @Bean
    public Queue analyticsQueue() {
        return QueueBuilder.durable(QUEUE_ANALYTICS).build();
    }

    @Bean
    public Binding searchBinding() {
        return BindingBuilder.bind(searchQueue())
                .to(testimonialExchange())
                .with("testimonial.*");
    }

    @Bean
    public Binding analyticsBinding() {
        return BindingBuilder.bind(analyticsQueue())
                .to(testimonialExchange())
                .with("testimonial.*");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
