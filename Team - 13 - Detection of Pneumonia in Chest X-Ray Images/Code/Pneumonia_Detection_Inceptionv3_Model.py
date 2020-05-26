import numpy as np
import matplotlib.pyplot as plt
import os
import pandas as pd
import math

from keras.applications import InceptionV3
from keras.models import load_model

from keras.applications.inception_v3 import preprocess_input as incep_preprocess_input

from keras.preprocessing.image import ImageDataGenerator
from keras.models import Model

from keras.callbacks import ModelCheckpoint
from keras.layers import Flatten, Dense, BatchNormalization, Dropout

from sklearn.metrics import confusion_matrix
from mlxtend.plotting import plot_confusion_matrix

import tensorflow as tf

from google.colab import drive
drive.mount('/content/drive')

input_path = '/content/drive/My Drive/chest_xray'

fig, ax = plt.subplots(2, 3, figsize=(15, 7))
ax = ax.ravel()
plt.tight_layout()

for i, _set in enumerate(['/train', '/val', '/test']):
    set_path = input_path+_set
    ax[i].imshow(plt.imread(set_path+'/NORMAL/'+os.listdir(set_path+'/NORMAL')[0]), cmap='gray')
    ax[i].set_title('Set: {}, Condition: Normal'.format(_set))
    ax[i+3].imshow(plt.imread(set_path+'/PNEUMONIA/'+os.listdir(set_path+'/PNEUMONIA')[0]), cmap='gray')
    ax[i+3].set_title('Set: {}, Condition: Pneumonia'.format(_set))

# Distribution of dataset
for _set in ['/train', '/val', '/test']:
    n_normal = len(os.listdir(input_path + _set + '/NORMAL'))
    n_infect = len(os.listdir(input_path + _set + '/PNEUMONIA'))
    print('Set: {}, normal images: {}, pneumonia images: {}'.format(_set, n_normal, n_infect))

input_path = '/content/drive/My Drive/chest_xray/'

def process_data(img_dims, batch_size):
    # Data generation objects
    train_datagen = ImageDataGenerator(rescale=1./299, zoom_range=0.3, vertical_flip=True)
    test_val_datagen = ImageDataGenerator(rescale=1./299)
    
    # This is fed to the network in specified batch sizes and image dimensions
    train_gen = train_datagen.flow_from_directory(
    directory=input_path+'train', 
    target_size=(img_dims, img_dims), 
    batch_size=batch_size, 
    class_mode='binary', 
    shuffle=True)

    test_gen = test_val_datagen.flow_from_directory(
    directory=input_path+'test', 
    target_size=(img_dims, img_dims), 
    batch_size=batch_size, 
    class_mode='binary', 
    shuffle=True)

    test_data = []
    test_labels = []

    for cond in ['/NORMAL/', '/PNEUMONIA/']:
        for img in (os.listdir(input_path + 'test' + cond)):
            img = plt.imread(input_path+'test'+cond+img)
            img = cv2.resize(img, (img_dims, img_dims))
            img = np.dstack([img, img, img])
            img = img.astype('float32') / 299
            if cond=='/NORMAL/':
                label = 0
            elif cond=='/PNEUMONIA/':
                label = 1
            test_data.append(img)
            test_labels.append(label)
        
    test_data = np.array(test_data)
    test_labels = np.array(test_labels)
    
    return train_gen, test_gen, test_data, test_labels

import cv2
# Hyperparameters
img_dims = 299
epochs = 30
batch_size = 64

# Getting the data
train_gen, test_gen, test_data, test_labels = process_data(img_dims, batch_size)

import ssl
ssl._create_default_https_context = ssl._create_unverified_context

batch_size = 64
target_size = (299, 299)

print("Using Inception v3")
base_model = InceptionV3(weights='imagenet', input_shape=(299, 299, 3), include_top=False)

x = base_model.output
x = Flatten()(x)
x = Dense(64, activation='relu')(x)
x = Dropout(0.33)(x)
x = BatchNormalization()(x)
output = Dense(1, activation='sigmoid')(x)

model = Model(inputs=base_model.input, outputs=output)
model.summary()

from keras.callbacks import ModelCheckpoint, ReduceLROnPlateau, EarlyStopping
# Creating model and compiling
model = model
model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])

# Callbacks
checkpoint = ModelCheckpoint(filepath='model-2/weights.epoch_{epoch:02d}.hdf5', save_best_only=True, save_weights_only=True)
lr_reduce = ReduceLROnPlateau(monitor='val_loss', factor=0.3, patience=2, verbose=2, mode='max')
early_stop = EarlyStopping(monitor='val_loss', min_delta=0.1, patience=1, mode='min')

