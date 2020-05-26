import pandas as pd
import numpy as np
import re
import warnings

from TagsList import tags_lst

warnings.filterwarnings("ignore")
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfVectorizer
from wordcloud import WordCloud
import nltk
#nltk.download('stopwords')
#nltk.download('punkt')
from nltk.corpus import stopwords
from Stopwords import stop_words
from nltk.tokenize import word_tokenize
from nltk.stem.snowball import SnowballStemmer
from sklearn.model_selection import train_test_split
from sklearn.multiclass import OneVsRestClassifier
from sklearn.linear_model import SGDClassifier
from sklearn.linear_model import LogisticRegression
from sklearn import metrics
from sklearn.metrics import f1_score,precision_score,recall_score

from flask import *
from flask import session
app = Flask(__name__)

vectorizer = TfidfVectorizer(min_df=0.00009, max_features=200000, tokenizer=lambda x: x.split(), ngram_range=(1, 3))
clf = OneVsRestClassifier(LogisticRegression(penalty='l2'))
stemmer = SnowballStemmer("english")
tags_list = tags_lst

@app.route('/')
def main():
    print("Main")
    data_half = pd.read_csv("C:/Pycharm/ProjectTest/MajorProject400000Records.csv")
    data_half["tag_count"] = data_half["Tags"].apply(lambda x: len(str(x).split()))
    print(data_half["tag_count"].value_counts())


    final_list = []
    i = 0
    for result in data_half.Tags:
        split_list = str(result).split()

        val = [data_half.iloc[i]["Id"], data_half.iloc[i]["Title"], data_half.iloc[i]["Body"],data_half.iloc[i]["Tags"]]
        final_list.append(val)
        i += 1
        if i % 100 == 0:
            print(i)
            l = len(final_list)
            print(l)
            if (l > 5000):
                break
    print(len(final_list))

    final_list = final_list[0:5000]
    data = pd.DataFrame(final_list, columns=["Id", "Title", "Body", "Tags"])
    # , columns=["Id", "Title", "Body", "Tags"])
    # data = data_half.iloc[:10000, :]
    print("Done")
    data["tag_count"] = data_half["Tags"].apply(lambda x: len(str(x).split()))
    print(data["tag_count"].value_counts())
    vectorizerC = CountVectorizer()
    tag_bow = vectorizerC.fit_transform(data['Tags'].values.astype('U'))
    print("Number of questions :", tag_bow.shape[0])
    print("Number of unique tags :", tag_bow.shape[1])
    tags = vectorizerC.get_feature_names()
    freq = tag_bow.sum(axis=0).A1
    #stops = stop_words
    stops = set(stopwords.words('english'))
    print(len(stops))
    qus_list = []
    qus_with_code = 0
    len_before_preprocessing = 0
    len_after_preprocessing = 0
    i = 0
    for index, row in data.iterrows():
        title, body, tags = row["Title"], row["Body"], row["Tags"]
        if '<code>' in body:
            qus_with_code += 1
        len_before_preprocessing += len(title) + len(body)
        body = re.sub('<code>(.*?)</code>', '', body, flags=re.MULTILINE | re.DOTALL)
        body = re.sub('<.*?>', ' ', str(body.encode('utf-8')))
        title = title.encode('utf-8')
        question = str(title) + " " + str(body)
        question = re.sub(r'[^A-Za-z]+', ' ', question)
        words = word_tokenize(str(question.lower()))
        question = ' '.join(str(stemmer.stem(j)) for j in words if j not in stops and (len(j) != 1 or j == 'c'))
        qus_list.append(question)
        len_after_preprocessing += len(question)
        if i % 10 == 0:
            print(i)
        i += 1
    data["question"] = qus_list

    avg_len_before_preprocessing = (len_before_preprocessing * 1.0) / data.shape[0]
    avg_len_after_preprocessing = (len_after_preprocessing * 1.0) / data.shape[0]
    print("Avg. length of questions(Title+Body) before preprocessing: ", avg_len_before_preprocessing)
    print("Avg. length of questions(Title+Body) after preprocessing: ", avg_len_after_preprocessing)
    print("% of questions containing code: ", (qus_with_code * 100.0) / data.shape[0])

    preprocessed_df = data[["question", "Tags"]]
    print("Shape of preprocessed data :", preprocessed_df.shape)
    vectorizerC = CountVectorizer(tokenizer=lambda x: x.split(), binary='true')
    y_multilabel = vectorizerC.fit_transform(preprocessed_df['Tags'])

    def tags_to_consider(n):
        tag_i_sum = y_multilabel.sum(axis=0).tolist()[0]
        sorted_tags_i = sorted(range(len(tag_i_sum)), key=lambda i: tag_i_sum[i], reverse=True)
        yn_multilabel = y_multilabel[:, sorted_tags_i[:n]]
        return yn_multilabel

    def questions_covered_fn(numb):
        yn_multilabel = tags_to_consider(numb)
        x = yn_multilabel.sum(axis=1)
        return (np.count_nonzero(x == 0))

    questions_covered = []
    total_tags = y_multilabel.shape[1]
    total_qus = preprocessed_df.shape[0]
    print("total_tags : ", total_tags)
    print("total_qus : ", total_qus)
    for i in range(100, total_tags, 100):
        # print("entering for loop : ",i)
        qus_cov = questions_covered_fn(i)
        # print(qus_cov)
        questions_covered.append(np.round(((total_qus - qus_cov) / total_qus) * 100, 3))

    print("Number of questions that are not covered by 100 tags : ", questions_covered_fn(1000), "out of ", total_qus)

    yx_multilabel = tags_to_consider(5)
    print("Number of tags in the subset :", y_multilabel.shape[1])
    print("Number of tags considered :", yx_multilabel.shape[1], "(",
          (yx_multilabel.shape[1] / y_multilabel.shape[1]) * 100, "%)")
    X_train, X_test, y_train, y_test = train_test_split(preprocessed_df, yx_multilabel, test_size=0.2, random_state=42)
    print("Number of data points in training data :", X_train.shape[0])
    print("Number of data points in test data :", X_test.shape[0])
    X_train_multilabel = vectorizer.fit_transform(X_train['question'])
    X_test_multilabel = vectorizer.transform(X_test['question'])
    print("DONE")

    clf.fit(X_train_multilabel, y_train)
    y_pred = clf.predict(X_test_multilabel)
    print("Done")
    print("Accuracy :", metrics.accuracy_score(y_test, y_pred))
    print("Macro f1 score :", metrics.f1_score(y_test, y_pred, average='macro'))
    print("Micro f1 scoore :", metrics.f1_score(y_test, y_pred, average='micro'))
    print("Hamming loss :", metrics.hamming_loss(y_test, y_pred))
    return render_template('home.html')

