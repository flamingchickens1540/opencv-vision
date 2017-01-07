package org.team1540.optometry.comm;

import ccre.channel.BooleanInput;
import ccre.channel.FloatInput;
import ccre.cluck.Cluck;

public class CommInput {
	private String linkName;
	
	public CommInput(String linkName) {
		this.linkName = linkName;
	}
	
    public FloatInput subscribeFI(String name) {
        return Cluck.subscribeFI(linkName + "/" + name, true);
    }
    
    public BooleanInput subscribeBI(String name) {
        return Cluck.subscribeBI(linkName + "/" + name, true);
    }
}
