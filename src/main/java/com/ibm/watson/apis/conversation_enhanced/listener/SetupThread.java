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
package com.ibm.watson.apis.conversation_enhanced.listener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.apis.conversation_enhanced.utils.Constants;
import com.ibm.watson.apis.conversation_enhanced.utils.HttpSolrClientUtils;
import com.ibm.watson.apis.conversation_enhanced.utils.Messages;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.Ranker;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster.Status;
import com.ibm.watson.developer_cloud.service.exception.UnauthorizedException;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusterOptions;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusters;
import com.ibm.watson.developer_cloud.util.CredentialUtils;
import com.ibm.watson.developer_cloud.util.CredentialUtils.ServiceCredentials;

/**
 * This class is forked when the application is accessed for the first time. It detects if the
 * retrieve and rank service associated with the app (either bound on bluemix or specified in
 * server.env) has established artifacts such as the SOLR cluster and ranker. If they are not there
 * then the thread will go ahead and create a cluster, upload a configuration, create a collection,
 * index documents, and create and train a ranker.
 */
public class SetupThread extends Thread {
  private static final Logger logger = LogManager.getLogger(SetupThread.class.getName());
  public static String RANKER_ID = null;
  private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();
  public JsonObject config = new JsonObject();

  public void run() {
    String username = ""; //$NON-NLS-1$
    String password = ""; //$NON-NLS-1$

    try {
      logger.info(Messages.getString("SetupThread.GET_CREDENTIALS")); //$NON-NLS-1$
      ServiceCredentials creds = CredentialUtils.getUserNameAndPassword("retrieve_and_rank");
      
      updateConfigObject("0",Constants.NOT_READY, Messages.getString("SetupThread.STEP_1_OF_2"), Messages.getString("SetupThread.GETTING_CREDENTIALS")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      
      if (creds == null) {
        throw new IllegalArgumentException(Messages.getString("SetupThread.NO_RNR_CREDENTIALS")); //$NON-NLS-1$
      }
      username = creds.getUsername();
      password = creds.getPassword();

      logger.info(Messages.getString("SetupThread.CREATE_RNR_SERVICE")); //$NON-NLS-1$
      RetrieveAndRank service = new RetrieveAndRank();
      service.setUsernameAndPassword(username, password);

      if (isAlreadySetup(service)) {
        logger.info(Messages.getString("SetupThread.CLUSTER_ALREADY_SETUP")); //$NON-NLS-1$
        updateConfigObject("1",Constants.NOT_READY, Messages.getString("SetupThread.STEP_1_OF_2"), Messages.getString("SetupThread.CLUSTER_ALREADY_SETUP")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        RANKER_ID = System.getenv("RANKER_ID"); //$NON-NLS-1$
        if (RANKER_ID == null) {
          // If the user has not specifically set a ranker in the ENV, detect a ranker. We
          // return the first discovered ranker, which in our deploy story is the one we created
          if (service.getRankers().execute().getRankers().size() > 0) {
            RANKER_ID = service.getRankers().execute().getRankers().get(0).getId();
          }
          
          // If no ranker is detected at this point, create one
          if (RANKER_ID == null || RANKER_ID.isEmpty()) {
            logger.info(Messages.getString("SetupThread.CLUSTER_NO_RANKER")); //$NON-NLS-1$

            logger.info(Messages.getString("SetupThread.CREATE_RANKER")); //$NON-NLS-1$
            updateConfigObject("2",Constants.NOT_READY, Messages.getString("SetupThread.STEP_2_OF_2"), Messages.getString("SetupThread.CREATING_RANKER")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            createRanker(service);
            updateConfigObject("3",Constants.READY, Messages.getString("SetupThread.EMPTY"), Messages.getString("SetupThread.EMPTY")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            return;
          }
        }
        
        logger.info(Messages.getString("SetupThread.SETUP_ALREADY_COMPLETE")); //$NON-NLS-1$
        updateConfigObject("3",Constants.READY, Messages.getString("SetupThread.EMPTY"), Messages.getString("SetupThread.EMPTY")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        return;
      }

      // If we get here this is a new retrieve and ranker service with no artifacts, start by creating the SOLR cluster
      logger.info(Messages.getString("SetupThread.CREATE_CLUSTER")); //$NON-NLS-1$
      updateConfigObject("1",Constants.NOT_READY, Messages.getString("SetupThread.STEP_1_OF_2"), Messages.getString("SetupThread.CREATING_CLUSTER")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      SolrCluster cluster = createCluster(service);
      
      logger.info(Messages.getString("SetupThread.UPLOADING_CONFIGURATION")); //$NON-NLS-1$
      uploadConfiguration(service, cluster);
      
      logger.info(Messages.getString("SetupThread.CREATE_COLLECTION")); //$NON-NLS-1$
      HttpSolrClient solrClient = HttpSolrClientUtils.getSolrClient(service.getSolrUrl(cluster.getId()), username, password);
      createCollection(solrClient);
      
      logger.info(Messages.getString("SetupThread.INDEX_DOCUMENTS")); //$NON-NLS-1$
      indexDocuments(solrClient);

      logger.info(Messages.getString("SetupThread.CREATE_RANKER")); //$NON-NLS-1$
      updateConfigObject("2",Constants.NOT_READY, Messages.getString("SetupThread.STEP_2_OF_2"), Messages.getString("SetupThread.CREATING_RANKER")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      createRanker(service);
      
      logger.info(Messages.getString("SetupThread.SETUP_COMPLETE")); //$NON-NLS-1$
      updateConfigObject("3",Constants.READY, Messages.getString("SetupThread.EMPTY"), Messages.getString("SetupThread.EMPTY")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      
    } catch (Exception e) {
      logger.error(Messages.getString("SetupThread.ERROR_COLLECTION_INIT") + e.getMessage()); //$NON-NLS-1$
      if(e instanceof UnauthorizedException){
        updateConfigObject("0", Constants.NOT_READY, Messages.getString("SetupThread.ERROR"), //$NON-NLS-1$ //$NON-NLS-2$
            Messages.getString("SetupThread.INVALID_CREDS")); //$NON-NLS-1$ //$NON-NLS-4$
      } else {
        updateConfigObject("0", Constants.NOT_READY, Messages.getString("SetupThread.ERROR"), //$NON-NLS-1$ //$NON-NLS-2$
            Messages.getString("SetupThread.CHECK_LOGS")); //$NON-NLS-1$ //$NON-NLS-4$
      }
    }
  }

  /**
   * Makes a call to get the number of clusters, if it is > 0 then we assume the setup has already
   * been done and we skip it
   * 
   * @param service a RetrieveAndRank object from the Watson Developer Cloud SDK that has been
   *        instantiated and provided credentials
   * @return true if a cluster exists in the retrieve and rank service, falls otherwise
   */
  private boolean isAlreadySetup(RetrieveAndRank service) {
    SolrClusters clusters = service.getSolrClusters().execute();
    return clusters.getSolrClusters().size() > 0 ? true : false;
  }

  /**
   * Reads documents from the file4.json file, uploads them to the previously created collection and
   * commits them.
   * 
   * @param solrClient
   */
  private void indexDocuments(HttpSolrClient solrClient) {
    URL url = this.getClass().getClassLoader().getResource("file4.json"); //$NON-NLS-1$
    File dataFile = null;
    try {
      dataFile = new File(url.toURI());
    } catch (Exception e) {
      logger.error(e.getMessage());;
    }

    JsonArray a = null;
    try {
      a = (JsonArray) new JsonParser().parse(new FileReader(dataFile)).getAsJsonArray();
    } catch (Exception e) {
      logger.error(Messages.getString("SetupThread.INGESTION_ERROR_PARSING") + e.getMessage()); //$NON-NLS-1$
    }
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    for (int i = 0, size = a.size(); i < size; i++) {
      SolrInputDocument document = new SolrInputDocument();
      JsonObject car = a.get(i).getAsJsonObject();

      int id = car.get(Constants.SCHEMA_FIELD_ID).getAsInt();
      String title = (String) car.get(Constants.SCHEMA_FIELD_TITLE).getAsString();
      String body = (String) car.get(Constants.SCHEMA_FIELD_BODY).getAsString();
      String sourceUrl = (String) car.get(Constants.SCHEMA_FIELD_SOURCE_URL).getAsString();
      String contentHtml = (String) car.get(Constants.SCHEMA_FIELD_CONTENT_HTML).getAsString();
      document.addField(Constants.SCHEMA_FIELD_ID, id);
      document.addField(Constants.SCHEMA_FIELD_TITLE, title);
      document.addField(Constants.SCHEMA_FIELD_BODY, body);
      document.addField(Constants.SCHEMA_FIELD_SOURCE_URL, sourceUrl);
      document.addField(Constants.SCHEMA_FIELD_CONTENT_HTML, contentHtml);
      docs.add(document);
    }
    logger.info(Messages.getString("SetupThread.INDEXING_DOCUMENT")); //$NON-NLS-1$

    UpdateResponse addResponse;
    try {
      addResponse = solrClient.add(Constants.COLLECTION_NAME, docs);

      logger.info(addResponse);

      // Commit the document to the index so that it will be available for searching.
      solrClient.commit(Constants.COLLECTION_NAME);
      logger.info(Messages.getString("SetupThread.INDEX_DOC_COMMITTED")); //$NON-NLS-1$
    } catch (SolrServerException e) {
      logger.error(Messages.getString("SetupThread.SOLR_INDEXING_ERROR") + e.getMessage()); //$NON-NLS-1$
    } catch (IOException e) {
      logger.error(Messages.getString("SetupThread.SOLR_IO_ERROR") + e.getMessage()); //$NON-NLS-1$
    }
  }

  /**
   * Creates a SOLR collection in which documents can later be ingested
   * 
   * @param solrClient
   */
  private static void createCollection(HttpSolrClient solrClient) {
    final CollectionAdminRequest.Create createCollectionRequest = new CollectionAdminRequest.Create();
    createCollectionRequest.setCollectionName(Constants.COLLECTION_NAME);
    createCollectionRequest.setConfigName(Constants.CONFIGURATION_NAME);

    logger.info(Messages.getString("SetupThread.CREATING_COLLECTION")); //$NON-NLS-1$
    CollectionAdminResponse response = null;
    try {
      response = createCollectionRequest.process(solrClient);
    } catch (SolrServerException e) {
      logger.error(e.getMessage());
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    if (!response.isSuccess()) {
      logger.error(Messages.getString("SetupThread.CREATING_COLLECTION_FAILED") + response.getErrorMessages().toString()); //$NON-NLS-1$
    }
    logger.info(Messages.getString("SetupThread.COLLECTION_CREATED")); //$NON-NLS-1$
  }


  /**
   * Uploads the SOLR configuration specified in the src/main/resource/solr_config.zip
   * 
   * @param service
   * @param cluster
   */
  private void uploadConfiguration(RetrieveAndRank service, SolrCluster cluster) {
    URL url = this.getClass().getClassLoader().getResource("solr_config.zip"); //$NON-NLS-1$
    File configZip = null;
    try {
      configZip = new File(url.toURI());
    } catch (Exception e) {
      logger.error(Messages.getString("SetupThread.ERROR_UPLOAD_CONFIG") + e.getMessage()); //$NON-NLS-1$
    }

    service.uploadSolrClusterConfigurationZip(cluster.getId(), Constants.CONFIGURATION_NAME, configZip).execute();
    logger.info(Messages.getString("SetupThread.UPLOADING_CONFIG")); //$NON-NLS-1$
  }

  /**
   * Creates a SOLR cluster in the retrieve and rank instance associated with the
   * <code>service</code> parameter
   * 
   * @param service Watson Developer Cloud SDK's RetrieveAndRank object instantiated with
   *        appropriate credentials
   * @return the Watson Developer Cloud SDK's object representing the newly created SOLR cluster
   */
  private SolrCluster createCluster(RetrieveAndRank service) {
    // Determine the cluster size to create
    String cluster_size = System.getenv("CLUSTER_SIZE"); //$NON-NLS-1$
    
    // create the Solr Cluster. null indicates the free cluster, otherwise a size of 1 to 14 may be used
    SolrClusterOptions options = new SolrClusterOptions(Constants.CLUSTER_NAME, 
        cluster_size == null ? null : Integer.valueOf(cluster_size));
    SolrCluster cluster = (SolrCluster) service.createSolrCluster(options).execute();
    logger.info(Messages.getString("SetupThread.SOLR_CLUSTER") + cluster); //$NON-NLS-1$

    // wait until the Solr Cluster is available
    while (cluster.getStatus() == Status.NOT_AVAILABLE) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      } // sleep 10 seconds
      cluster = (SolrCluster) service.getSolrCluster(cluster.getId()).execute();
      logger.info(Messages.getString("SetupThread.SOLR_CLUSTER_STATUS") + cluster.getStatus()); //$NON-NLS-1$
    }
    
    // sleep even after cluster reports ready, service may need additional time to process
    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }

    // list SOLR Clusters
    logger.info(Messages.getString("SetupThread.SOLR_CLUSTERS") + service.getSolrClusters().execute()); //$NON-NLS-1$

    return cluster;
  }

  /**
   * This method creates a ranker with the existing training data and waits until the 'Training'
   * phase is complete and the Ranker status is 'Available'.
   * 
   * @param service
   */
  private void createRanker(RetrieveAndRank service) {
    URL url = this.getClass().getClassLoader().getResource("trainingdata.csv"); //$NON-NLS-1$
    File trainingFile = null;
    try {
      trainingFile = new File(url.toURI());
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    Ranker ranker = service.createRanker(Constants.RANKER_NAME, trainingFile).execute();
    String rankerId = ranker.getId();
    logger.info(Messages.getString("SetupThread.CREATING_A_RANKER") + rankerId); //$NON-NLS-1$
    ranker = service.getRankerStatus(rankerId).execute();
    logger.info(ranker.getStatus().toString());
    while (ranker.getStatus().toString().equalsIgnoreCase(Messages.getString("SetupThread.TRAINING_RANKER"))) { //$NON-NLS-1$
      try {
        Thread.sleep(60000);
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      } // sleep 60 seconds
      ranker = service.getRankerStatus(rankerId).execute();
      logger.info(Messages.getString("SetupThread.RANKER_STATUS") + ranker.getStatus()); //$NON-NLS-1$
    }
  }

  /**
   * Method to update the listeners about any property changes. This is used by the UI to inform
   * user what the status of the setup is since it can take a long time.
   * 
   * @param object
   * @param config
   */
  private void notifyListeners(Object object, JsonObject config) {
    for (PropertyChangeListener configUpdate : listener) {
      configUpdate.propertyChange(new PropertyChangeEvent(this, "configSetup", config, config)); //$NON-NLS-1$
    }
  }

  /**
   * Method to add a listener
   * 
   * @param newListener PropertyChangeListener
   */
  public void addChangeListener(PropertyChangeListener newListener) {
    listener.add(newListener);
  }

  /**
   * This method is used to update the JSON Config Object that will be sent back to the UI.
   * 
   * @param config
   * @param setup_step The step at which the setup is
   * @param setup_state The state of the setup(accepts ready/not_ready)
   * @param setup_phase The current phase of the setup(this can be that the services is being setup
   *        or if any other phase of the application is being setup)
   * @param setup_message The message that you want to be shown in the UI.
   */
  private void updateConfigObject(String setup_step,String setup_state, String setup_phase, String setup_message) {
    config.addProperty(Constants.SETUP_STEP, setup_step); //$NON-NLS-1$
    config.addProperty(Constants.SETUP_STATE, setup_state); //$NON-NLS-1$
    config.addProperty(Constants.SETUP_PHASE, setup_phase); //$NON-NLS-1$
    config.addProperty(Constants.SETUP_MESSAGE, setup_message); //$NON-NLS-1$
    notifyListeners(this, config);
  }
}
