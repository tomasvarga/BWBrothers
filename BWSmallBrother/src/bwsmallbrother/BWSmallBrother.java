package bwsmallbrother;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Order;
import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import bwta.BWTA;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bwsmallbrother.Server_UCType;

import org.ini4j.*;


public class BWSmallBrother extends DefaultBWListener {

    private Mirror mirror = new Mirror();
    private Game game;
    private Player player;
    private int SEND_EACH_FRAME = 240;
    private int frameToRecord = SEND_EACH_FRAME;
	private Map<String, String> strategies = new HashMap<>();
    private List<Action> actions = new ArrayList<>();
    private int actionCount = 0;

    private int unitGroupId = 0;
    
    private int rendStaretgY = 200;

    private Map<Integer, FrameSnap> snapFrames = new HashMap<>();
    
    private Map<Integer, Integer> unitGroupIds = new HashMap<>();
    
    private Ini ini;
    private String pname = "";
    
    private PlayerBase base = null;
    
    
    public void run() {
		try {
			ini = new Ini(new File("settings.ini"));
	    	SEND_EACH_FRAME = Integer.parseInt(ini.get("send_each", "frame"));
	    	strategies.put("CannonRush", "-");
	    	strategies.put("ZerglingRush", "-");
	    	strategies.put("TwoGateways", "-");
	    	strategies.put("ThreeGateways", "-");
	        mirror.getModule().setEventListener(this);
	        mirror.startGame();
		} catch (InvalidFileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public void onEnd(boolean isWinner){
    	//Reset everything
    	frameToRecord = SEND_EACH_FRAME;
    	actionCount = 0;
    	actions.clear();
    	unitGroupIds.clear();
    	unitGroupId = 0;
    	
    	snapFrames.clear();
        actionCount = 0;
    	
    	strategies.put("CannonRush", "-");
    	strategies.put("ZerglingRush", "-");
    	strategies.put("TwoGateways", "-");
    	strategies.put("ThreeGateways", "-");
    	rendStaretgY = 200;
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        game.enableFlag(1);
        //First time load any player
        player = game.getPlayer(0);
        //Load player name from ini
        pname = ini.get("player", "name");
        //game.setLatCom(false);
        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
    }	
    
    @Override
    public void onFrame() {
		if (!player.getName().equals(pname)){
		 	for( Player p: game.getPlayers()){
        		if (p.getName().equals(pname)){
      	        	player = p;
      	        	break;
        		}
        	}
		}else{
	        game.setTextSize(bwapi.Text.Size.Enum.Large);   
	    	game.drawTextScreen(15, 10,  "Monitoring player: " + player.getName());
	
			for (Object obj : strategies.entrySet()) {
				Map.Entry<String, String> entry = (Map.Entry) obj;
	        	game.drawTextScreen(15, rendStaretgY,  entry.getKey() + ":" +  entry.getValue());
	        	if (entry.getKey() != "ThreeGateways"){
	        		rendStaretgY += 25;
	        	}else{
	        		rendStaretgY = 200;
	        	}
			}
	        
			int frame = game.getFrameCount();
			if (frame > 0){
				Map<Integer, Order> orders = new HashMap<>();
				Map<Integer, Position> ordersPosition = new HashMap<>();
				
				for (Unit u: player.getUnits()){
					orders.put(u.getID(), u.getOrder());
					ordersPosition.put(u.getID(), u.getOrderTargetPosition());
				}
				FrameSnap snapFrame = new FrameSnap(player.getUnits(), orders, ordersPosition);
				snapFrames.put(frame, snapFrame);
			}
	        for (Unit myUnit : player.getUnits()) {
	        	Integer groupId = 0;
	        	if (!unitGroupIds.containsKey(myUnit.getID())){
	        		unitGroupIds.put(myUnit.getID(), unitGroupId);
	        	}else{
    	        	groupId = unitGroupIds.get(myUnit.getID());
	        	}
	        	
	        	Order order = myUnit.getOrder();
	        	if (order != bwapi.Order.None && order != bwapi.Order.Nothing && order != bwapi.Order.PlayerGuard 
	        			&& order != bwapi.Order.MoveToGas && order != bwapi.Order.MoveToMinerals  && order != bwapi.Order.MiningMinerals
	        			&& order != bwapi.Order.ReturnMinerals && order != bwapi.Order.Harvest4 && order != bwapi.Order.HarvestGas && order != bwapi.Order.WaitForMinerals){
	        		// BUILDING
	        		if (order == bwapi.Order.PlaceBuilding || order == bwapi.Order.PlaceAddon || order == bwapi.Order.BuildNydusExit){
	        			UnitType builder = null;
	        			UnitType targetID = null;
	        			if (player.getRace().equals(Race.Zerg)){
	        				FrameSnap beforeSnap = snapFrames.get(frame-1);
		    				int index = findUnitBackInTime(frame-1, myUnit);
		            		if (index != -1 ){
		                        	Order oldOrder = beforeSnap.getAllOrders().get(index);
		                            if (oldOrder != order){
		    	        				builder = myUnit.getInitialType();
		    	        				targetID = myUnit.getBuildType();
		    			            	unitGroupId += 1;
		    	        				groupId = unitGroupId;
					                    addNewAction(myUnit.getPlayer().getID(), UnitCommandType.Build, Order.None, groupId, targetID, myUnit.getPosition().getX(), myUnit.getPosition().getY(), builder, frame, myUnit.getPlayer().getRace());		                            	
		                            }
		            			}
	        			}else{
	        				builder = myUnit.getType();
		        			targetID = myUnit.getOrderTarget().getType();
			            	unitGroupId += 1;
	        				groupId = unitGroupId;
		        			addNewAction(myUnit.getPlayer().getID(), UnitCommandType.Build, Order.None, groupId, targetID, myUnit.getOrderTarget().getPosition().getX(), myUnit.getOrderTarget().getPosition().getY(), builder, frame, myUnit.getPlayer().getRace());
	        			}
	        		}
	        		 
	        		// ANYTHING DIFFERENT -> PRINT HERE
	        		if (order != bwapi.Order.Move && order != bwapi.Order.PlaceBuilding && order != bwapi.Order.RightClickAction && order != bwapi.Order.Stop && order != bwapi.Order.Guard &&  order != bwapi.Order.Larva  &&  order != bwapi.Order.ZergUnitMorph && order != bwapi.Order.PickupIdle && order != bwapi.Order.ZergBirth && order != bwapi.Order.TowerGuard && order != bwapi.Order.AttackUnit && order != bwapi.Order.IncompleteBuilding && order != bwapi.Order.ResetCollision && order != bwapi.Order.PlaceAddon && order != bwapi.Order.BuildNydusExit && order != bwapi.Order.AttackMove  ){
	        			System.out.println(order);
	        		}
	        		
	        		// MORPHING
	        		if (order == bwapi.Order.ZergUnitMorph){
	        			FrameSnap beforeSnap = snapFrames.get(frame-1);
	    				int index = findUnitBackInTime(frame-1, myUnit);
	            		if (index != -1 ){
	                        	Order oldOrder = beforeSnap.getAllOrders().get(index);
	                            if (oldOrder != order){
	                            	unitGroupId += 1;
	    	        				groupId = unitGroupId;
	                            	addNewAction(myUnit.getPlayer().getID(), UnitCommandType.Morph, Order.None, groupId, myUnit.getBuildType(), -1, -1, myUnit.getInitialType(), frame, myUnit.getPlayer().getRace());
	                            	
	                            }
	            			}
	        		}
	        		
	        		// MOVING
	        		if (order == bwapi.Order.Move){
	    				FrameSnap beforeSnap = snapFrames.get(frame-1);
	    				int index = findUnitBackInTime(frame-1, myUnit);
	            		if (index != -1 ){            				
	            				Position uP = beforeSnap.getAllOrdersPosition().get(index);
	                        	if (uP == null){
	                        		uP = myUnit.getOrderTargetPosition();
	                        	}
	                        	Order oldOrder = beforeSnap.getAllOrders().get(index);
	                            Position pos = myUnit.getOrderTargetPosition();
	                            if (oldOrder != order){
	                            	addNewAction(myUnit.getPlayer().getID(), UnitCommandType.Move, Order.None, groupId, pos.getX(), myUnit.getInitialPosition().getX(), pos.getY(), myUnit.getType(), frame, myUnit.getPlayer().getRace());
	                    		}
								else if (myUnit.getOrderTargetPosition().getDistance(uP) != 0){
	                            	addNewAction(myUnit.getPlayer().getID(), UnitCommandType.None, Order.Move, groupId, -1, myUnit.getInitialPosition().getX(), pos.getY(), myUnit.getType(), frame, myUnit.getPlayer().getRace());
	                    		}
	            			}
	        		}
	        		
	        		// ATTACK_MOVE
	        		if (order == bwapi.Order.AttackMove){
	                	addNewAction(myUnit.getPlayer().getID(), UnitCommandType.None, Order.AttackMove, groupId, myUnit.getOrderTarget(), myUnit.getOrderTarget().getTargetPosition().getX(),myUnit.getOrderTarget().getTargetPosition().getY(), myUnit.getType(), frame, myUnit.getPlayer().getRace());
	        		}
	        		
	        		// ATTACK
	        		if (order == bwapi.Order.AttackUnit){
	                	addNewAction(myUnit.getPlayer().getID(), UnitCommandType.None, Order.AttackUnit, groupId, myUnit.getOrderTarget().getType(), myUnit.getOrderTarget().getTargetPosition().getX(),myUnit.getOrderTarget().getTargetPosition().getY(), myUnit.getType(), frame, myUnit.getPlayer().getRace());
	        		}
	        	}
	        	 
	        	if (order == bwapi.Order.Nothing && myUnit.getSecondaryOrder() != bwapi.Order.Nothing){
	        		// TRAINING
	            	if (myUnit.getSecondaryOrder() == Order.Train){ 
			            	if (frame == frame + (myUnit.getTrainingQueue().get(0).buildTime() - (myUnit.getRemainingTrainTime()))){
			            		UnitType type = myUnit.getTrainingQueue().get(0);
				            	unitGroupId += 1;
    	        				groupId = unitGroupId;
	                            addNewAction(myUnit.getPlayer().getID(), UnitCommandType.Train, Order.None, groupId, type, -1, -1, myUnit.getType(), frame, myUnit.getPlayer().getRace());
			            	}
	            	}
	            }
	        	
	        }
	      	
			if (frame >= frameToRecord){
				frameToRecord += SEND_EACH_FRAME;
				//TODO: ADD HTTP ASYNCH -> this is temporary solution working just fine, so far.
				 CompletableFuture.runAsync(() -> {
				 try {
					 predictActions();
			  		} catch (ClientProtocolException e) {
			  			// TODO Auto-generated catch block
			  			e.printStackTrace();
			  		} catch (IOException e) {
			  			// TODO Auto-generated catch block
			  			e.printStackTrace();
			  		}
				});
			}
		}
    }
    
    public void addNewAction(Integer playerID, UnitCommandType unitCommandType, Order orderType, 
    		Integer unitGroupID, Object targetID, Integer targetX, Integer targetY, UnitType unitType, Integer frame, Race race){
    	actionCount++;
	
    	//BUG: normally we would just get the key, but for some reason its not working correctly -> thats why this workaround  
		for ( UnitType key : Server_UCType.serverUNTypeIds.keySet() ) {
		    if (targetID.toString().equals(key.toString())){
		    	targetID = Server_UCType.serverUNTypeIds.get(key);
		    	break;
		    }
		}

    	int ucTypeID = Server_UCType.serverUCTypeIds.get(unitCommandType);
    	int unitTypeID = Server_UCType.serverUNTypeIds.get(unitType);
    	int raceID = Server_UCType.serverRaceIds.get(race);
    	int orderID = Server_UCType.serverOTypeIds.get(orderType);

    	Action newAct = new Action(actionCount, playerID, ucTypeID, orderID, unitGroupID, targetID, targetX, targetY, unitTypeID, frame, raceID);
    	actions.add(newAct);
    }
    

    public int findUnitBackInTime(Integer frame, Unit myUnit){
    	FrameSnap beforeSnap = snapFrames.get(frame);
		for (int c= 0; c < beforeSnap.getUnits().size(); c++){ 
			if (myUnit.getID() == beforeSnap.getUnits().get(c).getID() ){
				return beforeSnap.getUnits().get(c).getID();
			}
		}
    	return -1;
    }
    
    public void predictActions() throws IOException{
    	HttpClient httpclient = HttpClients.createDefault();
    	String ip = ini.get("server_ip", "address") + ":" + ini.get("server_ip", "port") + "/predict";
        HttpPost httppost = new HttpPost("http://"+ip);
        
    	// Request parameters and other properties.
    	Gson gson = new Gson();
    	
    	if (base == null){
         	base = new PlayerBase(0, 0);
        	base.x = player.getStartLocation().toPosition().getX();
        	base.y = player.getStartLocation().toPosition().getY();
    	}

    	PredictObj obj = new PredictObj(actions, base);
    	
    	String json = gson.toJson(obj); 
    	
    	StringEntity se = new StringEntity(json);
    	httppost.addHeader("content-type", "application/json");
    	
    	//Execute and get the response.
    	httppost.setEntity(se);
    	HttpResponse response = httpclient.execute(httppost);

		// Get the response
		 if(response != null){
            InputStream source = response.getEntity().getContent(); //Get the data in the entity
            Reader reader = new InputStreamReader(source);
		    Gson gson2 = new Gson();
		    Result obj2 = gson2.fromJson(reader, Result.class);  
		    if (obj2.cannon_rush == 1){
		    	strategies.put("CannonRush", "ACTIVE");
		    }else{
		    	strategies.put("CannonRush", "-");
		    }
		    
		    if (obj2.zergling_rush == 1){
		    	strategies.put("ZerglingRush", "ACTIVE");
		    }else{
		    	strategies.put("ZerglingRush", "-");
		    }
		    
		    if (obj2.two_gateways == 1){
		    	strategies.put("TwoGateways", "ACTIVE");
		    }else{
		    	strategies.put("TwoGateways", "-");
		    }
		    
		    if (obj2.three_gateways == 1){
		    	strategies.put("ThreeGateways", "ACTIVE");
		    }else{
		    	strategies.put("ThreeGateways", "-");
		    }
		}
    }
    
    
}
