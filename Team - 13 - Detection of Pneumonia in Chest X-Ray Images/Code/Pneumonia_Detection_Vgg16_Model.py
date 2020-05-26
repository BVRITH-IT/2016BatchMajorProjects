import os
import glob
import urllib.request

import numpy as np
import matplotlib.pyplot as plt

import tensorflow as tf
from tensorflow.data import Dataset, Iterator

from google.colab import drive
drive.mount('/content/drive')

glob.glob('/content/drive/My Drive/chest_xray/*/*')

img_normal = plt.imread('/content/drive/My Drive/chest_xray/train/NORMAL/IM-0131-0001.jpeg')
img_penumonia_bacteria = plt.imread('/content/drive/My Drive/chest_xray/train/PNEUMONIA/person1017_bacteria_2948.jpeg')
img_penumonia_virus = plt.imread('/content/drive/My Drive/chest_xray/train/PNEUMONIA/person1021_virus_1711.jpeg')

plt.figure(figsize=(12, 5))

plt.subplot(1,3,1).set_title('NORMAL')
plt.imshow(img_normal, cmap='gray')

plt.subplot(1,3,2).set_title('PNEUMONIA')
plt.imshow(img_penumonia_bacteria, cmap='gray')

plt.subplot(1,3,3).set_title('PNEUMONIA')
plt.imshow(img_penumonia_virus, cmap='gray')

plt.tight_layout()

def get_labeled_files(folder):
    x = []
    y = []
    
    for folderName in os.listdir(folder):
        if not folderName.startswith('/'):
            if folderName in ['NORMAL']:
                label = 0
            elif folderName in ['PNEUMONIA']:
                label = 1
            else:
                label = 2
                continue 
            for image_filename in os.listdir(folder + folderName):
                img_path = folder + folderName + '/' + image_filename
                if img_path is not None and str.endswith(img_path, 'jpeg'):
                    x.append(img_path)
                    y.append(label)
    
    x = np.asarray(x)
    y = np.asarray(y)
    return x, y

x, y = get_labeled_files('/content/drive/My Drive/chest_xray/train/') 
print(x,y)

NUM_CLASSES = 2

# This function takes image paths as arguments and reads corresponding images
def input_parser(img_path, label):
    # convert the label to one-hot encoding
    one_hot = tf.one_hot(label, NUM_CLASSES)
    # read the img from file and decode it using tf
    img_file = tf.io.read_file(img_path)
    img_decoded = tf.image.decode_jpeg(img_file, channels=3, name="decoded_images")
    return img_decoded, one_hot

# This function takes image and resizes it to smaller format (150x150)
def image_resize(images, labels):
    
    resized_image = tf.image.resize(images, (150, 150), align_corners=True)
    resized_image_asint = tf.cast(resized_image, tf.int32)
    return resized_image_asint, labels

# Since it uses lazy evaluation, images will not be read after calling build_pipeline_plan()
# Using iterator defined here in tf context
def build_pipeline_plan(img_paths, labels, batch_size):

    # Build a tensor of image paths and labels
    tr_data = Dataset.from_tensor_slices((img_paths, labels))
    # Read images in paths as jpegs
    tr_data_imgs = tr_data.map(input_parser)
    # Apply resize to each image in the pipeline
    tr_data_imgs = tr_data_imgs.map(image_resize)
    # batch images into small groups
    tr_dataset = tr_data_imgs.batch(batch_size)
    # create TensorFlow Iterator object directly from input pipeline
    iterator = tf.compat.v1.data.make_one_shot_iterator(tr_dataset)
    next_element = iterator.get_next()
    return next_element

# Function to execute defined pipeline in Tensorflow session
def process_pipeline(next_element):
    gpu_options = tf.GPUOptions(per_process_gpu_memory_fraction=0.333)
    with tf.Session(config=tf.ConfigProto(gpu_options=gpu_options)) as sess:
        images = []
        labels_hot = []
        while True:
            try:
                elem = sess.run(next_element)
                images = elem[0]
                labels_hot = elem[1]
            except tf.errors.OutOfRangeError:
                print("Finished reading the dataset")
                return images, labels_hot

def load_dataset(path, batch_size):
    tf.reset_default_graph()
    files, labels = get_labeled_files(path)
    p = tf.constant(files, name="train_imgs")
    l = tf.constant(labels, name="train_labels")
    
    next_element = build_pipeline_plan(p, l, batch_size=batch_size)
    imgs, labels = process_pipeline(next_element)
    return imgs, labels

x_train, y_train = load_dataset("/content/drive/My Drive/chest_xray/train/",6000)
x_test, y_test = load_dataset("/content/drive/My Drive/chest_xray/test/",6000)
x_val, y_val = load_dataset("/content/drive/My Drive/chest_xray/val/",6000)

print(x_train.shape)
print(y_train.shape)

y_test

import matplotlib.pyplot as plt
import seaborn as sns 

plt.subplot(1,3,1)
sns.countplot(np.argmax(y_train, axis=1)).set_title('TRAIN')

plt.subplot(1,3,2)
sns.countplot(np.argmax(y_test, axis=1)).set_title('TEST')

plt.subplot(1,3,3)
sns.countplot(np.argmax(y_val, axis=1)).set_title('VALIDATION')

plt.tight_layout()

#Building a Model
import shutil
import pandas as pd
import seaborn as sns

print(x_train.shape)

plt.figure(figsize=(5, 3))

y_train_classes = np.argmax(y_train, axis = 1)

plt.subplot(1,2,1).set_title('NORMAL')
plt.imshow(x_train[np.argmax(y_train_classes == 0)])

plt.subplot(1,2,2).set_title('PNEUMONIA')
plt.imshow(x_train[np.argmax(y_train_classes == 1)])

