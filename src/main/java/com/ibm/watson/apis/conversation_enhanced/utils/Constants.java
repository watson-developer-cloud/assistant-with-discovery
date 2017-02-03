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

public class Constants {

  // Schema Fields
  public static final String SCHEMA_FIELD_BODY = "body";
  public static final String SCHEMA_FIELD_CONTENT_HTML = "contentHtml";
  public static final String SCHEMA_FIELD_ID = "id";
  public static final String SCHEMA_FIELD_SOURCE_URL = "sourceUrl";
  public static final String SCHEMA_FIELD_TITLE = "title";
  public static final String SCHEMA_FIELD_CONFIDENCE = "ranker.confidence";

  // Setup config JSON object Fields
  public static final String SETUP_STEP = "setup_step";
  public static final String SETUP_STATE = "setup_state";
  public static final String SETUP_PHASE = "setup_phase";
  public static final String SETUP_MESSAGE = "setup_message";
  public static final String SETUP_STATUS_MESSAGE = "setup_status_message";
  public static final String READY = "ready";
  public static final String NOT_READY = "not_ready";

  public static final String WORKSPACE_ID = "WORKSPACE_ID";

  public static final String CONVERSATION_URL = "https://gateway.watsonplatform.net/conversation/api";

  // Discovery JSON object fields

  public static final String DISCOVERY_FIELD_ID = "id";
  public static final String DISCOVERY_FIELD_BODY = "contentHtml";
  public static final String DISCOVERY_FIELD_TITLE = "title";
  public static final String DISCOVERY_FIELD_SOURCE_URL = "sourceUrl";
  public static final String DISCOVERY_FIELD_CONFIDENCE = "score";
  public static final String DISCOVERY_URL = "https://gateway.watsonplatform.net/discovery/api/";
  public static final String DISCOVERY_VERSION = "2016-12-01";
  public static final int DISCOVERY_MAX_SEARCH_RESULTS_TO_SHOW = 3;

}
