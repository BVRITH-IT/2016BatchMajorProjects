import pandas as pd
import numpy as np
import keras
from keras.models import Sequential
from keras.layers import Dense, Dropout, Activation
from keras.layers.normalization import BatchNormalization
from sklearn.metrics import roc_curve
import callback

"""### Load Training Data"""

train_data = pd.read_csv("./train.csv")

train_data.head()

# Features
X = train_data.iloc[:,1:].as_matrix()
m,_ = X.shape
print("X: ", X.shape)

# Labels
Y = np.reshape(train_data["Choice"].as_matrix(), (-1, 1))
print("Y: ",Y.shape)

"""### Split Data into training and validation set
80% of the data will be the training set and rest is going to be our validation set
"""

train_n = int(.80 * m)   

train_x = X[:train_n, :]
train_y = Y[:train_n, :]

val_x = X[train_n:, :]
val_y = Y[train_n:, :]

print("Training set size: ", train_x.shape)
print("Validation set size: ", val_x.shape)

"""### Build the Model"""

def rec_model():
    model = Sequential()
    
    model.add(Dense(50, activation='relu', input_dim=22, kernel_initializer='uniform'))
    model.add(Dropout(0.5))
    model.add(Dense(10, kernel_initializer='uniform'))
    model.add(BatchNormalization())
    model.add(Dense(1, activation='sigmoid', kernel_initializer='uniform'))
    
    model.compile(loss='binary_crossentropy',
              optimizer='adam',
              metrics=['accuracy'])

    return model

roc_curve_area = callback.roc_curve_area()

model = rec_model()
model.summary()

"""### Start Training"""

model = rec_model()
model.fit(train_x, train_y,
          epochs=20,
          batch_size=64,
          validation_data=(val_x, val_y),
          callbacks=[roc_curve_area])

"""### Create Submission file"""

test_data = pd.read_csv("./test.csv")

test_data = test_data.as_matrix()

test_data.shape

y_pred = model.predict(test_data)

sample_submission = pd.read_csv('./sample_predictions.csv')
sample_submission.shape

sample_submission["Choice"] = y_pred

sample_submission.to_csv('./submission.csv', index=False)