@app.route("/inputdata", methods = ['POST', 'GET'])
def inputdata():
    #qus_list = []
    qus_with_code = 0
    len_before_preprocessing = 0
    len_after_preprocessing = 0
    if request.method == 'POST':
        title = request.form['title']
        body = request.form['body']
    print("Title: ",title)
    print("body:",body)
    if '<code>' in body:
        qus_with_code += 1
    len_before_preprocessing += len(title) + len(body)
    body = re.sub('<code>(.*?)</code>', '', body, flags=re.MULTILINE | re.DOTALL)
    body = re.sub('<.*?>', ' ', str(body.encode('utf-8')))
    title = title.encode('utf-8')
    question = str(title) + " " + str(body)
    question = re.sub(r'[^A-Za-z]+', ' ', question)
    words = word_tokenize(str(question.lower()))
    question = ' '.join(str(stemmer.stem(j)) for j in words if j not in stop_words and (len(j) != 1 or j == 'c'))
    #qus_list.append(question)
    msg = []
    msg.append(question)
    print(msg)
    val = vectorizer.transform(msg)
    info = pd.DataFrame(val.todense(), columns=vectorizer.get_feature_names())
    output = clf.predict(val)
    headings = list(info.columns)
    lst = list(info.loc[0])
    ln = len(headings)
    cmplt_lst = []
    for i in range(ln):
        tp = []
        tp.append(headings[i])
        tp.append(lst[i])
        cmplt_lst.append(tp)
    print("Cmplt_list : ",len(cmplt_lst))
    cmp_sort_lst = sorted(cmplt_lst, key=lambda x: x[1], reverse=True)
    test_list = cmp_sort_lst[0:60]
    print("Tags_list",len(tags_list))
    final_list = []
    for i in range(60):
        if test_list[i][0] in tags_list:
            final_list.append(test_list[i])
    print(final_list)
    return render_template('home.html', suggested_tags = final_list[:10])

if __name__ == "__main__":
    app.run(debug=True)
    main()