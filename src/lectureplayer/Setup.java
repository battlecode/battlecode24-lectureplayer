package lectureplayer;

import battlecode.common.*;

public class Setup {

    private static final int EXPLORE_ROUNDS = 150;
    
    public static void runSetup(RobotController rc) throws GameActionException {

        if(rc.getRoundNum() < EXPLORE_ROUNDS) {
            //pickup flag if possible, explore randomly
            FlagInfo[] flags = rc.senseNearbyFlags(-1);
            for(FlagInfo flag : flags) {
                MapLocation flagLoc = flag.getLocation();
                if(rc.senseMapInfo(flagLoc).isSpawnZone() && rc.canPickupFlag(flagLoc)) {
                    rc.pickupFlag(flag.getLocation());
                }
            }
            Pathfind.explore(rc);
        }
        else {
            //try to place flag if it is far enough away from other flags
            if(rc.senseLegalStartingFlagPlacement(rc.getLocation())) {
                if(rc.canDropFlag(rc.getLocation())) rc.dropFlag(rc.getLocation());
            }
            //move towards flags and place defenses around them
            FlagInfo[] flags = rc.senseNearbyFlags(-1);

            FlagInfo targetFlag = null;
            for(FlagInfo flag : flags) {
                if(!flag.isPickedUp()) {
                    targetFlag = flag;
                    break;
                }
            }

            if(targetFlag != null) {
                Pathfind.moveTowards(rc, targetFlag.getLocation(), false);
                if(rc.getLocation().distanceSquaredTo(flags[0].getLocation()) < 9) {
                    if(rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation())) {
                        rc.build(TrapType.EXPLOSIVE, rc.getLocation());
                    }
                    else {
                        MapLocation waterLoc = rc.getLocation().add(RobotPlayer.directions[RobotPlayer.random.nextInt(8)]);
                        if(rc.canDig(waterLoc)) rc.dig(waterLoc);
                    }
                }
            } 
            else Pathfind.explore(rc);
        }
    }
}
