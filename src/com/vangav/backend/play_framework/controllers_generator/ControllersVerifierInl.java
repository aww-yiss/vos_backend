/**
 * "First, solve the problem. Then, write the code. -John Johnson"
 * "Or use Vangav M"
 * www.vangav.com
 * */

/**
 * no license, I know you already got more than enough to worry about
 * keep going, never give up
 * */

/**
 * Community
 * Facebook Group: Vangav Open Source - Backend
 *   fb.com/groups/575834775932682/
 * Facebook Page: Vangav
 *   fb.com/vangav.f
 * 
 * Third party communities for Vangav Backend
 *   - play framework
 *   - cassandra
 *   - datastax
 *   
 * Tag your question online (e.g.: stack overflow, etc ...) with
 *   #vangav_backend
 *   to easier find questions/answers online
 * */

package com.vangav.backend.play_framework.controllers_generator;

import java.util.HashSet;
import java.util.Set;

import com.vangav.backend.content.checking.JavaCodeVerifierInl;
import com.vangav.backend.content.checking.StringVerifierInl;
import com.vangav.backend.exceptions.CodeException;
import com.vangav.backend.exceptions.VangavException.ExceptionClass;
import com.vangav.backend.files.FileLoaderInl;
import com.vangav.backend.play_framework.controllers_generator.json.ControllerJson;
import com.vangav.backend.play_framework.controllers_generator.json.ControllersJson;
import com.vangav.backend.play_framework.controllers_generator.json.RequestParamJson;
import com.vangav.backend.play_framework.controllers_generator.json.ResponseParamJson;
import com.vangav.backend.play_framework.param.ParamOptionality;
import com.vangav.backend.play_framework.param.ParamType;
import com.vangav.backend.play_framework.request.response.ResponseBody;

/**
 * @author mustapha
 * fb.com/mustapha.abdallah
 */
/**
 * ControllersVerifierInl has inline static methods for verifying controllers
 *   config [controllers.json]
 * */
public class ControllersVerifierInl {
  
  // disable default instantiation
  private ControllersVerifierInl () {}
  
  /**
   * verifyControllersJson
   * throws an exception if param controllersJson is invalid
   * @param controllersJson
   * @throws Exception
   */
  public static void verifyControllersJson (
    final String configDirPath,
    final String projectName) throws Exception {
    
    if (FileLoaderInl.fileExists(
          configDirPath
          + "/"
          + ControllersGeneratorConstantsInl.kControllersConfigFileName) ==
        false) {
      
      return;
    }
    
    String controllersJsonRaw =
      FileLoaderInl.loadTextFileWithoutComments(
        ControllersGeneratorConstantsInl.kControllerCommentPrefix,
        configDirPath
        + "/"
        + ControllersGeneratorConstantsInl.kControllersConfigFileName);
    
    ControllersJson controllersJson =
      ControllersJson.fromJsonString(controllersJsonRaw);
    
    JavaCodeVerifierInl.verifyPackageName(controllersJson.java_package);
    JavaCodeVerifierInl.verifyIdentifier(
      "project name",
      projectName);
    
    if (controllersJson.controllers == null) {
      
      return;
    }
    
    Set<String> controllersNames = new HashSet<String>();
    Set<String> requestParamNames;
    Set<String> responseParamNames;
    
    for (ControllerJson controllerJson : controllersJson.controllers) {
      
      // verify name
      JavaCodeVerifierInl.verifyIdentifier(
        "controller name",
        controllerJson.name);
      
      // check for duplicates
      if (controllersNames.contains(controllerJson.name) == true) {
        
        throw new CodeException(
          "Can't have more than one controller with the same name ["
          + controllerJson.name
          + "]",
          ExceptionClass.JSON);
      }
      
      controllersNames.add(controllerJson.name);
      
      // verify type
      StringVerifierInl.isOneOfString(
        "controller type",
        controllerJson.type,
        true,
        "GET",
        "POST");
      
      if (controllerJson.request_params != null) {
        
        requestParamNames = new HashSet<String>();
        
        // verify request params
        for (RequestParamJson requestParamJson :
             controllerJson.request_params) {
          
          JavaCodeVerifierInl.verifyIdentifier(
            "request param name",
            requestParamJson.name);
          
          if (requestParamNames.contains(requestParamJson.name) == true) {
            
            throw new CodeException(
              "Controller ["
              + controllerJson.name
              + "] can't contain duplicate request param ["
              + requestParamJson.name
              + "]",
              ExceptionClass.JSON);
          }
          
          requestParamNames.add(requestParamJson.name);
          
          StringVerifierInl.belongsToEnum(
            "request param type",
            requestParamJson.type,
            ParamType.class);
          
          StringVerifierInl.belongsToEnum(
            "request param optionality",
            requestParamJson.optionality,
            ParamOptionality.class);
        }
      }
      
      // verify response type
      StringVerifierInl.belongsToEnum(
        "response type",
        controllerJson.response_type,
        ResponseBody.ResponseType.class);
      
      if (controllerJson.response_params != null) {
        
        responseParamNames = new HashSet<String>();
      
        // verify response params
        for (ResponseParamJson responseParamJson :
             controllerJson.response_params) {
          
          JavaCodeVerifierInl.verifyIdentifier(
            "response param name",
            responseParamJson.name);
          
          if (responseParamNames.contains(responseParamJson.name) == true) {
            
            throw new CodeException(
              "Controller ["
              + controllerJson.name
              + "] can't contain duplicate request param ["
              + responseParamJson.name
              + "]",
              ExceptionClass.JSON);
          }
          
          responseParamNames.add(responseParamJson.name);
          
          StringVerifierInl.isOneOfString(
            "response param type",
            responseParamJson.type,
            false,
            "short",
            "int",
            "long",
            "float",
            "double",
            "String");
        }
      }
    }
  }
}
