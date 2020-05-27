/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
} from 'react-native';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';
import Heading from './components/heading';
import HomeScreen from './screens/Home';
import ParkingSpots from './screens/parkingSpots';
import Booking from './screens/booking';
import BookingInfo from './screens/bookingInfo';
import Ticket from './screens/ticket';
import Login from './screens/login';
import Signup from './screens/signup';

const Stack = createStackNavigator();

const App = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName={'login'}>
        <Stack.Screen
          name="Home"
          component={HomeScreen}
          options={{
            headerMode: 'none',
            headerShown: false,
          }}
        />
        <Stack.Screen
          name="parkingSpots"
          component={ParkingSpots}
          options={{
            headerMode: 'none',
            headerShown: false,
          }}
        />
        <Stack.Screen
          name="booking"
          component={Booking}
          options={{
            headerMode: 'none',
            headerShown: false,
          }}
        />
        <Stack.Screen
          name="bookingInfo"
          component={BookingInfo}
          options={{
            headerMode: 'none',
            headerShown: false,
          }}
        />
        <Stack.Screen
          name="ticket"
          component={Ticket}
          options={{
            headerMode: 'none',
            headerShown: false,
          }}
        />
        <Stack.Screen
          name="login"
          component={Login}
          options={{
            headerMode: 'none',
            headerShown: false,
          }}
        />
        <Stack.Screen
          name="signup"
          component={Signup}
          options={{
            headerMode: 'none',
            headerShown: false,
          }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: '#fff',
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
});

export default App;
