/* RL-VizLib, a library for C++ and Java for adding advanced visualization and dynamic capabilities to RL-Glue.
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package rlVizLib.glueProxy;

import rlglue.agent.Agent;
import rlglue.environment.Environment;
import rlglue.types.Observation_action;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation_action_terminal;
import rlglue.types.State_key;

public class RLGlueProxy{
	private static RLGlueProxyInterface instance = null;
	
	static boolean localGlue=false;
	static boolean netGlue=true;
	static boolean inited=false;

	static boolean currentEpisodeOver=true;

	static public boolean isLocal(){
		return localGlue;
	}
	
	static public boolean isNet(){
		return netGlue;
	}
	
	static public boolean isInited(){
		return inited;
	}
	protected RLGlueProxy() {
		// Exists only to defeat instantiation.
	}

	public static void useNetGlue(){
		if(instance != null) {
			System.err.println("Someone tried to set the glue proxy to NET but its already set!");
			Thread.dumpStack();
		}
		localGlue=false;
		netGlue=true;
		instance = new RegularNetGlue();
	}
	public static void useLocalGlue(Environment E,Agent A){
		if(instance != null) {
			System.err.println("Someone tried to set the glue proxy to LOCAL but its already set!");
			Thread.dumpStack();
		}	
		localGlue=true;
		netGlue=false;
		instance = new LocalGlue(E,A);
	}
	
	private static void checkInstance(){
		if(instance == null) {
                    //This isn't really an error
//			System.err.println("Someone tried to use the glue proxy before deciding on type, defaulting to network");
			instance = new RegularNetGlue();
		}
	}

	public static String RL_agent_message(String message) {
		checkInstance();
		return instance.RL_agent_message(message);
	}
	public static  void RL_cleanup() {
		checkInstance();
                if(!inited)System.err.println("-- Warning From RLGlueProxy :: RL_cleanup() was called without matching RL_init() call previously.");
                instance.RL_cleanup();
		inited=false;
	}
	public static String RL_env_message(String message) {
		checkInstance();
		return instance.RL_env_message(message);
	}
	public static void RL_episode(int numSteps) {
		checkInstance();
		instance.RL_episode(numSteps);
	}

	public static void RL_freeze() {
		checkInstance();
		instance.RL_freeze();
	}

	public static Random_seed_key RL_get_random_seed() {
		checkInstance();
		return instance.RL_get_random_seed();
	}
	public static State_key RL_get_state() {
		checkInstance();
		return instance.RL_get_state();
	}
	public static void RL_init() {
		checkInstance();
                if(inited)System.err.println("-- Warning From RLGlueProxy :: RL_init() was called more than once without RL_cleanup.");
		instance.RL_init();
		inited=true;
                currentEpisodeOver=true;
	}
	public static int RL_num_episodes() {
		checkInstance();
		return instance.RL_num_episodes();
	}

	public static int RL_num_steps() {
		checkInstance();
		return instance.RL_num_steps(); 
	}
	public static double RL_return() {
		checkInstance();
		return instance.RL_return();
	}
	public static void RL_set_random_seed(Random_seed_key rsk) {
		checkInstance();
		instance.RL_set_random_seed(rsk);
	}
	public static void RL_set_state(State_key sk) {
		checkInstance();
		instance.RL_set_state(sk);
	}
	
	public static Observation_action RL_start() {
		checkInstance();
                if(!inited)System.err.println("-- Warning From RLGlueProxy :: RL_start() was called without RL_init().");
                currentEpisodeOver=false;
		return instance.RL_start();
	}
	public static Reward_observation_action_terminal RL_step() {
		checkInstance();
                if(!inited)System.err.println("-- Warning From RLGlueProxy :: RL_step() was called without RL_init().");
                Reward_observation_action_terminal stepResponse=instance.RL_step();
                currentEpisodeOver=(stepResponse.terminal==1);

		return stepResponse;
	}

        public static boolean isCurrentEpisodeOver(){
            return currentEpisodeOver;
        }
}