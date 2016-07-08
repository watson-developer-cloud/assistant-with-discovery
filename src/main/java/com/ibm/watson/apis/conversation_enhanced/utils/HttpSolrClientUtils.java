/**
 * (C) Copyright IBM Corp. 2016. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.watson.apis.conversation_enhanced.utils;

import java.net.URI;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;

/**
 * Utility class to create a {@link HttpSolrClient}
 */
public class HttpSolrClientUtils {
  private static final Logger logger = LogManager.getLogger(HttpSolrClientUtils.class.getName());
  
  /**
   * Helper method to call {@link createHttpClient}.
   * 
   * @param uri the Solr server url
   * @param username the {@link RetrieveAndRank} service username
   * @param password the {@link RetrieveAndRank} service password
   * @return the {@link HttpClient}
   */
  public static HttpSolrClient getSolrClient(String uri, String username, String password) {
    return new HttpSolrClient(uri, createHttpClient(uri, username, password));
  }

  private static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
    public void process(final HttpRequest request, final HttpContext context) throws HttpException {
      final AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);

      if (authState.getAuthScheme() == null) {
        final CredentialsProvider credsProvider =
            (CredentialsProvider) context.getAttribute(HttpClientContext.CREDS_PROVIDER);
        final HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
        final Credentials creds =
            credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
        if (creds == null) {
          logger.error(Messages.getString("HttpSolrClientUtils.NO_CREDS_PROVIDED")); //$NON-NLS-1$
          throw new HttpException(Messages.getString("HttpSolrClientUtils.NO_CREDS_PROVIDED")); //$NON-NLS-1$
        }
        authState.update(new BasicScheme(), creds);
      }
    }
  }

  /**
   * Creates the {@link HttpClient} to use with the Solrj
   *
   * @param url the Solr server url
   * @param username the {@link RetrieveAndRank} service username
   * @param password the {@link RetrieveAndRank} service password
   * @return the {@link HttpClient}
   */
  private static HttpClient createHttpClient(String url, String username, String password) {
    final URI scopeUri = URI.create(url);
    logger.info(Messages.getString("HttpSolrClientUtils.CREATING_HTTP_CLIENT")); //$NON-NLS-1$
    final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(new AuthScope(scopeUri.getHost(), scopeUri.getPort()),
        new UsernamePasswordCredentials(username, password));

    final HttpClientBuilder builder = HttpClientBuilder.create().setMaxConnTotal(128).setMaxConnPerRoute(32)
        .setDefaultRequestConfig(RequestConfig.copy(RequestConfig.DEFAULT).setRedirectsEnabled(true).build())
        .setDefaultCredentialsProvider(credentialsProvider).addInterceptorFirst(new PreemptiveAuthInterceptor());
    return builder.build();
  }
}
