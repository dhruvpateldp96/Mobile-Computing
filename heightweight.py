# /home/dhruv/Allprojects/MC

import numpy as np # linear algebra
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv)
import matplotlib.pyplot as plt
import os
from sklearn.metrics import r2_score
# import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression

df= pd.read_csv('/home/dhruv/Allprojects/MC/weight-height.csv')
X = df.iloc[:, 1:2].values
y = df.iloc[:, 2:3].values
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=31)
regressor = LinearRegression()
model_fit = regressor.fit(X_train, y_train)
y_predict = regressor.predict(X_test)
print(y_predict)
print(f"Model Accuracy is: {regressor.score(X_test, y_test)}")