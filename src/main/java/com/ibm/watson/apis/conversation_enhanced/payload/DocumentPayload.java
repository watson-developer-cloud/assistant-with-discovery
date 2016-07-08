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
package com.ibm.watson.apis.conversation_enhanced.payload;

/**
 * A Payload object which describes a single result returned by the Retrieve and Rank Service. The
 * service is provided with a user query, and returns a list of 'hits' for the query. Each hit is
 * called a 'document'. The document contains various metadata which describes the data the service
 * retrieved.
 */
public class DocumentPayload {

  private String title;
  private String body;
  private String sourceUrl;
  private String highlight;
  private String id;

  private String confidence;

  /**
   * Returns a <code>String</code> which represents the <code>title</code> of the document. This may
   * be null, depending on how the service was configured.
   * 
   * @return a string which represents the title of the document.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of the document. May be null.
   * 
   * @param title the title of the document.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns the text in the search result which is to be highlighted, indicating a match to the
   * search term.
   * 
   * @return a string which is to be highlighted
   */
  public String getHighlight() {
    return highlight;
  }

  /**
   * Sets the text which is to be highlighted.
   * 
   * @param highlight text to be highlighted.
   */
  public void setHighlight(String highlight) {
    this.highlight = highlight;
  }

  /**
   * Returns the body of the search result. The search result is comprised of several pieces of
   * metadata which when combined form a 'document'. This method returns the <code>body</code> of
   * the search result.
   * 
   * @return a string representing the body of the search result.
   */
  public String getBody() {
    return body;
  }

  /**
   * Sets the body of the document.
   * 
   * @param body the actual text contained in the body of the document.
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * Returns a urls which links to the source document owned by the service.
   * 
   * @return a url
   */
  public String getSourceUrl() {
    return sourceUrl;
  }

  /**
   * Sets the source document url.
   * 
   * @param url a string
   */
  public void setSourceUrl(String url) {
    this.sourceUrl = url;
  }

  /**
   * A unique id which represents the document.
   * 
   * @return a string which represents the doc id.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the document id.
   * 
   * @param id a string.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Returns the confidence associated with the search result. The higher the confidence the more
   * likely it is that the search result contains data relevant to the search query.
   * 
   * @return a value representing the confidence the service has that the result is a match.
   */
  public String getConfidence() {
    return confidence;
  }

  /**
   * Sets the confidence that the system has in the search result.
   * 
   * @param confidence confidence values for the answer
   */
  public void setConfidence(String confidence) {
    this.confidence = confidence;
  }

  @Override public String toString() {
    return "Track [title=" + title + ", body=" + body + "]";
  }

}
