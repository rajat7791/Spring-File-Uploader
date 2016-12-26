package org.webapp.config.root;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "org.webapp.service" })
public class RootContextConfig {

}
