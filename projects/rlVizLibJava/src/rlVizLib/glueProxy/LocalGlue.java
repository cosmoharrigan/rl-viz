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

  
package rlVizLib.glueProxy;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Observation_action;
import org.rlcommunity.rlglue.codec.types.Random_seed_key;
import org.rlcommunity.rlglue.codec.types.Reward_observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_action_terminal;
import org.rlcommunity.rlglue.codec.types.State_key;

public class LocalGlue implements RLGlueProxyInterface {
	EnvironmentInterface E=null;
	AgentInterface A=null;

	Action lastAction=null;

	int steps=0;
	boolean isTerminal=false;
	double totalReward=0.0d;
	int totalEpisodes=0;
	
	public LocalGlue(EnvironmentInterface E, AgentInterface A){
		this.E=E;
		this.A=A;
	}



	public synchronized String RL_env_message(String theString){
		return E.env_message(theString);
	}
	public synchronized String RL_agent_message(String theString){
		return A.agent_message(theString);
	}

	public synchronized void RL_init() {
		A.agent_init(E.env_init());
		totalEpisodes=0;
	}


	public synchronized Observation_action RL_start() {
		steps=1;
		isTerminal=false;
		totalReward=0.0d;
		
		Observation o=E.env_start();
		lastAction=A.agent_start(o);
		Observation_action ao=new Observation_action(o, lastAction);
		return ao;
	}



	public synchronized Reward_observation_action_terminal RL_step()	{
		Reward_observation RO=E.env_step(lastAction);
		
		totalReward+=RO.r;
		isTerminal=RO.terminal==1;

		if(isTerminal){
			A.agent_end(RO.r);
			totalEpisodes++;
		}else{
			steps++;
			lastAction=A.agent_step(RO.r, RO.o);
		}
		return new Reward_observation_action_terminal(RO.r,RO.o,lastAction, RO.terminal);
	}


	public synchronized void RL_cleanup() {
            E.env_cleanup();
            A.agent_cleanup();
	}

//Btanner: Jan 13 : Changing this to make it more like RL_glue.c
	public synchronized void RL_episode(int maxStepsThisEpisode) {
		int currentStep=0;
		RL_start();
		for(currentStep=1;!isTerminal &&(maxStepsThisEpisode<=0 || currentStep < maxStepsThisEpisode ) ; currentStep++){
			RL_step();
		}
	}

        /**
         * @deprecated
         */
	public synchronized void RL_freeze() { 
            System.err.println("RL_FREEZE is deprecated");
            Thread.dumpStack();
	}

	public synchronized Random_seed_key RL_get_random_seed() {
            return E.env_get_random_seed();
	}

	public synchronized State_key RL_get_state() {
            return E.env_get_state();
	}



	public synchronized int RL_num_episodes() {
		return totalEpisodes;
	}

	public synchronized int RL_num_steps() {
		return steps;
	}

	public synchronized double RL_return() {
		return totalReward;
	}

	public synchronized void RL_set_random_seed(Random_seed_key rsk) {
            E.env_set_random_seed(rsk);
	}

	public synchronized void RL_set_state(State_key sk) {
            E.env_set_state(sk);
	}



}
