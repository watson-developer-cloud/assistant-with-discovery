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
  public static final String SCHEMA_FIELD_CONTENT_TEXT = "contentText";
  public static final String SCHEMA_FIELD_ID = "id";
  public static final String SCHEMA_FIELD_SOURCE_URL = "sourceUrl";
  public static final String SCHEMA_FIELD_TITLE = "title";
  public static final String SCHEMA_FIELD_CONFIDENCE = "ranker.confidence";
  
  // Number of results to fetch in Query
  public static final Integer RESULTS_TO_FETCH = 5;
  
  // Retrieve and Rank Fields
  public static final String COLLECTION_NAME = "car_collection";
  public static final String CONFIGURATION_NAME = "car_config";
  public static final String CLUSTER_NAME = "car_cluster";
  public static final String RANKER_NAME = "ranker-CarManual";
  
  // Setup config JSON object Fields
  public static final String SETUP_STEP = "setup_step";
  public static final String SETUP_STATE = "setup_state";
  public static final String SETUP_PHASE = "setup_phase";
  public static final String SETUP_MESSAGE = "setup_message";
  public static final String SETUP_STATUS_MESSAGE = "setup_status_message";
  public static final String READY = "ready";
  public static final String NOT_READY = "not_ready";
  
  public static final String WORKSPACE_ID = "WORKSPACE_ID";
}
