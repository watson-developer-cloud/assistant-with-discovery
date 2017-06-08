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
package com.ibm.watson.apis.conversation_with_discovery.utils;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;

/**
 * The Class Constants.
 */
public class Constants {

  private Constants() {

  }
  /** The Constant CONVERSATION_URL. */
  public static final String CONVERSATION_URL = "https://gateway.watsonplatform.net/conversation/api";
  
  /** The Constant CONVERSATION_VERSION. */
  public static final String CONVERSATION_VERSION = ConversationService.VERSION_DATE_2016_09_20;

  /** The Constant DISCOVERY_FIELD_BODY. */
  public static final String DISCOVERY_FIELD_BODY = "contentHtml";

  /** The Constant DISCOVERY_FIELD_CONFIDENCE. */
  public static final String DISCOVERY_FIELD_CONFIDENCE = "score";

  /** The Constant DISCOVERY_FIELD_ID. */
  public static final String DISCOVERY_FIELD_ID = "id";

  /** The Constant DISCOVERY_FIELD_SOURCE_URL. */
  public static final String DISCOVERY_FIELD_SOURCE_URL = "sourceUrl";

  /** The Constant DISCOVERY_FIELD_TITLE. */
  public static final String DISCOVERY_FIELD_TITLE = "title";

  /** The Constant DISCOVERY_MAX_SEARCH_RESULTS_TO_SHOW. */
  public static final int DISCOVERY_MAX_SEARCH_RESULTS_TO_SHOW = 3;

  /** The Constant DISCOVERY_URL. */
  public static final String DISCOVERY_URL = "https://gateway.watsonplatform.net/discovery/api/";

  /** The Constant DISCOVERY_VERSION. */
  public static final String DISCOVERY_VERSION = "2016-12-15";

  /** The Constant NOT_READY. */
  public static final String NOT_READY = "not_ready";

  /** The Constant READY. */
  public static final String READY = "ready";

  // Discovery JSON object fields
  /** The Constant SCHEMA_FIELD_SOURCE_URL. */
  public static final String SCHEMA_FIELD_SOURCE_URL = "sourceUrl";

  /** The Constant SCHEMA_FIELD_TITLE. */
  public static final String SCHEMA_FIELD_TITLE = "title";

  /** The Constant SETUP_MESSAGE. */
  public static final String SETUP_MESSAGE = "setup_message";

  /** The Constant SETUP_PHASE. */
  public static final String SETUP_PHASE = "setup_phase";

  /** The Constant SETUP_STATE. */
  public static final String SETUP_STATE = "setup_state";

  /** The Constant SETUP_STATUS_MESSAGE. */
  public static final String SETUP_STATUS_MESSAGE = "setup_status_message";

  /** The Constant SETUP_STEP. */
  // Setup config JSON object Fields
  public static final String SETUP_STEP = "setup_step";

  /** The Constant WORKSPACE_ID. */
  public static final String WORKSPACE_ID = "WORKSPACE_ID";

}
