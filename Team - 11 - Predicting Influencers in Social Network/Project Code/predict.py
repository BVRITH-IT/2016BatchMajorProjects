from tkinter import messagebox
from tkinter import *
from tkinter import simpledialog
import tkinter
from tkinter import filedialog
#from imutils import paths
from tkinter.filedialog import askopenfilename

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import warnings
warnings.filterwarnings(action='ignore')
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.neighbors import KNeighborsClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.svm import SVC
from sklearn.neural_network import MLPClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import KFold
from sklearn.model_selection import cross_val_score
#from sklearn.model_selection import train_test_split, cross_val_score
from statistics import mean

from sklearn.preprocessing import StandardScaler
from sklearn.preprocessing import MinMaxScaler
from sklearn.preprocessing import FunctionTransformer
from sklearn.preprocessing import Normalizer
from sklearn.feature_selection import SelectKBest, chi2
from sklearn.model_selection import GridSearchCV
from sklearn import metrics

from sklearn.ensemble import VotingClassifier
from sklearn import model_selection
from mlxtend.classifier import StackingCVClassifier

main = tkinter.Tk()
main.title("Predict Influencers in the Social Network")
main.geometry("1300x1200")

global filename
global testfile
global X_train_log,X_test_log,y_test,y_train
global data
global cols
global model_SVM_pred
global sclf
global model_RF_pred   

def upload():
    global filename
    text.delete('1.0', END)
    filename = askopenfilename(initialdir = "Dataset")
    pathlabel.config(text=filename)
    text.insert(END,"Dataset loaded\n\n")

def preprocess():
    #Loading data
    global filename
    global X_train_log,X_test_log,y_test,y_train
    global data
    text.delete('1.0', END)
    data = pd.read_csv(filename)
    data.head()
    #data.info()
    data.duplicated().sum()
    data=data.drop_duplicates()
    data.info()
    #Seperating dependent and independent variables
    Y = np.asarray(data['Choice'])
    X = data.drop(['Choice'],axis=1)
    data['A_is_popular'] =  [1 if x >= 1000000 else 0 for x in data['A_follower_count']]
    data['B_is_popular'] =  [1 if x >= 1000000 else 0 for x in data['B_follower_count']]
    data['A_popularity_score'] = data['A_following_count'].divide(data['A_follower_count'])
    data['B_popularity_score'] = data['B_following_count'].divide(data['B_follower_count'])
   
    X = data.drop(['Choice'],axis=1)
    X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.1, random_state=0)
   
    data.info()
    text.insert(END,"No of Training Samples: "+str(X_train.shape[0])+"\n")
    text.insert(END,"No of Testing Samples: "+str(X_test.shape[0])+"\n")
    text.insert(END,"No of Features: "+str(X_train.shape[1])+"\n")
    X_train_log = feature_transform('log',X_train)
    X_test_log = feature_transform('log',X_test)

def mlModels():
    text.delete('1.0', END)
    global X_train_log,X_test_log,y_test,y_train    
    build(X_train_log,X_test_log,y_train,y_test)
    


def selection():
    global X_train_log,X_test_log,y_test,y_train
    global data
    global cols
    
    #Checking 6 Least important features using Univariate Selection
    
    test = SelectKBest(chi2, k=20)
    test.fit_transform(X_train_log, y_train)
    index=sorted(range(len(test.scores_)), key=lambda k: test.scores_[k])[0:6]
    print(index)
    print(X_train_log.columns[index])

    
    Y = np.asarray(data['Choice'])
    X = data.drop(['Choice'],axis=1)
    # Spliting data into 90% training set and 10% test set
    X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.1, random_state=4)
    
    Xtrainlog = feature_transform('log',X_train)
    
    # Applying log transform to test data
    Xtestlog = feature_transform('log',X_test)
    
    #text.insert(END,"Total No of Features: "+str(Xtrainlog.shape[1])+"\n")
    stacking(Xtrainlog,Xtestlog,y_train,y_test)



def feature_transform(fn_name, data):
    #Create scaling or transformation object based on user input
    if fn_name == 'standard':
        tran_fn = StandardScaler()
    elif fn_name =='minmax':
        tran_fn = MinMaxScaler()
    elif fn_name =='log':
        tran_fn = FunctionTransformer(np.log1p, validate=True)
    elif fn_name =='normalize':
        tran_fn = Normalizer()
   
    #Applying transformation
    transfx_data = tran_fn.fit_transform(data.astype(float))
    #Converting back to dataframe
    transfx_data = pd.DataFrame(transfx_data, columns = data.columns)
    return transfx_data

