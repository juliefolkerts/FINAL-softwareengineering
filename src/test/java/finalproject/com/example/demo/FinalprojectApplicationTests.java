package finalproject.com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = FinalprojectApplicationTests.NoDataSourceTestConfig.class)
class FinalprojectApplicationTests {

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            LiquibaseAutoConfiguration.class
    })
    static class NoDataSourceTestConfig {
    }

    @Test
    void contextLoads() {
    }

}