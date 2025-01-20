package com.simsimbookstore.apiserver.common.config;

import com.simsimbookstore.apiserver.common.dto.RabbitMqProperty;
import com.simsimbookstore.apiserver.coupons.mqConsumer.CouponMqConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    public static final String EXCHANGE_NAME = "simsimbooks.exchange";
    public static final String COUPON_ISSUE_QUEUE_ROUTING_KEY = "routing_key_issue_coupon";
    public static final String COUPON_DELETE_QUEUE_ROUTING_KEY = "routing_key_delete_coupon";
    public static final String COUPON_EXPIRE_QUEUE_ROUTING_KEY = "routing_key_expire_coupon";

    private final RabbitMqProperty rabbitMqProperty;
    private final Map<String, String> secretMap = new HashMap<>();
    private final KeyConfig keyConfig;


    @Bean
    public Map<String,String> setDecryptedRabbitMqSecret(){
        String secretKey = keyConfig.keyStore(rabbitMqProperty.getSecretKey());
        String[] split = secretKey.split("\n");
        for (String s : split) {
            String key = s.split(":")[0].trim();
            String value = s.split(":")[1].trim();
            secretMap.put(key, value);
        }
        return secretMap;
    }

    // ConnectionFactory에 암호화된 정보 정의
    @Bean
    @DependsOn("setDecryptedRabbitMqSecret")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(secretMap.get("host"));
        factory.setPort(rabbitMqProperty.getPort());
        factory.setUsername(secretMap.get("username"));
        factory.setPassword(secretMap.get("password"));
        factory.setVirtualHost(rabbitMqProperty.getVirtualHost());
        return factory;
    }
    // 메시지 컨버터
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate 정의
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    // RabbitAdmin 빈 정의
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // RabbitAdmin이 애플리케이션 시작 시 자동으로 리소스를 선언하도록 설정
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public Queue couponIssueQueue() {
        return new Queue(CouponMqConsumer.COUPON_ISSUE_QUEUE_NAME, true);
    }
    @Bean
    public Queue couponExpireQueue() {
        return new Queue(CouponMqConsumer.COUPON_EXPIRE_QUEUE_NAME, true);
    }
    @Bean
    public Queue couponDeleteQueue() {
        return new Queue("simsimbooks.coupon.delete.queue", true);
    }
    @Bean
    public DirectExchange directExchange() { //Exchange
        return new DirectExchange(EXCHANGE_NAME);
    }

    //아래부터는 Exchange와 queue 바인딩

    @Bean
    public Binding bindingCouponIssueQueue(Queue couponIssueQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(couponIssueQueue).to(directExchange).with(COUPON_ISSUE_QUEUE_ROUTING_KEY);
    }

    @Bean
    public Binding bindingCouponExpireQueue(Queue couponExpireQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(couponExpireQueue).to(directExchange).with(COUPON_EXPIRE_QUEUE_ROUTING_KEY);
    }
    @Bean
    public Binding bindingCouponDeleteQueue(Queue couponDeleteQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(couponDeleteQueue).to(directExchange).with(COUPON_DELETE_QUEUE_ROUTING_KEY);
    }
}


