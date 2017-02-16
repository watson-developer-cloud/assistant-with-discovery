/*
 * Copyright 2015 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.ibm.watson.apis.conversation_with_discovery.listener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.servlet.ServletContextEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

/**
 * The listener interface for receiving servletContext events. The class that is interested in processing a
 * servletContext event implements this interface, and the object created with that class is registered with a component
 * using the component's <code>addServletContextListener<code> method. When the servletContext event occurs, that
 * object's appropriate method is invoked.
 *
 * @see ServletContextEvent
 */
public class AppServletContextListener implements javax.servlet.ServletContextListener, PropertyChangeListener {

  /** The config. */
  private JsonObject config = new JsonObject();
  private static final Logger logger = LogManager.getLogger(AppServletContextListener.class.getName());

  /*
   * (non-Javadoc)
   *
   * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
   */
  @Override
  public void contextDestroyed(ServletContextEvent arg0) { 
    logger.info("Destroying ServletContextListener");
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
   */
  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    SetupThread setupThread = new SetupThread();
    setupThread.addChangeListener(this);
    setupThread.start();
    logger.info("Deploying ServletContextListener");
  }

  /**
   * Fetches the config JSON Object that is sent to the UI.
   *
   * @return config
   */
  public JsonObject getJsonConfig() {
    return config;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans. PropertyChangeEvent)
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    config = (JsonObject) evt.getNewValue();
  }
}
