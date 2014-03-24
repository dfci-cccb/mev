package edu.dfci.cccb.mev.kmeans.rest.configuration;

import lombok.ToString;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter;

@ToString
@Configuration
@ComponentScan ("edu.dfci.cccb.mev.limma.rest.controllers")
public class KmeansRestConfiguration extends MevRestConfigurerAdapter{

}
