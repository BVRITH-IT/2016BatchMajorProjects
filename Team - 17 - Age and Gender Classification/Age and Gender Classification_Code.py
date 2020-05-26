# -*- coding: utf-8 -*-

from google.colab import drive
drive.mount('/content/drive')

!wget https://data.vision.ee.ethz.ch/cvl/rrothe/imdb-wiki/static/wiki_crop.tar

!tar -xf wiki_crop.tar

import os

lists = os.scandir('wiki_crop')
length = 0
for item in lists:
   
   if '.mat' not in str(item.path): 
        
     length+=len(os.listdir(item.path))
   
print(length)

import os
path = "/content/wiki_crop/00/"
files = os.listdir(path)
size = len(files)
print("Total samples:",size)

#CNN model construction
import keras
from keras.models import Sequential,Model,Input
from keras.layers import Conv2D,MaxPooling2D,BatchNormalization,Dropout,Dense,Flatten

inputShape=(64,64,3)

model= Sequential()
model.add(Conv2D(32,(3,3),padding="same",input_shape=inputShape,activation='relu'))
model.add(BatchNormalization(axis=-1))
model.add(MaxPooling2D(pool_size=(3,3)))
model.add(Dropout(0.25))

model.add(Conv2D(32,(1,1)))
model.add(BatchNormalization(axis=-1))

model.add(Conv2D(64,(3,3),padding="same",activation='relu'))
model.add(BatchNormalization(axis=-1))

model.add(Conv2D(64,(1,1)))
model.add(BatchNormalization(axis=-1))
model.add(MaxPooling2D(pool_size=(2,2)))
model.add(Dropout(0.25))

model.add(Conv2D(128,(3,3),padding="same",activation='relu'))
model.add(BatchNormalization(axis=-1))

model.add(Conv2D(128,(1,1)))
model.add(BatchNormalization(axis=-1))
model.add(MaxPooling2D(pool_size=(2,2)))
model.add(Dropout(0.25))

model.add(Flatten())

model.add(Dense(1024,activation='relu'))
model.add(BatchNormalization(axis=-1))
model.add(Dropout(0.25))
x = model.get_output_at(-1)

y1 = Dense(128,activation='relu')(x)
y1 = Dropout(0.25)(y1)

y2 = Dense(128,activation='relu')(x)
y2 = Dropout(0.25)(y2)

y1 = Dense(2,activation='softmax',name='gender')(y1)
y2 = Dense(13,activation='softmax',name='age')(y2)

mod = Model(inputs = model.get_input_at(0), outputs = [y1,y2] )

mod.save("2-class-base-dropouts.h5")

#Preprocessing
import cv2
import numpy as np
import os
import glob
 
path='/content/wiki_crop' #path where dataset 
def doit(image_path):
    image = cv2.imread(image_path)
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades+'haarcascade_frontalface_default.xml')
    try:
      gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
      gray=np.array(gray,dtype='uint8')
      faces = face_cascade.detectMultiScale(
          gray,
          scaleFactor= 1.1,
          minNeighbors= 5,
          minSize=(10, 10)
      )
      for (x, y, w, h) in faces:
          face_crop = np.copy(image[y:y+h,x:x+w])
          face_crop = cv2.resize(face_crop,(64,64))
          if face_crop.any():
              cv2.imwrite(image_path,face_crop)
              return 1
    except Exception as e:
      print(e)
      return -1
    return 0

written=0
unable_to_read=0
no_faces_found=0
for i in glob.glob(path+'/*/*'):
      k=doit(i)
      if k==1:
        if written<10:
          print('Image is written '+str(i))
        written+=1
      elif k==-1:
        os.remove(i)
        if unable_to_read<10:
          print('Unable to read '+str(i))
        unable_to_read+=1
      elif k==0:
        os.remove(i)
        if no_faces_found<10:
          print('No face found '+str(i))
        no_faces_found+=1
      if ((written+unable_to_read+no_faces_found) % 500 ==0) and ((written+unable_to_read+no_faces_found)!=0):
          print(str(written+unable_to_read+no_faces_found)+' Faces Read')
      if (written % 500 ==0) and (written!=0):
          print(str(written)+' Faces done')
      if (unable_to_read % 500 ==0) and (unable_to_read!=0):
          print(str(unable_to_read)+' Unable to read')
      if (no_faces_found % 500 ==0) and (no_faces_found!=0):
          print(str(no_faces_found)+' no_faces_found')
print(str(written+unable_to_read+no_faces_found)+" Total pics")
print(str(written)+' Images cropped')
print(str(unable_to_read)+' unable to read')
print(str(no_faces_found)+' Faces not found')

#Training the data
import numpy as np
import cv2
import pandas as pd
import zipfile
path_to_zip_file='/content/Manual DataSet.zip'
path='/content'
with zipfile.ZipFile(path_to_zip_file, 'r') as zip_ref:
    zip_ref.extractall(path)

import glob
a = glob.glob(path+'/*/*/*')
print(len(a))

