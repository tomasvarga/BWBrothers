package bwsmallbrother;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import bwapi.Order;
import bwapi.Race;
import bwapi.UnitCommandType;
import bwapi.UnitType;


public class Server_UCType {
	
	public static final Map<UnitCommandType, Integer> serverUCTypeIds = createMap();
	public static final Map<Order, Integer> serverOTypeIds = createMap2();
	public static final Map<UnitType, Integer> serverUNTypeIds = createMap3();
	public static final Map<Race, Integer> serverRaceIds = createMap4();

    private static Map<UnitCommandType, Integer> createMap() {
        Map<UnitCommandType, Integer> result = new HashMap<>();
        result.put(UnitCommandType.Build, 2);
        result.put(UnitCommandType.Train, 4);
        result.put(UnitCommandType.Morph, 5);
        result.put(UnitCommandType.Research, 6);
        result.put(UnitCommandType.Upgrade, 7);
        result.put(UnitCommandType.Move, 10);
        result.put(UnitCommandType.Hold_Position, 12);
        result.put(UnitCommandType.Stop, 13);
        result.put(UnitCommandType.Return_Cargo, 16);
        result.put(UnitCommandType.Burrow, 17);
        result.put(UnitCommandType.Unburrow, 18);
        result.put(UnitCommandType.Siege, 22);
        result.put(UnitCommandType.Unsiege, 23);
        result.put(UnitCommandType.Lift, 24);
        result.put(UnitCommandType.Unload, 27);
        result.put(UnitCommandType.Unload_All, 28);
        result.put(UnitCommandType.Cancel_Construction, 33);
        result.put(UnitCommandType.Cancel_Addon, 34);
        result.put(UnitCommandType.Cancel_Train, 35);
        result.put(UnitCommandType.Cancel_Morph, 37);
        result.put(UnitCommandType.Use_Tech, 40);
        result.put(UnitCommandType.None, 44);
        return Collections.unmodifiableMap(result);
    }
    

    private static Map<Order, Integer> createMap2() {
        Map<Order, Integer> result = new HashMap<>();
        result.put(Order.Move, 6);
        result.put(Order.AttackUnit, 8);
        result.put(Order.AttackMove, 14);
        result.put(Order.Repair, 34);
        result.put(Order.RallyPointTile, 40);
        result.put(Order.Follow, 49);
        result.put(Order.TrainFighter, 63);
        result.put(Order.PickupTransport, 94);
        result.put(Order.CastDarkSwarm, 119);
        result.put(Order.CastParasite, 120);
        result.put(Order.CastSpawnBroodlings, 121);
        result.put(Order.PlaceMine, 132);
        result.put(Order.CastScannerSweep, 139);
        result.put(Order.CastPsionicStorm, 142);
        result.put(Order.CastConsume, 145);
        result.put(Order.Patrol, 152);
        result.put(Order.MedicHeal, 176);
        result.put(Order.None, 189);   
        return Collections.unmodifiableMap(result);
    }
    
