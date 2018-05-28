package bwsmallbrother;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import bwapi.Order;
import bwapi.Position;
import bwapi.Unit;


class Action {
	private int ActionID = 0;
	private int PlayerReplayID = 0;
	private Object UnitCommandTypeID;
	private Object OrderTypeID;
	private int UnitGroupID = 0;
	private Object TargetID = -1;
	private int TargetX = -1;
	private int TargetY = -1;
	private Object UnitTypeID;
	private int AC_FRAME = 0;
	private Object Race; 
	
	public Action(int ActionID, int PlayerReplayID, Object UnitCommandTypeID, Object OrderTypeID, int UnitGroupID, 
			Object TargetID, int TargetX, int TargetY, Object UnitTypeID, int AC_FRAME, Object Race) {
		this.ActionID = ActionID;
	    this.PlayerReplayID = PlayerReplayID;
	    this.UnitCommandTypeID = UnitCommandTypeID;
	    this.OrderTypeID = OrderTypeID;
	    this.UnitGroupID = UnitGroupID;
	    this.TargetID = TargetID;
	    this.TargetX = TargetX;
	    this.TargetY = TargetY;
	    this.UnitTypeID = UnitTypeID;
	    this.AC_FRAME = AC_FRAME;
	    this.Race = Race; 
	  }

}

class FrameSnap {
	public List<Unit> snapUnit;
	public Map<Integer, Order> order;
	public Map<Integer, Position> orderTargetPosition;

	public FrameSnap(List<Unit> sUnit, Map<Integer, Order> order,  Map<Integer, Position> orderTargetPosition){
    	this.snapUnit = sUnit;
    	this.order = order;
    	this.orderTargetPosition = orderTargetPosition;
    }
    
    public List<Unit> getUnits(){
    	return snapUnit;
    }
    
    public Map<Integer, Order> getAllOrders(){
    	return order;
    }
    
    public Map<Integer, Position> getAllOrdersPosition(){
    	return orderTargetPosition;
    }
    
}

class Result {
	  public int cannon_rush = 0;
	  public int three_gateways = 0;
	  public int two_gateways = 0;
	  public int zergling_rush = 0;
	  Result() {
	    // no-args constructor
	  }
	}

class PlayerBase{
	public int x = 0;
	public int y = 0;
	
	public PlayerBase(int x, int y){
    	this.x = x;
    	this.y = y;
    }
	
}

class PredictObj{
    public List<Action> actions = null;
    public PlayerBase base = null;
    
	public PredictObj( List<Action> actions, PlayerBase base){
    	this.actions = actions;
    	this.base = base;
    }
}