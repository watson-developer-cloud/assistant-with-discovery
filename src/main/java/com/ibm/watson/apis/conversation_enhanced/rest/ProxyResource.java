/**
 * Copyright IBM Corp. 2016
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

package com.ibm.watson.apis.conversation_enhanced.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.ibm.watson.apis.conversation_enhanced.payload.DocumentPayload;
import com.ibm.watson.apis.conversation_enhanced.retrieve_and_rank.Client;
import com.ibm.watson.apis.conversation_enhanced.utils.Logging;
import com.ibm.watson.apis.conversation_enhanced.utils.Messages;
import com.ibm.watson.developer_cloud.conversation.v1_experimental.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1_experimental.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1_experimental.model.MessageResponse;
import com.ibm.watson.developer_cloud.service.exception.UnauthorizedException;
import com.ibm.watson.developer_cloud.util.GsonSingleton;

@Path("conversation/api/v1/workspaces")
public class ProxyResource {
  private static final Logger logger = LogManager.getLogger(ProxyResource.class.getName());

  private static String API_VERSION;
  private static String PASSWORD;
  private static String URL;
  private static String USERNAME;

  private static boolean LOGGING_ENABLED = Boolean.parseBoolean(System.getenv("LOGGING_ENABLED"));

  public static void setConversationAPIVersion(String version) {
    API_VERSION = version;
  }

  public static void setCredentials(String username, String password, String url) {
    USERNAME = username;
    PASSWORD = password;
    URL = url;
  }

  private MessageRequest buildMessageFromPayload(InputStream body) {
    StringBuilder sbuilder = null;
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(body, "UTF-8"));
      sbuilder = new StringBuilder();
      String str = reader.readLine();
      while (str != null) {
        sbuilder.append(str);
        str = reader.readLine();
        if (str != null) {
          sbuilder.append("\n");
        }
      }
      return GsonSingleton.getGson().fromJson(sbuilder.toString(), MessageRequest.class);
    } catch (IOException e) {
      logger.error(Messages.getString("ProxyResource.JSON_READ"), e);
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        logger.error(Messages.getString("ProxyResource.STREAM_CLOSE"), e);
      }
    }
    return null;
  }

  /**
   * This method is responsible for sending the query the user types into the UI to the Watson
   * services. The code demonstrates how the conversation service is called, how the response is
   * evaluated, and how the response is then sent to the retrieve and rank service if necessary.
   * 
   * @param request The full query the user asked of Watson
   * @param id The ID of the conversational workspace
   * @return The response from Watson. The response will always contain the conversation service's
   *         response. If the intent confidence is high or the intent is out_of_scope, the response
   *         will also contain information from the retrieve and rank service
   */
  private MessageResponse getWatsonResponse(MessageRequest request, String id) throws Exception {

    // Configure the Watson Developer Cloud SDK to make a call to the appropriate conversation
    // service. Specific information is obtained from the VCAP_SERVICES environment variable
    ConversationService service =
        new ConversationService(API_VERSION != null ? API_VERSION : ConversationService.VERSION_DATE_2016_05_19);
    if (USERNAME != null || PASSWORD != null) {
      service.setUsernameAndPassword(USERNAME, PASSWORD);
    }
    if (URL != null) {
      service.setEndPoint(URL);
    }

    // Use the previously configured service object to make a call to the conversational service
    MessageResponse response = service.message(id, request).execute();

    // Determine if conversation's response is sufficient to answer the user's question or if we
    // should call the retrieve and rank service to obtain better answers
    if (response.getContext().containsKey("call_retrieve_and_rank") &&
    		(boolean)(response.getContext().get("call_retrieve_and_rank")) == true) {
      String query = response.getInputText();

      // Extract the user's original query from the conversational response
      if (query != null && !query.isEmpty()) {
        Client retrieveAndRankClient = new Client();

        // For this app, both the original conversation response and the retrieve and rank response
        // are sent to the UI. Extract and add the conversational response to the ultimate response
        // we will send to the user. The UI will process this response and show the top 5 retrieve
        // and rank answers to the user in the main UI. The JSON response section of the UI will
        // show information from the calls to both services.
        Map<String, Object> output = response.getOutput();
        if (output == null) {
          output = new HashMap<String, Object>();
          response.setOutput(output);
        }

        // Send the user's question to the retrieve and rank service
        List<DocumentPayload> docs = retrieveAndRankClient.getDocuments(query);

        // Append the retrieve and rank answers to the output object that will be sent to the UI
        output.put("CEPayload", docs); //$NON-NLS-1$
      }
    }

    // Log User input and output from Watson
    if (Boolean.TRUE.equals(LOGGING_ENABLED)) {
      logResponse(response);
    }

    return response;
  }

  @POST @Path("{id}/message") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON) public Response postMessage(
      @PathParam("id") String id, InputStream body) {

    HashMap<String, Object> errorsOutput = new HashMap<String, Object>();
    MessageRequest request = buildMessageFromPayload(body);

    if (request == null) {
      throw new IllegalArgumentException(Messages.getString("ProxyResource.NO_REQUEST"));
    }

    MessageResponse response = null;

    try {
      response = getWatsonResponse(request, id);

    } catch (Exception e) {
      if (e instanceof UnauthorizedException) {
        errorsOutput.put("error", Messages.getString("ProxyResource.INVALID_CONVERSATION_CREDS")); //$NON-NLS-1$
      } else if (e instanceof IllegalArgumentException) {
        errorsOutput.put("error", e.getMessage());
      } else if (e instanceof MalformedURLException) {
        errorsOutput.put("error", Messages.getString("ProxyResource.MALFORMED_URL")); //$NON-NLS-1$
      } else if (e.getMessage().contains("URL workspaceid parameter is not a valid GUID.")) {
        errorsOutput.put("error", Messages.getString("ProxyResource.INVALID_WORKSPACEID")); //$NON-NLS-1$
      } else if (e.getMessage().contains("/fcselect.")) {
        errorsOutput.put("error", Messages.getString("ProxyResource.INVALID_COLLECTION_NAME")); //$NON-NLS-1$
      } else if (e.getMessage().contains("is not authorized for cluster") && e.getMessage().contains("and ranker")) {
        errorsOutput.put("error", Messages.getString("ProxyResource.INVALID_RANKER_ID")); //$NON-NLS-1$
      } else {
        errorsOutput.put("error", Messages.getString("ProxyResource.GENERIC_ERROR")); //$NON-NLS-1$
      }
      logger.error(Messages.getString("ProxyResource.SOLR_QUERY_EXCEPTION") + e.getMessage()); //$NON-NLS-1$
      return Response.ok(new Gson().toJson(errorsOutput, HashMap.class)).type(MediaType.APPLICATION_JSON).build();
    }
    return Response.ok(new Gson().toJson(response, MessageResponse.class)).type(MediaType.APPLICATION_JSON).build();
  }
  
  /**
   * 
   * This method takes in the response object and sends in to the cloudant logging class
   * 
   * @param response
   * @throws Exception 
   */
  private void logResponse(MessageResponse response) throws Exception {
    Logging cloudantLogging = new Logging();
    String intent = "<no intent>";
    String confidence = "<no confidence>";
    if (!response.getIntents().isEmpty() && response.getIntents().get(0) != null) {
      intent = response.getIntents().get(0).getIntent();
      confidence = response.getIntents().get(0).getConfidence().toString();
    }
    String entity = response.getEntities().size() > 0 ? "Entity: " + response.getEntities().get(0).getEntity()
        + " Value:" + response.getEntities().get(0).getValue() : "<no entity>";
    String convoOutput = (String) (response.getOutput().get("text") != null
        ? response.getOutput().get("text").toString() : "<no response>");
    String convoId = (String) (response.getContext().get("conversation_id") != null
        ? (response.getContext().get("conversation_id")).toString() : "<no conversation id>");
    String retrieveAndRankOutput = (String) (response.getOutput().get("CEPayload") != null
        ? response.getOutput().get("CEPayload").toString() : "<no payload>");
    
    cloudantLogging.log(response.getInputText(), intent, confidence, entity, convoOutput, convoId,
        retrieveAndRankOutput);
  }

}
