import tweepy
import time
print('This is my twitter bot')
CONSUMER_KEY = 'dY3EFmDvP5LSFnXVyuJ0lC7zc'
CONSUMER_SECRET = '89XcinAWIGqjb5WJxczJXWg7974vdLfpYhKgKxxsVbjuicwYDq'
ACCESS_KEY = '1234094606381268993-8T1KuTIDVDu1HK9usfZ0azXO7TAjD4'
ACCESS_SECRET = 'u2KjJBmUVivgqCH3a5lM1lR3n5RT8dLTxPZLka5q5u3kL'
auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
auth.set_access_token(ACCESS_KEY, ACCESS_SECRET)
api = tweepy.API(auth)

FILE_NAME = 'last_seen_id.txt'

def retrieve_last_seen_id(file_name):
    f_read = open(file_name, 'r')
    last_seen_id = int(f_read.read().strip())
    f_read.close()
    return last_seen_id

def store_last_seen_id(last_seen_id, file_name):
    f_write = open(file_name, 'w')
    f_write.write(str(last_seen_id))
    f_write.close()
    return

def reply_to_tweets():
    print('retrieving and replying to tweets...')

    last_seen_id = retrieve_last_seen_id(FILE_NAME)
    mentions = api.mentions_timeline(
                        last_seen_id,
                        tweet_mode='extended')
    for mention in reversed(mentions):
        print(str(mention.id) + ' - ' + mention.full_text)
        last_seen_id = mention.id
        store_last_seen_id(last_seen_id, FILE_NAME)
        if 'bug' in mention.full_text.lower():
            print('found keyword bug')
            print('responding back...')
            if 'android' in mention.full_text.lower():
                print('found context item')
                print('responding back')
                api.update_status('@' + mention.user.screen_name + '  Thnx for reporting. we will look into it', mention.id)
            elif 'windows' in mention.full_text.lower():
                print('found context item')
                print('responding back')
                api.update_status('@' + mention.user.screen_name + '  Thanx for reporting. We will look into it', mention.id)
            elif 'ios' in mention.full_text.lower():
                print('found context item')
                print('responding back')
                api.update_status('@' + mention.user.screen_name + '   Thank you for reporting. we will get back to u', mention.id)
            else:
                api.update_status('@' + mention.user.screen_name + '  pls provide the device details', mention.id)
        elif 'problem' in mention.full_text.lower():
            print('found the keyword')
            print('responding back')
            if 'android' in mention.full_text.lower():
                print('found context item')
                print('responding back')
                api.update_status('@' + mention.user.screen_name + '  Thank you. we will look into it', mention.id)
            elif 'windows' in mention.full_text.lower():
                print('found context item')
                print('responding back')
                api.update_status('@' + mention.user.screen_name + '  Tq. We will get back to u.', mention.id)
            elif 'ios' in mention.full_text.lower():
                print('found context item')
                print('responding back')
                api.update_status('@' + mention.user.screen_name + '   Thanks for the report. We will get back to you', mention.id)
            else:
                api.update_status('@' + mention.user.screen_name + '  please tag us back with ur device details.', mention.id)
        elif 'rating' in mention.full_text.lower():
            print('reponding back....')
            api.update_status('@' + mention.user.screen_name + '  Thnx for rating us. Also rate us in the playstore', mention.id)
        elif 'rate' in mention.full_text.lower():
            print('reponding back....')
            api.update_status('@' + mention.user.screen_name + '  Thnx for rating us. Also rate us in the playstore', mention.id)
        elif 'bad' in mention.full_text.lower():
            print('reponding back....')
            api.update_status('@' + mention.user.screen_name + '  sorry for you inconvience we will look into it', mention.id)
        elif 'slow' in mention.full_text.lower():
            print('reponding back....')
            api.update_status('@' + mention.user.screen_name + '  we have fixed some bugs. Pls try upadating.', mention.id)
        elif 'Goodbye' in mention.full_text.lower():
            print('reponding back....')
            api.update_status('@' + mention.user.screen_name + ' Have a good day!! :) Pls rate us in the playstore.', mention.id)
        elif 'understand' in mention.full_text.lower():
            print('responding back')
            api.update_status('@'+ mention.user.screen_name + 'You might want to check up the details available in the app manual or the help option.', mention.id)
        elif 'android' in mention.full_text.lower():
            print('responding back')
            api.update_status('@'+ mention.user.screen_name + 'can you be more specific with details like oreo 8.0 connected to wifi.', mention.id)
        elif 'microsoft' in mention.full_text.lower():
            print('responding back')
            api.update_status('@'+ mention.user.screen_name + 'can you be more specific with details about the version and whether connected to wifi.', mention.id)
        elif 'ios' in mention.full_text.lower():
            print('responding back')
            api.update_status('@'+ mention.user.screen_name + 'can you be more specific withe details like version and whether connected to wifi.', mention.id)
        else:
            print('responding back....')
            api.update_status('@' + mention.user.screen_name + '  If any issues pls post ur queries and tag us and a hashtag', mention.id)
while True:
    reply_to_tweets()
    time.sleep(15)