/* 
* Copyright (C) 2007, Brian Tanner
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

#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "mines.h"

typedef struct 
{
  int START;			/* Start marker in grid */
  int GOAL;			/* End marker in grid */
  int LAND;			/* Free space in grid */
  int OBSTACLE;	                /* Obstical in grid */
  int MINE;                     /* Mine in grid */
  int row;			/* Number of rows in grid */
  int col;			/* Number of columns in grid */
  int startRow;	         	/* Starting position */
  int startCol;   		/* Starting position */
  int agentRow;		        /* Agents current position */
  int agentColumn;	        /* Agents current position */
} mine_env;

Observation o;
mine_env M;
Reward_observation ro;
int env_terminal;

int env_map[6][18] = 
{ 
  { 3, 3, 3, 3, 3, 3 ,3 ,3, 3, 3 ,3 ,3 ,3, 3, 3, 3, 3, 3 }, 
  { 3, 2, 2, 2, 2, 2, 2, 4, 4, 2, 2, 2, 0, 2, 2, 2, 2, 3 }, 
  { 3, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 },
  { 3, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 2, 2, 2, 2, 3, 3 },
  { 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 3 },
  { 3, 3, 3, 3, 3, 3 ,3 ,3, 3, 3 ,3 ,3 ,3, 3, 3, 3, 3, 3 }
};

/* RL-Glue Interface */

Task_specification env_init()
{    
  static char Task_spec[100] = {0};

  M.START = 0;
  M.GOAL = 1;
  M.LAND = 2;
  M.OBSTACLE = 3;
  M.MINE = 4;
  
  M.row = 6;
  M.col = 18;
  M.startRow= 1;
  M.startCol = 12;
  M.agentRow = M.startRow;
  M.agentColumn = M.startCol;
  
  o.numInts    = 1;
  o.numDoubles = 0;

  o.intArray    = (int*)malloc(sizeof(int)*o.numInts);
  o.doubleArray = 0;

	srand(0);

  /* Return task specification */
  
  sprintf(Task_spec,"2:e:1_[i]_[0,%d]:1_[i]_[0,%d]:[-10,10]",M.row*M.col-1,3);
  return Task_spec;
}

Observation env_start()
{   
  int r = 0, c = 0;

  env_terminal = 0;
  env_map[M.startRow][M.startCol] = M.LAND;
  
  while(env_map[r][c] != M.LAND)
  {
    r =  rand()% M.row;
    c = rand()% M.col;
  }

  M.startRow = r;
  M.startCol = c;
  env_map[M.startRow][M.startCol] = M.START;    
  
  M.agentColumn =  M.startCol;
  M.agentRow = M.startRow;

  o.intArray[0] = M.startRow * M.col + M.startCol;

  return o;
}

Reward_observation env_step(Action a)
{    
  getNextPosition(a); /* getNextPosition will update the values of agentRow and agentColumn */
 
  o.intArray[0] = getPosition();
  
  ro.o = o;
  ro.r = getReward();
  
  if(env_terminal) /* end of episode? */
    ro.terminal = 1;
  else
    ro.terminal = 0;

  return ro;
}

void env_cleanup()
{
  free(o.intArray);
  free(o.doubleArray);

  o.intArray    = 0;
  o.doubleArray = 0;
}

void env_set_state(State_key sk)
{
}
     
void env_set_random_seed(Random_seed_key rsk)
{
}

State_key env_get_state()
{
  State_key theKey;
  return theKey;
}

Random_seed_key env_get_random_seed()
{
  Random_seed_key theKey;
  return theKey;
}

Message env_message(const Message inMessage) {
  return NULL;
}


/* mines utitily functions */


void env_print(const char* header, RL_abstract_type* data) {
  unsigned int i = 0;
  fprintf(stderr, "%s", header);
  fprintf(stderr, "%d %d\n", data->numInts, data->numDoubles);

  for (i = 0; i < data->numInts; ++i)
    fprintf(stderr, "%d ", data->intArray[i]);
  fprintf(stderr, "\n");

  for (i = 0; i < data->numDoubles; ++i)
    fprintf(stderr, "%f ", data->doubleArray[i]);
  fprintf(stderr, "\n");
}


int getPosition()
{
  if (env_map[M.agentRow][M.agentColumn] != M.GOAL && env_map[M.agentRow][M.agentColumn] != M.MINE)
  {    
    /* The episode terminates if the agent hits a mine */
    return M.agentRow*M.col + M.agentColumn;
  }
  else
  {
    env_terminal = 1;
    return -1;
  }
}

void getNextPosition(Action a)
{
  /* When the move would result in hitting an obstacles, the agent simply doesn't move */
  int newRow = M.agentRow;
  int newColumn = M.agentColumn;
  
  if (a.intArray[0] == 0)
    newColumn = M.agentColumn - 1;
  else if (a.intArray[0] == 1)
    newColumn = M.agentColumn + 1;
  else if (a.intArray[0] == 2)
    newRow = M.agentRow - 1;
  else if (a.intArray[0] == 3)
    newRow = M.agentRow + 1;
  
  if(newRow >= M.row || newRow < 0 || newColumn >= M.col || newColumn < 0)
  {
    M.agentColumn = M.agentColumn;
    M.agentRow = M.agentRow;
  }
  else if (env_map[newRow][newColumn] != M.OBSTACLE)
  {
    M.agentRow = newRow;
    M.agentColumn = newColumn;
  }
}

Reward getReward()
{
  if (env_map[M.agentRow][M.agentColumn] == M.GOAL){
    return 10;}
  else if (env_map[M.agentRow][M.agentColumn] == M.MINE){
    return -10;}
  else
    return -1;
}
