from flask import Flask, jsonify
from flask import request
import json
import numpy as np
import pandas as pd
import pickle
import os
import csv
import stat
from sklearn import preprocessing
import statistics 
from statistics import mode 


app = Flask(__name__)

#path_to_videos = "C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/Done!!/"
# path_to_csv = "C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/key_points.csv"

# os.chmod("C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/key_points.csv", stat.S_IRUSR | stat.S_IRGRP | stat.S_IROTH)
# os.chmod("C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/key_points.csv", stat.S_IWUSR | stat.S_IWGRP | stat.S_IWOTH)
def center_by_nose(df):
    dfX = df.filter(regex='_x') #all X values
    dfY = df.filter(regex='_y') #all Y values
    dfX = dfX.sub(dfX["nose_x"], axis=0)
    dfY = dfY.sub(dfY["nose_y"], axis=0)
    return dfX, dfY    

def normalize_dataframe(df):
    x = df.values #returns a numpy array
    min_max_scaler = preprocessing.MinMaxScaler()
    x_scaled = min_max_scaler.fit_transform(x)
    df = pd.DataFrame(x_scaled)
    return df

@app.route('/postjson', methods = ['POST'])
def postJsonHandler():
    print(request.is_json)
    content = request.get_json()
    print(content)
    result1 = ""
    result2 = ""
    result3 = ""
    result4 = ""
    dict1 = content
    #print(dict1)

    columns = ['score_overall', 'nose_score', 'nose_x', 'nose_y', 'leftEye_score', 'leftEye_x', 'leftEye_y',
               'rightEye_score', 'rightEye_x', 'rightEye_y', 'leftEar_score', 'leftEar_x', 'leftEar_y',
               'rightEar_score', 'rightEar_x', 'rightEar_y', 'leftShoulder_score', 'leftShoulder_x', 'leftShoulder_y',
               'rightShoulder_score', 'rightShoulder_x', 'rightShoulder_y', 'leftElbow_score', 'leftElbow_x',
               'leftElbow_y', 'rightElbow_score', 'rightElbow_x', 'rightElbow_y', 'leftWrist_score', 'leftWrist_x',
               'leftWrist_y', 'rightWrist_score', 'rightWrist_x', 'rightWrist_y', 'leftHip_score', 'leftHip_x',
               'leftHip_y', 'rightHip_score', 'rightHip_x', 'rightHip_y', 'leftKnee_score', 'leftKnee_x', 'leftKnee_y',
               'rightKnee_score', 'rightKnee_x', 'rightKnee_y', 'leftAnkle_score', 'leftAnkle_x', 'leftAnkle_y',
               'rightAnkle_score', 'rightAnkle_x', 'rightAnkle_y']

    csv_data = np.zeros((len(dict1), len(columns)))
    for i in range(csv_data.shape[0]):
        one = []
        one.append(dict1[i]['score'])
        for obj in dict1[i]['keypoints']:
            one.append(obj['score'])
            one.append(obj['position']['x'])
            one.append(obj['position']['y'])
        csv_data[i] = np.array(one)
    df = pd.DataFrame(csv_data, columns=columns)
    #.to_csv('key_points.csv', index_label='Frames#')
    df.drop(columns=["score_overall", "nose_score", "leftEye_score", "rightEye_score", "leftEar_score","rightEar_score", "leftShoulder_score", "rightShoulder_score", "leftElbow_score", "rightElbow_score", "leftWrist_score","rightWrist_score","leftHip_score","rightHip_score",'leftKnee_score', 'leftKnee_x', 'leftKnee_y','rightKnee_score', 'rightKnee_x', 'rightKnee_y', 'leftAnkle_score', 'leftAnkle_x', 'leftAnkle_y','rightAnkle_score', 'rightAnkle_x', 'rightAnkle_y', 'leftEye_x', 'leftEye_y', 'rightEye_x', 'rightEye_y'], inplace=True)
    # df.sub(df["nose"], axis=0)
    dfX, dfY = center_by_nose(df)
    # dfX = normalize_dataframe(dfX)
    # dfY = normalize_dataframe(dfY)
    dfX= dfX.div(dfX.sum(axis=1), axis=0)
    dfY = dfY.div(dfY.sum(axis=1), axis=0)
    # Create x, where x the 'scores' column's values as floats
    dfX=normalize_dataframe(dfX)
    dfY=normalize_dataframe(dfY)

    df = pd.concat([dfX,dfY], axis=1)
    # df['label'] = label
    # return df

    # def normalise(file_path):
    #     data1 = []
    #     origin_x = 0
    #     origin_y = 0
    #     norm_x = 0
    #     norm_y = 0

    #     with open(file_path, 'r') as file:
    #         csvreader = csv.reader(file)
    #         cell = [row for row in csvreader]
    #         origin_x = float(cell[1][1])
    #         origin_y = float(cell[1][2])
    #         norm_x = float(cell[1][23]) - origin_x
    #         norm_y = float(cell[1][24]) - origin_y


    #     with open(file_path, 'r') as file:
    #         csvreader = csv.reader(file)
    #         type(csvreader)
    #         next(csvreader)
    #         count = 0
    #         for row in csvreader:
    #             count += 1
    #             #if row == 'null' && count < 125:
    #                 #row =
    #             for i in range(1, 27):
    #                 if i % 2 == 1:
    #                     data_pt = (float(row[i]) - origin_x) / norm_y
    #                 else:
    #                     data_pt = (float(row[i]) - origin_y) / norm_y
    #                 data1.append(str(data_pt))
    #             if count == 125:
    #                 break
    #             #elif row.isEmpty():
    #              #   count=0
    #             #elif count > 125:
    #     return data1


    # X_test = []
    # temp = normalise(path_to_csv)
    # X_test.append(temp)

    X_test = df

    loaded_model = pickle.load(open('forest.sav', 'rb'))
    result1 = loaded_model.predict(X_test)
    res1 = mode(result1)
    print(res1)

    loaded_model = pickle.load(open('gnb.sav', 'rb'))
    result2 = loaded_model.predict(X_test)
    res2 = mode(result2)
    print(res2)

    loaded_model = pickle.load(open('mlp.sav', 'rb'))
    result3 = loaded_model.predict(X_test)
    res3 = mode(result3)
    print(res3)

    loaded_model = pickle.load(open('knn.sav', 'rb'))
    result4 = loaded_model.predict(X_test)
    res4 = mode(result4)
    print(res4)
    #print(result4)

    data = {"1_Random_Forest": str(res1), "3_MLP": str(res3),
            "3_Gaussian_Naive_Bayes": str(res2), "4_KNN": str(res4)}

    return jsonify(data)



app.run(host='0.0.0.0', port=8073)