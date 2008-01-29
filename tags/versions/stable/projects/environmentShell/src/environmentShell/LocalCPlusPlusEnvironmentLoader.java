/*
Copyright 2007 Brian Tanner
brian@tannerpages.com
http://brian.tannerpages.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package environmentShell;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import rlVizLib.dynamicLoading.CompositeResourceGrabber;
import rlVizLib.dynamicLoading.DylibGrabber;
import rlVizLib.general.ParameterHolder;
import rlglue.environment.Environment;

/**
 *	Java class to talk to a C++ counterpart, 
 *
 *	environmentShell -> LocalCPlusPlusEnvironmentLoader (java) -> CPlusPlusEnvironmentLoader (C++)
 */
public class LocalCPlusPlusEnvironmentLoader implements EnvironmentLoaderInterface {

    // C++ functions to be called from within Java
    public native String JNIgetEnvParams(String fullFilePath);

    Vector<URI> allCPPEnvURIs = new Vector<URI>();
    private Vector<String> theNames = new Vector<String>();
    private Vector<ParameterHolder> theParamHolders = new Vector<ParameterHolder>();
    private Map<String, URI> publicNameToFullName = new TreeMap<String, URI>();
    private Set<URI> allFullURIName = new TreeSet<URI>();

    /**
     * CPPENV.dylib is assumed to be in the same directory as the envShell jar.
     */
    public LocalCPlusPlusEnvironmentLoader() {
        loadLoader();
    }


    /**
     * The CPPENV.dylib is the library that allows the c++ environments to
     * be used in java
     */
    private void loadLoader() {
        System.load(EnvironmentShellPreferences.getInstance().getJNILoaderLibDir() + File.separator+ "CPPENV.dylib");
    }

    private String getShortEnvNameFromURI(URI theURI) {
        String pathAsString = theURI.getPath();

        StringTokenizer toke = new StringTokenizer(pathAsString, File.separator);

        String fileName = "";
        while (toke.hasMoreTokens()) {
            fileName = toke.nextToken();
        }
        return fileName;
    }


    private String addFullNameToMap(URI theURI) {
        int num = 0;
        String theEndName = getShortEnvNameFromURI(theURI);

        String proposedShortName = theEndName;

        while (publicNameToFullName.containsKey(proposedShortName)) {
            num++;
            proposedShortName = theEndName + "(" + num + ")";
        }
        publicNameToFullName.put(proposedShortName, theURI);
        return proposedShortName;
    }

    /**
     * refresh the EnvUrlList to ensure its up to date, then for each URI in
     * the list, find all the dylibs in that directory.
     * 
     * @return true if there were no errors
     */
    public boolean makeList() {
        Vector<URI> allPlacesToLook=EnvironmentShellPreferences.getInstance().getList();
        
        CompositeResourceGrabber compGrabber=new CompositeResourceGrabber();
        for (URI uri : allPlacesToLook) {
            compGrabber.add(new DylibGrabber(uri));
        }
        compGrabber.refreshURIList();
        allCPPEnvURIs = compGrabber.getAllResourceURIs();
        

        for (URI thisURI : allCPPEnvURIs) {
            allFullURIName.add(thisURI);
            String shortName = addFullNameToMap(thisURI);
            theNames.add(shortName);

            String ParamHolderString = JNIgetEnvParams(thisURI.getPath());//JNI call like getParamHolderInStringFormat(i)
            ParameterHolder thisParamHolder = new ParameterHolder(ParamHolderString);
            theParamHolders.add(thisParamHolder);

            String sourcePath = thisURI.getPath();
        }
        System.out.println("Added a total of "+theNames.size()+" cpp envs");
        return true;
    }

    /**
     * This method gets a count of the number of environments, then for each
     * of the environments it gets its name through a JNI call.
     * 
     * @return a Vector of env names
     */
    public Vector<String> getNames() {
        return theNames;
    }

    /**
     * 
     * @return
     */
    public Vector<ParameterHolder> getParameters() {
        return theParamHolders;
    }

    /**
     * 
     * @param envName
     * @param theParams
     * @return
     */
    public Environment loadEnvironment(String envName, ParameterHolder theParams) {
        URI theEnvURI=publicNameToFullName.get(envName);
        
        JNIEnvironment theEnv = new JNIEnvironment(theEnvURI.getPath(), theParams);
        if (theEnv.isValid()) {
            return theEnv;
        } else {
            return null;
        }
    }

    public String getTypeSuffix() {
        return " - C++";
    }
}
