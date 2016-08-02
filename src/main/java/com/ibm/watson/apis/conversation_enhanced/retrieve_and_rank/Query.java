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
package com.ibm.watson.apis.conversation_enhanced.retrieve_and_rank;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.ibm.watson.apis.conversation_enhanced.utils.Constants;
import com.ibm.watson.apis.conversation_enhanced.utils.HttpSolrClientUtils;
import com.ibm.watson.apis.conversation_enhanced.utils.Messages;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.util.CredentialUtils;
import com.ibm.watson.developer_cloud.util.CredentialUtils.ServiceCredentials;

public class Query {

  public static String COLLECTION_NAME;
  private static String USERNAME;
  private static String PASSWORD;
  private static String RANKER_ID;
  private static String CLUSTER_ID;

  public static HttpSolrClient solrClient;
  private static final Logger logger = LogManager.getLogger(Query.class.getName());

  public Query(HttpSolrClient solrClient) {
    Query.solrClient = solrClient;
    logger.info(Messages.getString("Query.INITIALIZE_SOLR_CLIENT")); //$NON-NLS-1$
  }

  public Query() {
    ServiceCredentials creds = CredentialUtils.getUserNameAndPassword("retrieve_and_rank"); //$NON-NLS-1$
    if (creds == null) {
      throw new IllegalArgumentException(Messages.getString("Query.NO_SERVICE_CREDENTIALS")); //$NON-NLS-1$
    }
    USERNAME = creds.getUsername();
    PASSWORD = creds.getPassword();
    
    RetrieveAndRank retrieveAndRankService = new RetrieveAndRank();
    retrieveAndRankService.setUsernameAndPassword(USERNAME, PASSWORD);
    
    if(StringUtils.isNotBlank(System.getenv("CLUSTER_ID"))){ //$NON-NLS-1$
      CLUSTER_ID = System.getenv("CLUSTER_ID"); //$NON-NLS-1$
    } else {
      try{
        CLUSTER_ID = retrieveAndRankService.getSolrClusters().execute().getSolrClusters().get(0).getId();
      } catch(Exception e){
        throw new IllegalArgumentException(Messages.getString("Query.MISSING_CLUSTER_ID")); //$NON-NLS-1$
      }
    }
    if(StringUtils.isNotBlank(System.getenv("RANKER_ID"))){ //$NON-NLS-1$
      RANKER_ID = System.getenv("RANKER_ID"); //$NON-NLS-1$
    } else {
      try{
        RANKER_ID = retrieveAndRankService.getRankers().execute().getRankers().get(0).getId();
      } catch(Exception e) {
        throw new IllegalArgumentException(Messages.getString("Query.MISSING_RANKER_ID")); //$NON-NLS-1$
      }
    }
    if(StringUtils.isNotBlank(System.getenv("COLLECTION_NAME"))){ //$NON-NLS-1$
      COLLECTION_NAME = System.getenv("COLLECTION_NAME"); //$NON-NLS-1$
    } else {
      COLLECTION_NAME = Constants.COLLECTION_NAME;
    }
  }

  /**
   * Use the Watson Developer Cloud SDK to send the user's query to the retrive and rank service
   * 
   * @param userQuery The user's query to be sent to the retrieve and rank service
   * @return The unaltered SOLR query responses obtained from the retrieve and rank service
   * @throws SolrServerException
   * @throws IOException
   */
  public QueryResponse query(String userQuery) throws Exception {
    
    // Configure the Watson Developer Cloud SDK to make a call to the appropriate retrieve and rank
    // service. Specific information is obtained from environment variable and the services
    // associated with the app. See the Query constructor for details.
    RetrieveAndRank service = new RetrieveAndRank();
    HttpSolrClient solrClient = HttpSolrClientUtils.getSolrClient(service.getSolrUrl(CLUSTER_ID), USERNAME, PASSWORD);
    
    logger.info(Messages.getString("Query.PASS_CLUSTER_DETAILS")); //$NON-NLS-1$
    
    // Setup the query parameters
    final SolrQuery query = new SolrQuery(userQuery)
        // The fields we want in the response object
        .setFields(Constants.SCHEMA_FIELD_ID, Constants.SCHEMA_FIELD_BODY,
            Constants.SCHEMA_FIELD_TITLE, Constants.SCHEMA_FIELD_CONFIDENCE, Constants.SCHEMA_FIELD_SOURCE_URL)
        // The size of the SOLR snippet that we show as our initial answers
        .setHighlight(true).setHighlightFragsize(150).setHighlightSnippets(1)
        // The field to perform highlighting on
        .setParam("hl.fl", Constants.SCHEMA_FIELD_BODY)
        // The number of answers to return
        .setRows(Constants.RESULTS_TO_FETCH) // $NON-NLS-1$
        // The retrieve and rank endpoint to hit
        .setRequestHandler("/fcselect")
        // The ranker to rank the potential answers
        .setParam("ranker_id", RANKER_ID); //$NON-NLS-1$ //$NON-NLS-2$

    // Send the query to the retrieve and rank service to obtain answers to the user's query
    logger.info(Messages.getString("Query.QUERY_SOLR_RANKER")); //$NON-NLS-1$
    return solrClient.query(COLLECTION_NAME, query);
  }

}
