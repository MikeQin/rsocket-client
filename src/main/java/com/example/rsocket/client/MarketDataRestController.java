package com.example.rsocket.client;

import com.example.rsocket.model.MarketData;
import com.example.rsocket.model.MarketDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;

@RestController
@Slf4j
public class MarketDataRestController {

    private final static Random random = new Random();
    private RSocketRequester rSocketRequester;

    public MarketDataRestController(@Qualifier("webSocketClient") RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @GetMapping(value = "/current/{stock}")
    public Mono<MarketData> current(@PathVariable("stock") String stock) {
        return rSocketRequester.route("currentMarketData")
                .data(new MarketDataRequest(stock))
                .retrieveMono(MarketData.class);
    }

    @GetMapping(value = "/feed/{stock}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MarketData> feed(@PathVariable("stock") String stock) {
        return rSocketRequester.route("feedMarketData")
                .data(new MarketDataRequest(stock))
                .retrieveFlux(MarketData.class);
    }

    @GetMapping(value = "/send")
    public Mono<Void> send() {
        return rSocketRequester.route("collectMarketData")
                .data(getMarketData())
                .send();
    }

    private MarketData getMarketData() {

        return new MarketData("X", 50 + random.nextInt(51));
    }

    /**
     * Welcome landing page
     * @return ResponseEntity<Mono<String>>
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Mono<String>> home() {

        String html = "<html><title>RSocket Client Restful Endpoints</title>";
        html += "<body style='background-color:black; color:#FC8805'>";
        html += "<h1>Hello RSocket Client!</h1>";
        html += "<h3><pre>Request Response (1:1) | GET: /current/{stock}</pre></h3>";
        html += "<ul>";
        html += "<li><a href='/current/apple'>/current/apple</a></li>";
        html += "<li><a href='/current/google'>/current/google</a></li>";
        html += "<li><a href='/current/facebook'>/current/facebook</a></li>";
        html += "</ul>";
        html += "<h3><pre>Request Stream (1:M) | GET: /feed/{stock}</pre></h3>";
        html += "<ul>";
        html += "<li><a href='/feed/apple'>/feed/apple</a></li>";
        html += "<li><a href='/feed/google'>/feed/google</a></li>";
        html += "<li><a href='/feed/facebook'>/feed/facebook</a></li>";
        html += "</ul>";
        html += "<h3><pre>Fire & Forget (1:0) | GET: /send ONE-WAY - SEND MarketData to RSocket Server</h3></pre>";
        html += "<ul>";
        html += "<li><a href='/send'>/send</a></li>";
        html += "</ul>";
        html += "</body></html>";

        return ResponseEntity.ok(Mono.just(html));

    }
}