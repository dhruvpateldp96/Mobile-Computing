import csv
import numpy as np
import pandas as pd
import os
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.svm import SVC
from sklearn.neighbors import KNeighborsClassifier

path_to_csv = "F:/ASU couse material/Sem 2 Fall 2019/CSE 535 Mobile Computing/Project/Assignment 2/G14json/"

def normalise(file_path):
    '''
    with open(file_path, 'r') as f:
        wines = list(csv.reader(f, delimiter=","))
        wines = np.array(wines[1:], dtype=np.float)
    print(wines.shape)
    '''
    data = []
    origin_x = 0
    origin_y = 0
    norm_x = 0
    norm_y = 0
    with open(file_path, 'r') as csvfile:
        csvreader = csv.reader(csvfile)
        cell = [row for row in csvreader]
        origin_x = float(cell[1][1])
        origin_y = float(cell[1][2])
        norm_x = float(cell[1][23]) - origin_x
        norm_y = float(cell[1][24]) - origin_y
        #print(f' norm_x = {norm_x} and norm_y = {norm_y}')

    with open(file_path, 'r') as csvfile:
        csvreader = csv.reader(csvfile)
        next(csvreader)
        count = 0

        for row in csvreader:
            count += 1
            for i in range(1,27):
                #data_pt = float(row[i])
                if i%2 == 1:
                    data_pt = (float(row[i]) - origin_x) / norm_y
                else:
                    data_pt = (float(row[i]) - origin_y) / norm_y
                data.append(str(data_pt))
            if count==125:
                break
    #print(np.asarray(data).shape)
    return data
    '''
    data_final=np.asarray(data)
    return data_final
    '''


if __name__ == '__main__':
    X_train = []
    Y_train = []
    files = os.listdir(path_to_csv)
    for file in files:
        if file.endswith(".csv"):
            if file.startswith("Book"):
                Y_train.append("Book")
            elif file.startswith("Car"):
                Y_train.append("Car")
            elif file.startswith("Gift"):
                Y_train.append("Gift")
            elif file.startswith("Movie"):
                Y_train.append("Movie")
            elif file.startswith("Sell"):
                Y_train.append("Sell")
            elif file.startswith("Total"):
                Y_train.append("Total")
            else:
                print("Label not found!!")
            #print(file)
            new_path = path_to_csv + file
            temp = normalise(new_path)
            #print(temp)
            #print(type(temp))
            X_train.append(temp)
    '''
        for file in files:
            if not os.path.isdir(path_to_videos + file + "/"):
                new_path = path_to_videos + os.path.splitext(file)[0] + "/"
                convert_to_csv(new_path)
    
    X=np.asarray(X_train)
    print("printing one row")
    print(X[0])
    print(type(X))
    df = pd.DataFrame(X, columns = ['NK'])
    df = df.NK.list.split(" " , expand=True,)
    print(df)
    '''
    X = pd.DataFrame(X_train)
    #print(X)
    Y = pd.DataFrame(Y_train, columns = ['target'])
    #print(Y)
    X['target']=Y.target
    #print(X)
    train_X, test_X, train_Y, test_Y = train_test_split(X.drop(['target'], axis='columns'), Y.target, test_size=0.15)
    model = RandomForestClassifier(n_estimators=40)
    model.fit(train_X, train_Y)
    print(f'The training accuracy for Random Forest :  {model.score(test_X, test_Y)}')
    gnb = GaussianNB()
    gnb.fit(train_X, train_Y)
    print(f'The training accuracy for Naive Bayes:  {gnb.score(test_X, test_Y)}')
    svm_model_linear = SVC(kernel='linear', C=1).fit(train_X, train_Y)
    print(f'The training accuracy for SVM:  {svm_model_linear.score(test_X, test_Y)}')
    knn = KNeighborsClassifier(n_neighbors=7).fit(train_X, train_Y)
    print(f'The training accuracy for kNN:  {knn.score(test_X, test_Y)}')