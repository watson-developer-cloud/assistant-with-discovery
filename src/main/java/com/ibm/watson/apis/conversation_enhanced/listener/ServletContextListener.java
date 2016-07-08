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

import javax.servlet.ServletContextEvent;
import com.google.gson.JsonObject;



public class ServletContextListener implements javax.servlet.ServletContextListener, PropertyChangeListener {

  public static JsonObject config;

  public void contextDestroyed(ServletContextEvent arg0) {
    // TODO Auto-generated method stub
  }

  public void contextInitialized(ServletContextEvent arg0) {
    SetupThread setupThread = new SetupThread();
    setupThread.addChangeListener(this);
    setupThread.start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt) {
    config = (JsonObject) evt.getNewValue();
  }

  /**
   * Fetches the config JSON Object that is sent to the UI
   * 
   * @return config
   */
  public JsonObject getJsonConfig() {
    return config;
  }
}
