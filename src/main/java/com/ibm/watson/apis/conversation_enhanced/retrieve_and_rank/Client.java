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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.apis.conversation_enhanced.payload.DocumentPayload;
import com.ibm.watson.apis.conversation_enhanced.utils.Constants;
import com.ibm.watson.apis.conversation_enhanced.utils.Messages;

public class Client {
  private static final Logger logger = LogManager.getLogger(Client.class.getName());

  /**
   * This method uses the Query object to send the user's query (the
   * <code>input</code> param) to the retrieve and rank service
   * 
   * @param input The user's query to be sent to the retrieve and rank service
   * @return A list of DocumentPayload objects, each representing a single document the retrieve and
   *         rank service believes is a possible answer to the user's query
   * @throws SolrServerException
   * @throws IOException
   */
  public List<DocumentPayload> getDocuments(String input) throws Exception {
    List<DocumentPayload> documents = new ArrayList<DocumentPayload>();
    QueryResponse output = new Query().query(input);
    documents = createPayload(input, new Gson().toJson(output.getResults()), new Gson().toJson(output.getHighlighting()));
    return documents;
  }

  /**
   * Helper Method to include highlighting information along with the retrieve and rank response so
   * the final payload includes id,title,highlight,body,sourceUrl as json key value pairs.
   * 
   * @param input The user's query sent to the retrieve and rank service
   * @param results The results obtained from a call to the retrieve and rank service with
   *        <code>input</code> as the query
   * @param highlights SOLR highlighting information obtained from a call to the retrieve and rank
   *        service
   * @return A list of DocumentPayload objects, each representing a single document the retrieve and
   *         rank service believes is a possible answer to the user's query
   */
  private List<DocumentPayload> createPayload(String input, String results, String highlights) {
    logger.info(Messages.getString("Service.CREATING_RNR_PAYLOAD")); //$NON-NLS-1$
    List<DocumentPayload> payload = new ArrayList<DocumentPayload>();
    HashMap<String, Integer> hm = new HashMap<String, Integer>();
    JsonElement jelement = new JsonParser().parse(results);
    JsonArray jarray = jelement.getAsJsonArray();
    for (int i = 0; i < jarray.size(); i++) {
      DocumentPayload documentPayload = new DocumentPayload();
      String id = jarray.get(i).getAsJsonObject().get(Constants.SCHEMA_FIELD_ID).toString().replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
      documentPayload.setId(id);
      documentPayload.setTitle(
          jarray.get(i).getAsJsonObject().get(Constants.SCHEMA_FIELD_TITLE).toString().replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
      if (jarray.get(i).getAsJsonObject().get(Constants.SCHEMA_FIELD_BODY) !=null) {
        documentPayload.setBody(
            // This method limits the response text in this sample app to two paragraphs.
            limitParagraph(jarray.get(i).getAsJsonObject().get(Constants.SCHEMA_FIELD_BODY).toString()
                .replaceAll("\"", ""))); //$NON-NLS-1$ //$NON-NLS-2$
      } else {
        documentPayload.setBody("empty"); //$NON-NLS-1$
      }
      if (jarray.get(i).getAsJsonObject().get(Constants.SCHEMA_FIELD_SOURCE_URL) == null) {
        documentPayload.setSourceUrl("empty"); //$NON-NLS-1$
      } else {
        documentPayload.setSourceUrl(
            jarray.get(i).getAsJsonObject().get(Constants.SCHEMA_FIELD_SOURCE_URL).toString().replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
      }
      if (jarray.get(i).getAsJsonObject().get(Constants.SCHEMA_FIELD_CONFIDENCE) != null) {
        documentPayload.setConfidence(
            jarray.get(i).getAsJsonObject().get(Constants.SCHEMA_FIELD_CONFIDENCE).toString().replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
      } else {
        documentPayload.setConfidence("0.0"); //$NON-NLS-1$
      }
      payload.add(i, documentPayload);
      hm.put(id, i);
    }
    jelement = new JsonParser().parse(highlights);
    JsonObject highlight = jelement.getAsJsonObject();
    // Add highlighting information
    Set<Map.Entry<String, JsonElement>> entrySet = highlight.entrySet();
    for (Map.Entry<String, JsonElement> entry : entrySet) {
      String docid = entry.getKey();
      String highlighted = ""; //$NON-NLS-1$
      if (entry.getValue().getAsJsonObject().get(Constants.SCHEMA_FIELD_BODY) != null) {
        highlighted = entry.getValue().getAsJsonObject().get(Constants.SCHEMA_FIELD_BODY).getAsJsonArray()
            .get(0).toString();
      }
      if (hm.get(docid) != null)
        payload.get(hm.get(docid)).setHighlight(highlighted);
    }
    return payload;
  }

  /**
   * This method limits the response text in this sample app to two paragraphs.
   * For your own application, you can comment out the method to allow the full text to be returned.
   * @param replaceAll
   * @return string
   */
  private static String limitParagraph(String body) {
    String returnString = body;
    final Pattern pattern = Pattern.compile("((.+?)<br><br>){1,2}"); //$NON-NLS-1$
    final Matcher matcher = pattern.matcher(body);
    try {
    matcher.find();
    returnString = matcher.group(0);
    } catch (IllegalStateException e) {
      //We know the match is not found then should return the original body
      if (e.getMessage().equalsIgnoreCase("No match found")){
        return returnString;
      }
    }
    return returnString;
  }

}
