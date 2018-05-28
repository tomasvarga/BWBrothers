import sys
import os
import shutil
import time
import traceback
from flask import Flask, request, jsonify
import pandas as pd
import numpy as np
import json
from sklearn.externals import joblib
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
import SCUtilsOnline as sc

app = Flask(__name__)
ip = "192.168.1.18"

each_frame = 240

#Trainset
training_data = 'data/dataset-216.csv'
model_directory = "models"
#STRATEGY 1: CANNON RUSH, STRATEGY 2: ZERG POOL, STRATEGY 3: 2-gate, STRATEGY 4: 3-gate

model_files_name = ["models/cannon_rush.pkl", "models/zerg_pool.pkl", "models/two_gateways.pkl", "models/three_gateways.pkl"]
model_columns_files_name = ["models/cannon_rush_columns.pkl", "models/zerg_pool_columns.pkl", "models/two_gateways_columns.pkl", "models/three_gateways_columns.pkl"]
model_columns = [None, None, None, None]
clf = [None, None, None, None]

headers = {
    0: ["NumberOfBuildings", "NumberOfWorkers", "NumberOfAttackUnits", "NumberOfAttacks", "RatioAttackToNon", "exp_feature1", "exp_feature2", "exp_feature3", "exp_feature4", "exp_feature5", "Results1"],
    1: ["Race", "NumberOfBuildings", "NumberOfWorkers", "NumberOfAttackUnits", "NumberOfAttacks", "RatioAttackToNon", "exp_feature6", "exp_feature7","exp_feature8", "Results2"],
    2: ["NumberOfBuildings", "NumberOfWorkers", "NumberOfAttackUnits", "NumberOfAttacks", "RatioAttackToNon", "exp_feature9", "exp_feature12", "Results3"],
    3: ["NumberOfBuildings", "NumberOfWorkers", "NumberOfAttackUnits", "NumberOfAttacks", "RatioAttackToNon", "exp_feature9", "exp_feature10", "exp_feature11", "exp_feature12", "Results4"]
    }

@app.route('/predict', methods=['POST'])
def predict():
    global clf
    if clf is not None:
        try:
            json_ = request.json
            #aggregate
            replay = pd.DataFrame(json_['actions'])
            playerBase = json_['base']
            js = predictModels(replay, playerBase)
            return js
        except Exception, e:
            return jsonify({'error': str(e), 'trace': traceback.format_exc()})
    else:
        print 'train first'
        return 'no model here'

@app.route('/train', methods=['GET'])
def train():
    global model_columns
    global clf
    for i in xrange(0,len(model_files_name)):
        clf[i], model_columns[i] = trainModel(i, clf[i], model_columns[i],model_columns_files_name[i], headers.get(i))
        joblib.dump(clf[i], model_files_name[i])
    return "All models were trained."

@app.route('/wipe', methods=['GET'])
def wipe():
    try:
        shutil.rmtree('models')
        os.makedirs(model_directory)
        return 'Models removed. Please train them again by visting the web page at: http://' + ip + ":" + str(port) + "/train"

    except Exception, e:
        print str(e)
        return 'Could not remove and recreate the model directory'


@app.route('/test')
def test():
    json_str = "test.json"
    json_ = json.load(open(json_str))
    replay = pd.DataFrame(json_['actions'])
    print replay
    playerBase = json_['base']
    js = predictModels(replay, playerBase)
    return (js, "Everything seems to be working!")

# MARK: NON-WEB FUNCTION although used in api
def predictModels(replay, playerBase):
    global clf, headers
    nData = sc.preprocess(replay, playerBase)
    strategies = { "cannon_rush": 0, "zergling_rush": 0, "two_gateways":0, "three_gateways": 0  }
    for index in xrange(0,len(model_files_name)):
        c =  clf[index]
        header = headers.get(index)
        min = 0
        data = nData.groupby([pd.cut(nData["AC_FRAME"], np.arange(0, nData['AC_FRAME'].max(), each_frame))], as_index = False)[header[0:-1]].agg(lambda x: x.iloc[0])
        data = data.dropna()
        if len(data) != 0:
            prediction = list(c.predict(data))
            last_index = len(prediction) - 1
            if index == 0:
                strategies['cannon_rush'] = prediction[last_index]
            elif index == 1:
                strategies['zergling_rush'] = prediction[last_index]
            elif index == 2:
                strategies['two_gateways'] = prediction[last_index]
            elif index == 3:
                strategies['three_gateways'] = prediction[last_index]
    for k, v in strategies.iteritems():
        print str(k) + ": "+ str(v)
    return jsonify(strategies)

def trainModel(index, clf, model_columns,model_columns_file_name,  headers):
    # can do the training separately and just update the pickles
    if index == 0:
        df = pd.read_csv("data/dataset-108.csv", sep=';', encoding='utf-8')
    else:
        df = pd.read_csv(training_data, sep=';', encoding='utf-8')
    np.random.seed(0)
    train_x, test_x, train_y, test_y = split_dataset(df, 0.7, headers[0:-1], headers[-1])
    #capture a list of columns that will be used for prediction
    model_columns = list(headers[0:-1])
    joblib.dump(model_columns, model_columns_file_name)
    trained_model = gradient_boosting(index, train_x, train_y)
    clf = trained_model
    return clf, model_columns

def split_dataset(dataset, train_percentage, feature_headers, target_header):
    # Split dataset into train and test dataset
    train_x, test_x, train_y, test_y = train_test_split(dataset[feature_headers], dataset[target_header], train_size=train_percentage)
    return train_x, test_x, train_y, test_y

#MARK: GRADIENT BOOSTING
def gradient_boosting(index, features, target):
    from sklearn import ensemble
    params = {}
    if index == 0:
        params = {'max_features': 10, 'learning_rate': 0.05, 'max_depth': 7, 'min_samples_leaf': 10}
    elif index == 1:
        params = {'max_features': 9, 'learning_rate': 0.05, 'max_depth': 9, 'min_samples_leaf': 3}
    elif index == 2:
        params = {'max_features': 7, 'learning_rate': 0.1, 'max_depth': 9, 'min_samples_leaf': 20}
    elif index == 3:
        params = {'max_features': 9, 'learning_rate': 0.1, 'max_depth': 7, 'min_samples_leaf': 10}
    clf = ensemble.GradientBoostingClassifier(**params)
    clf.fit(features, target)
    del params
    print "Training GBM model:" + str(index)
    return clf

if __name__ == '__main__':
    try:
        port = int(sys.argv[1])
    except Exception, e:
        port = 8080
    try:
        for i in xrange(0,len(model_files_name)):
            clf[i] = joblib.load(model_files_name[i])
            print 'model loaded:', model_files_name[i]
            model_columns[i] = joblib.load(model_columns_files_name[i])
            print 'model columns loaded'
    except Exception, e:
        print 'Error: No models are found. Please train models at first.'
        print 'To train models go to: http://' + ip + ":" + str(port) + "/train"
        #print str(e)
        clf = [None, None, None, None]
    app.run(host=ip, port=port)
