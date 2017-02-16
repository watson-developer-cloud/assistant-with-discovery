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
package com.ibm.watson.apis.conversation_with_discovery.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.JsonObject;
import com.ibm.watson.apis.conversation_with_discovery.listener.AppServletContextListener;

/**
 * Unit tests for the {@link SetupResource}.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(SetupResource.class)
public class SetupResourceTest {

  /** The setup resource. */
  SetupResource setupResource;

  /** The mock servlet context listener. */
  AppServletContextListener mockServletContextListener = mock(AppServletContextListener.class);

  /** The config. */
  JsonObject config = new JsonObject();

  /** The workspace id. */
  String WORKSPACE_ID = "123456";

  /**
   * Sets the up.
   */
  @Before
  public void setUp() {
    setupResource = new SetupResource();
    PowerMockito.mockStatic(System.class);
  }

  /**
   * Should return ready state.
   *
   * @throws Exception the exception
   */
  @Test
  public void shouldReturnReadyState() throws Exception {

    // given
    config.addProperty("setup_step", "3");
    config.addProperty("setup_state", "ready");
    config.addProperty("setup_message", "all good");

    when(mockServletContextListener.getJsonConfig()).thenReturn(config);
    PowerMockito.whenNew(AppServletContextListener.class).withAnyArguments().thenReturn(mockServletContextListener);
    when(System.getenv("WORKSPACE_ID")).thenReturn(WORKSPACE_ID);
    when(System.getenv("PASSWORD")).thenReturn("accdefghi");
    when(System.getenv("USERNAME")).thenReturn("abcd-efgh-ijkl");

    // when
    Response response = setupResource.getConfig();

    // then
    assertTrue(response.getEntity().toString().indexOf("\"setup_state\":\"ready\"") > 1);
  }

  /**
   * Should return not ready state.
   *
   * @throws Exception the exception
   */
  @Test
  public void shouldReturnNotReadyState() throws Exception {

    // given
    config.addProperty("setup_step", "2");
    config.addProperty("setup_state", "not_ready");

    when(mockServletContextListener.getJsonConfig()).thenReturn(config);
    PowerMockito.whenNew(AppServletContextListener.class).withAnyArguments().thenReturn(mockServletContextListener);
    when(System.getenv("WORKSPACE_ID")).thenReturn(WORKSPACE_ID);
    when(System.getenv("PASSWORD")).thenReturn("accdefghi");
    when(System.getenv("USERNAME")).thenReturn("abcd-efgh-ijkl");

    // when
    Response response = setupResource.getConfig();

    // then
    assertTrue(response.getEntity().toString().indexOf("\"setup_state\":\"not_ready\"") > 1);
  }

  /**
   * Should return error state.
   *
   * @throws Exception the exception
   */
  @Test
  public void shouldReturnErrorState() throws Exception {

    // given
    config.addProperty("setup_step", "0");
    config.addProperty("setup_state", "not_ready");
    config.addProperty("setup_message", "error");

    when(System.getenv("PASSWORD")).thenReturn("accdefghi");
    when(System.getenv("USERNAME")).thenReturn("abcd-efgh-ijkl");

    when(mockServletContextListener.getJsonConfig()).thenReturn(config);
    PowerMockito.whenNew(AppServletContextListener.class).withAnyArguments().thenReturn(mockServletContextListener);

    // when
    Response response = setupResource.getConfig();

    // then
    assertTrue(response.getEntity().toString().indexOf("\"setup_message\":\"error\"") > 1);
  }

  /**
   * Should return error workspace id.
   *
   * @throws Exception the exception
   */
  @Test
  public void shouldReturnErrorWorkspaceId() throws Exception {

    // given
    config.addProperty("setup_step", "3");
    config.addProperty("setup_state", "ready");
    config.addProperty("setup_message", "all good");
    when(System.getenv("PASSWORD")).thenReturn("accdefghi");
    when(System.getenv("USERNAME")).thenReturn("abcd-efgh-ijkl");

    when(mockServletContextListener.getJsonConfig()).thenReturn(config);
    PowerMockito.whenNew(AppServletContextListener.class).withAnyArguments().thenReturn(mockServletContextListener);

    // when
    Response response = setupResource.getConfig();

    // then
    assertTrue(response.getEntity().toString()
        .indexOf("\"setup_message\":\"See steps on Github for adding an environment variable\"") > 1);
  }
}