for layer in base_model.layers:
    layer.trainable = False

model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

train_datagen = ImageDataGenerator(preprocessing_function=incep_preprocess_input,
                                       shear_range=0.2, zoom_range=0.2,
                                       horizontal_flip=True, fill_mode='nearest')

train_generator = train_datagen.flow_from_directory('/content/drive/My Drive/chest_xray/train',
                                                    target_size=target_size, color_mode='rgb',
                                                    batch_size=batch_size, class_mode='binary',
                                                    shuffle=True, seed=42)

val_datagen = ImageDataGenerator(preprocessing_function=incep_preprocess_input)

val_generator = val_datagen.flow_from_directory('/content/drive/My Drive/chest_xray/val',
                                                target_size=target_size, color_mode="rgb",
                                                batch_size=batch_size, shuffle=False, class_mode="binary")

test_datagen = ImageDataGenerator(preprocessing_function=incep_preprocess_input)

test_generator = test_datagen.flow_from_directory('/content/drive/My Drive/chest_xray/test',
                                                target_size=target_size, color_mode="rgb",
                                                batch_size=batch_size, shuffle=False, class_mode="binary")
 

step_size_train = train_generator.n // train_generator.batch_size
step_size_valid = val_generator.n // val_generator.batch_size
step_size_test = test_generator.n // test_generator.batch_size

from keras.callbacks import ModelCheckpoint, ReduceLROnPlateau, EarlyStopping
# Creating model and compiling
model = model
model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])

# Callbacks
checkpoint = ModelCheckpoint(filepath='best_weights.hdf5', save_best_only=True, save_weights_only=False)
lr_reduce = ReduceLROnPlateau(monitor='val_loss', factor=0.2, patience=2, verbose=2, mode='max')
early_stop = EarlyStopping(monitor='val_loss', min_delta=0.1, patience=1, mode='min')

history = model.fit_generator(generator=train_generator,
                    steps_per_epoch=step_size_train,
                    validation_data=val_generator,
                    validation_steps=step_size_valid,
                    callbacks=[checkpoint],
                    epochs=30, verbose=1)

model = load_model("/content/best_weights.hdf5")

model.save("inceptionv3.h5")

def plot_learning_curves(history):
    plt.figure(figsize=(12,4))
    
    plt.subplot(1,2,1)
    plt.plot(history.history['loss'])
    plt.plot(history.history['val_loss'])
    plt.title('model loss')
    plt.ylabel('loss')
    plt.xlabel('epoch')
    plt.legend(['train', 'val'], loc='upper left')
    
    plt.subplot(1,2,2)
    plt.plot(history.history['acc'])
    plt.plot(history.history['val_acc'])
    plt.title('model accuracy')
    plt.ylabel('accuracy')
    plt.xlabel('epoch')
    plt.legend(['train', 'val'], loc='upper left')
    
    plt.tight_layout()
    
plot_learning_curves(history)

probabilities = model.predict_generator(test_generator)

orig = test_generator.classes
preds = probabilities > 0.5

cm = confusion_matrix(orig, preds)
print(cm)

tn, fp, fn, tp = cm.ravel()

precision = tp/(tp+fp)
recall = tp/(tp+fn)

title = "Recall:{:.2f}%\nPrecision:{:.0f}%".format(recall * 100, precision * 100)
print(title)

plot_confusion_matrix(cm, hide_ticks=True, cmap=plt.cm.Blues)
plt.xticks(range(2), ['Normal', 'Pneumonia'], fontsize=16)
plt.yticks(range(2), ['Normal', 'Pneumonia'], fontsize=16)
plt.title(title)
plt.show()

probabilities = model.predict_generator(test_generator)

orig = test_generator.classes
preds = probabilities > 0.5

cm = confusion_matrix(orig, preds)
print(cm)

tn, fp, fn, tp = cm.ravel()

precision = tp/(tp+fp)
recall = tp/(tp+fn)

title = "Confusion Matrix"
print(title)

plot_confusion_matrix(cm, hide_ticks=True, cmap=plt.cm.Blues)
plt.xticks(range(2), ['Normal', 'Pneumonia'], fontsize=16)
plt.yticks(range(2), ['Normal', 'Pneumonia'], fontsize=16)
plt.title(title)
plt.show()

