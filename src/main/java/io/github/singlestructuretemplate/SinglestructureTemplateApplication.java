package io.github.singlestructuretemplate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.github.singlestructuretemplate.mapper")
public class SinglestructureTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SinglestructureTemplateApplication.class, args);
    }

}
