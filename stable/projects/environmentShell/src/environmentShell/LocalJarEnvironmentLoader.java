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


import rlVizLib.dynamicLoading.EnvOrAgentType;
import rlVizLib.dynamicLoading.LocalJarAgentEnvironmentLoader;
import rlVizLib.general.ParameterHolder;
import rlglue.environment.Environment;

public class LocalJarEnvironmentLoader extends LocalJarAgentEnvironmentLoader implements EnvironmentLoaderInterface{

    public LocalJarEnvironmentLoader() {
        super(EnvironmentShellPreferences.getInstance().getList(),EnvOrAgentType.kEnv);
    }


    public Environment loadEnvironment(String requestedName, ParameterHolder theParams) {
        Object theEnvObject=load(requestedName, theParams);
        if(theEnvObject!=null)return (Environment)theEnvObject;
        return null;
    }

}
