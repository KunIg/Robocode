package IK;

import robocode.AdvancedRobot;
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
		  setTurnRight(e.getBearing()+90-
	         30*movementDirection);
	      
	     // If the bot has small energy drop, it most likely fired
	    double changeInEnergy =
	      previousEnergy-e.getEnergy();
	    if (changeInEnergy>0 &&
	        changeInEnergy<=3) {
	         // Dodge!
	         movementDirection =
	          -movementDirection;
	         setAhead((e.getDistance()/4+25)*movementDirection);
	     }
	    
	    // Fire directly at target
	    fire ( 2 ) ;
	    
	    // Track the energy level
	    previousEnergy = e.getEnergy();
	  }

}


