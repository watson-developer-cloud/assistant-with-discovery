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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.ibm.watson.developer_cloud.util.CredentialUtils;
import com.ibm.watson.developer_cloud.util.CredentialUtils.ServiceCredentials;


public class Logging {
  CloudantClient client = null;
  private static String USERNAME;
  private static String PASSWORD;
  private static URL URL;
  
  private static final Logger logger = LogManager.getLogger(Logging.class.getName());

  public Logging() throws MalformedURLException {
      ServiceCredentials creds = CredentialUtils.getUserNameAndPassword("cloudantNoSQLDB"); //$NON-NLS-1$
      if (creds == null) {
        throw new IllegalArgumentException(Messages.getString("Logging.NO_SERVICE_CREDENTIALS")); //$NON-NLS-1$
      }
      USERNAME = creds.getUsername();
      PASSWORD = creds.getPassword();
      URL = new URL(CredentialUtils.getAPIUrl("cloudantNoSQLDB"));
      if(StringUtils.isBlank(URL.toString())){
        throw new IllegalArgumentException(Messages.getString("Logging.NO_SERVICE_URL")); //$NON-NLS-1$
      }
      
      logger.info(Messages.getString("Logging.INITIALIZE_CLOUDANT_CLIENT")); //$NON-NLS-1$
      client = ClientBuilder.url(URL).username(USERNAME)
          .password(PASSWORD).build();
  }

  /**
   * This methods connects to the database and saves the given document to the Cloudant DB
   * 
   * @param question
   * @param intent
   * @param confidence
   * @param entity
   * @param convoOutput
   * @param convoId
   * @param retrieveAndRankOutput
   * @throws Exception
   */
  public void log(String question, String intent, String confidence, String entity, String convoOutput, String convoId,
      String retrieveAndRankOutput) throws Exception {

    logger.info(Messages.getString("Logging.ENTRY_INTO_DB")); //$NON-NLS-1$
    Database db = client.database("conversation_enhanced_db", true);
    db.save(new Document(question, intent, confidence, entity, convoOutput, convoId, retrieveAndRankOutput));
  }

  /**
   * 
   * The POJO for the document to be saved to the Cloudant DB
   * 
   */
  private class Document {
    private String Question;
    private String Intent;
    private String Confidence;
    private String Entity;
    private String ConvoOutput;
    private String ConvoId;
    private String RetrieveAndRankOutput;
    private String Time;

    public Document(String Question, String Intent, String Confidence, String Entity, String ConvoOutput,
        String ConvoId, String RetrieveAndRankOutput) {
      this.Question = Question;
      this.Intent = Intent;
      this.Confidence = Confidence;
      this.Entity = Entity;
      this.ConvoOutput = ConvoOutput;
      this.ConvoId = ConvoId;
      this.RetrieveAndRankOutput = RetrieveAndRankOutput;
      this.Time = new Date().toString();
    }

    @Override public String toString() {
      return "Document [Question=" + Question + ", Intent=" + Intent + ", Confidence=" + Confidence + ", Entity="
          + Entity + ", ConvoOutput=" + ConvoOutput + ", ConvoId=" + ConvoId + ", RetrieveAndRankOutput="
          + RetrieveAndRankOutput + ", Time=" + Time + "]";
    }


  }

}
