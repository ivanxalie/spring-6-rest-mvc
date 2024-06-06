package guru.springframework.spring6restmvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

@SpringBootTest
class Spring6RestMvcApplicationTests {

    @MockBean
    private CacheManager manager;

    @Test
    void contextLoads() {
    }

}
