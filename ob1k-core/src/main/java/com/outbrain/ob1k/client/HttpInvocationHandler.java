package com.outbrain.ob1k.client;

import com.outbrain.ob1k.client.endpoints.AbstractClientEndpoint;
import com.outbrain.ob1k.client.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

/**
* Created by aronen on 4/24/14.
*/
class HttpInvocationHandler implements InvocationHandler {
  private static final Logger logger = LoggerFactory.getLogger(HttpInvocationHandler.class);

  private final HttpClient client;
  private final Map<Method, AbstractClientEndpoint> endpoints;
  private final List<String> targets;

  HttpInvocationHandler(final List<String> targets, final HttpClient client, final Map<Method, AbstractClientEndpoint> endpoints) {
    this.client = client;
    this.targets = targets;
    this.endpoints = endpoints;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if ("close".equals(method.getName()) && method.getParameterTypes().length == 0) {
      client.close();
      logger.debug("client {} is closed.", chooseTarget());
      return null;
    }

    final String target = chooseTarget();
    final AbstractClientEndpoint endpoint = endpoints.get(method);
    return endpoint.invoke(target, args);
  }

  private String chooseTarget() {
    return targets.get(0);
  }
}
