/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable react-native/no-inline-styles */
import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import Heading from '../components/heading';
import firestore from '@react-native-firebase/firestore';
import {StackActions} from '@react-navigation/native';

const Booking = props => {
  const {container, spotName} = styles;
  const [spots, setSpots] = useState([]);
  const [loading, setLoading] = useState(false);

  const data = [...Array(20).keys()].map((y, index) => ({
    bookedBy: '',
    for: '',
    from: '',
    bookedAt: '',
    on: '',
    id: props.route.params.spotName + index,
  }));

  const getUserData = async () => {
    // await firestore()
    //   .collection('spots')
    //   .doc(props.route.params.spotName)
    //   .update({
    //     spots: data,
    //   });

    setLoading(true);
    const documentSnapshot = await firestore()
      .collection('spots')
      .doc(props.route.params.spotName)
      .get();

    setLoading(false);
    setSpots(documentSnapshot.data().spots);
    // setSpots(JSON.parse(documentSnapshot.data().spots));
    // setUsers(documentSnapshot[0].data());
    // documentSnapshot.forEach(doc => {
    //   setSpots([...spots, ...doc.data()]);
    // });
    // setUsers(documentSnapshot.docs);
  };

  useEffect(() => {
    getUserData();
  }, []);

  return (
    <SafeAreaView style={container}>
      <ScrollView style={{flex: 1}}>
        <Heading>Book a spot</Heading>
        {/* <Text>{JSON.stringify(spots)}</Text> */}
        {/* <Text>{props.route.params.spotName}</Text> */}
        {loading && <ActivityIndicator />}
        <View style={{flexDirection: 'row', flexWrap: 'wrap'}}>
          {spots.map((spot, index) => {
            const spotData = spot;
            return (
              <TouchableOpacity
                key={index}
                style={{
                  height: 64,
                  width: 48,
                  borderRadius: 8,
                  borderWidth: 1,
                  borderColor: 'steelblue',
                  margin: 16,
                  backgroundColor: spotData.bookedBy ? 'yellow' : '#fff',
                }}
                onPress={() =>
                  !spotData.bookedBy &&
                  props.navigation.dispatch(
                    StackActions.replace('bookingInfo', {
                      spotName: props.route.params.spotName,
                      spotId: spotData.id,
                      email: props.route.params.email,
                      spot: props.route.params.spot,
                      price: props.route.params.price,
                    }),
                  )
                }
              />
            );
          })}
        </View>
      </ScrollView>
      <View
        style={{
          flexDirection: 'row',
          justifyContent: 'space-between',
          paddingHorizontal: 16,
        }}>
        <View style={{flexDirection: 'row', alignItems: 'center'}}>
          <View
            style={{
              height: 16,
              width: 16,
              borderRadius: 4,
              borderWidth: 1,
              borderColor: 'steelblue',
              margin: 8,
              backgroundColor: '#fff',
            }}
          />
          <Text>Available</Text>
        </View>
        <View style={{flexDirection: 'row', alignItems: 'center'}}>
          <View
            style={{
              height: 16,
              width: 16,
              borderRadius: 4,
              borderWidth: 1,
              borderColor: 'steelblue',
              margin: 8,
              backgroundColor: 'yellow',
            }}
          />
          <Text>Booked</Text>
        </View>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'white',
    flex: 1,
  },

  spotName: {
    fontSize: 20,
    marginHorizontal: 32,
    marginBottom: 16,
  },
});

export default Booking;
