package lectureplayer;

import java.util.Random;

import battlecode.common.*;

public class RobotPlayer {

    public static Random random = null;

    public static Direction[] directions = {
                Direction.NORTH,
                Direction.NORTHEAST,
                Direction.EAST,
                Direction.SOUTHEAST,
                Direction.SOUTH,
                Direction.SOUTHWEST,
                Direction.WEST,
                Direction.NORTHWEST,
            };
    
    public static void run(RobotController rc) throws GameActionException{
        while (true){
            try {
                if(random == null) random = new Random(rc.getID());
                trySpawn(rc);
                if(rc.isSpawned()) {
                    int round = rc.getRoundNum();
                    if(round < GameConstants.SETUP_ROUNDS) Setup.runSetup(rc);
                    else MainPhase.runMainPhase(rc);
                }
            } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                e.printStackTrace();
            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                // System.out.println("Exception");
                e.printStackTrace();
            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
        }
    }

    private static void trySpawn(RobotController rc) throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        for(MapLocation loc : spawnLocs) {
            if(rc.canSpawn(loc)) {
                rc.spawn(loc);
                break;
            }
        }
    }
}
