/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.dfci.cccb.mev.web.configuration.resolvers;

import static com.fasterxml.jackson.databind.ser.BeanSerializerFactory.instance;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;
import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

import edu.dfci.cccb.mev.configuration.rest.contract.JacksonConfigurer;
import edu.dfci.cccb.mev.web.rest.assembly.json.CalendarJsonSerializer;
import edu.dfci.cccb.mev.web.support.JsonViewResolver;
import org.springframework.context.annotation.Primary;

/**
 * @author levk
 * 
 */
@Configuration
@Log4j
@ToString
public class RestResolverConfiguration {

  @Bean
  public JsonViewResolver jsonViewResolver () {
    return new JsonViewResolver ();
  }

  @Bean
  public CalendarJsonSerializer calendarJsonSerializer () {
    return new CalendarJsonSerializer ();
  }

  @Primary
  @Bean(name = "RestJsonObjectMapper")
  public ObjectMapper jsonObjectMapper (AutowireCapableBeanFactory beanFactory, ApplicationContext context) {
    List<JsonSerializer<?>> serializers = new ArrayList<> ();
    ObjectMapper mapper = new ObjectMapper ();
    for (JsonSerializer<?> serializer : context.getBeansOfType (JsonSerializer.class).values ())
      serializers.add (serializer);
    for (JacksonConfigurer configurer : context.getBeansOfType (JacksonConfigurer.class).values ()) {
      configurer.configureObjectMapper (mapper);
      List<JsonSerializer<?>> configurerSerializers = new ArrayList<> ();
      configurer.addJsonSerializers (configurerSerializers);
      for (JsonSerializer<?> serializer : configurerSerializers)
        beanFactory.autowireBean (serializer);
      serializers.addAll (configurerSerializers);
    }
    log.info ("Registering custom JSON serializers: " + serializers);
    mapper.setSerializerFactory (instance.withAdditionalSerializers (new SimpleSerializers (serializers)));
    return mapper;
  }
}