from sklearn.metrics import accuracy_score
acc = accuracy_score(test_labels, np.round(preds))*100
print('\nTEST METRICS ----------------------')
precision = tp/(tp+fp)*100
recall = tp/(tp+fn)*100
print('Accuracy: {}%'.format(acc))
print('Precision: {}%'.format(precision))
print('Recall: {}%'.format(recall))
print('F1-score: {}'.format(2*precision*recall/(precision+recall)))

print('\nTRAIN METRICS ----------------------')
print('Train acc: {}%'.format(np.round((history.history['acc'][-1])*100, 5)))
print('Train loss: {}%'.format(np.round((history.history['loss'][-1])*100, 5)))

from sklearn.metrics import confusion_matrix
from model_depict import plot_confusion_matrix

preds = model.predict(test_generator)

#acc = accuracy_score(test_labels, np.round(preds))*100


conf_mtx = confusion_matrix(test_labels, np.round(preds))
plot_confusion_matrix(conf_mtx, target_names = ['NORMAL', 'PNEUMONIA'], normalize=False)

from sklearn.metrics import accuracy_score
from sklearn.metrics import confusion_matrix
preds = model.predict(test_generator)

acc = accuracy_score(test_labels, np.round(preds))*100
cm = confusion_matrix(test_labels, np.round(preds))
tn, fp, fn, tp = cm.ravel()

print('\nTEST METRICS ----------------------')
precision = tp/(tp+fp)*100
recall = tp/(tp+fn)*100
print('Accuracy: {}%'.format(acc))
print('Precision: {}%'.format(precision))
print('Recall: {}%'.format(recall))
print('F1-score: {}'.format(2*precision*recall/(precision+recall)))

print('\nTRAIN METRICS ----------------------')
print('Train acc: {}%'.format(np.round((history.history['acc'][-1])*100, 5)))
print('Train loss: {}%'.format(np.round((history.history['loss'][-1])*100, 5)))

from model_depict import plot_confusion_matrix
probabilities = model.predict_generator(test_generator)
orig = test_generator.classes
preds = probabilities > 0.5

cm = confusion_matrix(orig, preds)
print(cm)

tn, fp, fn, tp = cm.ravel()

#precision = tp/(tp+fp)
#recall = tp/(tp+fn)

tn, fp, fn, tp = cm.ravel()
score = model.evaluate(test_generator)
#precision = tp/(tp+fp)
#recall = tp/(tp+fn)
acc = (score[1])*100

print('\nTEST METRICS ----------------------')
precision = tp/(tp+fp)*100
recall = tp/(tp+fn)*100
print('Accuracy: {}%'.format(acc))
print('Precision: {}%'.format(precision))
print('Recall: {}%'.format(recall))
print('F1-score: {}'.format(2*precision*recall/(precision+recall)))
print('\nTRAIN METRICS ----------------------')
print('Train acc: {}%'.format(np.round((history.history['acc'][-1])*100, 5)))
print('Train loss: {}%'.format(np.round((history.history['loss'][-1])*100, 5)))

conf_mtx = confusion_matrix(orig, preds) 
plot_confusion_matrix(conf_mtx, target_names = ['NORMAL', 'PNEUMONIA'], normalize=False)
#plot_confusion_matrix(cm,figsize=(10,5), hide_ticks=True, cmap=plt.cm.Blues)
#plt.xticks(range(2), ['Normal', 'Pneumonia'], fontsize=16)
#plt.yticks(range(2), ['Normal', 'Pneumonia'], fontsize=16)
#plt.title(title)
#plt.show()

from sklearn.metrics import accuracy_score, confusion_matrix

preds = model.predict(test_generator)

acc = accuracy_score(test_labels, np.round(preds))*100
cm = confusion_matrix(test_labels, np.round(preds))
tn, fp, fn, tp = cm.ravel()

print('CONFUSION MATRIX ------------------')
print(cm)

print('\nTEST METRICS ----------------------')
precision = tp/(tp+fp)*100
recall = tp/(tp+fn)*100
print('Accuracy: {}%'.format(acc))
print('Precision: {}%'.format(precision))
print('Recall: {}%'.format(recall))
print('F1-score: {}%'.format(2*precision*recall/(precision+recall)))

print('\nTRAIN METRIC ----------------------')
print('Train acc: {}%'.format(np.round((history.history['acc'][-1])*100, 2)))
print('Train loss: {}%'.format(np.round((history.history['loss'][-1])*100, 2)))

#Sample Predictions
from PIL import Image

