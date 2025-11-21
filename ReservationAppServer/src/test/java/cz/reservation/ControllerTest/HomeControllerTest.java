package cz.reservation.ControllerTest;

import cz.reservation.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

//FIX!!! this is not good use
@WebMvcTest(HomeController.class)
@RunWith(SpringRunner.class)
class HomeControllerTest {

    @Mock
    HomeController homeController;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getMethodTest() {

        // default behavior
        when(homeController.show()).thenReturn("OK");

        //calling method
        String result = homeController.show();

        //assertation
        assertEquals("OK", result);





    }
}
