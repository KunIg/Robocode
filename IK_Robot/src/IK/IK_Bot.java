package IK;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class IK_Bot extends AdvancedRobot {
	  double previousEnergy = 100;
	  int movementDirection = 1;
	  int gunDirection = 1;
	  public void run() {
		setAdjustGunForRobotTurn(true);
	    do {
	    	if (getRadarTurnRemaining()== 0.0) {
	    		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	    		setTurnGunRightRadians(Double.POSITIVE_INFINITY);
	    	}
	    	execute();
	    } while (true);
	  }
	  public void onScannedRobot(
	    ScannedRobotEvent e) {
	      double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
		  double radarTurn = Utils.normalRelativeAngle(angleToEnemy - getRadarHeadingRadians());
		  double gunTurn = Utils.normalRelativeAngle(angleToEnemy - getGunHeadingRadians());
		  double extraTurn = Math.min(Math.atan(36.0/e.getDistance()),Rules.RADAR_TURN_RATE_RADIANS);
		  radarTurn += (radarTurn < 0 ? -extraTurn : extraTurn);
		  
		  setTurnRadarRightRadians(radarTurn);
		  setTurnGunRightRadians(gunTurn);
	      // Stay at right angles to the opponent
		  setTurnRight(e.getBearing()+90-30*movementDirection);
	      
	     // If the bot has small energy drop, it most likely fired
	    double changeInEnergy =
	      previousEnergy-e.getEnergy();
	    if (changeInEnergy>0 && changeInEnergy<=3) {
	         movementDirection =-movementDirection;
	         setAhead((e.getDistance()/4+25)*movementDirection);
	     }
	    
	    // Fire directly at target
	    fire ( 2 ) ;
	    
	    // Track the energy level
	    previousEnergy = e.getEnergy();
	  }
	  
		public void onHitRobot(HitRobotEvent e) {
			
		      double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
			  double bodyTurn = Utils.normalRelativeAngle(angleToEnemy - getHeadingRadians());

			turnRight(e.getBearing());

			// Determine a shot that won't kill the robot...
			// We want to ram him instead for bonus points
			if (e.getEnergy() > 16) {
				fire(3);
			} else if (e.getEnergy() > 10) {
				fire(2);
			} else if (e.getEnergy() > 4) {
				fire(1);
			} else if (e.getEnergy() > 2) {
				fire(.5);
			} else if (e.getEnergy() > .4) {
				fire(.1);
			}
			ahead(40); // Ram him again!
		}

}


