package org.team1540.optometry;

import ccre.channel.FloatInput;
import ccre.cluck.Cluck;
import ccre.cluck.CluckPublisher;
import ccre.cluck.tcp.CluckTCPClient;
import ccre.log.Logger;

public class TestCluck {
	public static void main(String[] args) throws InterruptedException {
		Cluck.setupServer(4001);
		new CluckTCPClient("RoboRIO-1540-FRC.local", Cluck.getNode(), "robot", "kangaroo").start();
		Thread.sleep(1000);
		
		Cluck.getNode().notifyNetworkModified();
		FloatInput input = CluckPublisher.subscribeFI(Cluck.getNode(), "robot/something", true);
		Cluck.publish("Kangaroo_Hello_World", FloatInput.zero.plus(154.0f));
		
		Logger.info("Hello Cluck!");
		
		while (true) {
			Logger.info("something " + input.get());
			Thread.sleep(3000);
		}
	}
}
