import json
import numpy as np
import pandas as pd
import os

path_to_videos = "F:/ASU couse material/Sem 2 Fall 2019/CSE 535 Mobile Computing/Project/Assignment 2/G14json/"

def convert_to_csv(path_to_video):
    '''
    columns = ['score_overall', 'nose_score', 'nose_x', 'nose_y', 'leftEye_score', 'leftEye_x', 'leftEye_y',
               'rightEye_score', 'rightEye_x', 'rightEye_y', 'leftEar_score', 'leftEar_x', 'leftEar_y',
               'rightEar_score', 'rightEar_x', 'rightEar_y', 'leftShoulder_score', 'leftShoulder_x', 'leftShoulder_y',
               'rightShoulder_score', 'rightShoulder_x', 'rightShoulder_y', 'leftElbow_score', 'leftElbow_x',
               'leftElbow_y', 'rightElbow_score', 'rightElbow_x', 'rightElbow_y', 'leftWrist_score', 'leftWrist_x',
               'leftWrist_y', 'rightWrist_score', 'rightWrist_x', 'rightWrist_y', 'leftHip_score', 'leftHip_x',
               'leftHip_y', 'rightHip_score', 'rightHip_x', 'rightHip_y', 'leftKnee_score', 'leftKnee_x', 'leftKnee_y',
               'rightKnee_score', 'rightKnee_x', 'rightKnee_y', 'leftAnkle_score', 'leftAnkle_x', 'leftAnkle_y',
               'rightAnkle_score', 'rightAnkle_x', 'rightAnkle_y']
    '''
    columns = ['nose_x', 'nose_y', 'leftEye_x', 'leftEye_y',
               'rightEye_x', 'rightEye_y', 'leftEar_x', 'leftEar_y',
               'rightEar_x', 'rightEar_y', 'leftShoulder_x', 'leftShoulder_y',
               'rightShoulder_x', 'rightShoulder_y', 'leftElbow_x',
               'leftElbow_y', 'rightElbow_x', 'rightElbow_y', 'leftWrist_x',
               'leftWrist_y', 'rightWrist_x', 'rightWrist_y','leftHip_x',
               'leftHip_y', 'rightHip_x', 'rightHip_y', 'leftKnee_x', 'leftKnee_y',
               'rightKnee_x', 'rightKnee_y', 'leftAnkle_x', 'leftAnkle_y',
               'rightAnkle_x', 'rightAnkle_y']
    #data = json.loads(open(path_to_video + 'Book_1_Choy.json', 'r').read())
    data = json.loads(open(path_to_video, 'r').read())
    csv_data = np.zeros((len(data), len(columns)))
    for i in range(csv_data.shape[0]):
        one = []
        #one.append(data[i]['score'])
        for obj in data[i]['keypoints']:
            #one.append(obj['score'])
            one.append(obj['position']['x'])
            one.append(obj['position']['y'])
        csv_data[i] = np.array(one)
    pd.DataFrame(csv_data, columns=columns).to_csv(path_to_video + 'key_points.csv', index_label='Frames#')


if __name__ == '__main__':

    files = os.listdir(path_to_videos)
    '''
    for file in files:
        if not os.path.isdir(path_to_videos + file + "/"):
            new_path = path_to_videos + os.path.splitext(file)[0] + "/"
            convert_to_csv(new_path)
    '''
    for file in files:
            new_path = path_to_videos + file
            convert_to_csv(new_path)
