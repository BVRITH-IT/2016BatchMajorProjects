import os
import cv2
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from PIL import Image
import tensorflow as tf
from sklearn.model_selection import train_test_split

data=[]
labels=[]
height = 30
width = 30
channels = 3
num_classes = 43
n_inputs = height * width*channels
#Retrieving the images and their labels
for i in range(num_classes) :
    path = "C:\\Users\\lenovo\\Downloads\\TrafficSignsRecognition\\Train\\{0}/".format(i)
    print(path)
    Class=os.listdir(path)
    for a in Class:
        try:
            image=cv2.imread(path+a)
            image_from_array = Image.fromarray(image, 'RGB')
            size_image = image_from_array.resize((height, width))
            data.append(np.array(size_image))
            labels.append(i)
        except AttributeError:
            print(" ")
#Converting lists into numpy arrays             
x_train=np.array(data)
x_train= x_train/255.0
#Applying Transformations
from keras.preprocessing.image import ImageDataGenerator
from keras.utils import to_categorical

y_train=np.array(labels)
y_train = to_categorical(y_train, num_classes)
X_train,X_valid,Y_train,Y_valid = train_test_split(x_train,y_train,test_size = 0.3,random_state=0)
datagen = ImageDataGenerator(featurewise_center=False,
                             featurewise_std_normalization=False,
                             width_shift_range=0.1,
                             height_shift_range=0.1,
                             zoom_range=0.2,
                             shear_range=0.1,
                             rotation_range=10.)

datagen.fit(X_train)

y_train=np.array(labels)
y_train = to_categorical(y_train, num_classes)
#Splitting training and testing dataset
X_train,X_valid,Y_train,Y_valid = train_test_split(x_train,y_train,test_size = 0.3,random_state=0)
print("Train :", X_train.shape)
print("Valid :", X_valid.shape)
#Showing training images
import random
def show_images(images, labels, amount):
    for i in range(amount):
        index = int(random.random() * len(images))
        plt.axis('off')
        plt.imshow(images[index])
        plt.show()       
        print("Size of this image is " + str(images[index].shape))
        print("Class of the image is " + str(labels[index]))

print("Train images")
show_images(X_train, Y_train, 3)
#Building the model
import keras
from keras.utils import to_categorical
from keras.models import Sequential
from keras.layers import Dense, Dropout, Flatten
from keras.layers import Conv2D, MaxPooling2D, BatchNormalization

def cnn_model():
    model = Sequential()
    model.add(Conv2D(32, kernel_size=(3, 3), activation='relu', input_shape=X_train.shape[1:])) 
    model.add(BatchNormalization()) 
    model.add(Dropout(0.5)) 
    
    model.add(Conv2D(32, kernel_size=(3, 3), activation='relu')) 
    model.add(BatchNormalization()) 
    model.add(Dropout(0.5)) 
    
    model.add(Conv2D(64, kernel_size=(3, 3), activation='relu')) 
    model.add(BatchNormalization()) 
    model.add(Dropout(0.5)) 
    
    model.add(Conv2D(128, kernel_size=(5, 5), activation='relu')) 
    model.add(BatchNormalization()) 
    model.add(MaxPooling2D(pool_size=(2, 2))) 
    
    model.add(Flatten()) 
    
    model.add(Dense(128, activation='relu')) 
    model.add(Dropout(0.5)) 
    
    model.add(Dense(43, activation='softmax'))
    return model

model = cnn_model()
model.compile(loss='categorical_crossentropy',
              optimizer='adam',
              metrics=['accuracy'])

model.summary()

#Compilation of the model
model = cnn_model()
model.compile(loss='categorical_crossentropy',
              optimizer='adam',
              metrics=['accuracy'])

epochs = 20
history = model.fit(X_train, Y_train, validation_data=(X_valid, Y_valid), batch_size=32, epochs=epochs,verbose=1)
model.save('Traffic_Recognition.h5')

#Plotting graphs for accuracy
plt.figure(0)
plt.plot(history.history['acc'], label='training accuracy')
plt.plot(history.history['val_acc'], label='val accuracy')
plt.title('Accuracy')
plt.xlabel('epochs')
plt.ylabel('accuracy')
plt.legend()

plt.figure(1)
plt.plot(history.history['loss'], label='training loss')
plt.plot(history.history['val_loss'], label='val loss')
plt.title('Loss')
plt.xlabel('epochs')
plt.ylabel('loss')
plt.legend()
#Testing accuracy on test dataset
y_test=pd.read_csv("C:\\Users\\lenovo\\Downloads\\TrafficSignsRecognition\\Test.csv")
labels=y_test['Path'].values
y_test=y_test['ClassId'].values

data=[]

for f in labels:
    image=cv2.imread("C:\\Users\\lenovo\\Downloads\\TrafficSignsRecognition\\Test/"+f.replace('Test/', ''))
    image_from_array = Image.fromarray(image, 'RGB')
    size_image = image_from_array.resize((height, width))
    data.append(np.array(size_image))

X_test=np.array(data)
X_test = X_test.astype('float32')/255  
pred = model.predict_classes(X_test)
#Accuracy with the test data
from sklearn.metrics import accuracy_score
accuracy_score(y_test, pred)









