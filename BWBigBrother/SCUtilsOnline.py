import pandas as pd
import numpy as np
import random
from collections import defaultdict
from time import gmtime, strftime

add_folder = "structures/"
order_type = add_folder + "OrderTypeID.csv"
unitcommand_type = add_folder + "UnitCommandTypeID.csv"
unit_type = add_folder + "unitType.csv"
workersID = [64, 7, 41] #all workers only
buildings = add_folder + "buildings.csv"
attackUnits = add_folder + "attackUnits.csv"

def countNumberOf(data, columnName, checkInList, defaultNumber, countWorkers):
    countList = []
    replayID = -1
    count = defaultNumber
    unitGroups = []
    for index,row in data.iterrows():
        if row['PlayerReplayID'] != replayID:
            count = defaultNumber
            replayID = row['PlayerReplayID']
        if countWorkers:
            #TODO: zerg drone -> larva, ...
            if row['UnitCommandTypeID'] == 2 and row['Race'] == 0:
                if (count-1) != 0 :
                    count -= 1
            if row['UnitCommandTypeID'] == 37 and row['Race'] == 0:
                count += 1
            if row['UnitCommandTypeID'] == 4 and (row[columnName] in checkInList):
                count += 1
        else:
            if (row[columnName] in checkInList) and (row['UnitGroupID'] not in unitGroups):
                count += 1
                unitGroups.append(row['UnitGroupID'])
        # -> find out how to do something like cancelation, death, etc...
        #else if row['UnitCommandTypeID'] in [33, 35]:
        #    count -= 1
        #Zerg worker(Drone) morph into building -> is lost in the process.
        countList.append(count)
    return countList

def identifyRace(data):
    # 0: Zerg; 1: Terran; 2: Protoss; 5: None
    replayID = 0
    addRaceColumn = []
    race = 5
    for index,row in data.iterrows():
        if row['PlayerReplayID'] != replayID:
            race = 5
            replayID = row['PlayerReplayID']
        if (row['UnitTypeID'] == 41 or row['UnitTypeID'] == 35) or row['TargetID'] == 41:
            race = 0
        elif row['UnitTypeID'] == 7 or row['TargetID'] == 7:
            race = 1
        elif row['UnitTypeID'] == 64 or row['TargetID'] == 64:
            race = 2
        addRaceColumn.append(race)
    return addRaceColumn

# MARK: Get coordinates to be used for later calculation
def getCoordinates(data):
    firstCoordinates = {}
    secondCoordinates = {}
    replays = []
    replayID = 0
    for index,row in data.loc[(data['AC_FRAME'] <= 400)].iterrows():
        if row['PlayerReplayID'] != replayID:
            replayID = row['PlayerReplayID']
        if row['PlayerReplayID'] not in replays:
            if row['UnitCommandTypeID'] == 10:
                #juno
                if abs(row['TargetID'] - row['TargetX']) >= 200:
                    firstCoordinates[row['PlayerReplayID']] = { 'unitGroupID': row['UnitGroupID'], 'targetID': row['TargetID'], 'targetX': row['TargetX'], 'targetY': row['TargetY']}
                    replays.append(row['PlayerReplayID'])
            if row['OrderTypeID'] == 6:
                #others
                    firstCoordinates[row['PlayerReplayID']] = { 'unitGroupID': row['UnitGroupID'], 'targetID': row['TargetID'], 'targetX': row['TargetX'], 'targetY': row['TargetY']}
                    replays.append(row['PlayerReplayID'])
        else:
            if row['OrderTypeID'] == 6 and firstCoordinates[row['PlayerReplayID']]['unitGroupID'] == row['UnitGroupID']:
                secondCoordinates[row['PlayerReplayID']] = { 'unitGroupID': row['UnitGroupID'], 'targetID': row['TargetID'], 'targetX': row['TargetX'], 'targetY': row['TargetY']}
    return (firstCoordinates, secondCoordinates)

# MARK: Find possible start locations.
def findStartLocation(data):
    enemyBase = {}
    junoBase = {}
    (coord1, coord2) = getCoordinates(data)
    for replay1 in coord1:
        for replay2 in coord2:
            if replay1 == replay2:
                enemyID = replay2
                enemyBase[enemyID] = (coord2[replay2]['targetX'],  coord2[replay2]['targetY'])
                junoBase[replay2] = (coord1[replay1]['targetX'],  coord1[replay1]['targetY'])
    return (junoBase, enemyBase)

# MARK: Find scout unit for the beginning of game.
def findScoutUnit(data):
    scouts = {}
    (firstCoordinates, secondCoordinates) = getCoordinates(data)
    for replay1 in firstCoordinates:
            scouts[replay1] = firstCoordinates[replay1]['unitGroupID']
    return scouts


def scoutInTheGroup(data, columnName, checkInList, replayID):
    binaryList = []
    b = 0
    for index,row in data.iterrows():
        if row['PlayerReplayID'] not in replayID:
            b = 0
            replayID.append(row['PlayerReplayID'])
        if row[columnName] in checkInList:
            b = 1
            # -> "scoutIsProbablyDead"- t: ~4 min ::after some time it doesn't matter if the scout is in the group
                #-> find a new one who is building cannons?
            if row['AC_FRAME'] >= 5750:
                b = 0
        else:
            b = 0
        binaryList.append(b)
    return binaryList


