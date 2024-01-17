package lectureplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import battlecode.common.*;

public class MainPhase {

    public static void runMainPhase(RobotController rc) throws GameActionException {

        // Buy global upgrade (prioritize capturing)
        if (rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) {
            rc.buyGlobal(GlobalUpgrade.CAPTURING);
        } else if (rc.canBuyGlobal(GlobalUpgrade.ACTION)) {
            rc.buyGlobal(GlobalUpgrade.ACTION);
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
        if (nearbyEnemies.length == 0) {
            for (int idx = Communication.BEGINNING_ENEMY_IDX; idx < 64; idx++) {
                if (Communication.checkIfUpdated(rc, idx))
                    ;
            }
        }
        // try to heal friendly robots
        for (RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (rc.canHeal(robot.getLocation()))
                rc.heal(robot.getLocation());
        }

        FlagInfo[] flags = rc.senseNearbyFlags(-1);
        for (FlagInfo flag : flags) {
            Communication.updateFlagInfo(rc, flag.getLocation(), flag.isPickedUp(), flag.getID());
        }

        if (!rc.hasFlag()) {
            // move towards the closest enemy flag (including broadcast locations)
            ArrayList<MapLocation> flagLocs = new ArrayList<>();
            FlagInfo[] enemyFlags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
            for (FlagInfo flag : enemyFlags) {
                flagLocs.add(flag.getLocation());
            }
            if (flagLocs.size() == 0) {

                // update based on flag id changing
                for (int i = 0; i < Communication.BEGINNING_ENEMY_IDX / 2; i++) {
                    if (Communication.checkIfUpdated(rc, i)) {
                        flagLocs.add(Communication.readLocation(rc, i));
                    }
                }

                MapLocation[] broadcastLocs = rc.senseBroadcastFlagLocations();
                for (MapLocation flagLoc : broadcastLocs)
                    flagLocs.add(flagLoc);
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
}
