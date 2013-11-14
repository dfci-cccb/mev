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
package edu.dfci.cccb.mev.web.domain.reflection.concrete;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import edu.dfci.cccb.mev.web.domain.reflection.Reflection;
import edu.dfci.cccb.mev.web.domain.reflection.RestParameter;
import edu.dfci.cccb.mev.web.domain.reflection.RestService;

/**
 * @author levk
 * 
 */
public class SpringReflector implements Reflection {

  private @Inject RequestMappingHandlerMapping mappings;
  private Collection<RestService> services;

  public Collection<RestService> services () {
    return services;
  }

  @SuppressWarnings ("unchecked")
  @PostConstruct
  public void populateRestServices () {
    Collection<RestService> services = new ArrayList<> ();

    for (Entry<RequestMappingInfo, HandlerMethod> mapping : mappings.getHandlerMethods ().entrySet ()) {
      final RequestMappingInfo info = mapping.getKey ();
      final HandlerMethod handler = mapping.getValue ();
      if (handler.getMethodAnnotation (ResponseBody.class) != null
          || handler.getBeanType ().getAnnotation (RestController.class) != null) {
        Collection<RequestMethod> methods = info.getMethodsCondition ().getMethods ();
        if (methods.isEmpty ())
          methods = asList (RequestMethod.values ());
        for (final RequestMethod method : methods)
          for (final String url : info.getPatternsCondition ().getPatterns ())
            services.add (new RestService () {

              private Collection<RestParameter> parameters;
              private Collection<Class<? extends Throwable>> throwing;

              {
                Collection<RestParameter> parameters = new ArrayList<> ();
                for (final MethodParameter parameter : handler.getMethodParameters ())
                  if (parameter.hasParameterAnnotation (RequestParam.class))
                    parameters.add (new RestParameter () {

                      @Override
                      public Class<?> type () {
                        return parameter.getParameterType ();
                      }

                      @Override
                      public String name () {
                        return parameter.getParameterAnnotation (RequestParam.class)
                                        .value ();
                      }
                    });
                this.parameters = unmodifiableCollection (parameters);

                Collection<Class<? extends Throwable>> throwing = new ArrayList<> ();
                for (Class<?> exceptionType : handler.getMethod ().getExceptionTypes ())
                  throwing.add ((Class<? extends Throwable>) exceptionType);
                this.throwing = unmodifiableCollection (throwing);
              }

              @Override
              public String url () {
                return url;
              }

              @Override
              public String method () {
                return method.name ();
              }

              @Override
              public Method handler () {
                return handler.getMethod ();
              }

              @Override
              public Collection<RestParameter> parameters () {
                return parameters;
              }

              @Override
              public Collection<Class<? extends Throwable>> throwing () {
                return throwing;
              }
            });
      }

      this.services = unmodifiableCollection (services);
    }
  }
}