def isNearEnemy(data, playerBase, timeStop):
    binaryList = []
    b = 0
    timeAt = -1000
    for index,row in data.iterrows():
        if row['OrderTypeID'] == 6:
            x  = playerBase['x']
            y  = playerBase['y']
            if (x-1000 <= row['TargetX'] <= x+1000) and (y-1000 <= row['TargetY'] <= y+1000):
                b = 1
                timeAt = row['AC_FRAME']
        else:
            b = 0
        #if the position didn't change for a while he/or someone in the group is in the enemy base / is heading there
        if (row['AC_FRAME']-120) <= timeAt <= (row['AC_FRAME']+120):
            b = 1
        else:
            b = 0
        binaryList.append(b)
    return binaryList

def expFeature(data, columnName, checkInList, replayID, cooldownActiv):
    binaryList = []
    b = 0
    time = 0
    for index,row in data.iterrows():
        if row['PlayerReplayID'] not in replayID:
            b = 0
            replayID.append(row['PlayerReplayID'])
        if row[columnName] in checkInList:
            b = 1
            time = row['AC_FRAME'] + 480
        if cooldownActiv:
            if row['AC_FRAME'] >= time:
                b = 0
        binaryList.append(b)
    return binaryList

def ratio(data, columnName, checkInList):
    countList = []
    actionList = 0.00
    replayID = 0
    count = 0.00
    for index,row in data.iterrows():
        if row['PlayerReplayID'] != replayID:
            count = 0.00
            actionList = 0.00
            replayID = row['PlayerReplayID']
        if row[columnName] in checkInList:
            count += 1.00
        actionList += 1.00
        countList.append(round(count/actionList, 2))
    return countList

def getUnitsGroupIDs(data, checkInList):
    replayID = 0
    unitGroups = []
    for index,row in data.iterrows():
        if row['PlayerReplayID'] != replayID:
            replayID = row['PlayerReplayID']
        #after training collect group id
        if (row["TargetID"] in checkInList and row['UnitCommandTypeID'] == 4) and (row['UnitGroupID'] not in unitGroups):
            unitGroups.append(data.loc[index+1]['UnitGroupID'])
    return unitGroups

def preprocess(nData, playerBase):
    buildingsList = pd.read_csv(buildings, sep=";")
    attUnits = pd.read_csv(attackUnits, sep=";")
    scouts = findScoutUnit(nData)
    scoutsGroup =  [scouts[r] for r in scouts]
    attackGroup = getUnitsGroupIDs(nData, attUnits.values) #aiming at the enemy
    replayIDs = scouts.keys()
    #General attrib.
    data = pd.DataFrame()
    data['AC_FRAME'] = nData['AC_FRAME']
    data['Race'] = nData['Race']
    data['NumberOfBuildings'] = countNumberOf(nData, 'TargetID', buildingsList['TargetID'].values, 1, False) #starting from one - nexus
    data['NumberOfWorkers'] = countNumberOf(nData, 'TargetID', workersID, 4, True) #starting from 4 - first 4 workers
    #look at number of attack units which can be spawn/morphed from Zerg Buildings
    data['NumberOfAttackUnits'] = countNumberOf(nData, 'TargetID', attUnits.values, 0, False) #starting from 0
    data['NumberOfAttacks'] = countNumberOf(nData, 'OrderTypeID', [8, 14], 0, False)
    data['RatioAttackToNon'] = ratio(nData, 'OrderTypeID', [8, 14])

    #Expert attrib. - JUNO: CannonRush - Race: Protoss
    data['exp_feature1'] = expFeature(nData, 'TargetID', [166], [0], False) #Protoss_Forge: B
    data['exp_feature2'] = expFeature(nData, 'TargetID', [156], [0], False) #Protoss_Pylon: B
    data['exp_feature3'] = expFeature(nData, 'TargetID', [162], [0], False) #Protoss_PhotonCannon: B
    data['exp_feature4'] = scoutInTheGroup(nData, 'UnitGroupID', scoutsGroup, replayIDs) #scoutInTheGroup
    data['exp_feature5'] = isNearEnemy(nData, playerBase, True) #scout is near the enemy

    #Expert attrib. - ZZZKBot,Killall, cpac others: 4/5/9 Pool - Race: Zerg
    data['exp_feature6'] = expFeature(nData, 'TargetID', [142], [0], False) #Zerg_SpawningPool: B
    data['exp_feature7'] = expFeature(nData, 'TargetID', [149], [0], False) #Zerg_Extractor: B
    data['exp_feature8'] = expFeature(nData, 'TargetID', [37], [0], False) #Zerg_Zerglings: U

    #Expert attrib. - MyscBot, other protoss bots, 2/3 Gate various types - Race: Protoss
    data['exp_feature9'] = countNumberOf(nData, 'TargetID', [160], 0, False) #Protoss_Gateway: B
    data['exp_feature10'] = expFeature(nData, 'TargetID', [157], [0], False) #Protoss_Assimilator: B
    data['exp_feature11'] = expFeature(nData, 'TargetID', [164], [0], False) #Protoss_CybernaticsCore: B
    data['exp_feature12'] = expFeature(nData, 'TargetID', [65], [0], False) #Protoss_Zealot: U -> #add manual annotation
    data['exp_feature13'] = isNearEnemy(nData, playerBase, True) #attackunits is going to the enemybase

    data['Results1'] = 0
    data['Results2'] = 0
    data['Results3'] = 0
    data['Results4'] = 0

    return data
