import tkinter as tk
from tkinter import filedialog
from tkinter import *
from PIL import ImageTk, Image

import numpy
#Loading the trained model to classify sign
from keras.models import load_model
model = load_model('C:\\Users\\lenovo\\Downloads\\TrafficSignsRecognition\\Traffic_Recognition.h5')
#dictionary to label all traffic signs class
classes = { 1:'Speed limit (20km/h)',
            2:'Speed limit (30km/h)', 
            3:'Speed limit (50km/h)', 
            4:'Speed limit (60km/h)', 
            5:'Speed limit (70km/h)', 
            6:'Speed limit (80km/h)', 
            7:'End of speed limit (80km/h)', 
            8:'Speed limit (100km/h)', 
            9:'Speed limit (120km/h)', 
            10:'No passing', 
            11:'No passing veh over 3.5 tons', 
            12:'Right-of-way at intersection', 
            13:'Priority road', 
            14:'Yield', 
            15:'Stop', 
            16:'No vehicles', 
            17:'Veh > 3.5 tons prohibited', 
            18:'No entry', 
            19:'General caution', 
            20:'Dangerous curve left', 
            21:'Dangerous curve right', 
            22:'Double curve', 
            23:'Bumpy road', 
            24:'Slippery road', 
            25:'Road narrows on the right', 
            26:'Road work', 
            27:'Traffic signals', 
            28:'Pedestrians', 
            29:'Children crossing', 
            30:'Bicycles crossing', 
            31:'Beware of ice/snow',
            32:'Wild animals crossing', 
            33:'End speed + passing limits', 
            34:'Turn right ahead', 
            35:'Turn left ahead', 
            36:'Ahead only', 
            37:'Go straight or right', 
            38:'Go straight or left', 
            39:'Keep right', 
            40:'Keep left', 
            41:'Roundabout mandatory', 
            42:'End of no passing', 
            43:'End no passing veh > 3.5 tons' }

def classify(file_path):
  image = Image.open(file_path)
  image = image.resize((30,30))
  image = numpy.expand_dims(image, axis=0)
  image = numpy.array(image)
  pred = model.predict_classes([image])[0]
  sign = classes[pred+1]
  print(sign)
  result.configure(text=sign) 
              
def show_classify_btn(file_path):
  classify_b=Button(top,text="Classify Image",command=lambda: classify(file_path),padx=10,pady=5)
  classify_b.configure(bg='#364156', fg='white',font=('arial',10,'bold'))
  classify_b.place(relx=0.79,rely=0.46)  

def upload_image():
  try:
    file_path=filedialog.askopenfilename()
    uploaded=Image.open(file_path)
    uploaded.thumbnail(((top.winfo_width()/2.25),(top.winfo_height()/2.25)))
    im=ImageTk.PhotoImage(uploaded)
    sign_image.configure(image=im)
    sign_image.image=im
    result.configure(text='')
    show_classify_btn(file_path)
  except:
    pass

#Initialise GUI
if __name__=="__main__":
  top=tk.Tk()
  top.geometry('800x600')
  top.title('Traffic sign recognition')
  top.configure(bg='#f9f6f7')
  heading = Label(top, text="Traffic sign recognition",pady=20, font=('arial',20,'bold'))
  heading.configure(background='#f9f6f7',fg='#364156')
  heading.pack()
  result=Label(top, font=('arial',15,'bold'))
  result.configure(fg='#011638',bg='#f9f6f7')
  sign_image = Label(top)
  upload=Button(top,text="Upload an image",command=upload_image,padx=10,pady=5)
  upload.configure(background='#364156', fg='white',font=('arial',10,'bold'))
  upload.pack(side=BOTTOM,pady=50)
  sign_image.pack(side=BOTTOM,expand=True)
  result.pack(side=BOTTOM,expand=True)
  top.mainloop()