    private static Map<UnitType, Integer> createMap3() {
        Map<UnitType, Integer> result = new HashMap<>();
        result.put(UnitType.Terran_Marine, 0);
        result.put(UnitType.Terran_Ghost, 1);
        result.put(UnitType.Terran_Vulture, 2);
        result.put(UnitType.Terran_Goliath, 3);
        result.put(UnitType.Terran_Goliath, 4);
        result.put(UnitType.Terran_SCV, 7);
        result.put(UnitType.Terran_Wraith, 8);
        result.put(UnitType.Terran_Science_Vessel, 9);
        result.put(UnitType.Hero_Gui_Montag, 10);
        result.put(UnitType.Terran_Dropship, 11);
        result.put(UnitType.Terran_Battlecruiser, 12);
        result.put(UnitType.Terran_Vulture_Spider_Mine, 13);
        result.put(UnitType.Terran_Nuclear_Missile, 14);
        result.put(UnitType.Hero_Sarah_Kerrigan, 16);
        result.put(UnitType.Hero_Alan_Schezar, 17);
        result.put(UnitType.Hero_Hyperion, 28);
        result.put(UnitType.Hero_Norad_II, 29);
        result.put(UnitType.Terran_Siege_Tank_Siege_Mode, 30);
        result.put(UnitType.Terran_Siege_Tank_Tank_Mode, 31);
        result.put(UnitType.Terran_Firebat, 32);
        result.put(UnitType.Spell_Scanner_Sweep, 33);
        result.put(UnitType.Terran_Medic, 34);
        result.put(UnitType.Zerg_Larva, 35);
        result.put(UnitType.Zerg_Egg, 36);
        result.put(UnitType.Zerg_Zergling, 37);
        result.put(UnitType.Zerg_Hydralisk, 38);
        result.put(UnitType.Zerg_Ultralisk, 39);
        result.put(UnitType.Zerg_Broodling, 40);
        result.put(UnitType.Zerg_Drone, 41);
        result.put(UnitType.Zerg_Overlord, 42);
        result.put(UnitType.Zerg_Mutalisk, 43);
        result.put(UnitType.Zerg_Guardian, 44);
        result.put(UnitType.Zerg_Queen, 45);
        result.put(UnitType.Zerg_Defiler, 46);
        result.put(UnitType.Zerg_Scourge, 47);
        result.put(UnitType.Hero_Torrasque, 48);
        result.put(UnitType.Hero_Matriarch, 49);
        result.put(UnitType.Zerg_Infested_Terran, 50);
        result.put(UnitType.Hero_Infested_Kerrigan, 51);
        result.put(UnitType.Hero_Unclean_One, 52);
        result.put(UnitType.Hero_Hunter_Killer, 53);
        result.put(UnitType.Hero_Devouring_One, 54);
        result.put(UnitType.Hero_Kukulza_Mutalisk, 55);
        result.put(UnitType.Hero_Kukulza_Guardian, 56);
        result.put(UnitType.Hero_Yggdrasill, 57);
        result.put(UnitType.Terran_Valkyrie, 58);
        result.put(UnitType.Zerg_Cocoon, 59);
        result.put(UnitType.Protoss_Corsair, 60);
        result.put(UnitType.Protoss_Dark_Templar , 61);
        result.put(UnitType.Zerg_Devourer, 62);
        result.put(UnitType.Protoss_Dark_Archon, 63);
        result.put(UnitType.Protoss_Probe, 64);
        result.put(UnitType.Protoss_Zealot, 65);
        result.put(UnitType.Protoss_Dragoon, 66);
        result.put(UnitType.Protoss_High_Templar, 67);
        result.put(UnitType.Protoss_Archon, 68);
        result.put(UnitType.Protoss_Shuttle, 69);
        result.put(UnitType.Protoss_Scout, 70);
        result.put(UnitType.Protoss_Arbiter, 71);
        result.put(UnitType.Protoss_Carrier, 72);
        result.put(UnitType.Protoss_Interceptor, 73);
        result.put(UnitType.Hero_Dark_Templar, 74);
        result.put(UnitType.Hero_Zeratul, 75);
        result.put(UnitType.Hero_Tassadar_Zeratul_Archon, 76);
        result.put(UnitType.Hero_Fenix_Zealot, 77);
        result.put(UnitType.Hero_Fenix_Dragoon, 78);
        result.put(UnitType.Hero_Tassadar, 79);
        result.put(UnitType.Hero_Mojo, 80);
        result.put(UnitType.Hero_Warbringer, 81);
        result.put(UnitType.Hero_Gantrithor, 82);
        result.put(UnitType.Protoss_Reaver, 83);
        result.put(UnitType.Protoss_Observer, 84);
        result.put(UnitType.Protoss_Scarab, 85);
        result.put(UnitType.Hero_Danimoth, 86);
        result.put(UnitType.Hero_Aldaris, 87);
        result.put(UnitType.Hero_Artanis, 88);
        result.put(UnitType.Critter_Rhynadon, 89);
        result.put(UnitType.Critter_Bengalaas, 90);
        result.put(UnitType.Special_Cargo_Ship, 91);
        result.put(UnitType.Special_Mercenary_Gunship, 92);
        result.put(UnitType.Critter_Scantid, 93);
        result.put(UnitType.Critter_Kakaru, 94);
        result.put(UnitType.Critter_Ragnasaur, 95);
        result.put(UnitType.Critter_Ursadon, 96);
        result.put(UnitType.Zerg_Lurker_Egg, 97);
        result.put(UnitType.Special_Map_Revealer, 101);
        result.put(UnitType.Zerg_Lurker, 103);
        result.put(UnitType.Zerg_Infested_Terran, 104);
        result.put(UnitType.Spell_Disruption_Web, 105);
        result.put(UnitType.Terran_Command_Center, 106);
        result.put(UnitType.Terran_Comsat_Station, 107);
        result.put(UnitType.Terran_Nuclear_Silo, 108);
        result.put(UnitType.Terran_Supply_Depot, 109);
        result.put(UnitType.Terran_Refinery, 110);
        result.put(UnitType.Terran_Barracks, 111);
        result.put(UnitType.Terran_Academy, 112);
        result.put(UnitType.Terran_Factory, 113);
        result.put(UnitType.Terran_Starport, 114);
        result.put(UnitType.Terran_Control_Tower, 115);
        result.put(UnitType.Terran_Science_Facility, 116);
        result.put(UnitType.Terran_Covert_Ops, 117);
        result.put(UnitType.Terran_Physics_Lab, 118);
        result.put(UnitType.Terran_Machine_Shop, 120);
        result.put(UnitType.Terran_Engineering_Bay, 122);
        result.put(UnitType.Terran_Armory, 123);
        result.put(UnitType.Terran_Missile_Turret, 124);
        result.put(UnitType.Terran_Bunker, 125);
        result.put(UnitType.Special_Crashed_Norad_II, 126);
        result.put(UnitType.Special_Ion_Cannon, 127);
        result.put(UnitType.Powerup_Uraj_Crystal, 128);
        result.put(UnitType.Powerup_Khalis_Crystal, 129);
        result.put(UnitType.Zerg_Infested_Command_Center, 130);
        result.put(UnitType.Zerg_Hatchery, 131);
        result.put(UnitType.Zerg_Lair, 132);
        result.put(UnitType.Zerg_Hive, 133);
        result.put(UnitType.Zerg_Nydus_Canal, 134);
        result.put(UnitType.Zerg_Hydralisk_Den, 135);
        result.put(UnitType.Zerg_Defiler_Mound, 136);
        result.put(UnitType.Zerg_Greater_Spire, 137);
        result.put(UnitType.Zerg_Queens_Nest, 138);
        result.put(UnitType.Zerg_Evolution_Chamber, 139);
        result.put(UnitType.Zerg_Ultralisk_Cavern, 140);
        result.put(UnitType.Zerg_Spire, 141);
        result.put(UnitType.Zerg_Spawning_Pool, 142);
        result.put(UnitType.Zerg_Creep_Colony, 143);
        result.put(UnitType.Zerg_Spore_Colony, 144);
        result.put(UnitType.Zerg_Sunken_Colony, 146);
        result.put(UnitType.Special_Overmind_With_Shell, 147);
        result.put(UnitType.Special_Overmind, 148);
        result.put(UnitType.Zerg_Extractor, 149);
        result.put(UnitType.Special_Mature_Chrysalis, 150);
        result.put(UnitType.Special_Cerebrate, 151);
        result.put(UnitType.Special_Cerebrate_Daggoth, 152);
        result.put(UnitType.Protoss_Nexus, 154);
        result.put(UnitType.Protoss_Robotics_Facility, 155);
        result.put(UnitType.Protoss_Pylon, 156);
        result.put(UnitType.Protoss_Assimilator, 157);
        result.put(UnitType.Protoss_Observatory, 159);
        result.put(UnitType.Protoss_Gateway, 160);
        result.put(UnitType.Protoss_Photon_Cannon, 162);
        result.put(UnitType.Protoss_Citadel_of_Adun, 163);
        result.put(UnitType.Protoss_Cybernetics_Core, 164);
        result.put(UnitType.Protoss_Templar_Archives, 165);
        result.put(UnitType.Protoss_Forge, 166);
        result.put(UnitType.Protoss_Stargate, 167);
        result.put(UnitType.Special_Stasis_Cell_Prison, 168);
        result.put(UnitType.Protoss_Fleet_Beacon, 169);
        result.put(UnitType.Protoss_Arbiter_Tribunal, 170);
        result.put(UnitType.Protoss_Robotics_Support_Bay, 171);
        result.put(UnitType.Protoss_Shield_Battery, 172);
        result.put(UnitType.Resource_Vespene_Geyser, 188);
        result.put(UnitType.None, 228);
        result.put(UnitType.Unknown, 233);

        return Collections.unmodifiableMap(result);
    }
    
    private static Map<Race, Integer> createMap4() {
        Map<Race, Integer> result = new HashMap<>();
        result.put(Race.Zerg, 0);
        result.put(Race.Terran, 1);
        result.put(Race.Protoss, 2);
        result.put(Race.Unknown, 5);
        return Collections.unmodifiableMap(result);
    }
    

}