int_to_gen = {0: 'female',1: 'male'}
gen_to_int = {'Female':0,'Male':1}
int_to_age = {
    0: '(0-5)',
    1: '(6-10)',
    2: '(11-15)',
    3:'(16-20)',
    4:'(21-25)',
    5:'(26-30)',
    6:'(31-35)',
    7:'(36-40)',
    8:'(41-45)',
    9:'(46-50)',
    10:'(51-55)',
    11:'(56-60)',
    12:'(61-100)'
}
age_to_int = {
    '1-5': 0,
    '6-10': 1 ,
    '11-15': 2,
    '16-20': 3,
    '21-25': 4,
    '26-30': 5,
    '31-35': 6,
    '36-40': 7,
    '41-45': 8,
    '46-50': 9,
    '51-55': 10,
    '56-60': 11,
    '61-100': 12
}

gen=[]
age=[]
for da in a:
  gen.append(gen_to_int[da.split('/')[3]])
  age.append(age_to_int[da.split('/')[4].split(' ')[0]])

from keras.utils import to_categorical

age=to_categorical(age,dtype='int')

gen=to_categorical(gen,dtype='int')

df = pd.DataFrame({'path':a,'gender':gen.tolist(),'age':age.tolist()})

print(df.head(652))

from keras.preprocessing.image import ImageDataGenerator

datagen=ImageDataGenerator(rescale=1./255.,validation_split=0.30)

train_generator=datagen.flow_from_dataframe(
dataframe=df,
directory=None,
x_col='path',
y_col=['gender','age'],
subset="training",
batch_size=32,
seed=42,
shuffle=True,
class_mode="multi_output",
target_size=(64,64))

validation_generator=datagen.flow_from_dataframe(
dataframe=df,
directory=None,
x_col='path',
y_col=['gender','age'],
subset="validation",
batch_size=32,
seed=42,
shuffle=True,
class_mode="multi_output",
target_size=(64,64))

from keras.models import load_model

model = load_model('/content/2-class-base-dropouts.h5')

model.compile(optimizer='adam',loss='categorical_crossentropy',metrics=['accuracy'])
history=model.fit_generator(generator=validation_generator,steps_per_epoch=validation_generator.samples//validation_generator.batch_size,epochs=30,validation_data=train_generator,validation_steps=train_generator.samples//train_generator.batch_size)

model.save('trainedmodel.h5')

import matplotlib.pyplot as plt
print(history.history.keys())

# summarize history for Gender accuracy
plt.plot(history.history['gender_acc'])
plt.title('Gender accuracy')
plt.ylabel('accuracy')
plt.xlabel('epoch')
plt.show()

# summarize history for Age accuracy
plt.plot(history.history['age_acc'])
plt.title('Age accuracy')
plt.ylabel('accuracy')
plt.xlabel('epoch')
plt.show()

import matplotlib.pyplot as plt
plt.plot(history.history['acc'])
plt.plot(history.history['val_acc'])
plt.title('model accuracy')
plt.ylabel('accuracy')
plt.xlabel('epoch')
plt.legend(['train', 'test'], loc='upper left')
plt.show()

import keras
path = "/content/wiki_crop/00/10110600_1985-09-17_2012.jpg" #path to image

import cv2,numpy as np
from PIL import Image
from keras.preprocessing.image import img_to_array
from google.colab.patches import cv2_imshow
from keras.models import load_model

a = load_model("/content/2-Trained_model.h5")

int_to_gen = {0: 'female',1: 'male'}
int_to_age = {
    0: '(0-5)',
    1: '(6-10)',
    2: '(11-15)',
    3:'(16-20)',
    4:'(21-25)',
    5:'(26-30)',
    6:'(31-35)',
    7:'(36-40)',
    8:'(41-45)',
    9:'(46-50)',
    10:'(51-55)',
    11:'(56-60)',
    12:'(61-100)'
}

image = cv2.imread(path)
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
facer = cv2.CascadeClassifier(cv2.data.haarcascades+'haarcascade_frontalface_default.xml')
faces = facer.detectMultiScale(
  gray,
  scaleFactor= 1.1,
  minNeighbors= 5,
  minSize=(10, 10)
)

for (x, y, w, h) in faces:
    cv2.rectangle(image,(x,y),(x+w,y+h),(0,255,0),2)
    face_crop = np.copy(image[y:y+h,x:x+w])
    face_crop = cv2.resize(face_crop,(64,64))
    if face_crop.any():
        i=cv2.cvtColor(face_crop, cv2.COLOR_BGR2RGB)
        i = Image.fromarray(i)
        i = i.resize((64,64))
        i = img_to_array(i)
        i=i/255.
        ans=a.predict(np.expand_dims(i,0))
        gender=int_to_gen[ans[0].argmax()]
        age=int_to_age[ans[1].argmax()]
        cv2.putText(image, gender+' '+age, (x,y-5), cv2.FONT_HERSHEY_SIMPLEX, 0.5,
                    (0,255,0), 2)
try:
    if not faces:
            i=cv2.cvtColor(face_crop, cv2.COLOR_BGR2RGB)
            i = Image.fromarray(i)
            i = i.resize((64,64))
            i = img_to_array(i)
            i=i/255.
            ans=a.predict(np.expand_dims(i,0))
            gender=int_to_gen[ans[0].argmax()]
            age=int_to_age[ans[1].argmax()]
            cv2.putText(image, gender+' '+age, (x,y-5), cv2.FONT_HERSHEY_SIMPLEX, 0.7,
                            (0,255,0), 2)
except:
    pass

cv2_imshow(image)
cv2.imwrite('result.png',image)
cv2.waitKey(0)
cv2.destroyAllWindows()

