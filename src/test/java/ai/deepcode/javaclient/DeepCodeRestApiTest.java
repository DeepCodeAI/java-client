/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ai.deepcode.javaclient;

import ai.deepcode.javaclient.requests.FileContent;
import ai.deepcode.javaclient.requests.FileContentRequest;
import ai.deepcode.javaclient.responses.CreateBundleResponse;
import ai.deepcode.javaclient.responses.GetAnalysisResponse;
import ai.deepcode.javaclient.responses.LoginResponse;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeepCodeRestApiTest {

  private final String testFileContent =
      "(function($) { \n"
          + "    \n"
          + "    var todos = storage.getTODOs(pullRequestJson).filter(function(todo) {}); \n"
          + "    \n"
          + "}(AJS.$));";

  @Test
  public void _010_newLogin() {
    LoginResponse response = DeepCodeRestApi.newLogin();
    assertNotNull(response);
    assertEquals(
        "https://www.deepcode.ai/login-api?sessionToken=" + response.getSessionToken(),
        response.getLoginURL());
    System.out.println(
        "New login request passed with returned:"
            + "\nsession token: "
            + response.getSessionToken()
            + "\nlogin URL: "
            + response.getLoginURL());
  }

  @Test
  public void _020_checkSession() {
    String token = "";
    int status = DeepCodeRestApi.checkSession(token);
    System.out.printf("Check Session call with token [%1$s] return [%2$d] code.\n", token, status);
    assertEquals(401, status);

    token = "blablabla";
    status = DeepCodeRestApi.checkSession(token);
    System.out.printf("Check Session call with token [%1$s] return [%2$d] code.\n", token, status);
    assertEquals(401, status);

    LoginResponse response = DeepCodeRestApi.newLogin();
    assertNotNull(response);
    token = response.getSessionToken();
    status = DeepCodeRestApi.checkSession(token);
    System.out.printf(
        "Check Session call with newly requested but not yet logged token [%1$s] return [%2$d] code.\n",
        token, status);
    assertEquals(304, status);

    // !!! Will works only with already logged sessionToken
    token = "aeedc7d1c2656ea4b0adb1e215999f588b457cedf415c832a0209c9429c7636e";
    status = DeepCodeRestApi.checkSession(token);
    System.out.printf(
        "Check Session call with logged user's token [%1$s] return [%2$d] code.\n", token, status);
    assertEquals(200, status);
  }

  @Test
  public void _030_createBundle() {
    // !!! Will works only with already logged sessionToken
    String token = "aeedc7d1c2656ea4b0adb1e215999f588b457cedf415c832a0209c9429c7636e";
    int status = DeepCodeRestApi.checkSession(token);
    assertEquals(200, status);
    FileContent fileContent = new FileContent("/test.js", testFileContent);
    FileContentRequest files = new FileContentRequest(Collections.singletonList(fileContent));
    CreateBundleResponse response = DeepCodeRestApi.createBundle(token, files);
    assertNotNull(response);
    System.out.printf("Create Bundle call return next BundleId: [%1$s]\n", response.getBundleId());

    try {
      DeepCodeRestApi.createBundle("fff", files);
      assertNotNull(
          "Create Bundle call with malformed token should not be accepted by server", null);
    } catch (Exception e) {
      System.out.println(
          "Create Bundle call with malformed token [fff] is not accepted by server.");
    }

    try {
      DeepCodeRestApi.createBundle(token, new FileContentRequest(Collections.emptyList()));
      assertNotNull(
          "Create Bundle call with malformed (empty) files array should not be accepted by server",
          null);
    } catch (Exception e) {
      System.out.println(
          "Create Bundle call with malformed (empty) files array is not accepted by server.");
    }
  }

  @Test
  public void _040_getAnalysis() {
    // !!! Will works only with already logged sessionToken and prefetched bundleId
    String token = "aeedc7d1c2656ea4b0adb1e215999f588b457cedf415c832a0209c9429c7636e";
    String bundleId =
        "gh/ArtsiomCh/DEEPCODE_PRIVATE_BUNDLE/83a47f630d9ad28bda6cbb068317565dce5fadce4d71f754e9a99794f2e4fb15";
    GetAnalysisResponse response = DeepCodeRestApi.getAnalysis(token, bundleId);
    System.out.println("Get Analysis call for test file: \n" + testFileContent +"\nreturns: " + response);
    //    assertEquals("DONE", response.getStatus());
  }
}