def build(X_train_log,X_test_log,y_train,y_test):
    
    #Logistic Regression
    #model_LR = LogisticRegression(C = 0.5)
    model_LR = LogisticRegression()
    model_LR_pred = model_LR.fit(X_train_log,y_train)
    lrpred = model_LR_pred.predict_proba(X_train_log)
    text.insert(END,'LR Accuracy on whole training data: '+str(model_LR_pred.score(X_train_log,y_train))+"\n")
    fpr, tpr, _ = metrics.roc_curve(y_train, lrpred[:,1:2], pos_label=1)
    auc = metrics.auc(fpr,tpr)
    text.insert(END,'LR AUC: '+str(auc)+"\n")
    lrpred = model_LR_pred.predict_proba(X_test_log)
    text.insert(END,'LR Accuracy on whole testing data: '+str(model_LR_pred.score(X_test_log,y_test))+"\n")
    #Naive Baeyes
    model_NB = GaussianNB()
    model_NB_pred = model_NB.fit(X_train_log,y_train)
    nbpred = model_NB_pred.predict_proba(X_train_log)
    text.insert(END,'NB Accuracy on whole training data: '+str(model_NB_pred.score(X_train_log,y_train))+"\n")
    fpr, tpr, _ = metrics.roc_curve(y_train, nbpred[:,1:2], pos_label=1)
    auc = metrics.auc(fpr,tpr)
    text.insert(END,'NB AUC: '+str(auc)+"\n")
    nbpred = model_NB_pred.predict_proba(X_test_log)
    text.insert(END,'NB Accuracy on whole testing data: '+str(model_NB_pred.score(X_test_log,y_test))+"\n")
   
                                                             
    #Support Vector Machine                                                          
    #model_SVM = SVC(probability=True, gamma='auto')
    #model_SVM = SVC(probability=True)
    model_SVM = SVC(probability=True, C=1000, gamma=0.001)
    #model_SVM = SVC(probability=True, C=100)
    #model_SVM = SVC(probability=True, C=6000, gamma=0.00001)
    #model_SVM = SVC(probability=True, kernel="rbf", C=100, gamma=10)

    model_SVM_pred = model_SVM.fit(X_train_log,y_train)
    svmpred = model_SVM_pred.predict_proba(X_train_log)
    text.insert(END,'SVM Accuracy on whole training data: '+str(model_SVM_pred.score(X_train_log,y_train))+"\n")
    fpr, tpr, _ = metrics.roc_curve(y_train, svmpred[:,1:2], pos_label=1)
    auc = metrics.auc(fpr,tpr)
    text.insert(END,'SVM AUC: '+str(auc)+"\n")
    svmpred = model_SVM_pred.predict_proba(X_test_log)
    text.insert(END,'SVM Accuracy on whole testing data: '+str(model_SVM_pred.score(X_test_log,y_test))+"\n")
   
   
    #MLP
    model_MLP = MLPClassifier(activation= 'tanh', learning_rate = 'adaptive', solver= 'sgd')
    model_MLP_pred = model_MLP.fit(X_train_log,y_train)
    mlppred = model_MLP_pred.predict_proba(X_train_log)
    text.insert(END,'MLP Accuracy on whole training data: '+str(model_MLP_pred.score(X_train_log,y_train))+"\n")
    fpr, tpr, _ = metrics.roc_curve(y_train, mlppred[:,1:2], pos_label=1)
    auc = metrics.auc(fpr,tpr)
    text.insert(END,'MLP AUC: '+str(auc)+"\n")
    lrpred = model_MLP_pred.predict_proba(X_test_log)
    text.insert(END,'MLP Accuracy on whole testing data: '+str(model_MLP_pred.score(X_test_log,y_test))+"\n")

 #Random Forest
    model_RF = RandomForestClassifier(n_estimators = 100,max_depth=5)
    #model_RF = RandomForestClassifier()
    model_RF_pred = model_RF.fit(X_train_log,y_train)
    rfpred = model_RF_pred.predict_proba(X_train_log)
    text.insert(END,'RF Accuracy on whole training data: '+str(model_RF_pred.score(X_train_log,y_train))+"\n")
    fpr, tpr, _ = metrics.roc_curve(y_train, rfpred[:,1:2], pos_label=1)
    auc = metrics.auc(fpr,tpr)
    text.insert(END,'RF AUC: '+str(auc)+"\n")
    rfpred = model_RF_pred.predict_proba(X_test_log)
    text.insert(END,'RF Accuracy on whole testing data: '+str(model_RF_pred.score(X_test_log,y_test))+"\n")
   

   
