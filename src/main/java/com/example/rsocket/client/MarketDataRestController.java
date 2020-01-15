package com.example.rsocket.client;

import com.example.rsocket.model.MarketData;
import com.example.rsocket.model.MarketDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Random;

@RestController
@Slf4j
public class MarketDataRestController {

    private final static Random random = new Random();
    private RSocketRequester rSocketRequester;

    public MarketDataRestController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @GetMapping(value = "/current/{stock}")
    public Publisher<MarketData> current(@PathVariable("stock") String stock) {
        return rSocketRequester.route("currentMarketData")
                .data(new MarketDataRequest(stock))
                .retrieveMono(MarketData.class);
    }

    @GetMapping(value = "/feed/{stock}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<MarketData> feed(@PathVariable("stock") String stock) {
        return rSocketRequester.route("feedMarketData")
                .data(new MarketDataRequest(stock))
                .retrieveFlux(MarketData.class);
    }

    @GetMapping(value = "/collect")
    public Publisher<Void> collect() {
        return rSocketRequester.route("collectMarketData")
                .data(getMarketData())
                .send();
    }

    private MarketData getMarketData() {
        return new MarketData("X", random.nextInt(10));
    }

    /**
     * Welcome landing page
     * @return
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Mono<String>> home() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Hello RSocket Client!</h1>");
        sb.append("<h3>GET: /current/{stock}</h3>");
        sb.append("<ul>");
        sb.append("<li><a href='/current/apple'>/current/apple</a></li>");
        sb.append("<li><a href='/current/google'>/current/google</a></li>");
        sb.append("<li><a href='/current/facebook'>/current/facebook</a></li>");
        sb.append("</ul>");
        sb.append("<h3>GET: /feed/{stock}</h3>");
        sb.append("<ul>");
        sb.append("<li><a href='/feed/apple'>/feed/apple</a></li>");
        sb.append("<li><a href='/feed/google'>/feed/google</a></li>");
        sb.append("<li><a href='/feed/facebook'>/feed/facebook</a></li>");
        sb.append("</ul>");
        sb.append("<h3>GET: /collect ONE-WAY - SEND stock request to the RSocket Server</h3>");
        sb.append("<ul>");
        sb.append("<li><a href='/collect'>/collect</a></li>");
        sb.append("</ul>");
        return ResponseEntity.ok(Mono.just(sb.toString()));
    }
}