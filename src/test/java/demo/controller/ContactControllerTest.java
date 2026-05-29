package demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import demo.config.SecurityConfig;
import demo.service.ContactRateLimiter;
import demo.service.ContactRequestService;
import demo.service.ProductService;

@WebMvcTest(PublicController.class)
@Import(SecurityConfig.class)
class ContactControllerTest {

    @Autowired MockMvc mvc;

    @MockBean ProductService productService;            // used by GlobalModelAttributes (nav)
    @MockBean ContactRequestService contactRequestService;
    @MockBean ContactRateLimiter contactRateLimiter;

    @BeforeEach
    void stubNav() {
        when(productService.findAll()).thenReturn(List.of());
        when(productService.getRangeGroups(any())).thenReturn(List.of());
    }

    @Test
    void submit_savesRequestAndShowsSuccess() throws Exception {
        when(contactRateLimiter.isAllowed(any())).thenReturn(true);

        mvc.perform(post("/contact").with(csrf())
                .param("fullName", "Jane Doe")
                .param("phone", "0900000000")
                .param("email", "jane@example.com")
                .param("message", "I need a quote"))
           .andExpect(status().isOk())
           .andExpect(model().attribute("success", true));

        verify(contactRequestService).save(any());
    }

    @Test
    void honeypotFilled_isSilentlyAcceptedButNotSaved() throws Exception {
        mvc.perform(post("/contact").with(csrf())
                .param("website", "spam-bot")
                .param("fullName", "Bot"))
           .andExpect(status().isOk())
           .andExpect(model().attribute("success", true));

        verifyNoInteractions(contactRequestService);
    }

    @Test
    void rateLimited_showsErrorAndDoesNotSave() throws Exception {
        when(contactRateLimiter.isAllowed(any())).thenReturn(false);

        mvc.perform(post("/contact").with(csrf())
                .param("fullName", "Jane Doe"))
           .andExpect(status().isOk())
           .andExpect(model().attribute("rateLimitError", true));

        verify(contactRequestService, never()).save(any());
    }
}
