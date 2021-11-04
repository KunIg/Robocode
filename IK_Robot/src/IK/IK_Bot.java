package IK;

import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class IK_Bot extends AdvancedRobot {
	  double previousEnergy = 100;
	  int movementDirection = 1;
	  int gunDirection = 1;
	  double oldEnemyHeading;
	  double buffer;
	  public void run() {
	  double width = this.getBattleFieldWidth();
	  double height = this.getBattleFieldHeight();
	  buffer = 0.2 * Math.max(height, width);
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
		  //double gunTurn = Utils.normalRelativeAngle(angleToEnemy - getGunHeadingRadians());
		  double extraTurn = Math.min(Math.atan(36.0/e.getDistance()),Rules.RADAR_TURN_RATE_RADIANS);
		  radarTurn += (radarTurn < 0 ? -extraTurn : extraTurn);
		  
		  setTurnRadarRightRadians(radarTurn);
		  //setTurnGunRightRadians(gunTurn);
	      // Stay at right angles to the opponent
		  setTurnRight(e.getBearing()+90-30*movementDirection);
	      
	     // If the bot has small energy drop, it most likely fired
	    double changeInEnergy = previousEnergy-e.getEnergy();
	    if (changeInEnergy>0 && changeInEnergy<=3) {
	         movementDirection =-movementDirection;
	         setAhead((e.getDistance()/4+25)*movementDirection);
	     }
	    
	    
	    // Fire directly at target
	    if (e.getDistance()<2*buffer) firelineary ( e );
	    
	    // Track the energy level
	    previousEnergy = e.getEnergy();
	  }
	  
		public void onHitRobot(HitRobotEvent e) {

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
		
		
		public void firelineary(ScannedRobotEvent e) {
			double bulletPower = 3.0;
			double myX = this.getX();
			double myY = this.getY();
			double absoluteBearing = e.getBearingRadians() + this.getHeadingRadians();
			double enemyX = myX + e.getDistance() * Math.sin(absoluteBearing);
			double enemyY = myY + e.getDistance() * Math.cos(absoluteBearing);
			double enemyHeading = e.getHeadingRadians();
			double enemyHeadingChange = enemyHeading - oldEnemyHeading;
			oldEnemyHeading = enemyHeading;
			double enemyVelocity = e.getVelocity();
			
			double deltaTime = 0;
			double predictedX = enemyX;
			double predictedY = enemyY;
			
			while (((++deltaTime)*(20-3.0*bulletPower))<Point2D.Double.distance(myX,myY,predictedX,predictedY)) {
				predictedX += Math.sin(enemyHeading)*enemyVelocity;
				predictedY += Math.cos(enemyHeading)*enemyVelocity;
				enemyHeading += enemyHeadingChange;
			}
			
			double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX-myX, predictedY-myY));
			this.setTurnGunRightRadians(Utils.normalRelativeAngle(theta-this.getGunHeadingRadians()));
			fire(bulletPower);
			
		}
		
		
		

}


