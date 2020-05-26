import pandas as pd 
import numpy as np
import re
import rf_model
import whois
import time
from urllib.parse import urlparse
import datetime
import socket
class feature_extractor:

    def __init__(self,url:str):
        self.input_url = url

    def long_url(self,l):
        l= str(l)
        if len(l) < 54:
            return 0
        elif len(l) >= 54 and len(l) <= 75:
            return 2
        return 1

    def have_at_symbol(self,l):
        if "@" in str(l):
            return 1
        return 0

    def redirection(self,l):
        if "//" in str(l):
            return 1
        return 0

    def prefix_suffix_seperation(self,l):
        if '-' in str(l):
            return 1
        return 0

    def sub_domains(self,l):
        l= str(l)
        if l.count('.') < 3:
            return 0
        elif l.count('.') == 3:
            return 2
        return 1
    def shortening_service(self,l):
        match=re.search('bit\.ly|goo\.gl|shorte\.st|go2l\.ink|x\.co|ow\.ly|t\.co|tinyurl|tr\.im|is\.gd|cli\.gs|'
                    'yfrog\.com|migre\.me|ff\.im|tiny\.cc|url4\.eu|twit\.ac|su\.pr|twurl\.nl|snipurl\.com|'
                    'short\.to|BudURL\.com|ping\.fm|post\.ly|Just\.as|bkite\.com|snipr\.com|fic\.kr|loopt\.us|'
                    'doiop\.com|short\.ie|kl\.am|wp\.me|rubyurl\.com|om\.ly|to\.ly|bit\.do|t\.co|lnkd\.in|'
                    'db\.tt|qr\.ae|adf\.ly|goo\.gl|bitly\.com|cur\.lv|tinyurl\.com|ow\.ly|bit\.ly|ity\.im|'
                    'q\.gs|is\.gd|po\.st|bc\.vc|twitthis\.com|u\.to|j\.mp|buzurl\.com|cutt\.us|u\.bb|yourls\.org|'
                    'x\.co|prettylinkpro\.com|scrnch\.me|filoops\.info|vzturl\.com|qr\.net|1url\.com|tweez\.me|v\.gd|tr\.im|link\.zip\.net',l)
        if match:
            return 1               # phishing
        else:
            return 0
    def domain_registration_length(self,l):
        dns = 0
        try:
            domain_name = whois.whois(urlparse(l).netloc)
        except:
            dns = 1
        
        if dns == 1:
            return 1     
        else:
            expiration_date = domain_name.expiration_date
            today = time.strftime('%Y-%m-%d')
            today = datetime.strptime(today, '%Y-%m-%d')
            if expiration_date is None:
                return 1
            elif type(expiration_date) is list or type(today) is list :
                return 2      
            else:
                creation_date = domain_name.creation_date
                expiration_date = domain_name.expiration_date
                if (isinstance(creation_date,str) or isinstance(expiration_date,str)):
                    try:
                        creation_date = datetime.strptime(creation_date,'%Y-%m-%d')
                        expiration_date = datetime.strptime(expiration_date,"%Y-%m-%d")
                    except:
                        return 2
                    registration_length = abs((expiration_date - today).days)
                if registration_length / 365 <= 1:
                    return 1 
                else:
                    return 0
    
    def dns_record(self,l):
       # l=str(l)
        dns = 0
        try:
            domain_name = whois.whois(urlparse(l).netloc)
           
        except:
            dns = 1
        
        if dns == 1:
            return 1
        else:
            return 0
        
    def https_token(self,l):
        match=re.search('https://|http://',l)
        try:
            if match.start(0)==0 and match.start(0) is not None:
                l=l[match.end(0):]
                match=re.search('http|https',l)
                if match:
                    return 1
                else:
                    return 0
        except:
            return 1
        
    def statistical_report(self,l):
        #l=str(l)
        hostname = l
        h = [(x.start(0), x.end(0)) for x in re.finditer('https://|http://|www.|https://www.|http://www.', hostname)]
        z = int(len(h))
        if z != 0:
            y = h[0][1]
            hostname = hostname[y:]
            h = [(x.start(0), x.end(0)) for x in re.finditer('/', hostname)]
            z = int(len(h))
            if z != 0:
                hostname = hostname[:h[0][0]]
        url_match=re.search('at\.ua|usa\.cc|baltazarpresentes\.com\.br|pe\.hu|esy\.es|hol\.es|sweddy\.com|myjino\.ru|96\.lt|ow\.ly',l)
        try:
            ip_address = socket.gethostbyname(hostname)
            ip_match=re.search('146\.112\.61\.108|213\.174\.157\.151|121\.50\.168\.88|192\.185\.217\.116|78\.46\.211\.158|181\.174\.165\.13|46\.242\.145\.103|121\.50\.168\.40|83\.125\.22\.219|46\.242\.145\.98|107\.151\.148\.44|107\.151\.148\.107|64\.70\.19\.203|199\.184\.144\.27|107\.151\.148\.108|107\.151\.148\.109|119\.28\.52\.61|54\.83\.43\.69|52\.69\.166\.231|216\.58\.192\.225|118\.184\.25\.86|67\.208\.74\.71|23\.253\.126\.58|104\.239\.157\.210|175\.126\.123\.219|141\.8\.224\.221|10\.10\.10\.10|43\.229\.108\.32|103\.232\.215\.140|69\.172\.201\.153|216\.218\.185\.162|54\.225\.104\.146|103\.243\.24\.98|199\.59\.243\.120|31\.170\.160\.61|213\.19\.128\.77|62\.113\.226\.131|208\.100\.26\.234|195\.16\.127\.102|195\.16\.127\.157|34\.196\.13\.28|103\.224\.212\.222|172\.217\.4\.225|54\.72\.9\.51|192\.64\.147\.141|198\.200\.56\.183|23\.253\.164\.103|52\.48\.191\.26|52\.214\.197\.72|87\.98\.255\.18|209\.99\.17\.27|216\.38\.62\.18|104\.130\.124\.96|47\.89\.58\.141|78\.46\.211\.158|54\.86\.225\.156|54\.82\.156\.19|37\.157\.192\.102|204\.11\.56\.48|110\.34\.231\.42',ip_address)  
        except:
            return 1

        if url_match:
            return 1
        else:
            return 0
        
    

    def extract(self):
        print("in script 2")
        input_data = [{"URL":self.input_url}]
        print('input taken')
        temp_df = pd.DataFrame(input_data)
        print("dataframe created")
        seperation_of_protocol = temp_df['URL'].str.split("://",expand = True)
        print("step 1 done")
        seperation_domain_name = seperation_of_protocol[1].str.split("/",1,expand = True)
        print("step 2 done")
        seperation_domain_name.columns=["domain_name","address"]
        print("step 3 done")
        splitted_data = pd.concat([seperation_of_protocol[0],seperation_domain_name],axis=1)
        print("step 4 done")
        splitted_data.columns = ['protocol','domain_name','address']
        print("step 5 done")

        #splitted_data['is_phished'] = pd.Series(temp_df['Target'], index=splitted_data.index)
        #print("step 6 done")

       
        splitted_data['long_url'] = temp_df['URL'].apply(self.long_url)
        print("feature extra 1")
        splitted_data['having_@_symbol'] = temp_df['URL'].apply(self.have_at_symbol)
        print("feature extra 2")
        splitted_data['redirection_//_symbol'] = seperation_of_protocol[1].apply(self.redirection)
        print("feature extra 3")
        splitted_data['prefix_suffix_seperation'] = seperation_domain_name['domain_name'].apply(self.prefix_suffix_seperation)
        print("feature extra 4")
        splitted_data['sub_domains'] = splitted_data['domain_name'].apply(self.sub_domains)
        print("feature extra 5")
        splitted_data['shortening_service']=temp_df['URL'].apply(self.shortening_service)
        print("feature extra 6")
        splitted_data['domain_registration_length'] = splitted_data['domain_name'].apply(self.domain_registration_length)
        print("feature extra 7")
        splitted_data['dns_record'] = splitted_data['domain_name'].apply(self.dns_record)
        print("feature extra 8")
        splitted_data['https_token'] = temp_df['URL'].apply(self.https_token)
        print("feature extra 9")
        splitted_data['statistical_report'] = temp_df['URL'].apply(self.statistical_report)
        print("feature extract 10")
        #splitted_data.to_csv(r'dataset3.csv',header= True)

        

        return rf_model.predictor(splitted_data)
