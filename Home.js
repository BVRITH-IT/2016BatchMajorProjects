/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable react-native/no-inline-styles */
/* eslint-disable react/self-closing-comp */
import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  StatusBar,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  FlatList,
  TouchableNativeFeedback,
  TouchableOpacity,
} from 'react-native';
import Heading from '../components/heading';
import firestore from '@react-native-firebase/firestore';
import AsyncStorage from '@react-native-community/async-storage';
import {StackActions} from '@react-navigation/native';
import moment from 'moment';

function HomeScreen(props) {
  const {container, button, heading} = styles;
  const [transactions, setTransactions] = useState([]);

  const getUserData = async () => {
    //Get the collection of bookings and check if there are any bookings done by the current user
    const documentSnapshot = await firestore()
      .collection('bookings')
      .onSnapshot(function(querySnap) {
        const newt = [];
        querySnap.forEach(doc => {
          if (doc.data()['bookedBy'] == props.route.params.email) {
            newt.push({id: doc.id, ...doc.data()});
          }
        });
        setTransactions(newt);
      });

    // setUsers(documentSnapshot[0].data());

    // setUsers(documentSnapshot.docs);
  };

  useEffect(() => {
    getUserData();
  }, []);

  return (
    <SafeAreaView style={container}>
      <StatusBar backgroundColor={'#fff'} />
      <Text style={heading}>Hey, {props.route.params.name}</Text>
      <View
        style={{
          flexDirection: 'row',
          justifyContent: 'space-around',
          marginVertical: 16,
        }}>
        <TouchableNativeFeedback
          onPress={() => {
            props.navigation.navigate('parkingSpots', {
              email: props.route.params.email,
            });
          }}>
          <Text style={[button, {backgroundColor: '#E1F5FE'}]}>
            New Booking
          </Text>
        </TouchableNativeFeedback>
        <TouchableNativeFeedback
          onPress={async () => {
            await AsyncStorage.clear();
            props.navigation.navigate('login');
          }}>
          <Text style={[button, {backgroundColor: '#FCE4EC'}]}>Sign Out</Text>
        </TouchableNativeFeedback>
      </View>
      <Heading>Recent Bookings</Heading>
      <FlatList
        data={transactions}
        ListEmptyComponent={
          <Text style={{textAlign: 'center'}}>No Recent Bookings</Text>
        }
        renderItem={({item, index}) => {
          return (
            <View key={index}>
              <TouchableOpacity
                style={{marginHorizontal: 32}}
                onPress={() =>
                  props.navigation.navigate('ticket', {
                    ticket: item,
                  })
                }>
                <View
                  style={{
                    flexDirection: 'row',
                    justifyContent: 'space-between',
                    marginBottom: 8,
                  }}>
                  <Text style={{fontSize: 18, fontWeight: 'bold'}}>
                    {item.spot}
                  </Text>
                  <Text
                    style={{
                      padding: 4,
                      borderRadius: 4,
                      backgroundColor:
                        item.status == 'booked' ? '#E0F2F1' : '#FFEBEE',
                    }}>
                    {item.status}
                  </Text>
                </View>
                <View
                  style={{
                    flexDirection: 'row',
                    justifyContent: 'space-between',
                  }}>
                  <Text style={{fontSize: 14}}>
                    {moment(item.on).format('MMM DD, YYYY')}
                  </Text>
                  <Text style={{fontSize: 14}}>
                    {moment(item.from).format('h:mm a')} -{' '}
                    {moment(item.from)
                      .add(Number(item.for), 'hours')
                      .format('h:mm a')}
                  </Text>
                  {/* <Text>{item.duration}</Text> */}
                </View>
              </TouchableOpacity>
              <View
                style={{
                  height: 1,
                  backgroundColor: '#ddd',
                  width: '100%',
                  marginHorizontal: 32,
                  marginVertical: 16,
                }}
              />
            </View>
          );
        }}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#fff',
    flex: 1,
  },
  button: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    backgroundColor: 'red',
    borderRadius: 4,
    width: '30%',
    textAlign: 'center',
    fontWeight: 'bold',
  },

  heading: {
    fontWeight: 'bold',
    marginHorizontal: 16,
    fontSize: 24,
  },
});

export default HomeScreen;
