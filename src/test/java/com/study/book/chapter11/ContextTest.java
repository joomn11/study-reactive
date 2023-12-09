package com.study.book.chapter11;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

@Slf4j
public class ContextTest {

    public static final String HEADER_AUTH_TOKEN = "authToken";

    private static Mono<String> postBook(Mono<Book> book) {
        Mono<String> result = Mono.zip(book,
                                       Mono.deferContextual(
                                           ctx -> Mono.just(ctx.get(HEADER_AUTH_TOKEN))
                                       )
                                  )
                                  .flatMap(tuple -> {
                                      String response = "POST the book(" + tuple.getT1().getBookName() + ", " + tuple.getT1().getAuthor() + ") with token: " + tuple.getT2();
                                      return Mono.just(response);
                                  });
        return result;
    }

    @Test
    void contextBasic() throws InterruptedException {
        Mono.deferContextual(ctx ->
                                 Mono.just("Hello " + ctx.get("firstName"))
                                     .doOnNext(data -> log.info("# just doOnNext : {}", data))
            )
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel())
            .transformDeferredContextual(
                (mono, ctx) -> mono.map(data -> data + " " + ctx.get("lastName"))
            )
            .contextWrite(context -> context.put("lastName", "Jobs"))
            .contextWrite(context -> context.put("firstName", "Steve"))
            .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(100L);
    }

    @Test
    void contextCommonApi() throws InterruptedException {
        final String key1 = "company";
        final String key2 = "firstName";
        final String key3 = "lastName";

        Mono.deferContextual(
                ctx -> Mono.just(ctx.get(key1) + ", " + ctx.get(key2) + ", " + ctx.get(key3))
            )
            .publishOn(Schedulers.parallel())
            .contextWrite(
                context -> context.putAll(Context.of(key2, "Steve", key3, "Jobs").readOnly())
            )
            .contextWrite(context -> context.put(key1, "Apple"))
            .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(100L);
    }

    @Test
    void contextViewCommonApi() throws InterruptedException {
        final String key1 = "company";
        final String key2 = "firstName";
        final String key3 = "lastName";
        Mono.deferContextual(
                ctx -> Mono.just(ctx.get(key1) + ", " +
                                     ctx.getOrEmpty(key2).orElse("no firstName ") + ", " +
                                     ctx.getOrDefault(key3, "no lastName"))
            )
            .publishOn(Schedulers.parallel())
            .contextWrite(context -> context.put(key1, "Apple"))
            .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(100L);
    }

    /**
     * context는 구독당 하나!
     * @throws InterruptedException
     */
    @Test
    void contextFeature1() throws InterruptedException {
        final String key1 = "company";

        Mono<String> mono = Mono.deferContextual(
                                    ctx -> Mono.just(" Company: " + ctx.get(key1))
                                )
                                .publishOn(Schedulers.parallel());

        mono.contextWrite(context -> context.put(key1, "Apple"))
            .subscribe(data -> log.info("# subscribe1 onNext: {}", data));

        mono.contextWrite(context -> context.put(key1, "Microsoft"))
            .subscribe(data -> log.info("# subscribe2 onNext: {}", data));

        Thread.sleep(100L);
    }

    /**
     * context는 operator 체인상 아래에서 위로 전파 된다
     * @throws InterruptedException
     */
    @Test
    void contextFeature2() throws InterruptedException {
        final String key1 = "company";
        final String key2 = "name";

        Mono.deferContextual(
                ctx -> Mono.just(ctx.get(key1))
            )
            .publishOn(Schedulers.parallel())
            .contextWrite(context -> context.put(key2, "Bill - i am too late"))
            .transformDeferredContextual(
                (mono, ctx) -> mono.map(data -> data + ", " + ctx.getOrDefault(key2, "Steve"))
            )
            .contextWrite(context -> context.put(key1, "Apple"))
            .subscribe(data -> log.info("# subscribe1 onNext: {}", data));

        Thread.sleep(100L);
    }

    /**
     * inner sequence에는 외부에서 접근 불가하다
     * @throws InterruptedException
     */
    @Test
    void contextFeature3() throws InterruptedException {
        final String key1 = "company";

        Mono.just("Steve")
//            .transformDeferredContextual(
//                (stringMono, ctx) -> ctx.get("role")
//            )
            .flatMap(
                name -> Mono.deferContextual(
                    ctx -> Mono.just(ctx.get(key1) + ", " + name)
                               .transformDeferredContextual(
                                   (mono, innerCtx) -> mono.map(data -> data + " " + innerCtx.get("role"))
                               )
                               .contextWrite(context -> context.put("role", "CEO"))
                )
            )
            .publishOn(Schedulers.parallel())
            .contextWrite(context -> context.put(key1, "Apple"))
            .subscribe(data -> log.info("# subscribe1 onNext: {}", data));

        Thread.sleep(100L);
    }

    @Test
    void contextUse() {
        Mono<String> mono = postBook(
            Mono.just(
                new Book("abce-1111-2334-1234", "Reactor's Bible", "Kevin")
            )
        ).contextWrite(Context.of(HEADER_AUTH_TOKEN, "eyJhCdwiOi"));

        mono.subscribe(data -> log.info("# onNext: {}", data));
    }

    @AllArgsConstructor
    @Data
    class Book {

        private String isbn;
        private String bookName;
        private String author;
    }
}