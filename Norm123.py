from flask import Flask, jsonify
from flask import request
import json
import numpy as np
import pandas as pd
import pickle
import os
import csv
import stat

app = Flask(__name__)

#path_to_videos = "C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/Done!!/"
path_to_csv = "C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/key_points.csv"

os.chmod("C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/key_points.csv", stat.S_IRUSR | stat.S_IRGRP | stat.S_IROTH)
os.chmod("C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/key_points.csv", stat.S_IWUSR | stat.S_IWGRP | stat.S_IWOTH)
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

    columns = ['nose_x', 'nose_y', 'leftEye_x', 'leftEye_y',
               'rightEye_x', 'rightEye_y', 'leftEar_x', 'leftEar_y',
               'rightEar_x', 'rightEar_y', 'leftShoulder_x', 'leftShoulder_y',
               'rightShoulder_x', 'rightShoulder_y', 'leftElbow_x',
               'leftElbow_y', 'rightElbow_x', 'rightElbow_y', 'leftWrist_x',
               'leftWrist_y', 'rightWrist_x', 'rightWrist_y', 'leftHip_x',
               'leftHip_y', 'rightHip_x', 'rightHip_y', 'leftKnee_x', 'leftKnee_y',
               'rightKnee_x', 'rightKnee_y', 'leftAnkle_x', 'leftAnkle_y',
               'rightAnkle_x', 'rightAnkle_y']

    csv_data = np.zeros((len(dict1), len(columns)))
    for i in range(csv_data.shape[0]):
        one = []
        for obj in dict1[i]['keypoints']:
            one.append(obj['position']['x'])
            one.append(obj['position']['y'])
        csv_data[i] = np.array(one)
    pd.DataFrame(csv_data, columns=columns).to_csv('key_points.csv', index_label='Frames#')

    def normalise(file_path):
        data1 = []
        origin_x = 0
        origin_y = 0
        norm_x = 0
        norm_y = 0

        with open(file_path, 'r') as file:
            csvreader = csv.reader(file)
            cell = [row for row in csvreader]
            origin_x = float(cell[1][1])
            origin_y = float(cell[1][2])
            norm_x = float(cell[1][23]) - origin_x
            norm_y = float(cell[1][24]) - origin_y


        with open(file_path, 'r') as file:
            csvreader = csv.reader(file)
            type(csvreader)
            next(csvreader)
            count = 0
            for row in csvreader:
                count += 1
                #if row == 'null' && count < 125:
                    #row =
                for i in range(1, 27):
                    if i % 2 == 1:
                        data_pt = (float(row[i]) - origin_x) / norm_y
                    else:
                        data_pt = (float(row[i]) - origin_y) / norm_y
                    data1.append(str(data_pt))
                if count == 125:
                    break
                #elif row.isEmpty():
                 #   count=0
                #elif count > 125:
        return data1

    X_test = []
    temp = normalise(path_to_csv)
    X_test.append(temp)


    loaded_model = pickle.load(open('C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/Random_Forest_Model.sav', 'rb'))
    result1 = loaded_model.predict(X_test)
    #res1 = str(result1)
    print(result1)

    loaded_model = pickle.load(open('C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/Support_Vector_Model.sav', 'rb'))
    result2 = loaded_model.predict(X_test)
    #res2 = str(result2)
    print(result2)

    #loaded_model = pickle.load(open('C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/Gaussian_Naive_Bayes_Model.sav', 'rb'))
    #result3 = loaded_model.predict(X_test)
    #res3 = str(result3)
    #print(result3)

    loaded_model = pickle.load(open('C:/Users/Prem/Desktop/ASU_Sub/CSE-535/Assignment_2/posenet_nodejs_setup-master/KNN_Model.sav', 'rb'))
    result4 = loaded_model.predict(X_test)
    res4 = str(result4)
    #print(result4)

    data = {"1_Random_Forest": str(result1), "2_Support_Vector_Machine": str(result2),
            "3_Gaussian_Naive_Bayes": str(result3), "4_KNN": str(result4)}

    return jsonify(data)



app.run(host='0.0.0.0', port=8073)