plt.tight_layout()

import keras
from keras import backend as K
from keras.models import Model
from keras.layers import Flatten, Dense, BatchNormalization, Dropout
from keras.applications.vgg16 import VGG16

K.clear_session()

NUM_CLASSES = 2

base_model = VGG16(weights='imagenet', include_top=False, input_shape=(150, 150, 3))

x = base_model.output
x = Flatten()(x)
x = Dense(NUM_CLASSES, activation='softmax')(x)

model = Model(inputs=base_model.input, outputs=x)

model.summary()

def print_layers(model):
    for idx, layer in enumerate(model.layers):
        print("layer {}: {}, trainable: {}".format(idx, layer.name, layer.trainable))

for layer in model.layers[0:20]:
    layer.trainable = False
    
print_layers(model)

model.trainable_weights

optimizer = keras.optimizers.RMSprop()

model.compile(loss='categorical_crossentropy',     
              optimizer=optimizer, 
              metrics=['accuracy'])

from keras.callbacks import ModelCheckpoint, TensorBoard, ReduceLROnPlateau, EarlyStopping

# This callback saves weights of model after each epoch
checkpoint = ModelCheckpoint(
    'model/weights.epoch_{epoch:02d}.hdf5',
    monitor='val_loss', 
    save_best_only=False, 
    save_weights_only=False,
    mode='auto',
    verbose=1
)

# This callback writes logs for TensorBoard
tensorboard = TensorBoard(
    log_dir='./Graph', 
    histogram_freq=0,  
    write_graph=True
)

from sklearn.utils import class_weight
y_labels = np.argmax(y_train, axis=1)
classweight = class_weight.compute_class_weight('balanced', np.unique(y_labels), y_labels)
print(classweight)

# prepare directory to store the model weights
os.makedirs('./model', exist_ok=True)

history = model.fit(
    x=x_train, y=y_train,
    class_weight=classweight,
    validation_split=0.3,
    callbacks=[checkpoint, tensorboard],
    shuffle=True,
    batch_size=64,
    epochs=30,
    verbose=1
)

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

model.save('vgg16_2.h5')

from sklearn.metrics import confusion_matrix
from model_depict import plot_confusion_matrix

y_pred = model.predict(x_train)
# To get prediction, we pick class with with highest probability
y_pred_classes = np.argmax(y_pred, axis = 1) 
y_true = np.argmax(y_train, axis = 1) 

conf_mtx = confusion_matrix(y_true, y_pred_classes) 
plot_confusion_matrix(conf_mtx, target_names = ['NORMAL', 'PNEUMONIA'], normalize=False)

score = model.evaluate(x_test, y_test)
print('Model Loss: {}, Accuracy: {}'.format(score[0], score[1]))

from sklearn.metrics import confusion_matrix
from model_depict import plot_confusion_matrix

y_pred = model.predict(x_test)
# to get the prediction, we pick the class with with the highest probability
y_pred_classes = np.argmax(y_pred, axis = 1) 
y_true = np.argmax(y_test, axis = 1) 

conf_mtx = confusion_matrix(y_true, y_pred_classes) 
plot_confusion_matrix(conf_mtx, target_names = ['NORMAL', 'PNEUMONIA'], normalize=False)

from sklearn.metrics import accuracy_score
from sklearn.metrics import confusion_matrix


y_pred = model.predict(x_test)
# to get the prediction, we pick the class with with the highest probability
y_pred_classes = np.argmax(y_pred, axis = 1) 
y_true = np.argmax(y_test, axis = 1) 

conf_mtx = confusion_matrix(y_true, y_pred_classes) 

score = model.evaluate(x_test, y_test)

conf_mtx = confusion_matrix(y_true, y_pred_classes)
acc = (score[1])*100

tn, fp, fn, tp = conf_mtx.ravel()

print('\nTEST METRICS ----------------------')
precision = tp/(tp+fp)*100
recall = tp/(tp+fn)*100
print('Accuracy: {}%'.format(acc))
print('Precision: {}%'.format(precision))
print('Recall: {}%'.format(recall))
print('F1-score: {}%'.format(2*precision*recall/(precision+recall)))

print('\nTRAIN METRICS ----------------------')
score = model.evaluate(x_train, y_train)
Ta=score[1]
print('Training Accuracy: ',Ta*100, '%')
Tl=score[0]
print("Training Loss:",Tl*100, '%')

import tensorflow as tf

loaded_model = tf.keras.models.load_model('/content/vgg16_2.h5')
loaded_model.layers[0].input_shape
model.summary()

from PIL import Image

def testImage(model, file, title, width=150, height=150, color=True):
    image = Image.open(file)
    if color:
        image = image.convert("RGB")
    plt.imshow(np.asarray(image))
    data = np.array(image.resize((width,height), Image.ANTIALIAS)).reshape(1, width, height, 3 if color else 1)/255
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
    title = "Pneumonia sample - VGG16 Model"
)

from PIL import Image

def testImage(model, file, title, width=150, height=150, color=True):
    image = Image.open(file)
    if color:
        image = image.convert("RGB")
    plt.imshow(np.asarray(image))
    data = np.array(image.resize((width,height), Image.ANTIALIAS)).reshape(1, width, height, 3 if color else 1)/255
    data
    plt.title(title)
    
    if (model.predict(data) <0.5).any():
      plt.xlabel("Prediction: " + "NORMAL")
    elif (model.predict(data) >0.5).any():     
      plt.xlabel("Prediction: " + "PNEUMONIA")    
    plt.show()
testImage(
    model = model,
    file = '/content/drive/My Drive/chest_xray/test/NORMAL/IM-0001-0001.jpeg',
    title = "Normal sample - VGG16 MODEL"
)