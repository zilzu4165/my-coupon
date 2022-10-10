package me.zilzu.mycoupon.application.controller;

import me.zilzu.mycoupon.api.controller.CouponController;
import me.zilzu.mycoupon.application.service.CouponCreationRequest;
import me.zilzu.mycoupon.application.service.CouponService;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.storage.NewCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CouponControllerTest {


    @Mock
    private CouponService couponService;        // 가짜 mock 객체

    @Mock
    private NewCouponRepository newCouponRepository;

    @InjectMocks
    private CouponController couponController;  // 테스트 대상


    @Autowired
    private MockMvc mockMvc; // http 호출을 위한


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/coupons/{id} 호출하면 CouponRetrieveResultResponse 로 응답이 와야한다.")
    void mock1() throws Exception {
        createCoupons(100, 100);

        mockMvc.perform(get("/api/v1/coupons/")
                        .accept(MediaType.APPLICATION_JSON).queryParam("page", "0", "size", "5"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public void createCoupons(int count, int nThreads) throws InterruptedException {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, null, null, null);

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads); // threadPoolSize 100

        List<CompletableFuture> futures = IntStream.range(0, count)
                .boxed()
                .map(target ->
                        CompletableFuture.runAsync(() ->
                                couponService.create(couponCreationRequest), executorService))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        executorService.shutdown();
    }
}
