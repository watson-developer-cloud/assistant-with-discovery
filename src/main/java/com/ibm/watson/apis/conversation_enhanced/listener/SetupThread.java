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
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.ibm.watson.apis.conversation_enhanced.utils.Constants;
import com.ibm.watson.apis.conversation_enhanced.utils.Messages;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.discovery.v1.model.query.QueryRequest;
import com.ibm.watson.developer_cloud.service.exception.UnauthorizedException;

/**
 * This class is forked when the application is accessed for the first time. It
 * tests if the conversation and discovery service associated with the app
 * (either bound on bluemix or specified in server.env)
 */
public class SetupThread extends Thread {

  private static final Logger logger = LogManager.getLogger(SetupThread.class.getName());

  private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();

  public JsonObject config = new JsonObject();

  public void run() {

    String status = "";

    try {

      updateConfigObject("1", Constants.NOT_READY, Messages.getString("SetupThread.EMPTY"),
          Messages.getString("SetupThread.GETTING_CREDENTIALS"));

      // test discovery credentials

      String userName = System.getenv("DISCOVERY_USERNAME");
      String password = System.getenv("DISCOVERY_PASSWORD");
      String collectionId = System.getenv("DISCOVERY_COLLECTION_ID");
      String environmentId = System.getenv("DISCOVERY_ENVIRONMENT_ID");

      if (userName == null || password == null || collectionId == null || environmentId == null) {
        throw new IllegalArgumentException(Messages.getString("SetupThread.DISC_INVALID_CREDS"));
      }
      if (userName.length() == 0 || password.length() == 0 || collectionId.length() == 0
          || environmentId.length() == 0) {
        throw new IllegalArgumentException(Messages.getString("SetupThread.DISC_INVALID_CREDS"));
      }

      status = "Discovery ";

      Discovery discovery = new Discovery(Constants.DISCOVERY_VERSION);
      discovery.setEndPoint(Constants.DISCOVERY_URL);
      discovery.setUsernameAndPassword(userName, password);

      QueryRequest.Builder queryBuilder = new QueryRequest.Builder(environmentId, collectionId);
      queryBuilder.query("searchText:car tire pressure");
      discovery.query(queryBuilder.build()).execute();

      // test conversation credentials

      updateConfigObject("2", Constants.NOT_READY, Messages.getString("SetupThread.EMPTY"),
          Messages.getString("SetupThread.GETTING_CREDENTIALS"));

      userName = System.getenv("CONVERSATION_USERNAME");
      password = System.getenv("CONVERSATION_PASSWORD");
      String workspaceId = System.getenv("WORKSPACE_ID");

      if (userName == null || password == null || workspaceId == null) {
        throw new IllegalArgumentException(Messages.getString("SetupThread.CONV_INVALID_CREDS"));
      }
      if (userName.length() == 0 || password.length() == 0 || workspaceId.length() == 0) {
        throw new IllegalArgumentException(Messages.getString("SetupThread.CONV_INVALID_CREDS"));
      }

      status = "Conversation ";

      ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);
      service.setUsernameAndPassword(userName, password);
      MessageRequest newMessage = new MessageRequest.Builder().inputText("hi").context(null).build();
      service.message(workspaceId, newMessage).execute();

      updateConfigObject("3", Constants.READY, Messages.getString("SetupThread.EMPTY"),
          Messages.getString("SetupThread.EMPTY"));

      logger.info(Messages.getString("SetupThread.SETUP_COMPLETE"));
    } catch (Exception e) {
      logger.error(Messages.getString("SetupThread.ERROR_COLLECTION_INIT") + e.getMessage());
      if (e instanceof UnauthorizedException) {
        updateConfigObject("0", Constants.NOT_READY, Messages.getString("SetupThread.ERROR"), status + e.getMessage());
      } else {
        updateConfigObject("0", Constants.NOT_READY, Messages.getString("SetupThread.ERROR"),
            e.getMessage() + " " + Messages.getString("SetupThread.CHECK_LOGS"));
      }
    }
  }

  /**
   * Method to update the listeners about any property changes. This is used by
   * the UI to inform user what the status of the setup.
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
   * @param newListener
   *          PropertyChangeListener
   */
  public void addChangeListener(PropertyChangeListener newListener) {
    listener.add(newListener);
  }

  /**
   * This method is used to update the JSON Config Object that will be sent back
   * to the UI.
   * 
   * @param config
   * @param setup_step
   *          The step at which the setup is
   * @param setup_state
   *          The state of the setup(accepts ready/not_ready)
   * @param setup_phase
   *          The current phase of the setup(this can be that the services is
   *          being setup or if any other phase of the application is being
   *          setup)
   * @param setup_message
   *          The message that you want to be shown in the UI.
   */
  private void updateConfigObject(String setup_step, String setup_state, String setup_phase, String setup_message) {
    config.addProperty(Constants.SETUP_STEP, setup_step); // $NON-NLS-1$
    config.addProperty(Constants.SETUP_STATE, setup_state); // $NON-NLS-1$
    config.addProperty(Constants.SETUP_PHASE, setup_phase); // $NON-NLS-1$
    config.addProperty(Constants.SETUP_MESSAGE, setup_message); // $NON-NLS-1$
    notifyListeners(this, config);
  }
}
