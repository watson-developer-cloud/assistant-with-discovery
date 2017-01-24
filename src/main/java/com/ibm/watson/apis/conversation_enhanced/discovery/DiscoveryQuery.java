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
package com.ibm.watson.apis.conversation_enhanced.discovery;

import com.ibm.watson.apis.conversation_enhanced.utils.Constants;
import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.discovery.v1.model.query.QueryRequest;
import com.ibm.watson.developer_cloud.discovery.v1.model.query.QueryResponse;

public class DiscoveryQuery {

  private String userName;

  private String password;

  private String collectionId;

  private String environmentId;

  private Discovery discovery;

  public DiscoveryQuery() {
    userName = System.getenv("DISCOVERY_USERNAME");
    password = System.getenv("DISCOVERY_PASSWORD");
    collectionId = System.getenv("DISCOVERY_COLLECTION_ID");
    environmentId = System.getenv("DISCOVERY_ENVIRONMENT_ID");

    discovery = new Discovery(Constants.DISCOVERY_VERSION);
    discovery.setEndPoint(Constants.DISCOVERY_URL);
    discovery.setUsernameAndPassword(userName, password);
  }

  /**
   * Use the Watson Developer Cloud SDK to send the user's query to the
   * discovery service
   * 
   * @param userQuery
   *          The user's query to be sent to the discovery service
   * @return The query responses obtained from the discovery service
   * @throws Exception
   */
  public QueryResponse query(String userQuery) throws Exception {
    QueryRequest.Builder queryBuilder = new QueryRequest.Builder(environmentId, collectionId);

    StringBuilder sb = new StringBuilder();
    sb.append("searchText:");
    sb.append(userQuery);
    sb.append(",");
    sb.append("enrichedText:");
    sb.append(userQuery);

    queryBuilder.query(sb.toString());
    QueryResponse queryResponse = discovery.query(queryBuilder.build()).execute();

    return queryResponse;
  }
}
