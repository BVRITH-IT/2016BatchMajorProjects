import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
  ScrollView,
  TouchableOpacity,
} from 'react-native';
import Heading from '../components/heading';

const ParkingSpots = props => {
  const {container, spotName} = styles;
  return (
    <SafeAreaView style={container}>
      <ScrollView style={{flex: 1}}>
        <Heading>Free</Heading>
        <TouchableOpacity
          onPress={() =>
            props.navigation.navigate('booking', {
              spotName: 'apj',
              spot: 'APJ Block',
              email: props.route.params.email,
              price: 0,
            })
          }>
          <Text style={spotName}>APJ Block</Text>
        </TouchableOpacity>

        <Heading>Paid</Heading>
        <TouchableOpacity
          onPress={() =>
            props.navigation.navigate('booking', {
              spotName: 'opal',
              spot: 'Opal Block',
              email: props.route.params.email,
              price: 20,
            })
          }>
          <Text style={spotName}>Opal Block</Text>
        </TouchableOpacity>
        <TouchableOpacity
          onPress={() =>
            props.navigation.navigate('booking', {
              spotName: 'pearl',
              spot: 'Pearl Block',
              email: props.route.params.email,
              price: 20,
            })
          }>
          <Text style={spotName}>Pearl Block</Text>
        </TouchableOpacity>
        {/* <TouchableOpacity
          onPress={() =>
            props.navigation.navigate('booking', {
              spotName: 'apj',
              spot: 'APJ Block',
              email: props.route.params.email,
            })
          }>
          <Text style={spotName}>APJ Block</Text>
        </TouchableOpacity> */}
      </ScrollView>
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

export default ParkingSpots;