def testImage(model, file, title, width=299, height=299, color=True):
    image = Image.open(file)
    if color:
        image = image.convert("RGB")
    plt.imshow(np.asarray(image))
    data = np.array(image.resize((width,height), Image.ANTIALIAS)).reshape(1, width, height, 3 if color else 1)/299
    plt.title(title)
    plt.xlabel("Prediction: " + ("PNEUMONIA" if model.predict(data) > 0.5 else "NORMAL"))
    plt.show()

from PIL import Image

def testImage(model, file, title, width=299, height=299, color=True):
    image = Image.open(file)
    if color:
        image = image.convert("RGB")
    plt.imshow(np.asarray(image))
    data = np.array(image.resize((width,height), Image.ANTIALIAS)).reshape(1, width, height, 3 if color else 1)/299
    data
    plt.title(title)
    
    if (model.predict(data) >0.5).any():
      plt.xlabel("Prediction: " + "PNEUMONIA")
    elif (model.predict(data) <0.5).any():
      plt.xlabel("Prediction: " + "NORMAL")
    plt.show()
testImage(
    model = model,
    file = '/content/drive/My Drive/chest_xray/test/PNEUMONIA/person100_bacteria_475.jpeg',
    title = "Pneumonia sample - Inceptionv3 Model"
)

from PIL import Image

def testImage(model, file, title, width=299, height=299, color=True):
    image = Image.open(file)
    if color:
        image = image.convert("RGB")
    plt.imshow(np.asarray(image))
    data = np.array(image.resize((width,height), Image.ANTIALIAS)).reshape(1, width, height, 3 if color else 1)/299
    data
    plt.title(title)
    
    if (model.predict(data) >0.5).any():
      plt.xlabel("Prediction: " + "PNEUMONIA")
    elif (model.predict(data) <0.5).any():     
      plt.xlabel("Prediction: " + "NORMAL")    
    plt.show()
testImage(
    model = model,
    file = '/content/drive/My Drive/chest_xray/test/NORMAL/IM-0001-0001.jpeg',
    title = "Normal sample - Inceptionv3 MODEL"
)

from PIL import Image

def testImage(model, file, title, width=299, height=299, color=True):
    image = Image.open(file)
    if color:
        image = image.convert("RGB")
    plt.imshow(np.asarray(image))
    data = np.array(image.resize((width,height), Image.ANTIALIAS)).reshape(1, width, height, 3 if color else 1)/299
    data
    plt.title(title)
    
    if (model.predict(data) >0.5).any():
      plt.xlabel("Prediction: " + "PNEUMONIA")
    elif (model.predict(data) <0.5).any():
      plt.xlabel("Prediction: " + "NORMAL")
    plt.show()
testImage(
    model = model,
    file = '/content/drive/My Drive/chest_xray/test/PNEUMONIA/person121_bacteria_580.jpeg',
    title = "Pneumonia sample - Inceptionv3 Model"
)

from PIL import Image

def testImage(model, file, title, width=299, height=299, color=True):
    image = Image.open(file)
    if color:
        image = image.convert("RGB")
    plt.imshow(np.asarray(image))
    data = np.array(image.resize((width,height), Image.ANTIALIAS)).reshape(1, width, height, 3 if color else 1)/299
    data
    plt.title(title)
    
    if (model.predict(data) >0.5).any():
      plt.xlabel("Prediction: " + "PNEUMONIA")
    elif (model.predict(data) <0.5).any():
      plt.xlabel("Prediction: " + "NORMAL")
    plt.show()
testImage(
    model = model,
    file = '/content/drive/My Drive/chest_xray/test/PNEUMONIA/person102_bacteria_487.jpeg',
    title = "Pneumonia sample - Inceptionv3 Model"
)

from PIL import Image

def testImage(model, file, title, width=299, height=299, color=True):
    image = Image.open(file)
    if color:
        image = image.convert("RGB")
    plt.imshow(np.asarray(image))
    data = np.array(image.resize((width,height), Image.ANTIALIAS)).reshape(1, width, height, 3 if color else 1)/299
    data
    plt.title(title)
    
    if (model.predict(data) >0.5).any():
      plt.xlabel("Prediction: " + "PNEUMONIA")
    elif (model.predict(data) <0.5).any():     
      plt.xlabel("Prediction: " + "NORMAL")    
    plt.show()
testImage(
    model = model,
    file = '/content/drive/My Drive/chest_xray/test/NORMAL/IM-0050-0001.jpeg',
    title = "Normal sample - Inceptionv3 MODEL"
)