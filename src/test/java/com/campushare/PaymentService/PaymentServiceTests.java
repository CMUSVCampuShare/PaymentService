package com.campushare.PaymentService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.campushare.PaymentService.dto.User;
import com.campushare.PaymentService.exception.CannotGetUserException;
import com.campushare.PaymentService.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PaymentServiceTests {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPayPalId() throws CannotGetUserException {
        // 假设的User对象
        User mockUser = new User();
        mockUser.setAccount("mockedPayPalId");

        // 配置RestTemplate模拟行为
        when(restTemplate.getForEntity(anyString(), eq(User.class)))
                .thenReturn(new ResponseEntity<>(mockUser, HttpStatus.OK));

        // 测试PaymentService的getPayPalId方法
        String payPalId = paymentService.getPayPalId("mockUserId");
        assertEquals("mockedPayPalId", payPalId);
    }
}
