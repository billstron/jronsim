package house.occupant;

import util.BoundedRand;
import TranRunJLite.TrjSys;
import TranRunJLite.TrjTask;

public class ApplianceManualTask extends TrjTask {

	private double Pon; // on power
	private double Poff; // off power
	private double Pcurrent; // current operating power
	private BoundedRand rand;

	// the task's states
	private final static int STATE_ON = 1;
	private final static int STATE_OFF = 0;

	// the commands
	public final static int CMD_NONE = -1;
	public final static int CMD_OFF = 0;
	public final static int CMD_ON = 1;

	/**
	 * construct n appliance with a time cycle length and manual start.
	 * 
	 * @param name
	 * @param sys
	 * @param dt
	 * @param Pon
	 * @param dtCycle
	 * @param rand
	 */
	public ApplianceManualTask(String name, TrjSys sys, double dt, double Pon,
			double Poff, BoundedRand rand) {
		super(name, sys, 0/* Initial State */, true/* active */);

		this.Pon = Pon;
		this.Poff = Poff;
		this.Pcurrent = 0;
		this.dtNominal = dt;
		stateNames.add("Off");
		stateNames.add("On");

		if (rand == null) {
			this.rand = new BoundedRand();
		} else {
			this.rand = rand;
		}
	}

	/**
	 * Return the current power consumption
	 * 
	 * @return
	 */
	double getP() {
		return Pcurrent;
	}

	/**
	 * Switch the appliance on
	 * 
	 */
	void switchOn() {
		command = CMD_ON;
	}

	/**
	 * Switch the appliance off
	 * 
	 */
	void switchOff() {
		command = CMD_OFF;
	}

	/**
	 * Check to see if the appliance is on.
	 * 
	 * @return on = true, off = false
	 */
	boolean isOn() {
		boolean on = false;
		if (currentState == STATE_ON)
			on = true;
		return on;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TranRunJLite.TrjTask#RunTask(TranRunJLite.TrjSys)
	 */
	@Override
	public boolean RunTask(TrjSys sys) {
		// get the current time
		double t = sys.GetRunningTime();
		// apply the current state
		switch (currentState) {
		case STATE_OFF: // the appliance is off
			// set the current power
			Pcurrent = Poff;
			nextState = -1;
			if (command == CMD_ON) {
				command = CMD_NONE;
				nextState = STATE_ON;
			}
			break;
		case STATE_ON: // the appliance is on
			// set the current power
			Pcurrent = Pon;
			nextState = -1;
			if (command == CMD_OFF) {
				command = CMD_NONE;
				nextState = STATE_OFF;
			}
			break;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TranRunJLite.TrjTask#RunTaskNow(TranRunJLite.TrjSys)
	 */
	@Override
	public boolean RunTaskNow(TrjSys sys) {
		return CheckTime(sys.GetRunningTime());
	}
}
