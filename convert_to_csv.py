import json
import numpy as np
import pandas as pd
import os
from sklearn import preprocessing


path_to_videos = "/home/dhruv/Allprojects/MC/KeyPoints/"

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


def convert_to_csv(path_to_video):
    columns = ['score_overall', 'nose_score', 'nose_x', 'nose_y', 'leftEye_score', 'leftEye_x', 'leftEye_y',
               'rightEye_score', 'rightEye_x', 'rightEye_y', 'leftEar_score', 'leftEar_x', 'leftEar_y',
               'rightEar_score', 'rightEar_x', 'rightEar_y', 'leftShoulder_score', 'leftShoulder_x', 'leftShoulder_y',
               'rightShoulder_score', 'rightShoulder_x', 'rightShoulder_y', 'leftElbow_score', 'leftElbow_x',
               'leftElbow_y', 'rightElbow_score', 'rightElbow_x', 'rightElbow_y', 'leftWrist_score', 'leftWrist_x',
               'leftWrist_y', 'rightWrist_score', 'rightWrist_x', 'rightWrist_y', 'leftHip_score', 'leftHip_x',
               'leftHip_y', 'rightHip_score', 'rightHip_x', 'rightHip_y', 'leftKnee_score', 'leftKnee_x', 'leftKnee_y',
               'rightKnee_score', 'rightKnee_x', 'rightKnee_y', 'leftAnkle_score', 'leftAnkle_x', 'leftAnkle_y',
               'rightAnkle_score', 'rightAnkle_x', 'rightAnkle_y']
    data = json.loads(open(path_to_video + 'key_points.json', 'r').read())
    print("file opened")
    csv_data = np.zeros((len(data), len(columns)))
    for i in range(csv_data.shape[0]):
        one = []
        one.append(data[i]['score'])
        for obj in data[i]['keypoints']:
            one.append(obj['score'])
            one.append(obj['position']['x'])
            one.append(obj['position']['y'])
        csv_data[i] = np.array(one)
    df = pd.DataFrame(csv_data, columns=columns)
    df.drop(columns=["score_overall", "nose_score", "leftEye_score", "rightEye_score", "leftEar_score","rightEar_score", "leftShoulder_score", "rightShoulder_score", "leftElbow_score", "rightElbow_score", "leftWrist_score"
      ,"rightWrist_score","leftHip_score","rightHip_score",'leftKnee_score', 'leftKnee_x', 'leftKnee_y',
               'rightKnee_score', 'rightKnee_x', 'rightKnee_y', 'leftAnkle_score', 'leftAnkle_x', 'leftAnkle_y',
               'rightAnkle_score', 'rightAnkle_x', 'rightAnkle_y'], inplace=True)
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

    df = df.to_numpy()
    df.append(1)
    print(df)
    # print(dfY)
    # print(dfX)
    # print(dfX.max())


    # .to_csv(path_to_video + 'key_points.csv', index_label='Frames#')



if __name__ == '__main__':

    files = os.listdir(path_to_videos)
    print(files)
    for file in files:
      print("1")
      if os.path.isdir(path_to_videos + file + "/"):
        print("2")
        new_path = path_to_videos + os.path.splitext(file)[0] + "/"
        convert_to_csv(new_path)
        break