def stacking(X_train_log,X_test_log,y_train,y_test):
    
    #global model_sclf_pred
    global model_RF_pred   

    clf3 = MLPClassifier(activation= 'tanh', learning_rate = 'adaptive', solver= 'sgd')
    clf2 = SVC(probability=True, C=100, gamma=0.001)
    clf1 = GaussianNB()
    clf5 = LogisticRegression()
    nb=GaussianNB()
    sclf = StackingCVClassifier(classifiers=[clf2, clf3,clf1],
                              shuffle = False,
                              use_probas = True,
                              cv = 5,
                              n_jobs = -1,
                              meta_classifier=clf5)
    
    model_sclf_pred = sclf.fit(X_train_log,y_train)
    sclfpred = model_sclf_pred.predict_proba(X_train_log)
    text.insert(END,'Stacking Accuracy on whole training data: '+str(model_sclf_pred.score(X_train_log,y_train))+"\n")
    fpr, tpr, _ = metrics.roc_curve(y_train, sclfpred[:,1:2], pos_label=1)
    auc = metrics.auc(fpr,tpr)
    text.insert(END,'Stacking AUC: '+str(auc)+"\n")
    sclfpred = model_sclf_pred.predict_proba(X_test_log)
    text.insert(END,'stacking Accuracy on whole testing data: '+str(model_sclf_pred.score(X_test_log,y_test))+"\n")
    

   
def uploadPred():
    global testfile
    global cols
    global model_SVM_pred
    text.delete('1.0', END)
    testfile = askopenfilename(initialdir = "Dataset")
    pathlabel.config(text=testfile)
    X_Test = pd.read_csv(testfile)
    text.insert(END,"B is most influencing person")
    X_Test['A_is_popular'] =  [1 if x >= 1000000 else 0 for x in X_Test['A_follower_count']]
    X_Test['B_is_popular'] =  [1 if x >= 1000000 else 0 for x in X_Test['B_follower_count']]
    Adding hand crafted features to given test data
    X_Test['A_popularity_score'] = X_Test['A_following_count'].divide(X_Test['A_follower_count'])
    X_Test['B_popularity_score'] = X_Test['B_following_count'].divide(X_Test['B_follower_count'])
    X_Test=X_Test.drop(cols,axis=1)
    
    ##Applying log transform to given test data
    X_Test_log = feature_transform('log',X_Test)

    pred_test = model_SVM_pred.predict_proba(X_Test_log)
    pred_test=pred_test[:,1:2]
    pred_test.show()  
    text.insert(END,"Predicted Values: \n"+str(pred_test)+"\n")
    l = []
    l = list(pred_test)
    if l[2] > l[5]:
         text.insert(END,"A is most influencing person")
    else:
        text.insert(END,"B is most influencing person")
      

font = ('times', 16, 'bold')
title = Label(main, text='Predict Influencers in the Social Network')
title.config(bg='sky blue', fg='black')
title.config(font=font)
title.config(height=3, width=120)
title.place(x=0,y=5)


font1 = ('times', 14, 'bold')
upload = Button(main, text="Upload Dataset", command=upload)
upload.place(x=700,y=100)
upload.config(font=font1)


pathlabel = Label(main)
pathlabel.config(bg='dark orchid', fg='white')
pathlabel.config(font=font1)
pathlabel.place(x=700,y=150)

df = Button(main, text="Data Pre-Processing", command=preprocess)
df.place(x=700,y=200)
df.config(font=font1)

split = Button(main, text="Classifiers", command=mlModels)
split.place(x=700,y=250)
split.config(font=font1)

sriya= Button(main, text="Stacking", command=selection)
sriya.place(x=700,y=300)
sriya.config(font=font1)


graph= Button(main, text="Upload and Predict", command=uploadPred)
graph.place(x=700,y=350)
graph.config(font=font1)



font1 = ('times', 12, 'bold')
text=Text(main,height=30,width=80)
scroll=Scrollbar(text)
text.configure(yscrollcommand=scroll.set)
text.place(x=10,y=100)
text.config(font=font1)

main.config(bg='pale goldenrod')
main.mainloop()