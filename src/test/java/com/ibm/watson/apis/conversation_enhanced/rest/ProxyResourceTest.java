package com.ibm.watson.apis.conversation_enhanced.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.ibm.watson.apis.conversation_enhanced.rest.ProxyResource;
import com.ibm.watson.developer_cloud.conversation.v1_experimental.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1_experimental.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1_experimental.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.HttpHeaders;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.util.GsonSingleton;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Unit tests for the {@link ConversationService}
 */

public class ProxyResourceTest {
  private static final String FIXTURE = "src/test/resources/conversation.json";
  private static final String WORKSPACE_ID = "123";
  protected MockWebServer server;
  protected static final String CONTENT_TYPE = "Content-Type";


  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.watson.developer_cloud.WatsonServiceTest#setUp()
   */
  // @Override
  @Before public void setUp() throws Exception {

    server = new MockWebServer();
    server.start();
    ProxyResource.setCredentials("dummy", "dummy", StringUtils.chop(server.url("/").toString()));
  }

  @After public void tearDown() throws IOException {
    server.shutdown();
  }

  /**
   * Test send message.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InterruptedException the 4interrupted exception
   */
  @Test public void testSendMessage() throws IOException, InterruptedException {
    String text = "I'd like to get a quote to replace my windows";

    MessageResponse mockResponse = loadFixture(FIXTURE, MessageResponse.class);
    ProxyResource proxy = new ProxyResource();
    server.enqueue(jsonResponse(mockResponse));

    MessageRequest request = new MessageRequest.Builder().inputText(text).build();
    String payload = GsonSingleton.getGsonWithoutPrettyPrinting().toJson(request, MessageRequest.class);
    InputStream inputStream = new ByteArrayInputStream(payload.getBytes());
    Response jaxResponse = proxy.postMessage(WORKSPACE_ID, inputStream);
    MessageResponse serviceResponse = GsonSingleton.getGsonWithoutPrettyPrinting()
        .fromJson(jaxResponse.getEntity().toString(), MessageResponse.class);

    RecordedRequest mockRequest = server.takeRequest();
    List<String> serviceText = serviceResponse.getText();
    List<String> mockText = serviceResponse.getText();
    assertNotNull(serviceText);
    assertNotNull(mockText);
    assertTrue(serviceText.containsAll(mockText) && mockText.containsAll(serviceText));
    assertEquals(serviceResponse, mockResponse);
    assertEquals(serviceResponse.getTextConcatenated(" "), mockResponse.getTextConcatenated(" "));

    assertEquals(mockRequest.getMethod(), "POST");
    assertNotNull(mockRequest.getHeader(HttpHeaders.AUTHORIZATION));
  }

  public static <T> T loadFixture(String filename, Class<T> returnType) throws FileNotFoundException {
    String jsonString = getStringFromInputStream(new FileInputStream(filename));
    return new Gson().fromJson(jsonString, returnType);
  }

  public static String getStringFromInputStream(InputStream is) {
    BufferedReader br = null;
    final StringBuilder sb = new StringBuilder();

    String line;
    try {

      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }

    } catch (final IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    }
    return sb.toString();
  }

  /**
   * Create a MockResponse with JSON content type and the object serialized to JSON as body.
   *
   * @param body the body
   * @return the mock response
   */
  protected static MockResponse jsonResponse(Object body) {
    return new MockResponse().addHeader(CONTENT_TYPE, HttpMediaType.APPLICATION_JSON)
        .setBody(GsonSingleton.getGsonWithoutPrettyPrinting().toJson(body));
  }
}