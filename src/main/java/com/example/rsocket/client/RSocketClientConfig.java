package com.example.rsocket.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@Configuration
public class RSocketClientConfig {

    @Value("${spring.rsocket.server.address}")
    private String host;
    @Value("${spring.rsocket.server.port}")
    private int port;

    //********************************************
    // Actual connect and use the requester
    //********************************************

    //@Profile("tcpClient")
    //@Bean
    public RSocketRequester tcpClientRequester(RSocketRequester.Builder rSocketRequesterBuilder) {
        return rSocketRequesterBuilder.connectTcp(host, port)
                .block(Duration.ofSeconds(5));
    }

    //@Profile("webSocketClient")
    @Bean
    public RSocketRequester webSocketClientRequester(RSocketRequester.Builder rSocketRequesterBuilder,
                                                     RSocketStrategies strategies) {
        return RSocketRequester.builder().rsocketStrategies(strategies)
                .connectWebSocket(URI.create("ws://localhost:8081/rsocket"))
                .block(Duration.ofSeconds(5));
    }

    //********************************************
    // Deferred to connect and use the requester
    //********************************************

    //@Profile("monoTCPClient")
    //@Bean
    public Mono<RSocketRequester> monoTCPClientRequester(RSocketRequester.Builder rSocketRequesterBuilder) {
        return rSocketRequesterBuilder.connectTcp(host, port);
    }

    //@Profile("monoWebSocketClient")
    //@Bean
    public Mono<RSocketRequester> monoWebSocketClientRequester(RSocketRequester.Builder rSocketRequesterBuilder,
                                                               RSocketStrategies strategies) {
        return RSocketRequester.builder().rsocketStrategies(strategies)
                .connectWebSocket(URI.create("ws://localhost:8081/rsocket"));
    }
}
