import pickle
import numpy,sklearn,pandas

"""
filename = 'finalized_model.sav'
pickle.dump(clf, open(filename, 'wb'))
"""
def predictor(splitted_data):
    print("/n script rf_model")
    # load the model from disk
    filename = 'forest_model.sav'
    loaded_model = pickle.load(open(filename, 'rb'))
    print("model loaded")
    print(splitted_data.shape)
    print(list(splitted_data))
    x = splitted_data.columns[3:14]
    preds = loaded_model.predict(splitted_data[x])
    print("prediction complete")
    print(preds)
    if preds == 0:
        str1 = "Phished Webpage"
    else: str1 = "Legitimate Webpage"
    
    score = loaded_model.predict_proba(splitted_data[x])
    return str1







