package com.app.controllers;


import com.app.config.SecurityConfig;
import com.app.security.JWTFilter;
import com.app.security.JWTUtil;
import com.app.services.AddressService;
import com.app.model.User;
import com.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AddressControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private JWTFilter jwtFilter;

    @MockBean
    private SecurityConfig securityConfig;

    @MockBean
    private AddressService addressService;


    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;


    @BeforeEach
    void setUp() throws Exception {
        User mockAuthUser = new User();
        mockAuthUser.setUserId(999L);
        //mockAuthUser.setUsername("admin");
        /*
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(mockAuthUser);

        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(tokenAuthenticationFilter).doFilterInternal(
                Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class),
                Mockito.any(FilterChain.class)
        );
        */

    }


}
