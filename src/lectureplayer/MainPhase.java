package lectureplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import battlecode.common.*;

public class MainPhase {

    static int[] flagIDs; // 0 0 0

    public static void runMainPhase(RobotController rc) throws GameActionException {
        flagIDs = new int[6];

        // Buy global upgrade (prioritize attack)
        if(rc.canBuyGlobal(GlobalUpgrade.ATTACK)) {
            rc.buyGlobal(GlobalUpgrade.ATTACK);
        } 
        else if(rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) {
            rc.buyGlobal(GlobalUpgrade.CAPTURING);
        }

        if (RobotPlayer.personalID == 0) {
            for (int i = 0; i < 64; i++) {
                Communication.setUnupdated(rc, i);
            }
        }
        // attack enemies, prioritizing enemies that have your flag
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for (RobotInfo robot : nearbyEnemies) {
            if (robot.hasFlag()) {
                Pathfind.moveTowards(rc, robot.getLocation(), true);
                if (rc.canAttack(robot.getLocation()))
                    rc.attack(robot.getLocation());
            }
        }
        for (RobotInfo robot : nearbyEnemies) {
            if (rc.canAttack(robot.getLocation())) {
                rc.attack(robot.getLocation());
            }
        }
        Communication.updateEnemyInfo(rc, rc.getLocation(), nearbyEnemies.length);

        // try to heal friendly robots
        for (RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (rc.canHeal(robot.getLocation()))
                rc.heal(robot.getLocation());
        }

        FlagInfo[] allFlags = rc.senseNearbyFlags(-1);
        for (FlagInfo flag : allFlags) {
            int flagID = flag.getID();
            int idx = flagIDToIdx(rc, flagID);
            Communication.updateFlagInfo(rc, flag.getLocation(), flag.isPickedUp(), flag.getTeam(), idx);
        }

        if (!rc.hasFlag()) {
            // move towards the closest enemy flag (including broadcast locations)
            ArrayList<MapLocation> flagLocs = new ArrayList<>();
            FlagInfo[] enemyFlags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
            for (FlagInfo flag : enemyFlags)
                flagLocs.add(flag.getLocation());
            if (flagLocs.size() == 0) {
                for (int i = 0; i <= Communication.LAST_FLAG_IDX; i++) {
                    if (Communication.getTeam(rc, i) == rc.getTeam().opponent() && Communication.getIfUpdated(rc, i)) {
                        flagLocs.add(Communication.getLocation(rc, i));
                    }
                }
                if (flagLocs.size() == 0) {
                    MapLocation[] broadcastLocs = rc.senseBroadcastFlagLocations();
                    for (MapLocation flagLoc : broadcastLocs)
                        flagLocs.add(flagLoc);
                }
            }

            MapLocation closestFlag = findClosestLocation(rc.getLocation(), flagLocs);
            if (closestFlag != null) {
                Pathfind.moveTowards(rc, closestFlag, true);

                if (rc.canPickupFlag(closestFlag))
                    rc.pickupFlag(closestFlag);
            } else {
                // if there are no dropped enemy flags, explore randomly
                Pathfind.explore(rc);
            }
        } else {
            // if we have the flag, move towards the closest ally spawn zone
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            MapLocation closestSpawn = findClosestLocation(rc.getLocation(), Arrays.asList(spawnLocs));
            Pathfind.moveTowards(rc, closestSpawn, true);
        }
    }

    public static MapLocation findClosestLocation(MapLocation me, List<MapLocation> otherLocs) {
        MapLocation closest = null;
        int minDist = Integer.MAX_VALUE;
        for (MapLocation loc : otherLocs) {
            int dist = me.distanceSquaredTo(loc);
            if (dist < minDist) {
                minDist = dist;
                closest = loc;
            }
        }
        return closest;
    }

    public static int flagIDToIdx(RobotController rc, int flagID) {
        for (int i = 0; i < flagIDs.length; i++) {
            if (flagIDs[i] == 0) {
                flagIDs[i] = flagID;
                return i;
            } else if (flagIDs[i] == flagID) {
                return i;
            } else
                continue;
        }
        return 0;
    }
}
